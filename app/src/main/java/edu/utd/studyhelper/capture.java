package edu.utd.studyhelper;

import static android.Manifest.permission.CAMERA;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.internal.TextRecognizerImpl;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

public class capture extends AppCompatActivity implements View.OnClickListener{
    Button scan;
    Button copy;
    Bitmap imageBitmap;
    ImageView cap;

    static final int REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        scan = findViewById(R.id.scan);
        copy = findViewById(R.id.copy);
        cap = findViewById(R.id.cap);
        scan.setOnClickListener(this);
        copy.setOnClickListener(this);




    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.scan)
        {
            if(checkPermissions())
            {
                capture();
            }
            else
            {
                requestPermission();
            }


        }
        if (view.getId() == R.id.copy)
        {
            detectText();
            Toast.makeText(capture.this, "Copied to Clipboard", Toast.LENGTH_SHORT).show();



        }

    }

    private void detectText()
    {
        //store the image as an array of bits
        InputImage image = InputImage.fromBitmap(imageBitmap, 0);
        TextRecognizer r =
                TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        Task<Text> result = r.process(image).addOnSuccessListener(new OnSuccessListener<Text>() {
            @Override
            public void onSuccess(@NonNull Text text) {
                StringBuilder result = new StringBuilder();
                //first get blocks, or paragraphs, then individual lines, and then each word
                for(Text.TextBlock block: text.getTextBlocks())
                {
                    String blockText = block.getText();
                    Point[] blockCornerPoint = block.getCornerPoints();
                    Rect blockFrame = block.getBoundingBox();
                    for(Text.Line line :block.getLines())
                    {
                        String lineText = line.getText();
                        Point[] lineCornerPoints = line.getCornerPoints();
                        Rect lineRect = line.getBoundingBox();
                        for(Text.Element element: line.getElements())
                        {
                            String elementText = element.getText();
                            result.append(elementText);
                        }
                        //copy the String to the user's keyboard to paste in the text to speech portion
                        copyToClipBoard(blockText);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(capture.this, "Failed To Detect Text From Image"+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void copyToClipBoard(String text)
    {
        ClipboardManager clipBoard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied data", text);
        clipBoard.setPrimaryClip(clip);

    }

    private boolean checkPermissions()
    {
        int cameraPermission = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        return cameraPermission == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission()
    {
        int PERMISSION_CODE = 200;
        ActivityCompat.requestPermissions(this, new String[]{CAMERA},PERMISSION_CODE);
    }

    private void capture()
    {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePicture.resolveActivity(getPackageManager())!=null)
        {
            startActivityForResult(takePicture,REQUEST);

        }


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length>0)
        {
            boolean cameraP = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if(cameraP)
            {
                Toast.makeText(this,"Permission Granted", Toast.LENGTH_SHORT).show();
                capture();


            }
            else
            {
                Toast.makeText(this,"Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST && resultCode == RESULT_OK)
        {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            cap.setImageBitmap(imageBitmap);
            copy.setEnabled(true);


        }
    }
}