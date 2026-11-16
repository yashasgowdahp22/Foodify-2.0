package com.foodify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Phase1SecurityRuntimeTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static String customerToken;
    private static String adminToken;
    private static final String UNIQUE_SUFFIX = String.valueOf(System.currentTimeMillis()).substring(7);

    @Test
    @Order(1)
    @DisplayName("1. Verify Customer Registration works with 201 Created")
    void testCustomerRegistration() throws Exception {
        String uniqueEmail = "testcust_" + UNIQUE_SUFFIX + "@example.com";
        String requestJson = """
            {
                "name": "Test Customer",
                "email": "%s",
                "address": "123 Phase 1 Lane",
                "password": "Password@123"
            }
            """.formatted(uniqueEmail);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isCreated());
    }

    @Test
    @Order(2)
    @DisplayName("2. Verify Duplicate Registration returns 409 Conflict")
    void testDuplicateCustomerRegistration() throws Exception {
        String uniqueEmail = "testcust_" + UNIQUE_SUFFIX + "@example.com";
        String requestJson = """
            {
                "name": "Duplicate Customer",
                "email": "%s",
                "address": "456 Phase 1 Lane",
                "password": "Password@123"
            }
            """.formatted(uniqueEmail);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    @Order(3)
    @DisplayName("3. Verify Customer Login returns JWT token and role CUSTOMER")
    void testCustomerLogin() throws Exception {
        String uniqueEmail = "testcust_" + UNIQUE_SUFFIX + "@example.com";
        String loginJson = """
            {
                "email": "%s",
                "password": "Password@123"
            }
            """.formatted(uniqueEmail);

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.role").value("CUSTOMER"))
                .andReturn();

        JsonNode jsonNode = objectMapper.readTree(result.getResponse().getContentAsString());
        customerToken = jsonNode.get("token").asText();
    }

    @Test
    @Order(4)
    @DisplayName("4. Verify Default Admin Login returns JWT token and role ADMIN")
    void testAdminLogin() throws Exception {
        String loginJson = """
            {
                "email": "admin@foodify.com",
                "password": "Admin@123"
            }
            """;

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andReturn();

        JsonNode jsonNode = objectMapper.readTree(result.getResponse().getContentAsString());
        adminToken = jsonNode.get("token").asText();
    }

    @Test
    @Order(5)
    @DisplayName("5. Verify /api/auth/me returns authenticated customer profile")
    void testMeEndpoint() throws Exception {
        mockMvc.perform(get("/api/auth/me")
                .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("CUSTOMER"))
                .andExpect(jsonPath("$.email").value("testcust_" + UNIQUE_SUFFIX + "@example.com"));
    }

    @Test
    @Order(6)
    @DisplayName("6. Verify CUSTOMER cannot access ADMIN operation (POST /addRestaurant returns 403 Forbidden)")
    void testCustomerForbiddenFromAdminOperation() throws Exception {
        String restaurantJson = """
            {
                "restaurantName": "Hacked Bistro",
                "address": "Nowhere",
                "contactNumber": "9876543210",
                "email": "hacked@example.com",
                "description": "Unauthorized"
            }
            """;

        mockMvc.perform(post("/addRestaurant")
                .header("Authorization", "Bearer " + customerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(restaurantJson))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.message").value("Forbidden: You do not have permission to access this resource"));
    }

    @Test
    @Order(7)
    @DisplayName("7. Verify CUSTOMER cannot access /api/auth/admin/create-user (403 Forbidden)")
    void testCustomerForbiddenFromCreateUser() throws Exception {
        String createUserJson = """
            {
                "name": "Fake Owner",
                "email": "fakeowner@example.com",
                "address": "Somewhere",
                "password": "Password@123",
                "role": "RESTAURANT"
            }
            """;

        mockMvc.perform(post("/api/auth/admin/create-user")
                .header("Authorization", "Bearer " + customerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createUserJson))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(8)
    @DisplayName("8. Verify ADMIN can create RESTAURANT and DELIVERY accounts")
    void testAdminCreatesRestaurantAndDeliveryAccounts() throws Exception {
        String createRestaurantUser = """
            {
                "name": "Pizza Partner",
                "email": "partner_%s@pizzabox.com",
                "address": "Food Street 1",
                "password": "Owner@123",
                "role": "RESTAURANT"
            }
            """.formatted(UNIQUE_SUFFIX);

        mockMvc.perform(post("/api/auth/admin/create-user")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createRestaurantUser))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("RESTAURANT"))
                .andExpect(jsonPath("$.email").value("partner_" + UNIQUE_SUFFIX + "@pizzabox.com"));

        String createDeliveryUser = """
            {
                "name": "Speedy Rider",
                "email": "rider_%s@foodify.com",
                "address": "Transit Bay",
                "password": "Rider@123",
                "role": "DELIVERY"
            }
            """.formatted(UNIQUE_SUFFIX);

        mockMvc.perform(post("/api/auth/admin/create-user")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createDeliveryUser))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("DELIVERY"))
                .andExpect(jsonPath("$.email").value("rider_" + UNIQUE_SUFFIX + "@foodify.com"));
    }

    @Test
    @Order(9)
    @DisplayName("9. Verify Invalid or Expired JWT returns 401 Unauthorized JSON")
    void testInvalidJwtReturns401() throws Exception {
        mockMvc.perform(get("/api/auth/me")
                .header("Authorization", "Bearer invalid.fake.token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Unauthorized: Authentication is required or your token is invalid/expired"));

        // No token provided to protected endpoint
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @Order(10)
    @DisplayName("10. Verify Password Change works and old password stops working")
    void testChangePassword() throws Exception {
        // Change password for customer
        String changePwJson = """
            {
                "oldPassword": "Password@123",
                "newPassword": "NewPassword@456"
            }
            """;

        mockMvc.perform(put("/api/auth/change-password")
                .header("Authorization", "Bearer " + customerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(changePwJson))
                .andExpect(status().isOk());

        // Login with old password fails with 401
        String oldLoginJson = """
            {
                "email": "testcust_%s@example.com",
                "password": "Password@123"
            }
            """.formatted(UNIQUE_SUFFIX);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(oldLoginJson))
                .andExpect(status().isUnauthorized());

        // Login with new password succeeds with 200
        String newLoginJson = """
            {
                "email": "testcust_%s@example.com",
                "password": "NewPassword@456"
            }
            """.formatted(UNIQUE_SUFFIX);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newLoginJson))
                .andExpect(status().isOk());
    }

    @Test
    @Order(11)
    @DisplayName("11. Verify Public endpoints (GET /getAllRestaurants) are accessible without any token")
    void testPublicEndpointsAccessibleWithoutToken() throws Exception {
        mockMvc.perform(get("/getAllRestaurants"))
                .andExpect(status().isOk());
    }
}
