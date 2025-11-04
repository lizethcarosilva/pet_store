package com.cipasuno.petstore.pet_store.services;

import com.cipasuno.petstore.pet_store.models.Appointment;
import com.cipasuno.petstore.pet_store.models.Client;
import com.cipasuno.petstore.pet_store.models.Invoice;
import com.cipasuno.petstore.pet_store.models.InvoiceDetail;
import com.cipasuno.petstore.pet_store.models.Product;
import com.cipasuno.petstore.pet_store.models.Service;
import com.cipasuno.petstore.pet_store.models.User;
import com.cipasuno.petstore.pet_store.models.Vaccination;
import com.cipasuno.petstore.pet_store.models.DTOs.InvoiceCreateDto;
import com.cipasuno.petstore.pet_store.models.DTOs.InvoiceDetailDto;
import com.cipasuno.petstore.pet_store.models.DTOs.InvoiceResponseDto;
import com.cipasuno.petstore.pet_store.repositories.AppointmentRepository;
import com.cipasuno.petstore.pet_store.repositories.ClientRepository;
import com.cipasuno.petstore.pet_store.repositories.InvoiceDetailRepository;
import com.cipasuno.petstore.pet_store.repositories.InvoiceRepository;
import com.cipasuno.petstore.pet_store.repositories.ProductRepository;
import com.cipasuno.petstore.pet_store.repositories.ServiceRepository;
import com.cipasuno.petstore.pet_store.repositories.UserRepository;
import com.cipasuno.petstore.pet_store.repositories.VaccinationRepository;
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
    private ClientRepository clientRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private VaccinationRepository vaccinationRepository;

    @Transactional
    public InvoiceResponseDto createInvoice(InvoiceCreateDto invoiceDto, String tenantId) {
        Invoice invoice = new Invoice();
        
        // Establecer tenantId
        invoice.setTenantId(tenantId);
        
        Client client = clientRepository.findById(invoiceDto.getClientId())
            .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + invoiceDto.getClientId()));
        
        invoice.setClient(client);
        
        if (invoiceDto.getEmployeeId() != null) {
            User employee = userRepository.findById(invoiceDto.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado con ID: " + invoiceDto.getEmployeeId()));
            invoice.setEmployee(employee);
        }
        
        // Asignar cita agendada si se proporciona (solo para servicios agendados)
        if (invoiceDto.getAppointmentId() != null) {
            Appointment appointment = appointmentRepository.findById(invoiceDto.getAppointmentId())
                .orElseThrow(() -> new RuntimeException("Cita no encontrada con ID: " + invoiceDto.getAppointmentId()));
            
            // Validar que la cita pertenece al cliente
            if (!appointment.getClient().getClientId().equals(invoiceDto.getClientId())) {
                throw new RuntimeException("La cita no pertenece al cliente especificado");
            }
            
            invoice.setAppointment(appointment);
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
                System.out.println("🟡 Procesando detalle tipo: " + detailDto.getTipo());
                
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
                    
                } else if ("VACUNACION".equals(detailDto.getTipo())) {
                    // 🔥 NUEVA FUNCIONALIDAD: Facturar vacunación
                    System.out.println("🟡 Facturando vacunación ID: " + detailDto.getVaccinationId());
                    
                    Vaccination vaccination = vaccinationRepository.findById(detailDto.getVaccinationId())
                        .orElseThrow(() -> new RuntimeException("Vacunación no encontrada con ID: " + detailDto.getVaccinationId()));
                    
                    System.out.println("🟡 Vacunación encontrada. Estado actual: " + vaccination.getEstado());
                    
                    // Establecer referencia a la vacunación
                    detail.setVaccination(vaccination);
                    detail.setPrecioUnitario(detailDto.getPrecioUnitario()); // El precio viene del frontend
                    
                    // 🔥 IMPORTANTE: Marcar la vacunación como FACTURADA automáticamente
                    vaccination.setEstado("FACTURADA");
                    vaccinationRepository.save(vaccination);
                    System.out.println("✅ Vacunación " + vaccination.getVaccinationId() + " marcada como FACTURADA");
                    
                } else if ("CITA".equals(detailDto.getTipo())) {
                    // 🔥 NUEVA FUNCIONALIDAD: Facturar cita
                    System.out.println("🟡 Facturando cita ID: " + detailDto.getAppointmentId());
                    
                    Appointment appointment = appointmentRepository.findById(detailDto.getAppointmentId())
                        .orElseThrow(() -> new RuntimeException("Cita no encontrada con ID: " + detailDto.getAppointmentId()));
                    
                    System.out.println("🟡 Cita encontrada. Estado actual: " + appointment.getEstado());
                    
                    // Establecer referencia a la cita
                    detail.setAppointment(appointment);
                    detail.setPrecioUnitario(detailDto.getPrecioUnitario()); // El precio viene del frontend
                    
                    // 🔥 IMPORTANTE: Marcar la cita como FACTURADA automáticamente
                    appointment.setEstado("FACTURADA");
                    appointmentRepository.save(appointment);
                    System.out.println("✅ Cita " + appointment.getAppointmentId() + " marcada como FACTURADA");
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
        return invoiceRepository.findAllWithRelations()
                .stream()
                .filter(invoice -> {
                    try {
                        // Verificar que el cliente existe (evitar datos huérfanos)
                        if (invoice.getClient() != null) {
                            invoice.getClient().getName(); // Esto lanza exception si el client no existe
                            return true;
                        }
                        return false;
                    } catch (Exception e) {
                        // Si hay error al acceder al cliente, filtrar esta factura
                        System.err.println("⚠️  Factura " + invoice.getInvoiceId() + " tiene cliente huérfano, omitiendo...");
                        return false;
                    }
                })
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public List<InvoiceResponseDto> getActiveInvoices() {
        return invoiceRepository.findByActivoTrue()
                .stream()
                .filter(invoice -> {
                    try {
                        // Verificar que el cliente existe (evitar datos huérfanos)
                        if (invoice.getClient() != null) {
                            invoice.getClient().getName(); // Esto lanza exception si el client no existe
                            return true;
                        }
                        return false;
                    } catch (Exception e) {
                        // Si hay error al acceder al cliente, filtrar esta factura
                        System.err.println("⚠️  Factura " + invoice.getInvoiceId() + " tiene cliente huérfano, omitiendo...");
                        return false;
                    }
                })
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
        dto.setClientId(invoice.getClient().getClientId());
        dto.setClientName(invoice.getClient().getName());
        
        if (invoice.getEmployee() != null) {
            dto.setEmployeeId(invoice.getEmployee().getUserId());
            dto.setEmployeeName(invoice.getEmployee().getName());
        }
        
        if (invoice.getAppointment() != null) {
            dto.setAppointmentId(invoice.getAppointment().getAppointmentId());
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
        
        // NO incluir details en listados masivos para evitar N+1
        // Los details se pueden obtener con el endpoint específico por ID
        dto.setDetails(new java.util.ArrayList<>());
        
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

