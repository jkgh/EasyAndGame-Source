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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Made for the ProjectProperties file manipulation. 
 * @author Joanne Kuei-Chen Ho
 *
 */
public class ProjectPropertiesFile {
	
	private IResource resource = null;
	private static final String PROJECTPROPERTIES_FILE = "project.properties";
	
	/**
	 * Constructor.
	 * @param project current project
	 */
	public ProjectPropertiesFile(IProject project) {
		resource = project.findMember(PROJECTPROPERTIES_FILE);
	}
	
	/**
	 * Get the location of the android ProjectProperties file.
	 * @return ProjectProperties location as IPath
	 */
	private IPath getProjectPropertiesLocation() {
		boolean resourceFound = ((resource != null) && ((resource.exists())) && (resource instanceof IFile));
		return resourceFound ? ((IFile) resource).getLocation() : null;
	}

	/**
	 * Update the Android ProjectProperties.
	 * @param activityName name of the new activity
	 * @param actions intent actions
	 * @param categories intent categories
	 * @throws CoreException 
	 */
	private boolean referenceAdded(){
		Scanner s=new Scanner(System.in);
	    boolean containsString=false;
	    System.out.println("Enter string to search :");
	    String searchWrd="android.library.reference.1=../AndEngine";
	    Scanner readFile = null;
		try {
			readFile = new Scanner(new File(getProjectPropertiesLocation().toString()));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//enter appropriate file   location
	    readFile.useDelimiter("\\s+");
	    while(readFile.hasNext())
	        if(searchWrd.equals(readFile.next()))
	        {
	            containsString=true;
	            break;
	        }
	    if(containsString){
	        System.out.println("Contains searched word");
	        return true;
	    }
	    else{ 
	        System.out.println("Doesnot contain searched word");
	        return false;
	    }
	}
	public void update() throws CoreException {
		IPath path = getProjectPropertiesLocation();
		//((IFile) resource).getContents().read()
		
		if (path != null && !referenceAdded()) {
			//AndroidManifest manifest = new AndroidManifest(path.toOSString());
			//manifest.addActivity(activityName, asArray(actions), asArray(categories));
			//manifest.save();
			Writer output;
			try {
				output = new BufferedWriter(new FileWriter(path.toOSString(), true));
				output.append("\nandroid.library.reference.1=../AndEngine\n");
				output.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			resource.refreshLocal(0, null);
		} else if(referenceAdded()){
			Status status = new Status(IStatus.WARNING, "com.joanneho.easyandgame", "Properties file was updated. Referencing AndEngine Library already.");
			throw new CoreException(status);
		}
	
		else {
			
			Status status = new Status(IStatus.ERROR, "com.joanneho.easyandgame", "Could not find Android ProjectProperties file.");
			throw new CoreException(status);
		}
	}
	
	/**
	 * helper method.
	 * @param set set of intents or categories
	 * @return set as array
	 */
	private String[] asArray(Set<String> set) {
		List<String> list = new ArrayList<String>();
		for (String entry : set) {
			list.add(entry);
		}
		return list.toArray(new String[]{});
	}
}
