package at.ac.wu.ucompvalidation;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
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
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLNamedObject;
import org.semanticweb.owlapi.model.OWLObject;
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
public class GwapPropertyDomainVerification extends AbstractOWLObjectPropertyViewComponent {

	// GUI Elements
	private Box currentDomainBox, propertyBox, allPropertiesBox, questionableDomainBox, 
	additionalInfoBox, goldUnitsFileBox, crowdFlowerBox, judgementsAndPriceBox, buttonBox, statusBox, removeBox;
	private JLabel lOntologyDomain, lProperty, lDomain, lInfo, numberOfJudgmentsLabel, paymentAmountLabel;
	private JTextField fOntologyDomain, fProperty, fDomain, fInfo;
	private JCheckBox cAllObjectProperties, cAllDataTypeProperties, goldUnitsCheckBox;
	private JRadioButton rCrowdflower, rQuiz;
	private JButton bCosts, bGo, bCancel, bRemove;
	private JScrollPane sStatus;
	private JEditorPane aStatus, fileStatusText;
	private JComboBox numberOfJudgments, paymentAmount;

	
	// OWL Classes
	private OWLOntologyManager man;
	private OWLOntology ont;
	private OWLDataFactory df;
	private OWLAnnotation comment;
	private OWLAxiom ax;
	private OWLObjectProperty obj;
	
	private OWLDataProperty dataProp;
	
	
	private OWLAnnotationProperty label;
	
	// Gwap Communication
	private GwapCommunication gwap;
	
	// Domain/Range to be removed
	private boolean removedomain, removerange;
	private ArrayList<OWLObjectProperty> owlObjectProperties;
	private ArrayList<OWLDataProperty> owlDataPropertyList;

	
	private String numberOfJudgementsDesired, paymentPerAssignment, selectedGoldUnitsFileName; 


	
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
		// initialise to be removed toggles
		removedomain = false;
		removerange = false;
		
		// BUILD THE GUI
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setToolTipText("<html>This component validates RDFS domain and range restrictions for <b>object properties</b>.</html>");
		
		// ONTOLOGY-DOMAIN-BOX
        currentDomainBox = Box.createHorizontalBox();
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
	    currentDomainBox.add(Box.createRigidArea(new Dimension(10, 0)));
	    currentDomainBox.add(lOntologyDomain);
	    currentDomainBox.add(Box.createRigidArea(new Dimension(10, 0)));
	    currentDomainBox.add(fOntologyDomain);
	    currentDomainBox.add(Box.createRigidArea(new Dimension(10, 0)));
	    
	    // OBJECT-PROPERTY-BOX
        propertyBox = Box.createHorizontalBox();        
        lProperty = new JLabel("Object Property to be validated:");
        lProperty.setHorizontalAlignment(SwingConstants.CENTER);        
        	    
        fProperty = new JTextField("", 10);
        fProperty.setEditable(false);			    
        fProperty.setColumns((int) (((lProperty.getPreferredSize().width * 2) - 6) / 8));
    
	    lProperty.setMaximumSize(new Dimension(1000, lProperty.getMaximumSize().height));
	    fProperty.setMaximumSize(new Dimension(2000, fProperty.getMaximumSize().height));
	        
	    propertyBox.add(Box.createRigidArea(new Dimension(10, 0)));
	    propertyBox.add(lProperty);
	    propertyBox.add(Box.createRigidArea(new Dimension(10, 0))); 
	    propertyBox.add(fProperty);
        propertyBox.add(Box.createRigidArea(new Dimension(10, 0)));
        
	    // ALL-RELATIONS-BOX
	    allPropertiesBox = Box.createHorizontalBox();
	    allPropertiesBox.setAlignmentX(CENTER_ALIGNMENT);
	    
	    cAllObjectProperties = new JCheckBox("Validate all object properties");
	    cAllObjectProperties.setHorizontalAlignment(SwingConstants.CENTER);
	    cAllObjectProperties.setEnabled(true);
	    
	    cAllDataTypeProperties = new JCheckBox("Validate all data properties");
	    cAllDataTypeProperties.setHorizontalAlignment(SwingConstants.CENTER);
	    cAllDataTypeProperties.setEnabled(true);
	    
	    allPropertiesBox.add(cAllObjectProperties);
	    allPropertiesBox.add(cAllDataTypeProperties);
        
        // DOMAIN-BOX
        questionableDomainBox = Box.createHorizontalBox();
        lDomain = new JLabel("Is this the correct domain?");
        lDomain.setHorizontalAlignment(SwingConstants.CENTER);        
        
        fDomain = new JTextField("", 10);
        fDomain.setEditable(false);
        fDomain.setColumns((int) (((lDomain.getPreferredSize().width * 2) - 6) / 8));
	    
	    lDomain.setMaximumSize(new Dimension(1000, lDomain.getMaximumSize().height));
	    fDomain.setMaximumSize(new Dimension(2000, fDomain.getMaximumSize().height));
	    
	    questionableDomainBox.add(Box.createRigidArea(new Dimension(10, 0)));
	    questionableDomainBox.add(lDomain);
	    questionableDomainBox.add(Box.createRigidArea(new Dimension(10, 0)));
	    questionableDomainBox.add(fDomain);
	    questionableDomainBox.add(Box.createRigidArea(new Dimension(10, 0)));
	    
	    fileStatusText = new JEditorPane();
	    fileStatusText.setEditable(false);
	    fileStatusText.setContentType("text/html");
	    fileStatusText.setText("no file selected yet");
	    fileStatusText.setVisible(false);
	    
	    
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
		    	     int option = chooser.showOpenDialog(GwapPropertyDomainVerification.this);
		    	     if (option == JFileChooser.APPROVE_OPTION) {
		    	       File[] sf = chooser.getSelectedFiles();
		    	       String filelist = "nothing";
		    	       if (sf.length > 0) filelist = sf[0].getAbsolutePath();
		    	       for (int i = 1; i < sf.length; i++) {
		    	    	   System.out.println("adding path to filelist: " +  sf[i].getPath());
		    	    	   filelist += ", " + sf[i].getPath();
		    	       }
		    	       String extension = "";
		       	       // checking for the correct file name extension: 
		    	       int i = filelist.lastIndexOf('.');
		    	       int p = Math.max(filelist.lastIndexOf('/'), filelist.lastIndexOf('\\'));

		    	       if (i > p) {
		    	           extension = filelist.substring(i+1);
		    	       }
		    	       System.out.println("You chose " + filelist);
		    	       
		    	       if (extension.equals("csv")){
			    	       fileStatusText.setText("Chosen file:  " +filelist);
			    	       selectedGoldUnitsFileName = filelist;

		    	       }
		    	       else {
			    	       fileStatusText.setText("Please select a valid csv file" +filelist);
		    	       }
		    	       
		    	       
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
	    
	    // GOLD UNIT BOX
	    goldUnitsFileBox = Box.createHorizontalBox();
	    goldUnitsFileBox.setAlignmentX(CENTER_ALIGNMENT);
	    goldUnitsFileBox.add(goldUnitsCheckBox);
	    goldUnitsFileBox.add(fileStatusText);
	    
	    
	    String[] judgementOptions = { "default", "1", "2", "3", "4", "5", "6","7","8","9","10","11","12","13" };
	    String[] paymentOptions = { "default", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16" };
	    
    	//Create the combo box, select item at index 4.
	   numberOfJudgmentsLabel = new JLabel("Number Of Judgements:");
	   numberOfJudgmentsLabel.setHorizontalAlignment(SwingConstants.CENTER);
	   numberOfJudgments = new JComboBox(judgementOptions);
	   numberOfJudgments.setSelectedIndex(0);
	   // using the default value if no other selection is made: 
	   numberOfJudgementsDesired = numberOfJudgments.getSelectedItem().toString();

	   numberOfJudgments.addActionListener(new ActionListener() {
		   public void actionPerformed(ActionEvent e) { 
			   JComboBox cb = (JComboBox)e.getSource();
		       String judgementAmount = (String)cb.getSelectedItem();
		       System.out.println("the number of desired judgements is: "+ judgementAmount);
			   numberOfJudgementsDesired = numberOfJudgments.getSelectedItem().toString();
		   }
	   });
	   
	   paymentAmountLabel = new JLabel("Payment Per Assignment in Cents:");
	   paymentAmountLabel.setHorizontalAlignment(SwingConstants.CENTER);
	   paymentAmount = new JComboBox(paymentOptions);
	   paymentAmount.setSelectedIndex(0);
	   paymentPerAssignment = paymentAmount.getSelectedItem().toString();
	   
	   paymentAmount.addActionListener(new ActionListener() {
		   public void actionPerformed(ActionEvent e) { 
			   JComboBox cb = (JComboBox)e.getSource();
		       String paymentAmountSelected = (String)cb.getSelectedItem();
		       System.out.println("amount of cents paid per task is: "+ paymentAmountSelected);
			   paymentPerAssignment = paymentAmount.getSelectedItem().toString();
		   }
	   });
	   
	     
	    // JUDGEMENT AND PRICE BOX
	    judgementsAndPriceBox = Box.createHorizontalBox();
	    judgementsAndPriceBox.add(Box.createRigidArea(new Dimension(10, 0)));
	    judgementsAndPriceBox.add(numberOfJudgmentsLabel);
	    judgementsAndPriceBox.add(numberOfJudgments);
	    judgementsAndPriceBox.add(paymentAmountLabel);
	    judgementsAndPriceBox.add(paymentAmount);
	    
	    
	    
	    // INFO-BOX
        additionalInfoBox = Box.createHorizontalBox();
	    lInfo = new JLabel("Additional information for validators:");
	    lInfo.setHorizontalAlignment(SwingConstants.CENTER);       
        
	    fInfo = new JTextField("", 10);
	    fInfo.setColumns((int) (((lInfo.getPreferredSize().width * 2) - 6) / 8));
	    fInfo.setToolTipText("You can use this field to give additional information about the task to the crowd workers.");

	    lInfo.setMaximumSize(new Dimension(1000, lInfo.getMaximumSize().height));
	    fInfo.setMaximumSize(new Dimension(2000, fInfo.getMaximumSize().height));	    
	    
	    additionalInfoBox.add(Box.createRigidArea(new Dimension(10, 0)));
	    additionalInfoBox.add(lInfo);
	    additionalInfoBox.add(Box.createRigidArea(new Dimension(10, 0)));
	    additionalInfoBox.add(fInfo);
	    additionalInfoBox.add(Box.createRigidArea(new Dimension(10, 0)));
	    
	    // CROWDFLOWER-BOX
	    crowdFlowerBox = Box.createHorizontalBox();
	    crowdFlowerBox.setAlignmentX(CENTER_ALIGNMENT);	    	    
	    
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
	    
	    crowdFlowerBox.add(rCrowdflower);
	    crowdFlowerBox.add(Box.createRigidArea(new Dimension(30, 0)));
	    crowdFlowerBox.add(rQuiz);
	    
	    // GO-BOX
	    buttonBox = Box.createHorizontalBox();
	    
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
	    			owlObjectProperties 	= new ArrayList<OWLObjectProperty>();
	    			
	    			// now checking out all the data properties
	    			owlDataPropertyList = new ArrayList <OWLDataProperty>();
	    			
	    			if (cAllDataTypeProperties.isSelected() == true) {	    				    			
	    				System.out.println("these are all data properties:"+ ont.getDataPropertiesInSignature());
	    				for (OWLDataProperty dataProperty : ont.getDataPropertiesInSignature()) {	    					
    						System.out.println("checking this data property: "+ dataProperty);
	    					// only if there isn't already a validation for this property in progress and 
    						// the object property isn't labeled as "relation"
    						if (GwapValidation.isValidating(dataProperty, ont, label,  4) == false && 
    								!getOWLModelManager().getRendering(dataProperty).equals("relation")) {
    							owlDataPropertyList.add(dataProperty);				
    						}						    					
	    				}
		    		System.out.println("I found the following data properties:" + owlDataPropertyList);
	    			}
	    			
	    			
	    			// if the validate all properties checkbox is selected, start the validation process also for every other object property
	    			if (cAllObjectProperties.isSelected() == true) {	    				    			
	    				for (OWLObjectProperty op : ont.getObjectPropertiesInSignature()) {	    					
    						// only if there isn't already a validation for this property in progress and 
    						// the object property isn't labeled as "relation"
    						if (GwapValidation.isValidating(op, ont, label,  4) == false && 
    								!getOWLModelManager().getRendering(op).equals("relation")) {
    							owlObjectProperties.add(op);				
    						}						    					
	    				}
		    		}
		    		else {
		    			if (GwapValidation.isValidating(obj, ont, label,  4) == false && 
								!getOWLModelManager().getRendering(obj).equals("relation")) {
		    				owlObjectProperties.add(obj);
		    			}
		    		}	    		
		    		// find all domains/ranges for all object properties										
					for (OWLObjectProperty op : owlObjectProperties) {
						String temp = "";
						// only the first domain if there is one
						for (OWLClassExpression ce : op.getDomains(ont)) {
							temp = getOWLModelManager().getRendering(ce);
							break;
						}
						if (!temp.equals("")) {
							drcount++;
						}
						
						// no more ranges 
						// only the first range if there is one
						/*
						temp = "";
						for (OWLClassExpression ce : op.getRanges(ont)) {
							temp = getOWLModelManager().getRendering(ce);
							break;
						}
						if (!temp.equals("")) {
							drcount++;
						}
						*/
					}
					
		    		// find all domains/ranges for all data properties										
					for (OWLDataProperty owlDataProperty : owlDataPropertyList) {
						String temp = "";
						// only the first domain if there is one
						for (OWLClassExpression ce : owlDataProperty.getDomains(ont)) {
							temp = getOWLModelManager().getRendering(ce);
							break;
						}
						if (!temp.equals("")) {
							drcount++;
						}
						// only the first range if there is one
						/*
						temp = "";
						for (OWLClassExpression ce : owlDataProperty.getRanges(ont) {
							temp = getOWLModelManager().getRendering(ce);
							break;
						}
						if (!temp.equals("")) {
							drcount++;
						}
					*/
					}
		    		
		    		// get judgements and centspaid setting from API-Settings textfile and calculate and display total costs
		    		try {	    					    			
		    			String judgementSelection = numberOfJudgments.getSelectedItem().toString();
		    			if (judgementSelection == "default"){
		    				judgements = 1;
		    			}
		    			else{
			    			judgements =  Integer.parseInt(numberOfJudgments.getSelectedItem().toString());
		    			}
		    			
		    			String centSelection = paymentAmount.getSelectedItem().toString();
		    			if (centSelection == "default"){
		    				centspaid = 1;
		    			}
		    			else {
		    				centspaid = Integer.parseInt(paymentAmount.getSelectedItem().toString());
		    			}
		    			
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
	    		// only if there is a selection
	    		if (obj == null) return;
	    		// only if there was provided a domain
	    		if (fOntologyDomain.getText().equals("")) {
	    			// Write status messages
    				aStatus.setText("Please <b>provide a domain</b> and start the validation again!");
    				// Set status scrollpane visible and refresh the GUI
    				sStatus.setVisible(true);
        			validate();
        			revalidate();
        			return;
	    		}
	    		
	    		ArrayList<OWLNamedObject> objectAndDataProperties = new ArrayList<OWLNamedObject>();
	    		ArrayList<ArrayList<String>> domainrange = new ArrayList<ArrayList<String>>();
	    		long jobid;
	    		int i, taskid;
	    		
				// initialise arraylist for all owl-object-properties that are to be validated
    			owlObjectProperties 	= new ArrayList<OWLObjectProperty>();
    			
				// if the validate all properties checkbox is selected, start the validation process also for every other object property
	    		if (cAllObjectProperties.isSelected() == true) {	    				    			
    				for (OWLObjectProperty op : ont.getObjectPropertiesInSignature()) {	    					
						// only if there isn't already a validation for this property in progress and 
						// the object property isn't labeled as "relation"
						if (GwapValidation.isValidating(op, ont, label,  4) == false && 
								!getOWLModelManager().getRendering(op).equals("relation")) {
							owlObjectProperties.add(op);				
						}						    					
    				}
	    		}
	    		else {
	    			if (GwapValidation.isValidating(obj, ont, label,  4) == false && 
							!getOWLModelManager().getRendering(obj).equals("relation")) {
	    				owlObjectProperties.add(obj);
	    			}
	    		}			    		
	    		
	    		// find all domains/ranges for all object properties										
				for (OWLObjectProperty op : owlObjectProperties) {
					if(!op.getDomains(ont).iterator().hasNext()) continue;
					OWLClassExpression ce = op.getDomains(ont).iterator().next();
					// only the first domain if there is one
					ArrayList<String> currentdomainrange = new ArrayList<String>();
					currentdomainrange.add(getOWLModelManager().getRendering(ce));
					domainrange.add(currentdomainrange);
					objectAndDataProperties.add(op);
				}
				
	    		// find all domains/ranges for all data properties										
				for (OWLDataProperty owlDataProperty : owlDataPropertyList) {
					if(!owlDataProperty.getDomains(ont).iterator().hasNext()) continue;
					OWLClassExpression ce = owlDataProperty.getDomains(ont).iterator().next();
					// only the first domain if there is one
					ArrayList<String> currentdomainrange = new ArrayList<String>();
					currentdomainrange.add(getOWLModelManager().getRendering(ce));
					domainrange.add(currentdomainrange);
					objectAndDataProperties.add(owlDataProperty);
				}
				
				if (objectAndDataProperties.size() > 0) {
					// send task to crowdflower or ucomp-quiz?
					boolean crowdflower = rCrowdflower.isSelected();
	    				    				
	    			// crowdflower has a minimum of 5 units TODO raise this again to 5!
	    			if (crowdflower == true && owlObjectProperties.size() >= 3) {
	    				
	    				ArrayList<String> objectAndDataPropertiesStr = new ArrayList<>();
	    				for (OWLObject op : objectAndDataProperties) {
	    					objectAndDataPropertiesStr.add(getOWLModelManager().getRendering(op));
	    				}
	    			
		    			// create job
	    				jobid = gwap.createJob(4, rCrowdflower.isSelected(), fOntologyDomain.getText(), objectAndDataPropertiesStr, domainrange, null, fInfo.getText(),
	    						numberOfJudgementsDesired, paymentPerAssignment);

	    				// if job creation was successful
	    				if (jobid != -1 && jobid != -2) {		    					    					
	    					
	    					// Write property validation information to property comment for every property
	    					i = taskid = 1;
	    					for (OWLNamedObject op : objectAndDataProperties) {
	    						int increment = 0;
	    						String domain = "false";
	    						if (!domainrange.get(i - 1).get(0).equals("")) {
	    							domain = "true";
	    							increment++;
	    						
	    							// Syntax: "#gwapproperty:domain=xyz;subject=true;object=false;info=abc;crowdflower=true/false;jobid=123;taskid=1;"
				    	    		comment = df.getOWLAnnotation(df.getRDFSComment(), df.getOWLLiteral("#gwapproperty:domain=" + fOntologyDomain.getText() + 
				    	    									  ";subject=" + domain + ";object=false" + ";info=" + fInfo.getText() + 
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
	    					message += "<b>Waiting for results</b> from uComp API...please check again later...<br>";
	    							
	    					// Write status messages		    					
		    	    		aStatus.setText(message);			    	    		
	
		    				// Set status scrollpane visible and refresh the GUI (without validate() JEditorPane is not refreshed properly)
		    				sStatus.setVisible(true);
		        			validate();
		        			revalidate();
		        			
	    	    			// set editable/enabled for textfield and buttons
		    	    		fOntologyDomain.setEditable(false);
		    	    		cAllObjectProperties.setEnabled(false);
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
	    	    			cAllObjectProperties.setEnabled(true);
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
	    
	    buttonBox.add(bCosts);
	    buttonBox.add(Box.createRigidArea(new Dimension(10, 0)));
	    buttonBox.add(bGo);
	    buttonBox.add(Box.createRigidArea(new Dimension(30, 0)));
	    buttonBox.add(bCancel);
	    
	    // STATUS-BOX
	    statusBox = Box.createHorizontalBox();
	    aStatus = new JEditorPane();
	    aStatus.setEditable(false);
	    aStatus.setContentType("text/html");
        sStatus = new JScrollPane(aStatus);
        sStatus.setVisible(false);
        
        statusBox.add(Box.createRigidArea(new Dimension(10, 0)));
        statusBox.add(sStatus);
        statusBox.add(Box.createRigidArea(new Dimension(10, 0)));
        
        // REMOVE-BOX
        removeBox = Box.createHorizontalBox();
	    
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
	    removeBox.add(bRemove);
	    
	    // MASTER-BOX
        // between every box there is a filler between 5px and 50px; between box and border the spacing is set to 10px
	    add(Box.createRigidArea(new Dimension(0, 10)));
	    add(currentDomainBox);
	    add(new Box.Filler(new Dimension(0, 5), new Dimension(0, 25), new Dimension(0, 50)));
	    add(propertyBox);	    
	    add(Box.createRigidArea(new Dimension(0, 10)));
	    add(allPropertiesBox);
	    add(new Box.Filler(new Dimension(0, 5), new Dimension(0, 25), new Dimension(0, 50)));
	    add(questionableDomainBox);
	    add(new Box.Filler(new Dimension(0, 5), new Dimension(0, 25), new Dimension(0, 50)));

	    add(additionalInfoBox);
	    add(new Box.Filler(new Dimension(0, 5), new Dimension(0, 25), new Dimension(0, 50)));
	    add(goldUnitsFileBox);
	    add(Box.createRigidArea(new Dimension(0, 10)));
	    add(judgementsAndPriceBox);
	    add(new Box.Filler(new Dimension(0, 5), new Dimension(0, 25), new Dimension(0, 50)));
	    add(crowdFlowerBox);
	    add(new Box.Filler(new Dimension(0, 5), new Dimension(0, 25), new Dimension(0, 50)));
	    add(buttonBox);
	    add(new Box.Filler(new Dimension(0, 5), new Dimension(0, 25), new Dimension(0, 50)));
	    add(statusBox);
	    add(new Box.Filler(new Dimension(0, 5), new Dimension(0, 25), new Dimension(0, 50)));
	    add(removeBox);
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
	    		cAllObjectProperties.setEnabled(false);
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
		    		cAllObjectProperties.setEnabled(false);
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
				System.out.println("validation is already in progres!");
				// set editable/enabled for textfield and buttons
	    		fOntologyDomain.setEditable(false);
	    		cAllObjectProperties.setEnabled(false);
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
	    		System.out.println("parmeters for the get results method are: ");
	    	    System.out.println("obj: "+ obj + "ont" +ont + "label" + label );
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
		    					
		    			// if the validation was positive
		    			if (results[index + range] > results[index + range + 1]) {
			    			message += "Results from uComp API for relevance of Object (Range): <b><u>Yes: " + results[index + range] + 
			    					   "</u>, No: " + results[index + range + 1] + ", I don't know: " + results[index + range + 2] + "</b><br>";
		    			}
		    			
		    			else {
		    				// if the validation was negative
		    				if (results[index + range] < results[index + range + 1]) {
				    			message += "Results from uComp API for relevance of Object (Range): <b>Yes: " + results[index + range] + 
				    					   ", <u>No: " + results[index + range + 1] + "</u>, I don't know: " + results[index + range + 2] + "</b><br>";				
		    					// set remove button enabled and visible
		    					removerange = true;
		    					bRemove.setEnabled(true);
		    					bRemove.setVisible(true);
		    				}
		    				// if the validation was undetermined
		    				else {
				    			message += "Results from uComp API for relevance of Object (Range): <b>Yes: " + results[index + range] + 
				    					   ", No: " + results[index + range + 1] + ", <u>I don't know: " + results[index + range + 2] + "</u></b><br>";
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
	    		cAllObjectProperties.setEnabled(true);
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
