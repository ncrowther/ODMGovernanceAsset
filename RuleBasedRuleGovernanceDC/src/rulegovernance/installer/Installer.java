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
import ilog.rules.teamserver.brm.IlrRuleProject;
import ilog.rules.teamserver.client.IlrRemoteSessionFactory;
import ilog.rules.teamserver.model.IlrApplicationException;
import ilog.rules.teamserver.model.IlrConnectException;
import ilog.rules.teamserver.model.IlrSession;
import ilog.rules.teamserver.model.IlrSessionHelper;

import java.io.IOException;

import rulegovernance.model.utils.ApplicationException;

/**
 * The class to set all needed configurations inside Decision Center.
 *
 */
public class Installer {

	/**
	 * Installs the needed configurations for Decision Center.
	 * @throws ApplicationException 
	 *
	 */
	public static void main (String[] args) throws IlrConnectException, IOException, IlrApplicationException, ApplicationException {
		
		String userName = null;
		if ( (args.length > 0) && (args[0] != null)) {
			userName = args[0].trim();
		}
		String password = null;
		if ( (args.length > 1) && (args[1] != null)) {
			password = args[1].trim();
		}
		String serverURL = null;
		if ( (args.length > 2) && (args[2] != null)) {
			serverURL = args[2].trim();     // "http://localhost:9080/teamserver";
		}
		String datasourceName = null;
		if ( (args.length > 3) && (args[3] != null)) {
			datasourceName = args[3].trim(); // "jdbc/ilogDataSource";
		}
		String project  = null;
		if ( (args.length > 4) && (args[4] != null)) {
			project = args[4].trim();     // "SampleRules";
		}
		String command  = null;
		if ( (args.length > 5) && (args[5] != null)) {
			command = args[5].trim();
		}
		String rootdir = null;
		if ( (args.length > 6) && (args[6] != null)) {
			rootdir = args[6].trim();
		}
		String statusValues = null;
		if ( (args.length > 7) && (args[7] != null)) {
			statusValues = args[7].trim();
		}
		String statusProperty = null;
		if ( (args.length > 8) && (args[8] != null)) {
			statusProperty = args[8].trim();
		}
		
		System.out.println("Installer arguments as follows:");
		System.out.println("	userName="+userName);
		System.out.println("	password="+password);
		System.out.println("	serverURL="+serverURL);
		System.out.println("	datasourceName="+datasourceName);
		System.out.println("	project="+project);
		System.out.println("	command="+command);
		System.out.println("	rootdir="+rootdir);
		System.out.println("	statusValues="+statusValues);
		System.out.println("	statusProperty="+statusProperty);
		
		if ("install".equalsIgnoreCase(command) && (args.length == 9)) {
			install (  userName,  password,  serverURL,  datasourceName,  project, rootdir, statusValues, statusProperty);
		}
		if ("hide".equalsIgnoreCase(command) && (args.length == 6)) {
			hide (  userName,  password,  serverURL,  datasourceName,  project);
		}
	}

	/**
	 * @throws ApplicationException 
	 */
	private static void install (String userName, String password, String serverURL, 
			String datasourceName, String project, String rootdir, String statusValues, String statusProperty) throws IlrConnectException, 
			IOException, IlrApplicationException, ApplicationException {
	    // Connect to Rule Team Server
		IlrRemoteSessionFactory factory = new IlrRemoteSessionFactory();
	    factory.connect(userName, password, serverURL, datasourceName);
	    IlrSession session = factory.getSession();
	    session.beginUsage();
	    // init baselines
	    IlrRuleProject proj = IlrSessionHelper.getProjectNamed(session, project);
	    
	    System.out.println("Decision Center Project:" + project  + ": " + proj);
	    
		IlrBaseline current = IlrSessionHelper.getCurrentBaseline(session, proj);
		session.setWorkingBaseline(current);

	    try {
			// Add the groups to Decision Center
	    	String[] groupNames = session.getAvailableGroups();
	    	System.out.println("Decision Center contains groups:");
	    	for (int i=0;i<groupNames.length;i++) {
	    		System.out.println("Group="+groupNames[i]);
	    	}
			// Set the groups for the project
	    	System.out.println("Enforce project security ...");
	    	System.out.println("Assign only the project (security) role to the project ["+project+"]");
	    	SimpleSecurityHelper.enForceSecurity(session, project, groupNames);
			// Load permissions for roles
	    	System.out.println("Setup role based permissions ...");
			SimplePermissionHelper.setPermissionsForRole(session, project, groupNames, rootdir);
			// Generate smart views
			System.out.println("Generate status based smart views ...");
			SimpleSmartViewHelper.generateSmartViews (session, project, statusValues, statusProperty);
			
			// Generate queries
			System.out.println("Generate queries ...");
			SimpleQueryHelper.generateDeploymentQuery(session, project);
			SimpleQueryHelper.defineCrToTestedQuery(session, project);		
			SimpleQueryHelper.defineCrToReviewedQuery(session, project);
			SimpleQueryHelper.findHistoricalCRQuery(session, project);			
					
			// Generate RES artefacts
			//System.out.println("Create rules extractor ...");
			//SimpleRESHelper.generateExtractor(session, project);
			
			System.out.println("Decision Center Rule Based Rule Governance successfully installed on : " + project);
		} finally {
			// Disconnect from Decision Center
		if (session!=null) {
		    session.endUsage();
		    session.close();
		}
		}
	}
	
	/**
	 */
	private static void hide (String userName, String password, String serverURL, 
			String datasourceName, String project) throws IlrConnectException, 
			IOException, IlrApplicationException {
	    // Connect to Rule Team Server
		IlrRemoteSessionFactory factory = new IlrRemoteSessionFactory();
	    factory.connect(userName, password, serverURL, datasourceName);
	    IlrSession session = factory.getSession();
	    session.beginUsage();
	    // init baselines
	    IlrRuleProject proj = IlrSessionHelper.getProjectNamed(session, project);
	    
	    System.out.println("Decision Center Project:" + project  + ": " + proj);
	    
		IlrBaseline current = IlrSessionHelper.getCurrentBaseline(session, proj);
		session.setWorkingBaseline(current);

	    try {
			// Add the groups to Decision Center
	    	String[] groupNames = new String[0];
			// Set the groups for the project
	    	System.out.println("Enforce project security ...");
	    	System.out.println("Assign no (security) role to the project ["+project+"]");
	    	SimpleSecurityHelper.enForceSecurity(session, project, groupNames);

			
			System.out.println("Decision Center Rule Based Rule Governance successfully installed on : " + project);
		} finally {
			// Disconnect from Decision Center
		if (session!=null) {
		    session.endUsage();
		    session.close();
		}
		}
	}

}
