package fr.toxbook;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Lock {

    private static final int BG   = 0xFF060606;
    private static final int GOLD = 0xFFD4AF37;
    private static final int GREY = 0xFF8A8A8A;
    private static final int RED  = 0xFFB3202A;

    private static final long[] DELAYS = {10000L, 60000L, 180000L, 300000L, 600000L, 3600000L};

    private static Handler handler;
    private static Runnable ticker;

    public static void show(final Activity a, final FrameLayout root) {
        final SharedPreferences sp = a.getSharedPreferences("toxbook", Context.MODE_PRIVATE);
        final String saved = sp.getString("code", "");
        final boolean setup = saved.isEmpty();

        final StringBuilder buf = new StringBuilder();
        final List<TextView> keys = new ArrayList<>();

        final FrameLayout ov = new FrameLayout(a);
        ov.setBackgroundColor(BG);
        ov.setClickable(true);
        ov.setFocusable(true);
        ov.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, android.view.MotionEvent e) { return true; }
        });

        LinearLayout col = new LinearLayout(a);
        col.setOrientation(LinearLayout.VERTICAL);
        col.setGravity(Gravity.CENTER);
        col.setPadding(dp(a, 30), 0, dp(a, 30), 0);

        TextView stamp = new TextView(a);
        stamp.setText("ACCES RESTREINT");
        stamp.setTypeface(Typeface.MONOSPACE);
        stamp.setTextSize(10);
        stamp.setLetterSpacing(0.35f);
        stamp.setTextColor(RED);
        stamp.setGravity(Gravity.CENTER);

        TextView title = new TextView(a);
        title.setText(setup ? "DEFINIR LE CODE" : "CODE A 4 CHIFFRES");
        title.setTypeface(Typeface.create("serif", Typeface.BOLD));
        title.setTextSize(20);
        title.setTextColor(GOLD);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, dp(a, 10), 0, dp(a, 20));

        final TextView dots = new TextView(a);
        dots.setTypeface(Typeface.MONOSPACE);
        dots.setTextSize(26);
        dots.setTextColor(GOLD);
        dots.setLetterSpacing(0.4f);
        dots.setGravity(Gravity.CENTER);
        dots.setPadding(0, 0, 0, dp(a, 20));

        final TextView msg = new TextView(a);
        msg.setTypeface(Typeface.MONOSPACE);
        msg.setTextSize(10);
        msg.setTextColor(GREY);
        msg.setGravity(Gravity.CENTER);
        msg.setMinHeight(dp(a, 28));
        msg.setPadding(0, 0, 0, dp(a, 10));

        col.addView(stamp);
        col.addView(title);
        col.addView(dots);
        col.addView(msg);

        LinearLayout pad = new LinearLayout(a);
        pad.setOrientation(LinearLayout.VERTICAL);
        pad.setGravity(Gravity.CENTER);

        String[][] rows = {{"1","2","3"},{"4","5","6"},{"7","8","9"},{"C","0","OK"}};
        for (String[] row : rows) {
            LinearLayout r = new LinearLayout(a);
            r.setOrientation(LinearLayout.HORIZONTAL);
            r.setGravity(Gravity.CENTER);
            for (String k : row) {
                TextView b = new TextView(a);
                b.setText(k);
                b.setTypeface(Typeface.MONOSPACE);
                b.setTextSize(18);
                b.setTextColor(GOLD);
                b.setGravity(Gravity.CENTER);
                GradientDrawable bg = new GradientDrawable();
                bg.setCornerRadius(dp(a, 3));
                bg.setStroke(dp(a, 1), 0x44D4AF37);
                bg.setColor(0xFF0E0E0E);
                b.setBackground(bg);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dp(a, 64), dp(a, 52));
                lp.setMargins(dp(a, 4), dp(a, 4), dp(a, 4), dp(a, 4));
                b.setLayoutParams(lp);
                b.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        press(a, sp, setup, saved, buf, dots, msg, keys, ov, root, ((TextView) v).getText().toString());
                    }
                });
                keys.add(b);
                r.addView(b);
            }
            pad.addView(r);
        }
        col.addView(pad);
        ov.addView(col, new FrameLayout.LayoutParams(-1, -1));
        root.addView(ov, new FrameLayout.LayoutParams(-1, -1));

        refresh(dots, buf);
        tick(a, sp, keys, msg, dots, buf);
    }

    private static void press(Activity a, SharedPreferences sp, boolean setup, String saved,
                              StringBuilder buf, TextView dots, TextView msg, List<TextView> keys,
                              FrameLayout ov, FrameLayout root, String k) {
        if (sp.getLong("lockUntil", 0L) > System.currentTimeMillis()) return;

        if (k.equals("C")) {
            buf.setLength(0);
            msg.setText("");
            refresh(dots, buf);
            return;
        }

        if (k.equals("OK")) {
            if (buf.length() < 4) { msg.setText("CODE INCOMPLET"); return; }
            check(a, sp, setup, saved, buf, dots, msg, keys, ov, root);
            return;
        }

        if (buf.length() >= 4) return;
        buf.append(k);
        msg.setText("");
        refresh(dots, buf);
    }

    private static void check(Activity a, SharedPreferences sp, boolean setup, String saved,
                              StringBuilder buf, TextView dots, TextView msg, List<TextView> keys,
                              FrameLayout ov, FrameLayout root) {
        String c = buf.toString();

        if (setup) {
            sp.edit().putString("code", c).putInt("errs", 0).putLong("lockUntil", 0L).apply();
            done(ov, root);
            return;
        }

        if (c.equals(saved)) {
            sp.edit().putInt("errs", 0).putLong("lockUntil", 0L).apply();
            done(ov, root);
            return;
        }

        int errs = sp.getInt("errs", 0) + 1;
        long delay = DELAYS[Math.min(errs - 1, DELAYS.length - 1)];
        sp.edit().putInt("errs", errs)
                .putLong("lockUntil", System.currentTimeMillis() + delay).apply();
        buf.setLength(0);
        refresh(dots, buf);
        tick(a, sp, keys, msg, dots, buf);
    }

    private static void tick(final Activity a, final SharedPreferences sp, final List<TextView> keys,
                             final TextView msg, final TextView dots, final StringBuilder buf) {
        if (handler != null && ticker != null) handler.removeCallbacks(ticker);
        handler = new Handler(Looper.getMainLooper());
        ticker = new Runnable() {
            public void run() {
                long left = sp.getLong("lockUntil", 0L) - System.currentTimeMillis();
                if (left > 0) {
                    int s = (int) Math.ceil(left / 1000.0);
                    msg.setText("VERROUILLE  " + String.format(Locale.FRENCH, "%02d:%02d", s / 60, s % 60)
                            + "  //  " + sp.getInt("errs", 0) + " ERREUR(S)");
                    for (TextView k : keys) { k.setEnabled(false); k.setAlpha(0.3f); }
                    buf.setLength(0);
                    refresh(dots, buf);
                    handler.postDelayed(ticker, 500);
                } else {
                    msg.setText("");
                    for (TextView k : keys) { k.setEnabled(true); k.setAlpha(1f); }
                }
            }
        };
        handler.post(ticker);
    }

    private static void refresh(TextView dots, StringBuilder buf) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < 4; i++) s.append(i < buf.length() ? "\u25C6" : "\u25C7");
        dots.setText(s.toString());
    }

    private static void done(FrameLayout ov, FrameLayout root) {
        if (handler != null && ticker != null) handler.removeCallbacks(ticker);
        ov.animate().alpha(0f).setDuration(400).withEndAction(new Runnable() {
            public void run() { root.removeView(ov); }
        }).start();
    }

    private static int dp(Activity a, int v) {
        return (int) (v * a.getResources().getDisplayMetrics().density);
    }
}