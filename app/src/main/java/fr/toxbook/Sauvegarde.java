package fr.toxbook;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Sauvegarde {

    private static final int BG   = 0xFF060606;
    private static final int GOLD = 0xFFD4AF37;
    private static final int GREY = 0xFF8A8A8A;
    private static final int RED  = 0xFFB3202A;
    private static final int WHITE = 0xFFEDEDED;

    public static void show(final Activity a, final FrameLayout root, final Runnable apres) {
        final FrameLayout ov = new FrameLayout(a);
        ov.setBackgroundColor(BG);
        ov.setClickable(true);
        ov.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, android.view.MotionEvent e) { return true; }
        });

        LinearLayout box = new LinearLayout(a);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(a, 20), dp(a, 20), dp(a, 20), dp(a, 16));

        TextView stamp = new TextView(a);
        stamp.setText("SAUVEGARDE LOCALE  //  FICHES, STOCK, CODE");
        stamp.setTypeface(Typeface.MONOSPACE);
        stamp.setTextSize(9);
        stamp.setLetterSpacing(0.3f);
        stamp.setTextColor(RED);

        TextView title = new TextView(a);
        title.setText("SAUVEGARDE");
        title.setTypeface(Typeface.create("serif", Typeface.BOLD));
        title.setTextSize(22);
        title.setTextColor(GOLD);
        title.setPadding(0, dp(a, 8), 0, dp(a, 8));

        TextView chemin = new TextView(a);
        chemin.setText(dir(a) == null ? "Stockage externe indisponible" : dir(a).getAbsolutePath());
        chemin.setTypeface(Typeface.MONOSPACE);
        chemin.setTextSize(9);
        chemin.setTextColor(GREY);
        chemin.setPadding(0, 0, 0, dp(a, 10));

        TextView exp = new TextView(a);
        exp.setText("EXPORTER MAINTENANT");
        exp.setTypeface(Typeface.MONOSPACE);
        exp.setTextSize(12);
        exp.setLetterSpacing(0.12f);
        exp.setGravity(Gravity.CENTER);
        exp.setTextColor(0xFF060606);
        exp.setPadding(dp(a, 14), dp(a, 13), dp(a, 14), dp(a, 13));
        GradientDrawable eb = new GradientDrawable();
        eb.setColor(GOLD);
        eb.setCornerRadius(dp(a, 3));
        exp.setBackground(eb);

        TextView lab = new TextView(a);
        lab.setText("SAUVEGARDES EXISTANTES");
        lab.setTypeface(Typeface.MONOSPACE);
        lab.setTextSize(9);
        lab.setLetterSpacing(0.25f);
        lab.setTextColor(GOLD);
        lab.setPadding(0, dp(a, 16), 0, dp(a, 6));

        final LinearLayout liste = new LinearLayout(a);
        liste.setOrientation(LinearLayout.VERTICAL);

        ScrollView sv = new ScrollView(a);
        sv.addView(liste);

        TextView close = new TextView(a);
        close.setText("FERMER");
        close.setTypeface(Typeface.MONOSPACE);
        close.setTextSize(11);
        close.setGravity(Gravity.CENTER);
        close.setTextColor(GREY);
        close.setPadding(dp(a, 10), dp(a, 13), dp(a, 10), dp(a, 13));
        GradientDrawable cb = new GradientDrawable();
        cb.setColor(0xFF101010);
        cb.setCornerRadius(dp(a, 3));
        cb.setStroke(dp(a, 1), 0x44D4AF37);
        close.setBackground(cb);
        close.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { ferme(ov, root); }
        });

        exp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    exporte(a);
                    remplis(a, liste, root, apres);
                    android.widget.Toast.makeText(a, "Sauvegarde exportee", android.widget.Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    android.widget.Toast.makeText(a, "Echec : " + e.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
                }
            }
        });

        box.addView(stamp);
        box.addView(title);
        box.addView(chemin);
        box.addView(exp, new LinearLayout.LayoutParams(-1, -2));
        box.addView(lab);
        box.addView(sv, new LinearLayout.LayoutParams(-1, 0, 1f));
        LinearLayout.LayoutParams clp = new LinearLayout.LayoutParams(-1, -2);
        clp.topMargin = dp(a, 10);
        box.addView(close, clp);

        ov.addView(box, new FrameLayout.LayoutParams(-1, -1));
        root.addView(ov, new FrameLayout.LayoutParams(-1, -1));
        ov.setAlpha(0f);
        ov.animate().alpha(1f).setDuration(250).start();

        remplis(a, liste, root, apres);
    }

    private static void remplis(final Activity a, final LinearLayout liste, final FrameLayout root, final Runnable apres) {
        liste.removeAllViews();
        List<File> items = liste(a);
        if (items.isEmpty()) {
            TextView vide = new TextView(a);
            vide.setText("Aucune sauvegarde.");
            vide.setTypeface(Typeface.MONOSPACE);
            vide.setTextSize(11);
            vide.setTextColor(GREY);
            vide.setPadding(0, dp(a, 8), 0, 0);
            liste.addView(vide);
            return;
        }
        for (final File f : items) {
            LinearLayout ligne = new LinearLayout(a);
            ligne.setOrientation(LinearLayout.VERTICAL);
            ligne.setPadding(dp(a, 10), dp(a, 10), dp(a, 10), dp(a, 10));
            GradientDrawable lg = new GradientDrawable();
            lg.setColor(0xFF101010);
            lg.setCornerRadius(dp(a, 3));
            lg.setStroke(dp(a, 1), 0x33D4AF37);
            ligne.setBackground(lg);
            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(-1, -2);
            llp.bottomMargin = dp(a, 6);
            ligne.setLayoutParams(llp);

            TextView n = new TextView(a);
            n.setText(f.getName());
            n.setTypeface(Typeface.MONOSPACE);
            n.setTextSize(12);
            n.setTextColor(WHITE);

            TextView d = new TextView(a);
            int c = 0;
            File[] fs = f.listFiles();
            if (fs != null) c = fs.length;
            d.setText(c + " fichier(s)  //  toucher pour restaurer");
            d.setTypeface(Typeface.MONOSPACE);
            d.setTextSize(9);
            d.setTextColor(GREY);
            d.setPadding(0, dp(a, 3), 0, 0);

            ligne.addView(n);
            ligne.addView(d);

            ligne.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    new android.app.AlertDialog.Builder(a)
                            .setTitle("Restaurer cette sauvegarde ?")
                            .setMessage("Ecrase les fiches, le stock et le code actuels.")
                            .setPositiveButton("RESTAURER", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dg, int w) {
                                    try {
                                        restaure(a, f);
                                        apres.run();
                                        android.widget.Toast.makeText(a, "Sauvegarde restauree", android.widget.Toast.LENGTH_SHORT).show();
                                    } catch (Exception e) {
                                        android.widget.Toast.makeText(a, "Echec : " + e.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .setNegativeButton("ANNULER", null)
                            .show();
                }
            });

            liste.addView(ligne);
        }
    }

    private static File dir(Activity a) {
        File base = a.getExternalFilesDir(null);
        if (base == null) return null;
        return new File(base, "sauvegardes");
    }

    private static void exporte(Activity a) throws Exception {
        File base = dir(a);
        if (base == null) throw new Exception("Stockage externe indisponible");
        if (!base.exists()) base.mkdirs();
        String nom = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.FRENCH).format(new Date());
        File cible = new File(base, nom);
        if (!cible.exists()) cible.mkdirs();

        File src = a.getFilesDir();
        for (String n : new String[]{"locales.json", "stock.txt"}) {
            File f = new File(src, n);
            if (f.exists()) copie(f, new File(cible, n));
        }
        String code = a.getSharedPreferences("toxbook", Activity.MODE_PRIVATE).getString("code", "");
        if (!code.isEmpty()) {
            FileOutputStream fo = new FileOutputStream(new File(cible, "code.txt"));
            fo.write(code.getBytes("UTF-8"));
            fo.close();
        }
    }

    private static void restaure(Activity a, File src) throws Exception {
        File dst = a.getFilesDir();
        for (String n : new String[]{"locales.json", "stock.txt"}) {
            File f = new File(src, n);
            if (f.exists()) copie(f, new File(dst, n));
        }
        File c = new File(src, "code.txt");
        if (c.exists()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(c), "UTF-8"));
            String code = br.readLine();
            br.close();
            if (code != null && code.trim().length() == 4) {
                a.getSharedPreferences("toxbook", Activity.MODE_PRIVATE).edit()
                        .putString("code", code.trim()).putInt("errs", 0).putLong("lockUntil", 0L).apply();
            }
        }
    }

    private static void copie(File src, File dst) throws Exception {
        FileInputStream in = new FileInputStream(src);
        FileOutputStream out = new FileOutputStream(dst);
        byte[] buf = new byte[8192];
        int n;
        while ((n = in.read(buf)) > 0) out.write(buf, 0, n);
        in.close();
        out.close();
    }

    private static List<File> liste(Activity a) {
        List<File> out = new ArrayList<>();
        File base = dir(a);
        if (base == null) return out;
        File[] fs = base.listFiles();
        if (fs == null) return out;
        for (File f : fs) if (f.isDirectory()) out.add(f);
        Collections.sort(out, new Comparator<File>() {
            public int compare(File x, File y) { return y.getName().compareTo(x.getName()); }
        });
        return out;
    }

    private static void ferme(final FrameLayout ov, final FrameLayout root) {
        ov.animate().alpha(0f).setDuration(250).withEndAction(new Runnable() {
            public void run() { root.removeView(ov); }
        }).start();
    }

    private static int dp(Activity a, int v) {
        return (int) (v * a.getResources().getDisplayMetrics().density);
    }
}