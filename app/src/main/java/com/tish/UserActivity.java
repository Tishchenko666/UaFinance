package com.tish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.tish.interfaces.FragmentSendDataListener;

public class UserActivity extends AppCompatActivity implements FragmentSendDataListener {

    private FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        initView();
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.no_spinner_toolbar_view);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.nav_item_acc_manager);

        AccountManagerFragment accountManagerFragment = new AccountManagerFragment();
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.user_container, accountManagerFragment, "TAG_AM_FRAGMENT");
        fragmentTransaction.commit();

        DrawerLayout drawer = findViewById(R.id.drawer_layout_user);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view_user);
        navigationView.setCheckedItem(R.id.nav_account_manager);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent openIntent = new Intent();
                switch (item.getItemId()) {
                    case R.id.nav_profile:

                        break;
                    case R.id.nav_account_manager:

                        break;
                    case R.id.nav_list:

                        break;
                    case R.id.nav_map:

                        break;
                    case R.id.nav_statistic:
                        openIntent.setClass(UserActivity.this, StatisticsActivity.class);
                        break;
                    case R.id.nav_settings:

                        break;
                    default:
                        Toast.makeText(UserActivity.this, "Nothing selected", Toast.LENGTH_LONG).show();
                }
                navigationView.setCheckedItem(item);
                startActivity(openIntent);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    @Override
    public void onSendData(long data) {
        if (data > 0) {
            AccountManagerFragment amf = null;
            amf = (AccountManagerFragment) getSupportFragmentManager().findFragmentByTag("TAG_AM_FRAGMENT");
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.detach(amf).attach(amf).commit();
        } else if (data == -1)
            Toast.makeText(this, "Введений рахунок вже існує", Toast.LENGTH_SHORT).show();
        else if (data == 0)
            Toast.makeText(this, "При оновлені даних рахунку виникла помилка", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout_user);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

}