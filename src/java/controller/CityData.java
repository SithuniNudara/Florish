package controller;

import com.google.gson.Gson;
import hibernate.City;
import hibernate.HibernateUtil;
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

@WebServlet(name = "CityData", urlPatterns = {"/CityData"})
public class CityData extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //connection
        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();

        //select * cities
        Criteria c = s.createCriteria(City.class);
        List<City> list = c.list();

        Gson gson = new Gson();
        String Json = gson.toJson(list);
        response.setContentType("application/json");
        response.getWriter().write(Json);
        s.close();
    }

}
