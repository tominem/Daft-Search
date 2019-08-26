package com.danielbyrne.daftsearch.controllers;

import com.danielbyrne.daftsearch.domain.forms.LettingForm;
import com.danielbyrne.daftsearch.domain.model.PropertyForRentDTO;
import com.danielbyrne.daftsearch.services.PropertyForRentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
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
    public String processSearchForm(@Valid LettingForm lettingForm, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) return BASE_URL + "/find";

        Set<PropertyForRentDTO> filteredProperties = propertyForRentService.filterPropertiesByDaftAttributes(lettingForm);
        Set<PropertyForRentDTO> result;


        return "property/sales";
    }

    @RequestMapping("/all")
    public String getAllProperties(Model model) {
        model.addAttribute("propertiesForRent", propertyForRentService.getAllProperties());
        return "property/lettings";
    }
}
