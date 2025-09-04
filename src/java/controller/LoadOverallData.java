package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.OrderItems;
import hibernate.Orders;
import hibernate.Product;
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

@WebServlet(name = "LoadOverallData", urlPatterns = {"/LoadOverallData"})
public class LoadOverallData extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();
        
        int totalUsers = 0;
        Criteria c1 = s.createCriteria(User.class);
        c1.addOrder(Order.desc("id"));
        List<User> userList = c1.list();
        
        for (User user : userList) {
            totalUsers+=1;
        }
        int totalProduct = 0;
        Criteria c2 = s.createCriteria(Product.class);
        c2.addOrder(Order.desc("id"));
        List<Product> productList = c2.list();
        
        for (Product product : productList) {
            totalProduct +=1;
        }
        
        int totalRevenue = 0;
        Criteria c3 = s.createCriteria(OrderItems.class);
        c3.addOrder(Order.desc("id"));
        List<OrderItems> orderItems = c3.list();
        
        for (OrderItems orderItem : orderItems) {
           totalRevenue += orderItem.getProduct().getPrice();
        }
        
        int totalOrders = 0;
        Criteria c4 = s.createCriteria(Orders.class);
        c4.addOrder(Order.desc("id"));
        List<Orders> orderList = c4.list();
        
        for (Orders orders : orderList) {
            totalOrders += 1;
        }
        
        responseObject.add("userList", gson.toJsonTree(userList));
        responseObject.add("productList", gson.toJsonTree(productList));
        responseObject.add("orderItemsList", gson.toJsonTree(orderItems));
        responseObject.add("orderList", gson.toJsonTree(orderList));
        responseObject.addProperty("totalUsers", totalUsers);
        responseObject.addProperty("totalProducts", totalProduct);
        responseObject.addProperty("totalIncome", totalRevenue);
        responseObject.addProperty("totalorders", totalOrders);
        responseObject.addProperty("status", true);

        String toString = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(toString);
    }

}
