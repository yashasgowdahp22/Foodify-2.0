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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Phase2RestaurantMenuRuntimeTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static String adminToken;
    private static String ownerToken;
    private static String intruderToken;
    private static String customerToken;

    private static Long createdRestaurantId;
    private static Long createdMenuItemId;

    private static final String UNIQUE_KEY = String.valueOf(System.currentTimeMillis()).substring(7);

    @Test
    @Order(1)
    @DisplayName("1. Admin logs in and creates accounts for RESTAURANT Owner, Intruder Owner, and Customer")
    void setupAccounts() throws Exception {
        // Admin login
        String adminLoginJson = """
            {
                "email": "admin@foodify.com",
                "password": "Admin@123"
            }
            """;
        MvcResult adminRes = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(adminLoginJson))
                .andExpect(status().isOk())
                .andReturn();
        adminToken = objectMapper.readTree(adminRes.getResponse().getContentAsString()).get("token").asText();

        // Create legitimate Owner
        String ownerEmail = "owner_" + UNIQUE_KEY + "@tastehub.com";
        String createOwnerJson = """
            {
                "name": "Mario Rossi",
                "email": "%s",
                "address": "Via Roma 10",
                "password": "Owner@123",
                "role": "RESTAURANT"
            }
            """.formatted(ownerEmail);
        mockMvc.perform(post("/api/auth/admin/create-user")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createOwnerJson))
                .andExpect(status().isCreated());

        // Login as Owner
        String ownerLoginJson = """
            {
                "email": "%s",
                "password": "Owner@123"
            }
            """.formatted(ownerEmail);
        MvcResult ownerRes = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ownerLoginJson))
                .andExpect(status().isOk())
                .andReturn();
        ownerToken = objectMapper.readTree(ownerRes.getResponse().getContentAsString()).get("token").asText();

        // Create Intruder Owner (different restaurant owner)
        String intruderEmail = "intruder_" + UNIQUE_KEY + "@otherplace.com";
        String createIntruderJson = """
            {
                "name": "Luigi Rival",
                "email": "%s",
                "address": "Corso Italia 5",
                "password": "Rival@123",
                "role": "RESTAURANT"
            }
            """.formatted(intruderEmail);
        mockMvc.perform(post("/api/auth/admin/create-user")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createIntruderJson))
                .andExpect(status().isCreated());

        // Login as Intruder
        String intruderLoginJson = """
            {
                "email": "%s",
                "password": "Rival@123"
            }
            """.formatted(intruderEmail);
        MvcResult intruderRes = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(intruderLoginJson))
                .andExpect(status().isOk())
                .andReturn();
        intruderToken = objectMapper.readTree(intruderRes.getResponse().getContentAsString()).get("token").asText();

        // Register Customer
        String custEmail = "diner_" + UNIQUE_KEY + "@example.com";
        String custRegisterJson = """
            {
                "name": "Hungry Diner",
                "email": "%s",
                "address": "Downtown 22",
                "password": "Password@123"
            }
            """.formatted(custEmail);
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(custRegisterJson))
                .andExpect(status().isCreated());

        // Login as Customer
        String custLoginJson = """
            {
                "email": "%s",
                "password": "Password@123"
            }
            """.formatted(custEmail);
        MvcResult custRes = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(custLoginJson))
                .andExpect(status().isOk())
                .andReturn();
        customerToken = objectMapper.readTree(custRes.getResponse().getContentAsString()).get("token").asText();
    }

    @Test
    @Order(2)
    @DisplayName("2. Admin creates a Restaurant linked to Owner")
    void testAdminCreatesRestaurantWithLinkedOwner() throws Exception {
        String ownerEmail = "owner_" + UNIQUE_KEY + "@tastehub.com";
        String restaurantJson = """
            {
                "restaurantName": "Taste Hub %s",
                "address": "Corner Avenue 99",
                "contactNumber": "9876543210",
                "email": "tastehub_%s@foodify.com",
                "description": "Authentic Italian wood-fired pizzas",
                "open": true,
                "imageUrl": "https://example.com/tastehub.jpg",
                "ownerEmail": "%s"
            }
            """.formatted(UNIQUE_KEY, UNIQUE_KEY, ownerEmail);

        mockMvc.perform(post("/addRestaurant")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(restaurantJson))
                .andExpect(status().isCreated());

        // Search for newly created restaurant to retrieve its ID
        MvcResult searchRes = mockMvc.perform(get("/searchRestaurants?query=Taste Hub " + UNIQUE_KEY))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode list = objectMapper.readTree(searchRes.getResponse().getContentAsString());
        createdRestaurantId = list.get(0).get("id").asLong();
    }

    @Test
    @Order(3)
    @DisplayName("3. Owner accesses their restaurant via /myRestaurant and updates open status")
    void testOwnerRestaurantManagement() throws Exception {
        // Owner views their restaurant
        mockMvc.perform(get("/myRestaurant")
                .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.restaurantName").value("Taste Hub " + UNIQUE_KEY))
                .andExpect(jsonPath("$.open").value(true));

        // Owner toggles restaurant status to CLOSED
        mockMvc.perform(patch("/myRestaurant/status?open=false")
                .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isOk());

        // Verify it is now closed
        mockMvc.perform(get("/myRestaurant")
                .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.open").value(false));

        // Toggle back to OPEN
        mockMvc.perform(patch("/myRestaurant/status?open=true")
                .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isOk());
    }

    @Test
    @Order(4)
    @DisplayName("4. Owner adds a Menu Item (automatically associated with their restaurant)")
    void testOwnerAddsMenuItem() throws Exception {
        String itemJson = """
            {
                "name": "Margherita Pizza %s",
                "description": "Fresh basil, tomato sauce, mozzarella",
                "price": 299.00,
                "category": "Pizza",
                "imageUrl": "https://example.com/margherita.jpg",
                "available": true
            }
            """.formatted(UNIQUE_KEY);

        MvcResult result = mockMvc.perform(post("/addMenuItem")
                .header("Authorization", "Bearer " + ownerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(itemJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Margherita Pizza " + UNIQUE_KEY))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.restaurantId").value(createdRestaurantId))
                .andReturn();

        JsonNode itemNode = objectMapper.readTree(result.getResponse().getContentAsString());
        createdMenuItemId = itemNode.get("id").asLong();
    }

    @Test
    @Order(5)
    @DisplayName("5. Owner views their menu items via /myRestaurant/menu")
    void testOwnerViewsMyRestaurantMenu() throws Exception {
        mockMvc.perform(get("/myRestaurant/menu")
                .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(createdMenuItemId))
                .andExpect(jsonPath("$[0].name").value("Margherita Pizza " + UNIQUE_KEY));
    }

    @Test
    @Order(6)
    @DisplayName("6. Public browsing: Get restaurant menu, search items, and filter by category without token")
    void testPublicBrowsingAndSearch() throws Exception {
        // Public menu view for the restaurant
        mockMvc.perform(get("/getRestaurantMenu/" + createdRestaurantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(createdMenuItemId));

        // Public search for dishes
        mockMvc.perform(get("/searchMenuItems?query=Margherita Pizza " + UNIQUE_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Margherita Pizza " + UNIQUE_KEY));

        // Public category filter
        mockMvc.perform(get("/getMenuItemsByCategory/Pizza"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @Order(7)
    @DisplayName("7. Service-level ownership enforcement: Customer and Rival Owner cannot modify or delete menu item (403 Forbidden)")
    void testOwnershipSecurityEnforcement() throws Exception {
        String hackJson = """
            {
                "name": "Hacked Price",
                "description": "Defaced description",
                "price": 1.00,
                "category": "Pizza",
                "available": false
            }
            """;

        // Customer attempts to update owner's menu item -> 403 Forbidden
        mockMvc.perform(put("/updateMenuItem/" + createdMenuItemId)
                .header("Authorization", "Bearer " + customerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(hackJson))
                .andExpect(status().isForbidden());

        // Intruder owner attempts to update another owner's menu item -> 403 Forbidden
        mockMvc.perform(put("/updateMenuItem/" + createdMenuItemId)
                .header("Authorization", "Bearer " + intruderToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(hackJson))
                .andExpect(status().isForbidden());

        // Intruder owner attempts to delete another owner's menu item -> 403 Forbidden
        mockMvc.perform(delete("/deleteMenuItem/" + createdMenuItemId)
                .header("Authorization", "Bearer " + intruderToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(8)
    @DisplayName("8. Owner can toggle item availability and update menu item")
    void testOwnerCanToggleAndEditMenuItem() throws Exception {
        // Toggle item to unavailable
        mockMvc.perform(patch("/updateMenuItemStatus/" + createdMenuItemId + "?available=false")
                .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isOk());

        // Verify status
        mockMvc.perform(get("/getMenuItem/" + createdMenuItemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(false));

        // Update item details
        String updateJson = """
            {
                "name": "Special Margherita Pizza %s",
                "description": "Double cheese & fresh basil",
                "price": 349.00,
                "category": "Pizza",
                "imageUrl": "https://example.com/special_margherita.jpg",
                "available": true
            }
            """.formatted(UNIQUE_KEY);

        mockMvc.perform(put("/updateMenuItem/" + createdMenuItemId)
                .header("Authorization", "Bearer " + ownerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andExpect(status().isOk());

        // Verify updated details
        mockMvc.perform(get("/getMenuItem/" + createdMenuItemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Special Margherita Pizza " + UNIQUE_KEY))
                .andExpect(jsonPath("$.price").value(349.00))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    @Order(9)
    @DisplayName("9. Admin creates restaurant without owner (existing backward-compatibility verification)")
    void testRestaurantWithoutOwnerWorks() throws Exception {
        String unownedRestaurant = """
            {
                "restaurantName": "Unowned Diner %s",
                "address": "Highway 101",
                "contactNumber": "9876543211",
                "email": "unowned_%s@foodify.com",
                "description": "Traditional highway eatery",
                "open": true
            }
            """.formatted(UNIQUE_KEY, UNIQUE_KEY);

        mockMvc.perform(post("/addRestaurant")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(unownedRestaurant))
                .andExpect(status().isCreated());

        // Verify it can be retrieved publicly and owner fields are safely null
        MvcResult res = mockMvc.perform(get("/searchRestaurants?query=Unowned Diner " + UNIQUE_KEY))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode list = objectMapper.readTree(res.getResponse().getContentAsString());
        JsonNode diner = list.get(0);
        org.junit.jupiter.api.Assertions.assertTrue(diner.get("ownerId").isNull());
    }

    @Test
    @Order(10)
    @DisplayName("10. Owner deletes their menu item successfully")
    void testOwnerDeletesMenuItem() throws Exception {
        mockMvc.perform(delete("/deleteMenuItem/" + createdMenuItemId)
                .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isOk());

        // Verify 404 on subsequent get
        mockMvc.perform(get("/getMenuItem/" + createdMenuItemId))
                .andExpect(status().isNotFound());
    }
}
