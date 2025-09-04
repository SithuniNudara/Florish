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
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;


@WebServlet(name = "editProduct", urlPatterns = {"/editProduct"})
public class editProduct extends HttpServlet {

   @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
 Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);
        
          String id = request.getParameter("id"); 

        if (id == null || id.isEmpty()) {
            response.setContentType("application/json");
            response.getWriter().write(gson.toJson(responseObject));
            return;
        }

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();
        
        Criteria productCriteria = s.createCriteria(Product.class);
        productCriteria.add(Restrictions.eq("id", Integer.parseInt(id)));
        List<Product> productList = productCriteria.list();

        //Cateogry
        Criteria c1 = s.createCriteria(Category.class);

        List<Category> categoryList = c1.list();
        
        //Status
        
        Criteria c2 = s.createCriteria(ProductStatus.class);
        
        List<ProductStatus> productStatusList = c2.list();
        
        //Volume
        Criteria c3 = s.createCriteria(Volume.class);
        
        List<Volume> VolumeList = c3.list();
        
        
        
      

        s.close();

       
        responseObject.add("categoryList", gson.toJsonTree(categoryList));
        responseObject.add("productStatusList", gson.toJsonTree(productStatusList));
        responseObject.add("volumeList", gson.toJsonTree(VolumeList));
        responseObject.add("productList", gson.toJsonTree(productList));
        responseObject.addProperty("status", true);
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));

    }


   
}
