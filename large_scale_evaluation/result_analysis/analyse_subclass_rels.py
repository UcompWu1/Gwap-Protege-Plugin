import csv, sys, re
import smarties

# argv[1] is the datafile from crowdflower
FN = sys.argv[1]
print "Using input file:", FN
DEBUG = False

# where is the data (in the input csv file)?
IS_GOLD=1
RESULTCOL=5
QUESTIONCOL=11

# to count how many gold and normal units we have
num_results = 0
num_gold, num_workunits = 0, 0
num_rel, num_non_rel = 0, 0
num_pos_j, num_neg_j= 0, 0

num_true_pos, num_true_neg= 0, 0
num_false_pos, num_false_neg= 0, 0


false_positives, false_negatives = [], []
false_positives_full, false_negatives_full = [], []



# concept and super-concept detection patterns
p = re.compile("Is class (.*) a subclass of")
p2 = re.compile("a subclass of (.*)\?") 

# read concepts file -- these are the concepts which have been swapped
swapped_concepts = [c.strip().title() for c in open('swapped_concepts.txt').readlines()]
print "Number of swapped_concepts:", len(swapped_concepts)
if DEBUG: print swapped_concepts 

swapped_concepts_in_results = []

with open(FN, 'rb') as csvfile:
    for row in csv.reader(csvfile):
        # skip first row
        if row[0] == '_unit_id': continue

        if DEBUG: print row
        if DEBUG: print row[IS_GOLD], row[RESULTCOL], row[QUESTIONCOL]

        num_results += 1

        # count number of gold / vs non-gold-units
        if row[IS_GOLD] == 'false':
            num_workunits += 1 
        else:
            num_gold += 1

        # extract concept from question
        if not row[QUESTIONCOL].find("class")>0:
            print "XXXXXXXX"
            continue

        orig_concept =  p.search(row[QUESTIONCOL]).groups()[0].replace("'", "")
        orig_superconcept = p2.search(row[QUESTIONCOL]).groups()[0].replace("'", "")

        concept = orig_concept.title()
        superconcept =  orig_superconcept.title()

        if DEBUG: print concept, "---", superconcept

        if concept == "Thing":
            raise Exception("Thing found??")

        # is the concept on the list of non-relevant concepts?
        if concept in swapped_concepts and not smarties.same_superclass_swap(orig_concept, orig_superconcept):
            relevant = False
            num_non_rel += 1
            swapped_concepts_in_results.append(concept)
        else:
            relevant = True
            num_rel += 1

        ########## ok -- now we check if the CF results are correct!!
        ##########
        if row[RESULTCOL] == "1":  ## this means the judgement was "yes", ie. the relation is correct acc to CF
            num_pos_j += 1
            if relevant:
                num_true_pos += 1 
            else:
                num_false_pos += 1
                false_positives.append(concept)
                false_positives_full.append(concept + " -> " + superconcept)


        elif row[RESULTCOL] == "2": ## this means the judgement was "no", ie. the relation is NOT correct acc to CF
            num_neg_j += 1
            if relevant:
                num_false_neg += 1 
                false_negatives.append(concept)
                false_negatives_full.append(concept + " -> " + superconcept)
            else:
                num_true_neg += 1

        else:
            sys.exit("ERROR")        
    

swapped_concepts_in_results.sort()

### general stats
print "Total number of results found:", num_results
print "Total number of results with swapped concepts", len(swapped_concepts_in_results)
print "Distinct number of swapped concepts in the result set", len(set(swapped_concepts_in_results))

### Result analysis stats
print "Number of gold units and worker units:", num_gold, num_workunits 
print "Number of relevant and non-relevant concepts in result set:", num_rel, num_non_rel
print "Number of positive and negative worker judgements in result set:", num_pos_j, num_neg_j
print "Number of true positive and false positive:", num_true_pos, num_false_pos
print "Number of true negative and false negative:", num_true_neg, num_false_neg
print "Number of correct and incorrect judgements", num_true_pos + num_true_neg, num_false_neg + num_false_pos
print "Accuracy", (num_true_pos + num_true_neg) / (float(num_false_neg) + num_false_pos + num_true_pos + num_true_neg)

print
print "False positive terms:", false_positives_full, "\n\n"
print "False negative terms:", false_negatives_full




#print results, len(results)
#for key in sorted(results):
#    print key, results[key]
#
#
#pickle.dump(results, open(FN[:-11]+"class.pickle", "wb"))
