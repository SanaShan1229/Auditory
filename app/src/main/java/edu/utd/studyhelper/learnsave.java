package edu.utd.studyhelper;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

public class learnsave extends AppCompatActivity {
    private ListView noteListView;
    private static boolean firstRun = true;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learnsave);
        initWidgets();
        loadFromDBToMemory();
        setNoteAdapter();
        setOnClickListener();








    }




    private void initWidgets()
    {
        noteListView = findViewById(R.id.noteListView);
    }

    private void loadFromDBToMemory()
    {
        if(firstRun)
        {
            SQLiteManagerOne sqLiteManager = SQLiteManagerOne.instanceOfDatabase(this);
            sqLiteManager.populateNoteListArray();
            sqLiteManager.close();
            finish();
        }
        firstRun = false;
    }

    private void setNoteAdapter()
    {
        NoteAdapterOne noteAdapter = new NoteAdapterOne(getApplicationContext(), NoteOne.nonDeletedNotes());
        noteListView.setAdapter(noteAdapter);
    }


    private void setOnClickListener()
    {
        noteListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
            {
                NoteOne selectedNote = (NoteOne) noteListView.getItemAtPosition(position);
                Intent editNoteIntent = new Intent(getApplicationContext(), learn.class);
                editNoteIntent.putExtra(NoteOne.NOTE_EDIT_EXTRA, selectedNote.getId());
                startActivity(editNoteIntent);
            }
        });
    }


    public void newNote(View view)
    {
        Intent newNoteIntent = new Intent(this, learn.class);
        startActivity(newNoteIntent);
    }

    @Override
    protected void onResume()
    {

        super.onResume();
        setNoteAdapter();
    }


}