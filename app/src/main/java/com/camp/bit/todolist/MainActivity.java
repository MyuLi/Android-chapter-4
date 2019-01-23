package com.camp.bit.todolist;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;

import com.camp.bit.todolist.beans.Note;
import com.camp.bit.todolist.beans.State;
import com.camp.bit.todolist.db.TodoContract;
import com.camp.bit.todolist.db.TodoDbHelper;
import com.camp.bit.todolist.debug.DebugActivity;
import com.camp.bit.todolist.ui.NoteListAdapter;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ADD = 1002;

    private RecyclerView recyclerView;
    private NoteListAdapter notesAdapter;

    TodoDbHelper todoDbHelper;
    SQLiteDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        todoDbHelper = new TodoDbHelper(getBaseContext());
        db = todoDbHelper.getReadableDatabase();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(
                        new Intent(MainActivity.this, NoteActivity.class),
                        REQUEST_CODE_ADD);
            }
        });


        recyclerView = findViewById(R.id.list_todo);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        notesAdapter = new NoteListAdapter(new NoteOperator() {
            @Override
            public void deleteNote(Note note) {
                MainActivity.this.deleteNote(note);
            }

            @Override
            public void updateNote(Note note) {
                MainActivity.this.updateNode(note);
            }
        });
        recyclerView.setAdapter(notesAdapter);

        notesAdapter.refresh(loadNotesFromDatabase());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        todoDbHelper.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_debug:
                startActivity(new Intent(this, DebugActivity.class));
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD
                && resultCode == Activity.RESULT_OK) {
            notesAdapter.refresh(loadNotesFromDatabase());
        }
    }

    private List<Note> loadNotesFromDatabase() {

        if(db == null) return Collections.emptyList();

        List<Note> result = new LinkedList<>();
        Cursor cursor = null;

        try{
            cursor = db.query(TodoContract.TodoList.TABLE_NAME
                    ,null
            ,null,null,null,null,
                    TodoContract.TodoList.COLUMN_NAME_PRIORITY + " DESC, " +
                            TodoContract.TodoList.COLUMN_NAME_DATE + " DESC"
            );//逆序显示


            while(cursor.moveToNext()){
                String content = cursor.getString(cursor.getColumnIndex(TodoContract.TodoList.COLUMN_NAME_TITLE));
                long dateMs = cursor.getLong(cursor.getColumnIndex(TodoContract.TodoList.COLUMN_NAME_DATE));
                long id = cursor.getLong(cursor.getColumnIndex(TodoContract.TodoList._ID));
                int intState = cursor.getInt(cursor.getColumnIndex(TodoContract.TodoList.COLUMN_NAME_STATE));
                int priority = cursor.getInt(cursor.getColumnIndex(TodoContract.TodoList.COLUMN_NAME_PRIORITY));

               // int id = cursor.getInt(cursor.getColumnIndex(TodoContract.TodoList._ID));
                Note note = new Note(id);
                note.setContent(content);
                note.setDate(new Date(dateMs));
                note.setState(State.from(intState));
                note.setPriority(priority);

                result.add(note);
            }
        }catch (Exception e){
            Log.d("error message", "loadNotesFromDatabase: "+e.getMessage());


        }
        finally {
            if(cursor != null)
                cursor.close();;
        }
        return result;
    }

    private void deleteNote(Note note) {
        String selection = TodoContract.TodoList._ID  + " = " + note.id;
        int deletedRows = db.delete(TodoContract.TodoList.TABLE_NAME,selection,null);
        notesAdapter.refresh(loadNotesFromDatabase());
    }

    private void updateNode(Note note) {
        // 更新数据
        State state = State.DONE;

        ContentValues values = new ContentValues();
        values.put(TodoContract.TodoList.COLUMN_NAME_STATE,state.intValue);
        String selection = TodoContract.TodoList._ID + " = " +note.id;
        db.update(TodoContract.TodoList.TABLE_NAME,values,selection,null);
        notesAdapter.refresh(loadNotesFromDatabase());
    }

}
