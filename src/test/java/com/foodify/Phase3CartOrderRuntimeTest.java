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

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
public class Phase3CartOrderRuntimeTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static String adminToken;
    private static String ownerToken;
    private static String owner2Token;
    private static String customerAToken;
    private static String customerBToken;

    private static Long restaurant1Id;
    private static Long restaurant2Id;

    private static Long pizzaItemId;
    private static Long pastaItemId;
    private static Long burgerItemId;

    private static Long pizzaCartItemId;
    private static Long pastaCartItemId;

    private static Long codOrderId;
    private static Long upiOrderId;

    private static final String UNIQUE_KEY = String.valueOf(System.currentTimeMillis()).substring(7);

    @Test
    @Order(1)
    @DisplayName("1. Setup: Admin logs in and creates accounts for 2 Owners and 2 Customers")
    void setupAccountsAndRestaurants() throws Exception {
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

        // Create Restaurant 1 Owner
        String owner1Email = "owner1_" + UNIQUE_KEY + "@trattoria.com";
        String createOwner1Json = """
            {
                "name": "Chef Marco",
                "email": "%s",
                "address": "10 Pizza Way",
                "password": "Owner@123",
                "role": "RESTAURANT"
            }
            """.formatted(owner1Email);
        mockMvc.perform(post("/api/auth/admin/create-user")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createOwner1Json))
                .andExpect(status().isCreated());

        MvcResult owner1Res = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    { "email": "%s", "password": "Owner@123" }
                    """.formatted(owner1Email)))
                .andExpect(status().isOk())
                .andReturn();
        ownerToken = objectMapper.readTree(owner1Res.getResponse().getContentAsString()).get("token").asText();

        // Create Restaurant 2 Owner
        String owner2Email = "owner2_" + UNIQUE_KEY + "@burgerjoint.com";
        String createOwner2Json = """
            {
                "name": "Bob Burger",
                "email": "%s",
                "address": "20 Grill Blvd",
                "password": "Owner@123",
                "role": "RESTAURANT"
            }
            """.formatted(owner2Email);
        mockMvc.perform(post("/api/auth/admin/create-user")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createOwner2Json))
                .andExpect(status().isCreated());

        MvcResult owner2Res = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    { "email": "%s", "password": "Owner@123" }
                    """.formatted(owner2Email)))
                .andExpect(status().isOk())
                .andReturn();
        owner2Token = objectMapper.readTree(owner2Res.getResponse().getContentAsString()).get("token").asText();

        // Admin creates Restaurant 1 linked to Owner 1
        String r1Json = """
            {
                "restaurantName": "Marco Trattoria %s",
                "address": "10 Pizza Way",
                "contactNumber": "9876543210",
                "email": "marco_%s@trattoria.com",
                "description": "Authentic Italian pizzas",
                "open": true,
                "ownerEmail": "%s"
            }
            """.formatted(UNIQUE_KEY, UNIQUE_KEY, owner1Email);
        mockMvc.perform(post("/addRestaurant")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(r1Json))
                .andExpect(status().isCreated());

        MvcResult r1Res = mockMvc.perform(get("/myRestaurant")
                .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isOk())
                .andReturn();
        restaurant1Id = objectMapper.readTree(r1Res.getResponse().getContentAsString()).get("id").asLong();

        // Owner 1 adds Pizza ($12.00)
        String pizzaJson = """
            {
                "name": "Margherita Pizza %s",
                "description": "Fresh tomato and mozzarella",
                "price": 12.00,
                "category": "Main Course",
                "available": true
            }
            """.formatted(UNIQUE_KEY);
        mockMvc.perform(post("/addMenuItem")
                .header("Authorization", "Bearer " + ownerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(pizzaJson))
                .andExpect(status().isCreated());

        // Owner 1 adds Pasta ($10.00)
        String pastaJson = """
            {
                "name": "Penne Arrabbiata %s",
                "description": "Spicy tomato sauce",
                "price": 10.00,
                "category": "Main Course",
                "available": true
            }
            """.formatted(UNIQUE_KEY);
        mockMvc.perform(post("/addMenuItem")
                .header("Authorization", "Bearer " + ownerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(pastaJson))
                .andExpect(status().isCreated());

        // Retrieve menu item IDs
        MvcResult menu1Res = mockMvc.perform(get("/myRestaurant/menu")
                .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode menu1Items = objectMapper.readTree(menu1Res.getResponse().getContentAsString());
        for (JsonNode item : menu1Items) {
            String name = item.get("name").asText();
            if (name.contains("Margherita")) {
                pizzaItemId = item.get("id").asLong();
            } else if (name.contains("Penne")) {
                pastaItemId = item.get("id").asLong();
            }
        }

        // Admin creates Restaurant 2 linked to Owner 2
        String r2Json = """
            {
                "restaurantName": "Bob Burger Joint %s",
                "address": "20 Grill Blvd",
                "contactNumber": "9123456780",
                "email": "bob_%s@burgerjoint.com",
                "description": "Best smashed burgers",
                "open": true,
                "ownerEmail": "%s"
            }
            """.formatted(UNIQUE_KEY, UNIQUE_KEY, owner2Email);
        mockMvc.perform(post("/addRestaurant")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(r2Json))
                .andExpect(status().isCreated());

        MvcResult r2Res = mockMvc.perform(get("/myRestaurant")
                .header("Authorization", "Bearer " + owner2Token))
                .andExpect(status().isOk())
                .andReturn();
        restaurant2Id = objectMapper.readTree(r2Res.getResponse().getContentAsString()).get("id").asLong();

        // Owner 2 adds Burger ($8.00)
        String burgerJson = """
            {
                "name": "Cheeseburger %s",
                "description": "Double patty with cheddar",
                "price": 8.00,
                "category": "Main Course",
                "available": true
            }
            """.formatted(UNIQUE_KEY);
        mockMvc.perform(post("/addMenuItem")
                .header("Authorization", "Bearer " + owner2Token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(burgerJson))
                .andExpect(status().isCreated());

        MvcResult menu2Res = mockMvc.perform(get("/myRestaurant/menu")
                .header("Authorization", "Bearer " + owner2Token))
                .andExpect(status().isOk())
                .andReturn();
        burgerItemId = objectMapper.readTree(menu2Res.getResponse().getContentAsString()).get(0).get("id").asLong();

        // Register Customer A
        String customerAEmail = "cust_a_" + UNIQUE_KEY + "@example.com";
        String regAJson = """
            {
                "name": "Customer Alice",
                "email": "%s",
                "address": "123 Elm Street",
                "password": "Customer@123"
            }
            """.formatted(customerAEmail);
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(regAJson))
                .andExpect(status().isCreated());

        MvcResult loginARes = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    { "email": "%s", "password": "Customer@123" }
                    """.formatted(customerAEmail)))
                .andExpect(status().isOk())
                .andReturn();
        customerAToken = objectMapper.readTree(loginARes.getResponse().getContentAsString()).get("token").asText();

        // Register Customer B
        String customerBEmail = "cust_b_" + UNIQUE_KEY + "@example.com";
        String regBJson = """
            {
                "name": "Customer Bob",
                "email": "%s",
                "address": "789 Pine Street",
                "password": "Customer@123"
            }
            """.formatted(customerBEmail);
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(regBJson))
                .andExpect(status().isCreated());

        MvcResult loginBRes = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    { "email": "%s", "password": "Customer@123" }
                    """.formatted(customerBEmail)))
                .andExpect(status().isOk())
                .andReturn();
        customerBToken = objectMapper.readTree(loginBRes.getResponse().getContentAsString()).get("token").asText();
    }

    @Test
    @Order(2)
    @DisplayName("2. Customer A views an empty cart: returns 200 with totalAmount 0 and empty items")
    void getEmptyCart() throws Exception {
        mockMvc.perform(get("/myCart")
                .header("Authorization", "Bearer " + customerAToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("Customer Alice"))
                .andExpect(jsonPath("$.totalAmount").value(0))
                .andExpect(jsonPath("$.itemCount").value(0))
                .andExpect(jsonPath("$.items", hasSize(0)));
    }

    @Test
    @Order(3)
    @DisplayName("3. Customer A adds items to cart: subTotal and totalAmount correctly calculated")
    void addItemsToCart() throws Exception {
        // Add 2 Pizzas (2 * $12.00 = $24.00)
        String addPizzaJson = """
            {
                "menuItemId": %d,
                "quantity": 2
            }
            """.formatted(pizzaItemId);

        MvcResult res1 = mockMvc.perform(post("/myCart/items")
                .header("Authorization", "Bearer " + customerAToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(addPizzaJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.restaurantId").value(restaurant1Id))
                .andExpect(jsonPath("$.itemCount").value(2))
                .andExpect(jsonPath("$.totalAmount").value(24.00))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].subTotal").value(24.00))
                .andReturn();

        JsonNode root1 = objectMapper.readTree(res1.getResponse().getContentAsString());
        pizzaCartItemId = root1.get("items").get(0).get("cartItemId").asLong();

        // Add 1 Pasta (1 * $10.00 = $10.00, total = $34.00)
        String addPastaJson = """
            {
                "menuItemId": %d,
                "quantity": 1
            }
            """.formatted(pastaItemId);

        MvcResult res2 = mockMvc.perform(post("/myCart/items")
                .header("Authorization", "Bearer " + customerAToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(addPastaJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemCount").value(3))
                .andExpect(jsonPath("$.totalAmount").value(34.00))
                .andExpect(jsonPath("$.items", hasSize(2)))
                .andReturn();

        JsonNode root2 = objectMapper.readTree(res2.getResponse().getContentAsString());
        for (JsonNode item : root2.get("items")) {
            if (item.get("menuItemId").asLong() == pastaItemId) {
                pastaCartItemId = item.get("cartItemId").asLong();
            }
        }
    }

    @Test
    @Order(4)
    @DisplayName("4. Customer A tries to add item from a different restaurant: rejected with 400")
    void multiRestaurantRestriction() throws Exception {
        // Customer A already has items from Restaurant 1, now tries to add Burger from Restaurant 2
        String addBurgerJson = """
            {
                "menuItemId": %d,
                "quantity": 1
            }
            """.formatted(burgerItemId);

        mockMvc.perform(post("/myCart/items")
                .header("Authorization", "Bearer " + customerAToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(addBurgerJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("only add items from one restaurant")));
    }

    @Test
    @Order(5)
    @DisplayName("5. Restaurant closed and MenuItem unavailable validation")
    void closedRestaurantAndUnavailableItemValidation() throws Exception {
        // Owner 1 closes Restaurant 1
        mockMvc.perform(patch("/myRestaurant/status?open=false")
                .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isOk());

        // Customer B tries to add Pizza from closed restaurant
        String addPizzaJson = """
            {
                "menuItemId": %d,
                "quantity": 1
            }
            """.formatted(pizzaItemId);

        mockMvc.perform(post("/myCart/items")
                .header("Authorization", "Bearer " + customerBToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(addPizzaJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("closed")));

        // Owner 1 reopens Restaurant 1
        mockMvc.perform(patch("/myRestaurant/status?open=true")
                .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isOk());

        // Owner 1 sets Pasta unavailable
        mockMvc.perform(patch("/updateMenuItemStatus/" + pastaItemId + "?available=false")
                .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isOk());

        // Customer B tries to add unavailable Pasta
        String addPastaJson = """
            {
                "menuItemId": %d,
                "quantity": 1
            }
            """.formatted(pastaItemId);

        mockMvc.perform(post("/myCart/items")
                .header("Authorization", "Bearer " + customerBToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(addPastaJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("not available")));

        // Restore Pasta availability
        mockMvc.perform(patch("/updateMenuItemStatus/" + pastaItemId + "?available=true")
                .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isOk());
    }

    @Test
    @Order(6)
    @DisplayName("6. Cart item update and delete with ownership authorization")
    void cartItemUpdateAndDeleteWithOwnership() throws Exception {
        // Customer A updates Pizza quantity from 2 to 3 (3 * $12.00 = $36.00 + $10.00 = $46.00)
        mockMvc.perform(put("/myCart/items/" + pizzaCartItemId + "/3")
                .header("Authorization", "Bearer " + customerAToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemCount").value(4))
                .andExpect(jsonPath("$.totalAmount").value(46.00));

        // Customer B tries to update Customer A's cart item -> 401/403 Forbidden
        mockMvc.perform(put("/myCart/items/" + pizzaCartItemId + "/5")
                .header("Authorization", "Bearer " + customerBToken))
                .andExpect(status().isUnauthorized());

        // Customer B tries to delete Customer A's cart item -> 401/403 Forbidden
        mockMvc.perform(delete("/myCart/items/" + pizzaCartItemId)
                .header("Authorization", "Bearer " + customerBToken))
                .andExpect(status().isUnauthorized());

        // Customer A deletes Pasta from cart -> only Pizza remains (qty 3, total = $36.00)
        mockMvc.perform(delete("/myCart/items/" + pastaCartItemId)
                .header("Authorization", "Bearer " + customerAToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemCount").value(3))
                .andExpect(jsonPath("$.totalAmount").value(36.00))
                .andExpect(jsonPath("$.items", hasSize(1)));
    }

    @Test
    @Order(7)
    @DisplayName("7. Checkout with COD: creates order with PENDING payment, clears cart")
    void checkoutWithCOD() throws Exception {
        String checkoutJson = """
            {
                "deliveryAddress": "456 Oak Avenue, Apt 2B",
                "specialInstructions": "Leave package at front door",
                "paymentMethod": "COD"
            }
            """;

        MvcResult res = mockMvc.perform(post("/checkout")
                .header("Authorization", "Bearer " + customerAToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(checkoutJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.totalAmount").value(36.00))
                .andExpect(jsonPath("$.deliveryAddress").value("456 Oak Avenue, Apt 2B"))
                .andExpect(jsonPath("$.specialInstructions").value("Leave package at front door"))
                .andExpect(jsonPath("$.paymentMethod").value("COD"))
                .andExpect(jsonPath("$.paymentStatus").value("PENDING"))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andReturn();

        JsonNode root = objectMapper.readTree(res.getResponse().getContentAsString());
        codOrderId = root.get("id").asLong();
        assertTrue(root.get("transactionId").isNull());

        // Verify Customer A's cart is now empty
        mockMvc.perform(get("/myCart")
                .header("Authorization", "Bearer " + customerAToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemCount").value(0))
                .andExpect(jsonPath("$.totalAmount").value(0))
                .andExpect(jsonPath("$.items", hasSize(0)));
    }

    @Test
    @Order(8)
    @DisplayName("8. Checkout with UPI: creates order with SUCCESS payment and transaction ID")
    void checkoutWithUPI() throws Exception {
        // Add 1 Pizza to Customer A's cart
        String addPizzaJson = """
            {
                "menuItemId": %d,
                "quantity": 1
            }
            """.formatted(pizzaItemId);

        mockMvc.perform(post("/myCart/items")
                .header("Authorization", "Bearer " + customerAToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(addPizzaJson))
                .andExpect(status().isOk());

        // Checkout with UPI
        String checkoutJson = """
            {
                "deliveryAddress": "123 Elm Street",
                "specialInstructions": "Call on arrival",
                "paymentMethod": "UPI"
            }
            """;

        MvcResult res = mockMvc.perform(post("/checkout")
                .header("Authorization", "Bearer " + customerAToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(checkoutJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.totalAmount").value(12.00))
                .andExpect(jsonPath("$.paymentMethod").value("UPI"))
                .andExpect(jsonPath("$.paymentStatus").value("SUCCESS"))
                .andReturn();

        JsonNode root = objectMapper.readTree(res.getResponse().getContentAsString());
        upiOrderId = root.get("id").asLong();
        String txnId = root.get("transactionId").asText();
        assertNotNull(txnId);
        assertTrue(txnId.startsWith("TXN-"));
    }

    @Test
    @Order(9)
    @DisplayName("9. Order History & Single Order details with ownership check")
    void orderHistoryAndOwnership() throws Exception {
        // Customer A views order history (should contain at least the 2 newly created orders)
        MvcResult historyRes = mockMvc.perform(get("/myOrders")
                .header("Authorization", "Bearer " + customerAToken))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode orders = objectMapper.readTree(historyRes.getResponse().getContentAsString());
        assertTrue(orders.size() >= 2);

        // Customer A views single order details
        mockMvc.perform(get("/myOrders/" + codOrderId)
                .header("Authorization", "Bearer " + customerAToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(codOrderId))
                .andExpect(jsonPath("$.customerEmail").value("cust_a_" + UNIQUE_KEY + "@example.com"));

        // Customer B tries to view Customer A's order -> 401/403 Forbidden
        mockMvc.perform(get("/myOrders/" + codOrderId)
                .header("Authorization", "Bearer " + customerBToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(10)
    @DisplayName("10. Customer cancels PENDING order: status becomes CANCELLED, payment is REFUNDED")
    void customerCancelsPendingOrder() throws Exception {
        // Customer A cancels UPI order
        mockMvc.perform(put("/myOrders/" + upiOrderId + "/cancel")
                .header("Authorization", "Bearer " + customerAToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"))
                .andExpect(jsonPath("$.paymentStatus").value("REFUNDED"));

        // Verify status in order details
        mockMvc.perform(get("/myOrders/" + upiOrderId)
                .header("Authorization", "Bearer " + customerAToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"))
                .andExpect(jsonPath("$.paymentStatus").value("REFUNDED"));
    }

    @Test
    @Order(11)
    @DisplayName("11. Restaurant Owner views orders and manages order status transitions")
    void restaurantOwnerOrderStatusTransitions() throws Exception {
        // Restaurant 1 Owner views incoming orders
        MvcResult ownerOrdersRes = mockMvc.perform(get("/restaurant/orders")
                .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode orders = objectMapper.readTree(ownerOrdersRes.getResponse().getContentAsString());
        assertTrue(orders.size() >= 2);

        // Owner advances COD order: PENDING -> CONFIRMED
        mockMvc.perform(put("/updateOrderStatus/" + codOrderId + "/CONFIRMED")
                .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isOk());

        // Customer A attempts to cancel CONFIRMED order -> rejected (only PENDING allowed)
        mockMvc.perform(put("/myOrders/" + codOrderId + "/cancel")
                .header("Authorization", "Bearer " + customerAToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Only PENDING orders can be cancelled")));

        // Customer A attempts to arbitrarily set status to DELIVERED -> rejected
        mockMvc.perform(put("/updateOrderStatus/" + codOrderId + "/DELIVERED")
                .header("Authorization", "Bearer " + customerAToken))
                .andExpect(status().isBadRequest());

        // Restaurant 2 Owner attempts to update Restaurant 1's order -> 401/403 Unauthorized
        mockMvc.perform(put("/updateOrderStatus/" + codOrderId + "/PREPARING")
                .header("Authorization", "Bearer " + owner2Token))
                .andExpect(status().isUnauthorized());

        // Owner advances: CONFIRMED -> PREPARING
        mockMvc.perform(put("/updateOrderStatus/" + codOrderId + "/PREPARING")
                .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isOk());

        // Admin advances: PREPARING -> OUT_FOR_DELIVERY (restaurant owners cannot mark OUT_FOR_DELIVERY)
        mockMvc.perform(put("/updateOrderStatus/" + codOrderId + "/OUT_FOR_DELIVERY")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        // Admin advances: OUT_FOR_DELIVERY -> DELIVERED (restaurant owners cannot mark DELIVERED)
        mockMvc.perform(put("/updateOrderStatus/" + codOrderId + "/DELIVERED")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        // Attempting to update DELIVERED order -> rejected (already completed)
        mockMvc.perform(put("/updateOrderStatus/" + codOrderId + "/CANCELLED")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("already completed")));
    }
}
