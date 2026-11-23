package fr.toxbook;

import android.app.Activity;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.LinkedHashMap;
import java.util.Map;

public class Fiche {

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
        stamp.setText("NOUVEAU DOSSIER  //  SAISIE MANUELLE");
        stamp.setTypeface(Typeface.MONOSPACE);
        stamp.setTextSize(9);
        stamp.setLetterSpacing(0.3f);
        stamp.setTextColor(RED);

        TextView title = new TextView(a);
        title.setText("CREER UNE FICHE");
        title.setTypeface(Typeface.create("serif", Typeface.BOLD));
        title.setTextSize(22);
        title.setTextColor(GOLD);
        title.setPadding(0, dp(a, 8), 0, dp(a, 12));

        final Map<String, EditText> champs = new LinkedHashMap<>();
        final java.util.Set<String> sel = new java.util.HashSet<>();
        LinearLayout body = new LinearLayout(a);
        body.setOrientation(LinearLayout.VERTICAL);

        ajoute(a, body, champs, "n",  "NOM", false);
        ajoute(a, body, champs, "c",  "CATEGORIE", false);
        ajoute(a, body, champs, "a",  "ACCES", false);
        ajoute(a, body, champs, "d",  "DOSE / SEUIL", true);
        ajoute(a, body, champs, "t",  "DELAI D'ACTION", false);
        ajoute(a, body, champs, "e",  "EFFETS", true);
        ajoute(a, body, champs, "s",  "SIGNES CLINIQUES", true);
        ajoute(a, body, champs, "an", "ANTIDOTE / TRAITEMENT", true);
        ajoute(a, body, champs, "m",  "MELANGE / REACTION", true);
        ajoute(a, body, champs, "no", "NOTES", true);

        TextView lTags = new TextView(a);
        lTags.setText("TAGS (AU MOINS UN)");
        lTags.setTypeface(Typeface.MONOSPACE);
        lTags.setTextSize(9);
        lTags.setLetterSpacing(0.25f);
        lTags.setTextColor(GOLD);
        lTags.setPadding(0, dp(a, 12), 0, dp(a, 6));
        body.addView(lTags);

        String[] dispo = {"MORT","VOMI","CARDIAQUE","COMA","NEURO","CONVULS","HEMO",
                "RESPIR","CAUSTIQUE","HALLU","CECITE","ACIDOSE","HYPOGLY",
                "DIGESTIF","FOIE","REIN","INFLAM","DIVERS"};

        LinearLayout zoneTags = new LinearLayout(a);
        zoneTags.setOrientation(LinearLayout.VERTICAL);
        LinearLayout ligneTags = null;
        for (int i = 0; i < dispo.length; i++) {
            if (i % 3 == 0) {
                ligneTags = new LinearLayout(a);
                ligneTags.setOrientation(LinearLayout.HORIZONTAL);
                zoneTags.addView(ligneTags);
            }
            final String tag = dispo[i];
            final TextView p = puce(a, tag);
            p.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (sel.contains(tag)) sel.remove(tag); else sel.add(tag);
                    paintPuce(a, p, sel.contains(tag));
                }
            });
            ligneTags.addView(p);
        }
        body.addView(zoneTags);

        ScrollView sv = new ScrollView(a);
        sv.addView(body);

        LinearLayout bar = new LinearLayout(a);
        bar.setOrientation(LinearLayout.HORIZONTAL);

        TextView annul = new TextView(a);
        annul.setText("ANNULER");
        annul.setTypeface(Typeface.MONOSPACE);
        annul.setTextSize(11);
        annul.setGravity(Gravity.CENTER);
        annul.setTextColor(GREY);
        annul.setPadding(dp(a, 10), dp(a, 13), dp(a, 10), dp(a, 13));
        GradientDrawable ab = new GradientDrawable();
        ab.setColor(0xFF101010);
        ab.setCornerRadius(dp(a, 3));
        ab.setStroke(dp(a, 1), 0x44D4AF37);
        annul.setBackground(ab);
        annul.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { ferme(ov, root); }
        });

        TextView ok = new TextView(a);
        ok.setText("ENREGISTRER");
        ok.setTypeface(Typeface.MONOSPACE);
        ok.setTextSize(11);
        ok.setLetterSpacing(0.1f);
        ok.setGravity(Gravity.CENTER);
        ok.setTextColor(0xFF060606);
        ok.setPadding(dp(a, 14), dp(a, 13), dp(a, 14), dp(a, 13));
        GradientDrawable ob = new GradientDrawable();
        ob.setColor(GOLD);
        ob.setCornerRadius(dp(a, 3));
        ok.setBackground(ob);
        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String nom = champs.get("n").getText().toString().trim();
                if (nom.isEmpty()) {
                    android.widget.Toast.makeText(a, "Le nom est obligatoire", android.widget.Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    JSONObject o = new JSONObject();
                    for (Map.Entry<String, EditText> en : champs.entrySet()) {
                        String val = en.getValue().getText().toString().trim();
                        o.put(en.getKey(), val.isEmpty() ? "—" : val);
                    }
                    o.put("di", 3);
                    o.put("st", false);
                    org.json.JSONArray tg = new org.json.JSONArray();
                    for (String s : sel) tg.put(s);
                    if (tg.length() == 0) tg.put("DIVERS");
                    o.put("tg", tg);
                    o.put("local", true);
                    ecrit(a, o);
                    ferme(ov, root);
                    apres.run();
                } catch (Exception e) {
                    android.widget.Toast.makeText(a, "Echec : " + e.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
                }
            }
        });

        LinearLayout.LayoutParams blp = new LinearLayout.LayoutParams(0, -2, 1f);
        blp.rightMargin = dp(a, 8);
        bar.addView(annul, blp);
        bar.addView(ok, new LinearLayout.LayoutParams(0, -2, 1.6f));

        box.addView(stamp);
        box.addView(title);
        box.addView(sv, new LinearLayout.LayoutParams(-1, 0, 1f));
        box.addView(bar, new LinearLayout.LayoutParams(-1, -2));

        ov.addView(box, new FrameLayout.LayoutParams(-1, -1));
        root.addView(ov, new FrameLayout.LayoutParams(-1, -1));
        ov.setAlpha(0f);
        ov.animate().alpha(1f).setDuration(250).start();
    }

    private static TextView puce(Activity a, String label) {
        TextView c = new TextView(a);
        c.setText(label);
        c.setTypeface(Typeface.MONOSPACE);
        c.setTextSize(9);
        c.setLetterSpacing(0.1f);
        c.setGravity(Gravity.CENTER);
        c.setPadding(dp(a, 6), dp(a, 7), dp(a, 6), dp(a, 7));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, -2, 1f);
        lp.setMargins(dp(a, 2), dp(a, 2), dp(a, 2), dp(a, 2));
        c.setLayoutParams(lp);
        paintPuce(a, c, false);
        return c;
    }

    private static void paintPuce(Activity a, TextView c, boolean on) {
        GradientDrawable d = new GradientDrawable();
        d.setCornerRadius(dp(a, 3));
        d.setStroke(dp(a, 1), on ? GOLD : 0x33D4AF37);
        d.setColor(on ? 0x22D4AF37 : 0xFF0C0C0C);
        c.setBackground(d);
        c.setTextColor(on ? GOLD : GREY);
    }

    private static void ajoute(Activity a, LinearLayout parent, Map<String, EditText> m,
                               String cle, String label, boolean multiligne) {
        TextView l = new TextView(a);
        l.setText(label);
        l.setTypeface(Typeface.MONOSPACE);
        l.setTextSize(9);
        l.setLetterSpacing(0.25f);
        l.setTextColor(GOLD);
        l.setPadding(0, dp(a, 10), 0, dp(a, 3));

        EditText e = new EditText(a);
        e.setTextColor(WHITE);
        e.setTypeface(Typeface.MONOSPACE);
        e.setTextSize(13);
        e.setHint(label.toLowerCase());
        e.setHintTextColor(0xFF4A4A4A);
        e.setPadding(dp(a, 10), dp(a, 8), dp(a, 10), dp(a, 8));
        if (multiligne) {
            e.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            e.setMinLines(2);
            e.setGravity(Gravity.TOP);
        } else {
            e.setSingleLine(true);
        }
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(0xFF101010);
        bg.setCornerRadius(dp(a, 3));
        bg.setStroke(dp(a, 1), 0x33D4AF37);
        e.setBackground(bg);

        parent.addView(l);
        parent.addView(e, new LinearLayout.LayoutParams(-1, -2));
        m.put(cle, e);
    }

    private static void ecrit(Activity a, JSONObject o) throws Exception {
        File f = new File(a.getFilesDir(), "locales.json");
        org.json.JSONArray arr = new org.json.JSONArray();
        if (f.exists()) {
            java.io.BufferedReader br = new java.io.BufferedReader(
                    new java.io.InputStreamReader(new java.io.FileInputStream(f), "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String l;
            while ((l = br.readLine()) != null) sb.append(l);
            br.close();
            if (sb.length() > 0) arr = new org.json.JSONArray(sb.toString());
        }
        arr.put(o);
        FileOutputStream fo = new FileOutputStream(f);
        fo.write(arr.toString().getBytes("UTF-8"));
        fo.close();
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