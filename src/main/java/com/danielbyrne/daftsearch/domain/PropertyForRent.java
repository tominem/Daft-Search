package com.danielbyrne.daftsearch.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document
public class PropertyForRent extends Property {

    private String moveInDate;
    private String leaseLength;
    private float monthlyRent;

    public void setMonthlyRent(){
        boolean weekly = getPriceString().toLowerCase().contains("per week");
        this.monthlyRent = weekly ? (getPrice()*52)/12 : getPrice();
    }
}