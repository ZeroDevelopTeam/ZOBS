package com.zero.basic.filter;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 基础拦截器
 * @auther Deram Zhao
 * @creatTime 2017/6/1
 */
@WebFilter(filterName = "BasicFilter",urlPatterns = "/*")
public class BasicFilter implements Filter {
    private static  final Logger LOGGER= LoggerFactory.getLogger(BasicFilter.class);
    public  static String user_id ="";
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("过滤器初始化");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        LOGGER.info(request.getRequestURI()+"执行过滤操作");
        user_id = request.getHeader("user_id");
        filterChain.doFilter(servletRequest, servletResponse);

//        if (session.getAttribute("user")!=null){
//            filterChain.doFilter(servletRequest, servletResponse);
//        }else {
//            response.sendRedirect(request.getContextPath()+"/login");
//        }

    }

    @Override
    public void destroy() {
        System.out.println("过滤器销毁");
    }
}
