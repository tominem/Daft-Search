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
    private Integer minBeds;

    @NotNull
    private Float maxPrice;

    @PositiveOrZero
    private Integer distanceInKms;

    @NotBlank
    private String location;

    @NotNull
    @Size(min = 1, max = 5)
    private County[] counties;

    @Min(0)
    private Integer commuteInMinutes;

    @NotNull
    private ModeOfTransport modeOfTransport;
}
