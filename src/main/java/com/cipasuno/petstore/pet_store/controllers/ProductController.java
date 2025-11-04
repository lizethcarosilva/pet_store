package com.cipasuno.petstore.pet_store.controllers;

import com.cipasuno.petstore.pet_store.config.TenantContext;
import com.cipasuno.petstore.pet_store.models.Product;
import com.cipasuno.petstore.pet_store.models.DTOs.ProductCreateDto;
import com.cipasuno.petstore.pet_store.models.DTOs.ProductResponseDto;
import com.cipasuno.petstore.pet_store.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
@Tag(name = "Product", description = "API para gestión de productos e inventario")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/create")
    @Operation(summary = "Crear un nuevo producto")
    public ResponseEntity<?> createProduct(@RequestBody ProductCreateDto product) {
        try {
            String tenantIdStr = TenantContext.getTenantId();
            if (tenantIdStr == null || tenantIdStr.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: tenantId es requerido. Verifique que el header X-Tenant-ID esté presente.");
            }

            Integer tenantId;
            try {
                tenantId = Integer.parseInt(tenantIdStr);
            } catch (NumberFormatException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: tenantId debe ser un número válido.");
            }

            // Validar código único
            Optional<ProductResponseDto> existingProduct = productService.getProductByCodigo(product.getCodigo());
            if (existingProduct.isPresent()) {
                return ResponseEntity.badRequest()
                    .body("Ya existe un producto con este código: " + product.getCodigo());
            }

            ProductResponseDto createdProduct = productService.createProduct(product, tenantId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al crear el producto: " + e.getMessage());
        }
    }

    @GetMapping
    @Operation(summary = "Obtener todos los productos")
    public ResponseEntity<List<ProductResponseDto>> getAllProducts() {
        List<ProductResponseDto> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/active")
    @Operation(summary = "Obtener productos activos")
    public ResponseEntity<List<ProductResponseDto>> getActiveProducts() {
        List<ProductResponseDto> products = productService.getActiveProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/getId")
    @Operation(summary = "Obtener producto por ID")
    public ResponseEntity<?> getProductById(@RequestBody Integer id) {
        Optional<ProductResponseDto> product = productService.getProductById(id);
        if (product.isPresent()) {
            return ResponseEntity.ok(product.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/codigo")
    @Operation(summary = "Obtener producto por código")
    public ResponseEntity<?> getProductByCodigo(@RequestParam String codigo) {
        Optional<ProductResponseDto> product = productService.getProductByCodigo(codigo);
        if (product.isPresent()) {
            return ResponseEntity.ok(product.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar productos por nombre")
    public ResponseEntity<List<ProductResponseDto>> searchProductsByName(@RequestParam String name) {
        List<ProductResponseDto> products = productService.searchProductsByName(name);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/lowStock")
    @Operation(summary = "Obtener productos con stock bajo")
    public ResponseEntity<List<ProductResponseDto>> getLowStockProducts() {
        List<ProductResponseDto> products = productService.getLowStockProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/expiringSoon")
    @Operation(summary = "Obtener productos próximos a vencer (30 días)")
    public ResponseEntity<List<ProductResponseDto>> getProductsExpiringSoon() {
        List<ProductResponseDto> products = productService.getProductsExpiringSoon();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/expiringBefore")
    @Operation(summary = "Obtener productos que vencen antes de una fecha")
    public ResponseEntity<List<ProductResponseDto>> getProductsExpiringBefore(@RequestParam String fecha) {
        try {
            LocalDate date = LocalDate.parse(fecha);
            List<ProductResponseDto> products = productService.getProductsExpiringBefore(date);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/update")
    @Operation(summary = "Actualizar producto")
    public ResponseEntity<?> updateProduct(@RequestBody Product productDetails) {
        try {
            Optional<ProductResponseDto> existingProduct = productService.getProductById(productDetails.getProductId());
            if (!existingProduct.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            ProductResponseDto updatedProduct = productService.updateProduct(productDetails.getProductId(), productDetails);
            return ResponseEntity.ok(updatedProduct);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al actualizar el producto: " + e.getMessage());
        }
    }

    @PutMapping("/updateStock")
    @Operation(summary = "Actualizar stock de producto")
    public ResponseEntity<?> updateStock(@RequestParam Integer productId, @RequestParam Integer cantidad) {
        try {
            productService.updateStock(productId, cantidad);
            return ResponseEntity.ok("Stock actualizado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al actualizar el stock: " + e.getMessage());
        }
    }

    @DeleteMapping("/deleteProduct")
    @Operation(summary = "Eliminar producto (soft delete)")
    public ResponseEntity<?> deleteProduct(@RequestBody Integer productId) {
        try {
            Optional<ProductResponseDto> product = productService.getProductById(productId);
            if (!product.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            productService.deletProduct(productId);
            return ResponseEntity.ok("Producto eliminado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al eliminar el producto: " + e.getMessage());
        }
    }

    @DeleteMapping("/productPermanent")
    @Operation(summary = "Eliminar producto permanentemente")
    public ResponseEntity<?> deleteProductPermanently(@RequestBody Integer id) {
        try {
            Optional<ProductResponseDto> product = productService.getProductById(id);
            if (!product.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            productService.deleteProductPermanently(id);
            return ResponseEntity.ok("Producto eliminado permanentemente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al eliminar el producto: " + e.getMessage());
        }
    }

    @GetMapping("/count")
    @Operation(summary = "Contar productos activos")
    public ResponseEntity<Long> countActiveProducts() {
        long count = productService.countActiveProducts();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count/lowStock")
    @Operation(summary = "Contar productos con stock bajo")
    public ResponseEntity<Long> countLowStockProducts() {
        long count = productService.countLowStockProducts();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/vaccines")
    @Operation(summary = "Obtener todas las vacunas registradas")
    public ResponseEntity<List<ProductResponseDto>> getVaccineProducts() {
        List<ProductResponseDto> vaccines = productService.getVaccineProducts();
        return ResponseEntity.ok(vaccines);
    }

    @GetMapping("/vaccines/available")
    @Operation(summary = "Obtener vacunas disponibles (con stock)")
    public ResponseEntity<List<ProductResponseDto>> getAvailableVaccineProducts() {
        List<ProductResponseDto> vaccines = productService.getAvailableVaccineProducts();
        return ResponseEntity.ok(vaccines);
    }

    @GetMapping("/count/vaccines")
    @Operation(summary = "Contar vacunas registradas")
    public ResponseEntity<Long> countVaccineProducts() {
        long count = productService.countVaccineProducts();
        return ResponseEntity.ok(count);
    }
}

