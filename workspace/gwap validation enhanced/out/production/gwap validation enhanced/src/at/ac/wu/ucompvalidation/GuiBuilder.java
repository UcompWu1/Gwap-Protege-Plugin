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

public class GuiBuilder extends AbstractOWLClassViewComponent {
	// GUI Elements
	public Box conceptBox, domainBox, additionalInfoBox, evaluationRadioButtonBox, checkBoxBox;
	public Box buttonBox, statusInfoScrollBox, removeBox;
	public JLabel conceptLabel, domainLabel, infoLabel;
	public JTextField conceptTextField, domainTextField, infoTextField;
	public JCheckBox subtreeCheckBox, cancelCheckBox, goldUnitsCheckBox;
	public JRadioButton crowdflowerRadioBox, ucompQuizRadioBox;
	public JButton calculateCostsButton, goButton, cancelButton, removeButton;
	public JScrollPane statusInfoScrollPanel;
	public JEditorPane statusInfoPanel, fileStatusText;
	public String selectedGoldUnitsFileName = "no file";
	
	
	public 	GuiBuilder(){
		// initializing the GUI objects at object creation
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
	}
	
	private OWLClass selectedClass;
	
	//public GuiBuilder(String GuiString) {
	//	this.GuiString = GuiString;
	//}
	
	public OWLObjectHierarchyProvider<OWLClass> getOntologyHierarchy(){	
		return getOWLModelManager().getOWLHierarchyManager().getOWLClassHierarchyProvider();
	}
	
	
	public void initialiseClassView() throws Exception {
		
	}
	protected OWLClass updateView(OWLClass currentlySelectedClass) {
		selectedClass = currentlySelectedClass;
		return selectedClass;
	}
	
	public void disposeView() {		
	}

	
	
}
