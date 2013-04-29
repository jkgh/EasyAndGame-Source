package com.joanneho.easyandgame.ui.wizards;

/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.internal.runtime.Activator;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.internal.ui.wizards.NewWizardMessages;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.dialogs.IOverwriteQuery;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.internal.ide.IIDEHelpContextIds;
import org.eclipse.ui.internal.wizards.datatransfer.DataTransferMessages;
import org.eclipse.ui.wizards.datatransfer.FileSystemStructureProvider;
import org.eclipse.ui.wizards.datatransfer.ImportOperation;

/**
 * Standard main page for a wizard that creates a project resource from
 * whose location already contains a project.
 * <p>
 * This page may be used by clients as-is; it may be also be subclassed to suit.
 * </p>
 * <p>
 * Example usage:
 * <pre>
 * mainPage = new WizardExternalProjectImportPage("basicNewProjectPage");
 * mainPage.setTitle("Project");
 * mainPage.setDescription("Create a new project resource.");
 * </pre>
 * </p>
 */
public class WizardExternalProjectImportPage extends WizardPage {

    private FileFilter projectFilter = new FileFilter() {
        //Only accept those files that are .project
        public boolean accept(File pathName) {
            return pathName.getName().equals(
                    IProjectDescription.DESCRIPTION_FILE_NAME);
        }
    };

    //Keep track of the directory that we browsed to last time
    //the wizard was invoked.
    private static String previouslyBrowsedDirectory = ""; //$NON-NLS-1$

    // widgets
    private Text projectNameField;

    private Text locationPathField;

    private Button browseButton;

    private IProjectDescription description;

    private Listener locationModifyListener = new Listener() {
        public void handleEvent(Event e) {
            setPageComplete(validatePage());
        }
    };

    // constants
    private static final int SIZING_TEXT_FIELD_WIDTH = 250;

    /**
     * Creates a new project creation wizard page.
     *
     */
    public WizardExternalProjectImportPage() {
        super("wizardExternalProjectPage"); //$NON-NLS-1$
        setPageComplete(false);
        //setTitle(DataTransferMessages.WizardExternalProjectImportPage_title);
        setTitle("Import AndEngine from file system");
        //setDescription(DataTransferMessages.WizardExternalProjectImportPage_description);
        setDescription("Choose the path to AndEngine folder");

    }


    /** (non-Javadoc)
     * Method declared on IDialogPage.
     */
    public void createControl(Composite parent) {

        initializeDialogUnits(parent);

        Composite composite = new Composite(parent, SWT.NULL);

        PlatformUI.getWorkbench().getHelpSystem().setHelp(composite,
                IIDEHelpContextIds.NEW_PROJECT_WIZARD_PAGE);

        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        composite.setFont(parent.getFont());

        createProjectNameGroup(composite);
        createProjectLocationGroup(composite);
        validatePage();
        // Show description on opening
        setErrorMessage(null);
        setMessage(null);
        setControl(composite);
    }

    /**
     * Creates the project location specification controls.
     *
     * @param parent the parent composite
     */
    private final void createProjectLocationGroup(Composite parent) {

        // project specification group
        Composite projectGroup = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        projectGroup.setLayout(layout);
        projectGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        projectGroup.setFont(parent.getFont());

        // new project label
        Label projectContentsLabel = new Label(projectGroup, SWT.NONE);
        projectContentsLabel.setText(DataTransferMessages.WizardExternalProjectImportPage_projectContentsLabel);
        projectContentsLabel.setFont(parent.getFont());

        createUserSpecifiedProjectLocationGroup(projectGroup);
    }

    /**
     * Creates the project name specification controls.
     *
     * @param parent the parent composite
     */
    private final void createProjectNameGroup(Composite parent) {

        Font dialogFont = parent.getFont();

        // project specification group
        Composite projectGroup = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        projectGroup.setFont(dialogFont);
        projectGroup.setLayout(layout);
        projectGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        // new project label
        Label projectLabel = new Label(projectGroup, SWT.NONE);
        projectLabel.setText(DataTransferMessages.WizardExternalProjectImportPage_nameLabel);
        projectLabel.setFont(dialogFont);

        // new project name entry field
        projectNameField = new Text(projectGroup, SWT.BORDER | SWT.READ_ONLY);
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        data.widthHint = SIZING_TEXT_FIELD_WIDTH;
        projectNameField.setLayoutData(data);
        projectNameField.setFont(dialogFont);
        projectNameField.setBackground(parent.getDisplay().getSystemColor(
                SWT.COLOR_WIDGET_BACKGROUND));
    }

    /**
     * Creates the project location specification controls.
     *
     * @param projectGroup the parent composite
     */
    private void createUserSpecifiedProjectLocationGroup(Composite projectGroup) {

        Font dialogFont = projectGroup.getFont();

        // project location entry field
        this.locationPathField = new Text(projectGroup, SWT.BORDER);
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        data.widthHint = SIZING_TEXT_FIELD_WIDTH;
        this.locationPathField.setLayoutData(data);
        this.locationPathField.setFont(dialogFont);

        // browse button
        this.browseButton = new Button(projectGroup, SWT.PUSH);
        this.browseButton.setText(DataTransferMessages.DataTransfer_browse);
        this.browseButton.setFont(dialogFont);
        setButtonLayoutData(this.browseButton);

        this.browseButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                handleLocationBrowseButtonPressed();
            }
        });

        locationPathField.addListener(SWT.Modify, locationModifyListener);
    }

    /**
     * Returns the current project location path as entered by 
     * the user, or its anticipated initial value.
     *
     * @return the project location path, its anticipated initial value, or <code>null</code>
     *   if no project location path is known
     */
    public IPath getLocationPath() {

        return new Path(getProjectLocationFieldValue());
    }

    /**
     * Creates a project resource handle for the current project name field value.
     * <p>
     * This method does not create the project resource; this is the responsibility
     * of <code>IProject::create</code> invoked by the new project resource wizard.
     * </p>
     *
     * @return the new project resource handle
     */
    public IProject getProjectHandle() {
        return ResourcesPlugin.getWorkspace().getRoot().getProject(
                getProjectName());
    }

    /**
     * Returns the current project name as entered by the user, or its anticipated
     * initial value.
     *
     * @return the project name, its anticipated initial value, or <code>null</code>
     *   if no project name is known
     */
    public String getProjectName() {
        return getProjectNameFieldValue();
    }

    /**
     * Returns the value of the project name field
     * with leading and trailing spaces removed.
     * 
     * @return the project name in the field
     */
    private String getProjectNameFieldValue() {
        if (projectNameField == null) {
			return ""; //$NON-NLS-1$
		}

        return projectNameField.getText().trim();
    }

    /**
     * Returns the value of the project location field
     * with leading and trailing spaces removed.
     * 
     * @return the project location directory in the field
     */
    String getProjectLocationFieldValue() {
        return locationPathField.getText().trim();
    }

    /**
     *	Open an appropriate directory browser
     */
    private void handleLocationBrowseButtonPressed() {
        DirectoryDialog dialog = new DirectoryDialog(locationPathField
                .getShell(), SWT.SHEET);
        dialog.setMessage(DataTransferMessages.WizardExternalProjectImportPage_directoryLabel);

        String dirName = getProjectLocationFieldValue();
        if (dirName.length() == 0) {
			dirName = previouslyBrowsedDirectory;
		}

        if (dirName.length() == 0) {
			dialog.setFilterPath(getWorkspace().getRoot().getLocation()
                    .toOSString());
		} else {
            File path = new File(dirName);
            if (path.exists()) {
				dialog.setFilterPath(new Path(dirName).toOSString());
			}
        }

        String selectedDirectory = dialog.open();
        if (selectedDirectory != null) {
            previouslyBrowsedDirectory = selectedDirectory;
            locationPathField.setText(previouslyBrowsedDirectory);
            setProjectName(projectFile(previouslyBrowsedDirectory));
        }
    }

    /**
     * Returns whether this page's controls currently all contain valid 
     * values.
     *
     * @return <code>true</code> if all controls are valid, and
     *   <code>false</code> if at least one is invalid
     */
    private boolean validatePage() {

        String locationFieldContents = getProjectLocationFieldValue();

        if (locationFieldContents.equals("")) { //$NON-NLS-1$
            setErrorMessage(null);
            setMessage(DataTransferMessages.WizardExternalProjectImportPage_projectLocationEmpty);
            return false;
        }

        IPath path = new Path(""); //$NON-NLS-1$
        if (!path.isValidPath(locationFieldContents)) {
            setErrorMessage(DataTransferMessages.WizardExternalProjectImportPage_locationError);
            return false;
        }

        File projectFile = projectFile(locationFieldContents);
        if (projectFile == null) {
            setErrorMessage(NLS.bind(DataTransferMessages.WizardExternalProjectImportPage_notAProject, locationFieldContents));
            return false;
        }
        setProjectName(projectFile);

        if (getProjectHandle().exists()) {
            setErrorMessage(DataTransferMessages.WizardExternalProjectImportPage_projectExistsMessage);
            return false;
        }

        setErrorMessage(null);
        setMessage(null);
        return true;
    }

    private IWorkspace getWorkspace() {
        IWorkspace workspace = IDEWorkbenchPlugin.getPluginWorkspace();
        return workspace;
    }

    /**
     * Return whether or not the specifed location is a prefix
     * of the root.
     */
    private boolean isPrefixOfRoot(IPath locationPath) {
        return Platform.getLocation().isPrefixOf(locationPath);
    }

    /**
     * Set the project name using either the name of the
     * parent of the file or the name entry in the xml for 
     * the file
     */
    private void setProjectName(File projectFile) {

        //If there is no file or the user has already specified forget it
        if (projectFile == null) {
			return;
		}

        IPath path = new Path(projectFile.getPath());

        IProjectDescription newDescription = null;

        try {
            newDescription = getWorkspace().loadProjectDescription(path);
        } catch (CoreException exception) {
            //no good couldn't get the name
        }

        if (newDescription == null) {
            this.description = null;
            this.projectNameField.setText(""); //$NON-NLS-1$
        } else {
            this.description = newDescription;
            this.projectNameField.setText(this.description.getName());
        }
    }

    /**
     * Return a.project file from the specified location.
     * If there isn't one return null.
     */
    public File projectFile(String locationFieldContents) {
        File directory = new File(locationFieldContents);
        if (directory.isFile()) {
			return null;
		}

        File[] files = directory.listFiles(this.projectFilter);
        if (files != null && files.length == 1) {
			return files[0];
		}

        return null;
    }


    /**
     * import a project from file system to workspace and copy the project to workspace as well
     * 
     */
    public void importProjectWorkspace(){
    	
    	MessageConsole console = new MessageConsole("My Console", null);
		console.activate();
		ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[]{ console });
		MessageConsoleStream stream = console.newMessageStream();
		
		
    	IOverwriteQuery overwriteQuery = new IOverwriteQuery() {
            public String queryOverwrite(String file) { return ALL; }
            
    	};

    	stream.println(this.getWorkspace().getRoot().getLocation().toString());
    	//stream.println(ResourcesPlugin.getWorkspace().toString());
    	stream.println(this.getLocationPath().toString());
    	stream.println(this.getProjectLocationFieldValue());
    	
    	//this.getProjectHandle().getWorkspace().getRoot().getFullPath()
    	
    	IWorkspace workspace = this.getWorkspace();
    	IProjectDescription newProjectDescription = workspace.newProjectDescription("AndEngine");
    	IProject newProject = workspace.getRoot().getProject("AndEngine");
    	try {
			newProject.create(newProjectDescription, null);
			newProject.open(null);
		} catch (CoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	//param that will reproduce user > joanne > code > runtime...   this.getWorkspace().getRoot().getLocation()
    	ImportOperation importOperation = new ImportOperation(newProject.getFullPath(),
            new File (this.getProjectLocationFieldValue()) , FileSystemStructureProvider.INSTANCE, overwriteQuery);
	    importOperation.setCreateContainerStructure(false);
	    try {
			importOperation.run(new NullProgressMonitor());
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    /**
    protected InputStream getInitialContents(){
    	String templateFilePath = "";
    	InputStream inputStream = null;
    	try {
    		inputStream = this.getProjectHandle().getProject().getBundle().getEntry().openStream();
    	}catch(IOException e){
    		
    	}
    	return inputStream;
    }
    **/
    /**
    void addSceneManager(){
    	String projectName = projectNameField.getText();
        final IWorkspace workspace = ResourcesPlugin.getWorkspace();
        final IProject project = workspace.getRoot().getProject(projectName);
        
    	IFile file = project.getFile("/templates/SceneManager.java"); // such as file.exists() == false
    	String contents = "Whatever";
    	InputStream source = new ByteArrayInputStream(contents.getBytes());
    	try {
					file.create(source, false, null);
		} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
		}
    }**/

    /**
     * 
     * To import an existing project to workspace 
     * (but not copying project to workspace, just linking it in and put it in Package explorer)
     * 
     * Creates a new project resource with the selected name.
     * <p>
     * In normal usage, this method is invoked after the user has pressed Finish on
     * the wizard; the enablement of the Finish button implies that all controls
     * on the pages currently contain valid values.
     * </p>
     *
     * @return the created project resource, or <code>null</code> if the project
     *    was not created
     */
    IProject createExistingProject() {

        String projectName = projectNameField.getText();
        final IWorkspace workspace = ResourcesPlugin.getWorkspace();
        final IProject project = workspace.getRoot().getProject(projectName);
        if (this.description == null) {
            this.description = workspace.newProjectDescription(projectName);
            IPath locationPath = getLocationPath();
            //If it is under the root use the default location
            if (isPrefixOfRoot(locationPath)) {
				this.description.setLocation(null);
			} else {
				this.description.setLocation(locationPath);
			}
        } else {
			this.description.setName(projectName);
		}

        // create the new project operation
        WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
            protected void execute(IProgressMonitor monitor)
                    throws CoreException {
                monitor.beginTask("", 2000); //$NON-NLS-1$
                project.create(description, new SubProgressMonitor(monitor,
                        1000));
                if (monitor.isCanceled()) {
					throw new OperationCanceledException();
				}
                project.open(IResource.BACKGROUND_REFRESH, new SubProgressMonitor(monitor, 1000));

            }
        };

        // run the new project creation operation
        try {
            getContainer().run(true, true, op);
        } catch (InterruptedException e) {
            return null;
        } catch (InvocationTargetException e) {
            // ie.- one of the steps resulted in a core exception	
            Throwable t = e.getTargetException();
            if (t instanceof CoreException) {
                if (((CoreException) t).getStatus().getCode() == IResourceStatus.CASE_VARIANT_EXISTS) {
                    MessageDialog
                            .open(MessageDialog.ERROR,
                                    getShell(),
                                    DataTransferMessages.WizardExternalProjectImportPage_errorMessage,
                                    NLS.bind(
                                    		DataTransferMessages.WizardExternalProjectImportPage_caseVariantExistsError,
                                    		projectName),
                                    SWT.SHEET
                            );
                } else {
                    ErrorDialog
                            .openError(
                                    getShell(),
                                    DataTransferMessages.WizardExternalProjectImportPage_errorMessage,
                                    null, ((CoreException) t).getStatus());
                }
            }
            return null;
        }

        return project;
    }

    /*
     * see @DialogPage.setVisible(boolean)
     */
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
			this.locationPathField.setFocus();
		}
    }

}
