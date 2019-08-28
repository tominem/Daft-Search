package com.danielbyrne.daftsearch.tasks;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class ScheduleTask {

    RefreshRentalProperties refreshRentalProperties;

    public ScheduleTask(RefreshRentalProperties refreshRentalProperties) {
        this.refreshRentalProperties = refreshRentalProperties;
    }

    @Scheduled(initialDelay = 5000, fixedDelay = 86400000)
    public void loadRentalProperties() throws IOException {
        log.info("Loading Rental Properties on Thread: {}", Thread.currentThread().getName());
        refreshRentalProperties.loadRentals();
    }
}