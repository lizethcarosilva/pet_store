package com.cipasuno.petstore.pet_store.services;

import com.cipasuno.petstore.pet_store.models.Service;
import com.cipasuno.petstore.pet_store.models.DTOs.ServiceCreateDto;
import com.cipasuno.petstore.pet_store.models.DTOs.ServiceResponseDto;
import com.cipasuno.petstore.pet_store.repositories.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class VeterinaryService {

    @Autowired
    private ServiceRepository serviceRepository;

    @Transactional
    public ServiceResponseDto createService(ServiceCreateDto serviceDto, Integer tenantId) {
        Service service = new Service();
        service.setTenantId(tenantId);
        service.setCodigo(serviceDto.getCodigo());
        service.setNombre(serviceDto.getNombre());
        service.setDescripcion(serviceDto.getDescripcion());
        service.setPrecio(serviceDto.getPrecio());
        service.setDuracionMinutos(serviceDto.getDuracionMinutos());

        Service savedService = serviceRepository.save(service);
        return mapToResponseDto(savedService);
    }

    public List<ServiceResponseDto> getAllServices() {
        return serviceRepository.findAll()
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public List<ServiceResponseDto> getActiveServices() {
        return serviceRepository.findByActivoTrue()
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public Optional<ServiceResponseDto> getServiceById(Integer id) {
        return serviceRepository.findById(id)
                .map(this::mapToResponseDto);
    }

    public Optional<ServiceResponseDto> getServiceByCodigo(String codigo) {
        return serviceRepository.findByCodigo(codigo)
                .map(this::mapToResponseDto);
    }

    public List<ServiceResponseDto> searchServicesByName(String nombre) {
        return serviceRepository.findByNombreContainingIgnoreCase(nombre)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ServiceResponseDto updateService(Integer id, Service serviceDetails) {
        Service service = serviceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Servicio no encontrado con ID: " + id));

        if (serviceDetails.getCodigo() != null) {
            service.setCodigo(serviceDetails.getCodigo());
        }
        if (serviceDetails.getNombre() != null) {
            service.setNombre(serviceDetails.getNombre());
        }
        if (serviceDetails.getDescripcion() != null) {
            service.setDescripcion(serviceDetails.getDescripcion());
        }
        if (serviceDetails.getPrecio() != null) {
            service.setPrecio(serviceDetails.getPrecio());
        }
        if (serviceDetails.getDuracionMinutos() != null) {
            service.setDuracionMinutos(serviceDetails.getDuracionMinutos());
        }
        if (serviceDetails.getActivo() != null) {
            service.setActivo(serviceDetails.getActivo());
        }

        Service updatedService = serviceRepository.save(service);
        return mapToResponseDto(updatedService);
    }

    @Transactional
    public void deleteService(Integer id) {
        Service service = serviceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Servicio no encontrado con ID: " + id));
        
        service.setActivo(false);
        serviceRepository.save(service);
    }

    @Transactional
    public void deleteServicePermanently(Integer id) {
        Service service = serviceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Servicio no encontrado con ID: " + id));
        
        serviceRepository.delete(service);
    }

    public long countActiveServices() {
        return serviceRepository.countActiveServices();
    }

    private ServiceResponseDto mapToResponseDto(Service service) {
        ServiceResponseDto dto = new ServiceResponseDto();
        dto.setServiceId(service.getServiceId());
        dto.setCodigo(service.getCodigo());
        dto.setNombre(service.getNombre());
        dto.setDescripcion(service.getDescripcion());
        dto.setPrecio(service.getPrecio());
        dto.setDuracionMinutos(service.getDuracionMinutos());
        dto.setActivo(service.getActivo());
        dto.setCreatedOn(service.getCreatedOn());
        return dto;
    }
}

