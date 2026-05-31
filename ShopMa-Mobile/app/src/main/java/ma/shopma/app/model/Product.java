package ma.shopma.app.model;

import com.google.gson.annotations.SerializedName;

/**
 * Modele produit - correspond exactement au JSON FakeStoreAPI / backend ShopMa.
 * Les noms des champs correspondent aux cles JSON (Gson).
 */
public class Product {

    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("price")
    private double price;

    @SerializedName("description")
    private String description;

    @SerializedName("category")
    private String category;

    @SerializedName("image")
    private String image;

    @SerializedName("rating")
    private Rating rating;

    public int getId() { return id; }
    public String getTitle() { return title; }
    public double getPrice() { return price; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public String getImage() { return image; }
    public Rating getRating() { return rating; }

    public static class Rating {
        @SerializedName("rate")
        private double rate;
        @SerializedName("count")
        private int count;
        public double getRate() { return rate; }
        public int getCount() { return count; }
    }
}
