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
        if (this.getPriceString().toLowerCase().contains("week")){
            setMonthlyRent((getPrice()*52)/12);
        } {
            setMonthlyRent(getPrice());
        }
    }
}