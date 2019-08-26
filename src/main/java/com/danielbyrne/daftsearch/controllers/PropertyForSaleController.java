package com.danielbyrne.daftsearch.controllers;

import com.danielbyrne.daftsearch.domain.County;
import com.danielbyrne.daftsearch.domain.ModeOfTransport;
import com.danielbyrne.daftsearch.domain.forms.SaleForm;
import com.danielbyrne.daftsearch.domain.model.PropertyForSaleDTO;
import com.danielbyrne.daftsearch.services.PropertyForSaleService;
import com.google.maps.errors.ApiException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Set;

@RequestMapping(PropertyForSaleController.BASE_URL)
@Controller
public class PropertyForSaleController {

    public final static String BASE_URL = "/properties/sales";
    private final PropertyForSaleService propertyForSaleService;

    public PropertyForSaleController(PropertyForSaleService propertyForSaleService) {
        this.propertyForSaleService = propertyForSaleService;
    }

    @GetMapping("/find")
    public String showForm(SaleForm saleForm) {
        return "property/sales/searchform";
    }

    @GetMapping
    public String processSearchForm(@Valid SaleForm saleForm, BindingResult bindingResult, Model model)
            throws InterruptedException, ApiException, IOException {

        if (bindingResult.hasErrors()) {
            return "property/sales/searchform";
        }

        float maxPrice = saleForm.getMaxPrice();
        int minBeds = saleForm.getMinBeds();
        County[] counties = saleForm.getCounties();

        int distance = saleForm.getDistanceInKms();
        int duration = saleForm.getCommuteInMinutes();
        String location = saleForm.getLocation();
        ModeOfTransport modeOfTransport = saleForm.getModeOfTransport();

        Set<PropertyForSaleDTO> filteredProperties = propertyForSaleService.filterPropertiesByDaftAttributes(maxPrice, minBeds, counties);

        Set<PropertyForSaleDTO> result = propertyForSaleService.filterPropertiesByGoogle(filteredProperties,
                                                                                    location,
                                                                                    modeOfTransport,
                                                                                    distance,
                                                                                    duration);

        model.addAttribute("propertiesforsale", result);
        return "property/sales";
    }

    @GetMapping("/all")
    public String getAllProperties(Model model) {
        model.addAttribute("propertiesforsale", propertyForSaleService.getAllProperties());
        return "property/sales";
    }
}