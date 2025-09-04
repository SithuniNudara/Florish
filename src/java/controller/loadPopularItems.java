package controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;

@WebServlet(name = "loadPopularItems", urlPatterns = {"/loadPopularItems"})
public class loadPopularItems extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();

        try {
            Criteria criteria = s.createCriteria(OrderItems.class);
            criteria.setProjection(Projections.projectionList()
                    .add(Projections.groupProperty("product.id"))
                    .add(Projections.sum("qty").as("total_sold"))
            );
            criteria.addOrder(Order.desc("total_sold"));
            criteria.setMaxResults(4);

            List<Object[]> resultList = criteria.list();

            JsonArray popularProducts = new JsonArray();

            for (Object[] row : resultList) {
                Integer productId = (Integer) row[0];
                Long totalSold = (Long) row[1];

                Product product = (Product) s.get(Product.class, productId);

                JsonObject productJson = new JsonObject();
                productJson.addProperty("product_id", productId);
                productJson.addProperty("product_name", product.getTitle());
                productJson.addProperty("product_price", product.getPrice());
                productJson.addProperty("product_description", product.getDescription());
                productJson.addProperty("product_category", product.getCategory().getValue());
                productJson.addProperty("total_sold", totalSold);

                popularProducts.add(productJson);
            }

            responseObject.add("popular_products", popularProducts);
            responseObject.addProperty("status", true);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            s.close();
        }

        String toJson = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(toJson);
    }

}
