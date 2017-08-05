/*
 * Licensed Materials - Property of IBM
 * 5724-Y00 5724-Y17 5655-V84
 * Copyright IBM Corp. 1987, 2010. All Rights Reserved.
 *
 * Note to U.S. Government Users Restricted Rights: 
 * Use, duplication or disclosure restricted by GSA ADP Schedule 
 * Contract with IBM Corp.
 */
package rulegovernance.workflow.manager.notification;

import java.util.logging.Level;
import java.util.logging.Logger;

import ilog.rules.teamserver.model.IlrSession;

import rulegovernance.model.utils.MessageLogger;
import rulegovernance.workflow.manager.notification.Notification;
import rulegovernance.workflow.manager.notification.NotificationListener;

/**
 * The simple default notification listener that prints notifications to the log file.
 */

public class NotificationListenerDefaultImpl implements NotificationListener {

	private static Logger log = Logger.getLogger(rulegovernance.workflow.manager.notification.NotificationListenerDefaultImpl.class.getName());
	
	/***
	 * Called whenever an element has been created, updated, or deleted from Decision Center as 
	 * decided by the notification rules.
	 *
	 * @param session The session
	 * @param notification The notification object
	 */
	public void notify(IlrSession session, Notification notification) {

		String oldStatus = notification.getOldstatus();
		String newStatus = notification.getNewStatus();

		if (log.isLoggable(Level.INFO)) {
			MessageLogger.logMessage(log, Level.INFO, "notify: old status: " + oldStatus);
			MessageLogger.logMessage(log, Level.INFO, "notify: new status: " + newStatus);
		}

		try {
			sendNotification(notification);
			if (log.isLoggable(Level.INFO)) {
				MessageLogger.logMessage(log, Level.INFO, "notify: Added notification to log file.");
			}
		} catch (Exception e) {
			MessageLogger.logMessage(log, Level.SEVERE, "Failed to perform notification");
			e.printStackTrace();
		}

	}

	
	private void sendNotification(Notification notification) throws Exception {

		String subject= notification.getSubject();
		String msgBody = notification.getMessageBody();
		String recipient = notification.getRecipient();

		if (log.isLoggable(Level.INFO)) {
			MessageLogger.logMessage(log, Level.INFO, "sendNotification: Subject : " + subject);
			MessageLogger.logMessage(log, Level.INFO, "sendNotification: Recipient : " + recipient);
			MessageLogger.logMessage(log, Level.INFO, "sendNotification: Message Body : " + msgBody);
		}
	}

}
