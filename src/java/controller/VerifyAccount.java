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
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "VerifyAccount", urlPatterns = {"/VerifyAccount"})
public class VerifyAccount extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        //session check
        HttpSession session = request.getSession();

        if (session.getAttribute("email") == null) {
            responseObject.addProperty("message", "Email Not Found");
        } else {
            String email = session.getAttribute("email").toString();

            JsonObject code = gson.fromJson(request.getReader(), JsonObject.class);

            String VerificationCode = code.get("code").getAsString();

            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();

            Criteria criteria = s.createCriteria(User.class);
            Criterion crt1 = Restrictions.eq("email", email);
            Criterion crt2 = Restrictions.eq("verification", VerificationCode);

            criteria.add(crt1);
            criteria.add(crt2);

            if (criteria.list().isEmpty()) {
                responseObject.addProperty("message", "Invalid Verification Code");
            } else {
                //code and email ok
                User user = (User) criteria.list().get(0);

                user.setVerification("Verified");

                s.update(user);
                s.beginTransaction().commit();
                s.close();

                //store user in the session
                session.setAttribute("user", user);
                //store user in the session

                responseObject.addProperty("status", true);
                responseObject.addProperty("message", "Verification Successful!");
            }
            String responseText = gson.toJson(responseObject);
            response.setContentType("application/json");
            response.getWriter().write(responseText);

        }
    }

}
