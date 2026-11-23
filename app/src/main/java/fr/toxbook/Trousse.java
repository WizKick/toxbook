package fr.toxbook;

import android.app.Activity;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class Trousse {

    private static final int BG   = 0xFF060606;
    private static final int GOLD = 0xFFD4AF37;
    private static final int GREY = 0xFF8A8A8A;
    private static final int RED  = 0xFFB3202A;
    private static final int WHITE = 0xFFEDEDED;

    private static final String[][] ITEMS = {
        {"CHARBON ACTIVE", "Adsorbe la plupart des toxiques organiques. A prendre dans l'heure.",
         "INUTILE OU NOCIF sur : caustiques (Javel, ammoniaque), metaux (fer, lithium), alcools legers, hydrocarbures. Ne jamais donner sur un coma non protege."},
        {"EAU", "Dilution des caustiques et des irritants digestifs.",
         "A eviter sur le phosphure (aggrave le degagement de phosphine). Petites gorgees si troubles de conscience."},
        {"LAIT", "Alternative de dilution, mieux tolere chez l'enfant.",
         "Contre-productif sur les hydrocarbures : les lipides augmentent l'absorption pulmonaire."},
        {"GLUCAGON", "Hypoglycemie profonde sous insuline, bradycardie des betabloquants.",
         "Peu efficace si les reserves hepatiques sont vides. Ne dispense pas du glucose IV."},
        {"NALOXONE", "Antagoniste des opioides. Redresse la depression respiratoire.",
         "Declenche un sevrage brutal chez le dependant. Demi-vie courte : risque de renarcotisation."},
        {"FLUMAZENIL", "Antagoniste des benzodiazepines.",
         "DANGEREUX si co-intoxication par tricyclique ou epileptique : convulsions."},
        {"BICARBONATE DE SODIUM", "Tricycliques (elargissement du QRS), salicyles.",
         "A manipuler avec precaution : alcalose, hypokaliemie."},
        {"OXYGENE", "Monoxyde de carbone, cyanure, tous les asphyxiants.",
         "Prudence sur les BPCO avances (risque d'hypercapnie)."},
        {"ANTICORPS ANTIDIGOXINE", "Seul traitement de l'intoxication digitalique grave.",
         "Dose inconnue = avis medical imperatif."},
        {"HYDROXOCOBALAMINE", "Cyanure. Fixe le CN sans le rendre toxique.",
         "Ne pas confondre avec la vitamine B12 orale, sans effet ici."},
        {"VITAMINE K", "Antagonistes des anticoagulents (AVK).",
         "Sans effet sur les anticoagulants recents (AOD)."},
        {"DEFEROXAMINE", "Chélateur du fer.",
         "Risque de choc anaphylactique : surveillance."},
        {"BLEU DE METHYLENE", "Methémoglobinisants (nitrites, dapsone, anesthesiques).",
         "Contre-indique chez le deficient en G6PD."},
        {"ANTITOXINE BOTULIQUE", "Botulisme. Neutralise la toxine circulante.",
         "Sans effet sur la toxine deja fixee : ne remplace pas la ventilation."},
        {"FOMEPIZOLE ou ETHANOL", "Methanol, ethylene glycol. Bloque la metabolisation toxique.",
         "Alcool : attention a l'hypoglycemie."},
        {"ATROPINE", "Syndrome cholinergique (organophosphores). 1 a 2 mg IV, a renouveler.",
         "Titrer sur l assechement des secretions. Pas de dose limite."},
        {"PRALIDOXIME", "Reactivateur des cholinestarases. Organophosphores, le plus tot possible.",
         "Inefficace apres 48 h. Inutile sur les carbamates."},
        {"PHYSOSTIGMINE", "Syndrome anticholinergique central. 1 a 2 mg IV lente.",
         "Contre-indique si bloc auriculo-ventriculaire ou QRS large."}
    };

    public static void show(final Activity a, final FrameLayout root) { showFor(a, root, null); }

    public static void showFor(final Activity a, final FrameLayout root, final String filtre) {
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
        stamp.setText("TROUSSE DE TERRAIN  //  ADULTE");
        stamp.setTypeface(Typeface.MONOSPACE);
        stamp.setTextSize(9);
        stamp.setLetterSpacing(0.3f);
        stamp.setTextColor(RED);

        TextView title = new TextView(a);
        title.setText("ANTIDOTES");
        title.setTypeface(Typeface.create("serif", Typeface.BOLD));
        title.setTextSize(24);
        title.setTextColor(GOLD);
        title.setPadding(0, dp(a, 8), 0, dp(a, 10));

        View rule = new View(a);
        GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{GOLD, 0x00D4AF37});
        rule.setBackground(gd);
        LinearLayout.LayoutParams rlp = new LinearLayout.LayoutParams(-1, dp(a, 1));
        rlp.bottomMargin = dp(a, 12);

        LinearLayout body = new LinearLayout(a);
        body.setOrientation(LinearLayout.VERTICAL);

        for (String[] it : ITEMS) {
            if (filtre != null && !it[0].equalsIgnoreCase(filtre)) continue;
            TextView n = new TextView(a);
            n.setText(it[0]);
            n.setTypeface(Typeface.MONOSPACE);
            n.setTextSize(12);
            n.setLetterSpacing(0.1f);
            n.setTextColor(WHITE);
            n.setPadding(0, dp(a, 12), 0, dp(a, 3));

            TextView u = new TextView(a);
            u.setText(it[1]);
            u.setTextSize(13);
            u.setTextColor(0xFFC8C8C8);

            TextView c = new TextView(a);
            c.setText(it[2]);
            c.setTextSize(12);
            c.setTextColor(RED);
            c.setLineSpacing(dp(a, 2), 1f);
            c.setPadding(0, dp(a, 4), 0, dp(a, 6));

            body.addView(n);
            body.addView(u);
            body.addView(c);
        }

        TextView foot = new TextView(a);
        foot.setText("La trousse ne remplace pas l'appel au 15. Toute dose se valide avec la regulation.");
        foot.setTypeface(Typeface.MONOSPACE);
        foot.setTextSize(9);
        foot.setTextColor(GREY);
        foot.setLineSpacing(dp(a, 2), 1f);
        foot.setPadding(0, dp(a, 14), 0, dp(a, 10));

        body.addView(foot);

        ScrollView sv = new ScrollView(a);
        sv.addView(body);

        TextView close = new TextView(a);
        close.setText("REFERMER LA TROUSSE");
        close.setTypeface(Typeface.MONOSPACE);
        close.setTextSize(12);
        close.setLetterSpacing(0.15f);
        close.setGravity(Gravity.CENTER);
        close.setTextColor(0xFF060606);
        close.setPadding(dp(a, 14), dp(a, 13), dp(a, 14), dp(a, 13));
        GradientDrawable cb = new GradientDrawable();
        cb.setColor(GOLD);
        cb.setCornerRadius(dp(a, 3));
        close.setBackground(cb);
        close.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { ov.animate().alpha(0f).setDuration(250).withEndAction(new Runnable() { public void run() { root.removeView(ov); } }).start(); }
        });

        box.addView(stamp);
        box.addView(title);
        if (filtre != null) box.addView(subNote(a, filtre));
        box.addView(rule, rlp);
        box.addView(sv, new LinearLayout.LayoutParams(-1, 0, 1f));
        box.addView(close, new LinearLayout.LayoutParams(-1, -2));

        ov.addView(box, new FrameLayout.LayoutParams(-1, -1));
        root.addView(ov, new FrameLayout.LayoutParams(-1, -1));
        ov.setAlpha(0f);
        ov.animate().alpha(1f).setDuration(250).start();
    }

    private static TextView subNote(Activity a, String f) {
        TextView s = new TextView(a);
        s.setText("ANTIDOTE LIE  //  " + f);
        s.setTypeface(Typeface.MONOSPACE);
        s.setTextSize(9);
        s.setTextColor(GREY);
        s.setPadding(0, 0, 0, dp(a, 6));
        return s;
    }

    private static int dp(Activity a, int v) {
        return (int) (v * a.getResources().getDisplayMetrics().density);
    }
}
