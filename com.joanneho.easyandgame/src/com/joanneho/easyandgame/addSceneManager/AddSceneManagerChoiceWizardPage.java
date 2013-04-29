/*
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.joanneho.easyandgame.addSceneManager;


import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.CodeGeneration;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.joanneho.easyandgame.ui.wizards.IntentReflectionHelper;

/**
 * Wizard page for new android activity.
 * @author Joanne Kuei-Chen Ho
 */
public class AddSceneManagerChoiceWizardPage extends NewTypeWizardPage {

	private static final String PAGE_NAME = "NewTypeWizardPage";
	private static final String SETTINGS_CREATEMAIN = "create_main"; 
	private static final String SETTINGS_CREATECONSTR = "create_constructor"; 
	private static final String SETTINGS_CREATEUNIMPLEMENTED = "create_unimplemented";

	private boolean isOnTemplateWithScene = false;
	private boolean isOnTemplateOnly = false;
	//private boolean isOnMaingameChecked = false;
	private boolean isOnResume = false;
	private boolean isOnPause = false;
	private boolean isOnStop = false;
	private boolean isOnDestroy = false;
	private IJavaProject javaProject = null;
	private Set<String> selectedCategories = null;
	private Set<String> selectedActions = null;
	
	/**
	 * Creates a new {@code ProjectSettingsWizardPage}.
	 * 
	 * @param midletProject
	 *            the project data container
	 */
	public AddSceneManagerChoiceWizardPage() {
		super(true, "ProjectSettingWizardPage");
		setTitle("Please indicate the project you want to develop as an Android game!");
		setDescription("Choose the path to an Android Project folder");
		
	}

	/**
	 * The wizard owning this page is responsible for calling this method with
	 * the current selection. The selection is used to initialize the fields of
	 * the wizard page.
	 * 
	 * @param selection
	 *            used to initialize the fields
	 */
	public void init(IStructuredSelection selection) {
		IJavaElement jelem = getInitialJavaElement(selection);
		javaProject = jelem.getJavaProject();
		initContainerPage(jelem);
		initTypePage(jelem);
		doStatusUpdate();
		IntentReflectionHelper helper = new IntentReflectionHelper(javaProject);
		helper.getCategories();
	}

	// ------ validation --------
	private void doStatusUpdate() {
		// status of all used components
		IStatus[] status = new IStatus[] {
				fContainerStatus, isEnclosingTypeSelected() ? fEnclosingTypeStatus
						: fPackageStatus };
//isEnclosingTypeSelected() ? fEnclosingTypeStatus: fPackageStatus
		// the mode severe status will be displayed and the OK button
		// enabled/disabled.
		updateStatus(status);
	}

	/*
	 * @see NewContainerWizardPage#handleFieldChanged
	 */
	protected void handleFieldChanged(String fieldName) {
		super.handleFieldChanged(fieldName);
		doStatusUpdate();
	}

	public IPath getPackageFromWizardPage(){
		IPackageFragment packageTextString = null;
		packageTextString = getPackageFragment();
		packageTextString.getPath();
		return packageTextString.getPath();
	}
	// ------ UI --------

	/*
	 * @see WizardPage#createControl
	 */
	public void createControl(Composite parent) {
		initializeDialogUnits(parent);

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());
		int nColumns = 4;
		GridLayout layout = new GridLayout();
		layout.numColumns = nColumns;
		composite.setLayout(layout);

		// pick & choose the wanted UI components
		createContainerControls(composite, nColumns);
		createPackageControls(composite, nColumns);
		createSeparator(composite, nColumns);
		createTypeNameControls(composite, nColumns);
		createMethodStubSelectionControls(composite, nColumns);
		//createSuperClassControls();

		// createCommentControls(composite, nColumns);
		setAddComments(true, false);
		enableCommentControl(true);

		setControl(composite);
		Dialog.applyDialogFont(composite);
		
		setTypeName("SceneManager", false);
		
		IntentReflectionHelper helper = new IntentReflectionHelper(javaProject);

	}
	

	/*
	 * @see WizardPage#becomesVisible
	 */
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			setFocus();
		} else {
			IDialogSettings dialogSettings = getDialogSettings();
			if (dialogSettings != null) {
				IDialogSettings section = dialogSettings.getSection(PAGE_NAME);
				if (section == null) {
					section = dialogSettings.addNewSection(PAGE_NAME);
				}
				section.put(SETTINGS_CREATEMAIN, false);
				section.put(SETTINGS_CREATECONSTR, false);
				section.put(SETTINGS_CREATEUNIMPLEMENTED, true);
			}
		}
	}
	private void createMethodStubSelectionControls(Composite composite,
			int nColumns) {

		Label label = new Label(composite, SWT.NONE);
		label.setText("Which one would you like to create?");
		label.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false,
				nColumns, 1));

		Composite methodsComposite = new Composite(composite, SWT.NONE);
		methodsComposite.setFont(composite.getFont());
		GridLayout layout = new GridLayout(2, true);///
		methodsComposite.setLayout(layout);
		methodsComposite.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false, nColumns, 1));



		final Button onTemplateOnly = new Button(methodsComposite, SWT.RADIO);
		onTemplateOnly.setSelection(true);
		onTemplateOnly.setText("Template and Instructions");
		onTemplateOnly.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		onTemplateOnly.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				isOnTemplateOnly = onTemplateOnly.getSelection();
				
			}
		});
		

		final Button onTemplateWithScene = new Button(methodsComposite, SWT.RADIO);
		onTemplateWithScene.setText("Template and Scenes built in: Splash Scene, Menu, and Game Scene");
		onTemplateWithScene.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		onTemplateWithScene.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				isOnTemplateWithScene = onTemplateWithScene.getSelection();
				
			}
		});

		/**final Button onMaingameChecked = new Button(methodsComposite, SWT.CHECK);
		onMaingameChecked.setText("onRestart()");
		onMaingameChecked.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		onMaingameChecked.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				isOnMaingameChecked = onMaingameChecked.getSelection();
			}
		});
		**/

		/***
		final Button onResumeCB = new Button(methodsComposite, SWT.CHECK);
		onResumeCB.setText("onResume()");
		onResumeCB.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		onResumeCB.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				isOnResume = onResumeCB.getSelection();
			}
		});
		
		Button onCreateSceneManagerCB = new Button(methodsComposite, SWT.CHECK);
		onCreateSceneManagerCB.setText("SceneManager\n(easy switch between scenes)");
		onCreateSceneManagerCB.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		onCreateSceneManagerCB.setSelection(true);
		onCreateSceneManagerCB.setEnabled(false);
		
		final Button onPauseCB = new Button(methodsComposite, SWT.CHECK);
		onPauseCB.setText("onPause()");
		onPauseCB.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		onPauseCB.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				isOnPause = onPauseCB.getSelection();
			}
		});

		final Button onStopCB = new Button(methodsComposite, SWT.CHECK);
		onStopCB.setText("onStop()");
		onStopCB.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		onStopCB.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				isOnStop = onStopCB.getSelection();
			}
		});

		final Button onDestroyCB = new Button(methodsComposite, SWT.CHECK);
		onDestroyCB.setText("onDestroy()");
		onDestroyCB.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		onDestroyCB.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				isOnDestroy = onDestroyCB.getSelection();
			}
		});
		
		***/
	}
	
	public int getChoice(){
		int resultChoice=0;
		
		if (isOnTemplateOnly==true && isOnTemplateWithScene==false )//000
			resultChoice=1;
		else if (isOnTemplateOnly==false && isOnTemplateWithScene==true )//001
			resultChoice=2;
		
		
		return resultChoice;
				
	}
	/*
	 * @see NewTypeWizardPage#createTypeMembers
	 */
	protected void createTypeMembers(IType type, ImportsManager imports,
			IProgressMonitor monitor) throws CoreException {
		
		boolean doConstr = false;
		boolean doInherited = true;
		createInheritedMethods(type, doConstr, doInherited, imports,
				new SubProgressMonitor(monitor, 1));


		
		if (monitor != null) {
			monitor.done();
		}
	}

}
