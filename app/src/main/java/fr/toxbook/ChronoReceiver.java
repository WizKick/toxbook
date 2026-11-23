package fr.toxbook;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class ChronoReceiver extends BroadcastReceiver {

    public static final String CHANNEL = "toxbook_chrono";

    public static void channel(Context c) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return;
        NotificationManager nm = c.getSystemService(NotificationManager.class);
        if (nm == null) return;
        NotificationChannel ch = new NotificationChannel(CHANNEL, "Chrono de delai", NotificationManager.IMPORTANCE_HIGH);
        ch.setDescription("Compte a rebours d'action des poisons");
        ch.enableVibration(true);
        nm.createNotificationChannel(ch);
    }

    @Override
    public void onReceive(Context c, Intent i) {
        channel(c);
        String nom = i.getStringExtra("nom");
        String dose = i.getStringExtra("dose");
        if (nom == null) nom = "Dossier";

        Intent o = new Intent(c, MainActivity.class);
        o.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) flags |= PendingIntent.FLAG_IMMUTABLE;
        PendingIntent pi = PendingIntent.getActivity(c, 0, o, flags);

        Notification.Builder b;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            b = new Notification.Builder(c, CHANNEL);
        } else {
            b = new Notification.Builder(c);
        }

        b.setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setContentTitle("DELAI ATTEINT  //  " + nom)
                .setContentText(dose == null ? "Le delai d'action est ecoule." : dose)
                .setStyle(new Notification.BigTextStyle().bigText(dose == null ? "Le delai d'action est ecoule." : dose))
                .setAutoCancel(true)
                .setContentIntent(pi)
                .setVibrate(new long[]{0, 400, 200, 400});

        NotificationManager nm = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) nm.notify((int) (System.currentTimeMillis() % 100000), b.build());
    }
}