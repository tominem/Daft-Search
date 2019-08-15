package com.danielbyrne.daftsearch.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PropertyDTO {

    private String address;
    private String eircode;
    private int beds;
    private int baths;
    private String propertyType;
    private String description;
}