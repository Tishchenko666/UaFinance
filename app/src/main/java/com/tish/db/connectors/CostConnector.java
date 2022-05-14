package com.tish.db.connectors;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tish.db.bases.Category;
import com.tish.db.bases.DBContract.*;
import com.tish.db.bases.DBHelper;
import com.tish.models.Cost;

import java.util.ArrayList;

public class CostConnector {

    private DBHelper dbHelper;
    private SQLiteDatabase db;
    private Cursor costCursor;

    public CostConnector(Context context) {
        this.dbHelper = DBHelper.newInstance(context);
    }

    public ArrayList<Cost> getCosts() {
        ArrayList<Cost> costs = new ArrayList<>();
        db = dbHelper.getReadableDatabase();
        costCursor = db.rawQuery("select * from " + Costs.TABLE_NAME, null);
        costCursor.moveToFirst();
        while (costCursor.moveToNext()) {
            int costId = costCursor.getInt(costCursor.getColumnIndexOrThrow(Costs.COLUMN_COST_ID));
            String category = costCursor.getString(costCursor.getColumnIndexOrThrow(Costs.COLUMN_CATEGORY));
            double amount = costCursor.getDouble(costCursor.getColumnIndexOrThrow(Costs.COLUMN_AMOUNT));
            String date = costCursor.getString(costCursor.getColumnIndexOrThrow(Costs.COLUMN_DATE));
            String marketName = costCursor.getString(costCursor.getColumnIndexOrThrow(Costs.COLUMN_MARKET_NAME));
            Integer accountId = costCursor.getInt(costCursor.getColumnIndexOrThrow(Costs.COLUMN_ACCOUNT_ID));
            Integer geoId = costCursor.getInt(costCursor.getColumnIndexOrThrow(Costs.COLUMN_GEO_ID));
            Integer photoId = costCursor.getInt(costCursor.getColumnIndexOrThrow(Costs.COLUMN_PHOTO_ID));
            costs.add(0, new Cost(costId, Category.valueOf(category), amount, date, marketName));
            if (accountId != null) {
                //get account and add to current cost
            }
            if (geoId != null) {
                //get geo and add to current cost
            }
            if (photoId != null) {
                //get photo and add to current cost
            }
        }
        costCursor.close();
        db.close();
        return costs;
    }
}
