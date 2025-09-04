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
import org.hibernate.criterion.Restrictions;


@WebServlet(name = "SearchProductItem", urlPatterns = {"/SearchProductItem"})
public class SearchProductItem extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
           Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);
        
        String keyword = request.getParameter("keyword");
        
        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();
        
        Criteria productCriteria  = s.createCriteria(Product.class);
        productCriteria.add(Restrictions.eq("title", keyword));
        List<Product> productList = productCriteria.list();
       responseObject.add("searchList", gson.toJsonTree(productList));
        responseObject.addProperty("status", true);
        
        
          String toString = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(toString);
    }

}
