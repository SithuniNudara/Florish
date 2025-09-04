package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Category;
import hibernate.HibernateUtil;
import hibernate.OrderItems;
import hibernate.Product;
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

@WebServlet(name = "LoadSigleProduct", urlPatterns = {"/LoadSigleProduct"})
public class LoadSigleProduct extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        String productId = request.getParameter("id");

        if (Util.isInteger(productId)) {

            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();
            
            

            try {
                
                Criteria orderItemsCriteria = s.createCriteria(OrderItems.class);
                orderItemsCriteria.add(Restrictions.eq("product.id", Integer.parseInt(productId)));
                List<OrderItems>  ratings = orderItemsCriteria.list();
                
                for (OrderItems rating : ratings) {
                    rating.setDeliveryType_id(null);
                    rating.setId(0);
                    rating.setOrderStatus(null);
                    rating.setQty(0);
                }
                
                Product product = (Product) s.get(Product.class, Integer.valueOf(productId));
                System.out.println(product.getProduct_status().getValue());

                if (product.getProduct_status().getValue().equalsIgnoreCase("In Stock")) {

                    Criteria c1 = s.createCriteria(Product.class);
                    c1.add(Restrictions.eq("category", product.getCategory()));
                    c1.add(Restrictions.ne("id", product.getId()));
                    c1.setMaxResults(4);
                    List<Product> productList = c1.list();

                    c1.setMaxResults(4);

                    //similar products
                    responseObject.add("product", gson.toJsonTree(product));
                    responseObject.add("productList", gson.toJsonTree(productList));
                    responseObject.add("ratings", gson.toJsonTree(ratings));
                    responseObject.addProperty("status", true);

                } else {
                    responseObject.addProperty("message", "Product Not Found!");
                }

            } catch (Exception e) {
                responseObject.addProperty("message", "Product Not Found");
            }

        } else {
            responseObject.addProperty("message", "Invalid Product Id");
        }
        response.setContentType("application/json");
        String toJosn = gson.toJson(responseObject);
        response.getWriter().write(toJosn);
    }

}
