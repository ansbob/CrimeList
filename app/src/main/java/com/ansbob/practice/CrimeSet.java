package com.ansbob.practice;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import database.BaseHelper;
import database.CrimeCursorWrapper;
import database.DBSchema;
import database.DBSchema.Table;

public class CrimeSet {
    private static CrimeSet crimeSet;
    private Context context;
    private SQLiteDatabase database;

    public static CrimeSet get(Context context) {
        if(crimeSet == null) {
            crimeSet = new CrimeSet(context);
        }
        return crimeSet;
    }

    public CrimeSet(Context context) {
        this.context = context.getApplicationContext();
        database = new BaseHelper(this.context).getWritableDatabase();
    }

    private static ContentValues getContentValues(Crime crime) {
        ContentValues values = new ContentValues();
        values.put(Table.Cols.UUID, crime.getId().toString());
        values.put(Table.Cols.TITLE, crime.getTitle());
        values.put(Table.Cols.DATE, crime.getDate().getTime());
        values.put(Table.Cols.SOLVED, crime.isSolved() ? 1 : 0);
        values.put(Table.Cols.SUSPECT, crime.getSuspect());
        return values;
    }

    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs) {
        Cursor c = database.query(
                Table.NAME,
                null, // Columns - null выбирает все столбцы
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null // orderBy
        );
        return new CrimeCursorWrapper(c);
    }

    public void deleteCrime(Crime c) {
        database.delete(Table.NAME, Table.Cols.UUID + " = ?", new String[]{c.getId().toString()});
    }

    public void addCrime(Crime c) {
        ContentValues values = getContentValues(c);
        database.insert(Table.NAME, null, values);
    }

    public void updateCrime(Crime crime) {
        String uuidString = crime.getId().toString();
        ContentValues values = getContentValues(crime);
        database.update(Table.NAME, values, Table.Cols.UUID + " = ?", new String[] {uuidString});
    }

    public List<Crime> getCrimes() {
        List<Crime> crimes = new ArrayList<>();
        CrimeCursorWrapper cursor = queryCrimes(null,null);
        try{
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                crimes.add(cursor.getCrime());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return crimes;
    }

    public Crime getCrime(UUID crimeId) {
        CrimeCursorWrapper cursor = queryCrimes(
                Table.Cols.UUID + " = ?",
                new String[]{crimeId.toString()}
        );
        try {
            if(cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getCrime();
        } finally {
            cursor.close();
        }
    }

    public File getPhotoFile(Crime crime) {
        File externalFilesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if(externalFilesDir == null) return null;
        return new File(externalFilesDir, crime.getPhotoFileName());
    }
}
