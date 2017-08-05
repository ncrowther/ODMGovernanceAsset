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


/**
 * An interface holding all the installation related constants.
 *
 */
public interface InstallConstants {
	/**
	 * The configuration server context.
	 */
	public static String STATUS_DELIMETER = ";";
	
	/**
	 * The configuration server context.
	 */
	public static String SERVER_CONTEXT = "server.context";

	/**
	 * The configuration datasource.
	 */
	public static String SERVER_DATASOURCE = "server.datasource";

	/**
	 * The server configuration file name.
	 */
	public static String SERVER_CONFIGURATION_FILE = "server.properties";

	/** *********************************************************** */
	
	/**
	 * The prefix used for the permission property.
	 */
	public static String BRM_PREFIX = "brm.";

	/** *********************************************************** */
	
	/**
	 * The directory where files are generated.
	 */
	public static String DATA_DIRECTORY = "data";
	
	/**
	 * The suffix for permission file names.
	 */
	public static String PERMISSION_SUFFIX = ".permissions";
	
	/**
	 * The permission dir.
	 */
	public static String PERMISSION_DIR = "permission";
	
	/**
	 * The permission 'role' dir.
	 */
	public static String PERMISSION_ROLE_DIR = "role";
	
	/**
	 * Used for create permission.
	 */
	public static  String CREATE  	= "create";

	/**
	 * Used for update permission.
	 */
	public static  String UPDATE  	= "update";

	/**
	 * Used for delete permission.
	 */
	public static  String DELETE  	= "delete";

	/**
	 * Used for view permission.
	 */
	public static  String VIEW  	= "view";

	/**
	 * Used for permission.
	 */
	public static  String NO  	= "no";

	/**
	 * Used for permission.
	 */
 	public static  String GROUP  	= "group";

}
