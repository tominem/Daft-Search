package com.danielbyrne.daftsearch.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import javax.persistence.Lob;

@Getter
@Setter
@Document
public abstract class Property {

    @Id
    private Long id;
    private String address;
    private String eircode;
    private int beds;
    private int baths;
    private String propertyType;
    private float price;
    private String priceString;
    private String link;
    private County county;

    private Long distanceInMetres;
    private Long duration;

    private String readableDistance;
    private String readableDuration;

    @Lob
    private String description;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Property{");
        sb.append("id=").append(id);
        sb.append(", address='").append(address).append('\'');
        sb.append(", link='").append(link).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
