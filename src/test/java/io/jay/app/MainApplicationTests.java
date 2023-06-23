package io.jay.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class MainApplicationTests {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        customerRepository.deleteAll();
    }

    @Test
    void test_addCustomer_returnsCustomerId() {
        webTestClient.post()
                .uri("/customers")
                .bodyValue(new NewCustomerRequest("Jay"))
                .exchange()
                .expectBody(String.class);
    }

    @Test
    void test_addCustomer_persistsInDB() {
        webTestClient.post()
                .uri("/customers")
                .bodyValue(new NewCustomerRequest("Jay"))
                .exchange();


        assertTrue(customerRepository.findAll().iterator().hasNext());
    }

    @Test
    void test_purchase_returnsCustomer() {
        webTestClient.post()
                .uri("/customers")
                .bodyValue(new NewCustomerRequest("Jay"))
                .exchange();


        webTestClient.patch()
                .uri("/customers/Jay")
                .bodyValue(new NewOrderRequest(199))
                .exchange()
                .expectBody(Customer.class);
    }

    @Test
    void test_purchase_addsOrder() {
        webTestClient.post()
                .uri("/customers")
                .bodyValue(new NewCustomerRequest("Jay"))
                .exchange();


        var response = webTestClient.patch()
                .uri("/customers/Jay")
                .bodyValue(new NewOrderRequest(199))
                .exchange()
                .expectBody(Customer.class)
                .returnResult()
                .getResponseBody();


        assertEquals("Jay", response.name());
        assertEquals(1, response.orders().size());
        assertNotNull(response.lastPurchased());

        var order = response.orders().iterator().next();
        assertEquals(199, order.total());
        assertNotNull(order.orderId());
        assertNotNull(order.purchasedDate());
    }

    @Test
    void test_all_returnsCustomers() {
        webTestClient.post()
                .uri("/customers")
                .bodyValue(new NewCustomerRequest("Jay"))
                .exchange();


        var response = webTestClient.get()
                .uri("/customers")
                .exchange()
                .expectBodyList(Customer.class)
                .returnResult()
                .getResponseBody();


        assertEquals(1, response.size());
        var customer = response.get(0);
        assertNotNull(customer.customerId());
        assertEquals("Jay", customer.name());
        assertNotNull(customer.memberSince());
        assertNull(customer.lastPurchased());
        assertEquals(0, customer.orders().size());
    }

    @Test
    void test_byName_returnsCustomer() {
        webTestClient.post()
                .uri("/customers")
                .bodyValue(new NewCustomerRequest("Jay"))
                .exchange();


        var response = webTestClient.get()
                .uri("/customers/Jay")
                .exchange()
                .expectBody(Customer.class)
                .returnResult()
                .getResponseBody();


        assertNotNull(response.customerId());
        assertEquals("Jay", response.name());
        assertNotNull(response.memberSince());
        assertNull(response.lastPurchased());
        assertEquals(0, response.orders().size());
    }
}