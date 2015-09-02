package at.ac.wu.ucompvalidation;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.Scanner;

////////////////////////////////////////////////////
// CLASS GWAPCOMMUNICATION						  //
// Java Class for the communication with the GWAP //
////////////////////////////////////////////////////
public class GwapCommunication {
	
	// define static part of the server URL
	private final String server = "https://www.ecoresearch.net/soc/facebook/election2008/ucomp-quiz-beta/api/v1/";
	
	// Attributes
	private String serverurl, json, line; 
	private URL url;
    private HttpURLConnection connection;
    private DataOutputStream wr;
    private InputStream is;
    private OutputStream out;
    private FileReader in;
    private BufferedReader rd;
    private StringBuffer response;
    private JSONParser parser;
    private JSONObject result;	
	
    // Constructor
    public GwapCommunication() {   	
    }
        
	////////////////////////////////////////////////////////////////////////////////////////////////////
	// METHOD CREATE JOB										      					   			  //
	// this method creates a validation job at the GWAP									   			  //
	// therefore it communicates with the GWAP with JSON and uploads the game data per CSV 			  //
    // the parameters include the information, whether it's a concept or individual validation,		  //
    // the subject (concept or individual), the object (domain or concept) and additional information //
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
    
    public FileWriter apppendGoldUnits(FileWriter existingWriter, String filePath){
			Scanner scanner;
			try {
				scanner = new Scanner(new File(filePath));
				 //Set the delimiter used in file
		        scanner.useDelimiter(",");					        
		        while (scanner.hasNext())
		        {
		            //System.out.print(scanner.next() + "|");
		            // adding all the elements of the csv file to the writer here 
					try {
						existingWriter.append(scanner.next());
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					if (scanner.hasNext()){
						try {
							existingWriter.append(',');
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}								
		        }
		        //Do not forget to close the scanner 
		        scanner.close();
			} catch (FileNotFoundException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
	       	    
    	return existingWriter;
    }
    // two method definitions for method overwriting
    public long createJob(int validationType, boolean crowdflower, String domain, ArrayList<String> subjects, 
			  ArrayList<ArrayList<String>> objects, /*String property, String subject, String object,*/ 
			  ArrayList<String> oprops, String info, String numberOfJudgementsDesired,
			  String paymentPerAssignment){
		
    	long returnValue = this.createJob(validationType, crowdflower, domain, subjects, objects, oprops, info, numberOfJudgementsDesired,
				  paymentPerAssignment,null);
    	
    	return returnValue;
	}
	
	
	   
    
    public long createJob(int validationType, boolean crowdflower, String domain, ArrayList<String> subjects, 
						  ArrayList<ArrayList<String>> objects, /*String property, String subject, String object,*/ 
						  ArrayList<String> oprops, String info, String numberOfJudgementsDesired,
						  String paymentPerAssignment, String goldQuestionFile ) {
		
		boolean success = false;
		long jobid = 0;
		String mode = "", payment = "", description = "", jobname = "";
		String apikey = "";
		int judgements = 0, centspaid = 0;
		
		System.out.println("[CREATE JOB]");
		
		// get API-Settings
		try {
			apikey 		= getAPISettings().substring(0, getAPISettings().indexOf(","));
			//judgements 	= Integer.valueOf(getAPISettings().substring(getAPISettings().indexOf(",") + 1, getAPISettings().lastIndexOf(",")));
			if (numberOfJudgementsDesired == "default"){
				judgements = 1;
				}
			else{
				judgements = Integer.parseInt(numberOfJudgementsDesired);
			}
			if (paymentPerAssignment == "default"){
				centspaid = 1;
				}
			else{
				centspaid = Integer.parseInt(paymentPerAssignment);
			}
			//judgements 	= judgements;
			//centspaid	= Integer.valueOf(getAPISettings().substring(getAPISettings().lastIndexOf(",") + 1));
			
		}	
		catch (Exception e) {
			System.out.println("Can't read API-Settings. Make sure there is a textfile named \"ucomp_api_settings.txt\" in your home directory " +
					   "in the \".Protege\" folder containing nothing except your API-Key, the number of judgements and the crowdflower payment in cents per task" +
					   "in one line in the following format:\n<ucomp_api_key>,<number_of_judgements_per_unit>,<payment_in_cents>\n" +
					   "Example file content: abcdefghijklmnopqrst,5,2\nPlease refer to our documentation for further information!");
			// What the hell is minus 2 - fuck you, sir!
			return -2;
		}
		
		// parameters vary depending on the validation method (crowdflower or ucomp-quiz)
		if (crowdflower) {
			mode = "crowdflower";
			payment = ", \"cf_payment_per_assignment\":\"" + centspaid + "\"";
		}
		else {
			mode = "quiz";
			payment = "";
		}
		
		// description depends on validation type
		// no quotation marks allowed
		switch (validationType) {
			case 1: jobname		= "Check word relevance  for a domain (" + domain +")";
					description = "Please decide whether the given word (also known as concept) is relevant for the mentioned domain. " +
								  "Generally, <i>relevant</i> means that the word comes to ones mind when thinking about that domain.<br><br>" +
								  "Class Relevance Check:<br>" +
								  "Your task is to decide if a given concept (also called class) is relevant for a given domain.<br><br>" +
								  "Examples:<br>" +
								  "Is racket relevant to the domain of tennis? - Correct answer: Yes<br>" +
								  "Is game relevant to the domain of tennis? - Correct answer: Yes<br>" +							
								  "Is election relevant to the domain of politics? - Correct answer: Yes<br>" +
								  "Is racquet relevant to the domain of politics? - Correct answer: No<br>" +
								  "Is party system relevant to the domain of politics? - Correct answer: Yes<br>" +
								  "Is partysystem relevant to the domain of politics? - Correct answer: No, this is no English term!<br><br><br>" +
								  "Sometimes there is no answer that is clearly correct, because the concept may be slightly relevant, too generic, or too specific.<br>" +
								  "Examples:<br>" +
								  "Is human relevant to the domain of politics? - Unclear, probably: Yes, but very generic.<br>" +
								  "Is weather relevant to the domain of politics? - Unclear, probably: No, only slightly relevant.<br>" +
								  "Is event relevant to the domain of politics? - Unclear, but probably: Yes.<br><br>" +
								  "Please consult the Web or any external source for additional information you might need for completing this task" +
								  " (for example, checking the definition of climate related terms on Wikipedia)." + " Additional information: " + info;
					break;										
			case 2: jobname		= "Verify that a term is more specific than another (" + domain +")";
					description = "Task: You task is to decide if a term A is more specific than a term B.<br><br>" +
								  "A term A (e.g., cat) is more specific than a term B (e.g., animal) if it can be said that <i>every A is a B</i>, " +
								  "but not the other way around. Indeed, every cat is an animal but not every animal is a cat.<br><br>" +
								  "If A (e.g., cat) is more specific than B (e.g., animal), we say that A is a subClass of B.<br><br>" +
								  "Attention! The order of the terms is important:<br><br>" +
								  "<i>cat - is a a subClass of - animal</i> is TRUE because every cat is also an animal (cats are types of animals)<br><br>" +
								  "<i>animal - is a a subClass of - cat</i> is FALSE because not every animal is a cat (animals are not types of cats)<br><br>" +
								  "Examples: Is human a subClass of mammal? Correct answer: Yes<br><br>" +
								  "Is human a subClass of car? Correct answer: No<br><br>" +
								  "Is politician a subClass of human? Correct answer: Yes<br><br>" +
								  "Is water a subClass of liquid? Correct answer: Yes<br><br>" +
								  "Is water a subClass of car? Correct answer: No<br><br>" +
								  "Please consult the Web or any external source for additional information you might need for completing this task " +
								  "(for example, checking the definition of climate related terms on Wikipedia)."+ " Additional information: " + info ;
					break;										
			
			case 3: jobname		= "Verify that an individual has the correct class type(" + domain +")";
			description = "Task: Your task is to decide an term (thing) is of a certain type (class).<br><br>" +
						  "Examples: Is <b>Barack Obama</b> a type of <b>president</b>? Correct answer: Yes<br><br>" +
						  "Is <b>Barack Obama</b> a type of <b>person</b>? Correct answer: Yes<br><br>" +
						  "Is <b>Barack Obama</b> a type of <b>location</b>? Correct answer: No<br><br>" +
						  "Is <b>Roger Federer</b> a type of <b>tennis player</b>? Correct answer: Yes<br><br>" +
						  "Is <b>Roger Federer</b> a type of <b>professional chess player</b>? Correct answer: No<br><br>" +
						  "Is <b>Coca Cola</b> a type of <b>soft drink</b>? Correct answer: Yes<br><br>" +
						  "Is <b>Coca Cola</b> a type of <b>person</b>? Correct answer: No<br><br>" +
						  "Please consult the Web or any external source for additional information you might need for completing this task " +
						  "(for example, checking the definition of climate related terms on Wikipedia).  "+ "Additional Information: "+ info;
			break;
			case 4: jobname		= "Property Domain Validation Task For The Domain(" + domain +")";
			description = "Select the correct option" + "Additional Information: "+ info;
			break;
			
			case 6: jobname		= "Property Range Validation Task For The Domain(" + domain +")";
			description = "Select the correct option" + "Additional Information: "+ info;
			break;	
			
			default:jobname		= "Ontology Learning";
					description = "Select the correct option" + "Additional Information: "+ info;
		}
				
		// initialise variables
		serverurl = server + "job.php";
		json   = "{\"key\":\"" + apikey + "\",\"task_title\":\"" + jobname + "\", \"task_type\":\"classification\", " + 
				 "\"task_description\":\"" + description + "\", \"task_mode\":\"" + mode + "\", \"judgements\":\"" + judgements + "\", " + 
				 "\"ts_default_categories\" : {\"1\":\"Yes\", \"2\":\"No\",\"3\":\"I don\'t know\"} , " + 
				 "\"ts_multiple_choice\":\"false\"" +", \"cf_channel\":\"cf_internal\"" +  payment + "}";
		parser	   = new JSONParser();
		connection = null;
				
		System.out.println("Communicating with server: " + serverurl);
		
		// global try/catch/finally block
		try {
			
			// connect to the webserver
			url = new URL(serverurl);
			connection = (HttpURLConnection) url.openConnection();
			// POST-method and JSON
			connection.setRequestMethod("POST");
			connection.setDoInput(true);
		    connection.setDoOutput(true);		    
		    connection.setRequestProperty("Content-Type", "application/json");
		   	connection.setRequestProperty("Content-Length", "" + Integer.toString(json.length()));
		   	
		   	// send game metadata
		   	System.out.println("Sending data to server: " + json);
		    wr = new DataOutputStream(connection.getOutputStream());
		    try {
		    	
		    	wr.writeBytes(json);
			    wr.flush();
		    }
		    finally {
		    	wr.close();
		    }
		    	    
		    // get back response including job ID
		    is = connection.getInputStream();
		    rd = new BufferedReader(new InputStreamReader(is));
		    response = new StringBuffer();
			try {
			    while((line = rd.readLine()) != null) {
			      response.append(line);
			    }
			}
			finally {
				rd.close();
			}
		    // debug information
		    System.out.println("Response from server: " + response.toString());
		    connection.disconnect();
		    // parse response string into JSON Object
		    result = (JSONObject) parser.parse(response.toString());
		    
		    // if job creation was successful
		    if (result.get("success").equals("true")) {
		    	
		    	// save job ID
		    	jobid = (long) result.get("job_id");
		    	
		    	// start new connection to webserver for uploading game data
		    	serverurl = server + "data.php";
		    	json = "{\"key\":\"" + apikey + "\", \"job_id\":" + jobid + ", \"gold\":\"true\" }";
		    	url = new URL(serverurl);
		    	connection = (HttpURLConnection) url.openConnection();
		    	connection.setRequestMethod("POST");
		    	connection.setDoInput(true);
		    	connection.setDoOutput(true);	    
			   				
				// create csv file
				File temp = File.createTempFile("data", ".csv");
				System.out.println("Sending file to server: " + temp.getAbsolutePath());
				FileWriter writer = new FileWriter(temp.getAbsolutePath());				
				
				// Validation Types: 1 = class validation, 2 = subclass validation, 3 = individual validation
				switch (validationType) {
					case 1:
						// text for concept validation, multiple lines possible
						//writer.append("What is the second letter of Finance?,a,e,i,3,\"\"\n");
						for (int i = 0; i < subjects.size(); i++) {
							if (i != 0) {
								writer.append("\n");
							}
							writer.append("Is the concept " + "'" + subjects.get(i) + "'" + " relevant for the domain of " + domain + "?" + ", yes, no, I don't know");
									
							if (i == subjects.size()-1) {
								writer.append("\n");
							}
						}
						
						
						  //now adding the gold questions:
						
						//JFileChooser fc = new JFileChooser();
					
						//File f = fc.getSelectedFile();
						// adding the gold Unit questions if desired: 
      					if (goldQuestionFile != null){
      						
      						System.out.println("Reading the following gold question file now: "+ goldQuestionFile);
      						writer = this.apppendGoldUnits(writer, goldQuestionFile);

      					}
						
					
						break;								
					case 2:
						// text for subclass validation, multiple superclasses for multiple subclasses and therefore multiple lines possible
						for (int i = 0; i < subjects.size(); i++) {						
							for (int j = 0; j < objects.get(i).size(); j++) {
								if (!(i == 0 && j == 0)) {
									writer.append("\n");
								}
								writer.append("In the domain of " + domain + ":Is class " + "'" + subjects.get(i) + "'" + " a subclass of " + objects.get(i).get(j)  + "?" + ", yes, no, I don't know");
								
								if (i == subjects.size()-1) {
									writer.append("\n");
								}
							}							
						}
												
						  //now adding the gold questions:
      					if (goldQuestionFile != null){
      						
      						System.out.println("Reading the following gold question file now: "+ goldQuestionFile);
      						writer = this.apppendGoldUnits(writer, goldQuestionFile);
      					}					
						
						break;
					case 3:
						// text for individual validation, multiple types (classes) for multiple individuals and therefore multiple lines possible
						for (int i = 0; i < subjects.size(); i++) {						
							for (int j = 0; j < objects.get(i).size(); j++) {
								if (!(i == 0 && j == 0)) {
									writer.append("\n");
								}
								writer.append("In the domain of " + domain + ": Is " + "'" + subjects.get(i) + "'" + " a type of " + objects.get(i).get(j) + "?"+ ", yes, no, I don't know");
								if (i == subjects.size()-1) {
									writer.append("\n");
								}
							}							
						}
						  //now adding the gold questions:
      					if (goldQuestionFile != null){
      						
      						System.out.println("Reading the following gold question file now: "+ goldQuestionFile);
      						writer = this.apppendGoldUnits(writer, goldQuestionFile);
      					}					
						
						break;
					case 4:
						// text for property domain validation
						int pos = -1;
						for(String property : subjects){
							pos = pos + 1;
							if (property == null || property.equals("")) continue;
							String domainElement = objects.get(pos).get(0);
							if (domainElement == null || domainElement.equals("")) continue;
							writer.append("Is '" +domainElement+ "' a valid domain for the object property '"+property+"'?\n");
						}
						break;
					case 5:
						// text for relation validation
						//What would be a an appropriate label to describe the relation between the subject abc and the object xyz? Additional information: 123,object1,object2,object3
						for (int i = 0; i < subjects.size(); i++) {
							if (i != 0) {
								writer.append("\n");
							}
							writer.append("In the domain of " + domain + ": What is an appropriate label to describe the relation between the subject " + subjects.get(i) + 
										  " and the object " + objects.get(0).get(i) + "?");
							if (!info.equals("")) {
								writer.append(" Additional information: " + info);
							}
							for (String str : oprops) {
								writer.append("," + str);
							}
							// add default answer
							writer.append(",None of those");
						}
						break;
					case 6:
						// text for property range validation
						int rangePosition = -1;
						for(String property : subjects){
							rangePosition = rangePosition + 1;
							if (property == null || property.equals("")) continue;
							System.out.println("the objects that I am working with are: "+ objects);
							String rangeElement = objects.get(rangePosition).get(1);
							if (rangeElement == null || rangeElement.equals("")) continue;
							writer.append("Is '" +rangeElement+ "' a valid range for the object property '"+property+"'?\n");
						}
						break;
				}
				
				writer.flush();
				writer.close();
				
				// file upload
				FileBody fileBody = new FileBody(temp);
                //FileBody fileBody = new FileBody(new File("/home/matyas/Programmierung/wu_workspace/java_workspace/protege_plugin/test_data/new_test.csv"));

				MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.STRICT);
				multipartEntity.addPart("json", new StringBody(json));
				multipartEntity.addPart("data", fileBody);
				
				connection.setRequestProperty("Content-Type", multipartEntity.getContentType().getValue());
				
				out = connection.getOutputStream();
				try {
				    multipartEntity.writeTo(out);
				} 				
				finally {
				    out.close();
				}
				
				// get back response
			    is = connection.getInputStream();
			    rd = new BufferedReader(new InputStreamReader(is));
			    response = new StringBuffer();
			    try {
				    while((line = rd.readLine()) != null) {
				      response.append(line);
				    }
			    }
			    finally {
			    	rd.close();
			    }
			    // debug information
			    System.out.println("Response from server: " + response.toString());	    	
			    connection.disconnect();
			    
			    result = (JSONObject) parser.parse(response.toString());
			    
			    // if upload was successful
			    if (result.get("success").equals("true")) {
			    	System.out.println("uComp API communication successful, task is running! =)");
			    	success = true;
			    }
			    // if not, throw exception
			    else {
			    	throw new Exception();
			    }
		    }
		    // throw exception, if job creation was unsuccessful
		    else {
		    	throw new Exception();
		    }		    
		}
		
		// catch all errors during gwap communication
		catch (Exception e) {
			System.out.println("Error during uComp API communication, please try again later!");
			e.printStackTrace();
			success = false;
		}
		
		// close the HTTP URL connection in any case
		finally {
			if(connection != null) {
		        connection.disconnect();
			}
	    }
		
		// return job ID if job was created successfully, else return -1    	
		if (success == true) {
			return jobid;
		}		
		else {
			return -1;
		}
	}
		
	
	
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	// METHOD GET RESULTS										      					   			    //
	// this method asks the GWAP for results for a specific job ID, the communication is via JSON		//
	// it then returns the detailed results in an array, or null if the validation is still in progress //
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	public int[] getResults(long id) {
		String[] objects = null;
		String task;
		int[] details;
		int[] help;
		int answers = 0;
		String apikey = "";
		
		System.out.println("[GET RESULTS]");
		
		// get API-Key
		apikey = getAPISettings().substring(0, getAPISettings().indexOf(","));
		
		if (apikey == null) {
			return null;
		}
		
		// initialise variables
		parser	   = new JSONParser();
		connection = null;
		serverurl  = server + "result.php";
		json = "{\"key\":\"" + apikey + "\", \"job_id\":" + id + "}";
    		
		System.out.println("Communicating with server: " + serverurl);
		
		// global try/catch/finally block
		try {
	    	
			// connect to the webserver
			url = new URL(serverurl);
	    	connection = (HttpURLConnection) url.openConnection();
	    	// POST-method and JSON
	    	connection.setRequestMethod("POST");
	    	connection.setDoInput(true);
	    	connection.setDoOutput(true);
	    	
	    	MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.STRICT);
			multipartEntity.addPart("json", new StringBody(json));
			connection.setRequestProperty("Content-Type", multipartEntity.getContentType().getValue());
			    	
		   	// send result metadata
			System.out.println("Sending data to server: " + json);
			out = connection.getOutputStream();
			try {
			    multipartEntity.writeTo(out);
			} 				
			finally {
			    out.close();
			}						
			
			// get results from the server
			is = connection.getInputStream();
		    rd = new BufferedReader(new InputStreamReader(is));
		    response = new StringBuffer();
			try {
			    while((line = rd.readLine()) != null) {
			      response.append(line);
			    }
			}
			finally {
				rd.close();
			}
			// debug information
			System.out.println("Response from server: " + response.toString());
			
			// parse JSON data
			result = (JSONObject) parser.parse(response.toString());
			
			// if request was successful
		    if (result.get("success").equals("true")) {
		    	
		    	result  = (JSONObject) result.get("result");		    	
		    	
		    	// if job has already finished
		    	if (result.get("finished").equals(true)) {
		    		
		    		// temporary file for result url
		    		//String responseName = "";
		    		File temp = File.createTempFile("response", ".csv");
					System.out.println("Response from uComp API saved in file: " + temp.getAbsolutePath());
					FileWriter writer = new FileWriter(temp.getAbsolutePath());					
					writer.append(response.toString());
					writer.append("\n" + result.get("result_url").toString());
					writer.append("\n" + result.get("quiz_result_url").toString());
					writer.append("\n" + result.get("crowdflower_result_url").toString());
					writer.flush();
					writer.close();	    														
															
					// get results in csv format
					serverurl = result.get("result_url").toString();
					
					url = new URL(serverurl);
					rd = new BufferedReader(new InputStreamReader(url.openStream()));						
					response = new StringBuffer();					
					
					try {
						// ignore headline
						line = rd.readLine();
						
						// find number of possible answers
						line = rd.readLine();
						objects = line.split(",");
						answers = (objects[1].length() - objects[1].replace("=>", "").length()) / 2;
						
						// fill the details-array with the results (for example "yes", "no", "i don't know") of each task of the job					
						details = new int[answers];	
						
						// initialise current task
						task = objects[0];
												
						do {						
							
							objects = line.split(",");
							
							// if the results of the next task are reached
							if (!task.equals(objects[0])) {
								
								task = objects[0];
								// increase details-array size
								help = Arrays.copyOf(details, details.length);
								details = Arrays.copyOf(help, help.length + answers);
							}
							
							// increase value in array for respective current answer
							// details [x] => x = length of array - (number of possible answers - number of current answer + 1)
							details [details.length - (answers - Integer.parseInt(objects[3].replace("\"", "")) + 1)] += 1;	
						
						// read each result line
					    } while ((line = rd.readLine()) != null);
					}
					
					finally {
						rd.close();
					}			
																								
					if (details != null) {
						// debug information
						System.out.println("Job successfully finished!");
					}
		    	}
		    	
		    	// if job is still in progress
				else {
					// debug information
					System.out.println("Job is still running!");
					details = null;
				}		
		    }
		    // if not, throw exception
		    else {
		    	throw new Exception();
		    }		
		}
		
		// catch all errors during gwap communication
		catch (Exception e) {
			//e.printStackTrace();
			System.out.println("Error during uComp API communication, please try again later!");
			details = null;
		} 
		
		// close the HTTP URL connection in any case
		finally {
			if(connection != null) {
		        connection.disconnect();
			}
		}
		
		// return detailed results
		return details;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// METHOD CANCEL JOB																						  //
	// this method cancels a GWAP validation job, therefore it gets the job ID, communicates it to the			  //
	// GWAP via JSON, gets a response from it about the success and returns true or false depending on the answer //
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public boolean cancelJob(long id) {
		boolean success = false;	
		String apikey = "";
		
		System.out.println("[CANCEL JOB]");
		
		// get API-Key
		apikey = getAPISettings().substring(0, getAPISettings().indexOf(","));
		
		if (apikey == null) {
			return false;
		}
		
		// initialise variables
		parser	   = new JSONParser();
		connection = null;
		serverurl  = server + "pause.php";
		json = "{\"key\":\"" + apikey + "\", \"job_id\":" + id + "}";
				
		System.out.println("Communicating with server: " + serverurl);
		
		// global try/catch/finally block
		try {
	    	
			// connect to the webserver
			url = new URL(serverurl);
	    	connection = (HttpURLConnection) url.openConnection();
	    	// POST-method and JSON
	    	connection.setRequestMethod("POST");
	    	connection.setDoInput(true);
	    	connection.setDoOutput(true);
	    	
	    	MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.STRICT);
			multipartEntity.addPart("json", new StringBody(json));
			connection.setRequestProperty("Content-Type", multipartEntity.getContentType().getValue());
			    	
		   	// send cancel metadata
			System.out.println("Sending data to server: " + json);
			out = connection.getOutputStream();
			try {
			    multipartEntity.writeTo(out);
			} 				
			finally {
			    out.close();
			}
			
			// get results from the server
			is = connection.getInputStream();
		    rd = new BufferedReader(new InputStreamReader(is));
		    response = new StringBuffer();
			try {
			    while((line = rd.readLine()) != null) {
			      response.append(line);
			    }
			}
			finally {
				rd.close();
			}
			// debug information
			System.out.println("Response from server: " + response.toString());
			
			// parse JSON data
			result = (JSONObject) parser.parse(response.toString());			
			
			// if job was successfully canceled
			if (result.get("success").equals("true")) {
				// debug information
				System.out.println("Job successfully canceled!");

				success = true;
			}
			// if job was not successfully canceled
			else {
				System.out.println("Job was not successfully canceled!");
				success = false;
			}
		}
		
		// catch all errors during gwap communication
		catch (Exception e) {
			System.out.println("Error during uComp API communication, please try again later!");
			success = false;
		} 
		
		// close the HTTP URL connection in any case
		finally {
			if(connection != null) {
		        connection.disconnect();
			}
		}		
		
		// return if job was canceled successfully
		return success;	
	}
		
	//////////////////////////////////////////////////////////////////////////////////
	// METHOD GET API SETTINGS														//
	// this method reads the API-Settings from a textfile named ucomp_api_key.txt, 	//
	// which has to be located in the .Protege folder in the user's home directory  //
	//////////////////////////////////////////////////////////////////////////////////
	public String getAPISettings() {
		
		String apisettings = "";
		
		// get API-Settings from textfile in home directory
		try {
			File key = new File(System.getProperty("user.home") + File.separator + ".Protege" + File.separator + "ucomp_api_settings.txt");
			in = new FileReader(key.getAbsolutePath());
			rd = new BufferedReader(in);			
			
			apisettings = rd.readLine();		
			rd.close();		
		}
		// catch all exceptions
		catch (Exception e) {			
			return null;
		} 
		// close the filereader in any case
		finally {		
			try {
				rd.close();
			} 
			catch (Exception e) {
			}			
		}			
		
		return apisettings;
	}
}
