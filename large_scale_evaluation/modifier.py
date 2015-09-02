#!/usr/bin/python
# -*- coding: utf-8 -*-
"""

"""
__author__ = 'Stefan Belk'

import rdflib, os, argparse, random

TEST_CONCEPT_COMMENT = 'This is a test concept.'
SWAP_CONCEPT_COMMENT = 'This is a swapped concept. Original label = '

def parse_args():
    parser = argparse.ArgumentParser(description='Make funny ontologies.')
    parser.add_argument('input_file', help='Input ontology file.')
    parser.add_argument('--input_format', help='Format of the input ontology file.')
    parser.add_argument('--output_file', help='Output ontology file.')
    parser.add_argument('--output_format', help='Format of the output ontology file.')
    parser.add_argument('--test_concepts_file', help='File with concepts to insert randomly into the ontology.')
    parser.add_argument('--test_concepts_capitalize', action='store_true', default=True, help='Capitalize concepts on insert.')
    parser.add_argument('--test_concepts_count', type=int, help='The maximum amount of test concepts to be inserted. Default=500.')
    parser.add_argument('--swap_concepts', action='store_true', help='Randomly swaps leaf concepts (except test concepts).')
    parser.add_argument('--swap_concepts_count', type=int, help='The maximum amount of swaps to be done. Default=500.')
    parser.add_argument('--convert_labels', action='store_true', help='Underscore will be converted to space, \' and " will be removed. Default=True')
    args = vars(parser.parse_args())
    print('Program arguments:')
    for key, val in args.items():
        if not val: del args[key]
        print('%s=%s' % (key, val))
    print('')
    return args

def load_graph(args):
    """

    :return:
    """
    print('Loading graph from %s ...' % args['input_file'])
    input_format = args.get('input_format', 'xml')
    input_graph = rdflib.Graph()
    input_graph.load(source=args['input_file'], format=input_format)
    print('Done. Got %s triples.' % len(input_graph))
    return input_graph

def save_graph(graph, args):
    """

    :param graph:
    :return:
    """
    output_format = args.get('output_format', 'pretty-xml')
    output_file = args.get('output_file', None)
    if not output_file:
        file_name, file_extension = os.path.splitext(args['input_file'])
        new_name = file_name+'_modified'+file_extension
        output_file = os.path.join(os.path.dirname(args['input_file']), new_name)

    print('Saving graph to %s ...' % output_file)
    graph.serialize(output_file, format=output_format)
    print('Done. Output format is %s.' % output_format)

def insert_test_concepts(graph, args):
    """

    :param graph:
    :return:
    """
    if not args.get('test_concepts_file'):
        print('Skipped insertion of test concepts. No file specified.')
        return

    print('Inserting concepts...')
    concepts = open(args['test_concepts_file'], 'r').readlines()
    concepts = [line.strip().decode('UTF-8') for line in concepts if len(line.strip()) > 0]
    nodes = list(set(graph.subjects(rdflib.RDF.type, rdflib.OWL.Class)))

    namespaces = dict(graph.namespaces())
    base = namespaces.get('', 'http://example.com')
    if base[-1] == '/': base = base[0:-1]+'#'
    elif base[-1] != '#': base += '#'
    print('The base URI for the added concepts is %s' % base)
    graph.bind('owl', rdflib.OWL)

    capitalize = args.get('test_concepts_capitalize', False)
    print('Capitalize concepts = %s' % capitalize)

    test_concepts_count = args.get('test_concepts_count', 500)
    count = 0
    parents = set()
    for concept in concepts:
        if count == test_concepts_count: break
        concept = concept.strip().replace(' ', '_')
        if capitalize: concept = concept.title()
        new_node = rdflib.URIRef(base + concept)
        subject = nodes[random.randint(0, len(nodes)-1)]
        graph.add((new_node, rdflib.RDF.type, rdflib.OWL.Class))
        graph.add((new_node, rdflib.RDFS.subClassOf, subject))
        graph.add((new_node, rdflib.RDFS.label, rdflib.Literal(concept)))
        graph.add((new_node, rdflib.RDFS.comment, rdflib.Literal(TEST_CONCEPT_COMMENT)))
        parents.add(subject)
        count += 1

    print('Done. Added %s of %s concepts to %s different nodes.' % (count, len(concepts), len(parents)))

def swap_leaf_concepts(graph, args):
    """

    :param graph:
    :return:
    """
    if not args.get('swap_concepts', False):
        print('Skipped swapping of leaf concepts.')
        return

    count = args.get('swap_concepts_count', 500)
    print('Swapping %s leaf concepts...' % count)

    nodes = set(graph.subjects(rdflib.RDF.type, rdflib.OWL.Class))
    #remove all that are no leafs
    for obj in graph.objects(None, rdflib.RDFS.subClassOf):
        if obj in nodes: nodes.remove(obj)
    #remove all that have no label
    def has_label(test_node):
        try:
            graph.objects(test_node, rdflib.RDFS.label).next()
            return True
        except StopIteration:
            return False
    nodes = [node for node in nodes if has_label(node)]

    print('Found %s leaf concepts (with labels).' % len(nodes))

    if count > (len(nodes) / 2):
        print('Cannot do %s swaps with %s concepts.' % (count, len(nodes)))
        count = len(nodes) / 2
        print('I will only do %s swaps.' % count)

    nodes = list(nodes)
    swaps = list()
    while count > 0 and len(nodes) > 1:
        first = nodes[random.randint(0, len(nodes)-1)]
        nodes.remove(first)
        second = nodes[random.randint(0, len(nodes)-1)]
        nodes.remove(second)
        swaps.append((first, second))
        count -= 1

    comment = SWAP_CONCEPT_COMMENT+'%s'

    for first, second in swaps:
        #get labels
        first_label = graph.objects(first, rdflib.RDFS.label).next()
        second_label = graph.objects(second, rdflib.RDFS.label).next()
        #add comments
        graph.add((first, rdflib.RDFS.comment, rdflib.Literal(comment % first_label.value)))
        graph.add((second, rdflib.RDFS.comment, rdflib.Literal(comment % second_label.value)))
        #remove labels
        graph.remove((first, rdflib.RDFS.label, first_label))
        graph.remove((second, rdflib.RDFS.label, second_label))
        #swap labels
        graph.add((first, rdflib.RDFS.label, second_label))
        graph.add((second, rdflib.RDFS.label, first_label))

    print('Done. Made %s swaps.' % len(swaps))

def convert_concept_labels(graph, args):
    """

    :param graph:
    :return:
    """
    if not args.get('convert_labels', True):
        print('Skipped concept label conversion.')
        return

    print('Converting concept labels...')
    count = 0
    for subj, predicate, obj in graph.triples((None, rdflib.RDFS.label, None)):
        if '_' in obj.value:
            graph.remove((subj, predicate, obj))
            label = obj.value.replace('_', ' ')
            if label[0] in ["'", '"']: label = label[1:]
            if label[-1] in ["'", '"']: label = label[:-1]
            graph.add((subj, predicate, rdflib.Literal(label)))
            count += 1
    print('Done. Changed %s literals.' % count)


def main(arguments):
    graph_object = load_graph(arguments)
    swap_leaf_concepts(graph_object, arguments)
    insert_test_concepts(graph_object, arguments)
    convert_concept_labels(graph_object, arguments)
    save_graph(graph_object, arguments)


if __name__ == '__main__':
    main(parse_args())

