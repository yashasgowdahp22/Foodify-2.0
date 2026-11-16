
package com.foodify.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.foodify.security.CustomAccessDeniedHandler;
import com.foodify.security.JwtAuthenticationEntryPoint;
import com.foodify.security.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${frontend.url:http://localhost:5173}")
    private String frontendUrl;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
            CustomAccessDeniedHandler customAccessDeniedHandler) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        List<String> origins = new ArrayList<>(List.of("http://localhost:5173", "http://localhost:3000"));
        if (frontendUrl != null && !frontendUrl.isBlank() && !origins.contains(frontendUrl)) {
            origins.add(frontendUrl);
        }
        configuration.setAllowedOrigins(origins);
        
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http.csrf(csrf -> csrf.disable());

        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

        http.sessionManagement(session ->
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        http.exceptionHandling(ex -> ex
            .authenticationEntryPoint(jwtAuthenticationEntryPoint)
            .accessDeniedHandler(customAccessDeniedHandler)
        );

        http.authorizeHttpRequests(auth -> auth
            // Public Auth endpoints
            .requestMatchers(
                "/api/auth/register",
                "/api/auth/login",
                "/api/auth/logout"
            ).permitAll()
            .requestMatchers("/api/auth/admin/**").hasRole("ADMIN")
            .requestMatchers("/api/auth/me", "/api/auth/change-password").authenticated()

            // Swagger endpoints
            .requestMatchers(
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/v3/api-docs/**"
            ).permitAll()

            // Public restaurant and menu browsing
            .requestMatchers(HttpMethod.GET,
                "/getAllRestaurants",
                "/getRestaurant/*",
                "/searchRestaurants/**",
                "/getAllMenuItems",
                "/getMenuItem/*",
                "/getRestaurantMenu/*",
                "/searchMenuItems/**",
                "/getMenuItemsByCategory/*"
            ).permitAll()

            // Owner-specific restaurant and menu endpoints
            .requestMatchers("/myRestaurant", "/myRestaurant/**").hasRole("RESTAURANT")

            // Restaurant management
            .requestMatchers(HttpMethod.POST, "/addRestaurant").hasRole("ADMIN")
            .requestMatchers(HttpMethod.PUT, "/updateRestaurant/*").hasAnyRole("ADMIN", "RESTAURANT")
            .requestMatchers(HttpMethod.DELETE, "/deleteRestaurant/*").hasRole("ADMIN")

            // Menu item management
            .requestMatchers(HttpMethod.POST, "/addMenuItem").hasAnyRole("ADMIN", "RESTAURANT")
            .requestMatchers(HttpMethod.PUT, "/updateMenuItem/*").hasAnyRole("ADMIN", "RESTAURANT")
            .requestMatchers(HttpMethod.PATCH, "/updateMenuItemStatus/*").hasAnyRole("ADMIN", "RESTAURANT")
            .requestMatchers(HttpMethod.DELETE, "/deleteMenuItem/*").hasAnyRole("ADMIN", "RESTAURANT")

            // Customer management
            .requestMatchers("/getAllCustomer", "/getCustomerByEmail", "/addCustomer", "/deleteCustomer/*").hasRole("ADMIN")
            .requestMatchers("/getCustomerById/*", "/updateCustomer/*").authenticated()

            // Cart endpoints (customer operations vs admin raw endpoints)
            .requestMatchers("/myCart", "/myCart/**").hasRole("CUSTOMER")
            .requestMatchers("/addItemToCart/*/*/*", "/getCart/*", "/updateCartItem/*/*", "/deleteCartItem/*", "/clearCart/*").hasRole("CUSTOMER")
            .requestMatchers("/addCart", "/getAllCart", "/updateCart/*", "/addCartItem", "/getAllCartItem", "/getCartItem/*", "/updateCartItem/*", "/deleteCartItemById/*").hasRole("ADMIN")

            // Order endpoints
            .requestMatchers("/checkout", "/myOrders", "/myOrders/**").hasRole("CUSTOMER")
            .requestMatchers("/restaurant/orders").hasRole("RESTAURANT")
            .requestMatchers(HttpMethod.POST, "/placeOrder/*").hasRole("CUSTOMER")
            .requestMatchers(HttpMethod.PUT, "/updateOrderStatus/*/*").hasAnyRole("ADMIN", "RESTAURANT", "CUSTOMER")
            .requestMatchers(HttpMethod.GET, "/getAllOrders").hasAnyRole("ADMIN", "RESTAURANT")
            .requestMatchers(HttpMethod.GET, "/getOrder/*").authenticated()
            .requestMatchers(HttpMethod.PUT, "/updateOrder/*").hasAnyRole("ADMIN", "CUSTOMER")
            .requestMatchers("/addOrder", "/deleteOrder/*", "/addOrderItem", "/getAllOrderItems", "/getOrderItem/*", "/updateOrderItem/*", "/deleteOrderItem/*").hasRole("ADMIN")

            // Payment endpoints
            .requestMatchers(HttpMethod.POST, "/processPayment/*/*").hasRole("CUSTOMER")
            .requestMatchers(HttpMethod.PUT, "/updatePaymentStatus/*/*").hasAnyRole("ADMIN", "DELIVERY", "CUSTOMER")
            .requestMatchers(HttpMethod.GET, "/getPaymentDetails/*").authenticated()
            .requestMatchers("/getAllPaymentDetails", "/addPaymentDetails", "/updatePaymentDetails/*", "/deletePaymentDetails/*").hasRole("ADMIN")

            // Delivery endpoints
            .requestMatchers("/myDeliveries", "/myDeliveries/**").hasRole("DELIVERY")
            .requestMatchers(HttpMethod.POST, "/assignDelivery/*").hasRole("ADMIN")
            .requestMatchers("/availableDeliveryPartners").hasRole("ADMIN")
            .requestMatchers(HttpMethod.PUT, "/updateDeliveryStatus/*/*").hasAnyRole("ADMIN", "DELIVERY")
            .requestMatchers(HttpMethod.GET, "/getAllDeliveries").hasRole("ADMIN")
            .requestMatchers(HttpMethod.GET, "/getDelivery/*").hasAnyRole("ADMIN", "DELIVERY", "CUSTOMER")
            .requestMatchers("/addDelivery", "/updateDelivery/*", "/deleteDelivery/*").hasRole("ADMIN")

            .anyRequest().authenticated()
        );

        http.addFilterBefore(
            jwtAuthenticationFilter,
            UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }
}
