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

@WebServlet(name = "SearchProducts", urlPatterns = {"/SearchProducts"})
public class SearchProducts extends HttpServlet {

    private static final int Max_Result = 6;
    private static final int Active_Id = 1;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        JsonObject requestJsonObject = gson.fromJson(request.getReader(), JsonObject.class);

        //connection
        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();

        //get all products for the filter
        Criteria c1 = s.createCriteria(Product.class);

        if (requestJsonObject.has("category") && !requestJsonObject.get("category").isJsonNull()) {

            String CategoryValue = requestJsonObject.get("category").getAsString();

            if (CategoryValue != null && !CategoryValue.isEmpty()) {
                Criteria c4 = s.createCriteria(Category.class);
                c4.add(Restrictions.eq("value", CategoryValue));
                Category category = (Category) c4.uniqueResult();

                if (category != null) {
                    // filter product by category only if found
                    c1.add(Restrictions.eq("category", category));
                }
            }
        }

//        if (requestJsonObject.has("category")) {
//
//            String CategoryValue = requestJsonObject.get("category").getAsString();
//
//            //get quality details
//            Criteria c4 = s.createCriteria(Category.class);
//            c4.add(Restrictions.eq("value", CategoryValue));
//            Category category = (Category) c4.uniqueResult();
//
//            //filter product by using Category
//            c1.add(Restrictions.eq("category", category));
//
//        }
//        if (requestJsonObject.has("volume")) {
//
//            String volumeName = requestJsonObject.get("volume").getAsString();
//
//            //get color details
//            Criteria c5 = s.createCriteria(Volume.class);
//            c5.add(Restrictions.eq("value", volumeName));
//            Volume volume = (Volume) c5.uniqueResult();
//
//            //filter product by using Volume
//            c1.add(Restrictions.eq("volumn", volume));
//
//        }
        if (requestJsonObject.has("volume") && !requestJsonObject.get("volume").isJsonNull()) {

            String volumeName = requestJsonObject.get("volume").getAsString();

            if (volumeName != null && !volumeName.isEmpty()) {
                Criteria c5 = s.createCriteria(Volume.class);
                c5.add(Restrictions.eq("value", volumeName));
                Volume volume = (Volume) c5.uniqueResult();

                if (volume != null) {
                    c1.add(Restrictions.eq("volumn", volume));
                }
            }
        }
        if (requestJsonObject.has("priceStart") && requestJsonObject.has("priceEnd")) {
            double priceStart = requestJsonObject.get("priceStart").getAsDouble();
            double priceEnd = requestJsonObject.get("priceEnd").getAsDouble();

            c1.add(Restrictions.ge("price", priceStart));
            c1.add(Restrictions.le("price", priceEnd));
        }

        if (requestJsonObject.has("sort") && !requestJsonObject.get("sort").isJsonNull()) {
            String sortValue = requestJsonObject.get("sort").getAsString();

            switch (sortValue) {
                case "Newest Arrivals":
                    c1.addOrder(Order.desc("id"));
                    break;
                case "Early Arrivals":
                    c1.addOrder(Order.asc("id"));
                    break;
                case "Price: Low to High":
                    c1.addOrder(Order.asc("price"));
                    break;
                case "Price: High to Low":
                    c1.addOrder(Order.desc("price"));
                    break;
            }
        }

        //add all product count
        responseObject.addProperty("allProductCount", c1.list().size());

        if (requestJsonObject.has("firstResult")) {
            int firstResult = requestJsonObject.get("firstResult").getAsInt();
            c1.setFirstResult(firstResult);
            c1.setMaxResults(SearchProducts.Max_Result);
        }

        ProductStatus status = (ProductStatus) s.get(ProductStatus.class, SearchProducts.Active_Id);

        c1.add(Restrictions.eq("product_status", status));
        List<Product> productList = c1.list();

        //hibernate session close
        s.close();

        responseObject.add("productList", gson.toJsonTree(productList));
        responseObject.addProperty("status", true);

        response.setContentType("application/json");
        String toJson = gson.toJson(responseObject);
        response.getWriter().write(toJson);

    }

}
