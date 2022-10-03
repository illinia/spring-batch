package io.spring.batch.helloworld.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@Getter
@Setter
@ToString
@Entity
@Table(name = "customer")
//@XmlRootElement
public class Customer {
    @Id
    private Long id;

    @Column(name = "firstname")
    private String firstName;

    @Column(name = "middleInitial")
    private String middleInitial;

    @Column(name = "lastName")
    private String lastName;
//    private String addressNumber;
//    private String street;
    private String address;
    private String city;
    private String state;
    private String zipCode;

//    private List<Transaction> transactions;

//    @XmlElementWrapper(name = "transactions")
//    @XmlElement(name = "transaction")
//    public void setTransactions(List<Transaction> transactions) {
//        this.transactions = transactions;
//    }

}
