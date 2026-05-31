package ma.shopma.app.model;

/** Ligne de la table SQLite 'commandes'. */
public class Order {
    private long id;
    private String date;
    private int nbArticles;
    private double montantTotal;
    private String statut; // "en_cours" ou "livree"

    public Order() {}

    public Order(String date, int nbArticles, double montantTotal, String statut) {
        this.date = date;
        this.nbArticles = nbArticles;
        this.montantTotal = montantTotal;
        this.statut = statut;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public int getNbArticles() { return nbArticles; }
    public void setNbArticles(int nbArticles) { this.nbArticles = nbArticles; }
    public double getMontantTotal() { return montantTotal; }
    public void setMontantTotal(double montantTotal) { this.montantTotal = montantTotal; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
}
