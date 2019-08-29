package com.danielbyrne.daftsearch.tasks;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ScheduleTask {

    RefreshRentalProperties refreshRentalProperties;
    RefreshSharedProperties refreshSharedProperties;
    RefreshPropertiesForSale refreshPropertiesForSale;

    public ScheduleTask(RefreshRentalProperties refreshRentalProperties,
                        RefreshSharedProperties refreshSharedProperties,
                        RefreshPropertiesForSale refreshPropertiesForSale) {
        this.refreshRentalProperties = refreshRentalProperties;
        this.refreshSharedProperties = refreshSharedProperties;
        this.refreshPropertiesForSale = refreshPropertiesForSale;
    }

    //    @Scheduled(initialDelay = 5000, fixedDelay = 86400000)
//    public void loadRentalProperties() throws IOException {
//        log.info("Loading Rental Properties on Thread: {}", Thread.currentThread().getName());
//        refreshRentalProperties.loadRentals();
//    }
//
//    @Scheduled(initialDelay = 5000, fixedDelay = 86400000)
//    public void loadSharedProperties() throws IOException {
//        log.info("Loading Shared Properties on Thread: {}", Thread.currentThread().getName());
//        refreshSharedProperties.loadSharedProperties();
//    }
//
//    @Scheduled(initialDelay = 5000, fixedDelay = 86400000)
//    public void loadSaleProperties() throws Exception {
//        log.info("Loading Properties For Sale on Thread: {}", Thread.currentThread().getName());
//        refreshPropertiesForSale.loadSales();
//    }
}