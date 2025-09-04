package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Category;
import hibernate.HibernateUtil;
import hibernate.Volume;
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


@WebServlet(name = "AddVolume", urlPatterns = {"/AddVolume"})
public class AddVolume extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        String volume = request.getParameter("volume");
        // System.out.println(category);

        if (volume.isEmpty()) {
            responseObject.addProperty("message", "Volume is Empty");
        } else {

            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();

            Criteria c1 = s.createCriteria(Volume.class);
            c1.add(Restrictions.eq("value", volume));

            if (!c1.list().isEmpty()) {
                responseObject.addProperty("status", false);
                responseObject.addProperty("message", "Volume is Already Exists!");
            } else {
                Volume vol = new Volume();
                vol.setValue(String.valueOf(volume));
                s.merge(vol);
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
