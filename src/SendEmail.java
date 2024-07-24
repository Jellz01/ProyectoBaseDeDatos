import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class SendEmail {
    Properties properties;
    String recipiente;
    String num;
    String fecha;
    String subt;
    String iva;
    String totalF;
    String nombreServicio;
    String cant;
    String cliente;

    public SendEmail(Properties properties, String recipiente, String num, String fecha, String subt, String iva, String totalF, String nombreServicio, String cant, String cliente) {
        this.properties = properties;
        this.recipiente = recipiente;
        this.cliente = cliente;
        this.cant = cant;
        this.nombreServicio = nombreServicio;
        this.num = num;
        this.fecha = fecha;
        this.subt = subt;
        this.iva = iva;
        this.totalF = totalF;

        final String username = "servidorjtayuda@gmail.com";
        final String password = "xtko zntw azxi fxfq";

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

            // Replace with the direct image URL
            String imgUrl = "https://i.imgur.com/abcd1234.png"; // Replace with the actual image URL

            // HTML content with external image
            String htmlContent = "<html>" +
                    "<head><style>" +
                    "table { border-collapse: collapse; width: 100%; }" +
                    "th, td { border: 1px solid black; padding: 8px; text-align: left; }" +
                    "th { background-color: #f2f2f2; }" +
                    "</style></head>" +
                    "<body>" +
                    "<h2>Factura</h2>" +
                    "<h2>Saludos " + cliente + "</h2>" +
                    "<p><strong>Número de Factura:</strong> " + num + "</p>" +
                    "<p><strong>Fecha:</strong> " + fecha + "</p>" +

                    "<table>" +
                    "<tr><th>Descripción</th><th>Subtotal</th></tr>" +
                    "<tr><td><strong>Nombre del Servicio:</strong></td><td>" + nombreServicio + "</td></tr>" +
                    "<tr><td><strong>Cantidad:</strong></td><td>" + cant + "</td></tr>" +
                    "<tr><td>Servicio</td><td>$" + subt + "</td></tr>" +
                    "<tr><td>IVA</td><td>$" + iva + "</td></tr>" +
                    "<tr><td><strong>Total</strong></td><td><strong>$" + totalF + "</strong></td></tr>" +
                    "</table>" +
                    "<p>Gracias por su tiempo.</p>" +
                    "</body>" +
                    "</html>";

            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(htmlContent, "text/html");

            MimeMultipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            message.setContent(multipart);

            Transport.send(message);

            System.out.println("Email sent successfully!");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
