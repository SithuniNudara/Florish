package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.User;
import hibernate.UserStatus;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "chageUserStatus", urlPatterns = {"/chageUserStatus"})
public class chageUserStatus extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        String id = request.getParameter("id");
        // System.out.println(id);

        if (!Util.isInteger(id)) {
            responseObject.addProperty("message", "invalid Id");
        } else {

            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();

            Criteria userCriteria = s.createCriteria(User.class);
            userCriteria.add(Restrictions.eq("id", Integer.valueOf(id)));
            User user = (User) userCriteria.uniqueResult();
            
            if(user == null){
                responseObject.addProperty("message", "User Not Found");
            }else{
                String currentStatus = user.getUserStatus().getName();
                
                Criteria statusCriteria = s.createCriteria(UserStatus.class);
                List<UserStatus> allStatuses = statusCriteria.list();
                
                UserStatus newStatus = null;
                
                for (UserStatus allStatuse : allStatuses) {
                    if(currentStatus.equalsIgnoreCase("Active") && allStatuse.getName().equalsIgnoreCase("Block")){
                        newStatus = allStatuse;
                    }else if(!currentStatus.equalsIgnoreCase("Active") && allStatuse.getName().equalsIgnoreCase("Active")){
                        newStatus = allStatuse;
                    }
                }
                
                if (newStatus != null) {
                    user.setUserStatus(newStatus);
                    s.update(user);
                    s.beginTransaction().commit();

                    responseObject.addProperty("status", true);
                    responseObject.addProperty("message", "User status updated successfully");
                    responseObject.addProperty("newStatus", newStatus.getName());
                } else {
                    responseObject.addProperty("message", "New status not found");
                }
            }
        }

        String toJson = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(toJson);

    }

}
