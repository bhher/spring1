package com.example.boardloginimg.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String method = request.getMethod();
        String path = request.getRequestURI();

        if ("GET".equals(method) && ("/api/posts".equals(path) || path.matches("/api/posts/\\d+"))) {
            return true;
        }
        if (!requiresAuth(path, method)) {
            return true;
        }
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(AuthApiController.SESSION_USER) == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"message\":\"로그인이 필요합니다.\"}");
            return false;
        }
        return true;
    }

    private static boolean requiresAuth(String path, String method) {
        if ("POST".equals(method) && "/api/posts".equals(path)) {
            return true;
        }
        if ("POST".equals(method) && path.matches("/api/posts/\\d+/update")) {
            return true;
        }
        if ("DELETE".equals(method) && path.matches("/api/posts/\\d+")) {
            return true;
        }
        return false;
    }
}
