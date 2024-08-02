package edu.utd.studyhelper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class learn extends AppCompatActivity implements View.OnClickListener {
    ImageButton play;
    ImageButton stop;
    TextToSpeech tts;
    SeekBar sBar;
    String str;
    private EditText titleEditText, descEditText;
    private Button deleteButton;
    private NoteOne selectedNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);
        initWidgets();
        checkForEditNote();
        play = findViewById(R.id.play);
        stop = findViewById(R.id.stop);
        sBar = findViewById(R.id.sbar);


        play.setOnClickListener(this);
        stop.setOnClickListener(this);
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i)
            {
                if (i == TextToSpeech.SUCCESS)
                {
                    //sets language to English
                    int result = tts.setLanguage(Locale.US);

                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
                    {
                        //logs error
                        Log.e("TTS", "Language not supported");
                    }
                    else
                    {
                        //button will be able to be pressed once everything is passed
                        play.setEnabled(true);
                    }
                }
                else
                {
                    //logs error
                    Log.e("TTS", "Initialization failed");
                }
            }
        });





    }

    private void initWidgets()
    {
        titleEditText = findViewById(R.id.titleEditText);
        descEditText = findViewById(R.id.descriptionEditText);
        deleteButton = findViewById(R.id.deleteNoteButton);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.play)
        {
            Toast.makeText(learn.this, "Playing", Toast.LENGTH_SHORT).show();
            //converts the edit text to a String
            String text = descEditText.getText().toString();
            str = descEditText.getText().toString();
            //checks if empty
            if(text.equals(""))
            {
                Toast.makeText(learn.this, "Please Enter Text", Toast.LENGTH_SHORT).show();

            }
            else
            {
                //make sure speed is not 0
                float speed = (float)sBar.getProgress()/ 50;
                if(speed < 0.1)
                {
                    speed = 0.1f;
                }

                tts.setSpeechRate(speed);
                //if user presses play again, media will restart rather than be on hold
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            }
        }
        if (view.getId() == R.id.stop)
        {
            if(tts.isSpeaking())
            {
                //did not implement tts.shutdown because if user wanted to player again after stopping, they can
                Toast.makeText(learn.this, "Stopping", Toast.LENGTH_SHORT).show();
                tts.stop();

            }
            else
            {
                //if no media playing, toast will appear
                Toast.makeText(learn.this, "Not Speaking", Toast.LENGTH_SHORT).show();
            }

        }





    }
    public void saveNote(View view)
    {
        SQLiteManagerOne sqLiteManager = SQLiteManagerOne.instanceOfDatabase(this);
        String title = String.valueOf(titleEditText.getText());
        String desc = String.valueOf(descEditText.getText());

        if(selectedNote == null)
        {
            int id = NoteOne.noteOneArrayList.size();
            NoteOne newNote = new NoteOne(id, title, desc);
            NoteOne.noteOneArrayList.add(newNote);
            sqLiteManager.addNoteToDatabase(newNote);
        }
        else
        {
            selectedNote.setTitle(title);
            selectedNote.setDescription(desc);
            sqLiteManager.updateNoteInDB(selectedNote);
        }

        finish();
    }



    private void checkForEditNote()
    {
        Intent previousIntent = getIntent();

        int passedNoteID = previousIntent.getIntExtra(NoteOne.NOTE_EDIT_EXTRA, -1);
        selectedNote = NoteOne.getNoteForID(passedNoteID);

        if (selectedNote != null)
        {
            titleEditText.setText(selectedNote.getTitle());
            descEditText.setText(selectedNote.getDescription());
        }
        else
        {
            deleteButton.setVisibility(View.INVISIBLE);
        }
    }



    public void deleteNote(View view)
    {
        selectedNote.setDeleted(new Date());
        SQLiteManagerOne sqLiteManager = SQLiteManagerOne.instanceOfDatabase(this);
        sqLiteManager.updateNoteInDB(selectedNote);
        finish();
    }






}
