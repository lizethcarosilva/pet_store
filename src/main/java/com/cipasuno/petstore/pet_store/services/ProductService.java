package com.cipasuno.petstore.pet_store.services;

import com.cipasuno.petstore.pet_store.models.Product;
import com.cipasuno.petstore.pet_store.models.DTOs.ProductCreateDto;
import com.cipasuno.petstore.pet_store.models.DTOs.ProductResponseDto;
import com.cipasuno.petstore.pet_store.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public ProductResponseDto createProduct(ProductCreateDto productDto) {
        Product product = new Product();
        product.setCodigo(productDto.getCodigo());
        product.setNombre(productDto.getNombre());
        product.setDescripcion(productDto.getDescripcion());
        product.setPresentacion(productDto.getPresentacion());
        product.setPrecio(productDto.getPrecio());
        product.setStock(productDto.getStock() != null ? productDto.getStock() : 0);
        product.setStockMinimo(productDto.getStockMinimo() != null ? productDto.getStockMinimo() : 5);
        product.setFechaVencimiento(productDto.getFechaVencimiento());

        Product savedProduct = productRepository.save(product);
        return mapToResponseDto(savedProduct);
    }

    public List<ProductResponseDto> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public List<ProductResponseDto> getActiveProducts() {
        return productRepository.findByActivoTrue()
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public Optional<ProductResponseDto> getProductById(Integer id) {
        return productRepository.findById(id)
                .map(this::mapToResponseDto);
    }

    public Optional<ProductResponseDto> getProductByCodigo(String codigo) {
        return productRepository.findByCodigo(codigo)
                .map(this::mapToResponseDto);
    }

    public List<ProductResponseDto> searchProductsByName(String nombre) {
        return productRepository.findByNombreContainingIgnoreCase(nombre)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public List<ProductResponseDto> getLowStockProducts() {
        return productRepository.findLowStockProducts()
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public List<ProductResponseDto> getProductsExpiringBefore(LocalDate fecha) {
        return productRepository.findProductsExpiringBefore(fecha)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public List<ProductResponseDto> getProductsExpiringSoon() {
        LocalDate thirtyDaysFromNow = LocalDate.now().plusDays(30);
        return getProductsExpiringBefore(thirtyDaysFromNow);
    }

    @Transactional
    public ProductResponseDto updateProduct(Integer id, Product productDetails) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));

        if (productDetails.getCodigo() != null) {
            product.setCodigo(productDetails.getCodigo());
        }
        if (productDetails.getNombre() != null) {
            product.setNombre(productDetails.getNombre());
        }
        if (productDetails.getDescripcion() != null) {
            product.setDescripcion(productDetails.getDescripcion());
        }
        if (productDetails.getPresentacion() != null) {
            product.setPresentacion(productDetails.getPresentacion());
        }
        if (productDetails.getPrecio() != null) {
            product.setPrecio(productDetails.getPrecio());
        }
        if (productDetails.getStock() != null) {
            product.setStock(productDetails.getStock());
        }
        if (productDetails.getStockMinimo() != null) {
            product.setStockMinimo(productDetails.getStockMinimo());
        }
        if (productDetails.getFechaVencimiento() != null) {
            product.setFechaVencimiento(productDetails.getFechaVencimiento());
        }
        if (productDetails.getActivo() != null) {
            product.setActivo(productDetails.getActivo());
        }

        Product updatedProduct = productRepository.save(product);
        return mapToResponseDto(updatedProduct);
    }

    @Transactional
    public void updateStock(Integer id, Integer cantidad) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
        
        product.setStock(product.getStock() + cantidad);
        productRepository.save(product);
    }

    @Transactional
    public void deletProduct(Integer id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
        
        product.setActivo(false);
        productRepository.save(product);
    }

    @Transactional
    public void deleteProductPermanently(Integer id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
        
        productRepository.delete(product);
    }

    public long countActiveProducts() {
        return productRepository.countActiveProducts();
    }

    public long countLowStockProducts() {
        return productRepository.countLowStockProducts();
    }

    private ProductResponseDto mapToResponseDto(Product product) {
        ProductResponseDto dto = new ProductResponseDto();
        dto.setProductId(product.getProductId());
        dto.setCodigo(product.getCodigo());
        dto.setNombre(product.getNombre());
        dto.setDescripcion(product.getDescripcion());
        dto.setPresentacion(product.getPresentacion());
        dto.setPrecio(product.getPrecio());
        dto.setStock(product.getStock());
        dto.setStockMinimo(product.getStockMinimo());
        dto.setFechaVencimiento(product.getFechaVencimiento());
        dto.setActivo(product.getActivo());
        dto.setCreatedOn(product.getCreatedOn());
        
        // Calcular campos adicionales
        dto.setLowStock(product.getStock() < product.getStockMinimo());
        
        if (product.getFechaVencimiento() != null) {
            long daysUntilExpiration = LocalDate.now().until(product.getFechaVencimiento()).getDays();
            dto.setNearExpiration(daysUntilExpiration <= 30 && daysUntilExpiration >= 0);
        } else {
            dto.setNearExpiration(false);
        }
        
        return dto;
    }
}

