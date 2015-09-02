from config import DEBUG, FILE_PATTERN 


def convert_to_resultlist(result):

    evaluators = result.keys()

    # compile a big dict/list of keys, evaluator votes
    first = True
    resultlist={}
    for evaluator in evaluators:
        edata = result[evaluator][1]

        # check that number of classes if always the same!
        if first:
            num_classes_init = len(edata)

        print evaluator, ": number of classes/relations", len(edata)
        assert len(edata) == num_classes_init

        for key in sorted(edata):
 
            if first:
                resultlist[key] = [edata[key]]
            else:          
                resultlist[key].append(edata[key])

        first = False
    return evaluators, resultlist

def compute_fleiss_matrix(resultlist, relation_suggestion=False):
    fmatrix = []
    perc_true,c = 0,0
    
    # now we have the list, do the output     
    for key in sorted(resultlist):

        if relation_suggestion:
            # here we have a number of categories
            # categories are:
            # ['is-part-of', 'is-part-of', 'is-part-of', 'other', 'other']

            categories = [0,0,0,0,0,0,0,0,0,0]
            #``has-effect-on'', ``is-affected-by'', ``is-a'', ``is-opposite-of'', ``has-part'', ``is-part-of'', ``used-by'' or``uses'' to each unlabeled relation, or ``other''
            print "YYYY", key, resultlist[key]
            for item in resultlist[key]:
                if item == 'has-effect-on':
                    categories[0]+=1
                elif item == 'is-affected-by':
                    categories[1]+=1
                elif item == 'is-a':
                    categories[2]+=1
                elif item == 'is-opposite-of':
                    categories[3]+=1
                elif item == 'has-part':
                    categories[4]+=1
                elif item == 'is-part-of':
                    categories[5]+=1
                elif item == 'used-by':
                    categories[6]+=1
                elif item == 'uses':
                    categories[7]+=1
                elif item == 'other':
                    categories[8]+=1
                elif item == 'modifierOf':
                    categories[9]+=1
                else:
                    raise Exception('not found' + item)

            print "YYYYYYYYYYYYYYYYY", categories

            fmatrix.append(categories)
 
        else:
            # default
            if key == "climate_change-global_warming" or key=="climatechange": continue
            #print resultlist[key]
            num_pos = len( [x for x in resultlist[key] if x == "true"] )
            num_neg = len( [x for x in resultlist[key] if x == "false"] )

            fmatrix.append([num_pos, num_neg])
            
            if num_pos>=num_neg: perc_true+=1
        c+=1



    # now we got the matrix
    import fleiss_kappa
    fleiss_kappa.computeKappa(fmatrix)

    print "percentage true", perc_true / float(c)






def print_csv(evaluators, resultlist):

    fn = "../results/" + FILE_PATTERN + "_results.csv"
    print fn
    ofile = open(fn, 'wb')

    # (header)
    ofile.write( "class," + ",".join(evaluators) + "\n") 

    # now we have the list, do the output     
    for key in sorted(resultlist):
        #print key
        #print resultlist[key]
        ofile.write("%s,%s\n" % (key, ",".join(resultlist[key])))

def compute_majority_check(evaluators, resultlist):
    """
        check if cf evaluators have the same opinion as the majority -- do only for cf_results1
    """

    # get index of cf_results
    i=0
    for e in evaluators:
        if e == "cf_results1":
            cfindex=i
            print "index of cf_results", cfindex 
        i+=1
            
    # now we have the list, do the output     
    for key in sorted(resultlist):
        if key == "climate_change-global_warming" or key=="climatechange": continue
        #print resultlist[key]
        num_pos = len( [x for x in resultlist[key] if x == "true"] )
        num_neg = len( [x for x in resultlist[key] if x == "false"] )

        if num_pos>=num_neg:
            # expecting true
            if not resultlist[key][cfindex] == "true":
                print "different opinion", key, num_pos, num_neg, resultlist[key]
            else:
                print "same opinion", resultlist[key]
        else:
            # expecting false 
            if not resultlist[key][cfindex] == "false":
                print "different opinion", key, num_neg, num_pos, resultlist[key]
            else:
                print "same opinion", resultlist[key]

    print evaluators    

