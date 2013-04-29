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

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.ui.wizards.NewElementWizard;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;

import com.joanneho.easyandgame.ui.wizards.ImageConstants;


/**
 * Wizard class for creating a new Base Game Activity
 * @author Joanne Kuei-Chen Ho
 */
public class AddSceneManagerWizard extends NewElementWizard {

    /**
     * Wizard id.
     */
    public static final String ID = "com.joanneho.easyandgame.ui.wizard.ImportAndEngineWizard";

    AddSceneManagerChoiceWizardPage addSceneManagerChoisePage;

    FinishAddingCodePage finishPage;
    static IPath packageStringText = null;

    /**
     * Creates a new android activity project wizard.
     */
    public AddSceneManagerWizard() {
        setWindowTitle("Add a SceneManager to your project");
        setDefaultPageImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(ImageConstants.ID,
                ImageConstants.LARGE_ACTIVITY_ICON));
        addSceneManagerChoisePage = new AddSceneManagerChoiceWizardPage();
        addSceneManagerChoisePage.setTitle("Add a SceneManger to your project!");
        addSceneManagerChoisePage.setDescription("SceneManager is a must-have if you want to devlop a multi-screen game");
        
        finishPage = new FinishAddingCodePage();
        System.out.println("ENTER!!!!!!!!!!!!");
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.internal.ui.wizards.NewElementWizard#performFinish()
     */
    @Override
    public boolean performFinish() {
    	boolean res= super.performFinish();    	

    	int choiceOfSceneContent =0;
    	if (res){
    		IResource resource = addSceneManagerChoisePage.getModifiedResource();
    		if (resource != null) {
    			try {
    				choiceOfSceneContent = addSceneManagerChoisePage.getChoice();
    				packageStringText = addSceneManagerChoisePage.getPackageFromWizardPage();
    				addSceneManager (addSceneManagerChoisePage.getJavaProject().getProject(), resource, choiceOfSceneContent);
    				
    			}catch (IOException e1) {
    				// TODO Auto-generated catch block
    				e1.printStackTrace();
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
        addPage(addSceneManagerChoisePage);
        addPage(finishPage);
        
    }
    public static void addSceneManager(IProject project, IResource resource, int choiceOfSceneContent) throws IOException, CoreException {
        String templateFilePath = "/templates/SceneManager.java";
        if(choiceOfSceneContent == 1)
        	templateFilePath = "/templates/SceneManager.java";
        else// if (choiceOfSceneContent==2)
        	templateFilePath = "/templates/SceneManager_splash_menu_maingame.java";
        //Bundle bundle
        InputStream inputStream = null;
        
        
        
        System.out.println("bundle: " +  Platform.getBundle("com.joanneho.easyandgame"));
        
        inputStream = Platform.getBundle("com.joanneho.easyandgame").getEntry(templateFilePath).openStream();
        //inputStream = Platform.getBundle().getEntry(templateFilePath).openStream();
        IFolder folder = project.getFolder("/src").getFolder(packageStringText.removeFirstSegments(2));
        //IFile file = folder.getFile("SceneManager.java");
        IFile file = (IFile) resource;
        
        //Path path = new Path(Platform.resolve(Platform.find(plugin.getBundle(), new Path(filePath))).getFile()).toFile().toString();
        
        
        
        
        file.create(inputStream, false, null); // false means don't create the file if the file already exists
    } 
    /* (non-Javadoc)
     * @see org.eclipse.jdt.internal.ui.wizards.NewElementWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
     */
    @Override
	 public void init(IWorkbench workbench, IStructuredSelection selection) {
    	addSceneManagerChoisePage.init(selection);
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
