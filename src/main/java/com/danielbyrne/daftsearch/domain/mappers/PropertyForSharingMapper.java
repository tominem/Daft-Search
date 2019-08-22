package com.danielbyrne.daftsearch.domain.mappers;

import com.danielbyrne.daftsearch.domain.PropertyForSharing;
import com.danielbyrne.daftsearch.domain.model.PropertyForSharingDTO;
import org.mapstruct.factory.Mappers;

public interface PropertyForSharingMapper {

    PropertyForSharingMapper INSTANCE = Mappers.getMapper(PropertyForSharingMapper.class);

    PropertyForSharingDTO propertyForSharingToPropertyForSharingDTO(PropertyForSharing propertyForSharing);

    PropertyForSharing propertyForSharingDTOtoPropertyForSharing(PropertyForSharingDTO propertyForSharingDTO);
}