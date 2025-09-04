package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.Mobile;
import hibernate.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "SaveUserMainData", urlPatterns = {"/SaveUserMainData"})
public class SaveUserMainData extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        JsonObject userData = gson.fromJson(request.getReader(), JsonObject.class);

        String firstname = userData.get("firstname").getAsString();
        String lastname = userData.get("lastname").getAsString();
        String phone1 = userData.get("phone1").getAsString();
        String phone2 = userData.get("phone2").getAsString();

        if (firstname.isEmpty()) {
            responseObject.addProperty("message", "First Name is Empty");
        } else if (lastname.isEmpty()) {
            responseObject.addProperty("message", "Last Name is Empty");
        } else if (phone1.isEmpty()) {
            responseObject.addProperty("message", "Mobile Number One is Empty");
        } else if (!Util.isValidPhone(phone1)) {
            responseObject.addProperty("message", "Mobile Number One is Invalid");
        } else if (phone2.isEmpty()) {
            responseObject.addProperty("message", "Mobile Number Two is Empty");
        } else if (!Util.isValidPhone(phone2)) {
            responseObject.addProperty("message", "Mobile Number Two is Invalid");
        } else {
            HttpSession User = request.getSession(false);

            if (User == null && User.getAttribute("user") == null) {
                responseObject.addProperty("message", "Please Login");
            } else {
                SessionFactory sf = HibernateUtil.getSessionFactory();
                Session s = sf.openSession();
//session user
                User SessionUser = (User) User.getAttribute("user");
                int id = SessionUser.getId();
                //dbuser          
                Criteria criteria = s.createCriteria(User.class);
                criteria.add(Restrictions.eq("id", Integer.valueOf(id)));

                if (criteria.list().isEmpty()) {
                    System.out.println("Empty");
                } else {
                    //save

                    // 1. First check if user exists
                    User dbuser = (User) criteria.uniqueResult();
                    if (dbuser == null) {
                        responseObject.addProperty("message", "User not found");
                    } else {
                        // 2. Update user data
                        dbuser.setFirstName(firstname);
                        dbuser.setLastName(lastname);
                        s.merge(dbuser);

                        // 3. Handle mobile data
                        Criteria mobileCriteria = s.createCriteria(Mobile.class);
                        mobileCriteria.add(Restrictions.eq("user", dbuser));
                        Mobile userMobile = (Mobile) mobileCriteria.uniqueResult();

                        if (userMobile == null) {
                            // Create new Mobile record if none exists
                            userMobile = new Mobile();
                            userMobile.setUser(dbuser);
                            userMobile.setMobile1(phone1);
                            userMobile.setMobile2(phone2);
                            s.save(userMobile); // Use save() for new entities
                            s.beginTransaction().commit();
                        } else {
                            // Update existing Mobile record
                            userMobile.setMobile1(phone1);
                            userMobile.setMobile2(phone2);
                            s.merge(userMobile);
                            s.beginTransaction().commit();
                        }

                        responseObject.addProperty("message", "Successfully Updated");
                        responseObject.addProperty("status", true);
                    }

                }

                s.close();
            }

        }

        String tojson = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(tojson);
    }

}
