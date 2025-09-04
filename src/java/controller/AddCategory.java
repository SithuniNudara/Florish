package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Category;
import hibernate.HibernateUtil;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "AddCategory", urlPatterns = {"/AddCategory"})
public class AddCategory extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        String category = request.getParameter("model");
        // System.out.println(category);

        if (category.isEmpty()) {
            responseObject.addProperty("message", "Category is Empty");
        } else {

            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();

            Criteria c1 = s.createCriteria(Category.class);
            c1.add(Restrictions.eq("value", category));

            if (!c1.list().isEmpty()) {
                responseObject.addProperty("status", false);
                responseObject.addProperty("message", "Category is Already Exists!");
            } else {
                Category cat = new Category();
                cat.setValue(String.valueOf(category));
                s.merge(cat);
                s.beginTransaction().commit();
                s.close();
                responseObject.addProperty("status", true);
                responseObject.addProperty("message", "Successfully Added!");
            }
        }

        response.setContentType("application/json");
        String toJosn = gson.toJson(responseObject);
        response.getWriter().write(toJosn);
    }

}
