package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.User;
import hibernate.UserStatus;
import hibernate.UserType;
import java.io.IOException;
import java.io.PrintWriter;
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

@WebServlet(name = "SignUp", urlPatterns = {"/SignUp"})
public class SignUp extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        Gson gson = new Gson();
        JsonObject user = gson.fromJson(request.getReader(), JsonObject.class);

        String FirstName = user.get("firstName").getAsString();
        String LastName = user.get("lastName").getAsString();
        final String Email = user.get("email").getAsString();
        String Password = user.get("password").getAsString();

        if (FirstName.isEmpty()) {
            responseObject.addProperty("message", "First Name Cannot Be Empty");
        } else if (LastName.isEmpty()) {
            responseObject.addProperty("message", "Last Name Cannot Be Empty");
        } else if (Email.isEmpty()) {
            responseObject.addProperty("message", "Email Cannot Be Empty");
        } else if (!Util.isEmailValid(Email)) {
            responseObject.addProperty("message", "Please enter a valid email");
        } else if (Password.isEmpty()) {
            responseObject.addProperty("message", "Password Cannot Be Empty");
        } else if (!Util.isPasswordValid(Password)) {
            responseObject.addProperty("message", "The Password must contains atleast uppercase, lower case, number , special character and to be minimum 8 charactors long");
        } else {

            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();

            Criteria criteria = s.createCriteria(User.class);
            criteria.add(Restrictions.eq("email", Email));

            if (!criteria.list().isEmpty()) {

                responseObject.addProperty("message", "This User Already Registerd!");
               
            } else {

                User u = new User();
                u.setFirstName(FirstName);
                u.setLastName(LastName);
                u.setEmail(Email);
                u.setPassword(Password);

                //generated code
                final String verification = Util.generateCode();

                u.setVerification(verification);

                u.setCreatedAt(new Date());

                UserStatus status = (UserStatus) s.get(UserStatus.class, 1);
                u.setUserStatus(status);

                UserType type = (UserType) s.get(UserType.class, 2);
                u.setUserType(type);

                //save data
                s.save(u);
                s.beginTransaction().commit();

                //send verification code
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Mail.sendMail(Email, "Florish User Verification", getContent(verification));
                    }
                }).start();

                //create Session
                HttpSession session = request.getSession();
                session.setAttribute("email", Email);
                //create Session
                
                //response message
                responseObject.addProperty("status", true);
                responseObject.addProperty("message", "Registration Successful Please Verify Your Account!");

            }
            s.close();
        }

        String responseText = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(responseText);

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
