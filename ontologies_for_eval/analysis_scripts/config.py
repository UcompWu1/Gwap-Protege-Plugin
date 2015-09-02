# config vars ..

# this is the main setting --> decides which data will be read
#FILE_PATTERN="climate_change_class"
#FILE_PATTERN="euro_class"
#FILE_PATTERN="climate_change_subclass"
#FILE_PATTERN="euro_subclass"
FILE_PATTERN="cc"
#FILE_PATTERN="tennis"


WHO="all"
#WHO="cf_only"
#WHO="manual_only"

DEBUG = False
DEBUG = True 

#ANNOTATION = "uComp_class_relevance"
ANNOTATION = "onto:uComp_subclassof_check"
#FILE = "euro_class_relevance_MS.rdf"
FPATH = "../evaluators/relation_suggestion"


# protege tags
P_CLASS_OPEN  = "<owl:Class"
P_CLASS_CLOSE = "</owl:Class"


