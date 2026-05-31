package ma.shopma.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * Note d'un produit. Aplati en deux colonnes (rating_rate, rating_count)
 * mais expose en objet JSON imbrique "rating": { "rate": .., "count": .. }
 * pour rester compatible avec le contrat FakeStoreAPI.
 */
@Embeddable
public class Rating {

    @Column(name = "rating_rate")
    private double rate;

    @Column(name = "rating_count")
    private int count;

    public Rating() {
    }

    public Rating(double rate, int count) {
        this.rate = rate;
        this.count = count;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
