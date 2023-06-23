package io.jay.app;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;

import java.time.ZonedDateTime;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJdbcTest
public class CustomerRepositoryTests {

    @Autowired
    CustomerRepository customerRepository;

    @Test
    void test_customerToOrderRelations() {
        var newCustomer = new Customer("1", "Jay", ZonedDateTime.now(), null, new HashSet<>(), 0);
        var savedCustomer = customerRepository.save(newCustomer);


        var newOrder = new Order("1001", 199, ZonedDateTime.now(), 0);
        savedCustomer.orders().add(newOrder);

        customerRepository.save(savedCustomer);

        var foundCustomer = customerRepository.findByName("Jay").get();

        assertEquals("1", foundCustomer.customerId());
        assertEquals("Jay", foundCustomer.name());

        assertEquals(1, foundCustomer.orders().size());
        var foundOrder = foundCustomer.orders().iterator().next();
        assertEquals("1001", foundOrder.orderId());
        assertEquals(199, foundOrder.total());
    }
}
