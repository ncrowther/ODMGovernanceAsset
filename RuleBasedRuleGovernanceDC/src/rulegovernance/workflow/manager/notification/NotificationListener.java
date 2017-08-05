/*
* Licensed Materials - Property of IBM
* 5725-B69 5655-Y17 5724-Y00 5724-Y17 5655-V84
* Copyright IBM Corp. 1987, 2012. All Rights Reserved.
*
* Note to U.S. Government Users Restricted Rights: 
* Use, duplication or disclosure restricted by GSA ADP Schedule 
* Contract with IBM Corp.
*/

package rulegovernance.workflow.manager.notification;

import ilog.rules.teamserver.model.IlrSession;
import rulegovernance.workflow.manager.notification.Notification;



/**
 * Interface that is implemented when a class wants to be informed of changes
 * in Rule Team Server.
 *
 */
public interface NotificationListener {

	/**
	 * Called whenever an element has been created, updated, or deleted from Decision Center.
	 *
	 * @param session The session
	 * @param notification The notification object
	 * 
	 */
	public void notify(IlrSession session, Notification notification);
}
