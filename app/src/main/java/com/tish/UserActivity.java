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
    private boolean addFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        addFragment = true;
        initView();
    }

    private void initView() {

        Toolbar toolbar = findViewById(R.id.no_spinner_toolbar_view);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout_user);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view_user);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent openIntent = new Intent();
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                switch (item.getItemId()) {
                    case R.id.nav_profile:
                        addFragment = false;
                        getSupportActionBar().setTitle(R.string.nav_item_profile);
                        ProfileFragment profileFragment = new ProfileFragment();
                        fragmentTransaction.replace(R.id.user_container, profileFragment, "TAG_PR_FRAGMENT");
                        fragmentTransaction.commit();
                        break;
                    case R.id.nav_account_manager:
                        addFragment = false;
                        getSupportActionBar().setTitle(R.string.nav_item_acc_manager);
                        AccountManagerFragment accountManagerFragment = new AccountManagerFragment();
                        fragmentTransaction.add(R.id.user_container, accountManagerFragment, "TAG_AM_FRAGMENT");
                        fragmentTransaction.commit();
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
                //navigationView.setCheckedItem(item);
                startActivity(openIntent);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        if (addFragment) {
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            Bundle args = getIntent().getExtras();
            if (args.getChar("fragment") == 'p') {
                getSupportActionBar().setTitle(R.string.nav_item_profile);
                navigationView.setCheckedItem(R.id.nav_profile);
                ProfileFragment profileFragment = new ProfileFragment();
                fragmentTransaction.add(R.id.user_container, profileFragment, "TAG_PR_FRAGMENT");
            } else if (args.getChar("fragment") == 'a') {
                getSupportActionBar().setTitle(R.string.nav_item_acc_manager);
                navigationView.setCheckedItem(R.id.nav_account_manager);
                AccountManagerFragment accountManagerFragment = new AccountManagerFragment();
                fragmentTransaction.add(R.id.user_container, accountManagerFragment, "TAG_AM_FRAGMENT");
            }
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onSendData(long data, String fragmentTag) {
        if (data > 0) {
            AccountManagerFragment amf = null;
            amf = (AccountManagerFragment) getSupportFragmentManager().findFragmentByTag(fragmentTag);
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