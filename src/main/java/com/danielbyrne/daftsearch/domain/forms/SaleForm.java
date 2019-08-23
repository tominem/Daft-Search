package com.danielbyrne.daftsearch.domain.forms;

import com.danielbyrne.daftsearch.domain.County;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaleForm {

    private int minBeds;
    private int maxPrice;
    private int distanceInKms;
    private String location;
    County[] counties;
}
