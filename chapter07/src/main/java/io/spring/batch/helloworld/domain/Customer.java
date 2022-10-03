package io.spring.batch.helloworld.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
public class Customer {

    private String firstName;
    private String middleInitial;
    private String lastName;
//    private String addressNumber;
//    private String street;
    private String address;
    private String city;
    private String state;
    private String zipCode;

    private List<Transaction> transactions;

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();

        output.append(firstName);
        output.append(" ");
        output.append(middleInitial);
        output.append(". ");
        output.append(lastName);

        if (transactions != null && transactions.size() > 0) {
            output.append(" has ");
            output.append(transactions.size());
            output.append(" transactions.");
        } else {
            output.append(" has no transactions.");
        }
        return output.toString();
    }
}
