import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class SendEmail {
    Properties properties;
    String recipiente;

       public SendEmail(Properties properties, String recipiente){

           this.properties = properties;
           this.recipiente = recipiente;

        final String username = "servidorjtayuda@gmail.com";
        final String password = "xtko zntw azxi fxfq";

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // Creación del mensaje
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("servidorjtayuda@gmail.com"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipiente));
            message.setSubject("Test Email");
            message.setText("Hello, this is a test email!");

            // Envío del mensaje
            Transport.send(message);

            System.out.println("Email sent successfully!");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
