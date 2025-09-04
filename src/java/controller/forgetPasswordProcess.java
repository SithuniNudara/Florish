package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.User;
import java.io.IOException;
import java.io.PrintWriter;
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
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "forgetPasswordProcess", urlPatterns = {"/forgetPasswordProcess"})
public class forgetPasswordProcess extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        final String email = request.getParameter("email");

        if (email.isEmpty()) {
            responseObject.addProperty("message", "Please Enter Your Email");
        } else if (!Util.isEmailValid(email)) {
            responseObject.addProperty("message", "Please Enter Valid Email");
        } else {

            request.setAttribute("email", email);

            Session session = HibernateUtil.getSessionFactory().openSession();

            Criteria criteria = session.createCriteria(User.class);
            criteria.add(Restrictions.eq("email", email));

            User user = (User) criteria.uniqueResult();

            if (user != null) {
                final String verification = Util.generateCode();
                user.setVerification(verification);

                Transaction transaction = session.beginTransaction();
                session.merge(user);
                transaction.commit();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Mail.sendMail(email, "Florish Forget Password", getContent(verification));
                    }
                }).start();

                responseObject.addProperty("status", true);
                responseObject.addProperty("message", "Code Sent to your Email");
            } else {
                responseObject.addProperty("message", "Please Register First!");
            }

        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));
    }

    //Html Content
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
                + "            <h1>Reset Your Password 🔐</h1>"
                + "            <p>We received a request to reset your password</p>"
                + "        </div>"
                + "        <div class=\"content\">"
                + "            <p>To proceed with resetting your password, please use the verification code below:</p>"
                + "            <div class=\"verification-code\">" + code + "</div>"
                + "            <p>Enter this code in the password reset page to continue.</p>"
                + "            <p>If you didn’t request a password reset, you can safely ignore this email.</p>"
                + "            <a href=\"http://localhost:8080/Florish/Reset-Password.html\" class=\"button\">Reset Password</a>"
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
