package com.danielbyrne.daftsearch.controllers;

import com.danielbyrne.daftsearch.domain.forms.SaleForm;
import com.danielbyrne.daftsearch.services.PropertyForSaleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("properties/sales")
@Controller
public class PropertyForSaleController {

    private final PropertyForSaleService propertyForSaleService;

    public PropertyForSaleController(PropertyForSaleService propertyForSaleService) {
        this.propertyForSaleService = propertyForSaleService;
    }

    @GetMapping("/find")
    public String searchProperties(Model model) {
        model.addAttribute("propertyForm", new SaleForm());
        return "property/sales/searchform";
    }

    @GetMapping
    public String testForm(SaleForm property, BindingResult result, Model model) {
        // todo handle errors and do relevant checks
        int maxPrice = property.getMaxPrice();
        int minBeds = property.getMinBeds();
        return "redirect:/properties/sales/search/" + property.getMaxPrice() + "/" + property.getMinBeds();
    }

    @GetMapping("/search/{maxPrice}/{minBeds}")
    public String filterSales(@PathVariable String maxPrice, @PathVariable String minBeds, Model model) {
        model.addAttribute("propertiesforsale",
                propertyForSaleService.filterProperties(Integer.parseInt(maxPrice), Integer.parseInt(minBeds)));
        return "property/sales";
    }

    @GetMapping("/all")
    public String getAllProperties(Model model) {
        model.addAttribute("propertiesforsale", propertyForSaleService.getAllProperties());
        return "property/sales";
    }
}