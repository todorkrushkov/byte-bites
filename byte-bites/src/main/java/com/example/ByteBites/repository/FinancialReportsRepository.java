package com.example.ByteBites.repository;

import com.example.ByteBites.models.Accounts;
import com.example.ByteBites.models.FinancialReports;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FinancialReportsRepository extends JpaRepository<FinancialReports, Long> {
    List<FinancialReports> findByDeliver(Accounts deliver);
}
