package ma.shopma.api.dto;

import ma.shopma.api.entity.Product;

/**
 * Representation JSON d'un produit, identique au contrat FakeStoreAPI :
 * { id, title, price, description, category, image, rating: { rate, count } }
 */
public class ProductDTO {

    private Long id;
    private String title;
    private double price;
    private String description;
    private String category;
    private String image;
    private RatingDTO rating;

    public ProductDTO() {
    }

    /** Construit le DTO a partir de l'entite JPA. */
    public static ProductDTO from(Product p) {
        ProductDTO dto = new ProductDTO();
        dto.id = p.getId();
        dto.title = p.getTitle();
        dto.price = p.getPrice();
        dto.description = p.getDescription();
        dto.category = p.getCategory();
        dto.image = p.getImage();
        if (p.getRating() != null) {
            dto.rating = new RatingDTO(p.getRating().getRate(), p.getRating().getCount());
        }
        return dto;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public double getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getImage() {
        return image;
    }

    public RatingDTO getRating() {
        return rating;
    }

    /** Note imbriquee. */
    public static class RatingDTO {
        private double rate;
        private int count;

        public RatingDTO() {
        }

        public RatingDTO(double rate, int count) {
            this.rate = rate;
            this.count = count;
        }

        public double getRate() {
            return rate;
        }

        public int getCount() {
            return count;
        }
    }
}
