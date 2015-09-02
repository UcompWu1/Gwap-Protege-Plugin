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
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

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

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//CLASS GWAPOBJECTPROPERTYVALIDATION														 				 	  				   //
//Java class for gwap validation for OWL object properties is inherited from default class for OWL Object Property View Components //
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
@SuppressWarnings("serial")
public class GwapJobOverView extends AbstractOWLObjectPropertyViewComponent {

	// GUI Elements
	private Box dBox, pBox, aBox, doBox, raBox, iBox, crBox, gBox, sBox, rBox;
	private JLabel lOntologyDomain, lProperty, lDomain, lRange, lInfo;
	private JTextField fOntologyDomain, fProperty, fDomain, fRange, fInfo;
	private JCheckBox cAllProperties;
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
	private OWLObjectProperty obj;
	private OWLAnnotationProperty label;
	
	// Gwap Communication
	private GwapCommunication gwap;
	
	// Domain/Range to be removed
	private boolean removedomain, removerange;
	private ArrayList<OWLObjectProperty> owlproperties;
	
	private String numberOfJudgementsDesired, paymentPerAssignment; 
	// Instance attributes used in this example
	private	JPanel		topPanel;
	private	JTable		table;
	private	JScrollPane scrollPane;

	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// METHOD INITIALISE VIEW																			 			 //	
	// abstract method from superclass must be implemented, it is called when view is initialised for the first time //
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void initialiseView() throws Exception {
	
		GwapCommunication gwapCommunication = new GwapCommunication();
		// getting the results for a job:
		int[] testResults = gwapCommunication.getResults(754);
		
		
		System.out.println("Establishing connection to the Ucomp API");
		
		
		
		
		
		// initialise OWL Classes
		System.out.println("initializing the job overview now!");

		man   	= getOWLModelManager().getOWLOntologyManager();
		ont   	= getOWLModelManager().getActiveOntology();
		df    	= getOWLModelManager().getOWLDataFactory();
		// define annotation type as RDFS comment
		label 	= df.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_COMMENT.getIRI());
		// initialise Gwap Communication Class
		gwap 	= new GwapCommunication();
		// initialise to be removed toggles
		removedomain = false;
		removerange = false;
		
		// BUILD THE GUI
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setToolTipText("<html>This component validates RDFS domain and range restrictions for <b>object properties</b>.</html>");
		
		
		
		// Create columns names
		String columnNames[] = {"tableId", "ucompJobId", "validationType", "concept", "positive Ratings", "negative Ratings", "" };

		// Create some data
		String dataValues[][] =
		{
			{"1", "12", "234", "67", "1", "2", "remove concept" },
			{"2", "-123", "43", "853","1", "2", "remove concept" },
			{"3", "93", "89.2", "109","1", "2", "remove concept" },
			{"4", "279", "9033", "3092","1", "2", "remove concept" }
		};

		// Create a new table instance
		table = new JTable( dataValues, columnNames );
	    JScrollPane scrollPane = new JScrollPane(table);
		

	    
		
		// ONTOLOGY-DOMAIN-BOX
        dBox = Box.createHorizontalBox();
        lOntologyDomain = new JLabel("In the domain of something awesome!:");
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
	    dBox.add(scrollPane);
	    dBox.add(Box.createRigidArea(new Dimension(10, 0)));
	    //dBox.add(lOntologyDomain);
	    //dBox.add(Box.createRigidArea(new Dimension(10, 0)));
	    //dBox.add(fOntologyDomain);
	    //dBox.add(Box.createRigidArea(new Dimension(10, 0)));
	    
	    // OBJECT-PROPERTY-BOX
        pBox = Box.createHorizontalBox();        
        lProperty = new JLabel("Object Property to be validated:");
        lProperty.setHorizontalAlignment(SwingConstants.CENTER);        
        	    
        fProperty = new JTextField("", 10);
        fProperty.setEditable(false);			    
        fProperty.setColumns((int) (((lProperty.getPreferredSize().width * 2) - 6) / 8));
    
	    lProperty.setMaximumSize(new Dimension(1000, lProperty.getMaximumSize().height));
	    fProperty.setMaximumSize(new Dimension(2000, fProperty.getMaximumSize().height));
	        
	    pBox.add(Box.createRigidArea(new Dimension(10, 0)));
	    pBox.add(lProperty);
	    pBox.add(Box.createRigidArea(new Dimension(10, 0))); 
	    pBox.add(fProperty);
        pBox.add(Box.createRigidArea(new Dimension(10, 0)));
        
	    // ALL-RELATIONS-BOX
	    aBox = Box.createHorizontalBox();
	    aBox.setAlignmentX(CENTER_ALIGNMENT);
	    
	    cAllProperties = new JCheckBox("Validate all object properties");
	    cAllProperties.setHorizontalAlignment(SwingConstants.CENTER);
	    cAllProperties.setEnabled(true);
	    
	    aBox.add(cAllProperties);
        
        // DOMAIN-BOX
        doBox = Box.createHorizontalBox();
        lDomain = new JLabel("Is this the correct rdfs:domain?");
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
	    lRange = new JLabel("Is this the correct rdfs:range?");
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
	    		int drcount = 0, judgements, centspaid;
	    		
	    		// only if there is a selection
	    		if (obj != null) {    			
	    			
	    			// initialise arraylist for all owl-object-properties that are to be validated
	    			owlproperties 	= new ArrayList<OWLObjectProperty>();	    			
    				// if the validate all properties checkbox is selected, start the validation process also for every other object property
		    		if (cAllProperties.isSelected() == true) {	    				    			
	    				for (OWLObjectProperty op : ont.getObjectPropertiesInSignature()) {	    					
    						// only if there isn't already a validation for this property in progress and 
    						// the object property isn't labeled as "relation"
    						if (GwapValidation.isValidating(op, ont, label,  4) == false && 
    								!getOWLModelManager().getRendering(op).equals("relation")) {
    							owlproperties.add(op);				
    						}						    					
	    				}
		    		}
		    		else {
		    			if (GwapValidation.isValidating(obj, ont, label,  4) == false && 
								!getOWLModelManager().getRendering(obj).equals("relation")) {
		    				owlproperties.add(obj);
		    			}
		    		}	    		
		    		// find all domains/ranges for all properties										
					for (OWLObjectProperty op : owlproperties) {
						String temp = "";
						// only the first domain if there is one
						for (OWLClassExpression ce : op.getDomains(ont)) {
							temp = getOWLModelManager().getRendering(ce);
							break;
						}
						if (!temp.equals("")) {
							drcount++;
						}
						// only the first range if there is one
						temp = "";
						for (OWLClassExpression ce : op.getRanges(ont)) {
							temp = getOWLModelManager().getRendering(ce);
							break;
						}
						if (!temp.equals("")) {
							drcount++;
						}
					}
		    		
		    		// get judgements and centspaid setting from API-Settings textfile and calculate and display total costs
		    		try {	    			
		    			judgements =  2;//Integer.valueOf(gwap.getAPISettings().substring(gwap.getAPISettings().indexOf(",") + 1, gwap.getAPISettings().lastIndexOf(",")));
		    			centspaid =  1;//Integer.valueOf(gwap.getAPISettings().substring(gwap.getAPISettings().lastIndexOf(",") + 1));
		    			
		    			
		    			double dollar = ((double) (drcount * judgements * centspaid)) / 100.0;
		    			aStatus.setText("This job contains <b>" + drcount + " units</b>.<br>You have set the number of judgements to <b>" + judgements + 
		    							"</b> and the amount of money paid per task to <b>" + centspaid + " cents</b> per task.<br>" +
		    							"Expected total costs of this job are <b>$" + new DecimalFormat("###,##0.00").format(dollar) + "</b>.");		
		    		}
		    		catch (Exception ex) {
		    			// Write status messages
		    			System.out.println(ex.toString());
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
	    		ArrayList<String> properties;
	    		ArrayList<ArrayList<String>> domainrange = new ArrayList<ArrayList<String>>();
	    		ArrayList<String> currentdomainrange;
	    		boolean crowdflower, notempty = false;
	    		long jobid;
	    		String domain, range;
	    		int i, taskid;
	    		
	    		// only if there is a selection
	    		if (obj != null) {
	    			// only if there was provided a domain
	    			if (!fOntologyDomain.getText().equals("")) {
	    			
	    				// initialise arraylist for all owl-object-properties that are to be validated
		    			owlproperties 	= new ArrayList<OWLObjectProperty>();
		    			properties		= new ArrayList<String>();
		    			
	    				// if the validate all properties checkbox is selected, start the validation process also for every other object property
			    		if (cAllProperties.isSelected() == true) {	    				    			
		    				for (OWLObjectProperty op : ont.getObjectPropertiesInSignature()) {	    					
	    						// only if there isn't already a validation for this property in progress and 
	    						// the object property isn't labeled as "relation"
	    						if (GwapValidation.isValidating(op, ont, label,  4) == false && 
	    								!getOWLModelManager().getRendering(op).equals("relation")) {
	    							owlproperties.add(op);				
	    						}						    					
		    				}
			    		}
			    		else {
			    			if (GwapValidation.isValidating(obj, ont, label,  4) == false && 
    								!getOWLModelManager().getRendering(obj).equals("relation")) {
			    				owlproperties.add(obj);
			    			}
			    		}			    		
			    		
			    		// parse array-list into normal strings for gwapcommunication
			    		for (OWLObjectProperty op : owlproperties) {
			    			properties.add(getOWLModelManager().getRendering(op));
			    		}
			    		
			    		// find all domains/ranges for all properties										
						for (OWLObjectProperty op : owlproperties) {
							currentdomainrange = new ArrayList<String>();
							String temp = "";
							// only the first domain if there is one
							for (OWLClassExpression ce : op.getDomains(ont)) {
								temp = getOWLModelManager().getRendering(ce);
								break;
							}
							if (!temp.equals("")) {
								notempty = true;
							}
							currentdomainrange.add(temp);
							// only the first range if there is one
							temp = "";
							for (OWLClassExpression ce : op.getRanges(ont)) {
								temp = getOWLModelManager().getRendering(ce);
								break;
							}
							if (!temp.equals("")) {
								notempty = true;
							}
							currentdomainrange.add(temp);
							
				    		domainrange.add(currentdomainrange);
						}
						
						if (notempty) {
							// send task to crowdflower or ucomp-quiz?
			    			if (rCrowdflower.isSelected()) {
			    				crowdflower = true;
			    			}
			    			else {
			    				crowdflower = false;
			    			}
			    				    				
			    			// crowdflower has a minimum of 5 units
			    			if (!(crowdflower == true && owlproperties.size() < 5)) {
			    			
				    			// create job
			    				jobid = gwap.createJob(4, crowdflower, fOntologyDomain.getText(), properties, domainrange, null, fInfo.getText(),
			    						numberOfJudgementsDesired, paymentPerAssignment);
	
			    				// if job creation was successful
			    				if (jobid != -1 && jobid != -2) {		    					    					
			    					
			    					// Write property validation information to property comment for every property
			    					i = taskid = 1;
			    					for (OWLObjectProperty op : owlproperties) {
			    						int increment = 0;
			    						if (domainrange.get(i - 1).get(0).equals("")) {
			    							domain = "false";
			    						}
			    						else {
			    							domain = "true";
			    							increment++;
			    						}
			    						if (domainrange.get(i - 1).get(1).equals("")) {
			    							range = "false";
			    						}
			    						else {
			    							range = "true";
			    							increment++;
			    						}
			    						
			    						if (increment != 0)	{
			    							// Syntax: "#gwapproperty:domain=xyz;subject=true;object=false;info=abc;crowdflower=true/false;jobid=123;taskid=1;"
						    	    		comment = df.getOWLAnnotation(df.getRDFSComment(), df.getOWLLiteral("#gwapproperty:domain=" + fOntologyDomain.getText() + 
						    	    									  ";subject=" + domain + ";object=" + range + ";info=" + fInfo.getText() + 
						    	    									  ";crowdflower=" + crowdflower + ";jobid=" + jobid + ";taskid=" + taskid + ";"));
						    	    		ax = df.getOWLAnnotationAssertionAxiom(op.getIRI(), comment);
						    	    		man.applyChange(new AddAxiom(ont, ax));
			    						}
					    	    		
					    	    		// increment counter for taskid depending on the amount of domains/ranges to match the correct position in the result csv later
					    				taskid += increment;
					    				i++;
			    					}
			    					 					
				    	    		// Map domain to ontology via comment in ontology head (remove old domain annotation first)
				    				for (OWLAnnotation anno : ont.getAnnotations()) {
				    					// if the comment starts with "#gwapclass:", it is relevant for the validation
				    					if (anno.getValue().toString().substring(1, 12).equals("#gwapclass:")) {
				    						man.applyChange(new RemoveOntologyAnnotation(ont, anno));
				    					}
				    				}
				    				
				    				// Syntax: "#gwapclass:domain=xyz;crowdflower=true/false"
				    	    		comment = df.getOWLAnnotation(df.getRDFSComment(), df.getOWLLiteral("#gwapclass:domain=" + fOntologyDomain.getText() + 
				    	    									  ";crowdflower=" + crowdflower + ";"));
				    	    		man.applyChange(new AddOntologyAnnotation(ont, comment));
				    				
				    	    		// prepare status messages
				    	    		String message = "";
				    	    		message += "Validating object property <b>\"" + fProperty.getText() + "\"</b> in the domain of <b>\"" + fOntologyDomain.getText() + "\"</b>";
			    					if (!fInfo.getText().equals("")) {
			    						message += " with additional info <b>\"" + fInfo.getText() + "\"</b><br>";
			    					}
			    					else {
			    						message += "<br>";
			    					}
			    					// check if validation for domain/range is needed
				    	    		if (!fDomain.getText().equals("")) {
				    	    			message += "Validating relevance of Subject (Domain) <b>\"" + fDomain.getText() + "\"</b><br>";				    	    		
				    	    		}
				    	    		if (!fRange.getText().equals("")) {
				    	    			message += "Validating relevance of Object (Range) <b>\"" + fRange.getText() + "\"</b><br>";
				    	    		}
			    					message += "<b>Waiting for results</b> from uComp API...please check again later...<br>";
			    							
			    					// Write status messages		    					
				    	    		aStatus.setText(message);			    	    		
			
				    				// Set status scrollpane visible and refresh the GUI (without validate() JEditorPane is not refreshed properly)
				    				sStatus.setVisible(true);
				        			validate();
				        			revalidate();
				        			
			    	    			// set editable/enabled for textfield and buttons
				    	    		fOntologyDomain.setEditable(false);
				    	    		cAllProperties.setEnabled(false);
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
	    					aStatus.setText("<b>No object properties with valid domains/ranges found</b> in current selection. Please select another object property to start a validation!");			    		
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
	    			if (GwapValidation.isValidating(obj, ont, label, 4) == true) {
	    				
	    				// if job cancelation was successful
	    	    		if (gwap.cancelJob(GwapValidation.getJobID(obj, ont, label, 4)) == true) { 
	    				
		    				// set editable/enabled for textfield and buttons
	    	    			fOntologyDomain.setEditable(true);
	    	    			cAllProperties.setEnabled(true);
	    	    			fInfo.setEditable(true);
	    	    			rCrowdflower.setEnabled(true);
	    	    			rQuiz.setEnabled(true);
		    	    		if (rCrowdflower.isSelected()) {
		    	    			bCosts.setEnabled(true);
		    	    		}
	    	    			bGo.setEnabled(true);
		    	    		bCancel.setEnabled(false);
		    	    		
		    	    		// Delete object property validation information from object property comment
		    				for (OWLAnnotation anno : obj.getAnnotations(ont, label)) {
		    					// if the comment starts with "#gwapproperty:", it is relevant for the validation
		    					if (anno.getValue().toString().substring(1, 15).equals("#gwapproperty:")) {
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
        
        // REMOVE-BOX
        rBox = Box.createHorizontalBox();
	    
	    bRemove = new JButton("REMOVE NEGATIVE DOMAIN/RANGE");
	    bRemove.setVisible(false);
	    // Action Listener is added to button
	    bRemove.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {    		
	    		
	    		// if domain/range was validated negatively, only the first domain/range gets deleted
	    		if (removedomain == true) {
	    			for (OWLClassExpression ce : obj.getDomains(ont)) {
		    			ax = df.getOWLObjectPropertyDomainAxiom(obj, ce);
			    		man.applyChange(new RemoveAxiom(ont, ax));
			    		break;
		    		}
	    		}	    		
	    		if (removerange == true) {
	    			for (OWLClassExpression ce : obj.getRanges(ont)) {
		    			ax = df.getOWLObjectPropertyRangeAxiom(obj, ce);		    			
			    		man.applyChange(new RemoveAxiom(ont, ax));
			    		break;
		    		}
	    		}
	    		
	    		// Write information if negatively validated domains/ranges have already been removed to property comment
				// Syntax: "#gwapproperty:removed=true;"
				comment = df.getOWLAnnotation(df.getRDFSComment(), df.getOWLLiteral("#gwapproperty:removed=true;"));
				ax = df.getOWLAnnotationAssertionAxiom(obj.getIRI(), comment);
				man.applyChange(new AddAxiom(ont, ax));	
	    		
	    		// update GUI   		
	    		bCancel.setEnabled(false);
	    		bRemove.setEnabled(false);
	    		aStatus.setText("All negatively validated domains/ranges <b>removed</b>...");
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
	    add(pBox);	    
	    add(Box.createRigidArea(new Dimension(0, 10)));
	    add(aBox);
	    add(new Box.Filler(new Dimension(0, 5), new Dimension(0, 25), new Dimension(0, 50)));
	    add(doBox);
	    add(new Box.Filler(new Dimension(0, 5), new Dimension(0, 25), new Dimension(0, 50)));
	    add(raBox);   
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
		removedomain = false;
		removerange = false;
		int[] results;
		int range, index;
		
		// only if there is a selection
		if (obj != null) {
			// update the textfields
			fOntologyDomain.setText(GwapValidation.getDomain(obj, ont, label, 4));
			fProperty.setText(getOWLModelManager().getRendering(obj));
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
			fInfo.setText(GwapValidation.getInfo(obj, ont, label, 4));
			rCrowdflower.setSelected(GwapValidation.getCrowdflower(obj, ont, label, 4));
			rQuiz.setSelected(!GwapValidation.getCrowdflower(obj, ont, label, 4));
			if (rQuiz.isSelected()) {
    			bCosts.setEnabled(false);
    		}
			
			// if the object property is the topObjectProperty or is labelled as "relation", no validation can be started
			if (fProperty.getText().equals("topObjectProperty") || fProperty.getText().equals("relation")) {
				aStatus.setText("The current Object Property <b>is either the topObjectProperty or labelled as \"relation\"</b>. " +
								"Please select another Object Property to start a validation!");
		
				// set editable/enabled for textfields and buttons
	    		fOntologyDomain.setEditable(false);
	    		cAllProperties.setEnabled(false);
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
	    		
				return obj;
			}
			
			// search through all comments of the selected property
			for (OWLAnnotation anno : obj.getAnnotations(ont, label)) {
				// if there is a comment "#gwapproperty:removed=true;", no results are displayed until the current validation is cancelled
				if (anno.getValue().toString().substring(1, 28).equals("#gwapproperty:removed=true;")) {
					aStatus.setText("All negatively validated domains/ranges have already been <b>removed</b>.<br>" + 
									"Please cancel current validation before starting a new validation!");
					
					// set editable/enabled for textfields and buttons
		    		fOntologyDomain.setEditable(false);
		    		cAllProperties.setEnabled(false);
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
		    		
					return obj;
				}
			}
			
			// if there is already a validation in progress
			if (GwapValidation.isValidating(obj, ont, label, 4) == true) {
				// set editable/enabled for textfield and buttons
	    		fOntologyDomain.setEditable(false);
	    		cAllProperties.setEnabled(false);
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
	    		message += "Validating object property <b>\"" + fProperty.getText() + "\"</b> in the domain of <b>\"" + fOntologyDomain.getText() + "\"</b>";
				if (!fInfo.getText().equals("")) {
					message += " with additional info <b>\"" + fInfo.getText() + "\"</b><br>";
				}
				else {
					message += "<br>";
				}

	    		// check if there are already results available from the GWAP
	    		results = gwap.getResults(GwapValidation.getJobID(obj, ont, label, 4));
	    		// set index for result array depending on the task-id
	    		index = (GwapValidation.getTaskID(obj, ont, label, 4) - 1) * 3;
	    		
	    		if (results != null) {
	    			
	    			// check if validation for domain/range is being validated
		    		if (GwapValidation.getSubject(obj, ont, label).equals("true")) {
		    			
		    			// write detailed results
		    			message += "Validating relevance of Subject (Domain) <b>\"" + fDomain.getText() + "\"</b><br>";	
		    		
		    			// if the validation was positive
		    			if (results[index + 0] > results[index + 1]) {  				
			    			message += "Results from uComp API for relevance of Subject (Domain): <b><u>Yes: " + results[index + 0] + 
			    					   "</u>, No: " + results[index + 1] + ", I don't know: " + results[index + 2] + "</b><br>";
		    				message += "uComp Validation <b><font color=\"green\">POSITIVE</font></b>! The Subject (Domain) \"" + fDomain.getText() +
		    						"\" <b>is relevant</b> for the object property \"" + fProperty.getText() + "\"!";
		    			} 
		    			
		    			else {
		    				// if the validation was negative
		    				if (results[index + 0] < results[index + 1]) {
		    					message += "Results from uComp API for relevance of Subject (Domain): <b>Yes: " + results[index + 0] + 
				    					   ", <u>No: " + results[index + 1] + "</u>, I don't know: " + results[index + 2] + "</b><br>";
		    					message += "uComp Validation <b><font color=\"red\">NEGATIVE</font></b>! The Subject (Domain) \"" + fDomain.getText() +
			    						"\" <b>is not relevant</b> for the object property \"" + fProperty.getText() + "\"!";  					
		    					// set remove button enabled and visible
		    					removedomain = true;
		    					bRemove.setEnabled(true);
		    					bRemove.setVisible(true);
		    				}
		    				// if the validation was undetermined
		    				else {
		    					message += "Results from uComp API for relevance of Subject (Domain): <b>Yes: " + results[index + 0] + 
				    					   ", No: " + results[index + 1] + ", <u>I don't know: " + results[index + 2] + "</u></b><br>";
		    					message += "uComp Validation <b>UNDETERMINED</b>! The Subject (Domain) \"" + fDomain.getText() +
			    						"\" <b>might or might not be relevant</b> for the object property \"" + fProperty.getText() + "\"!"; 
		    				}
		    			}
		    		}
		    		
		    		if (GwapValidation.getObject(obj, ont, label).equals("true")) {
		    			
		    			if (GwapValidation.getSubject(obj, ont, label).equals("false")){		    				
		    				range = 0;		    				
		    			}
		    			else {
		    				range = 3;
		    				message += "<br>";
		    			}
		    			
		    			// write detailed results
		    			message += "Validating relevance of Object (Range) <b>\"" + fRange.getText() + "\"</b><br>";
		    		
		    			// if the validation was positive
		    			if (results[index + range] > results[index + range + 1]) {
			    			message += "Results from uComp API for relevance of Object (Range): <b><u>Yes: " + results[index + range] + 
			    					   "</u>, No: " + results[index + range + 1] + ", I don't know: " + results[index + range + 2] + "</b><br>";
	    					message += "uComp Validation <b><font color=\"green\">POSITIVE</font></b>! The Object (Range) \"" + fRange.getText() +
		    						"\" <b>is relevant</b> for the object property \"" + fProperty.getText() + "\"!"; 
		    			}
		    			
		    			else {
		    				// if the validation was negative
		    				if (results[index + range] < results[index + range + 1]) {
				    			message += "Results from uComp API for relevance of Object (Range): <b>Yes: " + results[index + range] + 
				    					   ", <u>No: " + results[index + range + 1] + "</u>, I don't know: " + results[index + range + 2] + "</b><br>";
		    					message += "uComp Validation <b><font color=\"red\">NEGATIVE</font></b>! The Object (Range) \"" + fRange.getText() +
			    						"\" <b>is not relevant</b> for the object property \"" + fProperty.getText() + "\"!";  					
		    					// set remove button enabled and visible
		    					removerange = true;
		    					bRemove.setEnabled(true);
		    					bRemove.setVisible(true);
		    				}
		    				// if the validation was undetermined
		    				else {
				    			message += "Results from uComp API for relevance of Object (Range): <b>Yes: " + results[index + range] + 
				    					   ", No: " + results[index + range + 1] + ", <u>I don't know: " + results[index + range + 2] + "</u></b><br>";
		    					message += "uComp Validation <b>UNDETERMINED</b>! The Object (Range) \"" + fDomain.getText() +
			    						"\" <b>might or might not be relevant</b> for the object property \"" + fRange.getText() + "\"!";
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
				// set editable/enabled for textfield and buttons
	    		fOntologyDomain.setEditable(true);
	    		cAllProperties.setEnabled(true);
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
	    
		return obj;		
	}

	/////////////////////////////////////////////////////////
	// METHOD DISPOSE VIEW								   //
	// abstract method from superclass must be implemented //
	/////////////////////////////////////////////////////////
	public void disposeView() {		
	}
}
