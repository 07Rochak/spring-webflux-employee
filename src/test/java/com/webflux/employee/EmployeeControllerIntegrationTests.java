package com.webflux.employee;

import com.webflux.employee.dto.EmployeeDto;
import com.webflux.employee.repository.EmployeeRepository;
import com.webflux.employee.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Collections;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient // This annotation tells Spring Boot Test to create and configure a WebTestClient bean for your test.
public class EmployeeControllerIntegrationTests {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private EmployeeRepository employeeRepository;

    @BeforeEach
    public void before(){
        System.out.println("Before test cases");
        employeeRepository.deleteAll().subscribe();
    }

    @Test
    public void testSaveEmployee(){
        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setFirstName("Rochak");
        employeeDto.setLastName("Shrivastav");
        employeeDto.setEmail("rochakshrivastav02@gmail.com");

        // Send an HTTP POST request using WebTestClient
        webTestClient.post()
                // Target API endpoint for creating an employee
                .uri("/api/employees")
                // Set the request Content-Type header to JSON
                .contentType(MediaType.APPLICATION_JSON)
                // Tell the server that the client expects JSON in the response
                .accept(MediaType.APPLICATION_JSON)
                // Add the request body containing the employee data
                // Mono.just(...) wraps the DTO into a reactive publisher used in Spring WebFlux
                .body(Mono.just(employeeDto), EmployeeDto.class)
                // Execute the request
                .exchange()
                // Verify that the HTTP response status is 201 CREATED
                .expectStatus().isCreated()
                // Start validating the response body
                .expectBody()
                // Print the response body to the console (useful for debugging)
                .consumeWith(System.out::println)
                // Validate that the JSON response field "firstName" matches the input DTO
                .jsonPath("$.firstName").isEqualTo(employeeDto.getFirstName())
                // Validate that the JSON response field "lastName" matches the input DTO
                .jsonPath("$.lastName").isEqualTo(employeeDto.getLastName())
                // Validate that the JSON response field "email" matches the input DTO
                .jsonPath("$.email").isEqualTo(employeeDto.getEmail());
    }

    @Test
    public void testGetSingleEmployee(){
        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setFirstName("Web");
        employeeDto.setLastName("Flux");
        employeeDto.setEmail("webflux@gmail.com");

        EmployeeDto savedEmployee = employeeService.saveEmployee(employeeDto).block();

        // Send an HTTP GET request using WebTestClient
        webTestClient.get()
                // Target API endpoint with a path variable {id}
                // Collections.singletonMap("id", savedEmployee.getId()) replaces {id} with the actual employee ID
                .uri("/api/employees/{id}", Collections.singletonMap("id", savedEmployee.getId()))
                // Execute the request and receive the response
                .exchange()
                // Verify that the HTTP response status is 200 OK
                .expectStatus().isOk()
                // Start validating the response body
                .expectBody()
                // Print the response body to the console (useful for debugging the response JSON)
                .consumeWith(System.out::println)
                // Validate that the JSON response field "firstName" matches the expected value
                .jsonPath("$.firstName").isEqualTo(employeeDto.getFirstName())
                // Validate that the JSON response field "lastName" matches the expected value
                .jsonPath("$.lastName").isEqualTo(employeeDto.getLastName())
                // Validate that the JSON response field "email" matches the expected value
                .jsonPath("$.email").isEqualTo(employeeDto.getEmail());
    }

    @Test
    public void testGetAllEmployees(){
        webTestClient.get().uri("/api/employees")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(EmployeeDto.class)
                .consumeWith(System.out::println);
    }

    @Test
    public void testUpdateEmployee(){
        EmployeeDto employeeDto = new EmployeeDto();

        employeeDto.setFirstName("Spring");
        employeeDto.setLastName("Boot");
        employeeDto.setEmail("springboot@gmail.com");

        EmployeeDto savedEmployee = employeeService.saveEmployee(employeeDto).block();

        EmployeeDto updatedEmployee = new EmployeeDto();
        updatedEmployee.setFirstName("Web");
        updatedEmployee.setLastName("Flux");
        updatedEmployee.setEmail("webflux@gmail.com");

        webTestClient.put().uri("/api/employees/{id}", Collections.singletonMap("id", savedEmployee.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(updatedEmployee), EmployeeDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.firstName").isEqualTo(updatedEmployee.getFirstName())
                .jsonPath("$.lastName").isEqualTo(updatedEmployee.getLastName())
                .jsonPath("$.email").isEqualTo(updatedEmployee.getEmail());
    }

    @Test
    public void testDeleteEmployee(){

        EmployeeDto employeeDto = new EmployeeDto();

        employeeDto.setFirstName("Spring");
        employeeDto.setLastName("Boot");
        employeeDto.setEmail("springboot@gmail.com");

        EmployeeDto savedEmployee = employeeService.saveEmployee(employeeDto).block();

        webTestClient.delete().uri("/api/employees/{id}",  Collections.singletonMap("id", savedEmployee.getId()))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println);


    }
}
