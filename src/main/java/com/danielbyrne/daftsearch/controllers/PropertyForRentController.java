package com.danielbyrne.daftsearch.controllers;

import com.danielbyrne.daftsearch.domain.forms.LettingForm;
import com.danielbyrne.daftsearch.domain.model.PropertyForRentDTO;
import com.danielbyrne.daftsearch.services.PropertyForRentService;
import com.google.maps.errors.ApiException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Set;

@RequestMapping(PropertyForRentController.BASE_URL)
@Controller
public class PropertyForRentController {

    public static final String BASE_URL = "/lettings";

    private final PropertyForRentService propertyForRentService;

    public PropertyForRentController(PropertyForRentService propertyForRentService) {
        this.propertyForRentService = propertyForRentService;
    }

    @GetMapping("/find")
    public String showForm(LettingForm lettingForm) {
        return "property/lettings/searchform";
    }

    @GetMapping
    public String processSearchForm(@Valid LettingForm lettingForm, BindingResult bindingResult, Model model)
            throws InterruptedException, ApiException, IOException {

        if (bindingResult.hasErrors()) return BASE_URL + "/find";

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
