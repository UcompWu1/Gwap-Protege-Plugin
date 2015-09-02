import math

def average(s): 
    return sum(s) * 1.0 / len(s)

def avg_variance(s, avg):
    vari = [(val-avg)**2 for val in s]
    print "avg vari", average(vari)
    print "stddev", math.sqrt(average(vari))
     





cc1= [29,    32  ,    35    , 31   , 22    , 26   , 25    , 19]
cc2= [20   , 16  ,    20    , 23   , 30    , 16   , 35    , 24]


e1 =  [15   , 9   ,    30    ,        30    , 19   , 23, 23]
e2 =  [ 9   , 10  ,    24    ,        12    , 11   , 17, 22]


avg = average(cc1)
print avg
avg_variance(cc1, avg)
print


avg = average(cc2)
print avg
avg_variance(cc2, avg)

print
avg = average(e1)
print avg
avg_variance(e1, avg)

print
avg = average(e2)
print avg
avg_variance(e2, avg)


