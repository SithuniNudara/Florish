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
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "SignIn", urlPatterns = {"/SignIn"})
public class SignIn extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject signIn = gson.fromJson(request.getReader(), JsonObject.class);

        String email = signIn.get("Email").getAsString();
        String password = signIn.get("Password").getAsString();

        //response
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        //check
        if (email.isEmpty()) {
            responseObject.addProperty("message", "Email Cannot be Empty");
        } else if (!Util.isEmailValid(email)) {
            responseObject.addProperty("message", "Please enter a valid email");
        } else if (password.isEmpty()) {
            responseObject.addProperty("message", "Password Cannot Be Empty");
        } else if (!Util.isPasswordValid(password)) {
            responseObject.addProperty("message", "The Password must contains atleast uppercase, lower case, number , special character and to be minimum 8 charactors long");
        } else {
            
          SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();

            Criteria c = s.createCriteria(User.class);

            c.add(Restrictions.eq("email", email));
            c.add(Restrictions.eq("password", password));

            if (c.list().isEmpty()) {
                responseObject.addProperty("message", "Invalid Credentials!");
            } else {
                
               
                User user = (User) c.list().get(0);
                HttpSession session = request.getSession();
                
                if(user.getUserType().getId() == 1){
                    responseObject.addProperty("status", true);
                    responseObject.addProperty("type", "Admin Login!");
                }else{
                    responseObject.addProperty("status", true);
                    responseObject.addProperty("type", "User Login!");
                }

                if (!user.getVerification().equals("Verified")) { //not verify
                    session.setAttribute("email", email);

                    responseObject.addProperty("status", true);
                    responseObject.addProperty("message", "Not Verified");
                } else {
                    session.setAttribute("user", user);
                    responseObject.addProperty("status", true);
                    responseObject.addProperty("message", "Verified");
                }

                s.close();
            }
        }

        String responseText = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(responseText);

    }

}
