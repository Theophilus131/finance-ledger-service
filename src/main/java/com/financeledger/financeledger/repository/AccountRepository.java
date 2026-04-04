package com.financeledger.financeledger.repository;

import com.financeledger.financeledger.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {
    List<Account> findByUserId(UUID userId);
    Optional<Account> findByCode(String code);
    boolean existsByCode(String code);
}
