package at.ac.wu.ucompvalidation;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
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
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.model.RemoveOntologyAnnotation;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//CLASS GWAPSUBCLASSVALIDATION														 				 	  														//
//Java class for gwap validation for the relationship between an OWL class and its superclass is inherited from default class for OWL Class View Components //
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
@SuppressWarnings("serial")
public class GwapSubclassValidation extends AbstractOWLClassViewComponent {

	// GUI Elements
	private Box dBox, cBox, scBox, iBox, crBox, gBox, sBox, rBox;
	private JLabel lDomain, lSubclass, lSuperclass, lInfo;
	private JTextField fDomain, fSubclass, fSuperclass, fInfo;
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
	
	// Gwap Communication
	private GwapCommunication gwap;
	
	// Superclasses to be removed
	private ArrayList<String> remove;
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
		// define annotation type as RDFS comment
		label 	= df.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_COMMENT.getIRI());	
		// initialise Gwap Communication Class
		gwap 	= new GwapCommunication();
		// initialise ArrayList for communication of superclasses which should be removed
		remove  = new ArrayList<String>();
		
		// BUILD THE GUI
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setToolTipText("<html>This component validates <b>subClassOf</b> relationships between concepts.<br>" +
					   "You can validate a single relation, all relations in the subtree, or even all subClassOf relations " +
					   "by selecting \"Thing\" and its subtree.</html>");
		
		// ONTOLOGY-DOMAIN-BOX
        dBox = Box.createHorizontalBox();
        lDomain = new JLabel("In the domain of:");
        lDomain.setHorizontalAlignment(SwingConstants.CENTER);        
        
        // always set the number of columns for text fields to prevent them from resizing
        fDomain = new JTextField("", 10);
        
        // preferred size of text field gets manipulated: double the width of the label
	    // only possible with setColumns(x), one column equals 8px (plus the minimum size of 6px)
        fDomain.setColumns((int) (((lDomain.getPreferredSize().width * 2) - 6) / 8));
        fDomain.setToolTipText("Set the domain of knowlegde.");
        
        // set proportion for the maximum size of the elements
        lDomain.setMaximumSize(new Dimension(1000, lDomain.getMaximumSize().height));
        fDomain.setMaximumSize(new Dimension(2000, fDomain.getMaximumSize().height));
	    
	    // build the box, 10px space at the beginning, between the two elements, and at the end
	    dBox.add(Box.createRigidArea(new Dimension(10, 0)));
	    dBox.add(lDomain);
	    dBox.add(Box.createRigidArea(new Dimension(10, 0)));
	    dBox.add(fDomain);
	    dBox.add(Box.createRigidArea(new Dimension(10, 0)));
		
		// SUBCLASS-BOX
        cBox = Box.createHorizontalBox();        
        lSubclass = new JLabel("Is class:");
		lSubclass.setHorizontalAlignment(SwingConstants.CENTER);        
        
	    fSubclass = new JTextField("", 10);
	    fSubclass.setEditable(false);
		
	    fSubclass.setColumns((int) (((lSubclass.getPreferredSize().width * 2) - 6) / 8));

	    lSubclass.setMaximumSize(new Dimension(1000, lSubclass.getMaximumSize().height));
	    fSubclass.setMaximumSize(new Dimension(2000, fSubclass.getMaximumSize().height));

	    cBox.add(Box.createRigidArea(new Dimension(10, 0)));
	    cBox.add(lSubclass);
	    cBox.add(Box.createRigidArea(new Dimension(10, 0))); 
	    cBox.add(fSubclass);
        cBox.add(Box.createRigidArea(new Dimension(10, 0)));
	    
        // SUPERCLASS-BOX
        scBox = Box.createHorizontalBox();
	    lSuperclass = new JLabel("a subclass of the superclass(es):");
	    lSuperclass.setHorizontalAlignment(SwingConstants.CENTER);        
        
	    fSuperclass = new JTextField("", 10);
	    fSuperclass.setEditable(false);
	    fSuperclass.setColumns((int) (((lSuperclass.getPreferredSize().width * 2) - 6) / 8));
	    
	    lSuperclass.setMaximumSize(new Dimension(1000, lSuperclass.getMaximumSize().height));
	    fSuperclass.setMaximumSize(new Dimension(2000, fSuperclass.getMaximumSize().height));
	    
	    scBox.add(Box.createRigidArea(new Dimension(10, 0)));
	    scBox.add(lSuperclass);
	    scBox.add(Box.createRigidArea(new Dimension(10, 0)));
	    scBox.add(fSuperclass);
	    scBox.add(Box.createRigidArea(new Dimension(10, 0)));
	        
	    // INFO-BOX
        iBox = Box.createHorizontalBox();
	    lInfo = new JLabel("Additional information for validators:");
	    lInfo.setHorizontalAlignment(SwingConstants.CENTER);       
        
	    fInfo = new JTextField("", 10);
	    fInfo.setColumns((int) (((lInfo.getPreferredSize().width * 2) - 6) / 8));
	    fInfo.setToolTipText("<html>You can use this field to give additional information about the task to the crowd workers.<br><br>" +
	    					 "The following text will be sent to the uComp API as task description:<br>" +
	    					 "<u>Verify that a term is more specific than another</u><br>" +
	    					 "Task: You task is to decide if a term A is more specific than a term B.<br>" +
							 "A term A (e.g., cat) is more specific than a term B (e.g., animal) if it can be said that <i>every A is a B</i>, " +
							 "but not the other way around. Indeed, every cat is an animal but not every animal is a cat.<br>" +
							 "If A (e.g., cat) is more specific than B (e.g., animal), we say that A is a subClass of B.<br>" +
							 "Attention! The order of the terms is important:<br>" +
							 "<i>cat - is a a subClass of - animal</i> is TRUE because every cat is also an animal (cats are types of animals)<br>" +
							 "<i>animal - is a a subClass of - cat</i> is FALSE because not every animal is a cat (animals are not types of cats)<br>" +
							 "Examples: Is human a subClass of mammal? Correct answer: Yes<br>" +
							 "Is human a subClass of car? Correct answer: No<br>" +
							 "Is politician a subClass of human? Correct answer: Yes<br>" +
							 "Is water a subClass of liquid? Correct answer: Yes<br>" +
							 "Is water a subClass of car? Correct answer: No<br>" +
							 "Please consult the Web or any external source for additional information you might need for completing this task " +
							 "(for example, checking the definition of climate related terms on Wikipedia).</html>");
	    
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
	    cSubtree.setToolTipText("Validate all subClassOf relations in the subtree below the concept.");
	    
	    bCosts = new JButton("CALCULATE COSTS");
	    bCosts.setEnabled(false);
	    bCosts.setToolTipText("Press to calculate the expected costs of a job with the current settings, only possible when sending tasks to CrowdFlower.");
	    // Action Listener is added to button
	    bCosts.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {    		
	    		int sccount = 0, judgements, centspaid;
	    		
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
		    			if (GwapValidation.isValidating(cls, ont, label, 2) == false) {
		    				owlclasses.add(cls);
		    			}
		    		}		    		
		    		// if there are classes with superclasses in the selection
		    		if (owlclasses.size() != 0) {	    						    		
			    		// find all superclasses for all subclasses										
						for (OWLClass oc : owlclasses) {					
				    		for (OWLClassExpression ce : oc.getSuperClasses(ont)) {
								// no anonymous superclasses (for exampe "a value b" or "x some y")
								if (!ce.isAnonymous()) {
									sccount++;
								}
							}
						}
		    		}
		    		
		    		// get judgements and centspaid setting from API-Settings textfile and calculate and display total costs
		    		try {
		    			judgements = Integer.valueOf(gwap.getAPISettings().substring(gwap.getAPISettings().indexOf(",") + 1, gwap.getAPISettings().lastIndexOf(",")));
		    			centspaid = Integer.valueOf(gwap.getAPISettings().substring(gwap.getAPISettings().lastIndexOf(",") + 1));
		    			double dollar = ((double) (sccount * judgements * centspaid)) / 100.0;
		    			aStatus.setText("This job contains <b>" + sccount + " units</b>.<br>You have set the number of judgements to <b>" + judgements + 
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
	    		ArrayList<ArrayList<String>> superclasses = new ArrayList<ArrayList<String>>();
	    		ArrayList<String> currentsuperclasses;
	    		boolean crowdflower;
	    		long jobid;
	    		int i, taskid;	    		
	    		
	    		// only if there is a selection
	    		if (cls != null) {
	    			// only if there was provided a domain
	    			if (!fDomain.getText().equals("")) {
	    			
		    			// initialise arraylist for all owl-classes that are to be validated
		    			owlclasses 	= new ArrayList<OWLClass>();
		    			classes 	= new ArrayList<String>();
		    			
		    			// if the validate subtree checkbox is selected, start the validation process also for every subclass
		    			// this method also checks if there isn't already a validation for the respective class in progress
			    		if (cSubtree.isSelected() == true) {	    				    			
			    			startSubtree(cls);
			    		}
			    		else {
			    			// if there isn't already a validation for this class in progress
			    			if (GwapValidation.isValidating(cls, ont, label, 2) == false) {
			    				// if there are superclasses
			    				if (!fSuperclass.getText().equals("")) {	
			    					owlclasses.add(cls);
			    				}
			    			}
			    		}
			    		
			    		// if there are classes with superclasses in the selection
			    		if (owlclasses.size() != 0) {
			    			
			    			// parse array-list into normal strings for gwapcommunication
				    		for (OWLClass selectedClass : owlclasses) {
				    			classes.add(getOWLModelManager().getRendering(selectedClass));
				    		}
				    		
				    		// find all superclasses for all subclasses										
							for (OWLClass oc : owlclasses) {
								currentsuperclasses = new ArrayList<String>();					
					    		for (OWLClassExpression ce : oc.getSuperClasses(ont)) {
									// no anonymous superclasses (for exampe "a value b" or "x some y")
									if (!ce.isAnonymous()) {
										currentsuperclasses.add(getOWLModelManager().getRendering(ce));
									}
								}
					    		superclasses.add(currentsuperclasses);
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
					    		jobid = gwap.createJob(2, crowdflower, fDomain.getText(), classes, superclasses, null, fInfo.getText());
		
					    		// if job creation was successful
					    		if (jobid != -1 && jobid != -2) {
				        			
				        			// Write class validation information to class comment for every class
				        			i = taskid = 1;
				        			for (OWLClass selectedClass : owlclasses) {	        					        				
				        				// Syntax: "#gwapsubclass:domain=xyz;info=abc;crowdflower=true/false;subtree=true/false;jobid=123;taskid=1;"
				        				comment = df.getOWLAnnotation(df.getRDFSComment(), df.getOWLLiteral("#gwapsubclass:domain=" + fDomain.getText() + 
			        							";info=" + fInfo.getText() + ";crowdflower=" + crowdflower + ";subtree=" + cSubtree.isSelected() + 
			        							";jobid=" + jobid + ";taskid=" + taskid + ";"));
					    				ax = df.getOWLAnnotationAssertionAxiom(selectedClass.getIRI(), comment);
					    				man.applyChange(new AddAxiom(ont, ax));
					    				// increment counter for taskid depending on the amount of superclasses to match the correct position in the result csv later
					    				taskid = taskid + superclasses.get(i - 1).size();
					    				i++;
				        			}			        					    			
				        							
				    	    		// Map domain to ontology via comment in ontology head (remove old domain annotation first)
				    				for (OWLAnnotation anno : ont.getAnnotations()) {
				    					// if the comment starts with "#gwapclass:", it is relevant for the validation
				    					if (anno.getValue().toString().substring(1, 12).equals("#gwapclass:")) {
				    						man.applyChange(new RemoveOntologyAnnotation(ont, anno));
				    					}
				    				}
				    				
				    				// Syntax: "#gwapclass:domain=xyz;"
				    	    		comment = df.getOWLAnnotation(df.getRDFSComment(), df.getOWLLiteral("#gwapclass:domain=" + fDomain.getText() + 
				    	    									  ";crowdflower=" + crowdflower + ";"));
				    	    		man.applyChange(new AddOntologyAnnotation(ont, comment));    	    		
				    	    		   	    		
				    	    		// if there are no superclasses there is a different status message and different class validation information
				    				if (fSuperclass.getText().equals("")) {	
				    					// Write status messages
				    					aStatus.setText("Validation for subtree <b>in progress</b>...");
			    						
			    						// Write class validation information to class comment
					    				// Syntax: "#gwapsubclass:info=abc;crowdflower=true/false;subtree=true/false;" => NO JOB-ID!
					    				comment = df.getOWLAnnotation(df.getRDFSComment(), df.getOWLLiteral("#gwapsubclass:domain=" + fDomain.getText() + 
					    							";info=" + fInfo.getText() + ";crowdflower=" + crowdflower + ";subtree=" + cSubtree.isSelected() + ";"));
					    				ax = df.getOWLAnnotationAssertionAxiom(cls.getIRI(), comment);
					    				man.applyChange(new AddAxiom(ont, ax));
				    				}
				    	    		
				    				else {
				    					String message = "";
				    					message = "Validating subclass <b>\"" + fSubclass.getText() + "\"</b> against superclass(es) <b>\"" + fSuperclass.getText() + 
				    							  "\"</b> in the domain of <b>\"" + fDomain.getText() + "\"</b>";
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
	    					aStatus.setText("<b>No classes with superclasses found</b> in current selection. Please select another class to start a validation!");			    		
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
	    	    		bRemove.setEnabled(false);
	    	    		
	    	    		// Reset status box and set status scrollpane and remove-button invisible
	    	    		aStatus.setText("");    	    		
	    	    		sStatus.setVisible(false);
	    	    		bRemove.setVisible(false);
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
	    bCancel.setMaximumSize(new Dimension(bGo.getMaximumSize().width, bGo.getMaximumSize().height));
	    
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
        sStatus = new JScrollPane(aStatus);
        sStatus.setVisible(false);
        
        sBox.add(Box.createRigidArea(new Dimension(10, 0)));
        sBox.add(sStatus);
        sBox.add(Box.createRigidArea(new Dimension(10, 0)));
	    
        // REMOVE-BOX
        rBox = Box.createHorizontalBox();
	    
	    bRemove = new JButton("REMOVE ALL NEGATIVE RELATIONS");
	    bRemove.setVisible(false);
	    // Action Listener is added to button
	    bRemove.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {    			    			    			    	
	    		
	    		// remove all negatively validated subclassof relations of the current class
	    		for (OWLClassExpression ce : cls.getSuperClasses(ont)) {
	    			// if remove list contains the current superclass
	    			if (remove.contains(getOWLModelManager().getRendering(ce))) {
	    				// get all subclassofaxioms of the current subclass
	    				for (OWLSubClassOfAxiom subax : ont.getSubClassAxiomsForSubClass(cls)) {
	    	    			// if the superclass of the axiom matches the superclass to be removed
	    	    			if (getOWLModelManager().getRendering(subax.getSuperClass()).equals(getOWLModelManager().getRendering(ce))) {
	    	    				man.applyChange(new RemoveAxiom(ont, subax));
	    	    			}
	    	    		}
	    			}	    			
	    		}
	    		
	    		// Write information if negatively validated superclasses have already been removed to class comment
				// Syntax: "#gwapsubclass:removed=true;"
				comment = df.getOWLAnnotation(df.getRDFSComment(), df.getOWLLiteral("#gwapsubclass:removed=true;"));
				ax = df.getOWLAnnotationAssertionAxiom(cls.getIRI(), comment);
				man.applyChange(new AddAxiom(ont, ax));	
	    		
	    		// update GUI
	    		cCancel.setEnabled(false);    		
	    		bCancel.setEnabled(true);
	    		bRemove.setEnabled(false);
	    		aStatus.setText("All negatively validated subclassof relations <b>removed</b>...");
	    		revalidate();
	    	}
    	});
        
	    // Expand the preferred width of the button
	    bRemove.setMaximumSize(new Dimension(bRemove.getPreferredSize().width * 2, bRemove.getPreferredSize().height));
	    rBox.add(bRemove);
	    
        // MASTER-BOX
        // between every box there is a filler between 5px and 50px; between box and border the spacing is set to 10px
	    add(Box.createRigidArea(new Dimension(0, 10)));
	    add(dBox);
	    add(new Box.Filler(new Dimension(0, 5), new Dimension(0, 25), new Dimension(0, 50)));
	    add(cBox);
	    add(new Box.Filler(new Dimension(0, 5), new Dimension(0, 25), new Dimension(0, 50)));
	    add(scBox);	    
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
		if (GwapValidation.isValidating(selectedClass, ont, label, 2) == false) {
			// if the current owl-class has at least one superclass an doesn't exist in the array-list yet
			if (!(selectedClass.getSuperClasses(ont).size() == 0 || owlclasses.contains(selectedClass))) {
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
		if (GwapValidation.isValidating(selectedClass, ont, label, 2) == true) {			
		
			// if job cancelation was successful or the whole subtree is cancelled for a class without superclasses 
    		if (gwap.cancelJob(GwapValidation.getJobID(selectedClass, ont, label, 2)) == true || 
    						(fSuperclass.getText().equals("") && cCancel.isSelected() == true)) {   			
    			// Delete class validation information from class comment
    			for (OWLAnnotation anno : selectedClass.getAnnotations(ont, label)) {
    				// if the comment starts with "#gwapsubclass:", it is relevant for the validation
    				if (anno.getValue().toString().substring(1, 15).equals("#gwapsubclass:")) {
    					ax = df.getOWLAnnotationAssertionAxiom(selectedClass.getIRI(), anno);
    					man.applyChange(new RemoveAxiom(ont, ax));
    				}
    			}
    			
    			// for every subclass axiom of the ontology, where the subclass is the current class
	    		for (OWLSubClassOfAxiom subax : ont.getSubClassAxiomsForSubClass(selectedClass)) {
    				// backup current annotations except of the result of the subclassof check
    				Set<OWLAnnotation> annotations = new HashSet<OWLAnnotation>();
    				for (OWLAnnotation anno : subax.getAnnotations()) {
    					if (!anno.getProperty().getIRI().toString().equals(
    							ont.getOntologyID().getOntologyIRI().toString() + "#uComp_subclassof_check")) {
    						annotations.add(anno);
    					}
    				}			    	    					  				
    				// remove the old axiom and add a new axiom with all annotations
    				ax = df.getOWLSubClassOfAxiom(selectedClass, subax.getSuperClass(), annotations);
    				man.applyChange(new RemoveAxiom(ont, subax));
    	    		man.applyChange(new AddAxiom(ont, ax));	    			
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
		String[] classes;
		remove  = new ArrayList<String>();	
		int index;
		
		// only if there is a selection
		if (cls != null) {			
			
			// update the textfields and checkboxes
			fDomain.setText(GwapValidation.getDomain(cls, ont, label, 2));
			fSubclass.setText(getOWLModelManager().getRendering(cls));		
			
			// find all superclasses for this subclass
			String superclasses = "";
			for (OWLClassExpression ce : cls.getSuperClasses(ont)) {
				// no anonymous superclasses (for exampe "a value b" or "x some y")
				if (!ce.isAnonymous()) {
					superclasses = superclasses + getOWLModelManager().getRendering(ce) + ", ";
				}			
			}
			if (!superclasses.equals("")) {
				superclasses = superclasses.substring(0, superclasses.length() - 2);
			}
			fSuperclass.setText(superclasses);
			// parse superclasses
			classes = superclasses.split(", ");
			
			fInfo.setText(GwapValidation.getInfo(cls, ont, label, 2));
			rCrowdflower.setSelected(GwapValidation.getCrowdflower(cls, ont, label, 2));
			rQuiz.setSelected(!GwapValidation.getCrowdflower(cls, ont, label, 2));
			if (rQuiz.isSelected()) {
    			bCosts.setEnabled(false);
    		}
			cSubtree.setSelected(GwapValidation.getSubtree(cls, ont, label, 2));
			
			// search through all comments of the selected class
			for (OWLAnnotation anno : cls.getAnnotations(ont, label)) {
				// if there is a comment "#gwapsubclass:removed=true;", no results are displayed until the current validation is cancelled
				if (anno.getValue().toString().substring(1, 28).equals("#gwapsubclass:removed=true;")) {
					aStatus.setText("All negatively validated subclassof relations have already been <b>removed</b>.<br>" + 
									"Please cancel current validation before starting a new validation!");
					
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
					
					// Set status scrollpane visible and refresh the GUI
		    		sStatus.setVisible(true);
		    		revalidate();
		    		
					return cls;
				}
			}
			
			// if there are no superclasses
			if (superclasses.equals("")) {
				// if there is a validation in progress
				if (GwapValidation.isValidating(cls, ont, label, 2) == true) {
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
					aStatus.setText("The current class <b>has no superclass(es)</b>.<br>Please select \"Validate subtree\" "  + 
							"and press \"GO\" if you want to validate all subclasses of the current class.");
				}
							
				// set remove button non-visible
				bRemove.setVisible(false);
				
				// Set status scrollpane visible and refresh the GUI
				sStatus.setVisible(true);
				revalidate();
				
				return cls;
			}
			
			// if there is already a validation in progress
			if (GwapValidation.isValidating(cls, ont, label, 2) == true) {
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
				message = "Validating subclass <b>\"" + fSubclass.getText() + "\"</b> against superclass(es) <b>\"" + fSuperclass.getText() + 
						  "\"</b> in the domain of <b>\"" + fDomain.getText() + "\"</b>";
				if (!fInfo.getText().equals("")) {
					message += " with additional info <b>\"" + fInfo.getText() + "\"</b><br>";
				}
				else {
					message += "<br>";
				}	    		
	    		    		
	    		// check if there are already results available from the GWAP
	    		results = gwap.getResults(GwapValidation.getJobID(cls, ont, label, 2));
	    		// set index for result array depending on the task-id
	    		index = (GwapValidation.getTaskID(cls, ont, label, 2) - 1) * 3; 
	    		
	    		if (results != null) {
	    			// create IRI for uComp subclassof check
	    			IRI ucomp = IRI.create(ont.getOntologyID().getOntologyIRI() + "#uComp_subclassof_check");
	    			
	    			// for every validated class
	    			for (int i = 0; i < classes.length; i++) {  
	    				
		    			// if the validation was positive
		    			if (results[index + 3 * i + 0] > results[index + 3 * i + 1]) {
		    				
		    				// write detailed results
		    				message += "Results from uComp API for superclass \"" + classes[i] + "\": <b><u>Yes: " + results[index + 3 * i + 0] + 
		    						   "</u>, No: " + results[index + 3 * i + 1] + ", I don't know: " + results[index + 3 * i + 2] + "</b><br>";			
		    				message += "uComp Validation <b><font color=\"green\">POSITIVE</font></b>! The class \"" + fSubclass.getText() +
		    						"\" <b>is a subclass</b> of \"" + classes[i] + "\"!";
		    				
		    				// set uComp_subclassof_check
		    	    		relevance = df.getOWLAnnotation(df.getOWLAnnotationProperty(ucomp), df.getOWLLiteral(true));
		    	    		// for every subclass axiom of the ontology, where the subclass is the current class
		    	    		for (OWLSubClassOfAxiom subax : ont.getSubClassAxiomsForSubClass(selectedClass)) {
		    	    			// if the superclass of the axiom matches the currently validated superclass
		    	    			if (getOWLModelManager().getRendering(subax.getSuperClass()).equals(classes[i])) {
		    	    				// backup current annotations and add the result of the subclassof check
		    	    				Set<OWLAnnotation> annotations = new HashSet<OWLAnnotation>();
		    	    				annotations.addAll(subax.getAnnotations());				    	    					   	    				
		    	    				annotations.add(relevance);
		    	    				// remove the old axiom and add a new axiom with all annotations
		    	    				ax = df.getOWLSubClassOfAxiom(selectedClass, subax.getSuperClass(), annotations);
		    	    				man.applyChange(new RemoveAxiom(ont, subax));
				    	    		man.applyChange(new AddAxiom(ont, ax));
		    	    			}
		    	    		}	
		    			}    			
		    			
		    			else {
		    				// if the validation was negative
		    				if (results[index + 3 * i + 0] < results[index + 3 * i + 1]) {
		    					
		    					// write detailed results
			    				message += "Results from uComp API for superclass \"" + classes[i] + "\": <b>Yes: " + results[index + 3 * i + 0] + 
			    						   ", <u>No: " + results[index + 3 * i + 1] + "</u>, I don't know: " + results[index + 3 * i + 2] + "</b><br>";
			    				message += "uComp Validation <b><font color=\"red\">NEGATIVE</font></b>! The class \"" + fSubclass.getText() +
			    						"\" <b>is not a subclass</b> of \"" + classes[i] + "\"!";  		    					
		    					
			    				// set uComp_subclassof_check
			    	    		relevance = df.getOWLAnnotation(df.getOWLAnnotationProperty(ucomp), df.getOWLLiteral(false));
			    	    		// for every subclass axiom of the ontology, where the subclass is the current class
			    	    		for (OWLSubClassOfAxiom subax : ont.getSubClassAxiomsForSubClass(selectedClass)) {
			    	    			// if the superclass of the axiom matches the currently validated superclass
			    	    			if (getOWLModelManager().getRendering(subax.getSuperClass()).equals(classes[i])) {
			    	    				// backup current annotations and add the result of the subclassof check
			    	    				Set<OWLAnnotation> annotations = new HashSet<OWLAnnotation>();
			    	    				annotations.addAll(subax.getAnnotations());				    	    					
			    	    				annotations.add(relevance);
			    	    				// remove the old axiom and add a new axiom with all annotations
			    	    				ax = df.getOWLSubClassOfAxiom(selectedClass, subax.getSuperClass(), annotations);
			    	    				man.applyChange(new RemoveAxiom(ont, subax));
					    	    		man.applyChange(new AddAxiom(ont, ax));
			    	    			}
			    	    		}
		    					
		    					// add class to remove list
		    					remove.add(classes[i]);
		    					
		    					// set remove button enabled and visible
		    					bRemove.setEnabled(true);
		    					bRemove.setVisible(true);
		    				}
		    				// if the validation was undetermined
		    				else {
		    					// write detailed results
		    					message += "Results from uComp API for superclass \"" + classes[i] + "\": <b>Yes: " + results[index + 3 * i + 0] + 
			    						   ", No: " + results[index + 3 * i + 1] + ", <u>I don't know: " + results[index + 3 * i + 2] + "</u></b><br>";
			    				message += "uComp Validation <b>UNDETERMINED</b>! The class \"" + fSubclass.getText() +
			    						"\" <b>might or might not be a subclass</b> of \"" + classes[i] + "\"!";
		    				}
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
