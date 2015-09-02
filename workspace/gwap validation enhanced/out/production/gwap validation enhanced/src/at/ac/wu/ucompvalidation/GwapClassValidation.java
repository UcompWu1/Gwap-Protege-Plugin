package at.ac.wu.ucompvalidation;

import java.io.File;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JFileChooser;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.ui.view.cls.AbstractOWLClassViewComponent;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AddOntologyAnnotation;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.model.RemoveOntologyAnnotation;
import org.semanticweb.owlapi.util.OWLEntityRemover;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import at.ac.wu.ucompvalidation.GuiBuilder;

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// CLASS GWAPCLASSVALIDATION														 				 	  		//
// Java class for gwapCommunication validation for OWL classes is inherited from default class for OWL Class View Components //
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
@SuppressWarnings("serial")
public class GwapClassValidation extends AbstractOWLClassViewComponent {
	
	// GUI Elements
	private Box conceptBox, domainBox, additionalInfoBox, evaluationRadioButtonBox, checkBoxBox;
	private Box buttonBox, statusInfoScrollBox, removeBox;
	private JLabel conceptLabel, domainLabel, infoLabel;
	private JTextField conceptTextField, domainTextField, infoTextField;
	private JCheckBox subtreeCheckBox, cancelCheckBox, goldUnitsCheckBox;
	private JRadioButton crowdflowerRadioBox, ucompQuizRadioBox;
	private JButton calculateCostsButton, goButton, cancelButton, removeButton;
	private JScrollPane statusInfoScrollPanel;
	private JEditorPane statusInfoPanel, fileStatusText;
	private String selectedGoldUnitsFileName;
	// OWL Classes
	private OWLObjectHierarchyProvider<OWLClass> ontologyHierarchy;
	private OWLOntologyManager ontologyManager;
	private OWLOntology activeOntology;
	private OWLDataFactory owlDataFactory;
	private OWLAnnotation comment, relevance;
	private OWLAxiom ax;
	private OWLClass selectedClass;
	private OWLAnnotationProperty label;
	private OWLEntityRemover remover;
	
	// Gwap Communication
	private GwapCommunication gwapCommunication;
	private ArrayList<OWLClass> owlclasses;
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// METHOD INITIALISE CLASS VIEW																					 //	
	// abstract method from superclass must be implemented, it is called when view is initialised for the first time //
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void initialiseClassView() throws Exception {
		
		// initialise OWL Classes
		ontologyHierarchy	  	= getOWLModelManager().getOWLHierarchyManager().getOWLClassHierarchyProvider();
		ontologyManager   	= getOWLModelManager().getOWLOntologyManager();
		activeOntology   	= getOWLModelManager().getActiveOntology();
		owlDataFactory    	= getOWLModelManager().getOWLDataFactory();
		remover = new OWLEntityRemover(ontologyManager, Collections.singleton(activeOntology));
		// define annotation type as RDFS comment
		label 	= owlDataFactory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_COMMENT.getIRI());	
		// initialise gwapCommunication Communication Class
		gwapCommunication 	= new GwapCommunication();
		
		// BUILD THE GUI
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setToolTipText("<html>This component validates the <b>domain relevance of concepts</b> with respect to a domain.<br>" +
					   "You can validate a single concept, all subconcepts of a concept, or even all concepts in the ontology " +
					   "(by validating \"Thing\" and its subtree).</html>");
		
		// CONCEPT-BOX
        
		conceptBox = Box.createHorizontalBox();        
        conceptLabel = new JLabel("Concept to be validated:");
		conceptLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
	    // always set the number of columns for text fields to prevent them from resizing
	    conceptTextField = new JTextField("", 10);
	    conceptTextField.setEditable(false);
		
	    // preferred size of text field gets manipulated: double the width of the label
	    // only possible with setColumns(x), one column equals 8px (plus the minimum size of 6px)
	    conceptTextField.setColumns((int) (((conceptLabel.getPreferredSize().width * 2) - 6) / 8));

	    // set proportion for the maximum size of the elements
	    conceptLabel.setMaximumSize(new Dimension(1000, conceptLabel.getMaximumSize().height));
	    conceptTextField.setMaximumSize(new Dimension(2000, conceptTextField.getMaximumSize().height));
	    
	    // build the box, 10px space at the beginning, between the two elements, and at the end
	    conceptBox.add(Box.createRigidArea(new Dimension(10, 0)));
	    conceptBox.add(conceptLabel);
	    conceptBox.add(Box.createRigidArea(new Dimension(10, 0))); 
	    conceptBox.add(conceptTextField);
        conceptBox.add(Box.createRigidArea(new Dimension(10, 0)));
	    
        // create the GUI Object 
		
		// DOMAIN-BOX
        domainBox = Box.createHorizontalBox();
	    domainLabel = new JLabel("Validate relevance for domain:");
	    domainLabel.setHorizontalAlignment(SwingConstants.CENTER);        
        
	    domainTextField = new JTextField("", 10);
	    domainTextField.setColumns((int) (((domainLabel.getPreferredSize().width * 2) - 6) / 8));
	    domainTextField.setToolTipText("Set the domain of knowlegde.");
	    
	    domainLabel.setMaximumSize(new Dimension(1000, domainLabel.getMaximumSize().height));
	    domainTextField.setMaximumSize(new Dimension(2000, domainTextField.getMaximumSize().height));
	    
	    domainBox.add(Box.createRigidArea(new Dimension(10, 0)));
	    domainBox.add(domainLabel);
	    domainBox.add(Box.createRigidArea(new Dimension(10, 0)));
	    domainBox.add(domainTextField);
	    domainBox.add(Box.createRigidArea(new Dimension(10, 0)));
	        
	    // INFO-BOX
        additionalInfoBox = Box.createHorizontalBox();
	    infoLabel = new JLabel("Additional information for validators:");
	    infoLabel.setHorizontalAlignment(SwingConstants.CENTER);   
        
	    infoTextField = new JTextField("", 10);
	    infoTextField.setColumns((int) (((infoLabel.getPreferredSize().width * 2) - 6) / 8));
	    infoTextField.setToolTipText("<html>You can use this field to give additional information about the task to the crowd workers.<br><br>" + 
	    					 "The following text will be sent to the uComp API as task description:<br>" +
	    					 "<u>Check word relevance for a domain</u><br>" +
	    					 "Please decide whether the given word (also known as concept) is relevant for the mentioned domain. " +
							 "Generally, <i>relevant</i> means that the word comes to ones mind when thinking about that domain.<br>" +
							 "Class Relevance Check:<br>" +
							 "Your task is to decide if a given concept (also called class) is relevant for a given domain.<br>" +
							 "Examples:<br>" +
							 "Is racket relevant to the domain of tennis? - Correct answer: Yes<br>" +
							 "Is game relevant to the domain of tennis? - Correct answer: Yes<br>" +							
							 "Is election relevant to the domain of politics? - Correct answer: Yes<br>" +
							 "Is racquet relevant to the domain of politics? - Correct answer: No<br>" +
							 "Is party system relevant to the domain of politics? - Correct answer: Yes<br>" +
							 "Is partysystem relevant to the domain of politics? - Correct answer: No, this is no English term!<br>" +
							 "Sometimes there is no answer that is clearly correct, because the concept may be slightly relevant, too generic, or too specific.<br>" +
							 "Examples:<br>" +
							 "Is human relevant to the domain of politics? - Unclear, probably: Yes, but very generic.<br>" +
							 "Is weather relevant to the domain of politics? - Unclear, probably: No, only slightly relevant.<br>" +
							 "Is event relevant to the domain of politics? - Unclear, but probably: Yes.<br>" +
							 "Please consult the Web or any external source for additional information you might need for completing this task" +
							 " (for example, checking the definition of climate related terms on Wikipedia).</html>");

	    infoLabel.setMaximumSize(new Dimension(1000, infoLabel.getMaximumSize().height));
	    infoTextField.setMaximumSize(new Dimension(2000, infoTextField.getMaximumSize().height));	    
	    
	    additionalInfoBox.add(Box.createRigidArea(new Dimension(10, 0)));
	    additionalInfoBox.add(infoLabel);
	    additionalInfoBox.add(Box.createRigidArea(new Dimension(10, 0)));
	    additionalInfoBox.add(infoTextField);
	    additionalInfoBox.add(Box.createRigidArea(new Dimension(10, 0)));
	     
	    // CROWDFLOWER-BOX
	    evaluationRadioButtonBox = Box.createHorizontalBox();
	    evaluationRadioButtonBox.setAlignmentX(CENTER_ALIGNMENT);	    	    
	    
	    crowdflowerRadioBox = new JRadioButton("Send to CrowdFlower");
	    crowdflowerRadioBox.setEnabled(false);
	    crowdflowerRadioBox.setSelected(true);
	    crowdflowerRadioBox.setToolTipText("Select this radio button to send the task to CrowdFlower.");
	    // Action Listener is added to radio button
	    crowdflowerRadioBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (crowdflowerRadioBox.isSelected()) {
					calculateCostsButton.setEnabled(true);
				}
			}    	
	    });
	    
	    ucompQuizRadioBox = new JRadioButton("Send to uComp-Quiz");
	    ucompQuizRadioBox.setEnabled(false);
	    ucompQuizRadioBox.setToolTipText("Select this radio button to send the task to the uComp-Quiz.");
	    // Action Listener is added to radio button
	    ucompQuizRadioBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (ucompQuizRadioBox.isSelected()) {
					calculateCostsButton.setEnabled(false);
				}
			}    	
	    });
	    
	    // buttongroup for grouping the radio buttons together
	    ButtonGroup bg = new ButtonGroup();
	    bg.add(crowdflowerRadioBox);
	    bg.add(ucompQuizRadioBox);
	    
	    evaluationRadioButtonBox.add(crowdflowerRadioBox);
	    evaluationRadioButtonBox.add(Box.createRigidArea(new Dimension(30, 0)));
	    evaluationRadioButtonBox.add(ucompQuizRadioBox);    
	    
	    // GO-BOX
	    checkBoxBox = Box.createHorizontalBox();
	    
	    subtreeCheckBox = new JCheckBox("Validate subtree");
	    subtreeCheckBox.setEnabled(false);
	    subtreeCheckBox.setToolTipText("Validate the selected concept and all its sub-concepts!");
	    
	    
	    goldUnitsCheckBox = new JCheckBox("send Job with Gold Units");
	    goldUnitsCheckBox.setEnabled(true);
	    goldUnitsCheckBox.setToolTipText("If this option is selected Gold Questions will be sent from a predefined csv file"); 
	    
	    goldUnitsCheckBox.addActionListener(new ActionListener() {
	    
	    	public void actionPerformed(ActionEvent e){
	    		// showing the file selector
	    		if (goldUnitsCheckBox.isSelected()){
	    			fileStatusText.setVisible(true);
		    		
		    		JFileChooser chooser = new JFileChooser();
		    		//FileNameExtensionFilter filter = new FileNameExtensionFilter(
		    		//        "CSV files", "csv"); 
		    		
		    		chooser.setMultiSelectionEnabled(true);
		    		//chooser.setFileFilter(filter);
		    	     int option = chooser.showOpenDialog(GwapClassValidation.this);
		    	     if (option == JFileChooser.APPROVE_OPTION) {
		    	       File[] sf = chooser.getSelectedFiles();
		    	       String filelist = "nothing";
		    	       if (sf.length > 0) filelist = sf[0].getAbsolutePath();
		    	       for (int i = 1; i < sf.length; i++) {
		    	    	   System.out.println("adding path to filelist: " +  sf[i].getPath());
		    	    	   filelist += ", " + sf[i].getPath();
		    	       }
		    	       System.out.println("You chose " + filelist);
		    	       fileStatusText.setText("Chosen file:  " +filelist);
		    	       selectedGoldUnitsFileName = filelist;
		    	       
		    	       validate();
			    	   revalidate(); 
		    	     }
		    	     else {
		    	       System.out.println("You canceled.");
		    	       fileStatusText.setText("selection cancelled");
		    	       fileStatusText.setVisible(false);
		    	       goldUnitsCheckBox.setSelected(false);
		    	       validate();
			    	   revalidate(); 
		    	     }
	    		}
	    		// hiding the file selector
	    		else {
	    			fileStatusText.setVisible(false);
	    		}
	    	}
	    });
	    
	    
	    calculateCostsButton = new JButton("CALCULATE COSTS INTELLIJ VERSION 2");
	    calculateCostsButton.setEnabled(false);
	    calculateCostsButton.setToolTipText("Press to calculate the expected costs of a job with the current settings, only possible when sending tasks to CrowdFlower.");
	    // Action Listener is added to button
	    calculateCostsButton.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {    		
	    		int judgements, centspaid;
	    		
	    		// only if there is a selection
	    		if (selectedClass != null) {    			
	    			
	    			// initialise arraylist for all owl-classes that are to be validated
	    			owlclasses 	= new ArrayList<OWLClass>();	    					
	    			// if the validate subtree checkbox is selected, start the validation process also for every subclass
		    		if (subtreeCheckBox.isSelected() == true) {	    				    			
		    			startSubtree(selectedClass);
		    		}
		    		else {
		    			// if there isn't already a validation for this class in progress
		    			if (GwapValidation.isValidating(selectedClass, activeOntology, label, 1) == false) {
		    				owlclasses.add(selectedClass);
		    			}
		    		}
		    		
		    		// get judgements and centspaid setting from API-Settings textfile and calculate and display total costs
		    		try {	    			
		    			judgements = Integer.valueOf(gwapCommunication.getAPISettings().substring(gwapCommunication.getAPISettings().indexOf(",") + 1, gwapCommunication.getAPISettings().lastIndexOf(",")));
		    			centspaid = Integer.valueOf(gwapCommunication.getAPISettings().substring(gwapCommunication.getAPISettings().lastIndexOf(",") + 1));
		    			double dollar = ((double) (owlclasses.size() * judgements * centspaid)) / 100.0;
		    			statusInfoPanel.setText("This job contains <b>" + owlclasses.size() + " units</b>.<br>You have set the number of judgements to <b>" + judgements + 
		    							"</b> and the amount of money paid per task to <b>" + centspaid + " cents</b> per task.<br>" +
		    							"Expected total costs of this job are <b>$" + new DecimalFormat("###,##0.00").format(dollar) + "</b>.");		
		    		}
		    		catch (Exception ex) {
		    			// Write status messages
		    			System.out.println("Can't read API-Settings. Make sure there is a textfile named \"ucomp_api_settings.txt\" in your home directory " +
		    					   "in the \".Protege\" folder containing nothing except your API-Key, the number of judgements and the crowdflower payment in cents per task" +
		    					   "in one line in the following format:\n<ucomp_api_key>,<number_of_judgements_per_unit>,<payment_in_cents>\n" +
		    					   "Example file content: abcdefghijklmnopqrst,5,2\nPlease refer to our documentation for further information!");
		    			statusInfoPanel.setText("Can't read <b>API-Settings</b>. Make sure there is a textfile named \"ucomp_api_settings.txt\" in your home directory " +
								   "in the \".Protege\" folder containing nothing except your <b>API-Key</b>, the <b>number of judgements</b> and the crowdflower <b>payment in cents</b> per task " +
								   "in one line in the following format:<br>&lt;ucomp_api_key&gt;,&lt;number_of_judgements_per_unit&gt;,&lt;payment_in_cents&gt;<br>" +
								   "Example file content: abcdefghijklmnopqrst,5,2<br>Please refer to our documentation for further information!");
		    		}
		    		
		    		// Set status scrollpane visible and refresh the GUI (without validate() JEditorPane is not refreshed properly)
		    		statusInfoScrollPanel.setVisible(true);
		    		validate();
		    		revalidate(); 	    		
	    		}	 
	    	}
	    });
	    
	    
	        
	    goButton = new JButton("GO");
	    
	    // Expand the preferred width of the button (depending on the preferred size of the GO button)
	    calculateCostsButton.setMaximumSize(new Dimension(goButton.getPreferredSize().width * 5, goButton.getPreferredSize().height));
	       
	    goButton.setEnabled(false);
	    goButton.setToolTipText("Send to the crowdsourcing API.");
	    // Action Listener is added to button
	    goButton.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		ArrayList<String> classes;
	    		boolean crowdflower;
	    		long jobid;
	    		int taskid;
	    		
	    		// only if there is a selection
	    		if (selectedClass != null) {
	    				    				    	
	    			// only if there was provided a domain
	    			if (!domainTextField.getText().equals("")) {
	    			
		    			// initialise arraylist for all owl-classes that are to be validated
		    			owlclasses 	= new ArrayList<OWLClass>();
		    			classes 	= new ArrayList<String>();
		    					
		    			// if the validate subtree checkbox is selected, start the validation process also for every subclass
			    		if (subtreeCheckBox.isSelected() == true) {	    				    			
			    			startSubtree(selectedClass);
			    		}
			    		else {
			    			// if there isn't already a validation for this class in progress
			    			if (GwapValidation.isValidating(selectedClass, activeOntology, label, 1) == false) {
			    				if (!getOWLModelManager().getRendering(selectedClass).equals("Thing")) {
			    					owlclasses.add(selectedClass);
			    				}		    					
			    			}
			    		}	    				    		
		    	    	
			    		// if there are classes to be validated
			    		if (owlclasses.size() != 0) {			    			
			    		
				    		// parse array-list into normal strings for gwapcommunication
				    		for (OWLClass selectedClass : owlclasses) {
				    			classes.add(getOWLModelManager().getRendering(selectedClass));
				    		}
				    		
				    		// send task to crowdflower or ucomp-quiz?
			    			if (crowdflowerRadioBox.isSelected()) {
			    				crowdflower = true;
			    			}
			    			else {
			    				crowdflower = false;
			    			}
			    			
			    			// crowdflower has a minimum of 5 units
			    			if (!(crowdflower == true && owlclasses.size() < 5)) {
			    				
			    				// create job
				    			jobid = gwapCommunication.createJob(1, crowdflower, domainTextField.getText(), classes, null, null, infoTextField.getText(), selectedGoldUnitsFileName);
				    			System.out.println("Gold Units were requested file: "+ selectedGoldUnitsFileName);
				    			// if job creation was successful
				        		if (jobid != -1 && jobid != -2) {
				        			
				        			// Write class validation information to class comment for every class
				        			taskid = 1;
				        			for (OWLClass selectedClass : owlclasses) {
					    				// Syntax: "#gwapclass:domain=xyz;info=abc;crowdflower=true/false;subtree=true/false;jobid=123;taskid=1;"
					    				comment = owlDataFactory.getOWLAnnotation(owlDataFactory.getRDFSComment(), owlDataFactory.getOWLLiteral("#gwapclass:domain=" + domainTextField.getText() + 
					    							";info=" + infoTextField.getText() + ";crowdflower=" + crowdflower + 
					    							";subtree=" + subtreeCheckBox.isSelected() + ";jobid=" + jobid + ";taskid=" + taskid + ";"));
					    				ax = owlDataFactory.getOWLAnnotationAssertionAxiom(selectedClass.getIRI(), comment);
					    				ontologyManager.applyChange(new AddAxiom(activeOntology, ax));
					    				taskid++;
				        			}	
				        						        						    	    		
				    	    		// Map domain to ontology via comment in ontology head (remove old domain annotation first)
				    				for (OWLAnnotation anno : activeOntology.getAnnotations()) {
				    					// if the comment starts with "#gwapclass:", it is relevant for the validation
				    					if (anno.getValue().toString().substring(1, 12).equals("#gwapclass:")) {
				    						ontologyManager.applyChange(new RemoveOntologyAnnotation(activeOntology, anno));
				    					}
				    				}
				    				
				    				// Syntax: "#gwapclass:domain=xyz;crowdflower=true/false;"
				    	    		comment = owlDataFactory.getOWLAnnotation(owlDataFactory.getRDFSComment(), owlDataFactory.getOWLLiteral("#gwapclass:domain=" + domainTextField.getText() +
				    	    									  ";crowdflower=" + crowdflower + ";"));
				    	    		ontologyManager.applyChange(new AddOntologyAnnotation(activeOntology, comment));
				    	    			    	    		
				    	    		// if the root class Thing was selected there is a different status message and different class validation information
				    				if (getOWLModelManager().getRendering(selectedClass).equals("Thing")) {	
				    					// Write status messages
			    						statusInfoPanel.setText("Validation for subtree <b>in progress</b>...");
			    						
			    						// Write class validation information to class comment
			    						// Syntax: "#gwapclass:domain=xyz;info=abc;crowdflower=true/false;subtree=true/false;" => NO JOB-ID!
					    				comment = owlDataFactory.getOWLAnnotation(owlDataFactory.getRDFSComment(), owlDataFactory.getOWLLiteral("#gwapclass:domain=" + domainTextField.getText() + 
					    							";info=" + infoTextField.getText() + ";crowdflower=" + crowdflower + ";subtree=" + subtreeCheckBox.isSelected() + ";"));
					    				ax = owlDataFactory.getOWLAnnotationAssertionAxiom(selectedClass.getIRI(), comment);
					    				ontologyManager.applyChange(new AddAxiom(activeOntology, ax));	    						
				    				}
				    	    		
				    				else {
				    					String message = "";
				    					message = "Validating concept <b>\"" + conceptTextField.getText() + "\"</b> against domain <b>\"" + domainTextField.getText() + "\"</b>";
				    					if (!infoTextField.getText().equals("")) {
				    						message += " with additional info <b>\"" + infoTextField.getText() + "\"</b><br>";
				    					}
				    					else {
				    						message += "<br>";
				    					}
				    					message += "<b>Waiting for results</b> from uComp API...please check again later...<br>";
				    					
				    					// Write status messages
					    	    		statusInfoPanel.setText(message);
				    				}
				    				
				    				// Set status scrollpane visible and refresh the GUI (without validate() JEditorPane is not refreshed properly)
				    				statusInfoScrollPanel.setVisible(true);
				        			validate();
				        			revalidate();
				        			
				        			// set editable/enabled for textfields, checkboxes and buttons
				        			// this has to be done after revalidating with visible status scroll pane, otherwise it is not displayed properly
				    				domainTextField.setEditable(false);
				    	    		infoTextField.setEditable(false);
				    	    		crowdflowerRadioBox.setEnabled(false);
				    	    		ucompQuizRadioBox.setEnabled(false);
				    	    		subtreeCheckBox.setEnabled(false);
				    	    		cancelCheckBox.setEnabled(true);
				    	    		calculateCostsButton.setEnabled(false);
				    	    		goButton.setEnabled(false);
				    	    		cancelButton.setEnabled(true);
				        		}	        	
				    		
				    			else {
				    				// Write status messages
				    				if (jobid == -2) {
				    					statusInfoPanel.setText("Can't read <b>API-Settings</b>. Make sure there is a textfile named \"ucomp_api_settings.txt\" in your home directory " +
												   "in the \".Protege\" folder containing nothing except your <b>API-Key</b>, the <b>number of judgements</b> and the crowdflower <b>payment in cents</b> per task " +
												   "in one line in the following format:<br>&lt;ucomp_api_key&gt;,&lt;number_of_judgements_per_unit&gt;,&lt;payment_in_cents&gt;<br>" +
												   "Example file content: abcdefghijklmnopqrst,5,2<br>Please refer to our documentation for further information!");
				    				}
				    				else {
				    					statusInfoPanel.setText("Error - Validation <b>couldn't get started</b>...please try again later...");	 
				    				}
				    				// Set status scrollpane visible and refresh the GUI
				    				statusInfoScrollPanel.setVisible(true);
				        			validate();
				        			revalidate();
				    			}
			    			}
			    			
			    			else {
			    				// Write status messages
		    					statusInfoPanel.setText("There is a <b>minimum of 5 units</b> for jobs being sent to crowdflower. Please make another selection to start a validation!");			    		
		    					// Set status scrollpane visible and refresh the GUI
		    					statusInfoScrollPanel.setVisible(true);
			        			validate();
			        			revalidate();
			    			}				    						    		
	    				}
			    		
			    		else {
			    			// Write status messages
	    					statusInfoPanel.setText("<b>No classes found</b> in current selection. Please select another class to start a validation!");			    		
	    					// Set status scrollpane visible and refresh the GUI
	    					statusInfoScrollPanel.setVisible(true);
		        			validate();
		        			revalidate();
			    		}
	    			}
	    			
	    			else {
	    				// Write status messages
	    				statusInfoPanel.setText("Please <b>provide a domain</b> and start the validation again!");
	    				// Set status scrollpane visible and refresh the GUI
	    				statusInfoScrollPanel.setVisible(true);
	        			validate();
	        			revalidate();
	    			}	    			    			
	    		}
	    	}
	    });
	    
	    // Expand the preferred width of the button
	    goButton.setMaximumSize(new Dimension(goButton.getPreferredSize().width * 3, goButton.getPreferredSize().height));
	    
	    cancelCheckBox = new JCheckBox("Cancel subtree");
	    cancelCheckBox.setEnabled(false);
	    
	    cancelButton = new JButton("CANCEL");
	    cancelButton.setEnabled(false);
	    // Action Listener is added to button
	    cancelButton.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    
	    		// only if there is a selection
	    		if (selectedClass != null) {
	    			// if the validation could be canceled successfully
	    			// this method also checks if there is already a validation for this class in progress
	    			if (cancelValidation(selectedClass) == true) {	
	    				// set editable/enabled for textfields, checkboxes and buttons
	    	    		domainTextField.setEditable(true);
	    	    		infoTextField.setEditable(true);
	    	    		crowdflowerRadioBox.setEnabled(true);
	    	    		ucompQuizRadioBox.setEnabled(true);
	    	    		subtreeCheckBox.setEnabled(true);
	    	    		cancelCheckBox.setEnabled(false);
	    	    		if (crowdflowerRadioBox.isSelected()) {
	    	    			calculateCostsButton.setEnabled(true);
	    	    		}
	    	    		goButton.setEnabled(true);
	    	    		cancelButton.setEnabled(false);
	    	    		
	    	    		// Reset status box and set status scrollpane invisible
	    	    		statusInfoPanel.setText("");
	    	    		statusInfoScrollPanel.setVisible(false);
	    			}
		    		
	    			else {
	    				// Write status messages
	    				statusInfoPanel.setText("Error - Validation <b>couldn't get canceled</b>...please try again later...");	    					
	    			}
	    			
	    			// Refresh the GUI
    	    		revalidate();

		    		// if the cancel subtree checkbox is selected, cancel the validation process also for every subclass
		    		if (cancelCheckBox.isSelected() == true) {
	    	    		// Reset cancel subtree checkbox
	    	    		cancelCheckBox.setSelected(false);
		    			
		    			cancelSubtree(selectedClass);
		    		}
	    		}
	    	}
	    });		

	    // Expand the preferred width of the button (set it to the same size as GO button)
	    cancelButton.setMaximumSize(new Dimension(goButton.getPreferredSize().width * 3, goButton.getPreferredSize().height));
	    
	    checkBoxBox.add(subtreeCheckBox);
	    checkBoxBox.add(Box.createRigidArea(new Dimension(10, 0)));
	    checkBoxBox.add(goldUnitsCheckBox);

	    fileStatusText = new JEditorPane();
	    fileStatusText.setEditable(false);
	    fileStatusText.setContentType("text/html");
	    fileStatusText.setText("no file selected yet");
	    fileStatusText.setVisible(false);
	    
	    Box fileSelectorBox = Box.createHorizontalBox();
	    fileSelectorBox.add(buttonBox.createRigidArea(new Dimension(10, 0)));
	    fileSelectorBox.add(fileStatusText);
	    
	    
	    buttonBox = Box.createHorizontalBox();
	    buttonBox.add(Box.createRigidArea(new Dimension(10, 0)));
	    buttonBox.add(calculateCostsButton);
	    
	    
	    
	    buttonBox.add(Box.createRigidArea(new Dimension(10, 0)));
	      
	    buttonBox.add(Box.createRigidArea(new Dimension(10, 0)));
	    buttonBox.add(goButton);
	    buttonBox.add(Box.createRigidArea(new Dimension(30, 0)));
	    buttonBox.add(cancelCheckBox);
	    buttonBox.add(Box.createRigidArea(new Dimension(10, 0)));
	    buttonBox.add(cancelButton);
	    
	    // STATUS-BOX
	    statusInfoScrollBox = Box.createHorizontalBox();
	    statusInfoPanel = new JEditorPane();
	    statusInfoPanel.setEditable(false);
	    statusInfoPanel.setContentType("text/html");
        statusInfoScrollPanel = new JScrollPane(statusInfoPanel);
        statusInfoScrollPanel.setVisible(false);
        
        statusInfoScrollBox.add(Box.createRigidArea(new Dimension(10, 0)));
        statusInfoScrollBox.add(statusInfoScrollPanel);
        statusInfoScrollBox.add(Box.createRigidArea(new Dimension(10, 0)));
	    
        // REMOVE-BOX
        removeBox = Box.createHorizontalBox();
	    
	    removeButton = new JButton("REMOVE CONCEPT");
	    removeButton.setVisible(false);
	    // Action Listener is added to button
	    removeButton.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {    		
	    		
	    		// add remover to current class
	    		selectedClass.accept(remover);
	    		// apply the changes, in fact remove the current class
	    		ontologyManager.applyChanges(remover.getChanges());
	    		remover.reset();
	    		
	    		// update GUI
	    		cancelCheckBox.setEnabled(false);    		
	    		cancelButton.setEnabled(false);
	    		removeButton.setEnabled(false);
	    		statusInfoPanel.setText("Concept <b>removed</b>! Please select another concept...");
	    		revalidate();
	    	}
    	});
        
	    // Expand the preferred width of the button
	    removeButton.setMaximumSize(new Dimension(removeButton.getPreferredSize().width * 2, removeButton.getPreferredSize().height));
	    removeBox.add(removeButton);
	    
        // MASTER-BOX
        // between every box there is a filler between 5px and 50px; between box and border the spacing is set to 10px
	    add(Box.createRigidArea(new Dimension(0, 10)));
	    add(conceptBox);
	    add(new Box.Filler(new Dimension(0, 5), new Dimension(0, 25), new Dimension(0, 50)));
	    add(domainBox);	    
	    add(new Box.Filler(new Dimension(0, 5), new Dimension(0, 25), new Dimension(0, 50)));	
	    add(additionalInfoBox);
	    add(new Box.Filler(new Dimension(0, 5), new Dimension(0, 25), new Dimension(0, 50)));
	    add(evaluationRadioButtonBox);
	    add(new Box.Filler(new Dimension(0, 5), new Dimension(0, 25), new Dimension(0, 50)));
	    add(checkBoxBox);
	    
	    add(new Box.Filler(new Dimension(0, 5), new Dimension(0, 25), new Dimension(0, 50)));
	    add(fileSelectorBox);
	    
	    add(new Box.Filler(new Dimension(0, 5), new Dimension(0, 25), new Dimension(0, 50)));
	    add(buttonBox);
	    
	    add(new Box.Filler(new Dimension(0, 5), new Dimension(0, 25), new Dimension(0, 50)));
	    add(statusInfoScrollBox);
	    add(new Box.Filler(new Dimension(0, 5), new Dimension(0, 25), new Dimension(0, 50)));
	    add(removeBox);
	    add(Box.createRigidArea(new Dimension(0, 10)));
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// METHOD START SUBTREE																						  //
	// this method starts the validation for the current class and calls itself recursively for all child classes //
	// in this way, it walks through the whole subtree and starts the validation for every class in the tree	  //
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private void startSubtree(OWLClass selectedClass) {		
		// add current owl-class to validation-arraylist if there isn't already a validation for it in progress
		if (GwapValidation.isValidating(selectedClass, activeOntology, label, 1) == false) {	
			// if the current owl-class doesn't exist in the array-list yet
			if (!owlclasses.contains(selectedClass) && !getOWLModelManager().getRendering(selectedClass).equals("Thing")) {
				owlclasses.add(selectedClass);
			}
		}
		// call method recursively for every child class
		for (OWLClass sub : ontologyHierarchy.getChildren(selectedClass)) {
			startSubtree(sub);
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// METHOD CANCEL SUBTREE																					   //
	// this method cancels the validation for the current class and calls itself recursively for all child classes //
	// in this way, it walks through the whole subtree and cancels the validation for every class in the tree	   //
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private void cancelSubtree(OWLClass selectedClass) {
		// start validation for current class
		cancelValidation(selectedClass);
		// call method recursively for every child class
		for (OWLClass sub : ontologyHierarchy.getChildren(selectedClass)) {
			cancelSubtree(sub);
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// METHOD CANCEL VALIDATION																						 //
	// this method checks if there is a validation in progress for the selected class								 //
	// if there is so, it cancels the validation process (control information via comment + communication with gwapCommunication) //
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private boolean cancelValidation (OWLClass selectedClass) {	
		boolean success = false;
		
		// if there is a validation for this class in progress
		if (GwapValidation.isValidating(selectedClass, activeOntology, label, 1) == true) {			
			
			// if job cancelation was successful or the whole subtree is cancelled for the root class Thing
    		if (gwapCommunication.cancelJob(GwapValidation.getJobID(selectedClass, activeOntology, label, 1)) == true || 
					(getOWLModelManager().getRendering(selectedClass).equals("Thing") && cancelCheckBox.isSelected() == true)) {   			
    			// Delete class validation information from class comment
    			for (OWLAnnotation anno : selectedClass.getAnnotations(activeOntology)) {
    				// if the comment starts with "#gwapclass:", it is relevant for the validation
    				if (anno.getValue().toString().substring(1, 12).equals("#gwapclass:") || 
    						anno.getProperty().getIRI().toString().equals(
    						activeOntology.getOntologyID().getOntologyIRI().toString() + "#uComp_class_relevance")) {
    					ax = owlDataFactory.getOWLAnnotationAssertionAxiom(selectedClass.getIRI(), anno);
    					ontologyManager.applyChange(new RemoveAxiom(activeOntology, ax));
    				}
    			}
    			success = true;
    		}
    		else {
    			success = false;
    		}
		}
		// return if job cancelation was successful
		return success;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	// METHOD UPDATE VIEW																				 //
	// abstract method from superclass must be implemented, it is called everytime the view gets updated //
	// => that is in fact everytime the user selects another part of the ontology						 //
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	protected OWLClass updateView(OWLClass currentlySelectedClass) {			    
		// the current selected class is needed also in other methods => reference as class attribute
		selectedClass = currentlySelectedClass;
		int[] results;
		int index;
		
		// only if there is a selection
		if (selectedClass != null) {
			// update the textfields and checkbox
			conceptTextField.setText(getOWLModelManager().getRendering(selectedClass));
			domainTextField.setText(GwapValidation.getDomain(selectedClass, activeOntology, label, 1));
			infoTextField.setText(GwapValidation.getInfo(selectedClass, activeOntology, label, 1));
			crowdflowerRadioBox.setSelected(GwapValidation.getCrowdflower(selectedClass, activeOntology, label, 1));
			ucompQuizRadioBox.setSelected(!GwapValidation.getCrowdflower(selectedClass, activeOntology, label, 1));
			if (ucompQuizRadioBox.isSelected()) {
    			calculateCostsButton.setEnabled(false);
    		}
			subtreeCheckBox.setSelected(GwapValidation.getSubtree(selectedClass, activeOntology, label, 1));		
			
			// if the root class Thing was selected
			if (getOWLModelManager().getRendering(selectedClass).equals("Thing")) {
				// if there is a validation in progress
				if (GwapValidation.isValidating(selectedClass, activeOntology, label, 1) == true) {
					// set editable/enabled for textfields, checkboxes and buttons
					domainTextField.setEditable(false);
					infoTextField.setEditable(false);
					crowdflowerRadioBox.setEnabled(false);
					ucompQuizRadioBox.setEnabled(false);
					subtreeCheckBox.setEnabled(false);
					cancelCheckBox.setEnabled(true);
					calculateCostsButton.setEnabled(false);
					goButton.setEnabled(false);
					cancelButton.setEnabled(true);
					// write status messages
					statusInfoPanel.setText("Validation for subtree <b>in progress</b>...");					
				}
				
				else {
					// set editable/enabled for textfields, checkboxes and buttons
					domainTextField.setEditable(true);
					infoTextField.setEditable(true);
					crowdflowerRadioBox.setEnabled(true);
					ucompQuizRadioBox.setEnabled(true);
					subtreeCheckBox.setEnabled(true);
					cancelCheckBox.setEnabled(false);
					if (crowdflowerRadioBox.isSelected()) {
    	    			calculateCostsButton.setEnabled(true);
    	    		}
					goButton.setEnabled(true);
					cancelButton.setEnabled(false);
					// write status messages
					statusInfoPanel.setText("The class \"Thing\" <b>can't be validated</b> itself.<br>Please select \"Validate subtree\" "  + 
							"and press \"GO\" if you want to validate all subclasses of \"Thing\".");
				}
							
				// set remove button non-visible
				removeButton.setVisible(false);
				
				// set status scrollpane visible and refresh the GUI
				statusInfoScrollPanel.setVisible(true);
				revalidate();
				
				return selectedClass;
			}		
			
			// if there is already a validation in progress
			if (GwapValidation.isValidating(selectedClass, activeOntology, label, 1) == true) {
	    		// set editable/enabled for textfields, checkboxes and buttons
				domainTextField.setEditable(false);
	    		infoTextField.setEditable(false);
	    		crowdflowerRadioBox.setEnabled(false);
	    		ucompQuizRadioBox.setEnabled(false);
	    		subtreeCheckBox.setEnabled(false);
	    		cancelCheckBox.setEnabled(true);
	    		calculateCostsButton.setEnabled(false);
	    		goButton.setEnabled(false);
	    		cancelButton.setEnabled(true);
	    		// set remove button non-visible
	    		removeButton.setVisible(false);
	    		
	    		// prepare status messages
	    		String message = "";
				message = "Validating concept <b>\"" + conceptTextField.getText() + "\"</b> against domain <b>\"" + domainTextField.getText() + "\"</b>";
				if (!infoTextField.getText().equals("")) {
					message += " with additional info <b>\"" + infoTextField.getText() + "\"</b><br>";
				}
				else {
					message += "<br>";
				}
	    		    		
	    		// check if there are already results available from the gwapCommunication
	    		results = gwapCommunication.getResults(GwapValidation.getJobID(selectedClass, activeOntology, label, 1));
	    		// set index for result array depending on the task-id
	    		index = (GwapValidation.getTaskID(selectedClass, activeOntology, label, 1) - 1) * 3;    		
	    		
	    		if (results != null) {
	    			// create IRI for uComp class relevance check
	    			IRI ucomp = IRI.create(activeOntology.getOntologyID().getOntologyIRI() + "#uComp_class_relevance");	    			
	    			
	    			// if the validation was positive
	    			if (results[index] > results[index + 1]) {
	    				// write detailed results
		    			message += "Results from uComp API: <b><u>Yes: " + results[index] + "</u>, No: " + results[index + 1] +
		    					", I don't know: " + results[index + 2] + "</b><br>";
	    				message += "uComp Validation <b><font color=\"green\">POSITIVE</font></b>! The concept \"" + conceptTextField.getText() +
	    						"\" <b>is relevant</b> for the domain \"" + domainTextField.getText() + "\"!";
	    				
	    				// set uComp_class_relevance
	    	    		relevance = owlDataFactory.getOWLAnnotation(owlDataFactory.getOWLAnnotationProperty(ucomp), owlDataFactory.getOWLLiteral(true));
	    	    		ax = owlDataFactory.getOWLAnnotationAssertionAxiom(selectedClass.getIRI(), relevance);
	    	    		ontologyManager.applyChange(new AddAxiom(activeOntology, ax));
	    			}    			
	    			
	    			else {
	    				// if the validation was negative
	    				if (results[index] < results[index + 1]) {
	    					// write detailed results
	    	    			message += "Results from uComp API: <b>Yes: " + results[index] + ", <u>No: " + results[index + 1] +
	    	    					"</u>, I don't know: " + results[index + 2] + "</b><br>";
	    					message += "uComp Validation <b><font color=\"red\">NEGATIVE</font></b>! The concept \"" + conceptTextField.getText() +
		    						"\" <b>is not relevant</b> for the domain \"" + domainTextField.getText() + "\"!";   					
	    					
	    					// set uComp_class_relevance
	    					relevance = owlDataFactory.getOWLAnnotation(owlDataFactory.getOWLAnnotationProperty(ucomp), owlDataFactory.getOWLLiteral(false));
		    	    		ax = owlDataFactory.getOWLAnnotationAssertionAxiom(selectedClass.getIRI(), relevance);
		    	    		ontologyManager.applyChange(new AddAxiom(activeOntology, ax));
	    					
	    					// set remove button enabled and visible
	    					removeButton.setEnabled(true);
	    					removeButton.setVisible(true);
	    				}
	    				// if the validation was undetermined
	    				else {
	    					// write detailed results
	    	    			message += "Results from uComp API: <b>Yes: " + results[index] + ", No: " + results[index + 1] +
	    	    					", <u>I don't know: " + results[index + 2] + "</u></b><br>";
	    					message += "uComp Validation <b>UNDETERMINED</b>! The concept \"" + conceptTextField.getText() +
		    						"\" <b>might or might not be relevant</b> for the domain \"" + domainTextField.getText() + "\"!";
	    				}
	    			}
	    		}
	    		// if no results are available yet
	    		else {
	    			message += "<b>Waiting for results</b> from uComp API...please check again later..." + "<br>";
	    		}
	    		
	    		// Write status messages and set status scrollpane visible and refresh the GUI
	    		statusInfoPanel.setText(message);
	    		statusInfoScrollPanel.setVisible(true);
	    		revalidate();
			}
			// if there is no validation in progress
			else {
				// set editable/enabled for textfields, checkboxes and buttons
	    		domainTextField.setEditable(true);
	    		infoTextField.setEditable(true);
	    		crowdflowerRadioBox.setEnabled(true);
	    		ucompQuizRadioBox.setEnabled(true);
	    		subtreeCheckBox.setEnabled(true);
	    		cancelCheckBox.setEnabled(false);
	    		if (crowdflowerRadioBox.isSelected()) {
	    			calculateCostsButton.setEnabled(true);
	    		}
	    		goButton.setEnabled(true);
	    		cancelButton.setEnabled(false);
	    		
	    		// reset the status box, make the status box and the remove button non-visible and refresh the GUI
	    		statusInfoPanel.setText("");
	    		statusInfoScrollPanel.setVisible(false);
	    		removeButton.setVisible(false);
	    		revalidate();
			}
	    }
	    
	    return selectedClass;
	}
	
	/////////////////////////////////////////////////////////
	// METHOD DISPOSE VIEW								   //
	// abstract method from superclass must be implemented //
	/////////////////////////////////////////////////////////
	public void disposeView() {		
	}
}
