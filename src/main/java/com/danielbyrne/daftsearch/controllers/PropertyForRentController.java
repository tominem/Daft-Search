package com.danielbyrne.daftsearch.controllers;

import com.danielbyrne.daftsearch.services.PropertyForRentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(PropertyForRentController.BASE_URL)
@Controller
public class PropertyForRentController {

    public static final String BASE_URL = "/lettings";

    private final PropertyForRentService propertyForRentService;

    public PropertyForRentController(PropertyForRentService propertyForRentService) {
        this.propertyForRentService = propertyForRentService;
    }

    @RequestMapping("/all")
    public String getAllProperties(Model model) {
        model.addAttribute("propertiesForRent", propertyForRentService.getAllProperties());
        return "property/lettings";
    }
}
