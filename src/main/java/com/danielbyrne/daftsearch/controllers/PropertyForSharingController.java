package com.danielbyrne.daftsearch.controllers;

import com.danielbyrne.daftsearch.services.PropertyForSharingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PropertyForSharingController {

    private final PropertyForSharingService propertyForSharingService;

    public PropertyForSharingController(PropertyForSharingService propertyForSharingService) {
        this.propertyForSharingService = propertyForSharingService;
    }

    @RequestMapping("properties/sharing")
    public String getAllProperties(Model model) {
        model.addAttribute("propertiesToShare", propertyForSharingService.getAllProperties());
        return "property/sharing";
    }
}