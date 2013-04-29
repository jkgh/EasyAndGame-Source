package com.joanneho.easyandgame.ui.wizards;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class FinishImportPage extends WizardPage {
  private Text text1;
  private Composite container;

  public FinishImportPage() {
    super("Congrats!");
    setTitle("Import AndEngine Finished!");
    setDescription("AndEngine is imported to the workspace!");
  }

  public void createControl(Composite parent) {
    container = new Composite(parent, SWT.NULL);
    GridLayout layout = new GridLayout();
    container.setLayout(layout);
    layout.numColumns = 2;
    Label label1 = new Label(container, SWT.NULL);
    label1.setText("You've finished importing AndEngine!");

    
    
    // Required to avoid an error in the system
    setControl(container);
    setPageComplete(true);

  }

  public String getText1() {
    return text1.getText();
  }
} 