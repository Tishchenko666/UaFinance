package com.tish;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.tish.adapters.CostsExpListAdapter;
import com.tish.db.bases.UkrainianMonth;
import com.tish.db.connectors.CostConnector;
import com.tish.dialogs.EditCostDialog;
import com.tish.dialogs.EditGeoDialog;
import com.tish.models.Cost;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;


public class CostsListFragment extends Fragment {

    ImageButton backButton;
    ImageButton forwardButton;
    TextSwitcher ts;
    List<YearMonth> dateList;
    int dateCounter = 0;
    Animation slideInRight;
    Animation slideOutLeft;
    Animation slideInLeft;
    Animation slideOutRight;

    YearMonth thisYearMonth;

    ExpandableListView costsListView;
    CostsExpListAdapter costsListAdapter;

    ArrayList<Cost> costsList;
    CostConnector costConnector;

    List<PieEntry> entries;
    int[] colors;
    PieChart chart;
    PieDataSet set;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_costs_list, container, false);

        costsListView = view.findViewById(R.id.ex_list_costs);
        backButton = view.findViewById(R.id.ib_back);
        forwardButton = view.findViewById(R.id.ib_forward);
        ts = view.findViewById(R.id.ts_costs);
        chart = view.findViewById(R.id.chart);

        costConnector = new CostConnector(getContext());

        initDates();

        if (costConnector.costsExist()) {
            costsList = costConnector.getCostsByDate(thisYearMonth.toString());
            costsListAdapter = new CostsExpListAdapter(getContext(), costsList);
            costsListView.setAdapter(costsListAdapter);
            entries = costConnector.getTotalAmountsForCategoriesByDate(thisYearMonth.toString());
            colors = costConnector.getCategoriesColorsByDate(thisYearMonth.toString());
        } else {
            entries.add(new PieEntry(1));
            colors = new int[1];
            colors[0] = R.color.bright_blue;
        }

        set = new PieDataSet(entries, "Costs");
        set.setColors(colors, getContext());
        PieData data = new PieData(set);
        setChartOptions();
        chart.setData(data);
        chart.invalidate();

        initButtons();

        registerForContextMenu(costsListView);
        return view;
    }

    private void initDates() {
        thisYearMonth = YearMonth.now();
        dateList = costConnector.getCostDates();
        dateCounter = dateList.size() - 1;
        if (!dateList.get(dateCounter).equals(thisYearMonth)) {
            dateList.add(thisYearMonth);
            dateCounter++;
        }
        ts.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView tv = new TextView(getContext());
                tv.setTextSize(20);
                tv.setTextColor(Color.BLUE);
                tv.setGravity(Gravity.CENTER_HORIZONTAL);
                return tv;
            }
        });
        ts.setText(UkrainianMonth.valueOf(thisYearMonth.getMonth().toString()).getUrkMonth() + ", " + thisYearMonth.getYear());
    }

    private void setChartOptions() {
        set.setValueTextColor(Color.BLACK);
        set.setValueTextSize(15);
        chart.setEntryLabelColor(Color.BLACK);
        chart.getLegend().setEnabled(false);
        Description desc = new Description();
        desc.setText("");
        chart.setDescription(desc);
        chart.setCenterText(String.valueOf(entries.stream().map(PieEntry::getValue).reduce(0f, Float::sum)));
        chart.setUsePercentValues(true);
        chart.setDrawEntryLabels(false);
    }

    private void initButtons() {
        slideInRight = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_right);
        slideOutLeft = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_left);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dateCounter != 0) {
                    dateCounter--;
                    ts.setInAnimation(slideInRight);
                    ts.setOutAnimation(slideOutLeft);
                    YearMonth yearMonth = dateList.get(dateCounter);
                    ts.setText(UkrainianMonth.valueOf(yearMonth.getMonth().toString()).getUrkMonth() + ", " + yearMonth.getYear());

                    updateDataByDate(yearMonth.toString());
                }
            }
        });

        slideInLeft = AnimationUtils.loadAnimation(getContext(), android.R.anim.slide_in_left);
        slideOutRight = AnimationUtils.loadAnimation(getContext(), android.R.anim.slide_out_right);

        forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dateCounter != dateList.size() - 1) {
                    dateCounter++;
                    ts.setInAnimation(slideInLeft);
                    ts.setOutAnimation(slideOutRight);
                    YearMonth yearMonth = dateList.get(dateCounter);
                    ts.setText(UkrainianMonth.valueOf(yearMonth.getMonth().toString()).getUrkMonth() + ", " + yearMonth.getYear());

                    updateDataByDate(yearMonth.toString());
                }
            }
        });
    }

    private void updateDataByDate(String date) {
        costsList.clear();
        costsList = costConnector.getCostsByDate(date);
        costsListAdapter.notifyDataSetChanged();

        entries.clear();
        entries = costConnector.getTotalAmountsForCategoriesByDate(date);
        colors = costConnector.getCategoriesColorsByDate(date);
        chart.setCenterText(String.valueOf(entries.stream().map(PieEntry::getValue).reduce(0f, Float::sum)));
        chart.notifyDataSetChanged();
        chart.invalidate();
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.cost_list_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        Cost selectedCost = costsListAdapter.getGroup(info.position);
        switch (item.getItemId()) {
            case R.id.context_item_edit_cost:
                EditCostDialog editCostDialog = new EditCostDialog(getContext(), selectedCost);
                editCostDialog.show(getActivity().getSupportFragmentManager(), "ecd");
                return true;
            case R.id.context_item_edit_geo:
                EditGeoDialog editGeoDialog = new EditGeoDialog(getContext(), selectedCost.getGeo(), selectedCost.getCostId());
                editGeoDialog.show(getActivity().getSupportFragmentManager(), "egd");
                return true;
            case R.id.context_item_edit_photo:
                //describe later
                return true;
        }
        return super.onContextItemSelected(item);
    }
}