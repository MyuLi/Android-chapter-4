package com.camp.bit.todolist.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.camp.bit.todolist.db.TodoContract.SQL_DELETE_LISTS;
import static com.camp.bit.todolist.db.TodoContract.SQL_GREATE_LISTS;

/**
 * Created on 2019/1/22.
 *
 * @author xuyingyi@bytedance.com (Yingyi Xu)
 */
public class TodoDbHelper extends SQLiteOpenHelper {

    public  static  final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "todo.db";

    public TodoDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_GREATE_LISTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            db.execSQL("ALTER TABLE " + TodoContract.TodoList.TABLE_NAME +
                    " ADD "+ TodoContract.TodoList.COLUMN_NAME_PRIORITY + " INTEGER");
            //.scheonCreate(db);
    }

}
