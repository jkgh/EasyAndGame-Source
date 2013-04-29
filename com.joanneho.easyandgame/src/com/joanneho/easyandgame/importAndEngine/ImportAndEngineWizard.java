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
package com.joanneho.easyandgame.importAndEngine;

import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.ui.wizards.NewElementWizard;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.joanneho.easyandgame.ui.wizards.FinishImportPage;
import com.joanneho.easyandgame.ui.wizards.ImageConstants;
import com.joanneho.easyandgame.ui.wizards.ProjectPropertiesFile;
import com.joanneho.easyandgame.ui.wizards.WizardExternalProjectImportPage;


/**
 * Wizard class for creating a new Base Game Activity
 * @author Joanne Kuei-Chen Ho
 */
public class ImportAndEngineWizard extends NewElementWizard {

    /**
     * Wizard id.
     */
    public static final String ID = "com.joanneho.easyandgame.ui.wizard.ImportAndEngineWizard";

    ImportAndEngineProjectChoisePage projectChoisePage;
    WizardExternalProjectImportPage externalImportPage;
    FinishImportPage finishPage;

    /**
     * Creates a new android activity project wizard.
     */
    public ImportAndEngineWizard() {
        setWindowTitle("Import AndEngine to Workspace as a Library");
        setDefaultPageImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(ImageConstants.ID,
                ImageConstants.LARGE_ACTIVITY_ICON));
        
        externalImportPage = new WizardExternalProjectImportPage();
        projectChoisePage = new ImportAndEngineProjectChoisePage();
        //externalImportPage.setTitle("Import AndEngine to the workspace");
        //externalImportPage.setDescription("Download AndEngine file and extract it. Browse your file system and choose the AndEngine folder ");
        finishPage = new FinishImportPage();
        System.out.println("ENTER!!!!!!!!!!!!");
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.internal.ui.wizards.NewElementWizard#performFinish()
     */
    @Override
    public boolean performFinish() {
    	boolean res= super.performFinish();    	
    	if (!ResourcesPlugin.getWorkspace().getRoot().getProject("AndEngine").exists())
    		externalImportPage.importProjectWorkspace();
    	if (res){
    		IResource resource = projectChoisePage.getModifiedResource();
    		if(resource!=null){
				Set<String> selectedActions = projectChoisePage.getSelectedActions(); 
				Set<String> selectedCategories = projectChoisePage.getSelectedCategories();
				String activityName = projectChoisePage.getTypeName();
				IJavaProject javaProject = projectChoisePage.getJavaProject();
				ProjectPropertiesFile projectProperties = new ProjectPropertiesFile(javaProject.getProject());
				try {
					
					projectProperties.update();
				} catch (CoreException e) {
					Status status = new Status(IStatus.ERROR, "com.joanneho.easyandgame", e.getMessage());
					ErrorDialog.openError(getShell(), "Error when updating manifest", e.getMessage(), status);
				}
    		}
    	}
		return res;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    @Override
	public void addPages() {
        addPage(projectChoisePage);
        if (!ResourcesPlugin.getWorkspace().getRoot().getProject("AndEngine").exists())
        	addPage(externalImportPage);
        addPage(finishPage);
        
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.internal.ui.wizards.NewElementWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
     */
    @Override
	 public void init(IWorkbench workbench, IStructuredSelection selection) {
    	projectChoisePage.init(selection);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.Wizard#canFinish()
     */
    @Override
	public boolean canFinish() {
        // only allow the user to finish if the current page is the last page.
        return super.canFinish() && getContainer().getCurrentPage() == finishPage;
    }

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.wizards.NewElementWizard#finishPage(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected void finishPage(IProgressMonitor monitor)
			throws InterruptedException, CoreException {
		

	        
	}

	@Override
	public IJavaElement getCreatedElement() {
		// TODO Auto-generated method stub
		return null;
	}

}
