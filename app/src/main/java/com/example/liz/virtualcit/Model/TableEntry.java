package com.example.liz.virtualcit.Model;


public class TableEntry {
    private long id;
    private String tableEntry;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTableEntry() {
        return tableEntry;
    }

    public void setTableEntry(String tableEntry) {
        this.tableEntry = tableEntry;
    }

    @Override
    public String toString() {
        return tableEntry;
    }
}

