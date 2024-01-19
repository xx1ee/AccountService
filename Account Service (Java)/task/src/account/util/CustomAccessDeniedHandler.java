package account.util;

import account.models.SecurityAction;
import account.models.SecurityEvent;
import account.services.SecurityEventService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Autowired
    SecurityEventService securityEventService;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        securityEventService.save(new SecurityEvent(String.valueOf(System.currentTimeMillis()),String.valueOf(SecurityAction.ACCESS_DENIED), request.getUserPrincipal().getName().toLowerCase(), request.getRequestURI(), request.getRequestURI() ));
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        response.getWriter().write(new JSONObject()
                .put("timestamp", System.currentTimeMillis())
                .put("status", 403)
                .put("error", "Forbidden")
                .put("message", "Access Denied!")
                .put("path", request.getRequestURI())
                .toString());
    }
}
