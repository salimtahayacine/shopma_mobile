package ma.shopma.app.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

/**
 * Gestion du canal et de l'envoi de la notification de confirmation de commande.
 */
public class NotificationHelper {

    private static final String CHANNEL_ID = "shopma_commandes";
    private static final int NOTIF_ID = 1001;

    /** A appeler au démarrage de l'application (Application ou première Activity). */
    public static void createChannel(Context ctx) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Commandes ShopMa",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Notifications de confirmation de commande");
            NotificationManager nm = ctx.getSystemService(NotificationManager.class);
            if (nm != null) nm.createNotificationChannel(channel);
        }
    }

    /** Envoie la notification de confirmation de commande. */
    public static void notifierCommande(Context ctx) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Commande confirmée !")
                .setContentText("Votre commande a bien été enregistrée. Merci !")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat nm = NotificationManagerCompat.from(ctx);
        try {
            nm.notify(NOTIF_ID, builder.build());
        } catch (SecurityException e) {
            // Permission POST_NOTIFICATIONS refusée sur API 33+ : on ignore proprement
        }
    }
}
