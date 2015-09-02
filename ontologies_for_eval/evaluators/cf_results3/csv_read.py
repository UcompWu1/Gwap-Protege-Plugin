import csv,pickle

FN='cc_concept.csv'

RESULTCOL=6
QUESTIONCOL=7

results = {}

with open(FN, 'rb') as csvfile:
    csvr = csv.reader(csvfile)
    for row in csvr:
        print row
        print row[RESULTCOL], row[QUESTIONCOL]
        
        # extract concept from question
        if not row[QUESTIONCOL].find("concept")>0:
            continue
        print row[QUESTIONCOL]
        concept = row[QUESTIONCOL].split(" ")[3]
        print concept 

        if concept == "Thing":
            continue

        if row[RESULTCOL] == "1":
            value = "true"
        elif row[RESULTCOL] == "2":
            value = "false"
        else:
            sys.exit("ERROR")        
    
        results[concept] = value 

print results, len(results)
for key in sorted(results):
    print key, results[key]


pickle.dump(results, open(FN[:-11]+"class.pickle", "wb"))
