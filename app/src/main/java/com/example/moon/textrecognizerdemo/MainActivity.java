package com.example.moon.textrecognizerdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView textView;
    private Button button;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private FloatingActionButton floatingActionButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init_views();
        init_variables();
        init_listeners();
        init_functions();

    }

    private void init_functions() {

    }

    private void init_listeners() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processImage();
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
    }

    private void init_variables() {
        //imageView.setImageResource(R.drawable.textimagetest);
        imageView.setImageResource(R.drawable.banglaimages);
        button.setText("Render Image for text");
        textView.setText("Demo text here");
    }

    private void init_views() {
        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.text);
        button = findViewById(R.id.btn);
        floatingActionButton = findViewById(R.id.floatingActionButton);
    }

    //user defined methods
    private void processImage() {
        Bitmap imageBitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(imageBitmap);
        FirebaseVisionTextRecognizer firebaseVisionTextRecognizer = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
        firebaseVisionTextRecognizer.processImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                String finalText = processVisionText(firebaseVisionText);
                textView.setText(finalText);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private String processVisionText(FirebaseVisionText firebaseVisionText) {
        StringBuffer stringBuffer = new StringBuffer();
        List<FirebaseVisionText.TextBlock> textBlocks = firebaseVisionText.getTextBlocks();
        if(textBlocks.size()==0){
            return stringBuffer.toString();
        }

        for(int i=0;i<textBlocks.size();i++){
            FirebaseVisionText.TextBlock singleBlock = textBlocks.get(i);
            List<FirebaseVisionText.Line> linesInEachBlock = singleBlock.getLines();
            if(linesInEachBlock.size()>0) {
                for (int j = 0; j < linesInEachBlock.size();j++){
                    List<FirebaseVisionText.Element> wordsInSingleLine = linesInEachBlock.get(j).getElements();
                    if(wordsInSingleLine.size()>0){
                        for(int k=0;k<wordsInSingleLine.size();k++){
                            stringBuffer.append(wordsInSingleLine.get(k).getText());
                            stringBuffer.append(" ");
                        }
                        stringBuffer.append("\n");
                    }

                }
                stringBuffer.append("\n");
                stringBuffer.append("\n");
            }
        }
        return stringBuffer.toString();
    }

  //capture Image

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
        }
    }


}
