package com.tish.db.connectors;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.github.mikephil.charting.data.PieEntry;
import com.tish.R;
import com.tish.db.bases.Category;
import com.tish.db.bases.DBContract.*;
import com.tish.db.bases.DBHelper;
import com.tish.models.Cost;
import com.tish.models.Geolocation;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CostConnector {

    private DBHelper dbHelper;
    private SQLiteDatabase db;
    private Cursor costCursor;
    private AccPhoConnector accPhoConnector;
    private GeoConnector geoConnector;

    public CostConnector(Context context) {
        this.dbHelper = DBHelper.newInstance(context);
        accPhoConnector = new AccPhoConnector(context);
        geoConnector = new GeoConnector(context);
    }

    public boolean costsExist() {
        db = dbHelper.getReadableDatabase();
        costCursor = db.rawQuery("select * from " + Costs.TABLE_NAME, null);
        costCursor.moveToFirst();
        if (costCursor.getInt(0) == 0)
            return false;
        else
            return true;
    }

    public ArrayList<Cost> getCosts() {
        ArrayList<Cost> costs = new ArrayList<>();
        Cost tempCost;
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
            tempCost = new Cost(costId, Category.valueOf(category), amount, date, marketName);
            if (accountId != null) {
                tempCost.setAccountNumber(accPhoConnector.getAccountById(accountId));
            }
            if (geoId != null) {
                Geolocation geo = geoConnector.getGeoById(geoId);
                tempCost.setGeo(geo);
            }
            if (photoId != null) {
                tempCost.setPhotoAddress(accPhoConnector.getPhotoById(photoId));
            }
            costs.add(0, tempCost);
        }
        costCursor.close();
        db.close();
        return costs;
    }

    public ArrayList<Cost> getCostsByDate(String currentDate) {
        ArrayList<Cost> costsByDate = new ArrayList<>();
        Cost tempCost;
        db = dbHelper.getReadableDatabase();
        costCursor = db.rawQuery("select * from " + Costs.TABLE_NAME + " where " + Costs.COLUMN_DATE + " like '" + currentDate + "%'", null);
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
            tempCost = new Cost(costId, Category.valueOf(category), amount, date, marketName);
            if (accountId != null) {
                tempCost.setAccountNumber(accPhoConnector.getAccountById(accountId));
            }
            if (geoId != null) {
                Geolocation geo = geoConnector.getGeoById(geoId);
                tempCost.setGeo(geo);
            }
            if (photoId != null) {
                tempCost.setPhotoAddress(accPhoConnector.getPhotoById(photoId));
            }
            costsByDate.add(0, tempCost);
        }
        costCursor.close();
        db.close();
        return costsByDate;
    }

    public ArrayList<PieEntry> getTotalAmountsForCategoriesByDate(String currentDate) {
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        db = dbHelper.getReadableDatabase();
        costCursor = db.rawQuery("select " + Costs.COLUMN_CATEGORY + ", sum(" + Costs.COLUMN_AMOUNT + ") from "
                + Costs.TABLE_NAME + " where " + Costs.COLUMN_DATE + " like '" + currentDate + "%'" + " group by " + Costs.COLUMN_CATEGORY, null);
        costCursor.moveToFirst();
        while (costCursor.moveToNext()) {
            String category = costCursor.getString(costCursor.getColumnIndexOrThrow(Costs.COLUMN_CATEGORY));
            float totalAmount = (float) costCursor.getDouble(costCursor.getColumnIndexOrThrow(Costs.COLUMN_CATEGORY) + 1);
            pieEntries.add(new PieEntry(totalAmount, Category.valueOf(category).getCategoryName()));
        }
        costCursor.close();
        db.close();
        return pieEntries;
    }

    public int[] getCategoriesColorsByDate(String currentDate) {
        ArrayList<Integer> colorsList = new ArrayList<>();
        db = dbHelper.getReadableDatabase();
        costCursor = db.rawQuery("select distinct " + Costs.COLUMN_CATEGORY + " from " + Costs.TABLE_NAME + " where " + Costs.COLUMN_DATE + " like '" + currentDate + "%'", null);
        costCursor.moveToFirst();
        while (costCursor.moveToNext()) {
            String category = costCursor.getString(0);
            colorsList.add(Category.valueOf(category).getColorResource());
        }
        int[] colors = new int[colorsList.size()];
        for (int i = 0; i < colors.length; i++) {
            colors[i] = colorsList.get(i);
        }
        costCursor.close();
        db.close();
        return colors;
    }

    public List<YearMonth> getCostDates() {
        List<YearMonth> dateList = new ArrayList<>();
        YearMonth ym;
        db = dbHelper.getReadableDatabase();
        costCursor = db.rawQuery("select DISTINCT " + Costs.COLUMN_DATE + " from " + Costs.TABLE_NAME, null);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        costCursor.moveToFirst();
        while (costCursor.moveToNext()) {
            ym = YearMonth.parse(costCursor.getString(0), dtf);
            dateList.add(ym);
        }
        costCursor.close();
        db.close();
        return dateList;
    }

    public long insertNewCost(Cost cost) {
        ContentValues cvNewCost = new ContentValues();
        cvNewCost.put(Costs.COLUMN_CATEGORY, cost.getCategory().toString());
        cvNewCost.put(Costs.COLUMN_AMOUNT, cost.getAmount());
        cvNewCost.put(Costs.COLUMN_DATE, cost.getDate());
        cvNewCost.put(Costs.COLUMN_MARKET_NAME, cost.getMarketName());
        if (cost.getAccountNumber() != null) {
            cvNewCost.put(Costs.COLUMN_ACCOUNT_ID, accPhoConnector.getAccountIdByNumber(cost.getAccountNumber()));
        }
        if (cost.getGeo() != null) {
            cvNewCost.put(Costs.COLUMN_GEO_ID, geoConnector.insertGeolocationToGetId(cost.getGeo()));
        }
        if (cost.getPhotoAddress() != null) {
            cvNewCost.put(Costs.COLUMN_PHOTO_ID, accPhoConnector.insertPhotoToGetPhotoId(cost.getPhotoAddress()));
        }
        db = dbHelper.getWritableDatabase();
        long result = db.insert(Costs.TABLE_NAME, null, cvNewCost);
        db.close();
        return result;
    }

    public int updateCost(Cost editCost, boolean updateAmount) {
        ContentValues cvEditCost = new ContentValues();
        cvEditCost.put(Costs.COLUMN_CATEGORY, editCost.getCategory().toString());
        cvEditCost.put(Costs.COLUMN_AMOUNT, editCost.getAmount());
        cvEditCost.put(Costs.COLUMN_DATE, editCost.getDate());
        cvEditCost.put(Costs.COLUMN_MARKET_NAME, editCost.getMarketName());
        if (updateAmount) {
            if (editCost.getAccountNumber() != null)
                cvEditCost.put(Costs.COLUMN_ACCOUNT_ID, accPhoConnector.getAccountIdByNumber(editCost.getAccountNumber()));
            else
                cvEditCost.put(Costs.COLUMN_ACCOUNT_ID, editCost.getAccountNumber());
        }
        db = dbHelper.getWritableDatabase();
        int result = db.update(Costs.TABLE_NAME, cvEditCost, Costs.COLUMN_COST_ID + "=" + editCost.getCostId(), null);
        db.close();
        return result;
    }

    public int updateGeoInCost(Geolocation editGeo, int costId) {
        int geoId = geoConnector.insertGeolocationToGetId(editGeo);
        int result = 0;
        if (geoId != editGeo.getGeoId()) {
            ContentValues cvEditGeoInCost = new ContentValues();
            cvEditGeoInCost.put(Costs.COLUMN_GEO_ID, geoId);
            db = dbHelper.getWritableDatabase();
            result = db.update(Costs.TABLE_NAME, cvEditGeoInCost, Costs.COLUMN_COST_ID + "=" + costId, null);
            db.close();
        }
        return result;
    }
}
