package io.jay.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.domain.Persistable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@SpringBootApplication
public class MainApplication {

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }
}

record Customer (@Id String id, String name, int orderCount, ZonedDateTime memberSince, ZonedDateTime lastPurchased, @Version int version) {
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
    public String create(@RequestBody NewCustomerRequest customer) {
        Customer saved = repository.save(new Customer(UUID.randomUUID().toString(), customer.name(), 0, ZonedDateTime.now(), null, 0));
        return saved.id();
    }

    @PatchMapping("/{name}")
    public Customer purchase(@PathVariable String name) {
        var customer = repository.findByName(name).orElseThrow(() -> new NoSuchElementException("No customer found with name: " + name));
        customer = new Customer(customer.id(), customer.name(), customer.orderCount() + 1, customer.memberSince(), ZonedDateTime.now(), customer.version());
        return repository.save(customer);
    }
}

record NewCustomerRequest(String name) {
}

interface CustomerRepository extends CrudRepository<Customer, String> {
    Optional<Customer> findByName(String name);
}
