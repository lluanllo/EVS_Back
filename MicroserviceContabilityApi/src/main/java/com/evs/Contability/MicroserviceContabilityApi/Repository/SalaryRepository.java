package com.evs.Contability.MicroserviceContabilityApi.Repository;

import com.evs.Contability.MicroserviceContabilityApi.Entities.Salary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SalaryRepository extends JpaRepository<Salary, Long> {

    Optional<Salary> findByTeacherId(Long teacherId);

    List<Salary> findByContractType(String contractType);

    List<Salary> findByActive(Boolean active);

    boolean existsByTeacherId(Long teacherId);
}

