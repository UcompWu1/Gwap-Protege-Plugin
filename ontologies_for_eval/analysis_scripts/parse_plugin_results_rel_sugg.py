#!/usr/bin/python

import re,sys,pickle,os.path
from config import *
from helpers import *
from output import *

result = {}

# (1) read data into our data structures ..
result = get_files_and_evaluators(FILE_PATTERN, FPATH)

print "Number of data files as input found: %d" % (len(result),) 

for evaluator, data in result.iteritems():

    print "\n\nNext file:", data[0]

    # add evaluation data
    if data[0].find('relation_suggestion')>0:
        print "Starting subClassOf_parse_into_resultlist"
        if evaluator.startswith("cf_results"):
             #print evaluator; sys.exit()
            cffile = os.path.join( FPATH, evaluator, FILE_PATTERN+".pickle")
            print cffile
            result[evaluator].append ( pickle.load(open(cffile)))  
        else: 
            result[evaluator].append ( relation_suggestion_parse_into_resultlist(data[0]) ) 

       
    else:
        # concept relevance parse
        print "Starting class extract_eval_results_from_rdf" 
        if evaluator.startswith("cf_results"):
            #print evaluator; sys.exit()
            cffile = os.path.join( FPATH, evaluator, FILE_PATTERN+".pickle")
            print cffile
            result[evaluator].append ( pickle.load(open(cffile)))  
        else: 
            result[evaluator].append ( extract_eval_results_from_rdf(data[0]) ) 


print "Number of data sets extracted from files: %d" % (len(result),) 

#for k in result:
#    print k, result[k]

# (2) print a csv result
evaluators, resultlist = convert_to_resultlist(result)


print "Length of resultlist", len(resultlist)
print_csv(evaluators, resultlist)

print "Kappa:"
compute_fleiss_matrix(resultlist, relation_suggestion=True)
print "Majority check for cf_results -- skipping"
#compute_majority_check(evaluators, resultlist)
