package com.danielbyrne.daftsearch.controllers;

import com.danielbyrne.daftsearch.domain.forms.LettingForm;
import com.danielbyrne.daftsearch.domain.model.PropertyForRentDTO;
import com.danielbyrne.daftsearch.services.PropertyForRentService;
import com.google.maps.errors.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Set;

@Slf4j
@RequestMapping(PropertyForRentController.BASE_URL)
@Controller
public class PropertyForRentController {

    public static final String BASE_URL = "/lettings";
    private static final String SEARCH_FORM = "property/lettings/searchform_v1";

    private final PropertyForRentService propertyForRentService;

    public PropertyForRentController(PropertyForRentService propertyForRentService) {
        this.propertyForRentService = propertyForRentService;
    }

    @GetMapping("/find")
    public String showForm(LettingForm lettingForm) {
        return SEARCH_FORM;
    }

    @GetMapping
    public String processSearchForm(@Valid @ModelAttribute("lettingForm") LettingForm lettingForm, BindingResult bindingResult, Model model)
            throws InterruptedException, ApiException, IOException {

        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(objectError -> {
                log.debug(objectError.toString());
            });
            return SEARCH_FORM;
        }

        Set<PropertyForRentDTO> filteredProperties = propertyForRentService.filterPropertiesByDaftAttributes(lettingForm);
        if (filteredProperties.size()==0) return "property/noresults";

        Set<PropertyForRentDTO> result = propertyForRentService.filterPropertiesByGoogle(filteredProperties, lettingForm);
        if (result.size()==0) return "property/noresults";

        model.addAttribute("propertiesForRent", result);

        return "property/lettings";
    }

    @RequestMapping("/all")
    public String getAllProperties(Model model) {
        model.addAttribute("propertiesForRent", propertyForRentService.getAllProperties());
        return "property/lettings";
    }
}
