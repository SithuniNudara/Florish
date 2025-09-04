package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.User;
import java.io.IOException;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.Mail;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "ResendCode", urlPatterns = {"/ResendCode"})
public class ResendCode extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        // Parse the request JSON into a JsonObject
        JsonObject json = gson.fromJson(request.getReader(), JsonObject.class);

        final String verifyEmail = json.get("email").getAsString();

        // logic for sending code
        //connection
        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();
//select * from user
        User user = (User) s.createCriteria(User.class)
                .add(Restrictions.eq("email", verifyEmail))
                .uniqueResult();

        if (user == null) {

            responseObject.addProperty("message", "Your Are Not Registered!");

        } else {

            //generated code
           final  String verification = Util.generateCode();

            user.setVerification(verification);

            //save data
            s.merge(user);
            s.beginTransaction().commit();

            //send verification code
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Mail.sendMail(verifyEmail, "Florish Verification Code Resend", getContent(verification));
                }
            }).start();

            //create Session
            HttpSession session = request.getSession();
            session.setAttribute("email", verifyEmail);
            //create Session

            // set based on actual outcome
            responseObject.addProperty("message", "Verification code resent Successfully");

            s.close();

        }
        String responseText = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(responseText);
    }

    private static String getContent(String code) {
        String htmlContent = "<!DOCTYPE html>"
                + "<html>"
                + "<head>"
                + "    <style>"
                + "        body { font-family: 'Arial', sans-serif; margin: 0; padding: 0; background-color: #f5f6fa; }"
                + "        .container { max-width: 600px; margin: 20px auto; background: white; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1); }"
                + "        .header { background: linear-gradient(135deg, #6c5ce7, #a29bfe); padding: 30px 20px; text-align: center; color: white; }"
                + "        .header h1 { margin: 0; font-size: 24px; font-weight: 700; }"
                + "        .content { padding: 30px; text-align: center; }"
                + "        .verification-code { font-size: 32px; font-weight: bold; letter-spacing: 2px; color: #6c5ce7; margin: 20px 0; padding: 15px; background: #f9f9f9; border-radius: 8px; display: inline-block; }"
                + "        .footer { padding: 15px; text-align: center; font-size: 12px; color: #777; background: #f5f6fa; }"
                + "        .button { display: inline-block; margin-top: 20px; padding: 12px 25px; background: linear-gradient(135deg, #6c5ce7, #a29bfe); color: white !important; text-decoration: none; border-radius: 5px; font-weight: bold; }"
                + "    </style>"
                + "</head>"
                + "<body>"
                + "    <div class=\"container\">"
                + "        <div class=\"header\">"
                + "            <h1>Welcome to Florish! 🌸</h1>"
                + "            <p>Verify your account to get started</p>"
                + "        </div>"
                + "        <div class=\"content\">"
                + "            <p>Thank you for signing up! To complete your registration, please use the verification code below:</p>"
                + "            <div class=\"verification-code\">" + code + "</div>"
                + "            <p>Use this Code if you not <strong>Verified</strong> your Account .</p>"
                + "            <p>If you didn’t request this, please ignore this email.</p>"
                + "            <a href=\"http://localhost:8080/Florish/Sign-In.html\" class=\"button\">Verify Now</a>"
                + "        </div>"
                + "        <div class=\"footer\">"
                + "            <p>© 2025 Florish. All rights reserved.</p>"
                + "            <p>Need help? Contact us at <a href=\"mailto:support@florish.com\">support@florish.com</a></p>"
                + "        </div>"
                + "    </div>"
                + "</body>"
                + "</html>";

        return htmlContent;
    }

}
