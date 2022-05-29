package com.tish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.tish.db.connectors.AccPhoConnector;
import com.tish.dialogs.AddCostDialog;
import com.tish.interfaces.FragmentSendDataListener;

import java.util.List;

public class MainActivity extends AppCompatActivity implements FragmentSendDataListener {

    private FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar_view);
        setSupportActionBar(toolbar);
        Spinner spinner = findViewById(R.id.toolbar_spinner_account);
        AccPhoConnector accPhoConnector = new AccPhoConnector(this);
        List<String> accountList = accPhoConnector.getAccounts();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, accountList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                Bundle ab = new Bundle();
                ab.putString("account", accountList.get(position));
                CostsListFragment clf = null;
                clf = (CostsListFragment) getSupportFragmentManager().findFragmentByTag("TAG_COSTS_FRAGMENT");
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.detach(clf).attach(clf).commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        CostsListFragment costsListFragment = new CostsListFragment();
        Bundle accountBundle = new Bundle();
        accountBundle.putString("account", spinner.getSelectedItem().toString());
        costsListFragment.setArguments(accountBundle);
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.main_container, costsListFragment, "TAG_COSTS_FRAGMENT");
        fragmentTransaction.commit();
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
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
                        openIntent.setClass(MainActivity.this, StatisticsActivity.class);
                        break;
                    case R.id.nav_settings:

                        break;
                    default:
                        Toast.makeText(MainActivity.this, "Nothing selected", Toast.LENGTH_LONG).show();
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
            CostsListFragment clf = null;
            clf = (CostsListFragment) getSupportFragmentManager().findFragmentByTag("TAG_COSTS_FRAGMENT");
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.detach(clf).attach(clf).commit();
        } else if (data == -1)
            Toast.makeText(this, "При обробці витрати виникла помилка", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.fragment_cost_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_add_cost:
                AddCostDialog addCostDialog = new AddCostDialog(MainActivity.this);
                addCostDialog.show(getSupportFragmentManager(), "acd");
                break;
            case R.id.item_change_type:
                //describe
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

}