package com.danielbyrne.daftsearch.domain.model;

import com.danielbyrne.daftsearch.domain.County;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PropertyForSaleDTO {

    private String address;
    private int beds;
    private int baths;
    private String propertyType;
    private String priceString;
    private String link;
    private County county;

    private Long distanceKm;
    private Long durationMin;

    private String readableDistance;
    private String readableDuration;
}
