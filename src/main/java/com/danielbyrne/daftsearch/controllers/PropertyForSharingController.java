package com.danielbyrne.daftsearch.controllers;

import com.danielbyrne.daftsearch.domain.forms.SharingForm;
import com.danielbyrne.daftsearch.domain.model.PropertyForSharingDTO;
import com.danielbyrne.daftsearch.services.PropertyForSharingService;
import com.google.maps.errors.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Set;

@Slf4j
@RequestMapping(PropertyForSharingController.BASE_URL)
@Controller
public class PropertyForSharingController {

    public static final String BASE_URL = "/sharing";
    private static final String SEARCH_FORM = "property/sharing/searchform_v2";

    private final PropertyForSharingService propertyForSharingService;

    public PropertyForSharingController(PropertyForSharingService propertyForSharingService) {
        this.propertyForSharingService = propertyForSharingService;
    }

    @GetMapping("/find")
    public String showForm(SharingForm sharingForm) {
        sharingForm.setMinBeds(1);
        return SEARCH_FORM;
    }

    @GetMapping
    public String processSearchForm(@Valid SharingForm sharingForm, BindingResult bindingResult, Model model) throws InterruptedException, ApiException, IOException {

        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(objectError -> {
                log.debug(objectError.toString());
            });
            return SEARCH_FORM;
        }

        sharingForm.setRoomPreferences();

        Set<PropertyForSharingDTO> filteredProperties = propertyForSharingService.filterPropertiesByDaftAttributes(sharingForm);

        if(filteredProperties.size() == 0) return "property/noresults";

        Set<PropertyForSharingDTO> result = propertyForSharingService.filterPropertiesByGoogle(filteredProperties, sharingForm);

        if (result.size()==0) return "property/noresults";

        model.addAttribute("propertiesToShare", result);
        return "property/sharing";
    }

    @RequestMapping("/all")
    public String getAllProperties(Model model) {
        model.addAttribute("propertiesToShare", propertyForSharingService.getAllProperties());
        return "property/sharing";
    }
}