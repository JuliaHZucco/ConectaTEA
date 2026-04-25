package com.example.conectaTEA;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class TeacherRequestsActivity extends BaseActivity {

    private LinearLayout containerTeacherRequests;
    private TextView tvNoPending;
    private ListenerRegistration requestsListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_requests);
        setupBackButton();

        containerTeacherRequests = findViewById(R.id.containerTeacherRequests);
        tvNoPending = findViewById(R.id.tvNoPending);

        loadMyRequestsRealtime();
    }

    private void loadMyRequestsRealtime() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        requestsListener = FirebaseFirestore.getInstance().collection("accessRequests")
                .whereEqualTo("teacherId", uid)
                .whereEqualTo("status", "pending")
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;
                    containerTeacherRequests.removeAllViews();
                    if (value == null || value.isEmpty()) {
                        tvNoPending.setVisibility(View.VISIBLE);
                    } else {
                        tvNoPending.setVisibility(View.GONE);
                        for (QueryDocumentSnapshot doc : value) {
                            addRequestRow(doc);
                        }
                    }
                });
    }

    private void addRequestRow(QueryDocumentSnapshot doc) {
        TextView tv = new TextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, (int) (12 * getResources().getDisplayMetrics().density));
        tv.setLayoutParams(params);
        tv.setPadding(24, 24, 24, 24);
        tv.setBackgroundResource(R.drawable.bg_card);
        tv.setTextColor(androidx.core.content.ContextCompat.getColor(this, R.color.title_purple));
        tv.setTextSize(16);

        String childName = doc.getString("childName");
        String tableCode = doc.getString("tableCode");
        tv.setText(String.format("Tabela de %s\nCódigo: %s\nStatus: Pendente", childName, tableCode));

        containerTeacherRequests.addView(tv);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (requestsListener != null) requestsListener.remove();
    }
}