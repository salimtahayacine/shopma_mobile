package ma.shopma.api;

import ma.shopma.api.dto.ProductDTO;
import ma.shopma.api.entity.Product;
import ma.shopma.api.entity.Rating;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test unitaire leger : verifie le mapping Entity -> DTO (compatibilite FakeStoreAPI).
 * N'a pas besoin du contexte Spring ni de la base de donnees.
 */
class ShopMaApiApplicationTests {

    @Test
    void mappingProductVersDto() {
        Product p = new Product("Caftan Brode Royal", 1850.0,
                "Caftan marocain brode a la main", "women's clothing",
                "https://exemple.ma/caftan.jpg", new Rating(4.9, 320));
        p.setId(7L);

        ProductDTO dto = ProductDTO.from(p);

        assertEquals(7L, dto.getId());
        assertEquals("Caftan Brode Royal", dto.getTitle());
        assertEquals(1850.0, dto.getPrice());
        assertEquals("women's clothing", dto.getCategory());
        assertNotNull(dto.getRating());
        assertEquals(4.9, dto.getRating().getRate());
        assertEquals(320, dto.getRating().getCount());
    }
}
