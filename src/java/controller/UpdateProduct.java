package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Category;
import hibernate.HibernateUtil;
import hibernate.Product;
import hibernate.ProductStatus;
import hibernate.Volume;
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

@WebServlet(name = "UpdateProduct", urlPatterns = {"/UpdateProduct"})
public class UpdateProduct extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        try {
            JsonObject requestObject = gson.fromJson(request.getReader(), JsonObject.class);

            String idStr = requestObject.get("id").getAsString();
            String title = requestObject.get("title").getAsString();
            String priceStr = requestObject.get("price").getAsString();
            String qtyStr = requestObject.get("qty").getAsString();
            String statusStr = requestObject.get("status").getAsString();
            String categoryStr = requestObject.get("category").getAsString();
            String volumeStr = requestObject.get("volume").getAsString();
            String description = requestObject.get("description").getAsString();

            // Validation
            if (!Util.isInteger(idStr)) {
                responseObject.addProperty("message", "Invalid ID");
            } else if (title == null || title.trim().isEmpty()) {
                responseObject.addProperty("message", "Title cannot be empty");
            } else if (!Util.isDouble(priceStr)) {
                responseObject.addProperty("message", "Invalid price format");
            } else if (!Util.isInteger(qtyStr)) {
                responseObject.addProperty("message", "Quantity must be an integer");
            } else if (!Util.isInteger(statusStr)) {
                responseObject.addProperty("message", "Invalid status ID");
            } else if (!Util.isInteger(categoryStr)) {
                responseObject.addProperty("message", "Invalid category ID");
            } else if (!Util.isInteger(volumeStr)) {
                responseObject.addProperty("message", "Invalid volume ID");
            } else {
                int id = Integer.parseInt(idStr);
                double price = Double.parseDouble(priceStr);
                int qty = Integer.parseInt(qtyStr);
                int statusID = Integer.parseInt(statusStr);
                int categoryID = Integer.parseInt(categoryStr);
                int volumeID = Integer.parseInt(volumeStr);

                SessionFactory sf = HibernateUtil.getSessionFactory();
                Session s = sf.openSession();
                Transaction tx = s.beginTransaction();

                // Fetch the specific product
                Product product = (Product) s.get(Product.class, id);
                if (product == null) {
                    responseObject.addProperty("message", "Product not found");
                } else {
                    // Fetch related entities
                    ProductStatus pStatus = (ProductStatus) s.get(ProductStatus.class, statusID);
                    Category pCategory = (Category) s.get(Category.class, categoryID);
                    Volume pVolume = (Volume) s.get(Volume.class, volumeID);

                    // Update product
                    product.setTitle(title);
                    product.setPrice(price);
                    product.setQty(qty);
                    product.setProduct_status(pStatus);
                    product.setCategory(pCategory);
                    product.setVolumn(pVolume);
                    product.setDescription(description);

                    s.merge(product);
                    tx.commit();

                    responseObject.addProperty("status", true);
                    responseObject.addProperty("message", "Product updated successfully");
                }
                s.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
            responseObject.addProperty("message", "Error: " + e.getMessage());
        }

        String toString = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(toString);
    }

}
