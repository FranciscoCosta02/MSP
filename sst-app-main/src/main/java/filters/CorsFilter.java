package filters;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@WebFilter(filterName="CorsFilter")
public class CorsFilter implements Filter {

    public void init(FilterConfig config) throws ServletException{

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse resp = (HttpServletResponse)  response;

        resp.addHeader("Access-Control-Allow-Methods", "*");
        resp.addHeader("Access-Control-Allow-Origin", "*");
        resp.addHeader("Access-Control-Allow-Headers", "*");

        chain.doFilter(request,response);
    }

    public void destroy() {}
    //http://localhost:3000
    /*@Override
    public void doFilter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
            throws IOException {
        //responseContext.getHeaders().add("Access-Control-Allow-Methods", "HEAD,GET,PUT,POST,DELETE,OPTIONS");
        responseContext.getHeaders().add("Access-Control-Allow-Origin", "*");
        //responseContext.getHeaders().add("Access-Control-Allow-Headers", "Content-Type, X-Requested-With, Authorization");
    }*/
}