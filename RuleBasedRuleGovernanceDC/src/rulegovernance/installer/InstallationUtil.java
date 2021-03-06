/*
* Licensed Materials - Property of IBM
* 5725-B69 5655-Y17 5724-Y00 5724-Y17 5655-V84
* Copyright IBM Corp. 1987, 2012. All Rights Reserved.
*
* Note to U.S. Government Users Restricted Rights: 
* Use, duplication or disclosure restricted by GSA ADP Schedule 
* Contract with IBM Corp.
*/
package rulegovernance.installer;

import ilog.rules.teamserver.brm.IlrBaseline;
// @711 import ilog.rules.teamserver.model.IlrBaselineNotCurrentException;
import ilog.rules.teamserver.brm.IlrBrmPackage;
import ilog.rules.teamserver.brm.IlrBusinessRule;
import ilog.rules.teamserver.brm.IlrExtractor;
import ilog.rules.teamserver.brm.IlrQuery;
import ilog.rules.teamserver.brm.IlrRuleProject;
import ilog.rules.teamserver.brm.IlrSmartView;
import ilog.rules.teamserver.model.IlrApplicationException;
import ilog.rules.teamserver.model.IlrCannotDeleteException;
import ilog.rules.teamserver.model.IlrDefaultSearchCriteria;
import ilog.rules.teamserver.model.IlrElementDetails;
import ilog.rules.teamserver.model.IlrFolderLockedException;
import ilog.rules.teamserver.model.IlrFrozenBaselineException;
import ilog.rules.teamserver.model.IlrInvalidElementException;
import ilog.rules.teamserver.model.IlrKnownUUIDException;
import ilog.rules.teamserver.model.IlrModelConstants;
import ilog.rules.teamserver.model.IlrObjectLockedException;
import ilog.rules.teamserver.model.IlrObjectNotFoundException;
import ilog.rules.teamserver.model.IlrSession;
import ilog.rules.teamserver.model.IlrSessionHelper;
import ilog.rules.teamserver.model.impl.IlrElementHandleImpl;
import ilog.rules.teamserver.model.permissions.IlrPermissionException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

public class InstallationUtil {

	/**
	 *
	 * @return The directory name of file permissions dedicated to roles.
	 */
	public static String getRolesPermissionDir () {
		return  InstallConstants.DATA_DIRECTORY + "/" + InstallConstants.PERMISSION_DIR + "/" + InstallConstants.PERMISSION_ROLE_DIR;
	}
	
	/**
	 * Loads file properties, and retains the order of read lines.
	 */
	public static HashMap<String,String> loadSorted (String resource) throws FileNotFoundException, IOException {
		LinkedHashMap<String,String> properties = new LinkedHashMap<String,String> ();	
		BufferedReader bufRdr = null; 
		try {
			File file = new File(resource);
			bufRdr = new BufferedReader(new FileReader(file));

			String str;
			while ((str = bufRdr.readLine()) != null) {
				StringTokenizer st= new StringTokenizer (str,"=");
				if (str.trim().length()==0) continue;
				if (str.trim().startsWith("#")) continue;
				properties.put(st.nextToken().trim(), st.nextToken().trim());
			}
		} finally {
			 if (bufRdr!=null) bufRdr.close();
		}
		return properties;
	}
	
	/**
	 * Creates a smart view name built using the status.
	 * @param status The status value
	 */
	public static String makeSmartViewName (String status) {
		return "'" + status.replaceFirst(status.charAt(0)+"",(status.charAt(0)+"").toUpperCase()) + "' Rules" ;
	}
	
	/**
	 * Removes the smart view.
	 * @param session The session to DC.
	 * @param projectname The project in which the smart view will be created.
	 * @param smview The name of the smart view.
	 * @throws IlrApplicationException 
	 * 
	 * @throws IlrBaselineNotCurrentException
	 */
	public static void removeView (IlrSession session, String projectname, String smview) 
			throws IlrApplicationException { // @711, IlrBaselineNotCurrentException {
		
		System.out.println("Removing Smart View in project called "+projectname+", name= "+smview);
		
		// Get the project
		IlrRuleProject project = IlrSessionHelper.getProjectNamed(session, projectname);
		// Save the working baseline
		IlrBaseline oldBaseline = session.getWorkingBaseline();
		try {
			// Get the current baseline for the project
			IlrBaseline currentBaseline = IlrSessionHelper.getCurrentBaseline(session, project);
			// Set it as the current baseline
			session.setWorkingBaseline(currentBaseline);
			// Retrieve the BRM package
			IlrBrmPackage brm = session.getModelInfo().getBrmPackage();
			// Build the search criteria to retrieve the smart view
			List<Object> features = new ArrayList<Object>();
			// Find 'name' property
			EAttribute name = brm.getModelElement_Name();
			features.add(name);
			// Pass the values
			List<Object> values = new ArrayList<Object>();
			values.add(smview);
			// Create an IlrDefaultSearchCriteria instance
			IlrDefaultSearchCriteria searchCriteria = new IlrDefaultSearchCriteria(brm.getSmartView(), features, values, 
					null, IlrModelConstants.SCOPE_PROJECT, null, true);
			// Execute the find
			List list = session.findElements(searchCriteria);
			// Is it found?
			if ((list!=null) && list.size() > 0) {
				// Smart view found
				IlrSmartView view = (IlrSmartView)list.get(0);
				
				System.out.println("Deleting Smart View called "+view.getName());
				// Delete it!
				session.deleteElement(view);
			}
		} finally {
			// Restore the saved baseline
			session.setWorkingBaseline(oldBaseline);
		}
	}

	/**
	 * Removes the smart view.
	 * @param session The session to DC.
	 * @param projectname The project in which the smart view will be created.
	 * @param smview The name of the smart view.
	 * @throws IlrApplicationException 
	 * 
	 * @throws IlrBaselineNotCurrentException
	 */
	public static void removeQuery (IlrSession session, String projectname, String query) 
			throws IlrApplicationException { // @711, IlrBaselineNotCurrentException {
		
		System.out.println("Removing Query in project called "+projectname+", name= "+query);
		
		// Get the project
		IlrRuleProject project = IlrSessionHelper.getProjectNamed(session, projectname);
		// Save the working baseline
		IlrBaseline oldBaseline = session.getWorkingBaseline();
		try {
			// Get the current baseline for the project
			IlrBaseline currentBaseline = IlrSessionHelper.getCurrentBaseline(session, project);
			// Set it as the current baseline
			session.setWorkingBaseline(currentBaseline);
			// Retrieve the BRM package
			IlrBrmPackage brm = session.getModelInfo().getBrmPackage();
			// Build the search criteria to retrieve the query
			List<Object> features = new ArrayList<Object>();
			// Find 'name' property
			EAttribute name = brm.getModelElement_Name();
			features.add(name);
			// Pass the values
			List<Object> values = new ArrayList<Object>();
			values.add(query);
			// Create an IlrDefaultSearchCriteria instance
			IlrDefaultSearchCriteria searchCriteria = new IlrDefaultSearchCriteria(brm.getQuery(), features, values, 
					null, IlrModelConstants.SCOPE_PROJECT, null, true);
			// Execute the find
			List list = session.findElements(searchCriteria);
			// Is it found?
			if ((list!=null) && list.size() > 0) {
				// Smart view found
				IlrQuery q = (IlrQuery)list.get(0);
				
				System.out.println("Deleting Query called "+q.getName());
				// Delete it!
				session.deleteElement(q);
			}
		} finally {
			// Restore the saved baseline
			session.setWorkingBaseline(oldBaseline);
		}
	}
	
	/**
	 * Removes the element called <code>elementName</code> of type <code>class</code>.
	 * @param session The session to DC.
	 * @param projectname The project in which the element will be created.
	 * @param elementName The name of the element.
	 * @param cl The type of the element
	 * @throws IlrApplicationException 
	 * 
	 * @throws IlrBaselineNotCurrentException
	 */
	public static void removeElement (IlrSession session, String projectname, String elementName, Class cl) 
			throws IlrApplicationException {// @711, IlrBaselineNotCurrentException {
		
		System.out.println("Removing Element in project called "+projectname+", name= "+elementName+", type="+cl.getName());
		
		// Get the project
		IlrRuleProject project = IlrSessionHelper.getProjectNamed(session, projectname);
		// Save the working baseline
		IlrBaseline oldBaseline = session.getWorkingBaseline();
		try {
			// Get the current baseline for the project
			IlrBaseline currentBaseline = IlrSessionHelper.getCurrentBaseline(session, project);
			// Set it as the current baseline
			session.setWorkingBaseline(currentBaseline);
			// Retrieve the BRM package
			IlrBrmPackage brm = session.getModelInfo().getBrmPackage();
			// Build the search criteria to retrieve the query
			List<Object> features = new ArrayList<Object>();
			// Find 'name' property
			EAttribute name = null;
			if (IlrQuery.class.getName().equals(cl.getName())) {
				name = brm.getModelElement_Name();
			} else if (IlrSmartView.class.getName().equals(cl.getName())) {
				name = brm.getModelElement_Name();
			} else if (IlrSmartView.class.getName().equals(cl.getName())) {
				name = brm.getExtractor_Name() ;
			}

			features.add(name);
			// Pass the values
			List<Object> values = new ArrayList<Object>();
			values.add(elementName);
			// Create an IlrDefaultSearchCriteria instance
			IlrDefaultSearchCriteria searchCriteria = null;
						
			if (IlrQuery.class.getName().equals(cl.getName())) {
				searchCriteria = new IlrDefaultSearchCriteria(brm.getQuery(), features, values, 
						null, IlrModelConstants.SCOPE_PROJECT, null, true);
			} else if (IlrSmartView.class.getName().equals(cl.getName())) {
				searchCriteria = new IlrDefaultSearchCriteria(brm.getSmartView(), features, values, 
						null, IlrModelConstants.SCOPE_PROJECT, null, true);
			} else if (IlrExtractor.class.getName().equals(cl.getName())) {
				searchCriteria = new IlrDefaultSearchCriteria(brm.getExtractor(), features, values, 
						null, IlrModelConstants.SCOPE_PROJECT, null, true);
			}
			
			if (searchCriteria == null) return;
					
			// Execute the find
			List list = session.findElements(searchCriteria);
			// Is it found?
			if ((list!=null) && list.size() > 0) {
				// Smart view found
				IlrElementDetails q = (IlrElementDetails)list.get(0);
				
				System.out.println("Deleting Element called "+q.getName());
				// Delete it!
				session.deleteElement(q);
			}
		} finally {
			// Restore the saved baseline
			session.setWorkingBaseline(oldBaseline);
		}
	}
	

	
	/**
	 * Creates a query
	 * @param session The session to DC.
	 * @param projectname The project in which the smart view will be created.
	 * @param smview The name of the smart view.
	 * @param query The smart view query.
	 * @throws IlrApplicationException 
	 * 
	 * @throws IlrBaselineNotCurrentException
	 * 
	 */
	public static void createQuery (IlrSession session, String projectname, String queryName, String queryString) 
			throws IlrApplicationException {
		// @711 IlrBaselineNotCurrentException {
		
		// Remove the query before the creation
		removeElement (session, projectname, queryName, IlrQuery.class);
		// Retrieve the BRM package
		IlrBrmPackage brm = session.getModelInfo().getBrmPackage();
		// Create a new query
		IlrQuery query = IlrSessionHelper.newQuery(session);
		// Retrieve the project
		IlrRuleProject project = IlrSessionHelper.getProjectNamed(session, projectname);
		// Get the query element details
 		IlrElementDetails queryDetails = session.getElementDetailsForThisHandle(query);
 		// Set its property
 		queryDetails.setRawValue(brm.getProjectElement_Project(), project);
 		queryDetails.setRawValue(brm.getModelElement_Name(), queryName);
 		queryDetails.setRawValue(brm.getAbstractQuery_IncludeDependencies(), new Boolean(true));
 		queryDetails.setRawValue(brm.getAbstractQuery_Definition(), queryString);
	    
	    System.out.println("Created Query called "+query.getName()+", on project called "+project.getName()+", using query="+queryString);
	    
	    // Persist in the database
	    session.commit(queryDetails);
	}
	
	/**
	 * Creates a smart view.
	 * @param session The session to DC.
	 * @param projectname The project in which the smart view will be created.
	 * @param smview The name of the smart view.
	 * @param query The smart view query.
	 * 
	 * @throws IlrPermissionException 
	 * @throws IlrObjectNotFoundException 
	 * @throws IlrObjectLockedException 
	 * @throws IlrCannotDeleteException 
	 * @throws IlrFrozenBaselineException
	 * @throws IlrInvalidElementException
	 * @throws IlrFolderLockedException
	 * @throws IlrKnownUUIDException
	 * for JR7.1.1
	 * @throws IlrBaselineNotCurrentException
	 */
	public static void createExtractor (IlrSession session, String projectname, String extractorName, String queryString) 
			throws IlrObjectNotFoundException, IlrInvalidElementException, IlrFolderLockedException, IlrKnownUUIDException, 
			IlrObjectLockedException, IlrPermissionException, IlrFrozenBaselineException, IlrCannotDeleteException { 
		// @711 IlrBaselineNotCurrentException {
		
		// Remove the query before the creation
//		removeElement (session, projectname, extractorName, IlrExtractor.class);
		// Retrieve the BRM package
		/*IlrBrmPackage brm = session.getModelInfo().getBrmPackage();
		// Create a new query
		IlrQuery query = IlrSessionHelper.newQuery(session);
		// Retrieve the project
		IlrRuleProject project = IlrSessionHelper.getProjectNamed(session, projectname);
		// Get the query element details
 		IlrElementDetails queryDetails = session.getElementDetailsForThisHandle(query);
 		// Set its property
 		queryDetails.setRawValue(brm.getProjectElement_Project(), project);
 		queryDetails.setRawValue(brm.getModelElement_Name(), queryName);
 		queryDetails.setRawValue(brm.getAbstractQuery_IncludeDependencies(), new Boolean(true));
 		queryDetails.setRawValue(brm.getAbstractQuery_Definition(), queryString);
	    
	    System.out.println("Created Query called "+query.getName()+", on project called "+project.getName()+", using query="+queryString);
	    
	    // Persist in the database
	    session.commit(queryDetails);*/
	}
}
