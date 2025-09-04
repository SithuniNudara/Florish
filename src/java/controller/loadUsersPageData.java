package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;

@WebServlet(name = "loadUsersPageData", urlPatterns = {"/loadUsersPageData"})
public class loadUsersPageData extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();

        Criteria userCriteria = s.createCriteria(User.class);
        List<User> userList = userCriteria.list();
        
        int total = 0;
        int activeCount = 0;
        int blockedCount = 0;

        if (userList.isEmpty()) {
            responseObject.addProperty("message", "Users Not Found");
        } else {
            for (User user : userList) {
                total += 1;
                
                if (user.getUserStatus().getName().equals("Active")) {
                    activeCount += 1;
                } else {
                    blockedCount += 1;
                }             
                
            }
            
            responseObject.add("userList", gson.toJsonTree(userList));
            responseObject.addProperty("total", total);
            responseObject.addProperty("Active", activeCount);
            responseObject.addProperty("Blocked", blockedCount);
            responseObject.addProperty("status", true);
        }

        String toString = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(toString);

    }

}