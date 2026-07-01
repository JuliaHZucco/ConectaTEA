package com.example.conectaTEA;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class NotificationsActivity extends BaseActivity {

    private LinearLayout containerNotifications;
    private TextView tvNoNotif;
    private ListenerRegistration notifListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        setupBackButton();

        containerNotifications = findViewById(R.id.containerNotifications);
        tvNoNotif = findViewById(R.id.tvNoNotif);

        loadNotificationsRealtime();
    }

    private void loadNotificationsRealtime() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        notifListener = FirebaseFirestore.getInstance().collection("notifications")
                .whereEqualTo("userId", uid)
                .addSnapshotListener((value, error) -> {
                    containerNotifications.removeAllViews();
                    if (error != null) {
                        tvNoNotif.setVisibility(View.VISIBLE);
                        tvNoNotif.setText("Erro ao carregar notificações.");
                        return;
                    }

                    if (value == null || value.isEmpty()) {
                        tvNoNotif.setVisibility(View.VISIBLE);
                        tvNoNotif.setText("Você não possui notificações no momento.");
                    } else {
                        tvNoNotif.setVisibility(View.GONE);
                        
                        java.util.List<QueryDocumentSnapshot> sortedDocs = new java.util.ArrayList<>();
                        for (QueryDocumentSnapshot doc : value) {
                            sortedDocs.add(doc);
                        }
                        
                        java.util.Collections.sort(sortedDocs, (d1, d2) -> {
                            com.google.firebase.Timestamp t1 = d1.getTimestamp("timestamp");
                            com.google.firebase.Timestamp t2 = d2.getTimestamp("timestamp");
                            if (t1 == null || t2 == null) return 0;
                            return t2.compareTo(t1);
                        });

                        for (QueryDocumentSnapshot doc : sortedDocs) {
                            addNotifRow(doc);
                        }
                    }
                });
    }

    private void addNotifRow(QueryDocumentSnapshot doc) {
        TextView tv = new TextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, (int) (12 * getResources().getDisplayMetrics().density));
        tv.setLayoutParams(params);
        tv.setPadding(24, 24, 24, 24);
        tv.setBackgroundResource(R.drawable.bg_card);
        tv.setTextColor(androidx.core.content.ContextCompat.getColor(this, R.color.title_purple));
        tv.setTextSize(14);

        String message = doc.getString("message");
        Boolean read = doc.getBoolean("read");
        
        tv.setText(message);
        if (read != null && !read) {
            tv.setTypeface(null, Typeface.BOLD);
            tv.setOnClickListener(v -> doc.getReference().update("read", true));
        }

        containerNotifications.addView(tv);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (notifListener != null) notifListener.remove();
    }
}