package edu.utd.studyhelper;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class Option extends AppCompatActivity implements View.OnClickListener{


    Button learn;
    Button quiz;
    Button scan;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);
        quiz = findViewById(R.id.quizone);
        learn = findViewById(R.id.learnone);
        scan = findViewById(R.id.scanone);
        quiz.setOnClickListener(this);
        learn.setOnClickListener(this);
        scan.setOnClickListener(this);
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean firstStart = prefs.getBoolean("firstStart", true);
        if(firstStart)
        {
            showStartDialog();
        }

    }

    private void showStartDialog() {
        new AlertDialog.Builder(this).setTitle("Welcome to Auditory!").setMessage("There are three main functions of this app: Learn, Quiz, and Scan. Quiz is designed to test your ability to answer specialized questions, and each questions and answer is followed by a brief pause to give you time to think. Learn is designed to read your notes out loud because the more you listen to information, the easier it is to remember. Lastly, Scan is designed to import your physical notes into text since you can copy to clipboard and paste anywhere you like. Enjoy!")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                })
                .create().show();
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("firstStart", false);
        editor.apply();

    }

    @Override
    public void onClick(View view) {



        if(view.getId() == learn.getId())
        {

                Intent intent = new Intent(getApplicationContext(), learnsave.class);
                startActivity(intent);


        }
        if(view.getId() == scan.getId())
        {

                Intent intent = new Intent(getApplicationContext(), capture.class);
                startActivity(intent);


        }
        if(view.getId() == quiz.getId())
        {



            Intent intent = new Intent(getApplicationContext(), quizsave.class);
            startActivity(intent);



        }
    }
}