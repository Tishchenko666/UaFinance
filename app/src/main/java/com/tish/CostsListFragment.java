package com.tish;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import com.tish.db.connectors.CostConnector;
import com.tish.models.Cost;

import java.util.ArrayList;
import java.util.List;


public class CostsListFragment extends Fragment {

    ExpandableListView costsListView;
    ImageButton backButton;
    ImageButton forwardButton;
    TextSwitcher ts;
    String[] days = new String[]{"mon", "tues", "wends", "thurs", "fri", "sat", "sun"};
    int count = 0;
    Animation slideInRight;
    Animation slideOutLeft;
    Animation slideInLeft;
    Animation slideOutRight;
    ArrayList<Cost> costsList;
    CostConnector costConnector;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_costs_list, container, false);
        costsListView = view.findViewById(R.id.ex_list_costs);

        costConnector = new CostConnector(getContext());
        costsList = costConnector.getCosts();

        CostsExpListAdapter costsListAdapter = new CostsExpListAdapter(getContext(), costsList);
        costsListView.setAdapter(costsListAdapter);

        PieChart chart = view.findViewById(R.id.chart);

        List<PieEntry> entries = new ArrayList<>();

        entries.add(new PieEntry(18.5f, "Green"));
        entries.add(new PieEntry(26.7f, "Yellow"));
        entries.add(new PieEntry(24.0f, "Red"));
        entries.add(new PieEntry(30.8f, "Blue"));

        PieDataSet set = new PieDataSet(entries, "Election Results");
        set.setColors(new int[]{Color.GREEN, Color.YELLOW, Color.RED, Color.BLUE}, 150);
        set.setValueTextColor(Color.BLACK);
        set.setValueTextSize(15);
        PieData data = new PieData(set);
        chart.setEntryLabelColor(Color.BLACK);
        chart.getLegend().setEnabled(false);
        Description desc = new Description();
        desc.setText("");
        chart.setDescription(desc);
        chart.setCenterText("Total");
        chart.setData(data);
        chart.invalidate();

        backButton = view.findViewById(R.id.ib_back);
        forwardButton = view.findViewById(R.id.ib_forward);
        ts = view.findViewById(R.id.ts_costs);
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
        ts.setText(days[count]);

        slideInRight = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_right);
        slideOutLeft = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_left);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (count != 0) {
                    count--;
                    ts.setInAnimation(slideInRight);
                    ts.setOutAnimation(slideOutLeft);
                    ts.setText(days[count]);
                }
            }
        });

        slideInLeft = AnimationUtils.loadAnimation(getContext(), android.R.anim.slide_in_left);
        slideOutRight = AnimationUtils.loadAnimation(getContext(), android.R.anim.slide_out_right);

        forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (count != 6) {
                    count++;
                    ts.setInAnimation(slideInLeft);
                    ts.setOutAnimation(slideOutRight);
                    ts.setText(days[count]);
                }
            }
        });
        return view;
    }
}