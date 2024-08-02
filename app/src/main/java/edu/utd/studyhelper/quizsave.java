package edu.utd.studyhelper;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

public class quizsave extends AppCompatActivity {

    LinearLayout layout;
        private ListView noteListView;
    private static boolean firstRun = true;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_quizsave);
            layout = findViewById(R.id.container);
            initWidgets();
            loadFromDBToMemory();
            setNoteAdapter();
            setOnClickListener();



        }



    private void loadFromDBToMemory() {
            if(firstRun)
            {
                SQLiteManager sqLiteManager = SQLiteManager.instanceOfDatabase(this);
                sqLiteManager.populateNoteListArray();
                sqLiteManager.close();
                finish();
            }
            firstRun = false;


    }
    private void setOnClickListener()
    {
        noteListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
            {
                Note selectedNote = (Note) noteListView.getItemAtPosition(position);
                Intent editNoteIntent = new Intent(getApplicationContext(), studySets.class);
                editNoteIntent.putExtra(Note.NOTE_EDIT_EXTRA, selectedNote.getId());
                startActivity(editNoteIntent);
            }
        });
    }


    private void initWidgets()
        {
            noteListView = findViewById(R.id.noteListView);
        }
        private void setNoteAdapter()
        {
            NoteAdapter noteAdapter = new NoteAdapter(getApplicationContext(), Note.nonDeletedNotes());
            noteListView.setAdapter(noteAdapter);
        }
        public void newNote(View view)
        {
            Intent newNoteIntent = new Intent(this, studySets.class);
            startActivity(newNoteIntent);
        }
        @Override
        protected void onResume()
        {
            super.onResume();
            setNoteAdapter();

        }



    }

