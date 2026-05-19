package com.amz.wms.dto;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class JwtErrorResponseWriter {

    public void writeUnauthorized(HttpServletResponse response,
                                  String path,
                                  String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("""
            {
              "timestamp": "%s",
              "status": 401,
              "error": "Unauthorized",
              "message": "%s",
              "path": "%s"
            }
        """.formatted(LocalDateTime.now(), message, path));
    }
}
