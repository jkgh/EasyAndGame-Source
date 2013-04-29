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
package com.joanneho.easyandgame.ui.wizards;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.internal.ui.wizards.NewElementWizard;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.joanneho.easyandgame.addSceneManager.AddSceneManagerChoiceWizardPage;
import com.joanneho.easyandgame.addSceneManager.FinishAddingCodePage;
import com.joanneho.easyandgame.ui.wizards.AndroidManifestFile;
import com.joanneho.easyandgame.ui.wizards.FinishImportPage;
import com.joanneho.easyandgame.ui.wizards.ImageConstants;
import com.joanneho.easyandgame.ui.wizards.ProjectPropertiesFile;
import com.joanneho.easyandgame.ui.wizards.WizardExternalProjectImportPage;


/**
 * Wizard class for creating a new Base Game Activity
 * @author Joanne Kuei-Chen Ho
 */
public class UpdateSceneManagerWizard extends NewElementWizard {

    /**
     * Wizard id.
     */
    public static final String ID = "com.joanneho.easyandgame.ui.wizard.NewBaseGameActivityWizard";

	private UpdateSceneManagerWizardPage updateSceneManagerWizardPage = null;
    
	
    WizardExternalProjectImportPage externalImportPage;
    FinishImportPage finishImportPage;


    /**
     * Creates a new android activity project wizard.
     */
    public UpdateSceneManagerWizard() {
    	
    	setWindowTitle("New Android Base Game Activity");
        setDefaultPageImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(ImageConstants.ID,
                ImageConstants.LARGE_ACTIVITY_ICON));
        updateSceneManagerWizardPage = new UpdateSceneManagerWizardPage();
        externalImportPage = new WizardExternalProjectImportPage();
        //externalImportPage.setTitle("Import AndEngine to the workspace");
        //externalImportPage.setDescription("Download AndEngine file and extract it. Browse your file system and choose the AndEngine folder ");
        finishImportPage = new FinishImportPage();
        
        
        System.out.println("ENTER!!!!!!!!!!!!");
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.internal.ui.wizards.NewElementWizard#performFinish()
     */
    @Override
    public boolean performFinish() {
    	//ProjectPropertiesFile projectProperties = new ProjectPropertiesFile(updateBaseGameActivityPage.getJavaProject().getProject());

    	
    	boolean res= super.performFinish();    	
    	if (!ResourcesPlugin.getWorkspace().getRoot().getProject("AndEngine").exists())
    		externalImportPage.importProjectWorkspace();

    	//externalImportPage.addSceneManager();
		if (res) {
			IResource resource= updateSceneManagerWizardPage.getModifiedResource();
			//IResource resource_SceneManager = updateCodeProjectChoisePage.getModifiedResource();
			
			if (resource != null) {
				// update manifest
		    	UpdateSceneManager_AddScene updateSceneManager_addScene = new UpdateSceneManager_AddScene(updateSceneManagerWizardPage.getJavaProject().getProject());
		    	try {
		    		updateSceneManager_addScene.update();
				} catch (CoreException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				openResource((IFile) resource);
			}
		}
		return res;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    @Override
	public void addPages() {
        addPage(updateSceneManagerWizardPage);
        if (!ResourcesPlugin.getWorkspace().getRoot().getProject("AndEngine").exists())
        	addPage(externalImportPage);
        addPage(finishImportPage);

        
    }
    
   /**
    public static void addSceneManager(IProject project, IResource resource) throws IOException, CoreException {
        String templateFilePath = "/templates/SceneManager.java";
        InputStream inputStream = null;
        inputStream = Platform.getBundle("com.joanneho.eclipse.adt.extensions").getEntry(templateFilePath).openStream();
        
        IFolder folder = project.getFolder("/src");
        
        
        IFile file = folder.getFile("SceneManager.java");
        //IFile file = (IFile) resource;
        file.create(inputStream, false, null); // false means don't create the file if the file already exists
    }
    **/

    /* (non-Javadoc)
     * @see org.eclipse.jdt.internal.ui.wizards.NewElementWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
     */
    @Override
	 public void init(IWorkbench workbench, IStructuredSelection selection) {
    	updateSceneManagerWizardPage.init(selection);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.Wizard#canFinish()
     */
    @Override
	public boolean canFinish() {
        // only allow the user to finish if the current page is the last page.
        return super.canFinish() && getContainer().getCurrentPage() == updateSceneManagerWizardPage;
    }

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.wizards.NewElementWizard#finishPage(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected void finishPage(IProgressMonitor monitor)
			throws InterruptedException, CoreException {
		//updateSceneManagerWizardPage.createType(monitor); 

	        
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.wizards.NewElementWizard#getCreatedElement()
	 */
	@Override
	public IJavaElement getCreatedElement() {
		return updateSceneManagerWizardPage.getCreatedType();
	}
	
}
