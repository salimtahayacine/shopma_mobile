package ma.shopma.api.repository;

import ma.shopma.api.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Acces aux donnees des produits (Spring Data JPA).
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

    /** Produits d'une categorie donnee. */
    List<Product> findByCategory(String category);

    /** Liste des categories distinctes (pour GET /products/categories). */
    @Query("SELECT DISTINCT p.category FROM Product p ORDER BY p.category")
    List<String> findDistinctCategories();
}
