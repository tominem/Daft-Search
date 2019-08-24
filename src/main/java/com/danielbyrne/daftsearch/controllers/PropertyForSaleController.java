package com.danielbyrne.daftsearch.controllers;

import com.danielbyrne.daftsearch.domain.County;
import com.danielbyrne.daftsearch.domain.ModeOfTransport;
import com.danielbyrne.daftsearch.domain.forms.SaleForm;
import com.danielbyrne.daftsearch.domain.model.PropertyForSaleDTO;
import com.danielbyrne.daftsearch.services.GoogleMapServices;
import com.danielbyrne.daftsearch.services.PropertyForSaleService;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixElement;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@RequestMapping("properties/sales")
@Controller
public class PropertyForSaleController {

    private final PropertyForSaleService propertyForSaleService;
    private final GoogleMapServices googleMapServices;

    public PropertyForSaleController(PropertyForSaleService propertyForSaleService,
                                     GoogleMapServices googleMapServices) {
        this.propertyForSaleService = propertyForSaleService;
        this.googleMapServices = googleMapServices;
    }

    @GetMapping("/find")
    public String showForm(SaleForm saleForm) {
        return "property/sales/searchform";
    }

    @GetMapping
    public String processSearchForm(@Valid SaleForm saleForm, BindingResult bindingResult, Model model) throws InterruptedException, ApiException, IOException {

        if (bindingResult.hasErrors()) {
            return "property/sales/searchform";
        }

        int maxPrice = saleForm.getMaxPrice();
        int minBeds = saleForm.getMinBeds();
        County[] counties = saleForm.getCounties();

        int distance = saleForm.getDistanceInKms();
        int duration = saleForm.getCommuteInMinutes();
        String location = saleForm.getLocation();
        ModeOfTransport modeOfTransport = saleForm.getModeOfTransport();

        Set<PropertyForSaleDTO> filteredProperties = propertyForSaleService.filterProperties(maxPrice, minBeds, counties);

        Set<PropertyForSaleDTO> result = new HashSet<>();

        DistanceMatrix distanceMatrix;
        DistanceMatrixElement matrixElement;
        for (PropertyForSaleDTO dto : filteredProperties) {

            distanceMatrix = googleMapServices.getDistanceMatrix(location, dto.getAddress(), modeOfTransport);
            matrixElement = distanceMatrix.rows[0].elements[0];

            if (matrixElement != null) {
                dto.setReadableDistance(matrixElement.distance.humanReadable);
                dto.setReadableDuration(matrixElement.duration.humanReadable);
                dto.setDistanceKm(matrixElement.distance.inMeters/1000);
                dto.setDuranceMin(matrixElement.duration.inSeconds/60);

                if (dto.getDistanceKm() <= distance || dto.getDuranceMin() <= duration) {
                    result.add(dto);
                }
            }
        }
        model.addAttribute("propertiesforsale", result);
        return "property/sales";
    }

    @GetMapping("/all")
    public String getAllProperties(Model model) {
        model.addAttribute("propertiesforsale", propertyForSaleService.getAllProperties());
        return "property/sales";
    }
}