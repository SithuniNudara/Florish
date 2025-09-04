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
import java.text.SimpleDateFormat;
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

@WebServlet(name = "LoadOrderDetails", urlPatterns = {"/LoadOrderDetails"})
public class LoadOrderDetails extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();

        //select * from order
        Criteria orderCriteria = s.createCriteria(Orders.class);
        List<Orders> orderList = orderCriteria.list();

        List invoicesArray = new ArrayList();

        int total = 0;
        int pending = 0;
        int processing = 0;
        int shipped = 0;
        int delivered = 0;

        if (orderList.isEmpty()) {
            responseObject.addProperty("message", "Orders Are Not Found");
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
                            itemObject.addProperty("item_id", item.getId());

                            if (item.getOrderStatus().getValue().equals("Pending")) {
                                itemObject.addProperty("status", "Pending");
                                pending += 1;

                            } else if (item.getOrderStatus().getValue().equals("Delivered")) {
                                itemObject.addProperty("status", "Delivered");
                                delivered += 1;

                            } else if (item.getOrderStatus().getValue().equals("Processing")) {
                                itemObject.addProperty("status", "Processing");
                                processing += 1;

                            } else {
                                itemObject.addProperty("status", "Shipped");
                                shipped += 1;
                            }

                            itemsArray.add(itemObject);
                            total += (int) (product.getPrice() * item.getQty());
                        }
                    }

                    User user = (User) s.get(User.class, orders.getUser().getId());

                    JsonObject orderObject = new JsonObject();
                    orderObject.addProperty("order_id", orders.getId());
                    if (user != null) {
                        orderObject.addProperty("customer_name", user.getFirstName() + " " + user.getLastName());
                    }
                    SimpleDateFormat sm = new SimpleDateFormat("dd/MM/yyyy");
                    String formattedDate = sm.format(orders.getCreatedAt());
                    orderObject.addProperty("order_date", formattedDate);
                    orderObject.add("items", gson.toJsonTree(itemsArray));

                    invoicesArray.add(orderObject);
                }
            }

            if (invoicesArray.size() > 0) {
                responseObject.addProperty("status", true);
                responseObject.addProperty("message", "Invoice data retrieved successfully");
                responseObject.add("data", gson.toJsonTree(invoicesArray));
                responseObject.addProperty("allTotal", Integer.valueOf(total));
                responseObject.addProperty("processing", Integer.valueOf(processing));
                responseObject.addProperty("pending", Integer.valueOf(pending));
                responseObject.addProperty("delivered", Integer.valueOf(delivered));
                responseObject.addProperty("shipped", Integer.valueOf(shipped));
            } else {
                responseObject.addProperty("message", "No complete invoice data found");
            }
        }

        s.beginTransaction().commit();

        if (orderList.isEmpty()) {
            responseObject.addProperty("message", "OrderList is Empty");
        } else {
            responseObject.addProperty("status", true);
            responseObject.add("message", gson.toJsonTree(orderList));
        }

        String toString = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(toString);
    }

}
