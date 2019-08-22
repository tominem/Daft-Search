package com.danielbyrne.daftsearch.controllers;

import com.danielbyrne.daftsearch.services.PropertyForSaleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PropertyForSaleController {

    private final PropertyForSaleService propertyForSaleService;

    public PropertyForSaleController(PropertyForSaleService propertyForSaleService) {
        this.propertyForSaleService = propertyForSaleService;
    }

    @RequestMapping("properties/sales")
    public String getAllProperties(Model model) {
        model.addAttribute("propertiesforsale", propertyForSaleService.getAllProperties());
        return "property/sales";
    }

    @GetMapping("properties/sales/{maxPrice}/{minBed}")
    public String filterSales(@PathVariable String maxPrice, @PathVariable String minBed, Model model) {
        model.addAttribute("propertiesforsale",
                propertyForSaleService.filterProperties(Integer.parseInt(maxPrice), Integer.parseInt(minBed)));
        return "property/sales";
    }
}