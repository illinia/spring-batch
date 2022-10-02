package io.spring.batch.helloworld.domain;

import java.util.List;

public interface TransactionDao {
    List<Transaction> getTransactionsByAccountNumber(String accountNumber);
}
