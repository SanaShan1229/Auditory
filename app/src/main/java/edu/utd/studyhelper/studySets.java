package edu.utd.studyhelper;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

public class studySets extends AppCompatActivity implements View.OnClickListener {


    TextToSpeech speech;
    SeekBar sBar;
    LinearLayout layout;
    ImageButton play;
    AlertDialog dialog;
    private ArrayList <String> fill;
    ImageButton stop;
    private Button deleteButton;
    private Note selectedNote;
    private EditText titleEditText;

    boolean speaking;
    play obj;
    //private ListView noteListView;
    //private static boolean firstRun = true;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_sets);
        layout = findViewById(R.id.container);
        fill = new ArrayList<>();
        initWidgets();
        //loadFromDBToMemory();
        //setNoteAdapter();
        checkForEditNote();
        speaking = false;
        play = findViewById(R.id.play);
        stop = findViewById(R.id.stop);
        buildDialog();
        speech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    int result = speech.setLanguage(Locale.US);

                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    } else {
                        play.setEnabled(true);
                    }
                } else {
                    Log.e("TTS", "Initialization failed");
                }
            }
        });

        sBar = findViewById(R.id.sbar);

        play.setOnClickListener(this);
        stop.setOnClickListener(this);

    }



    //}
    public void newNote(View view)
    {
        //Intent newNoteIntent = new Intent(this, question.class);
        //startActivity(newNoteIntent);
        dialog.show();

    }

    private void initWidgets()
    {
        titleEditText = findViewById(R.id.titleEditText);
        deleteButton = findViewById(R.id.deleteNoteButton);
        //noteListView = findViewById(R.id.noteListView);
    }
    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.play)
        {
            if (speech.isSpeaking() || speaking)
            {
                Toast.makeText(studySets.this, "Already Playing", Toast.LENGTH_SHORT).show();
            }
            else
            {
                speaking = true;
                Toast.makeText(studySets.this, "Playing", Toast.LENGTH_SHORT).show();
                obj = new play(fill, sBar.getProgress(), speech);
                obj.start();
            }


        }
        if (view.getId() == R.id.stop)
        {
            if (speech.isSpeaking() || speaking)
            {
                Toast.makeText(studySets.this, "Stopping", Toast.LENGTH_SHORT).show();
                obj.interrupt();
                speaking = false;
            }
            else
            {
                Toast.makeText(studySets.this, "Not Speaking", Toast.LENGTH_SHORT).show();
            }



        }



    }
    private void checkForEditNote()
    {
        Intent previousIntent = getIntent();

        int passedNoteID = previousIntent.getIntExtra(Note.NOTE_EDIT_EXTRA, -1);
        selectedNote = Note.getNoteForID(passedNoteID);

        if (selectedNote != null)
        {
                titleEditText.setText(selectedNote.getTitle());
                String str = selectedNote.getArray();
                String[] parts = str.split(", ");
                parts[0] = parts[0].substring(1);
                parts[parts.length-1] = parts[parts.length-1].substring(0,parts[parts.length-1].length()-1);
                if(selectedNote.getSize()!= 0)
                {
                    for (int i = 0; i < selectedNote.getSize(); i++) {
                        fill.add(parts[i]);
                    }
                    for (int i = 0; i < selectedNote.getSize(); i += 2) {
                        if (!(fill.get(i).equals("")))
                        {
                            addCard(fill.get(i), fill.get(i+1));
                        }
                    }
                }


        }
        else
        {
            deleteButton.setVisibility(View.INVISIBLE);
        }
    }
    public void saveNote(View view)
    {
        SQLiteManager sqLiteManager = SQLiteManager.instanceOfDatabase(this);
        String title = String.valueOf(titleEditText.getText());
        String array = fill.toString();
        int size = fill.size();


        if(selectedNote == null)
        {
            int id = Note.noteArrayList.size();
            Note newNote = new Note(id, title, array, size);
            Note.noteArrayList.add(newNote);
            sqLiteManager.addNoteToDatabase(newNote);
        }
        else
        {
            selectedNote.setTitle(title);
            selectedNote.setArray(array);
            selectedNote.setSize(size);
            //add the questions and answer in here
            sqLiteManager.updateNoteInDB(selectedNote);
        }

        finish();
    }

    public void deleteNote(View view)
    {
        selectedNote.setDeleted(new Date());
        SQLiteManager sqLiteManager = SQLiteManager.instanceOfDatabase(this);
        sqLiteManager.updateNoteInDB(selectedNote);
        finish();
    }
    //speak method that allows the user to change the pace and pitch of the audio through the use of seekbars




    private void buildDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog1, null);
        EditText q = view.findViewById(R.id.question);
        EditText a = view.findViewById(R.id.answer);
        builder.setView(view);
        builder.setTitle("Enter Question and Answer").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        fill.add(q.getText().toString());
                        fill.add(a.getText().toString());
                        addCard(q.getText().toString(), a.getText().toString());
                        q.setText("");
                        a.setText("");
                        q.requestFocus();
                        q.setSelection(0);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        q.setText("");
                        a.setText("");
                        q.requestFocus();
                        q.setSelection(0);
                    }
                });
        dialog = builder.create();

    }
    private void addCard(String ques, String ans)
    {
        View view = getLayoutInflater().inflate(R.layout.card1, null);
        TextView qname = view.findViewById(R.id.quest);
        qname.setText(ques);
        TextView aname = view.findViewById(R.id.answe);
        aname.setText(ans);
        Button delete = view.findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qu = fill.indexOf(ques);
                int an = fill.indexOf(ans);
                fill.set(qu, "");
                fill.set(an, "");
                layout.removeView(view);
            }
        });

        layout.addView(view);


    }

}