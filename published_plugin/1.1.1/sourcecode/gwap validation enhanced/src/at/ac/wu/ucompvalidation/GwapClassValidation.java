package at.ac.wu.ucompvalidation;

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

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// CLASS GWAPCLASSVALIDATION														 				 	  		//
// Java class for gwap validation for OWL classes is inherited from default class for OWL Class View Components //
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
@SuppressWarnings("serial")
public class GwapClassValidation extends AbstractOWLClassViewComponent {
	
	// GUI Elements
	private Box cBox, dBox, iBox, crBox, gBox, sBox, rBox;
	private JLabel lConcept, lDomain, lInfo;
	private JTextField fConcept, fDomain, fInfo;
	private JCheckBox cSubtree, cCancel;
	private JRadioButton rCrowdflower, rQuiz;
	private JButton bCosts, bGo, bCancel, bRemove;
	private JScrollPane sStatus;
	private JEditorPane aStatus;
	
	// OWL Classes
	private OWLObjectHierarchyProvider<OWLClass> hp;
	private OWLOntologyManager man;
	private OWLOntology ont;
	private OWLDataFactory df;
	private OWLAnnotation comment, relevance;
	private OWLAxiom ax;
	private OWLClass cls;
	private OWLAnnotationProperty label;
	private OWLEntityRemover remover;
	
	// Gwap Communication
	private GwapCommunication gwap;
	private ArrayList<OWLClass> owlclasses;
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// METHOD INITIALISE CLASS VIEW																					 //	
	// abstract method from superclass must be implemented, it is called when view is initialised for the first time //
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void initialiseClassView() throws Exception {
		
		// initialise OWL Classes
		hp	  	= getOWLModelManager().getOWLHierarchyManager().getOWLClassHierarchyProvider();
		man   	= getOWLModelManager().getOWLOntologyManager();
		ont   	= getOWLModelManager().getActiveOntology();
		df    	= getOWLModelManager().getOWLDataFactory();
		remover = new OWLEntityRemover(man, Collections.singleton(ont));
		// define annotation type as RDFS comment
		label 	= df.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_COMMENT.getIRI());	
		// initialise Gwap Communication Class
		gwap 	= new GwapCommunication();
		
		// BUILD THE GUI
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setToolTipText("<html>This component validates the <b>domain relevance of concepts</b> with respect to a domain.<br>" +
					   "You can validate a single concept, all subconcepts of a concept, or even all concepts in the ontology " +
					   "(by validating \"Thing\" and its subtree).</html>");
		
		// CONCEPT-BOX
        cBox = Box.createHorizontalBox();        
        lConcept = new JLabel("Concept to be validated:");
		lConcept.setHorizontalAlignment(SwingConstants.CENTER);
        
	    // always set the number of columns for text fields to prevent them from resizing
	    fConcept = new JTextField("", 10);
	    fConcept.setEditable(false);
		
	    // preferred size of text field gets manipulated: double the width of the label
	    // only possible with setColumns(x), one column equals 8px (plus the minimum size of 6px)
	    fConcept.setColumns((int) (((lConcept.getPreferredSize().width * 2) - 6) / 8));

	    // set proportion for the maximum size of the elements
	    lConcept.setMaximumSize(new Dimension(1000, lConcept.getMaximumSize().height));
	    fConcept.setMaximumSize(new Dimension(2000, fConcept.getMaximumSize().height));
	    
	    // build the box, 10px space at the beginning, between the two elements, and at the end
	    cBox.add(Box.createRigidArea(new Dimension(10, 0)));
	    cBox.add(lConcept);
	    cBox.add(Box.createRigidArea(new Dimension(10, 0))); 
	    cBox.add(fConcept);
        cBox.add(Box.createRigidArea(new Dimension(10, 0)));
	    
        // DOMAIN-BOX
        dBox = Box.createHorizontalBox();
	    lDomain = new JLabel("Validate relevance for domain:");
	    lDomain.setHorizontalAlignment(SwingConstants.CENTER);        
        
	    fDomain = new JTextField("", 10);
	    fDomain.setColumns((int) (((lDomain.getPreferredSize().width * 2) - 6) / 8));
	    fDomain.setToolTipText("Set the domain of knowlegde.");
	    
	    lDomain.setMaximumSize(new Dimension(1000, lDomain.getMaximumSize().height));
	    fDomain.setMaximumSize(new Dimension(2000, fDomain.getMaximumSize().height));
	    
	    dBox.add(Box.createRigidArea(new Dimension(10, 0)));
	    dBox.add(lDomain);
	    dBox.add(Box.createRigidArea(new Dimension(10, 0)));
	    dBox.add(fDomain);
	    dBox.add(Box.createRigidArea(new Dimension(10, 0)));
	        
	    // INFO-BOX
        iBox = Box.createHorizontalBox();
	    lInfo = new JLabel("Additional information for validators:");
	    lInfo.setHorizontalAlignment(SwingConstants.CENTER);   
        
	    fInfo = new JTextField("", 10);
	    fInfo.setColumns((int) (((lInfo.getPreferredSize().width * 2) - 6) / 8));
	    fInfo.setToolTipText("<html>You can use this field to give additional information about the task to the crowd workers.<br><br>" + 
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

	    lInfo.setMaximumSize(new Dimension(1000, lInfo.getMaximumSize().height));
	    fInfo.setMaximumSize(new Dimension(2000, fInfo.getMaximumSize().height));	    
	    
	    iBox.add(Box.createRigidArea(new Dimension(10, 0)));
	    iBox.add(lInfo);
	    iBox.add(Box.createRigidArea(new Dimension(10, 0)));
	    iBox.add(fInfo);
	    iBox.add(Box.createRigidArea(new Dimension(10, 0)));
	     
	    // CROWDFLOWER-BOX
	    crBox = Box.createHorizontalBox();
	    crBox.setAlignmentX(CENTER_ALIGNMENT);	    	    
	    
	    rCrowdflower = new JRadioButton("Send to CrowdFlower");
	    rCrowdflower.setEnabled(false);
	    rCrowdflower.setSelected(true);
	    rCrowdflower.setToolTipText("Select this radio button to send the task to CrowdFlower.");
	    // Action Listener is added to radio button
	    rCrowdflower.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (rCrowdflower.isSelected()) {
					bCosts.setEnabled(true);
				}
			}    	
	    });
	    
	    rQuiz = new JRadioButton("Send to uComp-Quiz");
	    rQuiz.setEnabled(false);
	    rQuiz.setToolTipText("Select this radio button to send the task to the uComp-Quiz.");
	    // Action Listener is added to radio button
	    rQuiz.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (rQuiz.isSelected()) {
					bCosts.setEnabled(false);
				}
			}    	
	    });
	    
	    // buttongroup for grouping the radio buttons together
	    ButtonGroup bg = new ButtonGroup();
	    bg.add(rCrowdflower);
	    bg.add(rQuiz);
	    
	    crBox.add(rCrowdflower);
	    crBox.add(Box.createRigidArea(new Dimension(30, 0)));
	    crBox.add(rQuiz);    
	    
	    // GO-BOX
	    gBox = Box.createHorizontalBox();
	    
	    cSubtree = new JCheckBox("Validate subtree");
	    cSubtree.setEnabled(false);
	    cSubtree.setToolTipText("Validate the selected concept and all its sub-concepts!");
	    
	    bCosts = new JButton("CALCULATE COSTS");
	    bCosts.setEnabled(false);
	    bCosts.setToolTipText("Press to calculate the expected costs of a job with the current settings, only possible when sending tasks to CrowdFlower.");
	    // Action Listener is added to button
	    bCosts.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {    		
	    		int judgements, centspaid;
	    		
	    		// only if there is a selection
	    		if (cls != null) {    			
	    			
	    			// initialise arraylist for all owl-classes that are to be validated
	    			owlclasses 	= new ArrayList<OWLClass>();	    					
	    			// if the validate subtree checkbox is selected, start the validation process also for every subclass
		    		if (cSubtree.isSelected() == true) {	    				    			
		    			startSubtree(cls);
		    		}
		    		else {
		    			// if there isn't already a validation for this class in progress
		    			if (GwapValidation.isValidating(cls, ont, label, 1) == false) {
		    				owlclasses.add(cls);
		    			}
		    		}
		    		
		    		// get judgements and centspaid setting from API-Settings textfile and calculate and display total costs
		    		try {	    			
		    			judgements = Integer.valueOf(gwap.getAPISettings().substring(gwap.getAPISettings().indexOf(",") + 1, gwap.getAPISettings().lastIndexOf(",")));
		    			centspaid = Integer.valueOf(gwap.getAPISettings().substring(gwap.getAPISettings().lastIndexOf(",") + 1));
		    			double dollar = ((double) (owlclasses.size() * judgements * centspaid)) / 100.0;
		    			aStatus.setText("This job contains <b>" + owlclasses.size() + " units</b>.<br>You have set the number of judgements to <b>" + judgements + 
		    							"</b> and the amount of money paid per task to <b>" + centspaid + " cents</b> per task.<br>" +
		    							"Expected total costs of this job are <b>$" + new DecimalFormat("###,##0.00").format(dollar) + "</b>.");		
		    		}
		    		catch (Exception ex) {
		    			// Write status messages
		    			System.out.println("Can't read API-Settings. Make sure there is a textfile named \"ucomp_api_settings.txt\" in your home directory " +
		    					   "in the \".Protege\" folder containing nothing except your API-Key, the number of judgements and the crowdflower payment in cents per task" +
		    					   "in one line in the following format:\n<ucomp_api_key>,<number_of_judgements_per_unit>,<payment_in_cents>\n" +
		    					   "Example file content: abcdefghijklmnopqrst,5,2\nPlease refer to our documentation for further information!");
		    			aStatus.setText("Can't read <b>API-Settings</b>. Make sure there is a textfile named \"ucomp_api_settings.txt\" in your home directory " +
								   "in the \".Protege\" folder containing nothing except your <b>API-Key</b>, the <b>number of judgements</b> and the crowdflower <b>payment in cents</b> per task " +
								   "in one line in the following format:<br>&lt;ucomp_api_key&gt;,&lt;number_of_judgements_per_unit&gt;,&lt;payment_in_cents&gt;<br>" +
								   "Example file content: abcdefghijklmnopqrst,5,2<br>Please refer to our documentation for further information!");
		    		}
		    		
		    		// Set status scrollpane visible and refresh the GUI (without validate() JEditorPane is not refreshed properly)
		    		sStatus.setVisible(true);
		    		validate();
		    		revalidate(); 	    		
	    		}	 
	    	}
	    });
	    
	    bGo = new JButton("GO");
	    
	    // Expand the preferred width of the button (depending on the preferred size of the GO button)
	    bCosts.setMaximumSize(new Dimension(bGo.getPreferredSize().width * 5, bGo.getPreferredSize().height));
	       
	    bGo.setEnabled(false);
	    bGo.setToolTipText("Send to the crowdsourcing API.");
	    // Action Listener is added to button
	    bGo.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		ArrayList<String> classes;
	    		boolean crowdflower;
	    		long jobid;
	    		int taskid;
	    		
	    		// only if there is a selection
	    		if (cls != null) {
	    				    				    	
	    			// only if there was provided a domain
	    			if (!fDomain.getText().equals("")) {
	    			
		    			// initialise arraylist for all owl-classes that are to be validated
		    			owlclasses 	= new ArrayList<OWLClass>();
		    			classes 	= new ArrayList<String>();
		    					
		    			// if the validate subtree checkbox is selected, start the validation process also for every subclass
			    		if (cSubtree.isSelected() == true) {	    				    			
			    			startSubtree(cls);
			    		}
			    		else {
			    			// if there isn't already a validation for this class in progress
			    			if (GwapValidation.isValidating(cls, ont, label, 1) == false) {
			    				if (!getOWLModelManager().getRendering(cls).equals("Thing")) {
			    					owlclasses.add(cls);
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
			    			if (rCrowdflower.isSelected()) {
			    				crowdflower = true;
			    			}
			    			else {
			    				crowdflower = false;
			    			}
			    			
			    			// crowdflower has a minimum of 5 units
			    			if (!(crowdflower == true && owlclasses.size() < 5)) {
			    				
			    				// create job
				    			jobid = gwap.createJob(1, crowdflower, fDomain.getText(), classes, null, null, fInfo.getText());
				    			
				    			// if job creation was successful
				        		if (jobid != -1 && jobid != -2) {
				        			
				        			// Write class validation information to class comment for every class
				        			taskid = 1;
				        			for (OWLClass selectedClass : owlclasses) {
					    				// Syntax: "#gwapclass:domain=xyz;info=abc;crowdflower=true/false;subtree=true/false;jobid=123;taskid=1;"
					    				comment = df.getOWLAnnotation(df.getRDFSComment(), df.getOWLLiteral("#gwapclass:domain=" + fDomain.getText() + 
					    							";info=" + fInfo.getText() + ";crowdflower=" + crowdflower + 
					    							";subtree=" + cSubtree.isSelected() + ";jobid=" + jobid + ";taskid=" + taskid + ";"));
					    				ax = df.getOWLAnnotationAssertionAxiom(selectedClass.getIRI(), comment);
					    				man.applyChange(new AddAxiom(ont, ax));
					    				taskid++;
				        			}	
				        						        						    	    		
				    	    		// Map domain to ontology via comment in ontology head (remove old domain annotation first)
				    				for (OWLAnnotation anno : ont.getAnnotations()) {
				    					// if the comment starts with "#gwapclass:", it is relevant for the validation
				    					if (anno.getValue().toString().substring(1, 12).equals("#gwapclass:")) {
				    						man.applyChange(new RemoveOntologyAnnotation(ont, anno));
				    					}
				    				}
				    				
				    				// Syntax: "#gwapclass:domain=xyz;crowdflower=true/false;"
				    	    		comment = df.getOWLAnnotation(df.getRDFSComment(), df.getOWLLiteral("#gwapclass:domain=" + fDomain.getText() +
				    	    									  ";crowdflower=" + crowdflower + ";"));
				    	    		man.applyChange(new AddOntologyAnnotation(ont, comment));
				    	    			    	    		
				    	    		// if the root class Thing was selected there is a different status message and different class validation information
				    				if (getOWLModelManager().getRendering(cls).equals("Thing")) {	
				    					// Write status messages
			    						aStatus.setText("Validation for subtree <b>in progress</b>...");
			    						
			    						// Write class validation information to class comment
			    						// Syntax: "#gwapclass:domain=xyz;info=abc;crowdflower=true/false;subtree=true/false;" => NO JOB-ID!
					    				comment = df.getOWLAnnotation(df.getRDFSComment(), df.getOWLLiteral("#gwapclass:domain=" + fDomain.getText() + 
					    							";info=" + fInfo.getText() + ";crowdflower=" + crowdflower + ";subtree=" + cSubtree.isSelected() + ";"));
					    				ax = df.getOWLAnnotationAssertionAxiom(cls.getIRI(), comment);
					    				man.applyChange(new AddAxiom(ont, ax));	    						
				    				}
				    	    		
				    				else {
				    					String message = "";
				    					message = "Validating concept <b>\"" + fConcept.getText() + "\"</b> against domain <b>\"" + fDomain.getText() + "\"</b>";
				    					if (!fInfo.getText().equals("")) {
				    						message += " with additional info <b>\"" + fInfo.getText() + "\"</b><br>";
				    					}
				    					else {
				    						message += "<br>";
				    					}
				    					message += "<b>Waiting for results</b> from uComp API...please check again later...<br>";
				    					
				    					// Write status messages
					    	    		aStatus.setText(message);
				    				}
				    				
				    				// Set status scrollpane visible and refresh the GUI (without validate() JEditorPane is not refreshed properly)
				    				sStatus.setVisible(true);
				        			validate();
				        			revalidate();
				        			
				        			// set editable/enabled for textfields, checkboxes and buttons
				        			// this has to be done after revalidating with visible status scroll pane, otherwise it is not displayed properly
				    				fDomain.setEditable(false);
				    	    		fInfo.setEditable(false);
				    	    		rCrowdflower.setEnabled(false);
				    	    		rQuiz.setEnabled(false);
				    	    		cSubtree.setEnabled(false);
				    	    		cCancel.setEnabled(true);
				    	    		bCosts.setEnabled(false);
				    	    		bGo.setEnabled(false);
				    	    		bCancel.setEnabled(true);
				        		}	        	
				    		
				    			else {
				    				// Write status messages
				    				if (jobid == -2) {
				    					aStatus.setText("Can't read <b>API-Settings</b>. Make sure there is a textfile named \"ucomp_api_settings.txt\" in your home directory " +
												   "in the \".Protege\" folder containing nothing except your <b>API-Key</b>, the <b>number of judgements</b> and the crowdflower <b>payment in cents</b> per task " +
												   "in one line in the following format:<br>&lt;ucomp_api_key&gt;,&lt;number_of_judgements_per_unit&gt;,&lt;payment_in_cents&gt;<br>" +
												   "Example file content: abcdefghijklmnopqrst,5,2<br>Please refer to our documentation for further information!");
				    				}
				    				else {
				    					aStatus.setText("Error - Validation <b>couldn't get started</b>...please try again later...");	 
				    				}
				    				// Set status scrollpane visible and refresh the GUI
				    				sStatus.setVisible(true);
				        			validate();
				        			revalidate();
				    			}
			    			}
			    			
			    			else {
			    				// Write status messages
		    					aStatus.setText("There is a <b>minimum of 5 units</b> for jobs being sent to crowdflower. Please make another selection to start a validation!");			    		
		    					// Set status scrollpane visible and refresh the GUI
		    					sStatus.setVisible(true);
			        			validate();
			        			revalidate();
			    			}				    						    		
	    				}
			    		
			    		else {
			    			// Write status messages
	    					aStatus.setText("<b>No classes found</b> in current selection. Please select another class to start a validation!");			    		
	    					// Set status scrollpane visible and refresh the GUI
	    					sStatus.setVisible(true);
		        			validate();
		        			revalidate();
			    		}
	    			}
	    			
	    			else {
	    				// Write status messages
	    				aStatus.setText("Please <b>provide a domain</b> and start the validation again!");
	    				// Set status scrollpane visible and refresh the GUI
	    				sStatus.setVisible(true);
	        			validate();
	        			revalidate();
	    			}	    			    			
	    		}
	    	}
	    });
	    
	    // Expand the preferred width of the button
	    bGo.setMaximumSize(new Dimension(bGo.getPreferredSize().width * 3, bGo.getPreferredSize().height));
	    
	    cCancel = new JCheckBox("Cancel subtree");
	    cCancel.setEnabled(false);
	    
	    bCancel = new JButton("CANCEL");
	    bCancel.setEnabled(false);
	    // Action Listener is added to button
	    bCancel.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    
	    		// only if there is a selection
	    		if (cls != null) {
	    			// if the validation could be canceled successfully
	    			// this method also checks if there is already a validation for this class in progress
	    			if (cancelValidation(cls) == true) {	
	    				// set editable/enabled for textfields, checkboxes and buttons
	    	    		fDomain.setEditable(true);
	    	    		fInfo.setEditable(true);
	    	    		rCrowdflower.setEnabled(true);
	    	    		rQuiz.setEnabled(true);
	    	    		cSubtree.setEnabled(true);
	    	    		cCancel.setEnabled(false);
	    	    		if (rCrowdflower.isSelected()) {
	    	    			bCosts.setEnabled(true);
	    	    		}
	    	    		bGo.setEnabled(true);
	    	    		bCancel.setEnabled(false);
	    	    		
	    	    		// Reset status box and set status scrollpane invisible
	    	    		aStatus.setText("");
	    	    		sStatus.setVisible(false);
	    			}
		    		
	    			else {
	    				// Write status messages
	    				aStatus.setText("Error - Validation <b>couldn't get canceled</b>...please try again later...");	    					
	    			}
	    			
	    			// Refresh the GUI
    	    		revalidate();

		    		// if the cancel subtree checkbox is selected, cancel the validation process also for every subclass
		    		if (cCancel.isSelected() == true) {
	    	    		// Reset cancel subtree checkbox
	    	    		cCancel.setSelected(false);
		    			
		    			cancelSubtree(cls);
		    		}
	    		}
	    	}
	    });		

	    // Expand the preferred width of the button (set it to the same size as GO button)
	    bCancel.setMaximumSize(new Dimension(bGo.getPreferredSize().width * 3, bGo.getPreferredSize().height));
	    
	    gBox.add(cSubtree);
	    gBox.add(Box.createRigidArea(new Dimension(10, 0)));
	    gBox.add(bCosts);
	    gBox.add(Box.createRigidArea(new Dimension(10, 0)));
	    gBox.add(bGo);
	    gBox.add(Box.createRigidArea(new Dimension(30, 0)));
	    gBox.add(cCancel);
	    gBox.add(Box.createRigidArea(new Dimension(10, 0)));
	    gBox.add(bCancel);
	    
	    // STATUS-BOX
	    sBox = Box.createHorizontalBox();
	    aStatus = new JEditorPane();
	    aStatus.setEditable(false);
	    aStatus.setContentType("text/html");
        sStatus = new JScrollPane(aStatus);
        sStatus.setVisible(false);
        
        sBox.add(Box.createRigidArea(new Dimension(10, 0)));
        sBox.add(sStatus);
        sBox.add(Box.createRigidArea(new Dimension(10, 0)));
	    
        // REMOVE-BOX
        rBox = Box.createHorizontalBox();
	    
	    bRemove = new JButton("REMOVE CONCEPT");
	    bRemove.setVisible(false);
	    // Action Listener is added to button
	    bRemove.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {    		
	    		
	    		// add remover to current class
	    		cls.accept(remover);
	    		// apply the changes, in fact remove the current class
	    		man.applyChanges(remover.getChanges());
	    		remover.reset();
	    		
	    		// update GUI
	    		cCancel.setEnabled(false);    		
	    		bCancel.setEnabled(false);
	    		bRemove.setEnabled(false);
	    		aStatus.setText("Concept <b>removed</b>! Please select another concept...");
	    		revalidate();
	    	}
    	});
        
	    // Expand the preferred width of the button
	    bRemove.setMaximumSize(new Dimension(bRemove.getPreferredSize().width * 2, bRemove.getPreferredSize().height));
	    rBox.add(bRemove);
	    
        // MASTER-BOX
        // between every box there is a filler between 5px and 50px; between box and border the spacing is set to 10px
	    add(Box.createRigidArea(new Dimension(0, 10)));
	    add(cBox);
	    add(new Box.Filler(new Dimension(0, 5), new Dimension(0, 25), new Dimension(0, 50)));
	    add(dBox);	    
	    add(new Box.Filler(new Dimension(0, 5), new Dimension(0, 25), new Dimension(0, 50)));	
	    add(iBox);
	    add(new Box.Filler(new Dimension(0, 5), new Dimension(0, 25), new Dimension(0, 50)));
	    add(crBox);
	    add(new Box.Filler(new Dimension(0, 5), new Dimension(0, 25), new Dimension(0, 50)));
	    add(gBox);
	    add(new Box.Filler(new Dimension(0, 5), new Dimension(0, 25), new Dimension(0, 50)));
	    add(sBox);
	    add(new Box.Filler(new Dimension(0, 5), new Dimension(0, 25), new Dimension(0, 50)));
	    add(rBox);
	    add(Box.createRigidArea(new Dimension(0, 10)));
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// METHOD START SUBTREE																						  //
	// this method starts the validation for the current class and calls itself recursively for all child classes //
	// in this way, it walks through the whole subtree and starts the validation for every class in the tree	  //
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private void startSubtree(OWLClass selectedClass) {		
		// add current owl-class to validation-arraylist if there isn't already a validation for it in progress
		if (GwapValidation.isValidating(selectedClass, ont, label, 1) == false) {	
			// if the current owl-class doesn't exist in the array-list yet
			if (!owlclasses.contains(selectedClass) && !getOWLModelManager().getRendering(selectedClass).equals("Thing")) {
				owlclasses.add(selectedClass);
			}
		}
		// call method recursively for every child class
		for (OWLClass sub : hp.getChildren(selectedClass)) {
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
		for (OWLClass sub : hp.getChildren(selectedClass)) {
			cancelSubtree(sub);
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// METHOD CANCEL VALIDATION																						 //
	// this method checks if there is a validation in progress for the selected class								 //
	// if there is so, it cancels the validation process (control information via comment + communication with GWAP) //
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private boolean cancelValidation (OWLClass selectedClass) {	
		boolean success = false;
		
		// if there is a validation for this class in progress
		if (GwapValidation.isValidating(selectedClass, ont, label, 1) == true) {			
			
			// if job cancelation was successful or the whole subtree is cancelled for the root class Thing
    		if (gwap.cancelJob(GwapValidation.getJobID(selectedClass, ont, label, 1)) == true || 
					(getOWLModelManager().getRendering(selectedClass).equals("Thing") && cCancel.isSelected() == true)) {   			
    			// Delete class validation information from class comment
    			for (OWLAnnotation anno : selectedClass.getAnnotations(ont)) {
    				// if the comment starts with "#gwapclass:", it is relevant for the validation
    				if (anno.getValue().toString().substring(1, 12).equals("#gwapclass:") || 
    						anno.getProperty().getIRI().toString().equals(
    						ont.getOntologyID().getOntologyIRI().toString() + "#uComp_class_relevance")) {
    					ax = df.getOWLAnnotationAssertionAxiom(selectedClass.getIRI(), anno);
    					man.applyChange(new RemoveAxiom(ont, ax));
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
	protected OWLClass updateView(OWLClass selectedClass) {			    
		// the current selected class is needed also in other methods => reference as class attribute
		cls = selectedClass;
		int[] results;
		int index;
		
		// only if there is a selection
		if (cls != null) {
			// update the textfields and checkbox
			fConcept.setText(getOWLModelManager().getRendering(cls));
			fDomain.setText(GwapValidation.getDomain(cls, ont, label, 1));
			fInfo.setText(GwapValidation.getInfo(cls, ont, label, 1));
			rCrowdflower.setSelected(GwapValidation.getCrowdflower(cls, ont, label, 1));
			rQuiz.setSelected(!GwapValidation.getCrowdflower(cls, ont, label, 1));
			if (rQuiz.isSelected()) {
    			bCosts.setEnabled(false);
    		}
			cSubtree.setSelected(GwapValidation.getSubtree(cls, ont, label, 1));		
			
			// if the root class Thing was selected
			if (getOWLModelManager().getRendering(cls).equals("Thing")) {
				// if there is a validation in progress
				if (GwapValidation.isValidating(cls, ont, label, 1) == true) {
					// set editable/enabled for textfields, checkboxes and buttons
					fDomain.setEditable(false);
					fInfo.setEditable(false);
					rCrowdflower.setEnabled(false);
					rQuiz.setEnabled(false);
					cSubtree.setEnabled(false);
					cCancel.setEnabled(true);
					bCosts.setEnabled(false);
					bGo.setEnabled(false);
					bCancel.setEnabled(true);
					// write status messages
					aStatus.setText("Validation for subtree <b>in progress</b>...");					
				}
				
				else {
					// set editable/enabled for textfields, checkboxes and buttons
					fDomain.setEditable(true);
					fInfo.setEditable(true);
					rCrowdflower.setEnabled(true);
					rQuiz.setEnabled(true);
					cSubtree.setEnabled(true);
					cCancel.setEnabled(false);
					if (rCrowdflower.isSelected()) {
    	    			bCosts.setEnabled(true);
    	    		}
					bGo.setEnabled(true);
					bCancel.setEnabled(false);
					// write status messages
					aStatus.setText("The class \"Thing\" <b>can't be validated</b> itself.<br>Please select \"Validate subtree\" "  + 
							"and press \"GO\" if you want to validate all subclasses of \"Thing\".");
				}
							
				// set remove button non-visible
				bRemove.setVisible(false);
				
				// set status scrollpane visible and refresh the GUI
				sStatus.setVisible(true);
				revalidate();
				
				return cls;
			}		
			
			// if there is already a validation in progress
			if (GwapValidation.isValidating(cls, ont, label, 1) == true) {
	    		// set editable/enabled for textfields, checkboxes and buttons
				fDomain.setEditable(false);
	    		fInfo.setEditable(false);
	    		rCrowdflower.setEnabled(false);
	    		rQuiz.setEnabled(false);
	    		cSubtree.setEnabled(false);
	    		cCancel.setEnabled(true);
	    		bCosts.setEnabled(false);
	    		bGo.setEnabled(false);
	    		bCancel.setEnabled(true);
	    		// set remove button non-visible
	    		bRemove.setVisible(false);
	    		
	    		// prepare status messages
	    		String message = "";
				message = "Validating concept <b>\"" + fConcept.getText() + "\"</b> against domain <b>\"" + fDomain.getText() + "\"</b>";
				if (!fInfo.getText().equals("")) {
					message += " with additional info <b>\"" + fInfo.getText() + "\"</b><br>";
				}
				else {
					message += "<br>";
				}
	    		    		
	    		// check if there are already results available from the GWAP
	    		results = gwap.getResults(GwapValidation.getJobID(cls, ont, label, 1));
	    		// set index for result array depending on the task-id
	    		index = (GwapValidation.getTaskID(cls, ont, label, 1) - 1) * 3;    		
	    		
	    		if (results != null) {
	    			// create IRI for uComp class relevance check
	    			IRI ucomp = IRI.create(ont.getOntologyID().getOntologyIRI() + "#uComp_class_relevance");	    			
	    			
	    			// if the validation was positive
	    			if (results[index] > results[index + 1]) {
	    				// write detailed results
		    			message += "Results from uComp API: <b><u>Yes: " + results[index] + "</u>, No: " + results[index + 1] +
		    					", I don't know: " + results[index + 2] + "</b><br>";
	    				message += "uComp Validation <b><font color=\"green\">POSITIVE</font></b>! The concept \"" + fConcept.getText() +
	    						"\" <b>is relevant</b> for the domain \"" + fDomain.getText() + "\"!";
	    				
	    				// set uComp_class_relevance
	    	    		relevance = df.getOWLAnnotation(df.getOWLAnnotationProperty(ucomp), df.getOWLLiteral(true));
	    	    		ax = df.getOWLAnnotationAssertionAxiom(selectedClass.getIRI(), relevance);
	    	    		man.applyChange(new AddAxiom(ont, ax));
	    			}    			
	    			
	    			else {
	    				// if the validation was negative
	    				if (results[index] < results[index + 1]) {
	    					// write detailed results
	    	    			message += "Results from uComp API: <b>Yes: " + results[index] + ", <u>No: " + results[index + 1] +
	    	    					"</u>, I don't know: " + results[index + 2] + "</b><br>";
	    					message += "uComp Validation <b><font color=\"red\">NEGATIVE</font></b>! The concept \"" + fConcept.getText() +
		    						"\" <b>is not relevant</b> for the domain \"" + fDomain.getText() + "\"!";   					
	    					
	    					// set uComp_class_relevance
	    					relevance = df.getOWLAnnotation(df.getOWLAnnotationProperty(ucomp), df.getOWLLiteral(false));
		    	    		ax = df.getOWLAnnotationAssertionAxiom(selectedClass.getIRI(), relevance);
		    	    		man.applyChange(new AddAxiom(ont, ax));
	    					
	    					// set remove button enabled and visible
	    					bRemove.setEnabled(true);
	    					bRemove.setVisible(true);
	    				}
	    				// if the validation was undetermined
	    				else {
	    					// write detailed results
	    	    			message += "Results from uComp API: <b>Yes: " + results[index] + ", No: " + results[index + 1] +
	    	    					", <u>I don't know: " + results[index + 2] + "</u></b><br>";
	    					message += "uComp Validation <b>UNDETERMINED</b>! The concept \"" + fConcept.getText() +
		    						"\" <b>might or might not be relevant</b> for the domain \"" + fDomain.getText() + "\"!";
	    				}
	    			}
	    		}
	    		// if no results are available yet
	    		else {
	    			message += "<b>Waiting for results</b> from uComp API...please check again later..." + "<br>";
	    		}
	    		
	    		// Write status messages and set status scrollpane visible and refresh the GUI
	    		aStatus.setText(message);
	    		sStatus.setVisible(true);
	    		revalidate();
			}
			// if there is no validation in progress
			else {
				// set editable/enabled for textfields, checkboxes and buttons
	    		fDomain.setEditable(true);
	    		fInfo.setEditable(true);
	    		rCrowdflower.setEnabled(true);
	    		rQuiz.setEnabled(true);
	    		cSubtree.setEnabled(true);
	    		cCancel.setEnabled(false);
	    		if (rCrowdflower.isSelected()) {
	    			bCosts.setEnabled(true);
	    		}
	    		bGo.setEnabled(true);
	    		bCancel.setEnabled(false);
	    		
	    		// reset the status box, make the status box and the remove button non-visible and refresh the GUI
	    		aStatus.setText("");
	    		sStatus.setVisible(false);
	    		bRemove.setVisible(false);
	    		revalidate();
			}
	    }
	    
	    return cls;
	}
	
	/////////////////////////////////////////////////////////
	// METHOD DISPOSE VIEW								   //
	// abstract method from superclass must be implemented //
	/////////////////////////////////////////////////////////
	public void disposeView() {		
	}
}
