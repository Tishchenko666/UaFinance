package com.tish.db.connectors;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tish.db.bases.DBContract.Geolocations;
import com.tish.db.bases.DBHelper;
import com.tish.models.Geolocation;

public class GeoConnector {
    private DBHelper dbHelper;
    private SQLiteDatabase db;
    private Cursor geoCursor;

    public GeoConnector(Context context) {
        this.dbHelper = DBHelper.newInstance(context);
    }

    public Geolocation getGeoById(int geoId) {
        Geolocation geo = new Geolocation();
        db = dbHelper.getReadableDatabase();
        geoCursor = db.rawQuery("select * from " + Geolocations.TABLE_NAME + " where " + Geolocations.COLUMN_GEO_ID + "=" + geoId, null);
        geoCursor.moveToFirst();
        geo.setGeoId(geoCursor.getInt(geoCursor.getColumnIndexOrThrow(Geolocations.COLUMN_GEO_ID)));
        geo.setLongitude(geoCursor.getDouble(geoCursor.getColumnIndexOrThrow(Geolocations.COLUMN_LONGITUDE)));
        geo.setLatitude(geoCursor.getDouble(geoCursor.getColumnIndexOrThrow(Geolocations.COLUMN_LATITUDE)));
        geo.setCountry(geoCursor.getString(geoCursor.getColumnIndexOrThrow(Geolocations.COLUMN_COUNTRY)));
        geo.setCity(geoCursor.getString(geoCursor.getColumnIndexOrThrow(Geolocations.COLUMN_CITY)));
        geo.setAddress(geoCursor.getString(geoCursor.getColumnIndexOrThrow(Geolocations.COLUMN_ADDRESS)));
        geoCursor.close();
        db.close();
        return geo;
    }
}
