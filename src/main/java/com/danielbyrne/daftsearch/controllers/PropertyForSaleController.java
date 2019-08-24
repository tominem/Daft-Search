package com.danielbyrne.daftsearch.controllers;

import com.danielbyrne.daftsearch.domain.County;
import com.danielbyrne.daftsearch.domain.TravelMode;
import com.danielbyrne.daftsearch.domain.forms.SaleForm;
import com.danielbyrne.daftsearch.domain.model.PropertyForSaleDTO;
import com.danielbyrne.daftsearch.services.PropertyForSaleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.Set;

@RequestMapping("properties/sales")
@Controller
public class PropertyForSaleController {

    private final PropertyForSaleService propertyForSaleService;

    public PropertyForSaleController(PropertyForSaleService propertyForSaleService) {
        this.propertyForSaleService = propertyForSaleService;
    }

    @GetMapping("/find")
    public String showForm(SaleForm saleForm) {
        return "property/sales/searchform";
    }

    @GetMapping
    public String processSearchForm(@Valid SaleForm saleForm, BindingResult result, Model model) {

        if (result.hasErrors()) {
            return "property/sales/searchform";
        }

        int maxPrice = saleForm.getMaxPrice();
        int minBeds = saleForm.getMinBeds();
        County[] counties = saleForm.getCounties();

        int distance = saleForm.getDistanceInKms();
        String location = saleForm.getLocation();
        TravelMode modeOfTransport = saleForm.getTravelMode();

        Set<PropertyForSaleDTO> filteredProperties = propertyForSaleService.filterProperties(maxPrice, minBeds, counties);

        model.addAttribute("propertiesforsale", filteredProperties);
        return "property/sales";
//        return "redirect:/properties/sales/search/" + maxPrice + "/" + minBeds;
    }

    @GetMapping("/search/{maxPrice}/{minBeds}")
    public String filterSales(@PathVariable String maxPrice, @PathVariable String minBeds, Model model) {
//        model.addAttribute("propertiesforsale",
//                propertyForSaleService.filterProperties(Integer.parseInt(maxPrice), Integer.parseInt(minBeds)));
        return "property/sales";
    }

    @GetMapping("/all")
    public String getAllProperties(Model model) {
        model.addAttribute("propertiesforsale", propertyForSaleService.getAllProperties());
        return "property/sales";
    }
}