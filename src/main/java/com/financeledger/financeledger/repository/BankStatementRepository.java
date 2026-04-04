package com.financeledger.financeledger.repository;

import com.financeledger.financeledger.entity.BankStatement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BankStatementRepository extends JpaRepository<BankStatement, UUID> {

}
