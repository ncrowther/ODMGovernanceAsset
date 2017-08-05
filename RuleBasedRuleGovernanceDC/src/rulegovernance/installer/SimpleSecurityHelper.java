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
import ilog.rules.teamserver.model.IlrApplicationException;
import ilog.rules.teamserver.model.IlrCommitableObject;
import ilog.rules.teamserver.model.IlrElementDetails;
import ilog.rules.teamserver.model.IlrFolderLockedException;
import ilog.rules.teamserver.model.IlrFrozenBaselineException;
import ilog.rules.teamserver.model.IlrInvalidElementException;
import ilog.rules.teamserver.model.IlrKnownUUIDException;
import ilog.rules.teamserver.model.IlrObjectLockedException;
import ilog.rules.teamserver.model.IlrObjectNotFoundException;
import ilog.rules.teamserver.model.IlrSession;
import ilog.rules.teamserver.model.IlrSessionHelper;
import ilog.rules.teamserver.model.permissions.IlrPermissionException;

import java.util.ArrayList;
import java.util.List;

public class SimpleSecurityHelper {
	
	/**
	 * Enables security for the project main branch.
	 *
	 * @param session The Rule Team Server session.
	 * @param projectName The project.
	 * @param groups Groups that must be attached to the project.
	 */
	public static void enForceSecurity (IlrSession session, String projectName, String[] groups) throws IlrApplicationException, IlrObjectNotFoundException, IlrInvalidElementException, IlrKnownUUIDException, IlrObjectLockedException, IlrFolderLockedException, IlrPermissionException, IlrFrozenBaselineException  {
		// Retrieve the project
		IlrRuleProject project = IlrSessionHelper.getProjectNamed(session, projectName);
		if (project==null) {
			throw new IlrObjectNotFoundException(new IllegalArgumentException(projectName + " is not an existing project"));
		}
		// Create a commitable artifactType. Hold element to commit in the database along
		// with its details and/or a list of its contained elements to add or delete during the commit.
		IlrCommitableObject	cobject = new IlrCommitableObject(project);
		
		// Retrieve its details
		IlrBaseline mainBranch = IlrSessionHelper.getCurrentBaseline(session, project) ;
		// Set the property security to TRUE
		// Set the property Groups to the given list of groups
		mainBranch.setRawValue(session.getBrmPackage().getBranch_SecurityEnforced(), Boolean.TRUE);
		
		List<String> grps = new ArrayList<String> ();
		for (int i = 0; i < groups.length; i++) {
			String groupName = groups[i];
			if (projectName.equals(groupName)) {
				System.out.println("Adding "+groupName+" to the security roles (RTS groups) for the project called "+projectName);
				grps.add(groupName);
			}
		}
		mainBranch.setRawValue(session.getBrmPackage().getBranch_Groups(), grps);
		// Persist in the database
		cobject.addModifiedElement(session.getBrmPackage().getRuleProject_Baselines(), mainBranch) ;
		session.commit(cobject);
	}
	
}
