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
    private static final String SEARCH_FORM = "property/sharing/searchform";

    private final PropertyForSharingService propertyForSharingService;

    public PropertyForSharingController(PropertyForSharingService propertyForSharingService) {
        this.propertyForSharingService = propertyForSharingService;
    }

    @GetMapping("/find")
    public String showForm(SharingForm sharingForm) {
        // setting minBeds as it will fail NotNull validation otherwise,
        // this field is not used to filter shared properties so won't have any knock on effect
        sharingForm.setMinBeds(1);

        log.debug("Showing search form");
        return SEARCH_FORM;
    }

    @GetMapping
    public String processSearchForm(@Valid SharingForm sharingForm, BindingResult bindingResult, Model model) throws InterruptedException, ApiException, IOException {

        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(objectError -> log.debug(objectError.toString()));
            return SEARCH_FORM;
        }

        sharingForm.setRoomPreferences();

        Set<PropertyForSharingDTO> filteredProperties = propertyForSharingService.filterPropertiesByDaftAttributes(sharingForm);
        log.debug("Received {} properties via daft attributes.", filteredProperties.size());


        if(filteredProperties.size() == 0) return "property/noresults";

        Set<PropertyForSharingDTO> result = propertyForSharingService.filterPropertiesByGoogle(filteredProperties, sharingForm);
        log.debug("Received {} properties via Google.", result.size());

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