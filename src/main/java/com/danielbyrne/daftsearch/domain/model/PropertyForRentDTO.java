package com.danielbyrne.daftsearch.domain.model;

import com.danielbyrne.daftsearch.domain.County;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PropertyForRentDTO {

    private String address;
    private String eircode;
    private int beds;
    private int baths;
    private String propertyType;
    private String priceString;
    private String link;
    private County county;

    private String readableDistance;
    private String readableDuration;

    private String moveInDate;
    private String leaseLength;

    private float monthlyRent;
}
