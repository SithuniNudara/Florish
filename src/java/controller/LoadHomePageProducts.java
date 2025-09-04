package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
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

@WebServlet(name = "LoadHomePageProducts", urlPatterns = {"/LoadHomePageProducts"})
public class LoadHomePageProducts extends HttpServlet {

    public static final int Max_Result = 4;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

 
        String finalResParam = request.getParameter("finalres");
        int firstResult = 0;
        if (finalResParam != null && !finalResParam.isEmpty()) {
            try {
                firstResult = Integer.parseInt(finalResParam);
            } catch (NumberFormatException e) {
               
                firstResult = 0;
            }
        }

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();

        try {
            Criteria c1 = s.createCriteria(Product.class);

            
            responseObject.addProperty("allProductCount", c1.list().size());

            
            c1.setFirstResult(firstResult);
            c1.setMaxResults(Max_Result);
            List<Product> productList = c1.list();

            responseObject.add("popular_products", gson.toJsonTree(productList));
            responseObject.addProperty("status", true);

        } catch (Exception e) {
            responseObject.addProperty("message", "Server error: " + e.getMessage());
        } finally {
            s.close();
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));
    }
}
