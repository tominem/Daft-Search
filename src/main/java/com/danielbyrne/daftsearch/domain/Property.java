package com.danielbyrne.daftsearch.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

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
    private String description;
}