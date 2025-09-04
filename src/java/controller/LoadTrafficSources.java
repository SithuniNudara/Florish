package controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.OrderItems;
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
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;

@WebServlet(name = "LoadTrafficSources", urlPatterns = {"/LoadTrafficSources"})
public class LoadTrafficSources extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session session = sf.openSession();

        try {
            Criteria criteria = session.createCriteria(User.class, "u");
            criteria.createAlias("u.userType", "ut");

            ProjectionList projectionList = Projections.projectionList()
                    .add(Projections.groupProperty("ut.id")) // Group by userType ID
                    .add(Projections.property("ut.name"), "userTypeName") // Select userType name
                    .add(Projections.count("u.id"), "userIdCount");            // Count users

            criteria.setProjection(projectionList);
            criteria.addOrder(Order.desc("userIdCount"));                      // Order by user count

            List<Object[]> results = criteria.list();

            if (results.isEmpty()) {
                responseObject.addProperty("message", "No user data found");
            } else {
                JsonArray dataArray = new JsonArray();

                for (Object[] row : results) {
                    String source = (String) row[1];  // userType name
                    Number count = (Number) row[2];   // user count

                    JsonObject item = new JsonObject();
                    item.addProperty("source", source);  // renamed from title → source
                    item.addProperty("count", count != null ? count.intValue() : 0);  // renamed from qty → count

                    dataArray.add(item);
                }

                responseObject.addProperty("status", true);
                responseObject.add("data", dataArray);
            }
        } catch (Exception e) {
            responseObject.addProperty("message", "Server error: " + e.getMessage());
        } finally {
            session.close();
        }

        String toJson = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(toJson);
    }

}
