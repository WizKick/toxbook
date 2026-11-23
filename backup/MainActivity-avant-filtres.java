package fr.toxbook;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {

    private static final int BG      = 0xFF060606;
    private static final int CARD    = 0xFF101010;
    private static final int CARD_ON = 0xFF1A1A1A;
    private static final int GOLD    = 0xFFD4AF37;
    private static final int GOLD_D  = 0x55D4AF37;
    private static final int WHITE   = 0xFFEDEDED;
    private static final int GREY    = 0xFF8A8A8A;
    private static final int RED     = 0xFFB3202A;

    private final List<JSONObject> all = new ArrayList<>();
    private final List<JSONObject> shown = new ArrayList<>();
    private BaseAdapter adapter;
    private TextView status;
    private ListView list;
    private Typeface serif;
    private Typeface mono;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        serif = Typeface.create("serif", Typeface.NORMAL);
        mono = Typeface.MONOSPACE;

        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(BG);

        LinearLayout col = new LinearLayout(this);
        col.setOrientation(LinearLayout.VERTICAL);
        root.addView(col, new FrameLayout.LayoutParams(-1, -1));

        col.addView(buildHeader());
        col.addView(buildSearch());

        list = new ListView(this);
        list.setDivider(null);
        list.setDividerHeight(0);
        list.setPadding(dp(14), dp(6), dp(14), dp(14));
        list.setClipToPadding(false);
        list.setSelector(android.R.color.transparent);
        col.addView(list, new LinearLayout.LayoutParams(-1, 0, 1f));

        adapter = new DossierAdapter();
        list.setAdapter(adapter);

        setContentView(root);

        loadData();
        filter("");

        list.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            public void onItemClick(android.widget.AdapterView<?> p, View v, int pos, long id) {
                if (pos < shown.size()) open(shown.get(pos));
            }
        });

        final FrameLayout overlay = new FrameLayout(this);
        overlay.setBackgroundColor(BG);
        root.addView(overlay, new FrameLayout.LayoutParams(-1, -1));
        playIntro(overlay);
    }

    private View buildHeader() {
        LinearLayout h = new LinearLayout(this);
        h.setOrientation(LinearLayout.VERTICAL);
        h.setPadding(dp(18), dp(22), dp(18), dp(12));

        TextView kicker = new TextView(this);
        kicker.setText("DOSSIERS CLASSES");
        kicker.setTypeface(mono);
        kicker.setTextSize(10);
        kicker.setLetterSpacing(0.35f);
        kicker.setTextColor(RED);

        TextView title = new TextView(this);
        title.setText("TOXBOOK");
        title.setTypeface(Typeface.create("serif", Typeface.BOLD));
        title.setTextSize(34);
        title.setLetterSpacing(0.12f);
        title.setTextColor(GOLD);

        View rule = new View(this);
        LinearLayout.LayoutParams rlp = new LinearLayout.LayoutParams(-1, dp(1));
        rlp.topMargin = dp(10);
        GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{GOLD, 0x00D4AF37});
        rule.setBackground(gd);
        rule.setLayoutParams(rlp);

        status = new TextView(this);
        status.setTypeface(mono);
        status.setTextSize(10);
        status.setTextColor(GREY);
        status.setPadding(0, dp(8), 0, 0);

        h.addView(kicker);
        h.addView(title);
        h.addView(rule);
        h.addView(status);
        return h;
    }

    private View buildSearch() {
        LinearLayout wrap = new LinearLayout(this);
        wrap.setOrientation(LinearLayout.VERTICAL);
        wrap.setPadding(dp(18), 0, dp(18), dp(10));

        EditText e = new EditText(this);
        e.setHint("RECHERCHER : poison, effet, antidote...");
        e.setHintTextColor(0xFF5A5A5A);
        e.setTextColor(WHITE);
        e.setTypeface(mono);
        e.setTextSize(13);
        e.setSingleLine(true);
        e.setBackground(searchBg());
        e.setPadding(dp(14), dp(12), dp(14), dp(12));
        e.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            public void onTextChanged(CharSequence s, int a, int b, int c) { filter(s.toString()); }
            public void afterTextChanged(Editable s) {}
        });
        wrap.addView(e, new LinearLayout.LayoutParams(-1, -2));
        return wrap;
    }

    private GradientDrawable searchBg() {
        GradientDrawable d = new GradientDrawable();
        d.setColor(CARD);
        d.setCornerRadius(dp(4));
        d.setStroke(dp(1), GOLD_D);
        return d;
    }    private int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density);
    }

    private void loadData() {
        try {
            InputStream is = getAssets().open("poisons.json");
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
            br.close();
            JSONArray arr = new JSONArray(sb.toString());
            for (int i = 0; i < arr.length(); i++) all.add(arr.getJSONObject(i));
            status.setText(all.size() + " DOSSIERS  //  ADULTE 70 KG  //  DONNEES INDICATIVES");
        } catch (Exception e) {
            status.setText("ERREUR : " + e.getMessage());
        }
    }

    private void filter(String q) {
        String s = q.toLowerCase(Locale.FRENCH).trim();
        shown.clear();
        for (JSONObject o : all) {
            String hay = (o.optString("n") + " " + o.optString("c") + " " + o.optString("a") + " "
                    + o.optString("e") + " " + o.optString("s") + " " + o.optString("an") + " "
                    + o.optString("m") + " " + o.optString("no")).toLowerCase(Locale.FRENCH);
            if (s.isEmpty() || hay.contains(s)) shown.add(o);
        }
        adapter.notifyDataSetChanged();
    }

    private class DossierAdapter extends BaseAdapter {
        public int getCount() { return shown.size(); }
        public Object getItem(int p) { return shown.get(p); }
        public long getItemId(int p) { return p; }

        public View getView(int pos, View reuse, ViewGroup parent) {
            JSONObject o = shown.get(pos);

            LinearLayout card = new LinearLayout(MainActivity.this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(dp(14), dp(12), dp(14), dp(12));

            StateListDrawable sld = new StateListDrawable();
            GradientDrawable off = new GradientDrawable();
            off.setColor(CARD);
            off.setCornerRadius(dp(3));
            off.setStroke(dp(1), 0x22D4AF37);
            GradientDrawable on = new GradientDrawable();
            on.setColor(CARD_ON);
            on.setCornerRadius(dp(3));
            on.setStroke(dp(1), GOLD);
            sld.addState(new int[]{android.R.attr.state_pressed}, on);
            sld.addState(new int[]{}, off);
            card.setBackground(sld);

            LinearLayout top = new LinearLayout(MainActivity.this);
            top.setOrientation(LinearLayout.HORIZONTAL);
            top.setGravity(Gravity.CENTER_VERTICAL);

            TextView ref = new TextView(MainActivity.this);
            ref.setText(String.format(Locale.FRENCH, "%03d", pos + 1));
            ref.setTypeface(mono);
            ref.setTextSize(10);
            ref.setTextColor(GOLD);
            ref.setPadding(0, 0, dp(10), 0);

            TextView name = new TextView(MainActivity.this);
            name.setText(o.optString("n"));
            name.setTypeface(Typeface.create("serif", Typeface.BOLD));
            name.setTextSize(16);
            name.setTextColor(WHITE);
            name.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1f));

            top.addView(ref);
            top.addView(name);

            LinearLayout meta = new LinearLayout(MainActivity.this);
            meta.setOrientation(LinearLayout.HORIZONTAL);
            meta.setPadding(0, dp(5), 0, 0);

            TextView cat = new TextView(MainActivity.this);
            cat.setText(o.optString("c").toUpperCase(Locale.FRENCH));
            cat.setTypeface(mono);
            cat.setTextSize(9);
            cat.setTextColor(RED);
            cat.setLetterSpacing(0.2f);

            TextView acc = new TextView(MainActivity.this);
            acc.setText("   //   " + o.optString("a"));
            acc.setTypeface(mono);
            acc.setTextSize(9);
            acc.setTextColor(GREY);

            meta.addView(cat);
            meta.addView(acc);

            card.addView(top);
            card.addView(meta);

            LinearLayout holder = new LinearLayout(MainActivity.this);
            holder.setOrientation(LinearLayout.VERTICAL);
            holder.setPadding(0, dp(5), 0, 0);
            holder.addView(card, new LinearLayout.LayoutParams(-1, -2));
            return holder;
        }
    }    private void open(JSONObject o) {
        Dialog d = new Dialog(this);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);

        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setBackgroundColor(0xFF0A0A0A);
        box.setPadding(dp(20), dp(20), dp(20), dp(16));

        TextView stamp = new TextView(this);
        stamp.setText("CONFIDENTIEL  //  DIFFUSION RESTREINTE");
        stamp.setTypeface(mono);
        stamp.setTextSize(9);
        stamp.setLetterSpacing(0.3f);
        stamp.setTextColor(RED);

        TextView name = new TextView(this);
        name.setText(o.optString("n"));
        name.setTypeface(Typeface.create("serif", Typeface.BOLD));
        name.setTextSize(24);
        name.setTextColor(GOLD);
        name.setPadding(0, dp(8), 0, dp(8));

        View rule = new View(this);
        GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{GOLD, 0x00D4AF37});
        rule.setBackground(gd);

        LinearLayout body = new LinearLayout(this);
        body.setOrientation(LinearLayout.VERTICAL);

        add(body, "CATEGORIE", o.optString("c").toUpperCase(Locale.FRENCH));
        add(body, "ACCES", o.optString("a"));
        add(body, "DOSE / SEUIL", o.optString("d"));
        add(body, "DELAI D'ACTION", o.optString("t"));
        add(body, "EFFETS", o.optString("e"));
        add(body, "SIGNES CLINIQUES", o.optString("s"));
        add(body, "ANTIDOTE / TRAITEMENT", o.optString("an"));
        add(body, "MELANGE / REACTION", o.optString("m"));
        add(body, "NOTES", o.optString("no"));

        ScrollView sv = new ScrollView(this);
        sv.addView(body);

        TextView close = new TextView(this);
        close.setText("FERMER LE DOSSIER");
        close.setTypeface(mono);
        close.setTextSize(12);
        close.setLetterSpacing(0.15f);
        close.setGravity(Gravity.CENTER);
        close.setTextColor(0xFF060606);
        close.setPadding(dp(14), dp(13), dp(14), dp(13));
        GradientDrawable cb = new GradientDrawable();
        cb.setColor(GOLD);
        cb.setCornerRadius(dp(3));
        close.setBackground(cb);
        close.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { d.dismiss(); }
        });

        LinearLayout.LayoutParams rlp = new LinearLayout.LayoutParams(-1, dp(1));
        rlp.topMargin = dp(4);
        rlp.bottomMargin = dp(12);

        box.addView(stamp);
        box.addView(name);
        box.addView(rule, rlp);
        box.addView(sv, new LinearLayout.LayoutParams(-1, 0, 1f));
        box.addView(close, new LinearLayout.LayoutParams(-1, -2));

        d.setContentView(box);
        Window w = d.getWindow();
        if (w != null) {
            w.setBackgroundDrawableResource(android.R.color.black);
            w.setLayout(-1, -1);
        }
        d.show();
    }

    private void add(LinearLayout parent, String label, String value) {
        if (value == null || value.trim().isEmpty() || value.trim().equals("—")) return;

        TextView l = new TextView(this);
        l.setText(label);
        l.setTypeface(mono);
        l.setTextSize(9);
        l.setLetterSpacing(0.25f);
        l.setTextColor(GOLD_D);
        l.setPadding(0, dp(12), 0, dp(3));

        TextView v = new TextView(this);
        v.setText(value);
        v.setTextSize(14);
        v.setTextColor(0xFFDADADA);
        v.setLineSpacing(dp(3), 1f);

        parent.addView(l);
        parent.addView(v);
    }

    private void playIntro(final FrameLayout overlay) {
        final TextView gun = new TextView(this);
        gun.setText("\u25CF");
        gun.setTextSize(90);
        gun.setTextColor(0xFF151515);
        gun.setGravity(Gravity.CENTER);
        gun.setAlpha(0f);
        overlay.addView(gun, new FrameLayout.LayoutParams(-1, -1));

        final TextView zero = new TextView(this);
        zero.setText("007");
        zero.setTypeface(Typeface.create("serif", Typeface.BOLD));
        zero.setTextSize(64);
        zero.setTextColor(GOLD);
        zero.setGravity(Gravity.CENTER);
        zero.setLetterSpacing(0.2f);
        zero.setAlpha(0f);
        overlay.addView(zero, new FrameLayout.LayoutParams(-1, -1));

        final TextView sub = new TextView(this);
        sub.setText("T O X B O O K");
        sub.setTypeface(mono);
        sub.setTextSize(12);
        sub.setLetterSpacing(0.5f);
        sub.setTextColor(WHITE);
        sub.setGravity(Gravity.CENTER);
        sub.setAlpha(0f);
        FrameLayout.LayoutParams slp = new FrameLayout.LayoutParams(-1, -2);
        slp.gravity = Gravity.CENTER;
        slp.topMargin = dp(120);
        overlay.addView(sub, slp);

        gun.animate().alpha(1f).setDuration(500).start();
        zero.animate().alpha(1f).setStartDelay(300).setDuration(500).start();
        sub.animate().alpha(1f).setStartDelay(700).setDuration(400).start();

        overlay.postDelayed(new Runnable() {
            public void run() {
                overlay.animate().alpha(0f).setDuration(600).withEndAction(new Runnable() {
                    public void run() {
                        overlay.setVisibility(View.GONE);
                    }
                }).start();
            }
        }, 1900);
    }
}