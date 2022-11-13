package com.example.myexpenses;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class OCRScan extends AppCompatActivity {

    private Toolbar mToolbar;
    Button button_capture, button_add;
    EditText ocrTitle, date;
    TextView ocrOutput;
    String userID;
    ProgressDialog progressDialog;
    private static final int REQUEST_CAMERA_CODE=100;
    Bitmap bitmap;

    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr_scan);

        button_capture = findViewById(R.id.button_capture);
        button_add = findViewById(R.id.button_add);
        ocrOutput = findViewById(R.id.ocrOutput);
        ocrTitle = findViewById(R.id.ocrTitle);
        //date = findViewById(R.id.date);

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userID = mAuth.getCurrentUser().getUid();

        progressDialog = new ProgressDialog(this);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("OCR Scan");

        //set back button on the action bar to go back to home
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

//        Calendar calendar = Calendar.getInstance();
//        int year = calendar.get(Calendar.YEAR);
//        int month = calendar.get(Calendar.MONTH);
//        int day = calendar.get(Calendar.DAY_OF_MONTH);

//        date.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                DatePickerDialog datePickerDialog = new DatePickerDialog(
//                        OCRScan.this, new DatePickerDialog.OnDateSetListener() {
//                    @Override
//                    public void onDateSet(DatePicker view, int year, int month, int day) {
//                        month = month+1;
//                        String dateString = day+"/"+month+"/"+year;
//                        date.setText(dateString);
//
//                    }
//                },year,month,day);
//                datePickerDialog.show();
//            }
//        });

        if(ContextCompat.checkSelfPermission(OCRScan.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(OCRScan.this, new String[]{
                    Manifest.permission.CAMERA
            }, REQUEST_CAMERA_CODE);
        }

        button_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(OCRScan.this);

            }
        });

        button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String ocrTitleString = ocrTitle.getText().toString();
                final String ocrOutputString = ocrOutput.getText().toString();
                //final  String dateString = date.getText().toString();

                if (TextUtils.isEmpty(ocrTitleString)){
                    Toast.makeText(OCRScan.this, "Please enter title", Toast.LENGTH_SHORT).show();
//                    ocrTitle.setError("Title is required");
//                    ocrTitle.requestFocus();
//                    return;
                }
                else{
                    progressDialog.setMessage("In Progress");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    DocumentReference documentReference = fStore.collection("User Record Information").document(userID)
                            .collection("OCR").document();
                    Map<String, Object> addOCRRecord= new HashMap<>();
                    addOCRRecord.put("OCR Title", ocrTitleString);
                    addOCRRecord.put("OCR Output", ocrOutputString);
                    //addOCRRecord.put("Date", dateString);
                    documentReference.set(addOCRRecord);
                    progressDialog.dismiss();
                    Toast.makeText(OCRScan.this, "New record has been added", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(OCRScan.this, OCRFragment.class);
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK){
                Uri resultUri= result.getUri();

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri); // bitmap crop liao de image
                    getTextFromImage(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void getTextFromImage(Bitmap bitmap){

        TextRecognizer recognizer = new TextRecognizer.Builder(this).build();
        if(!recognizer.isOperational()){
            Toast.makeText(OCRScan.this,"Error occurred", Toast.LENGTH_SHORT).show();
        }
        else{
            Frame frame = new Frame.Builder().setBitmap(bitmap).build(); //get crop de image
            SparseArray<TextBlock> textBlockSparseArray = recognizer.detect(frame); //detect textblock from the frame
            StringBuilder stringBuilder = new StringBuilder(); //build string
            for(int i =0; i<textBlockSparseArray.size();i++){  //based on size, then get value from textblock
                TextBlock textBlock = textBlockSparseArray.valueAt(i);
                stringBuilder.append(textBlock.getValue()); //convert to string
                stringBuilder.append("\n");
            }
            ocrOutput.setText(stringBuilder.toString());
            button_capture.setText("Retake");
            ocrTitle.setVisibility(View.VISIBLE);
            button_add.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}