package com.danielbyrne.daftsearch.controllers;

import com.danielbyrne.daftsearch.domain.forms.SharingForm;
import com.danielbyrne.daftsearch.domain.model.PropertyForSharingDTO;
import com.danielbyrne.daftsearch.services.PropertyForSharingService;
import com.google.maps.errors.ApiException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Set;

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

    @GetMapping
    public String processSearchForm(@Valid SharingForm sharingForm, BindingResult bindingResult, Model model) throws InterruptedException, ApiException, IOException {

        if (bindingResult.hasErrors()) return BASE_URL + "/searchform";

        sharingForm.setRoomPreferences();

        Set<PropertyForSharingDTO> filteredProperties = propertyForSharingService.filterPropertiesByDaftAttributes(sharingForm);
        Set<PropertyForSharingDTO> result = propertyForSharingService.filterPropertiesByGoogle(filteredProperties, sharingForm);
        model.addAttribute("propertiesToShare", result);
        return "property/sharing";
    }

    @RequestMapping("/all")
    public String getAllProperties(Model model) {
        model.addAttribute("propertiesToShare", propertyForSharingService.getAllProperties());
        return "property/sharing";
    }
}