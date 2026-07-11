package ma.shopma.app.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ma.shopma.app.model.CartItem;
import ma.shopma.app.model.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Gestionnaire SQLite.
 * Tables : panier, commandes.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "shopma.db";
    private static final int DB_VERSION = 1;

    public static final ExecutorService DB_EXECUTOR = Executors.newSingleThreadExecutor();

    // Table panier
    public static final String TABLE_PANIER = "panier";
    public static final String COL_ID = "id";
    public static final String COL_PRODUCT_ID = "product_id";
    public static final String COL_TITLE = "title";
    public static final String COL_PRICE = "price";
    public static final String COL_QTY = "quantity";

    // Table commandes
    public static final String TABLE_COMMANDES = "commandes";
    public static final String COL_DATE = "date";
    public static final String COL_NB_ARTICLES = "nb_articles";
    public static final String COL_MONTANT = "montant_total";
    public static final String COL_STATUT = "statut";

    private static DatabaseHelper instance;

    public static DatabaseHelper getInstance(Context ctx) {
        if (instance == null) instance = new DatabaseHelper(ctx.getApplicationContext());
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_PANIER + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_PRODUCT_ID + " INTEGER, " +
                COL_TITLE + " TEXT, " +
                COL_PRICE + " REAL, " +
                COL_QTY + " INTEGER)");

        db.execSQL("CREATE TABLE " + TABLE_COMMANDES + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_DATE + " TEXT, " +
                COL_NB_ARTICLES + " INTEGER, " +
                COL_MONTANT + " REAL, " +
                COL_STATUT + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANIER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMMANDES);
        onCreate(db);
    }

    // ==================== PANIER ====================

    /** Ajoute au panier ou incrémente la quantité si le produit existe déjà. */
    public void ajouterAuPanier(CartItem item) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.query(TABLE_PANIER, new String[]{COL_ID, COL_QTY},
                COL_PRODUCT_ID + "=?", new String[]{String.valueOf(item.getProductId())},
                null, null, null);
        if (c.moveToFirst()) {
            long rowId = c.getLong(0);
            int oldQty = c.getInt(1);
            ContentValues cv = new ContentValues();
            cv.put(COL_QTY, oldQty + item.getQuantity());
            db.update(TABLE_PANIER, cv, COL_ID + "=?", new String[]{String.valueOf(rowId)});
        } else {
            ContentValues cv = new ContentValues();
            cv.put(COL_PRODUCT_ID, item.getProductId());
            cv.put(COL_TITLE, item.getTitle());
            cv.put(COL_PRICE, item.getPrice());
            cv.put(COL_QTY, item.getQuantity());
            db.insert(TABLE_PANIER, null, cv);
        }
        c.close();
    }

    /** Liste des articles du panier. */
    public List<CartItem> lirePanier() {
        List<CartItem> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_PANIER, null, null, null, null, null, null);
        while (c.moveToNext()) {
            CartItem item = new CartItem();
            item.setId(c.getLong(c.getColumnIndexOrThrow(COL_ID)));
            item.setProductId(c.getInt(c.getColumnIndexOrThrow(COL_PRODUCT_ID)));
            item.setTitle(c.getString(c.getColumnIndexOrThrow(COL_TITLE)));
            item.setPrice(c.getDouble(c.getColumnIndexOrThrow(COL_PRICE)));
            item.setQuantity(c.getInt(c.getColumnIndexOrThrow(COL_QTY)));
            list.add(item);
        }
        c.close();
        return list;
    }

    /** Met à jour la quantité d'un article (si qty=0 → suppression). */
    public void mettreAJourQuantite(long id, int qty) {
        SQLiteDatabase db = getWritableDatabase();
        if (qty <= 0) {
            db.delete(TABLE_PANIER, COL_ID + "=?", new String[]{String.valueOf(id)});
        } else {
            ContentValues cv = new ContentValues();
            cv.put(COL_QTY, qty);
            db.update(TABLE_PANIER, cv, COL_ID + "=?", new String[]{String.valueOf(id)});
        }
    }

    /** Supprime un article du panier. */
    public void supprimerArticle(long id) {
        getWritableDatabase().delete(TABLE_PANIER, COL_ID + "=?", new String[]{String.valueOf(id)});
    }

    /** Vide intégralement le panier (après commande). */
    public void viderPanier() {
        getWritableDatabase().delete(TABLE_PANIER, null, null);
    }

    /** Nombre total d'articles dans le panier (somme des quantités) → badge. */
    public int compterArticlesPanier() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT SUM(" + COL_QTY + ") FROM " + TABLE_PANIER, null);
        int total = 0;
        if (c.moveToFirst()) total = c.isNull(0) ? 0 : c.getInt(0);
        c.close();
        return total;
    }

    /** Calcule le total monétaire du panier. */
    public double totalPanier() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT SUM(" + COL_PRICE + " * " + COL_QTY + ") FROM " + TABLE_PANIER, null);
        double total = 0;
        if (c.moveToFirst()) total = c.isNull(0) ? 0 : c.getDouble(0);
        c.close();
        return total;
    }

    // ==================== COMMANDES ====================

    /** Enregistre une commande. */
    public void ajouterCommande(Order order) {
        ContentValues cv = new ContentValues();
        cv.put(COL_DATE, order.getDate());
        cv.put(COL_NB_ARTICLES, order.getNbArticles());
        cv.put(COL_MONTANT, order.getMontantTotal());
        cv.put(COL_STATUT, order.getStatut());
        getWritableDatabase().insert(TABLE_COMMANDES, null, cv);
    }

    /** Historique des commandes (plus récentes d'abord). */
    public List<Order> lireCommandes() {
        List<Order> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_COMMANDES, null, null, null, null, null, COL_ID + " DESC");
        while (c.moveToNext()) {
            Order o = new Order();
            o.setId(c.getLong(c.getColumnIndexOrThrow(COL_ID)));
            o.setDate(c.getString(c.getColumnIndexOrThrow(COL_DATE)));
            o.setNbArticles(c.getInt(c.getColumnIndexOrThrow(COL_NB_ARTICLES)));
            o.setMontantTotal(c.getDouble(c.getColumnIndexOrThrow(COL_MONTANT)));
            o.setStatut(c.getString(c.getColumnIndexOrThrow(COL_STATUT)));
            list.add(o);
        }
        c.close();
        return list;
    }
}
