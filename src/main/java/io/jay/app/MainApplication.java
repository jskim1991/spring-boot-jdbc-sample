package io.jay.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

interface CustomerRepository extends CrudRepository<Customer, String> {
    Optional<Customer> findByName(String name);
}

@SpringBootApplication
public class MainApplication {

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }
}

record Customer(
        @Id String customerId,
        String name,
        ZonedDateTime memberSince,
        ZonedDateTime lastPurchased,
        @MappedCollection(idColumn = "CUSTOMER_ID") Set<Order> orders,
        @Version int version
) {
}

@Table("ORDERS")
record Order(
        @Id String orderId,
        int total,
        ZonedDateTime purchasedDate,
        @Version int version
) {
}

@Controller
@ResponseBody
@RequestMapping("/customers")
class CustomerController {

    private final CustomerRepository repository;

    CustomerController(CustomerRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public Iterable<Customer> all() {
        return repository.findAll();
    }

    @GetMapping("/{name}")
    public Customer byName(@PathVariable String name) {
        return repository.findByName(name).orElseThrow(() -> new NoSuchElementException("No customer found with name: " + name));
    }

    @PostMapping
    public String addCustomer(@RequestBody NewCustomerRequest customer) {
        Customer saved = repository.save(new Customer(UUID.randomUUID().toString(), customer.name(), ZonedDateTime.now(), null, new HashSet<>(), 0));
        return saved.customerId();
    }

    @PatchMapping("/{name}")
    @Transactional
    public Customer purchase(@PathVariable String name, @RequestBody NewOrderRequest orderRequest) {
        var customer = repository.findByName(name).orElseThrow(() -> new NoSuchElementException("No customer found with name: " + name));

        var order = new Order(UUID.randomUUID().toString(), orderRequest.total(), ZonedDateTime.now(), 0);
        customer.orders().add(order);

        customer = new Customer(customer.customerId(), customer.name(), customer.memberSince(), ZonedDateTime.now(), customer.orders(), customer.version());
        return repository.save(customer);
    }
}

record NewCustomerRequest(String name) {
}

record NewOrderRequest(int total) {
}