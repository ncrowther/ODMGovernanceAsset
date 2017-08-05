package rulegovernance.workflow.manager.notification;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class EmailTester {

	private static final String CONTENT = "text/html";
	private static final String HRML_MESSAGE = "<html><body>Dear Mr Crawler,"
			+ "\n\n No spam please!</body></html>";

	public static void main(String[] args) {

		final String username = "ncrowthe1@gmail.com";
		final String password = "Porker01";

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(username, password);
					}
				});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("neddy@ibm.com"));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse("ncrowthe@talk21.com"));
			message.setSubject("Testing Subject");
			message.setContent(message, CONTENT);
			setHtmlMessage(message, HRML_MESSAGE);

			Transport.send(message);

			System.out.println("Email sent!");

		} catch (MessagingException e) {
			throw new RuntimeException(e);
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
			System.out.println("Error: " + e.getMessage());
		}
	}

}
