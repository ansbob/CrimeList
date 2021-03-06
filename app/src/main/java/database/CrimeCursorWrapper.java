package database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.ansbob.practice.Crime;

import java.util.Date;
import java.util.UUID;

import database.DBSchema.Table;

public class CrimeCursorWrapper extends CursorWrapper {
    public CrimeCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Crime getCrime() {
        String uuidString = getString(getColumnIndex(Table.Cols.UUID));
        String title = getString(getColumnIndex(Table.Cols.TITLE));
        long date = getLong(getColumnIndex(Table.Cols.DATE));
        int isSolved = getInt(getColumnIndex(Table.Cols.SOLVED));
        String suspect = getString(getColumnIndex(Table.Cols.SUSPECT));

        Crime crime = new Crime(UUID.fromString(uuidString));
        crime.setTitle(title);
        crime.setDate(new Date(date));
        crime.setSolved(isSolved != 0);
        crime.setSuspect(suspect);
        return crime;
    }
}
