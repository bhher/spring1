package com.example.boardlogin.web;

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
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(AuthController.SESSION_USER) == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }
        return true;
    }
}


// @Component
// public class LoginCheckInterceptor implements HandlerInterceptor {

//     @Override
//     public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
//             throws Exception {

//         HttpSession session = request.getSession(false);

//         if (session == null || session.getAttribute(SessionConst.LOGIN_USER) == null) {

//             // AJAX 요청 처리
//             String ajaxHeader = request.getHeader("X-Requested-With");
//             if ("XMLHttpRequest".equals(ajaxHeader)) {
//                 response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                 return false;
//             }

//             // 로그인 후 돌아올 URL 저장
//             String requestURI = request.getRequestURI();
//             response.sendRedirect(request.getContextPath() + "/login?redirectURL=" + requestURI);
//             return false;
//         }

//         return true;
//     }
// }