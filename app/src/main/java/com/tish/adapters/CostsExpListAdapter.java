package com.tish.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.tish.R;
import com.tish.models.Cost;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class CostsExpListAdapter extends BaseExpandableListAdapter {

    private static class CostViewHolder {
        ImageView imageViewCategoryIcon;
        TextView textViewCategory;
        TextView textViewAmount;
    }

    private static class CostDetailsViewHolder {
        TextView textViewMarketName;
        TextView textViewDate;
        TextView textViewGeo;
        ImageButton imageButtonPhoto;
    }

    private Context context;
    private List<Cost> costList;

    public CostsExpListAdapter(Context context, ArrayList<Cost> list) {
        this.context = context;
        this.costList = list;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View view, ViewGroup viewGroup) {
        CostViewHolder viewHolder;
        if (view == null) {
            viewHolder = new CostViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.item_cost, viewGroup, false);
            viewHolder.imageViewCategoryIcon = view.findViewById(R.id.iv_category_icon);
            viewHolder.textViewCategory = view.findViewById(R.id.tv_category);
            viewHolder.textViewAmount = view.findViewById(R.id.tv_amount);
        } else
            viewHolder = (CostViewHolder) view.getTag();

        Cost cost = costList.get(groupPosition);

        viewHolder.imageViewCategoryIcon.setImageResource(cost.getCategory().getIconResource());
        ShapeDrawable sd = new ShapeDrawable(new OvalShape());
        sd.setIntrinsicWidth(40);
        sd.setIntrinsicWidth(40);
        sd.getPaint().setColor(context.getResources().getColor(cost.getCategory().getColorResource(), null));
        viewHolder.imageViewCategoryIcon.setBackground(sd);
        viewHolder.textViewCategory.setText(cost.getCategoryName());
        viewHolder.textViewAmount.setText(String.valueOf(cost.getAmount()));
        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isExpanded,
                             View view, ViewGroup viewGroup) {
        CostDetailsViewHolder viewHolder;
        if (view == null) {
            viewHolder = new CostDetailsViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.item_cost_details, viewGroup, false);
            viewHolder.textViewMarketName = view.findViewById(R.id.tv_market_name);
            viewHolder.textViewDate = view.findViewById(R.id.tv_date);
            viewHolder.textViewGeo = view.findViewById(R.id.tv_geo);
            viewHolder.imageButtonPhoto = view.findViewById(R.id.ib_photo);
        } else
            viewHolder = (CostDetailsViewHolder) view.getTag();

        Cost cost = costList.get(groupPosition);

        if (cost.getMarketName() != null)
            viewHolder.textViewMarketName.setText(cost.getMarketName());
        else
            viewHolder.textViewMarketName.setText("Місце покупки не вказано");

        viewHolder.textViewDate.setText(cost.getDate());

        if (cost.getGeo() != null)
            viewHolder.textViewGeo.setText(cost.getGeo().getAddress() + ", " + cost.getGeo().getCity());

        if (cost.isPhotoExists())
            viewHolder.imageButtonPhoto.setImageResource(R.drawable.icon_cheque_is);
        else
            viewHolder.imageButtonPhoto.setImageResource(R.drawable.icon_no_cheque);

        viewHolder.imageButtonPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cost.isPhotoExists()) {
                    //open image
                } else {
                    //make photo
                }
            }
        });
        return view;
    }

    @Override
    public int getGroupCount() {
        return 0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 0;
    }

    @Override
    public Cost getGroup(int groupPosition) {
        return costList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
