package at.ac.wu.ucompvalidation;

import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;

//////////////////////////////////////////////////////////////////////////////////////////////////////
// CLASS GWAPVALIDATION																				//
// this final Java Class contains a set of static methods, which are used in the two plugin classes	//
//////////////////////////////////////////////////////////////////////////////////////////////////////
public final class GwapValidation {
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	// METHOD IS VALIDATING											      						  		 //
	// this method checks if an OWL Entity is currently getting validated 						  		 //
	// this method is used from all plugin classes, therefore it has an OWLEntity as parameter	  		 //
	// Validation Types: 																		  		 //
	// 1 = class validation, 2 = subclass validation, 3 = individual validation, 4 = property validation //
	// 5 = relation validation																			 //
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	public static boolean isValidating (OWLEntity selectedEntity, OWLOntology currentOntology, OWLAnnotationProperty annoType, int validationType) {
		boolean validating = false;
		
		// Search all annotations for keyphrase and return if there is a validation in progress
		for (OWLAnnotation anno : selectedEntity.getAnnotations(currentOntology, annoType)) {			
			try {
				switch(validationType) {
				case 1: 
					if (anno.getValue().toString().substring(1, 12).equals("#gwapclass:")) {
						validating = true;
					}
					break;
				case 2:
					if (anno.getValue().toString().substring(1, 15).equals("#gwapsubclass:")) {
						validating = true;
					}					
					break;
				case 3:
					if (anno.getValue().toString().substring(1, 17).equals("#gwapindividual:")) {
						validating = true;
					}
					break;
				case 4:
					if (anno.getValue().toString().substring(1, 15).equals("#gwapproperty:")) {
						validating = true;
					}
					break;
				case 5:
					if (anno.getValue().toString().substring(1, 15).equals("#gwaprelation:")) {
						validating = true;
					}
				}
			}
			// catch exceptions for index out of bounds for substring (if other, shorter labels than #gwap exist), but do nothing
			catch (Exception e) {				
			}				
		}
		// return if there is a validation in progress
		return validating;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	// METHOD GET INFO																		  			 //
	// this method gets the additional information for the gwap validation process of an OWL Entity 	 //
	// this method is used from all plugin classes, therefore it has an OWLEntity as parameter			 //
	// Validation Types: 																				 //
	// 1 = class validation, 2 = subclass validation, 3 = individual validation, 4 = property validation //
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	public static String getInfo(OWLEntity selectedEntity, OWLOntology currentOntology, OWLAnnotationProperty annoType, int validationType) {
		String info = "", com = "";
		int index1, index2;
		boolean validating = false;
		
		// search in class annotations
		for (OWLAnnotation anno : selectedEntity.getAnnotations(currentOntology, annoType)) {
			com = anno.getValue().toString();
			
			try {
				switch(validationType) {
					case 1:
						if (com.substring(1, 12).equals("#gwapclass:")) {
							validating = true;
						}
						break;
					case 2:
						// differ from "#gwapsubclass:remove=true;" to extract info later
						if (com.substring(1, 22).equals("#gwapsubclass:domain=")) {
							validating = true;
						}
						break;
					case 3:
						if (com.substring(1, 24).equals("#gwapindividual:domain=")) {
							validating = true;
						}
						break;
					case 4:
						// differ from "#gwapproperty:remove=true;" to extract info later
						if (com.substring(1, 22).equals("#gwapproperty:domain=")) {
							validating = true;
						}
						break;
					case 5:
						if (com.substring(1, 22).equals("#gwaprelation:domain=")) {
							validating = true;
						}
				}
			}
			// catch exceptions for index out of bounds for substring (if other, shorter labels than #gwap exist), but do nothing
			catch (Exception e) {				
			}
			
			if (validating == true) {
				index1 = com.indexOf("info=");
				// search for next semicolon
				index2 = com.indexOf(";", index1);
				info = anno.getValue().toString().substring(index1 + 5, index2);
			}
			
			validating = false;
		}
		// return additional information
		return info;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// METHOD GET CROWDFLOWER																		  	 	 //
	// this method gets the information whether the validation task is sent to CrowdFlower or the uComp-Quiz //
	// this method is used from all plugin classes, therefore it has an OWLEntity as parameter			 	 //
	// Validation Types: 																				 	 //
	// 1 = class validation, 2 = subclass validation, 3 = individual validation, 4 = property validation 	 //
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static boolean getCrowdflower(OWLEntity selectedEntity, OWLOntology currentOntology, OWLAnnotationProperty annoType, int validationType) {
		boolean crowdflower = true;
		String value = "", com = "";
		int index1, index2;
		boolean validating = false;
		
		// search in class annotations
		for (OWLAnnotation anno : selectedEntity.getAnnotations(currentOntology, annoType)) {
			com = anno.getValue().toString();
			
			try {
				switch(validationType) {
					case 1:
						if (com.substring(1, 12).equals("#gwapclass:")) {
							validating = true;
						}
						break;
					case 2:
						// differ from "#gwapsubclass:remove=true;" to extract info later
						if (com.substring(1, 22).equals("#gwapsubclass:domain=")) {
							validating = true;
						}
						break;
					case 3:
						if (com.substring(1, 24).equals("#gwapindividual:domain=")) {
							validating = true;
						}
						break;
					case 4:
						// differ from "#gwapproperty:remove=true;" to extract info later
						if (com.substring(1, 22).equals("#gwapproperty:domain=")) {
							validating = true;
						}
						break;
					case 5:
						if (com.substring(1, 22).equals("#gwaprelation:domain=")) {
							validating = true;
						}
				}
			}
			// catch exceptions for index out of bounds for substring (if other, shorter labels than #gwap exist), but do nothing
			catch (Exception e) {				
			}
			
			if (validating == true) {
				index1 = com.indexOf("crowdflower=");
				// search for next semicolon
				index2 = com.indexOf(";", index1);
				value = anno.getValue().toString().substring(index1 + 12, index2);				
				// check value
				if (value.equals("false")) {
					crowdflower = false;
				}				
				else {
					crowdflower = true;
				}
			}
			
			validating = false;
		}
		
		// else search in ontology annotations
		if (value.equals("")) {
			for (OWLAnnotation anno : currentOntology.getAnnotations()) {
				com = anno.getValue().toString();
				try {
					if (com.substring(1, 12).equals("#gwapclass:")) {
						index1 = com.indexOf("crowdflower=");
						// search for next semicolon
						index2 = com.indexOf(";", index1);
						value = anno.getValue().toString().substring(index1 + 12, index2);
						// check value
						if (value.equals("false")) {
							crowdflower = false;
						}				
						else {
							crowdflower = true;
						}
					}
				}
				// catch exceptions for index out of bounds for substring (if other, shorter labels than #gwap exist), but do nothing
				catch (Exception e) {				
				}
			}
		}	
		
		// return if crowdflower was selected
		return crowdflower;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	// METHOD GET JOB ID																		  		 //
	// this method gets the job ID for the gwap validation process of an OWL Entity 			  		 //
	// this method is used from all plugin classes, therefore it has an OWLEntity as parameter    		 //
	// Validation Types: 																				 //
	// 1 = class validation, 2 = subclass validation, 3 = individual validation, 4 = property validation //
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	public static long getJobID(OWLEntity selectedEntity, OWLOntology currentOntology, OWLAnnotationProperty annoType, int validationType) {
		String com = "";
		int index1, index2;
		long jobid = 0;
		boolean validating = false;
		
		// search in class annotations
		for (OWLAnnotation anno : selectedEntity.getAnnotations(currentOntology, annoType)) {
			com = anno.getValue().toString();
			
			try {
				switch(validationType) {
				case 1:
					if (com.substring(1, 12).equals("#gwapclass:")) {
						validating = true;
					}
					break;
				case 2:
					// differ from "#gwapsubclass:remove=true;" to extract jobid later
					if (com.substring(1, 22).equals("#gwapsubclass:domain=")) {
						validating = true;
					}
					break;
				case 3:
					if (com.substring(1, 24).equals("#gwapindividual:domain=")) {
						validating = true;
					}
					break;
				case 4:
					// differ from "#gwapproperty:remove=true;" to extract jobid later
					if (com.substring(1, 22).equals("#gwapproperty:domain=")) {
						validating = true;
					}
					break;
				case 5:
					if (com.substring(1, 22).equals("#gwaprelation:domain=")) {
						validating = true;
					}
				}
				
				if (validating == true) {
					index1 = com.indexOf("jobid=");
					// search for next semicolon
					index2 = com.indexOf(";", index1);
					jobid = Long.valueOf(anno.getValue().toString().substring(index1 + 6, index2)).longValue();
				}
				
				validating = false;
			}
			// catch exceptions for index out of bounds for substring (if other, shorter labels than #gwap exist), but do nothing
			catch (Exception e) {				
			}		
		}
		// return additional information
		return jobid;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	// METHOD GET TASK ID																		  		 //
	// this method gets the task ID for the gwap validation process of an OWL Entity 			  		 //
	// this method is used from all plugin classes, therefore it has an OWLEntity as parameter    		 //
	// Validation Types: 																				 //
	// 1 = class validation, 2 = subclass validation, 3 = individual validation, 4 = property validation //
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	public static int getTaskID(OWLEntity selectedEntity, OWLOntology currentOntology, OWLAnnotationProperty annoType, int validationType) {
		String com = "";
		int index1, index2;
		int taskid = 0;
		boolean validating = false;
		
		// search in class annotations
		for (OWLAnnotation anno : selectedEntity.getAnnotations(currentOntology, annoType)) {
			com = anno.getValue().toString();
			
			try {
				switch(validationType) {
				case 1:
					if (com.substring(1, 12).equals("#gwapclass:")) {
						validating = true;
					}
					break;
				case 2:
					// differ from "#gwapsubclass:remove=true;" to extract taskid later
					if (com.substring(1, 22).equals("#gwapsubclass:domain=")) {
						validating = true;
					}
					break;
				case 3:
					if (com.substring(1, 24).equals("#gwapindividual:domain=")) {
						validating = true;
					}
					break;
				case 4:
					// differ from "#gwapproperty:remove=true;" to extract taskid later
					if (com.substring(1, 22).equals("#gwapproperty:domain=")) {
						validating = true;
					}
					break;
				case 5:
					if (com.substring(1, 22).equals("#gwaprelation:domain=")) {
						validating = true;
					}
				}
				
				if (validating == true) {
					index1 = com.indexOf("taskid=");
					// search for next semicolon
					index2 = com.indexOf(";", index1);
					taskid = Integer.valueOf(anno.getValue().toString().substring(index1 + 7, index2)).intValue();
				}
				
				validating = false;
			}
			// catch exceptions for index out of bounds for substring (if other, shorter labels than #gwap exist), but do nothing
			catch (Exception e) {				
			}		
		}
		// return additional information
		return taskid;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// METHOD GET DOMAIN																					   //
	// this method gets the domain against which the current class or property is verified					   //
	// it first searches in the comments of the class, if no data is found, the domain of the ontology is used //
	// this method is only used from GwapClassValidation, therefore it has an OWLClass as parameter			   //
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static String getDomain(OWLEntity selectedEntity, OWLOntology currentOntology, OWLAnnotationProperty annoType, int validationType) {
		String domain = "", com = "";
		int index1, index2;
		boolean validating = false;
		
		// search in class annotations
		for (OWLAnnotation anno : selectedEntity.getAnnotations(currentOntology, annoType)) {
			com = anno.getValue().toString();
			
			try {
				switch(validationType) {
				case 1:
					if (com.substring(1, 12).equals("#gwapclass:")) {
						validating = true;
					}
					break;
				case 2:
					// differ from "#gwapsubclass:remove=true;" to extract taskid later
					if (com.substring(1, 22).equals("#gwapsubclass:domain=")) {
						validating = true;
					}
					break;
				case 3:
					if (com.substring(1, 24).equals("#gwapindividual:domain=")) {
						validating = true;
					}
					break;
				case 4:
					// differ from "#gwapproperty:remove=true;" to extract domain later
					if (com.substring(1, 22).equals("#gwapproperty:domain=")) {					
						validating = true;
					}
					break;
				case 5:
					if (com.substring(1, 22).equals("#gwaprelation:domain=")) {
						validating = true;
					}
				}
			}
			// catch exceptions for index out of bounds for substring (if other, shorter labels than #gwap exist), but do nothing
			catch (Exception e) {				
			}
				
			if (validating == true) {
				index1 = com.indexOf("domain=");
				index2 = com.indexOf(";", index1);
				domain = anno.getValue().toString().substring(index1 + 7, index2);
			}
			
			validating = false;
		}
		
		// else search in ontology annotations
		if (domain.equals("")) {
			for (OWLAnnotation anno : currentOntology.getAnnotations()) {
				com = anno.getValue().toString();
				try {
					if (com.substring(1, 12).equals("#gwapclass:")) {
						index1 = com.indexOf("domain=");
						// search for next semicolon
						index2 = com.indexOf(";", index1);
						domain = anno.getValue().toString().substring(index1 + 7, index2);
					}
				}
				// catch exceptions for index out of bounds for substring (if other, shorter labels than #gwap exist), but do nothing
				catch (Exception e) {				
				}
			}
		}
		
		// return the domain
		return domain;
	}	
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// METHOD GET SUBJECT																		//
	// this method gets the subject (Domain) for the gwap validation process of an OWL Property	//
	// this method is used from all plugin classes, therefore it has an OWLEntity as parameter  //
	//////////////////////////////////////////////////////////////////////////////////////////////
	public static String getSubject(OWLEntity selectedEntity, OWLOntology currentOntology, OWLAnnotationProperty annoType) {
		String subject = "", com = "";
		int index1, index2;
		
		// search in class annotations
		for (OWLAnnotation anno : selectedEntity.getAnnotations(currentOntology, annoType)) {
			com = anno.getValue().toString();
			try {
				if (com.substring(1, 15).equals("#gwapproperty:")) {					
					index1 = com.indexOf("subject=");
					// search for next semicolon
					index2 = com.indexOf(";", index1);
					subject = anno.getValue().toString().substring(index1 + 8, index2);
				}
			}
			// catch exceptions for index out of bounds for substring (if other, shorter labels than #gwap exist), but do nothing
			catch (Exception e) {				
			}
		}
		// return subject
		return subject;
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// METHOD GET OBJECT																		//
	// this method gets the object (Range) for the gwap validation process of an OWL Property	//
	// this method is used from all plugin classes, therefore it has an OWLEntity as parameter  //
	//////////////////////////////////////////////////////////////////////////////////////////////
	public static String getObject(OWLEntity selectedEntity, OWLOntology currentOntology, OWLAnnotationProperty annoType) {
		String object = "", com = "";
		int index1, index2;
		
		// search in class annotations
		for (OWLAnnotation anno : selectedEntity.getAnnotations(currentOntology, annoType)) {
			com = anno.getValue().toString();
			try {
				if (com.substring(1, 15).equals("#gwapproperty:")) {					
					index1 = com.indexOf("object=");
					// search for next semicolon
					index2 = com.indexOf(";", index1);
					object = anno.getValue().toString().substring(index1 + 7, index2);
				}
			}
			// catch exceptions for index out of bounds for substring (if other, shorter labels than #gwap exist), but do nothing
			catch (Exception e) {				
			}
		}
		// return object
		return object;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// METHOD GET CHECKBOX																										   //
	// this method checks for a relation validation the status of an checkbox by searching through the object property annotations //
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static boolean getCheckBox(OWLEntity selectedEntity, OWLOntology currentOntology, OWLAnnotationProperty annoType, String property) {
		boolean prop = false;
		String value = "", com = "";
		int index1, index2;		
		
		// search in object property annotations
		for (OWLAnnotation anno : selectedEntity.getAnnotations(currentOntology, annoType)) {
			com = anno.getValue().toString();

			try {
				if (com.substring(1, 15).equals("#gwaprelation:")) {
					index1 = com.indexOf("op_" + property + "=");
					// search for next semicolon
					index2 = com.indexOf(";", index1);
					value = anno.getValue().toString().substring(index1 + property.length() + 4, index2);
					// check value
					if (value.equals("true")) {
						prop = true;
					}				
					else {
						prop = false;
					}
				}
			}
			// catch exceptions for index out of bounds for substring (if other, shorter labels than #gwap exist), but do nothing
			catch (Exception e) {				
			}
		}
		// return true, if validate all relations was selected, else return false
		return prop;
	}	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// METHOD GET SUBTREE																				  					   //
	// this method checks if there is a validation for the whole subtree of the current class in progress 					   //
	// this method is only used from GwapClassValidation and GwapSubclassValidation, therefore it has an OWLClass as parameter //
	// Validation Types: 1 = class validation, 2 = subclass validation														   //
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static boolean getSubtree(OWLClass selectedClass, OWLOntology currentOntology, OWLAnnotationProperty annoType, int validationType) {
		boolean subtree = false;
		String value = "", com = "";
		int index1, index2;
		boolean validating = false;
		
		// search in class annotations
		for (OWLAnnotation anno : selectedClass.getAnnotations(currentOntology, annoType)) {
			com = anno.getValue().toString();
			
			try {
				switch(validationType) {
				case 1:
					if (com.substring(1, 12).equals("#gwapclass:")) {
						validating = true;
					}
					break;
				case 2:
					// differ from "#gwapsubclass:remove=true;" to extract subtree later
					if (com.substring(1, 22).equals("#gwapsubclass:domain=")) {
						validating = true;
					}
					break;
				}
			}
			// catch exceptions for index out of bounds for substring (if other, shorter labels than #gwap exist), but do nothing
			catch (Exception e) {				
			}
			
			if (validating == true) {
				index1 = com.indexOf("subtree=");
				// search for next semicolon
				index2 = com.indexOf(";", index1);
				value = anno.getValue().toString().substring(index1 + 8, index2);
				// check value
				if (value.equals("true")) {
					subtree = true;
				}				
				else {
					subtree = false;
				}
			}
			
			validating = false;
		}
		// return true, if validation for whole subtree is in progress, else return false
		return subtree;
	}
}
