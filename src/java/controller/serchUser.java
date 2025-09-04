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
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "serchUser", urlPatterns = {"/serchUser"})
public class serchUser extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        String keyword = request.getParameter("keyword");

        if (keyword == null) {
            responseObject.addProperty("message", "No Keyword Found");
        } else {
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();
            
            Criteria userCriteria = s.createCriteria(User.class);
            userCriteria.add(Restrictions.eq("firstName", keyword));
            List<User> userList = userCriteria.list();
            
            responseObject.addProperty("status", true);
             responseObject.add("userList", gson.toJsonTree(userList));
            responseObject.addProperty("message", "Search Compeleted!");
        }

        String toString = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(toString);
    }

}
