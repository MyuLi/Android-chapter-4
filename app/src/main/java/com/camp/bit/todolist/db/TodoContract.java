package com.camp.bit.todolist.db;

import android.provider.BaseColumns;

/**
 * Created on 2019/1/22.
 *
 * @author xuyingyi@bytedance.com (Yingyi Xu)
 */
public final class TodoContract {


    private TodoContract() {
    }

    public static class TodoList implements BaseColumns{
        public static final String TABLE_NAME = "note";
        public static final String COLUMN_NAME_TITLE = "content";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_STATE = "state";
        public static final String COLUMN_NAME_PRIORITY = "priority";

    }
    public static final  String SQL_GREATE_LISTS =
            "CREATE TABLE " + TodoList.TABLE_NAME + " ("+
                    TodoList._ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                    TodoList.COLUMN_NAME_TITLE +" TEXT,"+
                    TodoList.COLUMN_NAME_DATE +" LONG,"+
                    TodoList.COLUMN_NAME_STATE +" INTEGER,"+
                    TodoList.COLUMN_NAME_PRIORITY + " INTEGER)";

    public static final String SQL_DELETE_LISTS =
            "DROP TABLE IF EXISTS "+ TodoList.TABLE_NAME;

}
