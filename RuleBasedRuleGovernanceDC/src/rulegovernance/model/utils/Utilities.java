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

import ilog.rules.teamserver.brm.IlrBrmPackage;
import ilog.rules.teamserver.model.IlrElementDetails;
import ilog.rules.teamserver.model.IlrElementHandle;
import ilog.rules.teamserver.model.IlrObjectNotFoundException;
import ilog.rules.teamserver.model.IlrSession;
import ilog.rules.teamserver.model.permalink.IlrPermanentLinkHelper;

public class Utilities {
	/**
	 * Returns the element URL.
	 */
	public static String getURLForElement (IlrSession session,IlrElementHandle handle) {
	    IlrPermanentLinkHelper permanentLinkHelper = new IlrPermanentLinkHelper(session);
	    try {
	    IlrElementDetails element = (IlrElementDetails) session.getElementDetails(handle);
	    String project = getProjectName (session, element);
	    String url = permanentLinkHelper.getElementDetailsURL(project, handle);
	    return url;
	    } catch (Exception e) {
		return "";
	    }
	}
	/**
	 * Returns the element's project name.
	 */
	public static String getProjectName (IlrSession session, IlrElementDetails element) throws IlrObjectNotFoundException  {
		IlrBrmPackage brm = session.getBrmPackage();
		IlrElementHandle handle = (IlrElementHandle)element.getRawValue(brm.getProjectElement_Project());
		IlrElementDetails project = session.getElementDetailsForThisHandle(handle);
		return project.getName();
	}
}
