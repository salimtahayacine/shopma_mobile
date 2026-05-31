package ma.shopma.api.config;

import ma.shopma.api.entity.Product;
import ma.shopma.api.entity.Rating;
import ma.shopma.api.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Insere un jeu de produits marocains (prix en MAD) au demarrage,
 * uniquement si la table est vide. Categories identiques a FakeStoreAPI.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final ProductRepository repository;

    public DataSeeder(ProductRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (repository.count() > 0) {
            return; // deja initialise
        }

        List<Product> produits = Arrays.asList(
                // ===== electronics =====
                new Product("Smartphone Argan X10",
                        2499.00,
                        "Smartphone Android 6.5\", 128 Go, double SIM, ideal pour le quotidien.",
                        "electronics",
                        "https://fakestoreapi.com/img/61IBBVJvSDL._AC_SY879_.jpg",
                        new Rating(4.3, 215)),
                new Product("Casque Atlas Sound Pro",
                        599.00,
                        "Casque sans fil a reduction de bruit, autonomie 30h.",
                        "electronics",
                        "https://fakestoreapi.com/img/81QpkIctqPL._AC_SX679_.jpg",
                        new Rating(4.1, 98)),
                new Product("Disque dur externe Sahara 2 To",
                        749.00,
                        "Stockage portable USB 3.0, robuste et rapide.",
                        "electronics",
                        "https://fakestoreapi.com/img/61IBBVJvSDL._AC_SY879_.jpg",
                        new Rating(4.6, 340)),
                new Product("Ecran PC Medina 24\"",
                        1299.00,
                        "Moniteur Full HD 24 pouces, dalle IPS, 75 Hz.",
                        "electronics",
                        "https://fakestoreapi.com/img/81QpkIctqPL._AC_SX679_.jpg",
                        new Rating(4.0, 120)),
                new Product("Cle USB Tanja 64 Go",
                        99.00,
                        "Cle USB 3.1 compacte, transfert rapide.",
                        "electronics",
                        "https://fakestoreapi.com/img/61IBBVJvSDL._AC_SY879_.jpg",
                        new Rating(3.9, 76)),

                // ===== jewelery =====
                new Product("Bracelet Amazigh Argent",
                        450.00,
                        "Bracelet artisanal en argent grave de motifs berberes.",
                        "jewelery",
                        "https://fakestoreapi.com/img/71pWzhdJNwL._AC_UL640_QL65_ML3_.jpg",
                        new Rating(4.8, 410)),
                new Product("Collier Perle de Majorelle",
                        890.00,
                        "Collier elegant aux pierres bleues inspirees du jardin Majorelle.",
                        "jewelery",
                        "https://fakestoreapi.com/img/71YAIFU48IL._AC_UL640_QL65_ML3_.jpg",
                        new Rating(4.5, 150)),
                new Product("Bague Khamsa Or",
                        1750.00,
                        "Bague plaquee or motif main de Fatma, symbole de protection.",
                        "jewelery",
                        "https://fakestoreapi.com/img/71pWzhdJNwL._AC_UL640_QL65_ML3_.jpg",
                        new Rating(4.7, 89)),
                new Product("Boucles d'oreilles Zellige",
                        320.00,
                        "Boucles d'oreilles inspirees du zellige marocain.",
                        "jewelery",
                        "https://fakestoreapi.com/img/71YAIFU48IL._AC_UL640_QL65_ML3_.jpg",
                        new Rating(4.2, 64)),
                new Product("Pendentif Etoile Chrifa",
                        540.00,
                        "Pendentif en argent fin, finition artisanale de Fes.",
                        "jewelery",
                        "https://fakestoreapi.com/img/71pWzhdJNwL._AC_UL640_QL65_ML3_.jpg",
                        new Rating(4.4, 132)),

                // ===== men's clothing =====
                new Product("Djellaba Homme Traditionnelle",
                        650.00,
                        "Djellaba en laine, coupe classique, confortable et chaude.",
                        "men's clothing",
                        "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg",
                        new Rating(4.6, 188)),
                new Product("Chemise Lin Casablanca",
                        290.00,
                        "Chemise en lin respirant, ideale pour l'ete.",
                        "men's clothing",
                        "https://fakestoreapi.com/img/71YXzeOuslL._AC_UY879_.jpg",
                        new Rating(4.1, 95)),
                new Product("Veste Cuir Marrakech",
                        1450.00,
                        "Veste en cuir veritable, finition haut de gamme.",
                        "men's clothing",
                        "https://fakestoreapi.com/img/81fPKd-2AYL._AC_SL1500_.jpg",
                        new Rating(4.5, 142)),
                new Product("Babouches Cuir Fes",
                        220.00,
                        "Babouches traditionnelles en cuir naturel cousu main.",
                        "men's clothing",
                        "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg",
                        new Rating(4.3, 210)),
                new Product("Pull Atlas Laine",
                        380.00,
                        "Pull chaud en laine des montagnes de l'Atlas.",
                        "men's clothing",
                        "https://fakestoreapi.com/img/71YXzeOuslL._AC_UY879_.jpg",
                        new Rating(4.0, 73)),

                // ===== women's clothing =====
                new Product("Caftan Brode Royal",
                        1850.00,
                        "Caftan marocain brode a la main, tissu satine.",
                        "women's clothing",
                        "https://fakestoreapi.com/img/51eg55uWmdL._AC_UX679_.jpg",
                        new Rating(4.9, 320)),
                new Product("Takchita Ceremonie",
                        2400.00,
                        "Takchita deux pieces pour ceremonies, perles et fil dore.",
                        "women's clothing",
                        "https://fakestoreapi.com/img/71z3kpMAYsL._AC_UY879_.jpg",
                        new Rating(4.8, 175)),
                new Product("Robe Lin Essaouira",
                        420.00,
                        "Robe fluide en lin, parfaite pour la cote atlantique.",
                        "women's clothing",
                        "https://fakestoreapi.com/img/51Y5NI-I5jL._AC_UX679_.jpg",
                        new Rating(4.2, 88)),
                new Product("Foulard Soie Zellij",
                        180.00,
                        "Foulard en soie aux motifs geometriques marocains.",
                        "women's clothing",
                        "https://fakestoreapi.com/img/61pHAEJ4NML._AC_UX679_.jpg",
                        new Rating(4.4, 121)),
                new Product("Jabador Femme Moderne",
                        780.00,
                        "Jabador contemporain, coupe elegante et confortable.",
                        "women's clothing",
                        "https://fakestoreapi.com/img/71z3kpMAYsL._AC_UY879_.jpg",
                        new Rating(4.6, 134))
        );

        repository.saveAll(produits);
        System.out.println(">>> ShopMa : " + produits.size() + " produits marocains inseres.");
    }
}
