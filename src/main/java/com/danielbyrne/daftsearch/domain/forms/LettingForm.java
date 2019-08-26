package com.danielbyrne.daftsearch.domain.forms;

import com.danielbyrne.daftsearch.domain.County;
import com.danielbyrne.daftsearch.domain.ModeOfTransport;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class LettingForm {

    @NotNull
    private int minBeds;

    @NotNull
    private float maxPrice;

    @NotNull
    private int distanceInKms;

    @NotNull
    private String location;

    @NotNull
    @Size(min = 1, max = 5)
    private County[] counties;

    private int commuteInMinutes;
    private ModeOfTransport modeOfTransport;
}
