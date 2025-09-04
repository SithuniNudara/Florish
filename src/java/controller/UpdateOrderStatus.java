package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.OrderItems;
import hibernate.OrderStatus;
import hibernate.Orders;
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
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "UpdateOrderStatus", urlPatterns = {"/UpdateOrderStatus"})
public class UpdateOrderStatus extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);
        response.setContentType("application/json");

        String orderItemId = request.getParameter("id");
        String statusId = request.getParameter("status");

        // Validate parameters
        if (orderItemId == null || orderItemId.trim().isEmpty()) {
            responseObject.addProperty("message", "Order ID is empty");
        } else if (!Util.isInteger(orderItemId)) {
            responseObject.addProperty("message", "Invalid Order ID");
        } else if (statusId == null || statusId.trim().isEmpty()) {
            responseObject.addProperty("message", "Status ID is empty");
        } else if (!Util.isInteger(statusId)) {
            responseObject.addProperty("message", "Invalid Status ID");
        } else {

            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();
            Transaction tx = s.beginTransaction();

            OrderItems item = (OrderItems) s.get(OrderItems.class, Integer.valueOf(orderItemId));

            if (item != null) {
                OrderStatus newStatus = (OrderStatus) s.get(OrderStatus.class, Integer.valueOf(statusId));
                if (newStatus != null) {
                    item.setOrderStatus(newStatus);
                    s.update(item);
                    tx.commit();
                    responseObject.addProperty("status", true);
                    responseObject.addProperty("message", "Order item status updated");
                } else {
                    responseObject.addProperty("message", "Status not found");
                }
            } else {
                responseObject.addProperty("message", "Order item not found");
            }

        }

        response.getWriter().write(gson.toJson(responseObject));
    }
}
