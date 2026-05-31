package ma.shopma.api.controller;

import ma.shopma.api.dto.ProductDTO;
import ma.shopma.api.exception.ResourceNotFoundException;
import ma.shopma.api.service.ProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Endpoints REST compatibles FakeStoreAPI.
 *   GET /products
 *   GET /products/categories
 *   GET /products/category/{nom}
 *   GET /products/{id}
 */
@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    /** Liste de tous les produits. */
    @GetMapping
    public List<ProductDTO> getAllProducts() {
        return service.findAll();
    }

    /** Liste des categories disponibles. */
    @GetMapping("/categories")
    public List<String> getCategories() {
        return service.findCategories();
    }

    /** Produits filtres par categorie. */
    @GetMapping("/category/{nom}")
    public List<ProductDTO> getByCategory(@PathVariable("nom") String nom) {
        return service.findByCategory(nom);
    }

    /** Un produit par son identifiant (404 si introuvable). */
    @GetMapping("/{id}")
    public ProductDTO getById(@PathVariable("id") Long id) {
        return service.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit introuvable : id=" + id));
    }
}
