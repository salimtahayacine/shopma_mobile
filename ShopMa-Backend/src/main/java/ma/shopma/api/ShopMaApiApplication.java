package ma.shopma.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Point d'entree de l'API ShopMa.
 * Backend compatible FakeStoreAPI (produits marocains, prix en MAD).
 */
@SpringBootApplication
public class ShopMaApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopMaApiApplication.class, args);
    }
}
