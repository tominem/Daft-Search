package com.danielbyrne.daftsearch.domain.forms;

import com.danielbyrne.daftsearch.domain.County;
import com.danielbyrne.daftsearch.domain.ModeOfTransport;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;

@Getter
@Setter
public class SaleForm {

    @NotNull
    private int minBeds;

    @NotNull
    private float maxPrice;

    @PositiveOrZero
    private int distanceInKms;

    @NotBlank
    private String location;

    @NotNull
    @Size(min = 1, max = 5)
    private County[] counties;

    @Min(0)
    private int commuteInMinutes;

    @NotNull
    private ModeOfTransport modeOfTransport;
}
