package com.financeledger.financeledger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FinanceLedgerApplication {

    public static void main(String[] args) {

        SpringApplication.run(FinanceLedgerApplication.class, args);

        System.out.println("DB_USER=" + System.getenv("DB_USER"));
        System.out.println("DB_PASSWORD=" + System.getenv("DB_PASSWORD"));
    }

}
