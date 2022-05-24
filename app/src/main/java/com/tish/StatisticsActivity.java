package com.tish;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.tish.db.connectors.AccPhoConnector;
import com.tish.db.connectors.CostConnector;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StatisticsActivity extends AppCompatActivity {

    CostConnector costConnector;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        costConnector = new CostConnector(StatisticsActivity.this);
        initToolbar();
        initContent();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_view);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Spinner accountSpinner = findViewById(R.id.toolbar_spinner_account);
        AccPhoConnector accPhoConnector = new AccPhoConnector(this);
        List<String> accountList = accPhoConnector.getAccounts();
        ArrayAdapter<String> accountAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, accountList);
        accountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accountSpinner.setAdapter(accountAdapter);
        accountSpinner.setSelection(0);
    }

    private void initContent() {
        Spinner simpleDateSpinner = findViewById(R.id.spinner_statistics_date);
        Button getStatisticsButton = findViewById(R.id.button_get_statistics);

        YearMonth thisYearMonth = YearMonth.now();
        List<YearMonth> dateList = costConnector.getCostDates(1);
        if (!dateList.get(dateList.size() - 1).equals(thisYearMonth)) {
            dateList.add(0, thisYearMonth);
        }
        List<String> spinnerDatesList = dateList.stream().map(YearMonth::toString).collect(Collectors.toList());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerDatesList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        simpleDateSpinner.setAdapter(adapter);
        simpleDateSpinner.setSelection(0);

        getStatisticsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //create list
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_statistics_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_set_up_statistics:
                //open setup_dialog
                break;
            case R.id.pop_item_change_type:
                //describe
                break;
            case R.id.pop_item_export:
                //describe last
                break;
        }
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        //describe opening other activity
        return true;
    }
}
