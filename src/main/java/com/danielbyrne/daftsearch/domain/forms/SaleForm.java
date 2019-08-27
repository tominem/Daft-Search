package com.danielbyrne.daftsearch.domain.forms;

import com.danielbyrne.daftsearch.domain.County;
import com.danielbyrne.daftsearch.domain.ModeOfTransport;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Getter
@Setter
public class SaleForm {

    @Positive
    @NotNull
    private Integer minBeds;

    @Positive
    @NotNull
    private Float maxPrice;

    @Positive
    @NotNull
    private Integer distanceInKms;

    @NotBlank
    private String location;

    @NotNull
    @Size(min = 1, max = 5)
    private County[] counties;

    @Positive
    @NotNull
    private Integer commuteInMinutes;

    @NotNull
    private ModeOfTransport modeOfTransport;
}
