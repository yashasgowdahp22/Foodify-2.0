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
public class Phase4DeliveryRuntimeTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static String adminToken;
    private static String ownerToken;
    private static String customerAToken;
    private static String customerBToken;
    private static String partner1Token;
    private static String partner2Token;

    private static Long customerAId;
    private static Long partner1Id;
    private static Long partner2Id;

    private static Long restaurantId;
    private static Long menuItemId;
    private static Long codOrderId;
    private static Long deliveryId;

    private static final String UNIQUE_KEY = String.valueOf(System.currentTimeMillis()).substring(7);

    @Test
    @Order(1)
    @DisplayName("1. Setup: Admin creates Restaurant, Owner, Menu Item, Customer, and 2 Delivery Partners")
    void setupPhase4Accounts() throws Exception {
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

        // Create Restaurant Owner
        String ownerEmail = "owner_p4_" + UNIQUE_KEY + "@bengaluru.com";
        String createOwnerJson = """
            {
                "name": "Owner Ramesh",
                "email": "%s",
                "address": "100 MG Road",
                "password": "Owner@123",
                "role": "RESTAURANT"
            }
            """.formatted(ownerEmail);
        mockMvc.perform(post("/api/auth/admin/create-user")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createOwnerJson))
                .andExpect(status().isCreated());

        MvcResult ownerRes = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    { "email": "%s", "password": "Owner@123" }
                    """.formatted(ownerEmail)))
                .andExpect(status().isOk())
                .andReturn();
        ownerToken = objectMapper.readTree(ownerRes.getResponse().getContentAsString()).get("token").asText();

        // Create Restaurant
        String rJson = """
            {
                "restaurantName": "Royal Biryani %s",
                "address": "100 MG Road",
                "contactNumber": "9876543210",
                "email": "royal_%s@bengaluru.com",
                "description": "Authentic Dum Biryani",
                "open": true,
                "ownerEmail": "%s"
            }
            """.formatted(UNIQUE_KEY, UNIQUE_KEY, ownerEmail);
        mockMvc.perform(post("/addRestaurant")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(rJson))
                .andExpect(status().isCreated());

        MvcResult rRes = mockMvc.perform(get("/myRestaurant")
                .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isOk())
                .andReturn();
        restaurantId = objectMapper.readTree(rRes.getResponse().getContentAsString()).get("id").asLong();

        // Owner adds Menu Item ($15.00)
        String itemJson = """
            {
                "name": "Mutton Dum Biryani %s",
                "description": "Slow cooked fragrant basmati rice",
                "price": 15.00,
                "category": "Main Course",
                "available": true
            }
            """.formatted(UNIQUE_KEY);
        mockMvc.perform(post("/addMenuItem")
                .header("Authorization", "Bearer " + ownerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(itemJson))
                .andExpect(status().isCreated());

        MvcResult menuRes = mockMvc.perform(get("/myRestaurant/menu")
                .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isOk())
                .andReturn();
        menuItemId = objectMapper.readTree(menuRes.getResponse().getContentAsString()).get(0).get("id").asLong();

        // Create Delivery Partner 1
        String partner1Email = "delivery1_" + UNIQUE_KEY + "@foodify.com";
        String createP1Json = """
            {
                "name": "Rider Rahul",
                "email": "%s",
                "address": "Indiranagar 5th Cross",
                "password": "Rider@123",
                "role": "DELIVERY"
            }
            """.formatted(partner1Email);
        mockMvc.perform(post("/api/auth/admin/create-user")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createP1Json))
                .andExpect(status().isCreated());

        MvcResult p1Res = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    { "email": "%s", "password": "Rider@123" }
                    """.formatted(partner1Email)))
                .andExpect(status().isOk())
                .andReturn();
        partner1Token = objectMapper.readTree(p1Res.getResponse().getContentAsString()).get("token").asText();

        // Create Delivery Partner 2
        String partner2Email = "delivery2_" + UNIQUE_KEY + "@foodify.com";
        String createP2Json = """
            {
                "name": "Rider Suresh",
                "email": "%s",
                "address": "Koramangala 4th Block",
                "password": "Rider@123",
                "role": "DELIVERY"
            }
            """.formatted(partner2Email);
        mockMvc.perform(post("/api/auth/admin/create-user")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createP2Json))
                .andExpect(status().isCreated());

        MvcResult p2Res = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    { "email": "%s", "password": "Rider@123" }
                    """.formatted(partner2Email)))
                .andExpect(status().isOk())
                .andReturn();
        partner2Token = objectMapper.readTree(p2Res.getResponse().getContentAsString()).get("token").asText();

        // Register Customer A
        String customerAEmail = "cust_p4_a_" + UNIQUE_KEY + "@example.com";
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "name": "Customer Priya",
                        "email": "%s",
                        "address": "77 Residency Road",
                        "password": "Customer@123"
                    }
                    """.formatted(customerAEmail)))
                .andExpect(status().isCreated());

        MvcResult custARes = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    { "email": "%s", "password": "Customer@123" }
                    """.formatted(customerAEmail)))
                .andExpect(status().isOk())
                .andReturn();
        customerAToken = objectMapper.readTree(custARes.getResponse().getContentAsString()).get("token").asText();

        MvcResult meRes = mockMvc.perform(get("/api/auth/me")
                .header("Authorization", "Bearer " + customerAToken))
                .andExpect(status().isOk())
                .andReturn();
        customerAId = objectMapper.readTree(meRes.getResponse().getContentAsString()).get("id").asLong();

        // Register Customer B
        String customerBEmail = "cust_p4_b_" + UNIQUE_KEY + "@example.com";
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "name": "Customer Sneha",
                        "email": "%s",
                        "address": "99 Brigade Road",
                        "password": "Customer@123"
                    }
                    """.formatted(customerBEmail)))
                .andExpect(status().isCreated());

        MvcResult custBRes = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    { "email": "%s", "password": "Customer@123" }
                    """.formatted(customerBEmail)))
                .andExpect(status().isOk())
                .andReturn();
        customerBToken = objectMapper.readTree(custBRes.getResponse().getContentAsString()).get("token").asText();
    }

    @Test
    @Order(2)
    @DisplayName("2. Customer A places a COD order: status PENDING, payment PENDING, no delivery yet")
    void customerPlacesCODOrder() throws Exception {
        // Add item to cart
        mockMvc.perform(post("/myCart/items")
                .header("Authorization", "Bearer " + customerAToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "menuItemId": %d,
                        "quantity": 2
                    }
                    """.formatted(menuItemId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAmount").value(30.00));

        // Checkout COD
        MvcResult checkoutRes = mockMvc.perform(post("/checkout")
                .header("Authorization", "Bearer " + customerAToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "deliveryAddress": "77 Residency Road, Flat 3B",
                        "specialInstructions": "Call when near gate",
                        "paymentMethod": "COD"
                    }
                    """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.paymentMethod").value("COD"))
                .andExpect(jsonPath("$.paymentStatus").value("PENDING"))
                .andReturn();

        codOrderId = objectMapper.readTree(checkoutRes.getResponse().getContentAsString()).get("id").asLong();
    }

    @Test
    @Order(3)
    @DisplayName("3. Delivery can only be assigned to orders in PREPARING status")
    void assignmentRequiresPreparingStatus() throws Exception {
        String partner1Email = "delivery1_" + UNIQUE_KEY + "@foodify.com";
        String assignJson = """
            {
                "deliveryPartnerEmail": "%s"
            }
            """.formatted(partner1Email);

        // Attempt assignment while order is PENDING -> rejected
        mockMvc.perform(post("/assignDelivery/" + codOrderId)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(assignJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("PREPARING")));

        // Restaurant Owner confirms order: PENDING -> CONFIRMED
        mockMvc.perform(put("/updateOrderStatus/" + codOrderId + "/CONFIRMED")
                .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isOk());

        // Attempt assignment while order is CONFIRMED -> rejected
        mockMvc.perform(post("/assignDelivery/" + codOrderId)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(assignJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("PREPARING")));

        // Restaurant Owner advances to PREPARING
        mockMvc.perform(put("/updateOrderStatus/" + codOrderId + "/PREPARING")
                .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isOk());
    }

    @Test
    @Order(4)
    @DisplayName("4. Restaurant Owner cannot mark orders OUT_FOR_DELIVERY or DELIVERED")
    void restaurantOwnerCannotMarkDelivered() throws Exception {
        mockMvc.perform(put("/updateOrderStatus/" + codOrderId + "/OUT_FOR_DELIVERY")
                .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("reserved for delivery partners")));

        mockMvc.perform(put("/updateOrderStatus/" + codOrderId + "/DELIVERED")
                .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("reserved for delivery partners")));
    }

    @Test
    @Order(5)
    @DisplayName("5. Admin assigns Delivery Partner 1 to the order")
    void adminAssignsDeliveryPartner() throws Exception {
        String partner1Email = "delivery1_" + UNIQUE_KEY + "@foodify.com";
        String assignJson = """
            {
                "deliveryPartnerEmail": "%s",
                "deliveryAddress": "77 Residency Road, Flat 3B"
            }
            """.formatted(partner1Email);

        MvcResult res = mockMvc.perform(post("/assignDelivery/" + codOrderId)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(assignJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.deliveryStatus").value("ASSIGNED"))
                .andExpect(jsonPath("$.orderId").value(codOrderId))
                .andExpect(jsonPath("$.deliveryPartnerEmail").value(partner1Email))
                .andExpect(jsonPath("$.trackingNumber", containsString("TRK-")))
                .andReturn();

        JsonNode root = objectMapper.readTree(res.getResponse().getContentAsString());
        deliveryId = root.get("deliveryId").asLong();
        assertNotNull(deliveryId);
    }

    @Test
    @Order(6)
    @DisplayName("6. Prevent duplicate delivery assignment")
    void preventDuplicateDeliveryAssignment() throws Exception {
        String partner2Email = "delivery2_" + UNIQUE_KEY + "@foodify.com";
        String duplicateAssignJson = """
            {
                "deliveryPartnerEmail": "%s"
            }
            """.formatted(partner2Email);

        mockMvc.perform(post("/assignDelivery/" + codOrderId)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(duplicateAssignJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("already assigned")));
    }

    @Test
    @Order(7)
    @DisplayName("7. Delivery Partner 1 views their deliveries; Partner 2 sees empty list")
    void partnerViewsAssignedDeliveries() throws Exception {
        // Partner 1 views their deliveries
        MvcResult p1ListRes = mockMvc.perform(get("/myDeliveries")
                .header("Authorization", "Bearer " + partner1Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].deliveryId").value(deliveryId))
                .andExpect(jsonPath("$[0].orderId").value(codOrderId))
                .andReturn();

        // Partner 1 views single delivery details
        mockMvc.perform(get("/myDeliveries/" + deliveryId)
                .header("Authorization", "Bearer " + partner1Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deliveryId").value(deliveryId))
                .andExpect(jsonPath("$.restaurantName", containsString("Royal Biryani")));

        // Partner 2 views their deliveries (should be empty)
        mockMvc.perform(get("/myDeliveries")
                .header("Authorization", "Bearer " + partner2Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @Order(8)
    @DisplayName("8. Cross-partner isolation: Partner 2 cannot view or update Partner 1's delivery")
    void crossPartnerIsolation() throws Exception {
        // Partner 2 tries to view Partner 1's delivery -> 401/403
        mockMvc.perform(get("/myDeliveries/" + deliveryId)
                .header("Authorization", "Bearer " + partner2Token))
                .andExpect(status().isUnauthorized());

        // Partner 2 tries to update Partner 1's delivery status -> 401/403
        mockMvc.perform(put("/myDeliveries/" + deliveryId + "/status/PICKED_UP")
                .header("Authorization", "Bearer " + partner2Token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(9)
    @DisplayName("9. Invalid delivery status transitions rejected")
    void invalidDeliveryTransitionsRejected() throws Exception {
        // Current status is ASSIGNED. Attempt direct transition to DELIVERED -> rejected
        mockMvc.perform(put("/myDeliveries/" + deliveryId + "/status/DELIVERED")
                .header("Authorization", "Bearer " + partner1Token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Invalid delivery status transition")));

        // Attempt transition to random invalid status -> rejected
        mockMvc.perform(put("/myDeliveries/" + deliveryId + "/status/LOST")
                .header("Authorization", "Bearer " + partner1Token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Invalid delivery status")));
    }

    @Test
    @Order(10)
    @DisplayName("10. Valid delivery transitions and Order status synchronization")
    void deliveryAndOrderSynchronization() throws Exception {
        // Partner 1 advances: ASSIGNED -> PICKED_UP
        mockMvc.perform(put("/myDeliveries/" + deliveryId + "/status/PICKED_UP")
                .header("Authorization", "Bearer " + partner1Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deliveryStatus").value("PICKED_UP"));

        // Partner 1 advances: PICKED_UP -> OUT_FOR_DELIVERY
        mockMvc.perform(put("/myDeliveries/" + deliveryId + "/status/OUT_FOR_DELIVERY")
                .header("Authorization", "Bearer " + partner1Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deliveryStatus").value("OUT_FOR_DELIVERY"))
                .andExpect(jsonPath("$.orderStatus").value("OUT_FOR_DELIVERY"));

        // Verify Customer sees order as OUT_FOR_DELIVERY
        mockMvc.perform(get("/myOrders/" + codOrderId)
                .header("Authorization", "Bearer " + customerAToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OUT_FOR_DELIVERY"));
    }

    @Test
    @Order(11)
    @DisplayName("11. Delivery completion: Order becomes DELIVERED and COD payment becomes SUCCESS")
    void deliveryCompletionAndCODPaymentSuccess() throws Exception {
        // Partner 1 advances: OUT_FOR_DELIVERY -> DELIVERED
        MvcResult res = mockMvc.perform(put("/myDeliveries/" + deliveryId + "/status/DELIVERED")
                .header("Authorization", "Bearer " + partner1Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deliveryStatus").value("DELIVERED"))
                .andExpect(jsonPath("$.orderStatus").value("DELIVERED"))
                .andReturn();

        JsonNode root = objectMapper.readTree(res.getResponse().getContentAsString());
        assertNotNull(root.get("deliveredAt"));

        // Verify Customer order details shows status DELIVERED, paymentStatus SUCCESS, and transactionId generated
        MvcResult orderRes = mockMvc.perform(get("/myOrders/" + codOrderId)
                .header("Authorization", "Bearer " + customerAToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DELIVERED"))
                .andExpect(jsonPath("$.paymentStatus").value("SUCCESS"))
                .andReturn();

        JsonNode orderNode = objectMapper.readTree(orderRes.getResponse().getContentAsString());
        String txnId = orderNode.get("transactionId").asText();
        assertNotNull(txnId);
        assertTrue(txnId.startsWith("TXN-COD-"));

        // Attempting to transition after completion -> rejected
        mockMvc.perform(put("/myDeliveries/" + deliveryId + "/status/DELIVERED")
                .header("Authorization", "Bearer " + partner1Token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("already completed")));
    }

    @Test
    @Order(12)
    @DisplayName("12. Legacy endpoint security: Customer B cannot access Customer A's cart or order")
    void legacyEndpointHardening() throws Exception {
        // Customer B tries to view Customer A's cart via legacy GET /getCart/{customerAId} -> 401/403
        mockMvc.perform(get("/getCart/" + customerAId)
                .header("Authorization", "Bearer " + customerBToken))
                .andExpect(status().isUnauthorized());

        // Customer B tries to view Customer A's order via legacy GET /getOrder/{codOrderId} -> 401/403
        mockMvc.perform(get("/getOrder/" + codOrderId)
                .header("Authorization", "Bearer " + customerBToken))
                .andExpect(status().isUnauthorized());

        // Customer B tries to update Customer A's order via legacy PUT /updateOrder/{codOrderId} -> 401/403
        mockMvc.perform(put("/updateOrder/" + codOrderId)
                .header("Authorization", "Bearer " + customerBToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "deliveryAddress": "Hacked Address"
                    }
                    """))
                .andExpect(status().isUnauthorized());
    }
}
