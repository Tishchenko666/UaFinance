package com.tish.db.connectors;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tish.R;
import com.tish.db.bases.DBContract.Photos;
import com.tish.db.bases.DBContract.Accounts;
import com.tish.db.bases.DBHelper;

import java.util.ArrayList;
import java.util.List;

public class AccPhoConnector {
    private DBHelper dbHelper;
    private SQLiteDatabase db;
    private Cursor apCursor;

    public AccPhoConnector(Context context) {
        this.dbHelper = DBHelper.newInstance(context);
    }

    public List<String> getAccounts() {
        List<String> accountList = new ArrayList<>();
        accountList.add(String.valueOf(R.string.app_name));
        db = dbHelper.getReadableDatabase();
        apCursor = db.rawQuery("select * from " + Accounts.TABLE_NAME, null);
        if (apCursor.getCount() > 0) {
            apCursor.moveToFirst();
            while (apCursor.moveToNext()) {
                accountList.add(apCursor.getString(apCursor.getColumnIndexOrThrow(Accounts.COLUMN_NUMBER)));
            }
        }
        apCursor.close();
        db.close();
        return accountList;
    }

    public String getAccountById(int accountId) {
        db = dbHelper.getReadableDatabase();
        apCursor = db.rawQuery("select " + Accounts.COLUMN_NUMBER + " from " + Accounts.TABLE_NAME + " where " + Accounts.COLUMN_ACCOUNT_ID + "=" + accountId, null);
        apCursor.moveToFirst();
        String number = apCursor.getString(apCursor.getColumnIndexOrThrow(Accounts.COLUMN_NUMBER));
        apCursor.close();
        db.close();
        return number;
    }

    public String getPhotoById(int photoId) {
        db = dbHelper.getReadableDatabase();
        apCursor = db.rawQuery("select " + Photos.COLUMN_LINK + " from " + Photos.TABLE_NAME + " where " + Photos.COLUMN_PHOTO_ID + "=" + photoId, null);
        apCursor.moveToFirst();
        String address = apCursor.getString(apCursor.getColumnIndexOrThrow(Photos.COLUMN_LINK));
        apCursor.close();
        db.close();
        return address;
    }

}
