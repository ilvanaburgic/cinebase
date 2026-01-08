package com.sdp.cinebase.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI/Swagger configuration for CineBase REST API.
 * <p>
 * Access Swagger UI at: http://localhost:8080/swagger-ui.html
 * Access API Docs at: http://localhost:8080/api-docs
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "CineBase REST API",
                version = "1.0",
                description = """
                        CineBase is a comprehensive movie and TV show database application with social features.

                        ## Features
                        - User authentication and authorization (JWT)
                        - TMDB API integration for movie/TV data (3rd party API)
                        - Email notifications for reviews (Gmail SMTP - 3rd party API)
                        - User reviews and ratings
                        - Favorites and watchlist management
                        - AI-powered recommendations using TMDB ML
                        - Higher/Lower game with leaderboard
                        - User onboarding and preferences

                        ## Authentication
                        Most endpoints require JWT authentication. Use the /api/auth/login or /api/auth/register endpoints to obtain a token.
                        Then click 'Authorize' button above and enter: Bearer <your-token>
                        """,
                contact = @Contact(
                        name = "CineBase Team"
                ),
                license = @License(
                        name = "MIT License"
                )
        ),
        servers = {
                @Server(
                        url = "http://localhost:8080",
                        description = "Development Server"
                )
        }
)
@SecurityScheme(
        name = "bearer-jwt",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER,
        description = "JWT authentication token. Format: Bearer <your-token>"
)
public class OpenAPIConfig {
}
