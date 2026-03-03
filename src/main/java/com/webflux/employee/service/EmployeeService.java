package com.webflux.employee.service;

import com.webflux.employee.dto.EmployeeDto;
import com.webflux.employee.entity.Employee;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EmployeeService {
    Mono<EmployeeDto> saveEmployee(EmployeeDto employeeDto);

    Mono<EmployeeDto> findById(String id);

    Flux<EmployeeDto> getAllEmployees();
}
