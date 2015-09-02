import csv,pickle,sys,re


FN=sys.argv[1]
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



# concept detection pattern
p = re.compile("Is the concept (.*) relevant")



with open(FN, 'rb') as csvfile:
    csvr = csv.reader(csvfile)
    for row in csvr:
        if DEBUG: print row
        print row[IS_GOLD], row[RESULTCOL], row[QUESTIONCOL]

        # skip first row
        if row[0] == '_unit_id': continue

        num_results += 1

        # count number of gold / vs non-gold-units
        if row[IS_GOLD] == 'false':
            num_workunits += 1 
        else:
            num_gold += 1

        # extract concept from question
        if not row[QUESTIONCOL].find("concept")>0:
            print "XXXXXXXX"
            continue


        m = p.search(row[QUESTIONCOL])
        concept =  m.groups()[0].replace("'", "").title()
        if DEBUG: print concept

        
        # is the concept on the list of non-relevant concepts?
        if concept in non_rel_concepts:
            relevant = False
            num_non_rel += 1
        else:
            relevant = True
            num_rel += 1

        if concept == "Thing":
            continue

        if row[RESULTCOL] == "1":
            num_pos_j += 1
            if relevant:
                num_true_pos += 1 
            else:
                num_false_pos += 1
                false_positives.append(concept)


        elif row[RESULTCOL] == "2":
            num_neg_j += 1
            if relevant:
                num_false_neg += 1 
                false_negatives.append(concept)
            else:
                num_true_neg += 1

        else:
            sys.exit("ERROR")        
    


print "Total number of results found:", num_results
print "Number of gold units and worker units:", num_gold, num_workunits 
print "Number of relevant and non-relevant concepts in result set:", num_rel, num_non_rel
print "Number of positive and negative worker judgements in result set:", num_pos_j, num_neg_j
print "Number of true positive and false positive:", num_true_pos, num_false_pos
print "Number of true negative and false negative:", num_true_neg, num_false_neg
print "Number of correct and incorrect judgements", num_true_pos + num_true_neg, num_false_neg + num_false_pos


print "False positive terms:", false_positives
print "False negative terms:", false_negatives




#print results, len(results)
#for key in sorted(results):
#    print key, results[key]
#
#
#pickle.dump(results, open(FN[:-11]+"class.pickle", "wb"))
