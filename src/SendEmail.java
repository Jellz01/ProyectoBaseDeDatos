import javax.mail.*;
import javax.mail.internet.*;
import java.util.ArrayList;
import java.util.Properties;

public class SendEmail {
    Properties properties;
    String recipiente;
    ArrayList<String> emailContent;

    public SendEmail(Properties properties, String recipiente, ArrayList<String> emailContent) {
        this.properties = properties;
        this.recipiente = recipiente;
        this.emailContent = emailContent;

        final String username = "servidorjtayuda@gmail.com";
        final String password = "xtko zntw azxi fxfq"; // Ensure this is correct or use App Passwords if needed

        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipiente));
            message.setSubject("Factura de Servicio");

            // Join the ArrayList<String> into a single HTML-formatted string
            StringBuilder emailContentBuilder = new StringBuilder();
            for (String line : emailContent) {
                emailContentBuilder.append(line).append("<br>");
            }
            String emailBody = emailContentBuilder.toString();

            // Create the email body part with HTML content
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(emailBody, "text/html");

            MimeMultipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            message.setContent(multipart);

            Transport.send(message);

            System.out.println("Email sent successfully!");

        } catch (MessagingException e) {
            e.printStackTrace();
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }
}
