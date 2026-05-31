package ma.shopma.api.service;

import ma.shopma.api.dto.ProductDTO;
import ma.shopma.api.entity.Product;
import ma.shopma.api.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Logique metier des produits.
 */
@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    /** Tous les produits. */
    public List<ProductDTO> findAll() {
        return repository.findAll().stream()
                .map(ProductDTO::from)
                .collect(Collectors.toList());
    }

    /** Produits d'une categorie. */
    public List<ProductDTO> findByCategory(String category) {
        return repository.findByCategory(category).stream()
                .map(ProductDTO::from)
                .collect(Collectors.toList());
    }

    /** Un produit par identifiant (Optional vide si absent). */
    public Optional<ProductDTO> findById(Long id) {
        return repository.findById(id).map(ProductDTO::from);
    }

    /** Liste des categories distinctes. */
    public List<String> findCategories() {
        return repository.findDistinctCategories();
    }
}
