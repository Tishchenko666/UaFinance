package com.tish.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tish.R;
import com.tish.models.StatisticsItem;

import java.util.List;

public class StatisticsListAdapter extends ArrayAdapter<StatisticsItem> {

    private Context context;
    private List<StatisticsItem> statisticsList;

    public StatisticsListAdapter(Context context, List<StatisticsItem> statisticsList) {
        super(context, R.layout.item_statistics, statisticsList);
        this.context = context;
        this.statisticsList = statisticsList;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        StatisticsViewHolder viewHolder;
        if (view == null) {
            viewHolder = new StatisticsViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.item_statistics, parent, false);
            viewHolder.textViewType = view.findViewById(R.id.tv_statistics_type);
            viewHolder.textViewAmount = view.findViewById(R.id.tv_statistics_amount);
            viewHolder.textViewPercent = view.findViewById(R.id.tv_statistics_percent);
            view.setTag(viewHolder);
        } else {
            viewHolder = (StatisticsViewHolder) view.getTag();
        }
        StatisticsItem item = statisticsList.get(position);
        viewHolder.textViewType.setText(item.getTypeName());
        viewHolder.textViewAmount.setText(String.valueOf(item.getAmount()));
        viewHolder.textViewPercent.setText(item.getPercent() + "%");
        return view;
    }


    private static class StatisticsViewHolder {
        TextView textViewType;
        TextView textViewAmount;
        TextView textViewPercent;
    }
}
