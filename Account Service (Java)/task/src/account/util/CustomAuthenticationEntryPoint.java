package account.util;

import com.fasterxml.jackson.databind.util.JSONPObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.json.JSONObject;

import java.io.IOException;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        System.out.println(authException.getMessage());
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        response.getWriter().write(new JSONObject()
                .put("timestamp", System.currentTimeMillis())
                .put("status", 401)
                .put("error", "Unauthorized")
                .put("message", authException.getMessage())
                .put("path", request.getRequestURI())
                .toString());
    }
}
