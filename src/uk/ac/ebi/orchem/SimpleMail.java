package uk.ac.ebi.orchem;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Sends an e-mail
 * 
 * TODO
 * to run this as a tored java procedure you need extra permissions ....
 * dbms_java.grant_permission( 'STARLITE28P', 'SYS:java.net.SocketPermission', 'smtp.ebi.ac.uk', 'resolve' ); 
 * dbms_java.grant_permission( 'STARLITE28P', 'SYS:java.net.SocketPermission', '193.62.196.9:25', 'connect,resolve' ); 
 * So it seem easier instead to use PL/SQL !
 * 
 */
public class SimpleMail {


    public static void sendMail(String mailProtocol, String mailHost, String mailRecipient, String title, String body) throws Exception {
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", mailProtocol);
        props.setProperty("mail.host", mailHost);
        props.setProperty("mail.user", "orchem");

        Session mailSession = Session.getDefaultInstance(props, null);
        Transport transport = mailSession.getTransport();

        MimeMessage message = new MimeMessage(mailSession);
        message.setSubject(title);
        message.setContent(body, "text/plain");
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(mailRecipient));

        transport.connect();
        transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
        transport.close();
    }

    public static void main(String[] args) throws Exception {
        sendMail("smtp","smtp.ebi.ac.uk","markr@ebi.ac.uk","Hallo","\n\n\ncheck");
    }


}
