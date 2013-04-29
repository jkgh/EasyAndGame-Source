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
package com.joanneho.easyandgame.newBasegameactivity;


import java.util.Set;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.CodeGeneration;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.templates.DocumentTemplateContext;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.TemplateProposal;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.jdt.ui.*;

import com.joanneho.easyandgame.ui.wizards.ElementListSelector;
import com.joanneho.easyandgame.ui.wizards.IntentReflectionHelper;



/**
 * Wizard page for new android activity.
 * @author Joanne Kuei-Chen Ho
 */
public class NewBaseGameActivityWizardPage extends NewTypeWizardPage {

	//String packageTextString = null;
	private static final String PAGE_NAME = "NewTypeWizardPage";
	private static final String SETTINGS_CREATEMAIN = "create_main"; 
	private static final String SETTINGS_CREATECONSTR = "create_constructor"; 
	private static final String SETTINGS_CREATEUNIMPLEMENTED = "create_unimplemented";

	private boolean isOnBackPressed = false;
	private boolean isOnStart = false;
	private boolean isOnRestart = false;
	private boolean isOnResume = false;
	private boolean isOnPause = false;
	private boolean isOnStop = false;
	private boolean isOnDestroy = false;
	boolean isOnCreateSceneManager = true;
	private IJavaProject javaProject = null;
	private Set<String> selectedCategories = null;
	private Set<String> selectedActions = null;
	
	private Text cameraHeight=null;
	private Text cameraWidth=null;
	String camWidth = "800", camHeight = "480";
	
	
	/**
	 * Creates a new {@code ProjectSettingsWizardPage}.
	 * 
	 * @param midletProject
	 *            the project data container
	 */
	public NewBaseGameActivityWizardPage() {
		super(true, "ProjectSettingWizardPage");
		setTitle("Android Base Game Activity");
		setDescription("Create a new Android Base Game Activity.");
		
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
				fContainerStatus,
				isEnclosingTypeSelected() ? fEnclosingTypeStatus
						: fPackageStatus, fTypeNameStatus, fModifierStatus,
				fSuperClassStatus, fSuperInterfacesStatus };

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
		int nColumns = 5;
		GridLayout layout = new GridLayout();
		layout.numColumns = nColumns;
		composite.setLayout(layout);

		// pick & choose the wanted UI components
		createContainerControls(composite, nColumns);
		createPackageControls(composite, nColumns);
		createSeparator(composite, nColumns);
		createTypeNameControls(composite, nColumns);
		createSuperClassControls(composite, nColumns);
		
		createSeparator(composite, nColumns);
		setSuperClass("org.andengine.ui.activity.BaseGameActivity", true);
		createSuperInterfacesControls(composite, nColumns);
		createMethodStubSelectionControls(composite, nColumns);
		createCameraSizeControls(composite, nColumns);

		//packageTextString = getPackageText();
		// createCommentControls(composite, nColumns);
		setAddComments(true, false);
		enableCommentControl(true);

		setControl(composite);
		Dialog.applyDialogFont(composite);
		
		IntentReflectionHelper helper = new IntentReflectionHelper(javaProject);
		createIntentActionsControl(composite, nColumns, helper.getActions());
		createIntentCategoriesControl(composite, nColumns, helper.getCategories());
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
	private void createCameraSizeControls(Composite composite,
			int nColumns) {

		Label label = new Label(composite, SWT.NONE);
		label.setText("What do you want to put as a default size? ");
		label.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false, nColumns, 1));

		Composite methodsComposite = new Composite(composite, SWT.NONE);
		methodsComposite.setFont(composite.getFont());
		GridLayout layout = new GridLayout(nColumns, true);
		methodsComposite.setLayout(layout);
		methodsComposite.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false, nColumns, 1));
/*
		Label labelCameraHeightWidthQues = new Label(methodsComposite, SWT.NONE);
		labelCameraHeightWidthQues.setText("Screen default size ");
		labelCameraHeightWidthQues.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
	*/	
		Label labelCameraWidth = new Label(methodsComposite, SWT.NONE);
		labelCameraWidth.setText("Width: ");
		labelCameraWidth.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		Text cameraWidth = new Text(methodsComposite, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		cameraWidth.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		cameraWidth.setText("800");
		cameraWidth.addModifyListener(new ModifyListener(){
		      public void modifyText(ModifyEvent event) {
		          // Get the widget whose text was modified
		          Text text = (Text) event.widget;
		          camWidth = text.getText();
		          System.out.println(text.getText());
		        }
		      });
		//camWidth = cameraWidth.getText();
		
		Label labelCameraHeight = new Label(methodsComposite, SWT.NONE);
		labelCameraHeight.setText("Height: ");
		labelCameraHeight.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		Text cameraHeight = new Text(methodsComposite, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		cameraHeight.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		cameraHeight.setText("480");
		cameraHeight.addModifyListener(new ModifyListener(){
		      public void modifyText(ModifyEvent event) {
		          // Get the widget whose text was modified
		          Text text2 = (Text) event.widget;
		          camHeight = text2.getText();
		          System.out.println(text2.getText());
		        }
		      });
		//camHeight = cameraHeight.getText();
		
		
	}


	private void createMethodStubSelectionControls(Composite composite,
			int nColumns) {

		Label label = new Label(composite, SWT.NONE);
		label.setText("Which methods would you like to create?");
		label.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false,
				nColumns, 1));

		Composite methodsComposite = new Composite(composite, SWT.NONE);
		methodsComposite.setFont(composite.getFont());
		GridLayout layout = new GridLayout(nColumns, true);
		methodsComposite.setLayout(layout);
		methodsComposite.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false, nColumns, 1));

		Button onCreateCameraCB = new Button(methodsComposite, SWT.CHECK);
		onCreateCameraCB.setText("Camera\n(capture screen size)");
		onCreateCameraCB.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		onCreateCameraCB.setSelection(true);
		onCreateCameraCB.setEnabled(false);

		final Button onBackPressedCB = new Button(methodsComposite, SWT.CHECK);
		onBackPressedCB.setText("onBackPressed()");
		onBackPressedCB.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		onBackPressedCB.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				isOnBackPressed = onBackPressedCB.getSelection();
				
			}
		});
		

		final Button onStartCB = new Button(methodsComposite, SWT.CHECK);
		onStartCB.setText("onStart()");
		onStartCB.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		onStartCB.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				isOnStart = onStartCB.getSelection();
				
			}
		});

		final Button onRestartCB = new Button(methodsComposite, SWT.CHECK);
		onRestartCB.setText("onRestart()");
		onRestartCB.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		onRestartCB.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				isOnRestart = onRestartCB.getSelection();
			}
		});

		final Button onResumeCB = new Button(methodsComposite, SWT.CHECK);
		onResumeCB.setText("onResume()");
		onResumeCB.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		onResumeCB.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				isOnResume = onResumeCB.getSelection();
			}
		});
		
		final Button onCreateSceneManagerCB = new Button(methodsComposite, SWT.CHECK);
		onCreateSceneManagerCB.setText("SceneManager Template\n(easy switch between scenes)");
		onCreateSceneManagerCB.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		onCreateSceneManagerCB.setSelection(true);
		onCreateSceneManagerCB.setEnabled(true);
		onCreateSceneManagerCB.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				isOnCreateSceneManager = onCreateSceneManagerCB.getSelection();
			}
		});
		
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
	}

	/*
	 * @see NewTypeWizardPage#createTypeMembers
	 */
	protected void createTypeMembers(IType type, ImportsManager imports,
			IProgressMonitor monitor) throws CoreException {
		boolean doOnCreate = true;
		boolean doConstr = false;
		boolean doInherited = true;
		createInheritedMethods(type, doConstr, doInherited, imports,
				new SubProgressMonitor(monitor, 1));

		if (doOnCreate) {
			generateSceneManager(camWidth,camHeight,type, imports);
			
			//String parameterOnCreateEngineOptions [] = {""};
			IMethod methodOnCreateEngineOptions = type.getMethod("onCreateEngineOptions", new String[0]);
			methodOnCreateEngineOptions.delete(false, monitor);
			UpdateOnCreateEngineOptions(type, imports);
			
			String parameterOnCreateResource [] = {"QOnCreateResourcesCallback;"};
			IMethod methodOnCreateResource = type.getMethod("onCreateResources", parameterOnCreateResource);
			methodOnCreateResource.delete(false, monitor);
			UpdateOnCreateResources(type,imports);
			
			String parameterOnCreateScene [] = {"QOnCreateSceneCallback;"};
			IMethod methodOnCreateScene = type.getMethod("onCreateScene", parameterOnCreateScene);
			methodOnCreateScene.delete(false, monitor);
			UpdateOnCreateScene(type,imports);
			
			String parameterOnPopulateScene [] = {"QScene;", "QOnPopulateSceneCallback;"};
			IMethod methodOnPopulateScene = type.getMethod("onPopulateScene", parameterOnPopulateScene);
			methodOnPopulateScene.delete(false, monitor);
			UpdateOnPopulateScene(type,imports);
			
		}
		
		if (isOnStart) {
			generateStub("onStart", type, imports);
		}
		
		if (isOnBackPressed) {
			generateStub("onBackPressed", type, imports);
		}
		
		if (isOnRestart) {
			generateStub("onRestart", type, imports);
		}
		
		if (isOnResume) {
			generateStub("onResume", type, imports);
		}
		if (isOnPause) {
			generateStub("onPause", type, imports);
		}
		
		if (isOnStop) {
			generateStub("onStop", type, imports);
		}
		
		if (isOnDestroy) {
			generateStub("onDestroy", type, imports);
		}
		
		if (monitor != null) {
			monitor.done();
		}
	}
	
	/**
	private void generateInsertion(IType type, ImportsManager imports) throws CoreException, JavaModelException {
		AbstractTextEditor activeEditor = 
		        (AbstractTextEditor) HandlerUtil.getActiveEditor(event);

		ISourceViewer sourceViewer = 
		        (ISourceViewer) activeEditor.getAdapter(ITextOperationTarget.class);

		Point range = sourceViewer.getSelectedRange();

		// You can generate template dynamically here!
		Template template = new Template("sample", 
		        "sample description", 
		        "no-context", 
		        "private void ${name}(){\r\n" + 
		        "\tSystem.out.println(\"${name}\")\r\n"
		        + "}\r\n", true);

		IRegion region = new Region(range.x, range.y);
		TemplateContextType contextType = new TemplateContextType("test");
		TemplateContext ctx =
		    new DocumentTemplateContext(contextType, 
		        sourceViewer.getDocument(), 
		        range.x, 
		        range.y);

		TemplateProposal proposal 
		    = new TemplateProposal(template, ctx, region, null);

		proposal.apply(sourceViewer, (char) 0, 0, 0);
		
	}
	**/
	private void UpdateOnCreateEngineOptions(IType type, ImportsManager imports) throws CoreException, JavaModelException {
		StringBuilder buf = new StringBuilder();
		final String lineDelim = "\n"; // OK, since content is formatted afterwards //$NON-NLS-1$

		
		buf.append("@Override"); //$NON-NLS-1$
		buf.append(lineDelim);
		buf.append("public EngineOptions onCreateEngineOptions() {"); //$NON-NLS-1$
		buf.append(lineDelim);
		buf.append("mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);");
		buf.append(lineDelim);
		buf.append("EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new FillResolutionPolicy(), mCamera);");
		buf.append(lineDelim);
		buf.append("return engineOptions;");
		buf.append(lineDelim);
		buf.append("}"); 
		imports.addImport("org.andengine.engine.options.ScreenOrientation");
		imports.addImport("org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy");
		buf.append(lineDelim);
		
		type.createMethod(buf.toString(), null, true, null);
	}
	private void UpdateOnCreateResources(IType type, ImportsManager imports) throws CoreException, JavaModelException {
		StringBuilder buf = new StringBuilder();
		final String lineDelim = "\n"; // OK, since content is formatted afterwards //$NON-NLS-1$

		buf.append("@Override"); //$NON-NLS-1$
		buf.append(lineDelim);
		buf.append("public void onCreateResources("); //$NON-NLS-1$
		buf.append("			OnCreateResourcesCallback pOnCreateResourcesCallback)"); //$NON-NLS-1$
		buf.append("			throws Exception {"); //$NON-NLS-1$
		buf.append(lineDelim);
		buf.append("sceneManager = new SceneManager(this, mEngine, mCamera);");
		buf.append(lineDelim);
		buf.append("sceneManager.loadSplashSceneResources();");
		buf.append(lineDelim);
		buf.append("pOnCreateResourcesCallback.onCreateResourcesFinished();");
		buf.append(lineDelim);
		buf.append("}"); 
		buf.append(lineDelim);
		
		type.createMethod(buf.toString(), null, true, null);
	}
	private void UpdateOnCreateScene(IType type, ImportsManager imports) throws CoreException, JavaModelException {
		StringBuilder buf = new StringBuilder();
		final String lineDelim = "\n"; // OK, since content is formatted afterwards //$NON-NLS-1$

		buf.append("@Override"); //$NON-NLS-1$
		buf.append(lineDelim);
		buf.append("public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback)"); //$NON-NLS-1$
		buf.append("			throws Exception {"); //$NON-NLS-1$
		buf.append(lineDelim);
		buf.append("pOnCreateSceneCallback.onCreateSceneFinished(sceneManager.createSplashScene());");
		buf.append(lineDelim);
		buf.append("}"); 
		buf.append(lineDelim);
		
		type.createMethod(buf.toString(), null, true, null);
		//type.getType("onCreateResources").createField(buf.toString(), null, true, null);
		
		
	}
	private void UpdateOnPopulateScene(IType type, ImportsManager imports) throws CoreException, JavaModelException {
		StringBuilder buf = new StringBuilder();
		final String lineDelim = "\n"; // OK, since content is formatted afterwards //$NON-NLS-1$

		imports.addImport("org.andengine.engine.handler.timer.ITimerCallback");
		imports.addImport("org.andengine.engine.handler.timer.TimerHandler");
		buf.append("@Override"); //$NON-NLS-1$
		buf.append(lineDelim);
		buf.append("public void onPopulateScene(Scene pScene,"); //$NON-NLS-1$
		buf.append("			OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception {"); //$NON-NLS-1$
		buf.append(lineDelim);
		buf.append("mEngine.registerUpdateHandler(new TimerHandler(1f, new ITimerCallback() {");
		buf.append(lineDelim);
		buf.append("public void onTimePassed(final TimerHandler pTimerHandler){");
		buf.append(lineDelim);
		buf.append("mEngine.unregisterUpdateHandler(pTimerHandler);");
		buf.append(lineDelim);
		buf.append("sceneManager.loadGameSceneResources();");
		buf.append(lineDelim);
		buf.append("sceneManager.createGameScenes();");
		buf.append(lineDelim);
		buf.append("sceneManager.setCurrentScene(SceneManager.SceneType.MENU);}");
		buf.append(lineDelim);
		buf.append("}));"); 
		buf.append(lineDelim);
		buf.append("pOnPopulateSceneCallback.onPopulateSceneFinished();");
		buf.append(lineDelim);
		buf.append("}"); 
		buf.append(lineDelim);
		
		type.createMethod(buf.toString(), null, true, null);
	}
	private void generateSceneManager(String cameraWidthText, String cameraHeightText, IType type, ImportsManager imports) throws CoreException, JavaModelException {
	//private void generateSceneManager(IType type, ImportsManager imports) throws CoreException, JavaModelException {
			StringBuilder buf = new StringBuilder();
			final String lineDelim = "\n"; // OK, since content is formatted afterwards //$NON-NLS-1$
			
			imports.addImport("org.andengine.engine.camera.Camera");
			buf.append("private final int CAMERA_WIDTH = ");
			buf.append(cameraWidthText);
			buf.append(";");
			buf.append(lineDelim);
			buf.append("private final int CAMERA_HEIGHT = ");
			buf.append(cameraHeightText);
			buf.append(";");
			buf.append(lineDelim);
			buf.append("private Camera mCamera;");
			buf.append(lineDelim);
			buf.append("private SceneManager sceneManager;");
			buf.append(lineDelim);			
			type.createField(buf.toString(), null, true, null);


			
			/***
			buf.append("public void onCreate("); //$NON-NLS-1$
			buf.append(imports.addImport("android.os.Bundle")); //$NON-NLS-1$
			buf.append(" savedInstanceState) {"); //$NON-NLS-1$
			buf.append(lineDelim);
			buf.append("super.onCreate(savedInstanceState);");
			buf.append(lineDelim);
			buf.append("}"); 
			buf.append(lineDelim);
			
			type.createMethod(buf.toString(), null, false, null);
			***/
			
		}
	
	//private void generateOnCreate(String cameraWidthText, String cameraHeightText, IType type, ImportsManager imports) throws CoreException, JavaModelException {
	private void generateOnCreate(IType type, ImportsManager imports) throws CoreException, JavaModelException {
		StringBuilder buf = new StringBuilder();
		final String lineDelim = "\n"; // OK, since content is formatted afterwards //$NON-NLS-1$

		
		buf.append("public void onCreate("); //$NON-NLS-1$
		buf.append(imports.addImport("android.os.Bundle")); //$NON-NLS-1$
		buf.append(" savedInstanceState) {"); //$NON-NLS-1$
		buf.append(lineDelim);
		buf.append("super.onCreate(savedInstanceState);");
		buf.append(lineDelim);
		
		final String content = CodeGeneration.getMethodBodyContent(type
				.getCompilationUnit(), type.getTypeQualifiedName('.'),
				"onCreate", false, "", lineDelim); //$NON-NLS-1$ //$NON-NLS-2$
		if (content != null && content.length() != 0)
			buf.append(content);
		buf.append(lineDelim);
		buf.append("}"); //$NON-NLS-1$
		type.createMethod(buf.toString(), null, false, null);
	}
	

	private void generateStub(String method, IType type, ImportsManager imports) throws CoreException, JavaModelException {
		StringBuilder buf = new StringBuilder();
		final String lineDelim = "\n"; // OK, since content is formatted afterwards //$NON-NLS-1$
		buf.append("/* (non-Javadoc)").append(lineDelim);
		buf.append("* @see android.app.Base Game Activity#" + method + "()").append(lineDelim);
		buf.append("*/").append(lineDelim);
		buf.append("@Override");
		buf.append(lineDelim);
		buf.append("public void " + method + "() {"); //$NON-NLS-1$
		buf.append(lineDelim);
		buf.append("super." + method + "();");
		buf.append(lineDelim);
		final String content = CodeGeneration.getMethodBodyContent(type
				.getCompilationUnit(), type.getTypeQualifiedName('.'),
				method, false, "", lineDelim); //$NON-NLS-1$ //$NON-NLS-2$
		if (content != null && content.length() != 0)
			buf.append(content);
		buf.append(lineDelim);
		buf.append("}"); //$NON-NLS-1$
		type.createMethod(buf.toString(), null, false, null);
	}
	
	
	private void createIntentCategoriesControl(final Composite composite, int nColumns, Set<String> categories) {
		GridData gridData = new GridData(SWT.FILL, SWT.TOP, true, false, nColumns, 1);
		ElementListSelector selector = new ElementListSelector(composite, gridData, "Intent categories (Default: android.intent.category.LAUNCHER)", "Select Intent categories", categories.toArray(), 1);
		selectedCategories = selector.getSelectedElements();
	}
	
	private void createIntentActionsControl(final Composite composite, int nColumns, Set<String> actions) {
		GridData gridData = new GridData(SWT.FILL, SWT.TOP, true, false, nColumns, 1);
		ElementListSelector selector = new ElementListSelector(composite, gridData, "Intent actions (Default: android.intent.action.MAIN)", "Select Intent actions", actions.toArray(), 2);
		selectedActions = selector.getSelectedElements();
	}
	
	/**
	 * Get intent categories
	 * @return selected Intent categories
	 */
	public Set<String> getSelectedCategories() {
		return selectedCategories;
	}

	/**
	 * Get intent actions
	 * @return selected Intent actions
	 */
	public Set<String> getSelectedActions() {
		return selectedActions;
	}
}
