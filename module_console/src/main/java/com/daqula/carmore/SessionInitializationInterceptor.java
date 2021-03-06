package com.daqula.carmore;

import com.daqula.carmore.model.acl.ERPUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class SessionInitializationInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private ApplicationContext context;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        if (request.getSession().getAttribute("INITIALIZED") != null) return true;

        try {
            ERPUser user = (ERPUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            request.getSession().setAttribute("USER_NAME", user.username);
            request.getSession().setAttribute("USER_PWD", user.password);
            request.getSession().setAttribute("ROLE", user.role);
            request.getSession().setAttribute("INITIALIZED", true);

        } catch (Exception ignored) {}

        return true;
    }
}
