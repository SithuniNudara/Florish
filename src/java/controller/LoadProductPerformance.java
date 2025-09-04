package controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.OrderItems;
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

@WebServlet(name = "LoadProductPerformance", urlPatterns = {"/LoadProductPerformance"})
public class LoadProductPerformance extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session session = sf.openSession();

        try {
            Criteria criteria = session.createCriteria(OrderItems.class, "oi");
            criteria.createAlias("oi.product", "p");

            ProjectionList projectionList = Projections.projectionList()
                    .add(Projections.groupProperty("p.id")) // Group by product ID
                    .add(Projections.property("p.title"), "productTitle") // Select product title
                    .add(Projections.sum("oi.qty"), "qty");                    // Select SUM(qty) as alias 'qty'

            criteria.setProjection(projectionList);
            criteria.addOrder(Order.desc("qty"));                              // Order by alias 'qty'

            List<Object[]> results = criteria.list();

            if (results.isEmpty()) {
                responseObject.addProperty("message", "Order Items Are Not Found");
            } else {
                JsonArray dataArray = new JsonArray();

                for (Object[] row : results) {
                    String title = (String) row[1];
                    Number qty = (Number) row[2];

                    JsonObject item = new JsonObject();
                    item.addProperty("title", title);
                    item.addProperty("qty", qty != null ? qty.longValue() : 0);

                    dataArray.add(item);
                }

                responseObject.addProperty("status", true);
                responseObject.add("data", dataArray); // Match frontend: data[]
            }
        } catch (Exception e) {
            responseObject.addProperty("message", "Error: " + e.getMessage());
        } finally {
            session.close();
        }

        String toJson = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(toJson);
    }

}
