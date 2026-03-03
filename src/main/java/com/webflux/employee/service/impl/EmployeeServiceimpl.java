package com.webflux.employee.service.impl;

import com.webflux.employee.dto.EmployeeDto;
import com.webflux.employee.entity.Employee;
import com.webflux.employee.mapper.EmployeeMapper;
import com.webflux.employee.repository.EmployeeRepository;
import com.webflux.employee.service.EmployeeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class EmployeeServiceimpl implements EmployeeService {

    private EmployeeRepository employeeRepository;

    @Override
    public Mono<EmployeeDto> saveEmployee(EmployeeDto employeeDto) {

        Employee employee = EmployeeMapper.mapToEmployee(employeeDto);
        Mono<Employee> savedEmployee = employeeRepository.save(employee);
        return savedEmployee
                .map((employee1)->
                    EmployeeMapper.mapToEmployeeDto(employee1));
    }

    @Override
    public Mono<EmployeeDto> findById(String id) {
        Mono<Employee> employee = employeeRepository.findById(id);
        return employee.map(
                (employee1) ->
                        EmployeeMapper.mapToEmployeeDto(employee1)
        );
    }

    @Override
    public Flux<EmployeeDto> getAllEmployees() {
        Flux<Employee> employees = employeeRepository.findAll();
        return employees.map(
                (employee) -> EmployeeMapper.mapToEmployeeDto(employee)
        ).switchIfEmpty(Flux.empty());
    }

    @Override
    public Mono<EmployeeDto> updateEmployee(EmployeeDto employeeDto, String employeeId) {
        Mono<Employee> employee = employeeRepository.findById(employeeId);
        Mono<Employee> savedEmployee = employee.flatMap((existingEmployee)->{
            existingEmployee.setFirstName(employeeDto.getFirstName());
            existingEmployee.setLastName(employeeDto.getLastName());
            existingEmployee.setEmail(employeeDto.getEmail());

            return employeeRepository.save(existingEmployee);
        });
        return savedEmployee.map((sEmployee)-> EmployeeMapper.mapToEmployeeDto(sEmployee));
    }

    @Override
    public Mono<Void> deleteEmployee(String id) {
        return employeeRepository.deleteById(id);
    }
}
