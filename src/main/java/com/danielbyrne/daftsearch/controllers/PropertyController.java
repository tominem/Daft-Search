package com.danielbyrne.daftsearch.controllers;

import com.danielbyrne.daftsearch.services.PropertyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PropertyController {

    private final PropertyService propertyService;

    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @RequestMapping("properties")
    public String getAllProperties(Model model) {
        model.addAttribute("properties", propertyService.getAllProperties());
        return "property/property";
    }
}
