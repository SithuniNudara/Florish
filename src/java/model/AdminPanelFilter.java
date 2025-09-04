package model;

import hibernate.User;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebFilter(filterName = "Admin-Dashboard.html", urlPatterns = {"/Admin-Dashboard.html",
    "/User-Management.html",
    "/Product-Registration.html",
    "/Admin-Profile.html",
"/Payments.html",
"/Order.html"})
public class AdminPanelFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        HttpSession sess = request.getSession(false);
        if (sess != null && sess.getAttribute("user") != null) {
            User user = (User) sess.getAttribute("user");
            
            if(user.getUserType() != null && user.getUserType().getId() == 1){
                chain.doFilter(request, response);
            }else{
                response.sendRedirect("Sign-In.html");
            }
            
            
        } else {
            response.sendRedirect("Sign-In.html");
        }

    }

    @Override
    public void destroy() {

    }
}
