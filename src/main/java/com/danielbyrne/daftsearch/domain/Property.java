package com.danielbyrne.daftsearch.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

@Data
@Entity
public class Property {

    @Id
    private Long id;
    private String address;
    private String eircode;
    private int beds;
    private int baths;
    private String propertyType;
    private int price;

    @Lob
    private String description;
}