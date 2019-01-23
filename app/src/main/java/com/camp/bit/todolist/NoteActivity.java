package com.camp.bit.todolist;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.camp.bit.todolist.beans.State;
import com.camp.bit.todolist.db.TodoContract;
import com.camp.bit.todolist.db.TodoDbHelper;

import java.util.Date;

public class NoteActivity extends AppCompatActivity {

    private EditText editText;
    private Button addBtn;
    private RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        setTitle(R.string.take_a_note);

        editText = findViewById(R.id.edit_text);
        editText.setFocusable(true);
        editText.requestFocus();
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            inputManager.showSoftInput(editText, 0);
        }

        radioGroup = findViewById(R.id.rg_pri);
        addBtn = findViewById(R.id.btn_add);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence content = editText.getText();
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(NoteActivity.this,
                            "No content to add", Toast.LENGTH_SHORT).show();
                    return;
                }
                int priority = 0;
                if(radioGroup.getCheckedRadioButtonId() == -1){
                    Toast.makeText(NoteActivity.this,
                            "No priority to add", Toast.LENGTH_SHORT).show();
                    return ;

                }
                else {
                    switch (radioGroup.getCheckedRadioButtonId()) {
                        case R.id.radioButton: {
                            priority = 1;
                            break;
                        }
                        case R.id.radioButton2:{
                            priority = 2;
                            break;
                        }
                        case R.id.radioButton3 :{
                            priority = 3;
                            break;
                        }
                    }
                }
                boolean succeed = saveNote2Database(content.toString().trim(),priority);
                if (succeed) {
                    Toast.makeText(NoteActivity.this,
                            "Note added", Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_OK);
                } else {
                    Toast.makeText(NoteActivity.this,
                            "Error", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private boolean saveNote2Database(String content,int i) {

        TodoDbHelper mDbHelper = new TodoDbHelper(getBaseContext());//getcontext??
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        long date = new Date().getTime();
        ContentValues values = new ContentValues();
        values.put(TodoContract.TodoList.COLUMN_NAME_TITLE,content);
        values.put(TodoContract.TodoList.COLUMN_NAME_STATE,0);
        values.put(TodoContract.TodoList.COLUMN_NAME_DATE,date);
        values.put(TodoContract.TodoList.COLUMN_NAME_PRIORITY,i);

        long newRowId = db.insert(TodoContract.TodoList.TABLE_NAME,null,values);

        mDbHelper.close();

        return newRowId != -1;
    }
}
