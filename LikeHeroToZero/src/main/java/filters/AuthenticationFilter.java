package filters;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.User;
import java.io.IOException;

@WebFilter(filterName = "AuthFilter", urlPatterns = {"*.xhtml"})
public class AuthenticationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        try {
            HttpServletRequest reqt = (HttpServletRequest) request;
            HttpServletResponse resp = (HttpServletResponse) response;
            HttpSession ses = reqt.getSession(false);
            String reqURI = reqt.getRequestURI();

            // Public pages
            if (reqURI.contains("/login.xhtml")
                    || reqURI.contains("/register.xhtml")
                    || reqURI.contains("/index.xhtml")
                    || reqURI.contains("/public/")
                    || reqURI.contains("jakarta.faces.resource")) {
                chain.doFilter(request, response);
                return;
            }

            // secured pages
            if (reqURI.contains("/secured/") || reqURI.contains("/admin/") || reqURI.contains("/manager/")) {
                if (ses != null && ses.getAttribute("user") != null) {
                    User user = (User) ses.getAttribute("user");
                    
                    // Admin pages
                    if (reqURI.contains("/admin/") && !"ADMIN".equals(user.getRoleName())) {
                        resp.sendRedirect(reqt.getContextPath() + "/unauthorized.xhtml");
                        return;
                    }
                    // Scientist pages
                    if (reqURI.contains("/manager/") && !"MANAGER".equals(user.getRoleName())) {
                        resp.sendRedirect(reqt.getContextPath() + "/unauthorized.xhtml");
                        return;
                    }
                    
                    chain.doFilter(request, response);
                } else {
                    resp.sendRedirect(reqt.getContextPath() + "/login.xhtml");
                }
            } else {
                // allow access for other pages
                chain.doFilter(request, response);
            }
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void destroy() {
    }
}