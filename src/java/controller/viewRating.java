package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.OrderItems;
import hibernate.Orders;
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

@WebServlet(name = "viewRating", urlPatterns = {"/viewRating"})
public class viewRating extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();

        String orderitemId = request.getParameter("itemId");

        Criteria orderItems = s.createCriteria(OrderItems.class);
        orderItems.add(Restrictions.eq("id", Integer.valueOf(orderitemId)));
        List<OrderItems> orderItemsList = orderItems.list();

        if (!orderItemsList.isEmpty()) {
            OrderItems orderItem = orderItemsList.get(0);

            JsonObject reviewData = new JsonObject();
            reviewData.addProperty("rating", orderItem.getRating());
            reviewData.addProperty("order_date", orderItem.getOrders().getCreatedAt().toString());
            reviewData.addProperty("comment", orderItem.getComment());

            responseObject.add("review", reviewData);
            responseObject.addProperty("status", true);
        }

        String toString = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(toString);
    }

}
