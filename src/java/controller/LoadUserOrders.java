package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.OrderItems;
import hibernate.Orders;
import hibernate.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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

@WebServlet(name = "LoadUserOrders", urlPatterns = {"/LoadUserOrders"})
public class LoadUserOrders extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();

        User user = (User) request.getSession().getAttribute("user");

        if (user == null) {
            responseObject.addProperty("message", "Please Login");
        } else {
            Criteria orderCriteria = s.createCriteria(Orders.class);
            orderCriteria.add(Restrictions.eq("user", user));

            List<Orders> orderList = orderCriteria.list();

            if (orderList.isEmpty()) {
                responseObject.addProperty("message", "You do not have any orders");
            } else {
                List<OrderItems> orderItemList = new ArrayList<>();

                for (Orders order : orderList) {
                    order.setMobile(null);
                    order.setUser(null);
                    order.setAddress(null);

                    Criteria itemCriteria = s.createCriteria(OrderItems.class);
                    itemCriteria.add(Restrictions.eq("orders", order)); // Compare entity to entity

                    orderItemList.addAll(itemCriteria.list());
                }

                responseObject.add("OrderList", gson.toJsonTree(orderList));
                responseObject.add("OrderItemList", gson.toJsonTree(orderItemList));
                responseObject.addProperty("status", true);
            }
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));
    }

}
