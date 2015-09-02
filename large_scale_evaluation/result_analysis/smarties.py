
# what this is supposed to do:
# check in the ontology if the (SWAPPED) concept accidentally IS a subclass of the given superclass
import sys, rdflib

ontology = None
nodes = dict()
children = dict()
original_nodes = dict()
checked = 0
found = 0
SWAP_CONCEPT_COMMENT = 'This is a swapped concept.'

def convert_label(label):
    """
        just cleanup the label -- no logic here
    """
    if '_' in label: label = label.replace('_', ' ')
    if label[0] in ["'", '"']: label = label[1:]
    if label[-1] in ["'", '"']: label = label[:-1]
    if not label[0].isupper(): label = label.capitalize()

    label_parts = []
    for part in label.split(' '):
        if part.isupper() or '-' in part or part.islower():
            label_parts.append(part)
        else:
            section = ''
            for char in part:
                if char.isupper():
                    if section != '': label_parts.append(section)
                    section = char
                else:
                    section = section+char
            label_parts.append(section)
    if len(label_parts) > 1: label = ' '.join(label_parts)
    return label

def get_original_node(graph, node):
    """
    """
    global SWAP_CONCEPT_COMMENT, original_nodes
    if node in original_nodes: return original_nodes[node]

    original_concept_label = None
    for comment in graph.objects(node, rdflib.RDFS.comment):
        comment = unicode(comment.value)
        if SWAP_CONCEPT_COMMENT in comment:
            data = comment.replace(SWAP_CONCEPT_COMMENT, '').split(';')
            data = [elem.split('=', 1) for elem in data]
            data = [(elem[0].strip(), elem[1].strip()) for elem in data]
            original_concept_label = dict(data)['Original label']
            break

    if original_concept_label:
        original_concept_label = convert_label(original_concept_label.strip())
        original_nodes[node] = (original_concept_label, get_node(original_concept_label))
        return original_nodes[node]
    else:
        raise Exception('No original node found!')

def get_node(label_str):
    global nodes
    if label_str.strip() in nodes:
        return nodes[label_str.strip()]
    else:
        raise Exception('Could not find any node with label %s' % label_str)

def get_ontology():
    global ontology, nodes

    if not ontology:
        input_file = sys.argv[2]
        ontology = rdflib.Graph()
        input_format = 'xml'
        if len(sys.argv) > 3: input_format = sys.argv[3]
        print('Loading graph from %s ...' % input_file)
        ontology.load(source=input_file, format=input_format)
        subjects = set(ontology.subjects(rdflib.RDF.type, rdflib.OWL.Class))
        all_labels = list(ontology.triples((None, rdflib.RDFS.label, None)))
        nodes = dict([(elem[2].strip(), elem[0]) for elem in all_labels if elem[0] in subjects])
        print('Got %s triples.' % len(ontology))

    return ontology

def get_children(graph, node):
    global children
    if not node in children:
        children[node] = set(graph.transitive_subjects(rdflib.RDFS.subClassOf, node))
        children[node].add(node)
    return children[node]

def same_superclass_swap(concept, superconcept):
    """
    A swap condition
    All superclass relations (excluding the root node) are compared.
    :param concept: one of the swapped concepts
    :param superconcept: the newly assign parent
    :return: True id concept subclassOf superconcept
    """
    global found, checked
    checked += 1
    if checked % 250 == 0:
        print('Checked %s concepts. Found %s bad swaps.' % (checked, found))

    ontology = get_ontology()
    concept_node = get_node(concept)
    superconcept_node = get_node(superconcept)
    original_concept_label, original_concept_node = get_original_node(ontology, concept_node)

    same_super_swap = original_concept_node in get_children(ontology, superconcept_node)
    if same_super_swap:
        found += 1
        print('"%s" swapped with "%s" under "%s"' % (concept, original_concept_label, superconcept))
    return same_super_swap
