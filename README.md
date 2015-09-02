# GWAP-Plugin-For-Protege
A GWAP Plugin for the validation of ontologies for the Protege Ontology Editor 

This plugin for the Protege Ontology Editor is a Game With A Purpose for the validation of ontologies. 
The goals is to advance the automation and therefore the scalability of the Semantic Web.  

# How to install this plugin
In the Protege Editor go to file --> check for plugins and install the at.ac.wu.ucompvalidation
Plugin. 
Open an ontology. Go to classes. 
Go to window -> views -> Class views and choose either the ucomp Class Validation or the Ucomp Subclass Validation View. 

There are other views available in other ontology propertiers (object properties, data properties...) 
For the full description of the possible use cases for this plugin please take a look at the documentation (Documentation/uComp_Crowdsourcing_Validation_Plugin_Documentation.pdf). 

Before you use the plugin please make sure that you have a valid ucomp_api_settings.txt file. You will need an uComp API key which you can obtain here : http://soc.ecoresearch.net/facebook/election2008/ucomp-quiz-beta/api/v1/documentation/

# How to modify this plugin
These Instructions are valid for a development environment using Eclipse. 

1. Make sure you are using Java 7
2. Update your Path Variables and Java Home to match Java 7
3. Download the latest 32 Bit Version of Protege Plugin without the Java VM
4. Grant full read and write access everyone to the folder \Protege_4.3\plugins
5. Download and install Eclipse 
6. file --> switch workpace and choose protege_plugin_data/Workspace as new workspace 
7. add the 6 libaries from the folder plugin_data/Libaries to your path (Build Path --> Libaries --> Add external jar)

after you finished your code changes:

8. src --> export --> export as jar file 
9. select the package at.ac.wu.ucompvalidation, unselect the META-INF folder
10. select the lib folder --> next --> next 
11. At the last step select the manifest file from the META-INF folder

Optionally:

12. change the version the file update.properties
13. add your changes to the changelog in html-readme file
14. upload your jar file, update-properties, readme-html and your documentation-pdf to the protege plugin webserver to make your plugin publicly available

