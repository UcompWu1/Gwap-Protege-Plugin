import sys,pickle
result = {}
for line in open(sys.argv[1]):
    if line.find('relation')>=0: continue
    line=line.strip()
    fields = line.split()
    print fields 

    result['relation_'+fields[0]] = fields[1].lower()

print result

pickle.dump(result, open(sys.argv[2], 'wb'))
