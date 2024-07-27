import javax.mail.*;
import javax.mail.internet.*;
import java.util.ArrayList;
import java.util.Properties;

public class SendEmail {
    Properties properties;
    String recipiente;
    ArrayList<String> emailContent;

    String direccion;
    String email;
    String nombre;
    String cedula;

    public SendEmail(Properties properties, String recipiente, ArrayList<String> emailContent, String direccion, String email, String nombre, String cedula) {
        this.properties = properties;
        this.recipiente = recipiente;
        this.emailContent = emailContent;
        this.direccion = direccion;
        this.email = email;
        this.nombre = nombre;
        this.cedula = cedula;

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

            // Construct the HTML content
            StringBuilder emailContentBuilder = new StringBuilder();
            emailContentBuilder.append("<html><body style='font-family: Arial, sans-serif;'>");
            emailContentBuilder.append("<div style='max-width: 600px; margin: auto; padding: 20px; border: 1px solid #ddd;'>");
            emailContentBuilder.append("<div style='text-align: center; padding-bottom: 20px;'>");
            emailContentBuilder.append("<img src='logo_url_here' alt='Your Logo' style='max-width: 150px;'/><br/>");
            emailContentBuilder.append("<h2 style='color: #007bff;'>Thanks for your payment!</h2>");
            emailContentBuilder.append("<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.</p>");
            emailContentBuilder.append("</div>");

            emailContentBuilder.append("<div style='background: #f7f7f7; padding: 20px; border-radius: 10px;'>");
            emailContentBuilder.append("<h3 style='color: #007bff;'>Order Confirmation <span style='float: right;'>#123456</span></h3>");
            emailContentBuilder.append("<table style='width: 100%; border-collapse: collapse;'>");
            emailContentBuilder.append("<thead>");
            emailContentBuilder.append("<tr>");
            emailContentBuilder.append("<th style='border: 1px solid #ddd; padding: 8px;'>Item</th>");
            emailContentBuilder.append("<th style='border: 1px solid #ddd; padding: 8px;'>Price</th>");
            emailContentBuilder.append("</tr>");
            emailContentBuilder.append("</thead>");
            emailContentBuilder.append("<tbody>");

            for (String line : emailContent) {
                // Assuming each line in emailContent corresponds to a row
                emailContentBuilder.append("<tr>");
                String[] fields = line.split(", ");
                emailContentBuilder.append("<td style='border: 1px solid #ddd; padding: 8px;'>").append(fields[1].split(": ")[1]).append("</td>");
                emailContentBuilder.append("<td style='border: 1px solid #ddd; padding: 8px;'>").append(fields[2].split(": ")[1]).append("</td>");
                emailContentBuilder.append("</tr>");
            }

            emailContentBuilder.append("<tr>");
            emailContentBuilder.append("<td style='border: 1px solid #ddd; padding: 8px;'><strong>Total</strong></td>");
            emailContentBuilder.append("<td style='border: 1px solid #ddd; padding: 8px;'>$1000</td>"); // Assuming $1000 is the total, replace it with the actual total value if needed
            emailContentBuilder.append("</tr>");

            emailContentBuilder.append("</tbody>");
            emailContentBuilder.append("</table>");
            emailContentBuilder.append("</div>");

            emailContentBuilder.append("<div style='padding: 20px 0; text-align: center;'>");
            emailContentBuilder.append("<p>Get 20% OFF on your next order</p>");
            emailContentBuilder.append("<button style='background-color: #007bff; color: white; padding: 10px 20px; border: none; border-radius: 5px;'>20SPECIAL</button>");
            emailContentBuilder.append("</div>");

            emailContentBuilder.append("<div style='text-align: center; padding-top: 20px;'>");
            emailContentBuilder.append("<p>The Shop</p>");
            emailContentBuilder.append("<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.</p>");
            emailContentBuilder.append("</div>");
            emailContentBuilder.append("</div>");
            emailContentBuilder.append("</body></html>");

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
