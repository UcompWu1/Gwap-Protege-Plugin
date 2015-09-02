package at.ac.wu.ucompvalidation;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
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
import org.protege.editor.owl.ui.view.individual.AbstractOWLIndividualViewComponent;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AddOntologyAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.model.RemoveOntologyAnnotation;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// CLASS GWAPINDIVIDUALVALIDATION														 				 	  			 //
// Java class for gwap validation for OWL individuals is inherited from default class for OWL Individual View Components //
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
@SuppressWarnings("serial")
public class GwapIndividualValidation extends AbstractOWLIndividualViewComponent {

	// GUI Elements
	private Box dBox, nBox, cBox, aBox, iBox, crBox, gBox, sBox, rBox;
	private JLabel lDomain, lNamedIndividual, lClass, lInfo;
	private JTextField fDomain, fNamedIndividual, fClass, fInfo;
	private JCheckBox cAllIndividuals;
	private JRadioButton rCrowdflower, rQuiz;
	private JButton bCosts, bGo, bCancel, bRemove;
	private JScrollPane sStatus;
	private JEditorPane aStatus;
	
	// OWL Classes
	private OWLOntologyManager man;
	private OWLOntology ont;
	private OWLDataFactory df;
	private OWLAnnotation comment;
	private OWLAxiom ax;
	private OWLNamedIndividual ind;
	private OWLAnnotationProperty label;
	
	// Gwap Communication
	private GwapCommunication gwap;
	
	// Individuals to be removed
	private ArrayList<String> remove;
	private ArrayList<OWLIndividual> owlindividuals;
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// METHOD INITIALISE INDIVIDUALS VIEW																			 //	
	// abstract method from superclass must be implemented, it is called when view is initialised for the first time //
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void initialiseIndividualsView() throws Exception {
		
		// initialise OWL Classes
		man   	= getOWLModelManager().getOWLOntologyManager();
		ont   	= getOWLModelManager().getActiveOntology();
		df    	= getOWLModelManager().getOWLDataFactory();
		// define annotation type as RDFS comment
		label 	= df.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_COMMENT.getIRI());
		// initialise Gwap Communication Class
		gwap 	= new GwapCommunication();
		
		// BUILD THE GUI
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setToolTipText("<html>This component validates <b>instanceOf</b> relationships between concepts and individuals.</html>");
		
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
		
		// NAMED INDIVIDUAL-BOX
        nBox = Box.createHorizontalBox();        
        lNamedIndividual = new JLabel("Is individual:");
		lNamedIndividual.setHorizontalAlignment(SwingConstants.CENTER);        
        
	    fNamedIndividual = new JTextField("", 10);
	    fNamedIndividual.setEditable(false);
	    
	    fNamedIndividual.setColumns((int) (((lNamedIndividual.getPreferredSize().width * 2) - 6) / 8));

	    lNamedIndividual.setMaximumSize(new Dimension(1000, lNamedIndividual.getMaximumSize().height));
	    fNamedIndividual.setMaximumSize(new Dimension(2000, fNamedIndividual.getMaximumSize().height));
	    
	    nBox.add(Box.createRigidArea(new Dimension(10, 0)));
	    nBox.add(lNamedIndividual);
	    nBox.add(Box.createRigidArea(new Dimension(10, 0))); 
	    nBox.add(fNamedIndividual);
        nBox.add(Box.createRigidArea(new Dimension(10, 0)));
        
        // CLASS-BOX
        cBox = Box.createHorizontalBox();
	    lClass = new JLabel("an instance of the following class(es):");
	    lClass.setHorizontalAlignment(SwingConstants.CENTER);        
        
	    fClass = new JTextField("", 10);
	    fClass.setEditable(false);
	    fClass.setColumns((int) (((lClass.getPreferredSize().width * 2) - 6) / 8));
	    
	    lClass.setMaximumSize(new Dimension(1000, lClass.getMaximumSize().height));
	    fClass.setMaximumSize(new Dimension(2000, fClass.getMaximumSize().height));
	    
	    cBox.add(Box.createRigidArea(new Dimension(10, 0)));
	    cBox.add(lClass);
	    cBox.add(Box.createRigidArea(new Dimension(10, 0)));
	    cBox.add(fClass);
	    cBox.add(Box.createRigidArea(new Dimension(10, 0)));
	    
	    // ALL-RELATIONS-BOX
	    aBox = Box.createHorizontalBox();
	    aBox.setAlignmentX(CENTER_ALIGNMENT);
	    
	    cAllIndividuals = new JCheckBox("Validate all individuals which are an instance of this/these class(es)");
	    cAllIndividuals.setHorizontalAlignment(SwingConstants.CENTER);
	    cAllIndividuals.setEnabled(true);
	    
	    aBox.add(cAllIndividuals);
	    
	    // INFO-BOX
        iBox = Box.createHorizontalBox();
	    lInfo = new JLabel("Additional information for validators:");
	    lInfo.setHorizontalAlignment(SwingConstants.CENTER);       
        
	    fInfo = new JTextField("", 10);
	    fInfo.setColumns((int) (((lInfo.getPreferredSize().width * 2) - 6) / 8));
	    fInfo.setToolTipText("You can use this field to give additional information about the task to the crowd workers.");

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
	    
	    bCosts = new JButton("CALCULATE COSTS");
	    bCosts.setEnabled(false);
	    bCosts.setToolTipText("Press to calculate the expected costs of a job with the current settings, only possible when sending tasks to CrowdFlower.");
	    // Action Listener is added to button
	    bCosts.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {    		
	    		ArrayList<OWLClass> owlclasses;
	    		int ccount = 0, judgements, centspaid;
	    		
	    		// only if there is a selection
	    		if (ind != null) {    			
	    			
	    			// initialise arraylist for all owl-individuals that are to be validated
	    			owlindividuals 	= new ArrayList<OWLIndividual>();
	    			owlclasses 		= new ArrayList<OWLClass>();
	    			// if the validate all individuals checkbox is selected, start the validation process also for every individual
	    			// which is an instance of any of the classes which the currently selected individual is an instance of
		    		if (cAllIndividuals.isSelected() == true) {	    				    			
		    			// find all types (classes) for the current individual										
	    				for (OWLClassExpression ce : ind.getTypes(ont)) {
	    					owlclasses.add(ce.asOWLClass());
	    				}
	    				for (OWLClassExpression ce : owlclasses) {
	    					// find all indiviuals which are an instance of any of these classes
	    					for (OWLIndividual oi : ce.asOWLClass().getIndividuals(ont)) {
	    						// only if there isn't already a validation for this individual in progress and 
	    						// the indvidual doesn't exist in the array-list yet
	    						if (GwapValidation.isValidating(oi.asOWLNamedIndividual(), ont, label,  3) == false && 
	    								!owlindividuals.contains(oi)) {
	    							owlindividuals.add(oi);				
	    						}				
	    					}
	    				}
		    		}
		    		else {
		    			// if there isn't already a validation for this individual in progress
		    			if (GwapValidation.isValidating(ind, ont, label, 3) == false) {
		    				owlindividuals.add(ind);
		    			}
		    		}
		    		// find all types (classes) for all individuals										
					for (OWLIndividual oi : owlindividuals) {				
						ccount += oi.getTypes(ont).size();
					}
					
		    		// get judgements and centspaid setting from API-Settings textfile and calculate and display total costs
		    		try {	    			
		    			judgements = Integer.valueOf(gwap.getAPISettings().substring(gwap.getAPISettings().indexOf(",") + 1, gwap.getAPISettings().lastIndexOf(",")));
		    			centspaid = Integer.valueOf(gwap.getAPISettings().substring(gwap.getAPISettings().lastIndexOf(",") + 1));
		    			double dollar = ((double) (ccount * judgements * centspaid)) / 100.0;
		    			aStatus.setText("This job contains <b>" + ccount + " units</b>.<br>You have set the number of judgements to <b>" + judgements + 
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
	    		ArrayList<OWLClass> owlclasses;
	    		ArrayList<String> individuals;
	    		ArrayList<ArrayList<String>> classes = new ArrayList<ArrayList<String>>();
	    		ArrayList<String> currentclasses;
	    		boolean crowdflower;
	    		long jobid;
	    		int i, taskid;
	    		
	    		// only if there is a selection
	    		if (ind != null) {
	    			// only if there was provided a domain
	    			if (!fDomain.getText().equals("")) {
	    			
		    			// initialise arraylist for all owl-individuals that are to be validated
		    			owlindividuals 	= new ArrayList<OWLIndividual>();
		    			owlclasses 		= new ArrayList<OWLClass>();
		    			individuals 	= new ArrayList<String>();
		    			
		    			// if the validate all individuals checkbox is selected, start the validation process also for every individual
		    			// which is an instance of any of the classes which the currently selected individual is an instance of
			    		if (cAllIndividuals.isSelected() == true) {	    				    			
			    			// find all types (classes) for the current individual										
		    				for (OWLClassExpression ce : ind.getTypes(ont)) {
		    					owlclasses.add(ce.asOWLClass());
		    				}
		    				for (OWLClassExpression ce : owlclasses) {
		    					// find all indiviuals which are an instance of any of these classes
		    					for (OWLIndividual oi : ce.asOWLClass().getIndividuals(ont)) {
		    						// only if there isn't already a validation for this individual in progress and 
		    						// the indvidual doesn't exist in the array-list yet
		    						if (GwapValidation.isValidating(oi.asOWLNamedIndividual(), ont, label,  3) == false && 
		    								!owlindividuals.contains(oi)) {
		    							owlindividuals.add(oi);				
		    						}				
		    					}
		    				}
			    		}
			    		else {
			    			// if there isn't already a validation for this individual in progress
			    			if (GwapValidation.isValidating(ind, ont, label, 3) == false) {
			    				owlindividuals.add(ind);
			    			}
			    		}			    			    				
	    				
			    		// parse array-list into normal strings for gwapcommunication
			    		for (OWLIndividual oi : owlindividuals) {
			    			individuals.add(getOWLModelManager().getRendering(oi));
			    		}
			    		
			    		// find all types (classes) for all individuals										
						for (OWLIndividual oi : owlindividuals) {
							currentclasses = new ArrayList<String>();					
				    		for (OWLClassExpression ce : oi.getTypes(ont)) {
								currentclasses.add(getOWLModelManager().getRendering(ce));
							}
				    		classes.add(currentclasses);
						}	    				
	    				
	    				// send task to crowdflower or ucomp-quiz?
		    			if (rCrowdflower.isSelected()) {
		    				crowdflower = true;
		    			}
		    			else {
		    				crowdflower = false;
		    			}
		    			
		    			// crowdflower has a minimum of 5 units
		    			if (!(crowdflower == true && owlindividuals.size() < 5)) {
	    				
		    				// create job
		    				jobid = gwap.createJob(3, crowdflower, fDomain.getText(), individuals, classes, null, fInfo.getText());	    		
	    				
		    				// if job creation was successful
		    				if (jobid != -1 && jobid != -2) {	    			
	
		    					// Write individual validation information to individual comment for every individual
		    					i = taskid = 1;
		    					for (OWLIndividual oi : owlindividuals) {	
			    					// Syntax: "#gwapindividual:domain=xyz;info=abc;crowdflower=true/false;jobid=123;taskid=1;"
				    	    		comment = df.getOWLAnnotation(df.getRDFSComment(), df.getOWLLiteral("#gwapindividual:domain=" + fDomain.getText() + 
				    	    				";info=" + fInfo.getText() + ";crowdflower=" + crowdflower + ";jobid=" + jobid + ";taskid=" + taskid + ";"));
				    	    		ax = df.getOWLAnnotationAssertionAxiom(oi.asOWLNamedIndividual().getIRI(), comment);
				    	    		man.applyChange(new AddAxiom(ont, ax));
				    	    		// increment counter for taskid depending on the amount of types (classes) to match the correct position in the result csv later
				    				taskid = taskid + classes.get(i - 1).size();
				    				i++;
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
			    					    	    				    	    		
			    	    		// Write status messages
		    					String message = "";
		    					message = "Validating individual <b>\"" + fNamedIndividual.getText() + "\"</b> against class(es) <b>\"" + fClass.getText() + 
		    							  "\"</b> in the domain of <b>\"" + fDomain.getText() + "\"</b>";
		    					if (!fInfo.getText().equals("")) {
		    						message += " with additional info <b>\"" + fInfo.getText() + "\"</b><br>";
		    					}
		    					else {
		    						message += "<br>";
		    					}
		    					message += "<b>Waiting for results</b> from uComp API...please check again later...<br>";
		    					
			    	    		aStatus.setText(message);
			    	    		
			    				// Set status scrollpane visible and refresh the GUI (without validate() JEditorPane is not refreshed properly)
			    				sStatus.setVisible(true);
			        			validate();
			        			revalidate();
			        			
			        			// set editable/enabled for textfield and buttons
			    	    		fDomain.setEditable(false);
			    	    		cAllIndividuals.setEnabled(false);
		    	    			fInfo.setEditable(false);
			    	    		rCrowdflower.setEnabled(false);
			    	    		rQuiz.setEnabled(false);
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
	    
	    bCancel = new JButton("CANCEL");
	    bCancel.setEnabled(false);
	    // Action Listener is added to button
	    bCancel.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    
	    		// only if there is a selection
	    		if (ind != null) {
		    		// if there is a validation for this individual in progress
	    			if (GwapValidation.isValidating(ind, ont, label, 3) == true) {
	    				
	    				// if job cancelation was successful
	    	    		if (gwap.cancelJob(GwapValidation.getJobID(ind, ont, label, 3)) == true) { 
	    				
		    				// set editable/enabled for textfield and buttons
	    	    			fDomain.setEditable(true);
	    	    			cAllIndividuals.setEnabled(true);
	    	    			fInfo.setEditable(true);
	    	    			rCrowdflower.setEnabled(true);
	    	    			rQuiz.setEnabled(true);
		    	    		if (rCrowdflower.isSelected()) {
		    	    			bCosts.setEnabled(true);
		    	    		}
	    	    			bGo.setEnabled(true);
		    	    		bCancel.setEnabled(false);
		    	    		bRemove.setEnabled(false);
		    	    		
		    	    		// Delete individual validation information from individual comment
		    				for (OWLAnnotation anno : ind.getAnnotations(ont, label)) {
		    					// if the comment starts with "#gwapindividual:", it is relevant for the validation
		    					if (anno.getValue().toString().substring(1, 17).equals("#gwapindividual:")) {
		    						ax = df.getOWLAnnotationAssertionAxiom(ind.getIRI(), anno);
		    						man.applyChange(new RemoveAxiom(ont, ax));
		    					}
		    				}
		    				
		    				// Reset status box and set status scrollpane invisible
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
	    			}	
	    		}
	    	}
	    });		

	    // Expand the preferred width of the button (set it to the same size as GO button)
	    bCancel.setMaximumSize(new Dimension(bGo.getMaximumSize().width, bGo.getMaximumSize().height));
	    
	    gBox.add(bCosts);
	    gBox.add(Box.createRigidArea(new Dimension(10, 0)));
	    gBox.add(bGo);
	    gBox.add(Box.createRigidArea(new Dimension(30, 0)));
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
	    
	    bRemove = new JButton("REMOVE ALL NEGATIVE RELATIONS");
	    bRemove.setVisible(false);
	    // Action Listener is added to button
	    bRemove.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {    		
	    		    		
	    		// remove all negatively validated type (class) relations of the current individual
	    		for (OWLClassExpression ce : ind.getTypes(ont)) {
	    			// if remove list contains the current superclass
	    			if (remove.contains(getOWLModelManager().getRendering(ce))) {
	    				ax = df.getOWLClassAssertionAxiom(ce, ind);
		    			man.applyChange(new RemoveAxiom(ont, ax));
	    			}	    			
	    		}
	    		
	    		// Write information if negatively validated types (classes) have already been removed to individual comment
				// Syntax: "#gwapindividual:removed=true;"
				comment = df.getOWLAnnotation(df.getRDFSComment(), df.getOWLLiteral("#gwapindividual:removed=true;"));
				ax = df.getOWLAnnotationAssertionAxiom(ind.getIRI(), comment);
				man.applyChange(new AddAxiom(ont, ax));
	    		
	    		// update GUI   		
	    		bCancel.setEnabled(false);
	    		bRemove.setEnabled(false);
	    		aStatus.setText("All negatively validated type (class) relations of the individual <b>removed</b>...");
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
	    add(nBox);
	    add(new Box.Filler(new Dimension(0, 5), new Dimension(0, 25), new Dimension(0, 50)));
	    add(cBox);	    
	    add(Box.createRigidArea(new Dimension(0, 10)));
	    add(aBox);
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

	///////////////////////////////////////////////////////////////////////////////////////////////////////
	// METHOD UPDATE VIEW																				 //
	// abstract method from superclass must be implemented, it is called everytime the view gets updated //
	// => that is in fact everytime the user selects another part of the ontology						 //
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	public OWLNamedIndividual updateView(OWLNamedIndividual selectedIndividual) {		
		// the current selected individual is needed also in other methods => reference as class attribute
		ind = selectedIndividual;
		int[] results;
		String[] classes;
		remove  = new ArrayList<String>();
		int index;
		
		// only if there is a selection
		if (ind != null) {
			// update the textfields
			fDomain.setText(GwapValidation.getDomain(ind, ont, label, 3));
			fNamedIndividual.setText(getOWLModelManager().getRendering(ind));
			
			// find all types (classes) for this individual
			String types = "";
			for (OWLClassExpression ce : ind.getTypes(ont)) {
				types = types + getOWLModelManager().getRendering(ce) + ", ";
			}
			if (!types.equals("")) {
				types = types.substring(0, types.length() - 2);
			}
			fClass.setText(types);
			// parse superclasses
			classes = types.split(", ");
			
			fInfo.setText(GwapValidation.getInfo(ind, ont, label, 3));
			rCrowdflower.setSelected(GwapValidation.getCrowdflower(ind, ont, label, 3));
			rQuiz.setSelected(!GwapValidation.getCrowdflower(ind, ont, label, 3));
			if (rQuiz.isSelected()) {
    			bCosts.setEnabled(false);
    		}
			
			// search through all comments of the selected class
			for (OWLAnnotation anno : ind.getAnnotations(ont, label)) {
				// if there is a comment "#gwapindividual:removed=true;", no results are displayed until the current validation is cancelled
				if (anno.getValue().toString().substring(1, 30).equals("#gwapindividual:removed=true;")) {
					aStatus.setText("All negatively validated type (class) relations of this individual have already been <b>removed</b>.<br>" + 
									"Please cancel current validation before starting a new validation!");
					
					// set editable/enabled for textfields and buttons
					fDomain.setEditable(false);
					cAllIndividuals.setEnabled(false);
					fInfo.setEditable(false);
					rCrowdflower.setEnabled(false);
					rQuiz.setEnabled(false);
					bCosts.setEnabled(false);
					bGo.setEnabled(false);
		    		bCancel.setEnabled(true);
		    		// set remove button non-visible
		    		bRemove.setVisible(false);
					
					// Set status scrollpane visible and refresh the GUI
		    		sStatus.setVisible(true);
		    		revalidate();
		    		
					return ind;
				}
			}
			
			// if there are no type (class) relations, no validation for this individual can be started
			if (types.equals("")) {
				aStatus.setText("The current individual <b>has no type (class) relations</b>.<br>Please select another individual to start a validation!");
		
				// set editable/enabled for textfields, checkboxes and buttons
				fDomain.setEditable(false);
				cAllIndividuals.setEnabled(false);
				fInfo.setEditable(false);
				rCrowdflower.setEnabled(false);
				rQuiz.setEnabled(false);
				bCosts.setEnabled(false);
				bGo.setEnabled(false);
				bCancel.setEnabled(false);
				// set remove button non-visible
				bRemove.setVisible(false);
				
				// Set status scrollpane visible and refresh the GUI
				sStatus.setVisible(true);
				revalidate();
				
				return ind;
			}
			
			// if there is already a validation in progress
			if (GwapValidation.isValidating(ind, ont, label, 3) == true) {
				// set editable/enabled for textfield and buttons
				fDomain.setEditable(false);
				cAllIndividuals.setEnabled(false);
				fInfo.setEditable(false);
				rCrowdflower.setEnabled(false);
				rQuiz.setEnabled(false);
				bCosts.setEnabled(false);
				bGo.setEnabled(false);
	    		bCancel.setEnabled(true);
	    		// set remove button non-visible
	    		bRemove.setVisible(false);
	    		
				// prepare status messages
				String message = "";
				message = "Validating individual <b>\"" + fNamedIndividual.getText() + "\"</b> against class(es) <b>\"" + fClass.getText() + 
						  "\"</b> in the domain of <b>\"" + fDomain.getText() + "\"</b>";
				if (!fInfo.getText().equals("")) {
					message += " with additional info <b>\"" + fInfo.getText() + "\"</b><br>";
				}
				else {
					message += "<br>";
				}	    		
	    		
	    		// check if there are already results available from the GWAP
	    		results = gwap.getResults(GwapValidation.getJobID(ind, ont, label, 3));	    		
	    		// set index for result array depending on the task-id
	    		index = (GwapValidation.getTaskID(ind, ont, label, 3) - 1) * 3;
	    		
	    		if (results != null) {
	    			// for every validated class
	    			for (int i = 0; i < classes.length; i++) { 		    			    						    			
	    						
		    			// if the validation was positive
		    			if (results[index + 3 * i + 0] > results[index + 3 * i + 1]) {
		    				// write detailed results
		    				message += "Results from uComp API for type (class) relation \"" + classes[i] + "\": <b><u>Yes: " + 
			    					results[index + 3 * i + 0] + "</u>, No: " + results[index + 3 * i + 1] + ", I don't know: " + results[index + 3 * i + 2] + "</b><br>";
		    				message += "uComp Validation <b><font color=\"green\">POSITIVE</font></b>! The individual \"" + fNamedIndividual.getText() +
		    						"\" <b>is relevant</b> for the concept \"" + classes[i] + "\"!";		    				
		    			}    			
		    			  			
		    			else {
		    				// if the validation was negative
		    				if (results[index + 3 * i + 0] < results[index + 3 * i + 1]) {
			    				// write detailed results
			    				message += "Results from uComp API for type (class) relation \"" + classes[i] + "\": <b>Yes: " + 
				    					results[index + 3 * i + 0] + ", <u>No: " + results[index + 3 * i + 1] + "</u>, I don't know: " + results[index + 3 * i + 2] + "</b><br>";
			    				message += "uComp Validation <b><font color=\"red\">NEGATIVE</font></b>! The individual \"" + fNamedIndividual.getText() +
			    						"\" <b>is not relevant</b> for the concept \"" + classes[i] + "\"!";
		    					
		    					// add class to remove list
		    					remove.add(classes[i]);
		    					
		    					// set remove button enabled and visible
		    					bRemove.setEnabled(true);
		    					bRemove.setVisible(true);
		    				}
		    				// if the validation was undetermined
		    				else {
		    					// write detailed results
			    				message += "Results from uComp API for type (class) relation \"" + classes[i] + "\": <b>Yes: " + 
				    					results[index + 3 * i + 0] + ", No: " + results[index + 3 * i + 1] + ", <u>I don't know: " + results[index + 3 * i + 2] + "</u></b><br>";
			    				message += "uComp Validation <b>UNDETERMINED</b>! The individual \"" + fNamedIndividual.getText() +
			    						"\" <b>might or might not be relevant</b> for the concept \"" + classes[i] + "\"!";
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
	    		validate();
	    		revalidate();
			}
			// if there is no validation in progress
			else {
				// set editable/enabled for textfield and buttons
				fDomain.setEditable(true);
				cAllIndividuals.setEnabled(true);
				fInfo.setEditable(true);
				rCrowdflower.setEnabled(true);
				rQuiz.setEnabled(true);
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
	    
		return ind;
	}

	/////////////////////////////////////////////////////////
	// METHOD DISPOSE VIEW								   //
	// abstract method from superclass must be implemented //
	/////////////////////////////////////////////////////////
	public void disposeView() {		
	}
}
