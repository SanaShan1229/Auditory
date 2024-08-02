package edu.utd.studyhelper;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;

public class play extends Thread {
     ArrayList<String> fill;
     TextToSpeech speech;
     int progress;

    public play(ArrayList<String> a, int p, TextToSpeech s) {
        fill = a;
        progress = p;
        speech = s;
    }
    public void run()
    {
        try
        {
            for (int i = 0; i < fill.size(); i += 2) {
                int c = i;
                if (!(fill.get(c).equals(""))) {
                    speak(c);
                    while(true)
                    {
                        if(!(speech.isSpeaking()))
                        {
                            Thread.sleep(7000);
                            break;
                        }
                    }


                }


                if (!(fill.get(c + 1).equals(""))) {
                    speak(c + 1);
                    while(true)
                    {
                        if(!(speech.isSpeaking()))
                        {
                            try {
                                Thread.sleep(2000);
                                break;
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }


            }
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

    }

    public boolean isRunning()
    {
        return speech.isSpeaking();
    }

    private void speak(int i)
    {
        //make sure speed is not 0
        float speed = (float)progress/ 50;
        if(speed < 0.1)
        {
            speed = 0.1f;
        }

        speech.setSpeechRate(speed);
        //QUE_ADD plays the audio recordings one after another. Completely finishes the recording before playing another one.


        speech.speak(fill.get(i), TextToSpeech.QUEUE_ADD, null);

    }
}
