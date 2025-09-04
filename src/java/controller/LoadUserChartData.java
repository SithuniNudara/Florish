package controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import hibernate.Category;
import hibernate.HibernateUtil;
import hibernate.User;
import hibernate.Volume;
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

@WebServlet(name = "LoadUserChartData", urlPatterns = {"/LoadUserChartData"})
public class LoadUserChartData extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        Session session = HibernateUtil.getSessionFactory().openSession();

        try {
            // User Status Count
            List<Object[]> statusCounts = session.createQuery(
                    "SELECT u.userStatus.statusName, COUNT(u) FROM User u GROUP BY u.userStatus.statusName"
            ).list();

            JsonArray statusData = new JsonArray();
            for (Object[] row : statusCounts) {
                JsonObject obj = new JsonObject();
                obj.addProperty("status", (String) row[0]);
                obj.addProperty("count", ((Long) row[1]));
                statusData.add(obj);
            }

            // User Type Count
            List<Object[]> typeCounts = session.createQuery(
                    "SELECT u.userType.typeName, COUNT(u) FROM User u GROUP BY u.userType.typeName"
            ).list();

            JsonArray typeData = new JsonArray();
            for (Object[] row : typeCounts) {
                JsonObject obj = new JsonObject();
                obj.addProperty("type", (String) row[0]);
                obj.addProperty("count", ((Long) row[1]));
                typeData.add(obj);
            }

            responseObject.add("statusChart", statusData);
            responseObject.add("typeChart", typeData);
            responseObject.addProperty("status", true);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));
    }
}
