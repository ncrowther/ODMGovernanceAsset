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

import ilog.rules.teamserver.model.IlrSession;
import ilog.rules.teamserver.model.permissions.IlrPermission;
import ilog.rules.teamserver.model.permissions.IlrPermissionConstants;
import ilog.rules.teamserver.model.permissions.IlrPermissionsFacility;
import ilog.rules.teamserver.model.permissions.IlrRoleRestrictedPermissionException;
import ilog.rules.teamserver.model.permissions.IlrSecurityProfileData;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

import rulegovernance.model.utils.ApplicationException;

public class SimplePermissionHelper {

	/**
	 * For each role that matches the name of the project, this method loads the defined permissions and sets them in Decision Center.
	 */

	public static void setPermissionsForRole(IlrSession session, String projectName, String[] definedGroups, String rootdir ) 
			throws IOException, FileNotFoundException, IlrRoleRestrictedPermissionException, ApplicationException {
		for (int i = 0; i < definedGroups.length; i++) {
			String role = definedGroups[i];
			if (projectName.equals(role)) {
				System.out.println("Adding permissions for "+role+" to the project called "+projectName);

				// Set permission for the groups dedicated to defined roles
				System.out.println("Settings permissions for "+role);
				System.out.println("Root directory="+rootdir);
				HashMap<String,String> properties = 
					InstallationUtil.loadSorted(rootdir + "/" + InstallationUtil.getRolesPermissionDir () + "/" + role + InstallConstants.PERMISSION_SUFFIX);
				setPermissionsForRole (session, role, properties);
			}
		}
	}
	/**
	 * Cleans the permissions of a role and sets the ones passed in the properties.
	 */
	private static void setPermissionsForRole (IlrSession session, String role, HashMap<String,String> properties) 
			throws ApplicationException, IlrRoleRestrictedPermissionException {
		// Retrieve security properties for the role
		IlrSecurityProfileData profile = session.getSecurityProfileData(role);
		// Remove all of them
		if (profile!=null) {
			while (true) {
				int size = profile.size();
				if (size==0) break;
				profile.removeData(0);
			}
			((IlrPermissionsFacility)session).commitSecurityProfileData(role, profile) ;
		} else {
			profile = new IlrSecurityProfileData();
		}
		// Now the role is 'clean'
		Iterator<String> iterator = properties.keySet().iterator();
		// Add the security information defined in the passed properties
		while (iterator.hasNext()) {
			String key =  iterator.next();
			String val =  properties.get(key);

			System.out.println("Adding Permission: key="+key+", value="+val);
			Permission permission = new Permission (key,val);
			permission.add(profile);
		}
		// Persist in the database
		((IlrPermissionsFacility)session).commitSecurityProfileData(role, profile) ;
	}

	public static class Permission {
		/**
		 * The role.
		 */
		String role=null;
		/**
		 * The action (delete, update, view, create).
		 */
		String action=null;
		/**
		 * The artifact type (SmartView, ActionRule, ...).
		 */
		String artifactType=null;
		/**
		 * The property to which the permission applies.
		 */
		String field=null;
		/**
		 * The permission value.
		 */
		String value=null;

		/**
		 * Builds an instance of a permission.
		 */
		public Permission (String key,String value) throws ApplicationException {
			this.value=value;
			StringTokenizer st = new StringTokenizer (key,".");
			try {
				role = st.nextToken().trim();
			} catch (Exception e) {
				throw new ApplicationException ("Missing role in " + key);
			}
			try {
				action = st.nextToken().trim();
			} catch (Exception e) {
				throw new ApplicationException ("Missing action (update or view or delete or create) in " + key);
			}
			if (InstallConstants.UPDATE.equalsIgnoreCase(action)) {
				try {
					artifactType = st.nextToken().trim();
				} catch (Exception e) {
					throw new ApplicationException ("Missing artifactType (* or Query or RulePackage or SmartView or DecisionTable or DecisionTree or Template or TechnicalRule or Function or VariableSet or Ruleflow or ActionRule) in " + key);
				}
				if ("*".equalsIgnoreCase(artifactType)) {
					field = null;
				} else {
					try {
						field  = st.nextToken().trim();
					} catch (Exception e) {
						throw new ApplicationException ("Missing field (* or any field of '" + artifactType + "' artifact type) in " + key);
					}
				}
			} else {
				try {
					artifactType = st.nextToken().trim();
				} catch (Exception e) {
					throw new ApplicationException ("Missing artifactType (* or Query or RulePackage or SmartView or DecisionTable or DecisionTree or Template or TechnicalRule or Function or VariableSet or Ruleflow or ActionRule) in " + key);
				}
			}
		}

		/**
		 * Computes and sets the permissions as Rule Team Server expects them.
		 */
		public void add(IlrSecurityProfileData profile) throws ApplicationException {
			IlrPermission permission = getPermValue(value) ;
			String[] args = null;
			if (field==null) {
				if (InstallConstants.UPDATE.equalsIgnoreCase(action)) {
					args = new String [] {artifactType.equalsIgnoreCase("*") ? artifactType : InstallConstants.BRM_PREFIX + artifactType ,"*" };
				} else {
					args = new String [] {artifactType.equalsIgnoreCase("*") ? artifactType : InstallConstants.BRM_PREFIX + artifactType  };
				}
			} else {
				if (field.equalsIgnoreCase("*")) {
					args = new String [] {artifactType.equalsIgnoreCase("*") ? artifactType : InstallConstants.BRM_PREFIX + artifactType, "*"};
				} else {
					args = new String [] {artifactType.equalsIgnoreCase("*") ? artifactType : InstallConstants.BRM_PREFIX + artifactType, 
							InstallConstants.BRM_PREFIX + artifactType +"."+field};
				}
			}
			profile.addData(permission,args);
		}

		/**
		 * Computes the permission value.
		 */
		private IlrPermission getPermValue(String permissionValue) throws ApplicationException {
			if (InstallConstants.UPDATE.equalsIgnoreCase(action)) {
				if (permissionValue.equals(InstallConstants.NO)) {
					return IlrPermissionConstants.UPDATE_NONE;
				}
				if (permissionValue.equals(InstallConstants.GROUP)) {
					return IlrPermissionConstants.UPDATE_GROUP;
				}
				return IlrPermissionConstants.UPDATE_ANY;
			}
			if (InstallConstants.CREATE.equalsIgnoreCase(action)) {
				if (permissionValue.equals(InstallConstants.NO)) {
					return IlrPermissionConstants.CREATE_FALSE;
				}
				return IlrPermissionConstants.CREATE_TRUE;
			}
			if (InstallConstants.DELETE.equalsIgnoreCase(action)) {
				if (permissionValue.equals(InstallConstants.NO)) {
					return IlrPermissionConstants.DELETE_NONE;
				}
				if (permissionValue.equals(InstallConstants.GROUP)) {
					return IlrPermissionConstants.DELETE_GROUP;
				}
				return IlrPermissionConstants.DELETE_ANY;
			}
			if (InstallConstants.VIEW.equalsIgnoreCase(action)) {
				if (permissionValue.equals(InstallConstants.NO)) {
					return IlrPermissionConstants.VIEW_NONE;
				}
				if (permissionValue.equals(InstallConstants.GROUP)) {
					return IlrPermissionConstants.VIEW_GROUP;
				}
				return IlrPermissionConstants.VIEW_ANY;
			}
			throw new ApplicationException (permissionValue + " is not a valid value. Only yes, no, group are valid.");
		}
	}
}
