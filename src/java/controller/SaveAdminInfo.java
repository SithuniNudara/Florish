package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Address;
import hibernate.City;
import hibernate.HibernateUtil;
import hibernate.Mobile;
import hibernate.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

@MultipartConfig
@WebServlet(name = "SaveAdminInfo", urlPatterns = {"/SaveAdminInfo"})
public class SaveAdminInfo extends HttpServlet {

   @Override
protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    Gson gson = new Gson();
    JsonObject responseObject = new JsonObject();
    responseObject.addProperty("status", false);

    String fname = request.getParameter("fname");
    String lname = request.getParameter("lname");
    String line1 = request.getParameter("ad1");
    String line2 = request.getParameter("ad2");
    String postalCode = request.getParameter("pc");
    String city = request.getParameter("c");
    String mobile1 = request.getParameter("m1");
    String mobile2 = request.getParameter("m2");

    // Basic field validations
    if (fname == null || fname.isEmpty()) {
        responseObject.addProperty("message", "First Name is Empty");
    } else if (lname == null || lname.isEmpty()) {
        responseObject.addProperty("message", "Last Name is Empty");
    } else if (mobile1 == null || mobile1.isEmpty()) {
        responseObject.addProperty("message", "Mobile Number One is Empty");
    } else if (!Util.isValidPhone(mobile1)) {
        responseObject.addProperty("message", "Mobile Number One is Invalid");
    } else if (mobile2 == null || mobile2.isEmpty()) {
        responseObject.addProperty("message", "Mobile Number Two is Empty");
    } else if (!Util.isValidPhone(mobile2)) {
        responseObject.addProperty("message", "Mobile Number Two is Invalid");
    } else if (postalCode == null || postalCode.isEmpty() || postalCode.length() > 5) {
        responseObject.addProperty("message", "Postal Code is invalid or too long (max 5 digits)");
    } else if (mobile2.length() > 10) {
        responseObject.addProperty("message", "Mobile Number Two is too long");
    } else {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            responseObject.addProperty("message", "Please login to continue");
        } else {
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();
            Transaction tx = s.beginTransaction();

            try {
                User sessionUser = (User) session.getAttribute("user");

                Criteria userCriteria = s.createCriteria(User.class);
                userCriteria.add(Restrictions.eq("id", sessionUser.getId()));
                User dbuser = (User) userCriteria.uniqueResult();

                if (dbuser == null) {
                    responseObject.addProperty("message", "User not found");
                } else {
                    // Update User
                    dbuser.setFirstName(fname);
                    dbuser.setLastName(lname);
                    s.merge(dbuser);

                    // Check City
                    Criteria cityCriteria = s.createCriteria(City.class);
                    cityCriteria.add(Restrictions.eq("id", Integer.parseInt(city)));
                    City cityObj = (City) cityCriteria.uniqueResult();

                    if (cityObj == null) {
                        responseObject.addProperty("message", "Selected city not found in database.");
                    } else {
                        // Address update/create
                        Criteria addressCriteria = s.createCriteria(Address.class);
                        addressCriteria.add(Restrictions.eq("user", dbuser));
                        Address dbAddress = (Address) addressCriteria.uniqueResult();

                        if (dbAddress == null) {
                            dbAddress = new Address();
                         //   dbAddress.setUser(dbuser);
                        }

                        dbAddress.setLine1(line1);
                        dbAddress.setLine2(line2);
                        dbAddress.setPostalCode(postalCode);
                        dbAddress.setRecieverMobile(mobile2);
                        dbAddress.setFirstName(fname);
                        dbAddress.setLastName(lname);
                        dbAddress.setCity(cityObj);

                        s.saveOrUpdate(dbAddress);

                        // Mobile update/create
                        Criteria mobileCriteria = s.createCriteria(Mobile.class);
                        mobileCriteria.add(Restrictions.eq("user", dbuser));
                        Mobile mobile = (Mobile) mobileCriteria.uniqueResult();

                        if (mobile == null) {
                            mobile = new Mobile();
                            mobile.setUser(dbuser);
                        }

                        mobile.setMobile1(mobile1);
                        mobile.setMobile2(mobile2);
                        s.saveOrUpdate(mobile);

                        // Commit all
                        tx.commit();

                        responseObject.addProperty("message", "Successfully updated user information.");
                        responseObject.addProperty("status", true);
                    }
                }

            } catch (Exception e) {
                tx.rollback();
                e.printStackTrace(); // View error in console logs
                responseObject.addProperty("message", "Internal Error: " + e.getMessage());
            } finally {
                s.close();
            }
        }
    }

    response.setContentType("application/json");
    response.getWriter().write(gson.toJson(responseObject));
}

}
