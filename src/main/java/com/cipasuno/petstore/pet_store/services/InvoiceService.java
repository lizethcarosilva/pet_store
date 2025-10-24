package com.cipasuno.petstore.pet_store.services;

import com.cipasuno.petstore.pet_store.models.Invoice;
import com.cipasuno.petstore.pet_store.models.InvoiceDetail;
import com.cipasuno.petstore.pet_store.models.Product;
import com.cipasuno.petstore.pet_store.models.Service;
import com.cipasuno.petstore.pet_store.models.User;
import com.cipasuno.petstore.pet_store.models.DTOs.InvoiceCreateDto;
import com.cipasuno.petstore.pet_store.models.DTOs.InvoiceDetailDto;
import com.cipasuno.petstore.pet_store.models.DTOs.InvoiceResponseDto;
import com.cipasuno.petstore.pet_store.repositories.InvoiceDetailRepository;
import com.cipasuno.petstore.pet_store.repositories.InvoiceRepository;
import com.cipasuno.petstore.pet_store.repositories.ProductRepository;
import com.cipasuno.petstore.pet_store.repositories.ServiceRepository;
import com.cipasuno.petstore.pet_store.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private InvoiceDetailRepository invoiceDetailRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Transactional
    public InvoiceResponseDto createInvoice(InvoiceCreateDto invoiceDto) {
        Invoice invoice = new Invoice();
        
        User client = userRepository.findById(invoiceDto.getClientId())
            .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + invoiceDto.getClientId()));
        
        invoice.setClient(client);
        
        if (invoiceDto.getEmployeeId() != null) {
            User employee = userRepository.findById(invoiceDto.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado con ID: " + invoiceDto.getEmployeeId()));
            invoice.setEmployee(employee);
        }
        
        // Generar número de factura
        invoice.setNumero(generateInvoiceNumber());
        invoice.setObservaciones(invoiceDto.getObservaciones());
        
        // Calcular totales
        BigDecimal subtotal = BigDecimal.ZERO;
        
        // Guardar la factura primero
        Invoice savedInvoice = invoiceRepository.save(invoice);
        
        // Procesar detalles
        if (invoiceDto.getDetails() != null && !invoiceDto.getDetails().isEmpty()) {
            for (InvoiceDetailDto detailDto : invoiceDto.getDetails()) {
                InvoiceDetail detail = new InvoiceDetail();
                detail.setInvoice(savedInvoice);
                detail.setTipo(detailDto.getTipo());
                detail.setCantidad(detailDto.getCantidad());
                detail.setDescuento(detailDto.getDescuento() != null ? detailDto.getDescuento() : BigDecimal.ZERO);
                
                if ("PRODUCTO".equals(detailDto.getTipo())) {
                    Product product = productRepository.findById(detailDto.getProductId())
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + detailDto.getProductId()));
                    
                    // Verificar stock
                    if (product.getStock() < detailDto.getCantidad()) {
                        throw new RuntimeException("Stock insuficiente para el producto: " + product.getNombre());
                    }
                    
                    detail.setProduct(product);
                    detail.setPrecioUnitario(product.getPrecio());
                    
                    // Reducir stock
                    product.setStock(product.getStock() - detailDto.getCantidad());
                    productRepository.save(product);
                    
                } else if ("SERVICIO".equals(detailDto.getTipo())) {
                    Service service = serviceRepository.findById(detailDto.getServiceId())
                        .orElseThrow(() -> new RuntimeException("Servicio no encontrado con ID: " + detailDto.getServiceId()));
                    
                    detail.setService(service);
                    detail.setPrecioUnitario(service.getPrecio());
                }
                
                // Calcular subtotal del detalle
                BigDecimal precioTotal = detail.getPrecioUnitario().multiply(BigDecimal.valueOf(detail.getCantidad()));
                BigDecimal subtotalDetalle = precioTotal.subtract(detail.getDescuento());
                detail.setSubtotal(subtotalDetalle);
                
                invoiceDetailRepository.save(detail);
                subtotal = subtotal.add(subtotalDetalle);
            }
        }
        
        // Actualizar totales de la factura
        savedInvoice.setSubtotal(subtotal);
        savedInvoice.setDescuento(invoiceDto.getDescuento() != null ? invoiceDto.getDescuento() : BigDecimal.ZERO);
        savedInvoice.setImpuesto(invoiceDto.getImpuesto() != null ? invoiceDto.getImpuesto() : BigDecimal.ZERO);
        
        BigDecimal total = subtotal.subtract(savedInvoice.getDescuento()).add(savedInvoice.getImpuesto());
        savedInvoice.setTotal(total);
        
        Invoice finalInvoice = invoiceRepository.save(savedInvoice);
        return mapToResponseDto(finalInvoice);
    }

    public List<InvoiceResponseDto> getAllInvoices() {
        return invoiceRepository.findAll()
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public List<InvoiceResponseDto> getActiveInvoices() {
        return invoiceRepository.findByActivoTrue()
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public Optional<InvoiceResponseDto> getInvoiceById(Integer id) {
        return invoiceRepository.findById(id)
                .map(this::mapToResponseDto);
    }

    public Optional<InvoiceResponseDto> getInvoiceByNumero(String numero) {
        return invoiceRepository.findByNumero(numero)
                .map(this::mapToResponseDto);
    }

    public List<InvoiceResponseDto> getInvoicesByClientId(Integer clientId) {
        return invoiceRepository.findByClientId(clientId)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public List<InvoiceResponseDto> getInvoicesByEstado(String estado) {
        return invoiceRepository.findByEstado(estado)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public List<InvoiceResponseDto> getInvoicesByDateRange(LocalDateTime inicio, LocalDateTime fin) {
        return invoiceRepository.findByFechaEmisionBetween(inicio, fin)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public BigDecimal getTotalSalesToday() {
        return invoiceRepository.getTotalSalesToday();
    }

    public BigDecimal getTotalSalesThisMonth() {
        return invoiceRepository.getTotalSalesThisMonth();
    }

    public long countInvoicesToday() {
        return invoiceRepository.countInvoicesToday();
    }

    @Transactional
    public InvoiceResponseDto updateInvoiceStatus(Integer id, String estado) {
        Invoice invoice = invoiceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Factura no encontrada con ID: " + id));
        
        invoice.setEstado(estado);
        
        Invoice updatedInvoice = invoiceRepository.save(invoice);
        return mapToResponseDto(updatedInvoice);
    }

    @Transactional
    public void cancelInvoice(Integer id) {
        Invoice invoice = invoiceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Factura no encontrada con ID: " + id));
        
        // Devolver stock de productos
        List<InvoiceDetail> details = invoiceDetailRepository.findByInvoiceId(id);
        for (InvoiceDetail detail : details) {
            if ("PRODUCTO".equals(detail.getTipo()) && detail.getProduct() != null) {
                Product product = detail.getProduct();
                product.setStock(product.getStock() + detail.getCantidad());
                productRepository.save(product);
            }
        }
        
        invoice.setEstado("ANULADA");
        invoice.setActivo(false);
        invoiceRepository.save(invoice);
    }

    @Transactional
    public void deleteInvoice(Integer id) {
        Invoice invoice = invoiceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Factura no encontrada con ID: " + id));
        
        invoice.setActivo(false);
        invoiceRepository.save(invoice);
    }

    public List<Map<String, Object>> getTopSellingProducts() {
        return invoiceDetailRepository.findTopSellingProducts();
    }

    public List<Map<String, Object>> getTopSellingServices() {
        return invoiceDetailRepository.findTopSellingServices();
    }

    private String generateInvoiceNumber() {
        Integer maxNumber = invoiceRepository.findMaxInvoiceNumber();
        int nextNumber = (maxNumber != null ? maxNumber : 0) + 1;
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");
        String prefix = LocalDateTime.now().format(formatter);
        
        return String.format("FAC-%s-%06d", prefix, nextNumber);
    }

    private InvoiceResponseDto mapToResponseDto(Invoice invoice) {
        InvoiceResponseDto dto = new InvoiceResponseDto();
        dto.setInvoiceId(invoice.getInvoiceId());
        dto.setNumero(invoice.getNumero());
        dto.setClientId(invoice.getClient().getUserId());
        dto.setClientName(invoice.getClient().getName());
        
        if (invoice.getEmployee() != null) {
            dto.setEmployeeId(invoice.getEmployee().getUserId());
            dto.setEmployeeName(invoice.getEmployee().getName());
        }
        
        dto.setFechaEmision(invoice.getFechaEmision());
        dto.setSubtotal(invoice.getSubtotal());
        dto.setDescuento(invoice.getDescuento());
        dto.setImpuesto(invoice.getImpuesto());
        dto.setTotal(invoice.getTotal());
        dto.setEstado(invoice.getEstado());
        dto.setObservaciones(invoice.getObservaciones());
        dto.setActivo(invoice.getActivo());
        dto.setCreatedOn(invoice.getCreatedOn());
        
        // Cargar detalles
        List<InvoiceDetailDto> detailDtos = invoiceDetailRepository.findByInvoiceId(invoice.getInvoiceId())
                .stream()
                .map(this::mapDetailToDto)
                .collect(Collectors.toList());
        dto.setDetails(detailDtos);
        
        return dto;
    }

    private InvoiceDetailDto mapDetailToDto(InvoiceDetail detail) {
        InvoiceDetailDto dto = new InvoiceDetailDto();
        dto.setDetailId(detail.getDetailId());
        dto.setTipo(detail.getTipo());
        dto.setCantidad(detail.getCantidad());
        dto.setPrecioUnitario(detail.getPrecioUnitario());
        dto.setDescuento(detail.getDescuento());
        dto.setSubtotal(detail.getSubtotal());
        
        if ("PRODUCTO".equals(detail.getTipo()) && detail.getProduct() != null) {
            dto.setProductId(detail.getProduct().getProductId());
            dto.setItemNombre(detail.getProduct().getNombre());
        } else if ("SERVICIO".equals(detail.getTipo()) && detail.getService() != null) {
            dto.setServiceId(detail.getService().getServiceId());
            dto.setItemNombre(detail.getService().getNombre());
        }
        
        return dto;
    }
}

