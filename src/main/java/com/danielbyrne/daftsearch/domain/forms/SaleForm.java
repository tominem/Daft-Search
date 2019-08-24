package com.danielbyrne.daftsearch.domain.forms;

import com.danielbyrne.daftsearch.domain.County;
import com.danielbyrne.daftsearch.domain.TravelMode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class SaleForm {

    @NotNull
    private int minBeds;

    @NotNull(message = "Testing")
    private int maxPrice;

    @NotNull
    private int distanceInKms;

    @NotNull
    private String location;

    @Size(min = 1, max = 5)
    private County[] counties;

    private int commuteInMinutes;
    private TravelMode travelMode;
}
