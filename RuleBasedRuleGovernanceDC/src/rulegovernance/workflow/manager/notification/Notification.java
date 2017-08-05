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

public class Notification {

	/**
	 * The element project.
	 */
	private String project;
	
	private String deltaReport;

	public String getDeltaReport() {
		return deltaReport;
	}

	public void setDeltaReport(String deltaReport) {
		this.deltaReport = deltaReport;
	}

	/**
	 * The URL referencing the element in Rule Team Server.
	 */
	private String link;

	/**
	 * The author of the action done on the element.
	 */
	private String author;

	/**
	 * The old status.
	 */
	String oldstatus;

	/**
	 * The new status.
	 */
	String newStatus;

	/**
	 * The rule path.
	 */
	String path;

	/**
	 * The message body of the event.
	 */
	String messageBody;
	
	/**
	 * The subject
	 */
	String subject;		
	
	
	/**
	 * The person (or distribution list) to receive this notification
	 */
	String recipient;	

	/**
	 * Creates an <code>Item</code> instance.
	 */
	public Notification () {
	}

	/**
	 * Sets the new status.
	 */
	public void setNewStatus(String newStatus) {
		this.newStatus = newStatus;
	}

 	/**
	 * Returns the new status.
	 */
	public String getNewStatus() {
		return newStatus;
	}

	/**
	 * Returns the old status.
	 */
	public String getOldstatus() {
		return oldstatus;
	}

	public boolean isNotificationNeeded () {
		return (!oldstatus.equalsIgnoreCase(newStatus));
	}

	/**
	 * Returns the path.
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Sets the path.
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * Returns the author.
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * Sets the author.
	 */
	public void setAuthor(String author) {
		this.author = author;
	}


	/**
	 * Returns the link.
	 */
	public String getLink() {
		return link;
	}

	/**
	 * Sets the link.
	 */
	public void setLink(String link) {
		this.link = link;
	}

	/**
	 * Returns the project.
	 */
	public String getProject() {
		return project;
	}

	/**
	 * Returns the message body
	 */
	public String getMessageBody() {
		return messageBody;
	}
	
	public void setProject(String project) {
		this.project = project;
	}

	public void setOldStatus(String oldstatus) {
		this.oldstatus = oldstatus;
	}

	public void setMessageBody(String body) {
		this.messageBody = body;
	}
	
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	public String getSubject() {
		return subject;
	}
	
	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}	
}
