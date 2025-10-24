package com.cipasuno.petstore.pet_store.services;

import com.cipasuno.petstore.pet_store.models.Tenant;
import com.cipasuno.petstore.pet_store.models.DTOs.TenantCreateDto;
import com.cipasuno.petstore.pet_store.models.DTOs.TenantResponseDto;
import com.cipasuno.petstore.pet_store.repositories.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TenantService {

    @Autowired
    private TenantRepository tenantRepository;

    @Transactional
    public TenantResponseDto createTenant(TenantCreateDto tenantDto) {
        Tenant tenant = new Tenant();
        tenant.setNit(tenantDto.getNit());
        tenant.setRazonSocial(tenantDto.getRazonSocial());
        tenant.setDireccion(tenantDto.getDireccion());
        tenant.setTelefono(tenantDto.getTelefono());
        tenant.setEmail(tenantDto.getEmail());
        tenant.setPlan(tenantDto.getPlan() != null ? tenantDto.getPlan() : "BASIC");
        tenant.setFechaVencimiento(tenantDto.getFechaVencimiento());
        tenant.setConfiguracion(tenantDto.getConfiguracion());

        Tenant savedTenant = tenantRepository.save(tenant);
        return mapToResponseDto(savedTenant);
    }

    public List<TenantResponseDto> getAllTenants() {
        return tenantRepository.findAll()
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public List<TenantResponseDto> getActiveTenants() {
        return tenantRepository.findByActivoTrue()
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public Optional<TenantResponseDto> getTenantById(Integer tenantId) {
        return tenantRepository.findById(tenantId)
                .map(this::mapToResponseDto);
    }

    public Optional<TenantResponseDto> getTenantByNit(String nit) {
        return tenantRepository.findByNit(nit)
                .map(this::mapToResponseDto);
    }



    public List<TenantResponseDto> getTenantsByPlan(String plan) {
        return tenantRepository.findByPlan(plan)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public TenantResponseDto updateTenant(Integer tenantId, Tenant tenantDetails) {
        Tenant tenant = tenantRepository.findById(tenantId)
            .orElseThrow(() -> new RuntimeException("Tenant no encontrado con ID: " + tenantId));


        if (tenantDetails.getNit() != null) {
            tenant.setNit(tenantDetails.getNit());
        }
        if (tenantDetails.getRazonSocial() != null) {
            tenant.setRazonSocial(tenantDetails.getRazonSocial());
        }
        if (tenantDetails.getDireccion() != null) {
            tenant.setDireccion(tenantDetails.getDireccion());
        }
        if (tenantDetails.getTelefono() != null) {
            tenant.setTelefono(tenantDetails.getTelefono());
        }
        if (tenantDetails.getEmail() != null) {
            tenant.setEmail(tenantDetails.getEmail());
        }
        if (tenantDetails.getPlan() != null) {
            tenant.setPlan(tenantDetails.getPlan());
        }
        if (tenantDetails.getFechaVencimiento() != null) {
            tenant.setFechaVencimiento(tenantDetails.getFechaVencimiento());
        }
        if (tenantDetails.getConfiguracion() != null) {
            tenant.setConfiguracion(tenantDetails.getConfiguracion());
        }
        if (tenantDetails.getActivo() != null) {
            tenant.setActivo(tenantDetails.getActivo());
        }

        Tenant updatedTenant = tenantRepository.save(tenant);
        return mapToResponseDto(updatedTenant);
    }

    @Transactional
    public void deactivateTenant(Integer tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
            .orElseThrow(() -> new RuntimeException("Tenant no encontrado con ID: " + tenantId));
        
        tenant.setActivo(false);
        tenantRepository.save(tenant);
    }

    @Transactional
    public TenantResponseDto activateTenant(Integer tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
            .orElseThrow(() -> new RuntimeException("Tenant no encontrado con ID: " + tenantId));
        
        tenant.setActivo(true);
        Tenant savedTenant = tenantRepository.save(tenant);
        return mapToResponseDto(savedTenant);
    }

    public long countActiveTenants() {
        return tenantRepository.countActiveTenants();
    }

    public boolean existsByTenantId(Integer tenantId) {
        return tenantRepository.existsById(tenantId);
    }

    public boolean existsByNit(String nit) {
        return tenantRepository.findByNit(nit).isPresent();
    }

    private TenantResponseDto mapToResponseDto(Tenant tenant) {
        TenantResponseDto dto = new TenantResponseDto();
        dto.setTenantId(tenant.getTenantId());
        dto.setNit(tenant.getNit());
        dto.setRazonSocial(tenant.getRazonSocial());
        dto.setDireccion(tenant.getDireccion());
        dto.setTelefono(tenant.getTelefono());
        dto.setEmail(tenant.getEmail());
        dto.setPlan(tenant.getPlan());
        dto.setActivo(tenant.getActivo());
        dto.setFechaRegistro(tenant.getFechaRegistro());
        dto.setFechaVencimiento(tenant.getFechaVencimiento());
        dto.setConfiguracion(tenant.getConfiguracion());
        return dto;
    }
}

