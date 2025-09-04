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

@WebServlet(name = "ChangePw", urlPatterns = {"/ChangePw"})
public class ChangePw extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);
        
        JsonObject data = gson.fromJson(request.getReader(), JsonObject.class);
        
        if (data == null) {
            responseObject.addProperty("message", "Data Not Found");
        } else if (!data.has("current")) {
            responseObject.addProperty("message", "Current Password Not Found");
            
        } else if (!data.has("new")) {
            responseObject.addProperty("message", "New Password Not Found");
            
        } else if (!data.has("confirm")) {
            responseObject.addProperty("message", "Confirm Password Not Found");
            
        } else {
            
            String current = data.get("current").getAsString();
            String newPassword = data.get("new").getAsString();
            String confirm = data.get("confirm").getAsString();
            
            if (!Util.isPasswordValid(newPassword)) {
                responseObject.addProperty("message", "The Password must contain uppercase, lowercase, number, special character and be at least 8 characters long");
            } else {
                HttpSession Sessionuser = request.getSession(false);
                
                if (Sessionuser == null && Sessionuser.getAttribute("user") == null) {
                    responseObject.addProperty("message", "Please Log In");
                } else {
                    SessionFactory sf = HibernateUtil.getSessionFactory();
                    Session s = sf.openSession();
                    
                    User user = (User) Sessionuser.getAttribute("user");
                    
                    User dbUser = (User) s.createCriteria(User.class)
                            .add(Restrictions.eq("id", user.getId()))
                            .uniqueResult();
                    
                    if (dbUser == null) {
                        responseObject.addProperty("message", "Not a valid user");
                    } else if (!newPassword.equals(confirm)) {
                        responseObject.addProperty("message", "New password does not match confirmation");
                    } else {
                        dbUser.setPassword(confirm);
                        s.merge(dbUser);
                        s.beginTransaction().commit();
                        
                        responseObject.addProperty("message", "Password updated successfully!");
                        responseObject.addProperty("status", true);
                    }
                    s.close();
                }
                
            }
        }
        
        String toJson = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(toJson);
        
    }
    
}
