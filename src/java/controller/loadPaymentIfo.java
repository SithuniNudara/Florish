package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.OrderItems;
import hibernate.Orders;
import hibernate.Product;
import hibernate.User;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

@WebServlet(name = "loadPaymentIfo", urlPatterns = {"/loadPaymentIfo"})
public class loadPaymentIfo extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();

        List invoicesArray = new ArrayList();

        Criteria orderCriteria = s.createCriteria(Orders.class);
        List<Orders> orderList = orderCriteria.list();
        
        
        int total = 0;
        
        
        if (orderList.isEmpty()) {
            response.getWriter().write("Order Not Found");
        } else {

            for (Orders orders : orderList) {

                Criteria orderItemCriteria = s.createCriteria(OrderItems.class);
                orderItemCriteria.add(Restrictions.eq("orders.id", orders.getId()));
                List<OrderItems> orderItems = orderItemCriteria.list();

                if (orderItems.isEmpty()) {
                    responseObject.addProperty("message", "Order Items Not Found");

                } else {
                    List itemsArray = new ArrayList();
                    for (OrderItems item : orderItems) {

                        Product product = (Product) s.get(Product.class, item.getProduct().getId());

                        if (product != null) {
                            JsonObject itemObject = new JsonObject();
                            itemObject.addProperty("product_name", product.getTitle());
                            itemObject.addProperty("unit_price", product.getPrice());
                            itemObject.addProperty("quantity", item.getQty());
                            itemObject.addProperty("line_total", product.getPrice() * item.getQty());

                            itemsArray.add(itemObject);
                            total += (int) (product.getPrice() * item.getQty());
                        }
                    }

                    // Get user details
                    User user = (User) s.get(User.class, orders.getUser().getId());

                    JsonObject orderObject = new JsonObject();
                    orderObject.addProperty("order_id", orders.getId());
                    SimpleDateFormat sm = new SimpleDateFormat("dd/MM/yyyy");
                    String formattedDate = sm.format(orders.getCreatedAt());
                    orderObject.addProperty("order_date", formattedDate);
                    if (user != null) {
                        orderObject.addProperty("customer_name", user.getFirstName());
                    }
                    orderObject.add("items", gson.toJsonTree(itemsArray));

                    invoicesArray.add(orderObject);
                }
            }

            if (invoicesArray.size() > 0) {
                responseObject.addProperty("status", true);
                responseObject.addProperty("message", "Invoice data retrieved successfully");
                responseObject.add("data", gson.toJsonTree(invoicesArray));
                responseObject.addProperty("allTotal", Integer.valueOf(total));
            } else {
                responseObject.addProperty("message", "No complete invoice data found");
            }
        }
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));
    }

}
