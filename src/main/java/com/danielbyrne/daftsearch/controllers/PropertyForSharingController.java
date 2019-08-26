package com.danielbyrne.daftsearch.controllers;

import com.danielbyrne.daftsearch.domain.forms.SharingForm;
import com.danielbyrne.daftsearch.services.PropertyForSharingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(PropertyForSharingController.BASE_URL)
@Controller
public class PropertyForSharingController {

    public static final String BASE_URL = "/sharing";
    private final PropertyForSharingService propertyForSharingService;

    public PropertyForSharingController(PropertyForSharingService propertyForSharingService) {
        this.propertyForSharingService = propertyForSharingService;
    }

    @GetMapping("/find")
    public String showForm(SharingForm sharingForm) {
        return "property/sharing/searchform";
    }

    @RequestMapping("/all")
    public String getAllProperties(Model model) {
        model.addAttribute("propertiesToShare", propertyForSharingService.getAllProperties());
        return "property/sharing";
    }
}