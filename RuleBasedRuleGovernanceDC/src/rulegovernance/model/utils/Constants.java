/*
* Licensed Materials - Property of IBM
* 5725-B69 5655-Y17 5724-Y00 5724-Y17 5655-V84
* Copyright IBM Corp. 1987, 2012. All Rights Reserved.
*
* Note to U.S. Government Users Restricted Rights: 
* Use, duplication or disclosure restricted by GSA ADP Schedule 
* Contract with IBM Corp.
*/
package rulegovernance.model.utils;

import ilog.rules.teamserver.model.permissions.IlrPermissionConstants;

/**
 * An interface having all constants used by the sample.
 *
 */
public interface Constants {
	/**
	 * The administrator role.
	 */
	public static String RTS_ADMIN_ROLE = IlrPermissionConstants.ADMINISTRATOR_ROLE;
	
	/**
	 * Used for update permission.
	 */
	public static  String UPDATE  	= "update";

	/**
	 * Used for create permission.
	 */
	public static  String CREATE  	= "create";
	
	/**
	 * Used for delete permission.
	 */
	public static  String DELETE  	= "delete";
	
	/**
	 * DecisionTable keyword.
	 */
	static String DECISION_TABLE 	= "DecisionTable";
	/**
	 * ActionRule keyword.
	 */
	static String ACTION_RULE 		= "ActionRule";
}
