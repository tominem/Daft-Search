package com.danielbyrne.daftsearch.controllers;

import com.danielbyrne.daftsearch.domain.forms.SaleForm;
import com.danielbyrne.daftsearch.domain.model.PropertyForSaleDTO;
import com.danielbyrne.daftsearch.services.PropertyForSaleService;
import com.google.maps.errors.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Set;

@Slf4j
@RequestMapping(PropertyForSaleController.BASE_URL)
@Controller
public class PropertyForSaleController {

    public final static String BASE_URL = "/sales";
    private final static String SEARCH_FORM = "property/sales/searchform";
    private final PropertyForSaleService propertyForSaleService;

    public PropertyForSaleController(PropertyForSaleService propertyForSaleService) {
        this.propertyForSaleService = propertyForSaleService;
    }

    @GetMapping("/find")
    public String showForm(SaleForm saleForm) {
        log.debug("Showing search form");
        return SEARCH_FORM;
}

    @GetMapping
    public String processSearchForm(@Valid @ModelAttribute("saleForm") SaleForm saleForm, BindingResult bindingResult,
                                    Model model)
            throws InterruptedException, ApiException, IOException {

        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(objectError -> log.debug(objectError.toString()));
            return SEARCH_FORM;
        }

        Set<PropertyForSaleDTO> filteredProperties = propertyForSaleService.filterPropertiesByDaftAttributes(saleForm);
        log.debug("Received {} properties via daft attributes.", filteredProperties.size());

        if (filteredProperties.size()==0) return "property/noresults";

        Set<PropertyForSaleDTO> result = propertyForSaleService.filterPropertiesByGoogle(filteredProperties, saleForm);
        log.debug("Received {} properties via Google.", result.size());

        if (result.size()==0) return "property/noresults";
        model.addAttribute("propertiesforsale", result);
        return "property/sales";
    }

    @GetMapping("/all")
    public String getAllProperties(Model model) {
        model.addAttribute("propertiesforsale", propertyForSaleService.getAllProperties());
        return "property/sales";
    }
}