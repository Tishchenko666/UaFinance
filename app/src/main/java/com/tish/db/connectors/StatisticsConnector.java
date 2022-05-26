package com.tish.db.connectors;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tish.db.bases.DBContract.Costs;
import com.tish.db.bases.DBHelper;
import com.tish.models.StatisticsItem;

import java.util.ArrayList;
import java.util.List;

public class StatisticsConnector {

    private DBHelper dbHelper;
    private SQLiteDatabase db;
    private Cursor statisticsCursor;

    public StatisticsConnector(Context context) {
        this.dbHelper = DBHelper.newInstance(context);
    }

    public List<StatisticsItem> getCategoryStatistics() {
        List<StatisticsItem> itemList = new ArrayList<>();
        db = dbHelper.getReadableDatabase();
        statisticsCursor = db.rawQuery("select " + Costs.COLUMN_CATEGORY + ", sum(" + Costs.COLUMN_AMOUNT + "), "
                + "round(sum(" + Costs.COLUMN_AMOUNT + ")/(select sum(" + Costs.COLUMN_AMOUNT + ") from " + Costs.TABLE_NAME + ")*100, 2) "
                + "from " + Costs.TABLE_NAME + " group by " + Costs.COLUMN_CATEGORY, null);
        statisticsCursor.moveToFirst();
        int column = statisticsCursor.getColumnIndexOrThrow(Costs.COLUMN_CATEGORY);
        while (statisticsCursor.moveToNext()) {
            String category = statisticsCursor.getString(column);
            double amount = statisticsCursor.getDouble(column + 1);
            double percent = statisticsCursor.getDouble(column + 2);
            itemList.add(new StatisticsItem(category, amount, percent));
        }
        statisticsCursor.close();
        db.close();
        return itemList;
    }

    public List<StatisticsItem> getCategoryStatisticsByDate(String currentDate) {
        List<StatisticsItem> itemList = new ArrayList<>();
        db = dbHelper.getReadableDatabase();
        statisticsCursor = db.rawQuery("select " + Costs.COLUMN_CATEGORY + ", sum(" + Costs.COLUMN_AMOUNT + "), "
                + "round(sum(" + Costs.COLUMN_AMOUNT + ")/(select sum(" + Costs.COLUMN_AMOUNT + ") from " + Costs.TABLE_NAME + " where " + Costs.COLUMN_DATE + " like '" + currentDate + "%')*100, 2) "
                + "from " + Costs.TABLE_NAME + " where " + Costs.COLUMN_DATE + " like '" + currentDate + "%'"
                + " group by " + Costs.COLUMN_CATEGORY, null);
        statisticsCursor.moveToFirst();
        int column = statisticsCursor.getColumnIndexOrThrow(Costs.COLUMN_CATEGORY);
        while (statisticsCursor.moveToNext()) {
            String category = statisticsCursor.getString(column);
            double amount = statisticsCursor.getDouble(column + 1);
            double percent = statisticsCursor.getDouble(column + 2);
            itemList.add(new StatisticsItem(category, amount, percent));
        }
        statisticsCursor.close();
        db.close();
        return itemList;
    }

    public List<String> getDatesBySettingType(String dateType) {
        List<String> datesList = new ArrayList<>();
        StringBuilder query = new StringBuilder("select distinct strftime('%");
        StringBuilder from = new StringBuilder(Costs.COLUMN_DATE).append(") from ").append(Costs.TABLE_NAME);
        switch (dateType) {
            case "m":
                query.append("m', ").append(from);
                break;
            case "y":
                query.append("Y', ").append(from);
                break;
        }
        db = dbHelper.getReadableDatabase();
        statisticsCursor = db.rawQuery(query.toString(), null);
        statisticsCursor.moveToFirst();
        while (statisticsCursor.moveToNext()) {
            datesList.add(statisticsCursor.getString(0));
        }
        statisticsCursor.close();
        db.close();
        return datesList;
    }
}
