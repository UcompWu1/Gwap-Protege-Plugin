import re


p = re.compile('label = (.*)</rdfs')

f = open('swapped.txt').readlines()

for l in f:
    m = re.search(p,l)
    print m.groups()[0].replace('_',' ')


