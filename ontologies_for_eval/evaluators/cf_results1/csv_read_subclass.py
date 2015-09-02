import csv,pickle,re

FN='euro_sc.csv'
FN='cc_sc.csv'


RESULTCOL=6
QUESTIONCOL=8

results = {}

pattern = "Is class (.*) a subclass of (.*)\?$"

with open(FN, 'rb') as csvfile:
    csvr = csv.reader(csvfile)
    for row in csvr:
        print row
        
        # extract concepts from question
        q = row[QUESTIONCOL]
        if not q.find("subclass of")>0:
            continue
        print q 

        concepta = re.search(pattern, q).group(1)        
        conceptb = re.search(pattern, q).group(2)        

        print concepta, conceptb

        if row[RESULTCOL] == "1":
            value = "true"
        elif row[RESULTCOL] == "2":
            value = "false"
        else:
            sys.exit("ERROR")        
    
        results[concepta+"-"+conceptb] = value 

print results, len(results)
for key in sorted(results):
    print key, results[key]


pickle.dump(results, open(FN[:-6]+"subclass.pickle", "wb"))
