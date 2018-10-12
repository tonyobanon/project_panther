package com.re.paas.internal.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.re.paas.api.models.classes.MailCredentialSpec;

public class JMSUtil {
	
	private static MailCredentialSpec credentials;
	
	private Session session;
	private List<String> recipients;
	private String subject;
	private String message;

    public JMSUtil(List<String> recipients, String subject, String message) {
        this.recipients = recipients;
        this.subject = subject;
        this.message = message;
    }
    
    public void send() throws AddressException, MessagingException{
    	
    	//Creating properties
        Properties props = new Properties();

        //Configuring properties for mail
        props.put("mail.smtp.host", credentials.getProviderUrl());
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        //Creating a new session
        session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    //Authenticating the password
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(credentials.getUsername(), credentials.getPassword());
                    }
                });

            //Creating MimeMessage object
            MimeMessage mm = new MimeMessage(session);

            //Setting sender address
            mm.setFrom(new InternetAddress(credentials.getUsername()));
            
            //Adding receiver
            List<InternetAddress> recipients = new ArrayList<InternetAddress>(this.recipients.size());
            
            for(String s : this.recipients){
            	recipients.add(new InternetAddress(s));
            };
            
            mm.addRecipients(Message.RecipientType.TO, recipients.toArray(new InternetAddress[recipients.size()]));
            //Adding subject
            mm.setSubject(subject);        
            
            //Adding message
            mm.setText(message);

            //Sending email
            Transport.send(mm);

    }

	public static MailCredentialSpec getCredentials() {
		return credentials;
	}

	public static void setCredentials(MailCredentialSpec credentials) {
		JMSUtil.credentials = credentials;
	}
  
}
