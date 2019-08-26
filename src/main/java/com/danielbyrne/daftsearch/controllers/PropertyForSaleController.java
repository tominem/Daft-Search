package com.danielbyrne.daftsearch.controllers;

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

    public final static String BASE_URL = "/sales";
    private final PropertyForSaleService propertyForSaleService;

    public PropertyForSaleController(PropertyForSaleService propertyForSaleService) {
        this.propertyForSaleService = propertyForSaleService;
    }

    @GetMapping("/find")
    public String showForm(SaleForm saleForm) {
        return BASE_URL + "/searchform";
    }

    @GetMapping
    public String processSearchForm(@Valid SaleForm saleForm, BindingResult bindingResult, Model model)
            throws InterruptedException, ApiException, IOException {

        if (bindingResult.hasErrors()) {
            return BASE_URL + "/searchform";
        }

        Set<PropertyForSaleDTO> filteredProperties = propertyForSaleService.filterPropertiesByDaftAttributes(saleForm);

        Set<PropertyForSaleDTO> result = propertyForSaleService.filterPropertiesByGoogle(filteredProperties, saleForm);

        model.addAttribute("propertiesforsale", result);
        return "property/sales";
    }

    @GetMapping("/all")
    public String getAllProperties(Model model) {
        model.addAttribute("propertiesforsale", propertyForSaleService.getAllProperties());
        return "property/sales";
    }
}