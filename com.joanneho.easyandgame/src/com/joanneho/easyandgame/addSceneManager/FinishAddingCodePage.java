package com.joanneho.easyandgame.addSceneManager;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class FinishAddingCodePage extends WizardPage {
  private Text text1;
  private Composite container;

  public FinishAddingCodePage() {
    super("Congrats!");
    setTitle("Add a SceneManager to your project");
    setDescription("SceneManager is set to be added to the default package.");
  }

  public void createControl(Composite parent) {
    container = new Composite(parent, SWT.NULL);
    GridLayout layout = new GridLayout();
    container.setLayout(layout);
    layout.numColumns = 2;
    Label label1 = new Label(container, SWT.NULL);
    label1.setText("You've finished adding a SceneManager. \nIf you can't find it, try the default package!\n\n(It won't be added if it's already there.");

    
    
    // Required to avoid an error in the system
    setControl(container);
    setPageComplete(true);

  }

  public String getText1() {
    return text1.getText();
  }
} 