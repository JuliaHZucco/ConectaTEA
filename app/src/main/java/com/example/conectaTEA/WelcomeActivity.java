package com.example.conectaTEA;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

public class WelcomeActivity extends BaseActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TextView btnMenu, tvWelcome;

    private String userName;
    private String userEmail;
    private String profile;
    private ListenerRegistration profileListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        btnMenu = findViewById(R.id.btnMenu);
        tvWelcome = findViewById(R.id.tvWelcome);

        userName = getIntent().getStringExtra("USER_NAME");
        userEmail = getIntent().getStringExtra("USER_EMAIL");
        profile = getIntent().getStringExtra("PROFILE");

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null && (userName == null || profile == null)) {
            fetchProfileRealtime(currentUser.getUid());
        } else {
            updateUI();
        }

        btnMenu.setOnClickListener(v -> drawerLayout.open());

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.menu_children) {
                startActivity(new Intent(this, ChildrenListActivity.class));
            } else if (id == R.id.menu_add_child) {
                startActivity(new Intent(this, AddChildActivity.class));
            } else if (id == R.id.menu_requests) {
                startActivity(new Intent(this, ManageTableAccessActivity.class));
            } else if (id == R.id.menu_request_access) {
                startActivity(new Intent(this, RequestAccessActivity.class));
            } else if (id == R.id.menu_authorized_tables) {
                startActivity(new Intent(this, AuthorizedTablesActivity.class));
            } else if (id == R.id.menu_my_requests) {
                startActivity(new Intent(this, TeacherRequestsActivity.class));
            } else if (id == R.id.menu_notifications) {
                startActivity(new Intent(this, NotificationsActivity.class));
            } else if (id == R.id.menu_profile) {
                Intent intent = new Intent(this, ProfileActivity.class);
                intent.putExtra("USER_NAME", userName);
                intent.putExtra("USER_EMAIL", userEmail);
                startActivity(intent);
            } else if (id == R.id.menu_logout) {
                logout();
            }

            drawerLayout.close();
            return true;
        });
    }

    private void logout() {
        if (profileListener != null) profileListener.remove();
        
        FirebaseFirestore.getInstance().terminate().addOnCompleteListener(t -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void fetchProfileRealtime(String uid) {
        profileListener = FirebaseFirestore.getInstance().collection("users").document(uid)
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;
                    if (value != null && value.exists()) {
                        userName = value.getString("name");
                        userEmail = value.getString("email");
                        profile = value.getString("profile");
                        updateUI();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (profileListener != null) profileListener.remove();
    }

    private void updateUI() {
        if (userName == null || userName.isEmpty()) {
            tvWelcome.setText("Bem-vindo(a)!");
        } else {
            tvWelcome.setText(String.format("Bem-vindo(a), %s", userName));
        }
        configurarMenuPorPerfil();
        checkNotificationsCount();
    }

    private void checkNotificationsCount() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;
        
        FirebaseFirestore.getInstance().collection("notifications")
                .whereEqualTo("userId", user.getUid())
                .whereEqualTo("read", false)
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) return;
                    int count = value.size();
                    Menu menu = navigationView.getMenu();
                    android.view.MenuItem notifItem = menu.findItem(R.id.menu_notifications);
                    if (count > 0) {
                        notifItem.setTitle("Notificações (" + count + ")");
                    } else {
                        notifItem.setTitle("Notificações");
                    }
                });
    }

    private void configurarMenuPorPerfil() {
        Menu menu = navigationView.getMenu();
        boolean isProfessor = "professor".equals(profile);

        menu.findItem(R.id.menu_children).setVisible(!isProfessor);
        menu.findItem(R.id.menu_add_child).setVisible(!isProfessor);
        menu.findItem(R.id.menu_requests).setVisible(!isProfessor);

        menu.findItem(R.id.menu_request_access).setVisible(isProfessor);
        menu.findItem(R.id.menu_authorized_tables).setVisible(isProfessor);
        menu.findItem(R.id.menu_my_requests).setVisible(isProfessor);
        menu.findItem(R.id.menu_notifications).setVisible(true);

        View header = navigationView.getHeaderView(0);
        if (header != null) {
            TextView tvMenuProfile = header.findViewById(R.id.tvMenuProfile);
            TextView tvMenuName = header.findViewById(R.id.tvMenuName);
            
            if (tvMenuProfile != null) {
                tvMenuProfile.setText(isProfessor ? "Professor(a)" : "Responsável");
            }
            if (tvMenuName != null && userName != null) {
                tvMenuName.setText(userName);
            }
        }
    }
}