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
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "ResetPasswordProcess", urlPatterns = {"/ResetPasswordProcess"})
public class ResetPasswordProcess extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        JsonObject requestObject = gson.fromJson(request.getReader(), JsonObject.class);

        String Password = requestObject.get("Password").getAsString();
        String ConfirmPassword = requestObject.get("ConfirmPassword").getAsString();

        if (Password.isEmpty()) {
            responseObject.addProperty("message", "Type Password");
        } else if (ConfirmPassword.isEmpty()) {
            responseObject.addProperty("message", "Type Confirm Password");
        } else if (!Password.equals(ConfirmPassword)) {
            responseObject.addProperty("message", "Passwords do not match");
        } else {
            User user = (User) request.getSession().getAttribute("user");

            if (user == null) {
                responseObject.addProperty("message", "Session Error");
            } else {
                SessionFactory sf = HibernateUtil.getSessionFactory();
                Session s = sf.openSession();

                Criteria criteria = s.createCriteria(User.class);
                Criterion crt1 = Restrictions.eq("email", user.getEmail());
                criteria.add(crt1);

                if (criteria.list().isEmpty()) {
                    responseObject.addProperty("message", "User not found");
                } else {
                    User dbUser = (User) criteria.list().get(0);

                    Transaction tx = s.beginTransaction();
                    dbUser.setPassword(ConfirmPassword);
                    s.update(dbUser);
                    tx.commit();
                    s.close();

                    HttpSession session = request.getSession();
                    session.invalidate();  // Clear the session
                    responseObject.addProperty("status", true);
                    responseObject.addProperty("message", "Password Reset Successful! Please login with your new password.");

                    responseObject.addProperty("status", true);
                    responseObject.addProperty("message", "Password Reset Successful!");
                }
            }
        }

        String responseText = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(responseText);
    }
}
