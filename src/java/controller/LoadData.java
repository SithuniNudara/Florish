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
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "LoadData", urlPatterns = {"/LoadData"})
public class LoadData extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();

        Criteria c1 = s.createCriteria(Category.class);

        List<Category> categoryList = c1.list();

        Criteria c2 = s.createCriteria(Volume.class);

        List<Volume> volumeList = c2.list();

//        Criteria c3 = s.createCriteria(ProductStatus.class);
//
//        List<ProductStatus> productStatusList = c3.list();

        ProductStatus status = (ProductStatus) s.get(ProductStatus.class, 1);
        Criteria c6 = s.createCriteria(Product.class);
        c6.addOrder(Order.desc("id"));
        c6.add(Restrictions.eq("product_status", status));
        responseObject.add("allProductCount", gson.toJsonTree(c6.list().size()));
        // load product data end
        c6.setFirstResult(0);
        c6.setMaxResults(6);

        List<Product> productList = c6.list();

        responseObject.add("categoryList", gson.toJsonTree(categoryList));
        responseObject.add("volumeList", gson.toJsonTree(volumeList));
  //      responseObject.add("statusList", gson.toJsonTree(productStatusList));

        //responseObject.add("productList", gson.toJsonTree(productList.size()));
        responseObject.add("productList", gson.toJsonTree(productList));
        responseObject.addProperty("status", true);

        String toJson = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(toJson);
        s.close();

    }

}
