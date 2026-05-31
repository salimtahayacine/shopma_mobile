package ma.shopma.app.api;

import ma.shopma.app.model.Product;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.util.List;

/**
 * Interface Retrofit - compatible FakeStoreAPI et backend ShopMa Spring Boot.
 */
public interface ApiService {

    /** Tous les produits. */
    @GET("products")
    Call<List<Product>> getAllProducts();

    /** Liste des categories. */
    @GET("products/categories")
    Call<List<String>> getCategories();

    /** Produits par categorie. */
    @GET("products/category/{cat}")
    Call<List<Product>> getProductsByCategory(@Path("cat") String category);

    /** Un produit par id. */
    @GET("products/{id}")
    Call<Product> getProductById(@Path("id") int id);
}
