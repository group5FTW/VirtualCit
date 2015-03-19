package com.example.liz.virtualcit;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.liz.virtualcit.Model.TableEntry;

import java.util.ArrayList;
import java.util.List;

public class DatabaseManagement {

    // Database fields
    private SQLiteDatabase database;
    private MySQLLiteHelper dbHelper;
    private String[] allColumns = {MySQLLiteHelper.COLUMN_ID,
            MySQLLiteHelper.COLUMN_COMMENT};

    public DatabaseManagement(Context context) {
        dbHelper = new MySQLLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public TableEntry createTableEntry(String tableEntry) {
        ContentValues values = new ContentValues();
        values.put(MySQLLiteHelper.COLUMN_COMMENT, tableEntry);
        long insertId = database.insert(MySQLLiteHelper.TABLE_COMMENTS, null,
                values);
        Cursor cursor = database.query(MySQLLiteHelper.TABLE_COMMENTS,
                allColumns, MySQLLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);

        cursor.moveToFirst();
        TableEntry newTableEntry = cursorToTableEntry(cursor);
        cursor.close();
        return newTableEntry;
    }

    public void deleteTableEntry(TableEntry tableEntry) {
        long id = tableEntry.getId();
        System.out.println("TableEntry deleted with id: " + id);
        database.delete(MySQLLiteHelper.TABLE_COMMENTS, MySQLLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<TableEntry> getAllTableEntrys() {
        List<TableEntry> tableEntrys = new ArrayList<TableEntry>();

        Cursor cursor = database.query(MySQLLiteHelper.TABLE_COMMENTS,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            TableEntry tableEntry = cursorToTableEntry(cursor);
            tableEntrys.add(tableEntry);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return tableEntrys;
    }

    private TableEntry cursorToTableEntry(Cursor cursor) {
        TableEntry tableEntry = new TableEntry();
        tableEntry.setId(cursor.getLong(0));
        tableEntry.setTableEntry(cursor.getString(1));
        return tableEntry;
    }
}

