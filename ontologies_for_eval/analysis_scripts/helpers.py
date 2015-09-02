import os.path, re
import sys
from os import walk
from xml.dom import minidom

from config import DEBUG,P_CLASS_OPEN,ANNOTATION,WHO

p_relname = re.compile('onto:(relation_\d+) rdf:type ')
p_rellabel = re.compile('rdfs:label "*(.+)"* ;')

def get_files_and_evaluators(fpattern, path):
    """
        creates a dict looking like this
        {'stefan': ['path to his eval file],
         'Marta': ['path to her eval file],
        }
    """

    results = {}

    for (dirpath, dirnames, filenames) in walk(path):

        # check if we find our fpattern in the filenames
        found = [f for f in filenames if f.find(fpattern)>=0]
        if not found:
            continue

        # there should be exactly one match        
        assert len(found) == 1

        fpath = os.path.join(dirpath, found[0])

        # extract evaluator name from filepath        
        evaluator = dirpath[len(path)+1:]

        if DEBUG: print "YYYYYYYYYYYY", found, fpath, evaluator

        # select the evaluators that we want to analyze
        if WHO=="manual_only":
            #if evaluator.startswith('cf_result') and evaluator!='cf_result1': continue
            if evaluator.startswith('cf_result'):  # skip cf_result files
                continue
        elif WHO=="cf_only":
            #if evaluator.startswith('cf_result') and evaluator!='cf_result1': continue
            if not evaluator.startswith('cf_result'):  # skip manual files
                continue

        results[evaluator] = [fpath]

        #print; print
        #print "dirpath",   dirpath, 
        #print "dirnames",  dirnames 
        #print "filenames", filenames

    return results


def extract_eval_results_from_rdf(fname):
    result = {}

    for line in open(fname):
        line = line.strip()

        # a look for classes to open
        if line.find(P_CLASS_OPEN)>=0:

            #found_open=True
            #class_counter += 1

            # extract class name
            class_name = line[line.find(';')+1:-2]

            # cleanup 
            class_name = class_name.replace('"', '')
            result[class_name] = "" # initialize
         

        if line.find(ANNOTATION)>0 and line.find('datatype')>0:
         
            if len(result[class_name]) > 0:
                print "ERROR -- the class has an annotation already"

            # found the annotation we are looking for 
            if line.find("true")>0 and line.find("false")>0:
                print "ERROR -- found true and false .. check this", line
            if line.find("true")>0:
                result[class_name] = "true"
            if line.find("false")>0:
                result[class_name] = "false"

    return result

def subClassOf_parse_into_resultlist(FN):
    resultlist = {} 
    xmldoc = minidom.parse(FN)
    axiomlist = xmldoc.getElementsByTagName('owl:Axiom') 

    for ax in axiomlist:

        if not ax.hasChildNodes():
            continue

        source_list = ax.getElementsByTagName('owl:annotatedSource')
        target_list = ax.getElementsByTagName('owl:annotatedTarget')
        prop_list = ax.getElementsByTagName('owl:annotatedProperty')
        #check_list = ax.getElementsByTagName('onto:uComp_subclass_check')
        check_list = ax.getElementsByTagName('onto:uComp_subclassof_check')

        #print len(source_list), len(target_list), len(prop_list), len(check_list)
        if len(check_list) == 0:
            print "No uComp_subclass_check found for an Axiom :-("

        assert len(source_list) == len(target_list) == len(prop_list) == 1

        # source
        source_attr = source_list[0].getAttribute('rdf:resource')
        source_concept = source_attr.split('#')[-1]

        # target
        target_attr = target_list[0].getAttribute('rdf:resource')
        target_concept = target_attr.split('#')[-1]

        # property
        prop_attr = prop_list[0].getAttribute('rdf:resource')
        prop_type = prop_attr.split('#')[-1]

        # check
        if not check_list:
            check_val = ""
        elif check_list[0].toxml().find('true') > 0:
            check_val = "true"
        else:
            check_val = "false"

        print source_concept, target_concept, prop_type, check_val

        if prop_type == "subClassOf":
            ## create resultlist
            resultlist[source_concept + "-" + target_concept] = check_val

    return resultlist 


def relation_suggestion_parse_into_resultlist(FN):

    found = False
    numafter = 0

    result = {}

    for line in open(FN):

        line = line.strip()
        if not line: continue

        if found:
            if line.find('rdfs:label')>=0:
                label = p_rellabel.search(line).groups()[0]
                label = label.replace('"', '')
                label = label.replace('onto:', '')
                found = False
                numafter = 0

                result[relname] = label
            else:
                numafter += 1

        if numafter>5:
            raise Exception(FN + "-----" + line)

        if line.find('onto:relation_')>=0 and line.find('ObjectProperty')>=0:
            relname = p_relname.search(line).groups()[0]
            found = True

        continue

    print result

    return result 
