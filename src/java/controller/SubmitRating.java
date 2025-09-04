package controller;

import com.google.gson.Gson;
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
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "SubmitRating", urlPatterns = {"/SubmitRating"})
public class SubmitRating extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);
        
        JsonObject requestObject = gson.fromJson(request.getReader(), JsonObject.class);

        String id = requestObject.get("productId").getAsString();
        String orderId = requestObject.get("orderId").getAsString();
        String rating = requestObject.get("rating").getAsString();
        String comment = requestObject.get("comment").getAsString();

        if (id == null || id.isEmpty()) {
            responseObject.addProperty("message", "Product ID is required");
        } else if (!Util.isInteger(id)) {
            responseObject.addProperty("message", "Product ID must be an integer");
        } else if (rating == null || rating.isEmpty()) {
            responseObject.addProperty("message", "Rating is required");
        } else if (!Util.isInteger(rating)) {
            responseObject.addProperty("message", "Rating must be an integer");
        } else {
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session session = sf.openSession();

            try {
                int productId = Integer.parseInt(id);
                int ratingValue = Integer.parseInt(rating);

                Criteria criteria = session.createCriteria(OrderItems.class);
                criteria.add(Restrictions.eq("orders.id", Integer.parseInt(orderId)));
                criteria.add(Restrictions.eq("product.id", Integer.parseInt(id))); 
                List<OrderItems> orderItemsList = criteria.list();

                if (!orderItemsList.isEmpty()) {
                    session.beginTransaction();
                    for (OrderItems orderItem : orderItemsList) {
                        orderItem.setRating(ratingValue);
                        orderItem.setComment(comment);
                        session.merge(orderItem);
                    }
                    session.getTransaction().commit();

                    responseObject.addProperty("status", true);
                    responseObject.addProperty("message", "Rating added successfully");
                } else {
                    responseObject.addProperty("message", "No matching order item found for this product");
                }
            } catch (Exception e) {
                responseObject.addProperty("message", "Server error: " + e.getMessage());
            } finally {
                session.close();
            }
        }

        String jsonResponse = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(jsonResponse);
    }

}
