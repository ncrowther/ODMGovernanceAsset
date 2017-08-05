package rulegovernance.workflow.manager.notification;

import ilog.rules.teamserver.model.IlrSession;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import rulegovernance.model.utils.MessageLogger;
import rulegovernance.workflow.manager.notification.Notification;
import rulegovernance.workflow.manager.notification.NotificationListener;

public class EmailNotificationImpl implements NotificationListener {

	private static Logger log = Logger.getLogger(rulegovernance.workflow.manager.notification.EmailNotificationImpl.class.getName());
	
	private static final String MAIL_SESSION_JNDI = "java:comp/env/mail/RuleGovMailSession";
	private static final String CONTENT = "text/html";

	
	/**
	 * Send and email notification
	 */
	public void notify(IlrSession session, Notification notification) {
		try {
			InitialContext ctx = new javax.naming.InitialContext();

			javax.mail.Session mailSession = getEmailSession(ctx, MAIL_SESSION_JNDI);
			
			if ( (log.isLoggable(Level.INFO)) && !send(mailSession, notification)) {
				MessageLogger.logMessage(log, Level.INFO, "Failed to send notification");
			}

		} catch (Exception e) {
			if (log.isLoggable(Level.SEVERE)) {
				MessageLogger.logMessage(log, Level.SEVERE, "Failed to send notification:" + e.getMessage());
			}
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * Send an email notification
	 * rulegovernance.workflow.notification.ISendMail#sendMessage(java.lang.
	 * String[], java.lang.String, java.lang.String, java.lang.String)
	 */
	private boolean send(javax.mail.Session mailSession, Notification email) {

		if (mailSession == null) {
			return false;
		}

		String subject = email.getSubject();
		String htmlContent = email.getMessageBody();

		try {

			Message msg = new MimeMessage(mailSession);

			String[] recipients = new String[1];
			recipients[0] = email.getRecipient();
			InternetAddress[] addressTo = new InternetAddress[recipients.length];
			for (int i = 0; i < recipients.length; i++) {
				addressTo[i] = new InternetAddress(recipients[i]);
			}
			msg.setRecipients(Message.RecipientType.TO, addressTo);

			// Setting the Subject and Content Type
			msg.setSubject(subject);
			//msg.setContent(message, CONTENT);
			
			setHtmlMessage(msg, htmlContent);

			if (log.isLoggable(Level.INFO)) {
				MessageLogger.logMessage(log, Level.INFO, "send notification[" + addressTo[0] + "  "
					+ subject + " " + htmlContent + "]");
			}

			Transport.send(msg);

			return true;
		} catch (AddressException e) {
			if (log.isLoggable(Level.SEVERE)) {
				MessageLogger.logMessage(log, Level.SEVERE, "Bad Address.  Failed to send notification: " + e.getMessage());
			}
			return false;
		} catch (MessagingException e) {
			if (log.isLoggable(Level.SEVERE)) {
				MessageLogger.logMessage(log, Level.SEVERE, "Bad Message.  Failed to send notification: " + e.getMessage());
			}
			return false;
		}

	}

	private static void setHtmlMessage(Message message, String html) {

		Multipart multiPart = new MimeMultipart("alternative");

		try {

			//MimeBodyPart textPart = new MimeBodyPart();
			//textPart.setText(text, "utf-8");

			MimeBodyPart htmlPart = new MimeBodyPart();
			htmlPart.setContent(html, "text/html; charset=utf-8");

			multiPart.addBodyPart(htmlPart);
			//multiPart.addBodyPart(textPart);
			message.setContent(multiPart);

		} catch (MessagingException e) {
			e.printStackTrace();
			if (log.isLoggable(Level.SEVERE)) {
				MessageLogger.logMessage(log, Level.SEVERE, "Bad Message.  Failed to send notification: " + e.getMessage());
			}
		}
	}
	
	private static javax.mail.Session getEmailSession(InitialContext ctx, String jndiName) {
		try {
			if (log.isLoggable(Level.INFO)) {
				MessageLogger.logMessage(log, Level.INFO, "Trying to lookup: " + jndiName);
			}
			return (javax.mail.Session) ctx.lookup(jndiName);
		} catch (NamingException e) {
			if (log.isLoggable(Level.SEVERE)) {
				MessageLogger.logMessage(log, Level.SEVERE, "Bad Message.  Failed to get email session: " + e.getMessage());
			}
			e.printStackTrace();
		}
		return null;
	}
}
