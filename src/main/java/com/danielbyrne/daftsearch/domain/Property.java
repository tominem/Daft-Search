package com.danielbyrne.daftsearch.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

@Data
@Entity
public abstract class Property {

    @Id
    private Long id;
    private String address;
    private String eircode;
    private int beds;
    private int baths;
    private String propertyType;
    private int price;
    private String priceString;
    private String link;

    private Long distanceInMetres;
    private Long duration;

    private String readableDistance;
    private String readableDuration;

    @Lob
    private String description;

    @Lob
    private Byte[] image;

}
