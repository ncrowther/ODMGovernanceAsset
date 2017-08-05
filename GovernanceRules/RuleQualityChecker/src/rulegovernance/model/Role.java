/*
* Licensed Materials - Property of IBM
* 5725-B69 5655-Y17 5724-Y00 5724-Y17 5655-V84
* Copyright IBM Corp. 1987, 2012. All Rights Reserved.
*
* Note to U.S. Government Users Restricted Rights: 
* Use, duplication or disclosure restricted by GSA ADP Schedule 
* Contract with IBM Corp.
*/
package rulegovernance.model;

import ilog.rules.teamserver.model.IlrSession;
import rulegovernance.model.utils.Constants;

public class Role {
	/**
	 * Tests whether the user has the administrator role.
	 */
	public static boolean IsRtsAdministrator(IlrSession session) {
		return session.isUserInRole(Constants.RTS_ADMIN_ROLE);
	}

}
