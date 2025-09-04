package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Address;
import hibernate.HibernateUtil;
import hibernate.User;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "deleteUserAddress", urlPatterns = {"/deleteUserAddress"})
public class deleteUserAddress extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        HttpSession LoggedUser = request.getSession(false);

        if (LoggedUser == null && LoggedUser.getAttribute("user") == null) {
            responseObject.addProperty("message", "Please Login");
        } else {
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();

            User SessUser = (User) LoggedUser.getAttribute("user");
            int id = SessUser.getId();

            Criteria userCriteria = s.createCriteria(User.class);
            userCriteria.add(Restrictions.eq("id", id));

            if (userCriteria.list().isEmpty()) {
                responseObject.addProperty("message", "Invaild User");
            } else {
                User dbUser = (User) userCriteria.uniqueResult();

                Criteria addressCriteria = s.createCriteria(Address.class);
                addressCriteria.add(Restrictions.eq("user", SessUser));
                Address addressObject = (Address) addressCriteria.uniqueResult();

                if (addressObject == null) {
                    responseObject.addProperty("message", "No Address Saved!");
                } else {

                    s.delete(addressObject);
                    s.beginTransaction().commit();

                    responseObject.addProperty("message", "Address Deleted Successfully!");
                    responseObject.addProperty("status", true);
                }
                s.close();
            }

            String tojson = gson.toJson(responseObject);
            response.setContentType("application/json");
            response.getWriter().write(tojson);
        }
    }

}
