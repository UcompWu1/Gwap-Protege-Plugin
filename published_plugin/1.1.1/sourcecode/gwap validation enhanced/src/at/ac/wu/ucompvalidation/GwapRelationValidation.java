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
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import org.protege.editor.owl.ui.view.objectproperty.AbstractOWLObjectPropertyViewComponent;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AddOntologyAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.model.RemoveOntologyAnnotation;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//CLASS GWAPOBJECTPROPERTYVALIDATION														 				 	  				   		  //
//Java class for gwap validation for "relation" object properties is inherited from default class for OWL Object Property View Components //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
@SuppressWarnings("serial")
public class GwapRelationValidation extends AbstractOWLObjectPropertyViewComponent {

	// GUI Elements
	private Box dBox, reBox, aBox, doBox, raBox, opBox, opMasterBox, iBox, crBox, gBox, sBox, rBox;
	private ArrayList<Box> opBoxes;
	private JLabel lOntologyDomain, lRelation, lDomain, lRange, lObjectProperties, lInfo;
	private JTextField fOntologyDomain, fRelation, fDomain, fRange, fInfo;
	private JCheckBox cAllRelations;
	private JRadioButton rCrowdflower, rQuiz;
	private ArrayList<JCheckBox> cObjectProperties;
	private JButton bCosts, bGo, bCancel, bReplace;
	private JScrollPane sStatus;
	private JEditorPane aStatus;
	
	// OWL Classes
	private OWLOntologyManager man;
	private OWLOntology ont;
	private OWLDataFactory df;
	private OWLAnnotation comment;
	private OWLAxiom ax;
	private OWLObjectProperty obj;
	private OWLAnnotationProperty label;
	
	// Gwap Communication
	private GwapCommunication gwap;
	private String answer;
	private ArrayList<OWLObjectProperty> owlrelations;
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// METHOD INITIALISE VIEW																			 			 //	
	// abstract method from superclass must be implemented, it is called when view is initialised for the first time //
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void initialiseView() throws Exception {
		
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
		setToolTipText("<html>This component helps to label (select an object property) for an unlabeled relation " +
					   "between two concepts in the ontology. Select the candidate relation labels from the list below.<br>" +
					   "Note: this only works for unlabeled relations (see documentation for details).</html>");
				
		// ONTOLOGY-DOMAIN-BOX
        dBox = Box.createHorizontalBox();
        lOntologyDomain = new JLabel("In the domain of:");
        lOntologyDomain.setHorizontalAlignment(SwingConstants.CENTER);        
        
        // always set the number of columns for text fields to prevent them from resizing
        fOntologyDomain = new JTextField("", 10);
        
        // preferred size of text field gets manipulated: double the width of the label
	    // only possible with setColumns(x), one column equals 8px (plus the minimum size of 6px)
        fOntologyDomain.setColumns((int) (((lOntologyDomain.getPreferredSize().width * 2) - 6) / 8));
        fOntologyDomain.setToolTipText("Set the domain of knowlegde.");
        
        // set proportion for the maximum size of the elements
        lOntologyDomain.setMaximumSize(new Dimension(1000, lOntologyDomain.getMaximumSize().height));
	    fOntologyDomain.setMaximumSize(new Dimension(2000, fOntologyDomain.getMaximumSize().height));
	    
	    // build the box, 10px space at the beginning, between the two elements, and at the end
	    dBox.add(Box.createRigidArea(new Dimension(10, 0)));
	    dBox.add(lOntologyDomain);
	    dBox.add(Box.createRigidArea(new Dimension(10, 0)));
	    dBox.add(fOntologyDomain);
	    dBox.add(Box.createRigidArea(new Dimension(10, 0)));
		
		// RELATION-BOX
        reBox = Box.createHorizontalBox();
        lRelation = new JLabel("Suggest an object property for:");
        lRelation.setHorizontalAlignment(SwingConstants.CENTER);        
        
        fRelation = new JTextField("", 10);
        fRelation.setEditable(false);
        
        fRelation.setColumns((int) (((lRelation.getPreferredSize().width * 2) - 6) / 8));
	    
        lRelation.setMaximumSize(new Dimension(1000, lRelation.getMaximumSize().height));
	    fRelation.setMaximumSize(new Dimension(2000, fRelation.getMaximumSize().height));
	    
	    reBox.add(Box.createRigidArea(new Dimension(10, 0)));
	    reBox.add(lRelation);
	    reBox.add(Box.createRigidArea(new Dimension(10, 0)));
	    reBox.add(fRelation);
	    reBox.add(Box.createRigidArea(new Dimension(10, 0)));
	    
	    // ALL-RELATIONS-BOX
	    aBox = Box.createHorizontalBox();
	    aBox.setAlignmentX(CENTER_ALIGNMENT);
	    
	    cAllRelations = new JCheckBox("Suggest for all relations");
	    cAllRelations.setHorizontalAlignment(SwingConstants.CENTER);
	    cAllRelations.setEnabled(true);
	    
	    aBox.add(cAllRelations);
	    
	    // DOMAIN-BOX
        doBox = Box.createHorizontalBox();
        lDomain = new JLabel("Suggest between Subject (Domain):");
        lDomain.setHorizontalAlignment(SwingConstants.CENTER);        
        
        fDomain = new JTextField("", 10);
        fDomain.setEditable(false);
        fDomain.setColumns((int) (((lDomain.getPreferredSize().width * 2) - 6) / 8));
	    
	    lDomain.setMaximumSize(new Dimension(1000, lDomain.getMaximumSize().height));
	    fDomain.setMaximumSize(new Dimension(2000, fDomain.getMaximumSize().height));
	    
	    doBox.add(Box.createRigidArea(new Dimension(10, 0)));
	    doBox.add(lDomain);
	    doBox.add(Box.createRigidArea(new Dimension(10, 0)));
	    doBox.add(fDomain);
	    doBox.add(Box.createRigidArea(new Dimension(10, 0)));
	    
	    // RANGE-BOX
	    raBox = Box.createHorizontalBox();
	    lRange = new JLabel("and Object (Range):");
	    lRange.setHorizontalAlignment(SwingConstants.CENTER);        
        
	    fRange = new JTextField("", 10);
	    fRange.setEditable(false);
	    fRange.setColumns((int) (((lRange.getPreferredSize().width * 2) - 6) / 8));
	    
        lRange.setMaximumSize(new Dimension(1000, lRange.getMaximumSize().height));
        fRange.setMaximumSize(new Dimension(2000, fRange.getMaximumSize().height));
	    
	    raBox.add(Box.createRigidArea(new Dimension(10, 0)));
	    raBox.add(lRange);
	    raBox.add(Box.createRigidArea(new Dimension(10, 0)));
	    raBox.add(fRange);
	    raBox.add(Box.createRigidArea(new Dimension(10, 0)));
	    
	    // OBJECT-PROPERTIES-BOX
	    opBox = Box.createHorizontalBox();
	    opBox.setAlignmentX(CENTER_ALIGNMENT);
	    
	    lObjectProperties = new JLabel("Choose from existing object properties:");
	    lObjectProperties.setHorizontalAlignment(SwingConstants.CENTER);
	        
	    opBox.add(lObjectProperties);
	    
	    // DEFINE OBJECT PROPERTIES    	
	    cObjectProperties = new ArrayList<JCheckBox>();
	    
	    // check all object properties, the first 10 non-template relations ("relation") are included in the checkbox-list
	    int i = 0;
	    for (OWLObjectProperty op : ont.getObjectPropertiesInSignature()) {
	    	if (i < 15) {
	    		if (!getOWLModelManager().getRendering(op).equals("relation")) {
		    		cObjectProperties.add(new JCheckBox(getOWLModelManager().getRendering(op)));
		    		i++;
		    	}
	    	}
	    	else {
	    		break;
	    	}	    	
	    }
	    
	    // OBJECT-PROPERTIES-MASTER-BOX
	    opMasterBox = Box.createHorizontalBox();
	    opMasterBox.setAlignmentX(CENTER_ALIGNMENT);
	    // JPanel is needed for nested BoxLayout
	    JPanel jp = new JPanel();
	    jp.setLayout(new BoxLayout(jp, BoxLayout.X_AXIS));
	    opMasterBox.add(jp);	    
	    
	    // a new Box is created for every group of 5 checkboxes
	    opBoxes = new ArrayList<Box>();
	    for (i = 0; i < ((cObjectProperties.size() - 1) / 5) + 1; i++) {
	    	opBoxes.add(Box.createVerticalBox());
	    }
	    // add all Boxes with a filler between them
	    for (Box box : opBoxes) {
	    	box.setAlignmentY(TOP_ALIGNMENT);
	    	jp.add(box);
	    	jp.add(new Box.Filler(new Dimension(0, 0), new Dimension(5, 0), new Dimension(5, 0)));
	    }	    	        
	    // add checkboxes to respective Boxes
	    i = 0;
	    for (JCheckBox cb : cObjectProperties) {
	    	opBoxes.get(i / 5).add(cb);
	    	i++;
	    }
	    
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
	    gBox.setAlignmentX(CENTER_ALIGNMENT);
	    
	    bCosts = new JButton("CALCULATE COSTS");
	    bCosts.setEnabled(false);
	    bCosts.setToolTipText("Press to calculate the expected costs of a job with the current settings, only possible when sending tasks to CrowdFlower.");
	    // Action Listener is added to button
	    bCosts.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {    		
	    		int judgements, centspaid;
	    		
	    		// only if there is a selection
	    		if (obj != null) {    			
	    			
	    			// initialise arraylist for all owl-classes that are to be validated
	    			owlrelations	= new ArrayList<OWLObjectProperty>();	    							    			    		
		    		// if all relations are to be validated
    	    		if (cAllRelations.isSelected() == true) {
    	    			// for every object property
    	    			for (OWLObjectProperty op : ont.getObjectPropertiesInSignature()) {
    	    		    	// if the property is named "relation"
    	    				if (getOWLModelManager().getRendering(op).equals("relation")) {
    	    					// if there isn't already a validation for this object property in progress
    	    					if (GwapValidation.isValidating(op, ont, label, 5) == false) {
    	    						owlrelations.add(op);
    	    					}
    	    				}
    	    			}
    	    		}
    	    		else {
    	    			// if there isn't already a validation for this object property in progress
    					if (GwapValidation.isValidating(obj, ont, label, 5) == false) {
    						owlrelations.add(obj);
    					}
		    		}
		    			
		    		// get judgements and centspaid setting from API-Settings textfile and calculate and display total costs
		    		try {
		    			judgements = Integer.valueOf(gwap.getAPISettings().substring(gwap.getAPISettings().indexOf(",") + 1, gwap.getAPISettings().lastIndexOf(",")));
		    			centspaid = Integer.valueOf(gwap.getAPISettings().substring(gwap.getAPISettings().lastIndexOf(",") + 1));
		    			double dollar = ((double) (owlrelations.size() * judgements * centspaid)) / 100.0;
		    			aStatus.setText("This job contains <b>" + owlrelations.size() + " units</b>.<br>You have set the number of judgements to <b>" + judgements + 
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
	    		ArrayList<String> oprops = new ArrayList<String>();
	    		ArrayList<String> subjects = new ArrayList<String>();
	    		ArrayList<ArrayList<String>> objects = new ArrayList<ArrayList<String>>();
	    		ArrayList<String> temp = new ArrayList<String>();
	    		objects.add(temp);
	    		boolean crowdflower;
	    		long jobid;
	    		int taskid;
	    			    		    		
	    		// only if there is a selection
	    		if (obj != null) {
	    			// only if there was provided a domain
	    			if (!fOntologyDomain.getText().equals("")) {
	    				
	    				// initialise arraylist for all relations that are to be validated
		    			owlrelations	= new ArrayList<OWLObjectProperty>();
		    			
			    		// if all relations are to be validated
	    	    		if (cAllRelations.isSelected() == true) {
	    	    			// for every object property
	    	    			for (OWLObjectProperty op : ont.getObjectPropertiesInSignature()) {
	    	    		    	// if the property is named "relation"
	    	    				if (getOWLModelManager().getRendering(op).equals("relation")) {
	    	    					// if there isn't already a validation for this object property in progress
	    	    					if (GwapValidation.isValidating(op, ont, label, 5) == false) {
	    	    						owlrelations.add(op);
	    	    					}
	    	    				}
	    	    			}
	    	    		}
	    	    		else {
	    	    			// if there isn't already a validation for this object property in progress
	    					if (GwapValidation.isValidating(obj, ont, label, 5) == false) {
	    						owlrelations.add(obj);
	    					}
			    		}	    	    			    			
		    				
	    				// only selected checkboxes are sent to the GWAP as possible answers 	    					
	    				for (JCheckBox cb : cObjectProperties) {
	    					if (cb.isSelected() == true) {	    						
	    						oprops.add(cb.getText());
	    					}
	    				}
	    				
	    				// only if there was selected at least one object property
	    				if (oprops.size() != 0) {
	    					
	    					// get domain/range
				    		for (OWLObjectProperty op : owlrelations) {			    						    			
	    		    			String subject = "", object = "";	    	    		    			
	    		    			for (OWLClassExpression ce : op.getDomains(ont)) {
	    		    				subject = getOWLModelManager().getRendering(ce);
	    		    				subjects.add(subject);
	    		    				break;
	    		    			}
	    		    			for (OWLClassExpression ce : op.getRanges(ont)) {
	    		    				object = getOWLModelManager().getRendering(ce);
	    		    				objects.get(0).add(object);
	    		    				break;
	    		    			}				    			
				    		}			
	    					
	    					// send task to crowdflower or ucomp-quiz?
	    	    			if (rCrowdflower.isSelected()) {
	    	    				crowdflower = true;
	    	    			}
	    	    			else {
	    	    				crowdflower = false;
	    	    			}
	    	    			
	    	    			// crowdflower has a minimum of 5 units
			    			if (!(crowdflower == true && owlrelations.size() < 5)) {
	    					
		    					// create job
			    				jobid = gwap.createJob(5, crowdflower, fOntologyDomain.getText(), subjects, objects, oprops, fInfo.getText());
	
			    				// if job creation was successful
			    				if (jobid != -1 && jobid != -2) {
			    					
			    					// Write relation validation information to object property comment
			    					taskid = 1;
				        			for (OWLObjectProperty op : owlrelations) {	
					        			// Syntax: "#gwaprelation:all=true;a=true;b=false;c=true;info=abc;crowdflower=true/false;jobid=123;taskid=1;"
					    	    		String properties = "";
					    	    		for (JCheckBox cb : cObjectProperties) {
					    	    			properties = properties + "op_" + cb.getText() + "=" + cb.isSelected() + ";";
					    	    		} 
					    	    		comment = df.getOWLAnnotation(df.getRDFSComment(), df.getOWLLiteral("#gwaprelation:domain=" + fOntologyDomain.getText() + 
					    	    									  ";all=" + cAllRelations.isSelected() + ";" + properties.substring(0, properties.length() - 1) +
					    	    									  ";info=" + fInfo.getText() + ";crowdflower=" + crowdflower + ";jobid=" + jobid + 
					    	    									  ";taskid=" + taskid + ";"));
					    	    		ax = df.getOWLAnnotationAssertionAxiom(op.getIRI(), comment);
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
				    	    		comment = df.getOWLAnnotation(df.getRDFSComment(), df.getOWLLiteral("#gwapclass:domain=" + fOntologyDomain.getText() + 
				    	    									  ";crowdflower=" + crowdflower + ";"));
				    	    		man.applyChange(new AddOntologyAnnotation(ont, comment));
				    	    		
				    	    		// prepare status messages
				    	    		String message = "";
				    	    		message += "Validating relation <b>\"" + fRelation.getText() + "\"</b> between subject <b>\"" + fDomain.getText() +
				    	    				   "\"</b> and object <b>\"" + fRange.getText() + "\"</b> against the object properties: <b>\"";
				    	    		for (JCheckBox cb : cObjectProperties) {
				    	    			if (cb.isSelected() == true) {
				    	    				message += cb.getText() + ", "; 
				    	    			}		    	    			
				    	    		}
				    	    		message = message.substring(0, message.length() - 2) + "\"</b> in the domain of <b>\"" + fOntologyDomain.getText() + "\"</b>";				    	    		
			    					if (!fInfo.getText().equals("")) {
			    						message += " with additional info <b>\"" + fInfo.getText() + "\"</b><br>";
			    					}
			    					else {
			    						message += "<br>";
			    					}
			    					message += "<b>Waiting for results</b> from uComp API...please check again later...<br>";
			    					
			    					// Write status messages
				    	    		aStatus.setText(message);				    	    			    		
				    	    		
				    				// Set status scrollpane visible and refresh the GUI (without validate() JEditorPane is not refreshed properly)
				    				sStatus.setVisible(true);
				        			validate();
				        			revalidate();
				        			
				    	    		// set editable/enabled for textfields, checkboxes and buttons
				    				fOntologyDomain.setEditable(false);
			    	    			fInfo.setEditable(false);
			    	    			rCrowdflower.setEnabled(false);
			    	    			rQuiz.setEnabled(false);
				    				cAllRelations.setEnabled(false);
				    				for (JCheckBox cb : cObjectProperties) {
				    			    	cb.setEnabled(false);
				    			    }
				    	    		bCosts.setEnabled(false);
				    				bGo.setEnabled(false);
				    	    		bCancel.setEnabled(true);
				    	    		// set replace button non-visible
				    	    		bReplace.setVisible(false);
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
	    					aStatus.setText("Please <b>select</b> at least one <b>object property</b>!");
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
	    		if (obj != null) {
		    		// if there is a validation for this object property in progress
	    			if (GwapValidation.isValidating(obj, ont, label, 5) == true) {
	    				
	    				// if job cancelation was successful
	    	    		if (gwap.cancelJob(GwapValidation.getJobID(obj, ont, label, 5)) == true) { 
	    				
		    	    		// set editable/enabled for textfields, checkboxes and buttons
	    	    			fOntologyDomain.setEditable(true);
	    	    			fInfo.setEditable(true);
	    	    			rCrowdflower.setEnabled(true);
	    	    			rQuiz.setEnabled(true);
		    				cAllRelations.setEnabled(true);
		    				for (JCheckBox cb : cObjectProperties) {
		    			    	cb.setEnabled(true);
		    			    }
							if (rCrowdflower.isSelected()) {
		    	    			bCosts.setEnabled(true);
		    	    		}
		    				bGo.setEnabled(true);
		    	    		bCancel.setEnabled(false);
		    	    		bReplace.setVisible(false);
		    	    			    	    		
		    	    		// Delete object property validation information from object property comment
		    				for (OWLAnnotation anno : obj.getAnnotations(ont, label)) {
		    					// if the comment starts with "#gwaprelation:", it is relevant for the validation
		    					if (anno.getValue().toString().substring(1, 15).equals("#gwaprelation:")) {
		    						ax = df.getOWLAnnotationAssertionAxiom(obj.getIRI(), anno);
		    						man.applyChange(new RemoveAxiom(ont, ax));
		    					}
		    				}
		    				
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
        
        // REPLACE-BOX
        rBox = Box.createHorizontalBox();
	    
	    bReplace = new JButton("LABEL RELATION WITH SUGGESTED OBJECT PROPERTY");
	    bReplace.setVisible(false);
	    // Action Listener is added to button
	    bReplace.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		
	    		// replace "relation" label with a label which is named after the suggested objected property
	    		for (OWLAnnotation anno : obj.getAnnotations(ont)) {
					if (anno.getValue().toString().equals("\"relation\"") || 
							anno.getValue().toString().equals("\"relation\"^^xsd:string")) {
						ax = df.getOWLAnnotationAssertionAxiom(obj.getIRI(), anno);
						man.applyChange(new RemoveAxiom(ont, ax));
											
						comment = df.getOWLAnnotation(df.getRDFSLabel(), df.getOWLLiteral(answer));
						ax = df.getOWLAnnotationAssertionAxiom(obj.getIRI(), comment);
						man.applyChange(new AddAxiom(ont, ax));	
					}
				}
	    		
	    		// update GUI    		
	    		bCancel.setEnabled(false);
	    		bReplace.setEnabled(false);
	    		aStatus.setText("Relation <b>labeled</b>! Please select another object property...");
	    		revalidate();		
	    	}
		});
	    
	    // Expand the preferred width of the button
	    bReplace.setMaximumSize(new Dimension(bReplace.getPreferredSize().width * 2, bReplace.getPreferredSize().height));
	    rBox.add(bReplace);
	    	    
	    // MASTER-BOX
        // between every box there is a filler between 5px and 50px; between box and border the spacing is set to 10px
	    add(Box.createRigidArea(new Dimension(0, 10)));
	    add(dBox);
	    add(Box.createRigidArea(new Dimension(0, 10)));
	    add(reBox);
	    add(Box.createRigidArea(new Dimension(0, 10)));
	    add(aBox); 
	    add(new Box.Filler(new Dimension(0, 5), new Dimension(0, 25), new Dimension(0, 50)));
	    add(doBox);
	    add(new Box.Filler(new Dimension(0, 5), new Dimension(0, 25), new Dimension(0, 50)));
	    add(raBox);   
	    add(new Box.Filler(new Dimension(0, 5), new Dimension(0, 25), new Dimension(0, 50)));
	    add(opBox);
	    add(Box.createRigidArea(new Dimension(0, 5)));
	    add(opMasterBox);
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
	protected OWLObjectProperty updateView(OWLObjectProperty selectedProperty) {
		// the current selected property is needed also in other methods => reference as class attribute
		obj = selectedProperty;
		int[] results;
		int index, answers, max = 0;
		
		// only if there is a selection
		if (obj != null) {
			
			// update the textfields and checkboxes
			fOntologyDomain.setText(GwapValidation.getDomain(obj, ont, label, 5));
			fRelation.setText(getOWLModelManager().getRendering(obj));
			cAllRelations.setSelected(GwapValidation.getCheckBox(obj, ont, label, "all"));
			// only the first domain/range is used
			fDomain.setText("");
			for (OWLClassExpression ce : obj.getDomains(ont)) {
				fDomain.setText(getOWLModelManager().getRendering(ce));
				break;
			}
			fRange.setText("");
			for (OWLClassExpression ce : obj.getRanges(ont)) {
				fRange.setText(getOWLModelManager().getRendering(ce));
				break;
			}
			for (JCheckBox cb : cObjectProperties) {
				cb.setSelected(GwapValidation.getCheckBox(obj, ont, label, cb.getText()));
			}
			fInfo.setText(GwapValidation.getInfo(obj, ont, label, 5));
			rCrowdflower.setSelected(GwapValidation.getCrowdflower(obj, ont, label, 5));
			rQuiz.setSelected(!GwapValidation.getCrowdflower(obj, ont, label, 5));
			if (rQuiz.isSelected()) {
    			bCosts.setEnabled(false);
    		}
			
			// if the relation is not labelled as "relation", no validation can be started
			if (!fRelation.getText().equals("relation")) {
				aStatus.setText("The current relation <b>is not labelled as \"relation\"</b>. Please select another relation to start a relation validation!");
		
				// set editable/enabled for textfields, checkboxes and buttons
				fOntologyDomain.setEditable(false);
				fInfo.setEditable(false);
				rCrowdflower.setEnabled(false);
				rQuiz.setEnabled(false);
				cAllRelations.setEnabled(false);
				for (JCheckBox cb : cObjectProperties) {
			    	cb.setEnabled(false);
			    }
	    		bCosts.setEnabled(false);
				bGo.setEnabled(false);
	    		bCancel.setEnabled(false);
	    		// set replace button non-visible
	    		bReplace.setVisible(false);
				
				// Set status scrollpane visible and refresh the GUI
				sStatus.setVisible(true);
				revalidate();
				
				return obj;
			}		
			
			// if there is already a validation in progress
			if (GwapValidation.isValidating(obj, ont, label, 5) == true) {
				// set editable/enabled for textfields, checkboxes and buttons
				fOntologyDomain.setEditable(false);
				fInfo.setEditable(false);
				rCrowdflower.setEnabled(false);
				rQuiz.setEnabled(false);
				cAllRelations.setEnabled(false);
				for (JCheckBox cb : cObjectProperties) {
			    	cb.setEnabled(false);
			    }
	    		bCosts.setEnabled(false);
				bGo.setEnabled(false);
	    		bCancel.setEnabled(true);
	    		// set replace button non-visible
	    		bReplace.setVisible(false);
	    		
	    		// prepare status messages
	    		String message = "";
	    		message += "Validating relation <b>\"" + fRelation.getText() + "\"</b> between subject <b>\"" + fDomain.getText() +
	    				   "\"</b> and object <b>\"" + fRange.getText() + "\"</b> against the object properties: <b>\"";
	    		answers = 0;
	    		for (JCheckBox cb : cObjectProperties) {
	    			if (cb.isSelected() == true) {
	    				message += cb.getText() + ", "; 
	    				answers++;
	    			}		    	    			
	    		}
	    		message = message.substring(0, message.length() - 2) + "\"</b> in the domain of <b>\"" + fOntologyDomain.getText() + "\"</b>";				    	    		
				if (!fInfo.getText().equals("")) {
					message += " with additional info <b>\"" + fInfo.getText() + "\"</b><br>";
				}
				else {
					message += "<br>";
				}			
	    		
	    		// check if there are already results available from the GWAP
	    		results = gwap.getResults(GwapValidation.getJobID(obj, ont, label, 5));
	    		// set index for result array depending on the task-id
	    		index = (GwapValidation.getTaskID(obj, ont, label, 5) - 1) * (answers + 1);	    		
	    		
	    		if (results != null) {
	    		   			
	    			// write detailed results
	    			message += "Results from uComp API for suggestions for an object property: ";	    					
	    			// go through all selected checkboxes
	    			answer = "";
	    			for (JCheckBox cb : cObjectProperties) {
    					if (cb.isSelected() == true) {
    						// write answer count for current object property
    						message += cb.getText() + ": " + results[index] + ", ";
    						// check if it is the maximum count						
    						if (results[index] > max) {
    							answer = cb.getText();
    							max = results[index];
    						}
    						
    						index += 1;
    					}
    				}
	    			// add default answer
	    			message += "None of those: " + results[index] + "<br>";
	    			
	    			// if "Non of those" has the majority
	    			if (results[index] > max) {
	    				message += "<b>None of those properties can be suggested</b> from uComp API...";
	    			}
	    			
	    			else {    			
		    			// write suggested answer
		    			message += "Suggested object property from uComp API: <b>" + answer + "</b>";
		    			
		    			// set replace button enabled and visible
						bReplace.setEnabled(true);
						bReplace.setVisible(true);
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
				fOntologyDomain.setEditable(true);
				fInfo.setEditable(true);
				rCrowdflower.setEnabled(true);
				rQuiz.setEnabled(true);
				cAllRelations.setEnabled(true);
				for (JCheckBox cb : cObjectProperties) {
			    	cb.setEnabled(true);
			    }
				if (rCrowdflower.isSelected()) {
	    			bCosts.setEnabled(true);
	    		}
				bGo.setEnabled(true);
	    		bCancel.setEnabled(false);
	    		
	    		// reset the status box, make the status box and the replace button non-visible and refresh the GUI
	    		aStatus.setText("");
	    		sStatus.setVisible(false);
	    		bReplace.setVisible(false);
	    		revalidate();
			}
		}
		
		return obj;
	}

	/////////////////////////////////////////////////////////
	// METHOD DISPOSE VIEW								   //
	// abstract method from superclass must be implemented //
	/////////////////////////////////////////////////////////
	public void disposeView() {		
	}
}
