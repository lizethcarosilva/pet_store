package com.cipasuno.petstore.pet_store.services;

import com.cipasuno.petstore.pet_store.models.Client;
import com.cipasuno.petstore.pet_store.models.DTOs.ClientCreateDto;
import com.cipasuno.petstore.pet_store.models.DTOs.ClientResponseDto;
import com.cipasuno.petstore.pet_store.models.DTOs.UpdateClientRequest;
import com.cipasuno.petstore.pet_store.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar clientes
 * Los clientes tienen su propia tabla separada de usuarios
 */
@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    /**
     * Crear un nuevo cliente
     */
    @Transactional
    public ClientResponseDto createClient(ClientCreateDto clientDto, String tenantId) {
        // Validar que no exista con la misma identificación
        if (clientRepository.existsByIdent(clientDto.getIdent())) {
            throw new RuntimeException("Ya existe un cliente con la identificación: " + clientDto.getIdent());
        }
        
        // Validar que no exista con el mismo correo
        if (clientRepository.existsByCorreo(clientDto.getCorreo())) {
            throw new RuntimeException("Ya existe un cliente con el correo: " + clientDto.getCorreo());
        }
        
        Client client = new Client();
        client.setTenantId(tenantId);
        client.setName(clientDto.getName());
        client.setTipoId(clientDto.getTipoId());
        client.setIdent(clientDto.getIdent());
        client.setCorreo(clientDto.getCorreo());
        client.setTelefono(clientDto.getTelefono());
        client.setDireccion(clientDto.getDireccion());
        client.setObservaciones(clientDto.getObservaciones());
        
        Client savedClient = clientRepository.save(client);
        return mapToResponseDto(savedClient);
    }

    /**
     * Obtener todos los clientes
     */
    public List<ClientResponseDto> getAllClients() {
        return clientRepository.findAll()
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Obtener todos los clientes por tenant
     */
    public List<ClientResponseDto> getAllClientsByTenant(String tenantId) {
        return clientRepository.findByTenantId(tenantId)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Obtener clientes activos
     */
    public List<ClientResponseDto> getActiveClients() {
        return clientRepository.findAllActive()
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Obtener clientes activos por tenant
     */
    public List<ClientResponseDto> getActiveClientsByTenant(String tenantId) {
        return clientRepository.findActiveByTenantId(tenantId)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Obtener cliente por ID
     */
    public Optional<ClientResponseDto> getClientById(Integer clientId) {
        return clientRepository.findById(clientId)
                .map(this::mapToResponseDto);
    }

    /**
     * Buscar clientes por nombre
     */
    public List<ClientResponseDto> searchClientsByName(String name) {
        return clientRepository.searchByName(name)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Buscar cliente por identificación
     */
    public Optional<ClientResponseDto> getClientByIdent(String ident) {
        return clientRepository.findByIdent(ident)
                .map(this::mapToResponseDto);
    }

    /**
     * Buscar cliente por correo
     */
    public Optional<ClientResponseDto> getClientByCorreo(String correo) {
        return clientRepository.findByCorreo(correo)
                .map(this::mapToResponseDto);
    }

    /**
     * Actualizar información de un cliente
     */
    @Transactional
    public ClientResponseDto updateClient(UpdateClientRequest clientDetails, String tenantId) {
        Client client = clientRepository.findById(clientDetails.getClientId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + clientDetails.getClientId()));

        // Validar tenant
        if (!client.getTenantId().equals(tenantId)) {
            throw new RuntimeException("El cliente no pertenece a este tenant");
        }

        // Actualizar campos
        if (clientDetails.getName() != null) {
            client.setName(clientDetails.getName());
        }
        if (clientDetails.getTipoId() != null) {
            client.setTipoId(clientDetails.getTipoId());
        }
        if (clientDetails.getIdent() != null && !clientDetails.getIdent().equals(client.getIdent())) {
            // Validar que no exista otro cliente con esta identificación
            if (clientRepository.existsByIdent(clientDetails.getIdent())) {
                throw new RuntimeException("Ya existe un cliente con la identificación: " + clientDetails.getIdent());
            }
            client.setIdent(clientDetails.getIdent());
        }
        if (clientDetails.getCorreo() != null && !clientDetails.getCorreo().equals(client.getCorreo())) {
            // Validar que no exista otro cliente con este correo
            if (clientRepository.existsByCorreo(clientDetails.getCorreo())) {
                throw new RuntimeException("Ya existe un cliente con el correo: " + clientDetails.getCorreo());
            }
            client.setCorreo(clientDetails.getCorreo());
        }
        if (clientDetails.getTelefono() != null) {
            client.setTelefono(clientDetails.getTelefono());
        }
        if (clientDetails.getDireccion() != null) {
            client.setDireccion(clientDetails.getDireccion());
        }
        if (clientDetails.getObservaciones() != null) {
            client.setObservaciones(clientDetails.getObservaciones());
        }
        if (clientDetails.getActivo() != null) {
            client.setActivo(clientDetails.getActivo());
        }

        Client updatedClient = clientRepository.save(client);
        return mapToResponseDto(updatedClient);
    }

    /**
     * Eliminar cliente (soft delete)
     */
    @Transactional
    public void deleteClient(Integer clientId, String tenantId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + clientId));

        // Validar tenant
        if (!client.getTenantId().equals(tenantId)) {
            throw new RuntimeException("El cliente no pertenece a este tenant");
        }

        client.setActivo(false);
        clientRepository.save(client);
    }

    /**
     * Eliminar cliente permanentemente
     */
    @Transactional
    public void deleteClientPermanently(Integer clientId, String tenantId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + clientId));

        // Validar tenant
        if (!client.getTenantId().equals(tenantId)) {
            throw new RuntimeException("El cliente no pertenece a este tenant");
        }

        clientRepository.delete(client);
    }

    /**
     * Contar clientes activos por tenant
     */
    public long countActiveClientsByTenant(String tenantId) {
        return clientRepository.countActiveByTenantId(tenantId);
    }

    /**
     * Mapear entidad Client a ClientResponseDto
     */
    private ClientResponseDto mapToResponseDto(Client client) {
        ClientResponseDto dto = new ClientResponseDto();
        dto.setClientId(client.getClientId());
        dto.setName(client.getName());
        dto.setTipoId(client.getTipoId());
        dto.setIdent(client.getIdent());
        dto.setCorreo(client.getCorreo());
        dto.setTelefono(client.getTelefono());
        dto.setDireccion(client.getDireccion());
        dto.setObservaciones(client.getObservaciones());
        dto.setCreatedOn(client.getCreatedOn());
        dto.setActivo(client.getActivo());
        dto.setTenantId(client.getTenantId());
        return dto;
    }
}
