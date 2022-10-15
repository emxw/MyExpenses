package com.example.myexpenses;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HomeFragment extends AppCompatActivity {

    private Toolbar mToolbar;
    private ImageView image;
    private EditText title, date, amount;
    private Button chooseGallery, addRecord;
    private Uri imageUri;
    private String downloadUri;
    private FirebaseFirestore fStore;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private StorageReference storageReference;

//    ActivityResultLauncher<Intent> launchActivityForGalleryResult;
//    String currentImagePath = null;


    private static final int GalleryPick = 1;
    private static final int CAMERA_REQUEST = 100;
    private static final int STORAGE_REQUEST = 200;
    private static final int IMAGEPICK_GALLERY_REQUEST = 300;
    private static final int IMAGE_PICKCAMERA_REQUEST = 400;
    String cameraPermission[];
    String storagePermission[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_fragment);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("New Record");

        //set back button on the action bar to go back to home
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        image = findViewById(R.id.imageView);
        title = findViewById(R.id.title);
        date = findViewById(R.id.date);
        amount = findViewById(R.id.amount);
        chooseGallery = findViewById(R.id.addGallery);
        addRecord = findViewById(R.id.addRecord);

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        // allowing permissions of gallery and camera
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        HomeFragment.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        month = month+1;
                        String dateString = day+"/"+month+"/"+year;
                        date.setText(dateString);

                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });

        chooseGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePicture();
            }
        });

//        launchActivityForGalleryResult = registerForActivityResult(
//                new ActivityResultContracts.StartActivityForResult(),
//                new ActivityResultCallback<ActivityResult>() {
//                    @Override
//                    public void onActivityResult(ActivityResult result) {
//                        try {
//                            if (result.getData() != null) {
//                                Intent data = result.getData();
//                                imageUri = data.getData();
//                                String[] filePath = {MediaStore.Images.Media.DATA};
//                                Cursor c = getContentResolver().query(imageUri, filePath, null, null, null);
//                                c.moveToFirst();
//                                int columnIndex = c.getColumnIndex(filePath[0]);
//                                currentImagePath = c.getString(columnIndex);
//                                c.close();
//                                Bitmap thumbnail = (BitmapFactory.decodeFile(currentImagePath));
//                                String mimeType = MimeTypeMap.getFileExtensionFromUrl(currentImagePath);
//                                if (mimeType != null && mimeType.endsWith("mp4")) {
//                                    Toast.makeText(HomeFragment.this, "Uploaded file cannot be video, Please try again...", Toast.LENGTH_SHORT).show();
//                                } else {
//                                    if (mimeType!=null && thumbnail!=null){
//                                        mImgUpload.setVisibility(View.VISIBLE);
//                                        mImgUpload.setImageBitmap(thumbnail);
//                                        mTvImgUpload.setVisibility(View.GONE);
//                                        viewProceedCancelButton();
//                                    }else{
//                                        Toast.makeText(HomeFragment.this, "Upload Failed, Please try again...", Toast.LENGTH_SHORT).show();
//
//                                    }
//                                }
//                            } else {
//                                Toast.makeText(HomeFragment.this, "No image uploaded", Toast.LENGTH_SHORT).show();
//                            }
//                        } catch (Exception e) {
//                            Toast.makeText(HomeFragment.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });


        addRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String titleString = title.getText().toString();
//                String priceString = amount.getText().toString();
//
//                if (TextUtils.isEmpty(titleString)){
//                    title.setError("Title is required");
//                    title.requestFocus();
//                    return;
//                }
//                if (TextUtils.isEmpty(priceString)){
//                    amount.setError("Amount is required");
//                }
                //showImagePicDialog();
            }
        });
    }

//    private void showImagePicDialog() {
//        String options[] = {"Camera", "Gallery"};
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Pick Image From");
//        builder.setItems(options, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                if (which == 0){
//                    if (!checkCameraPermission()){
//                        requestCameraPermission();
//                    }
//                    else{
//                        pickFromGallery();
//                    }
//                }
//                else if (which == 1){
//                    if (!checkStoragePermission()){
//                        requestStoragePermission();
//                    }
//                    else{
//                        pickFromGallery();
//                    }
//                }
//            }
//        });
//        builder.create().show();
//    }

    // checking storage permissions
//    private boolean checkStoragePermission(){
//        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
//        return result;
//    }

    // Requesting  gallery permission
//    private void requestStoragePermission() {
//        requestPermissions(storagePermission, STORAGE_REQUEST);
//    }

    // checking camera permissions
//    private Boolean checkCameraPermission() {
//        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
//        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
//        return result && result1;
//    }

    // Requesting camera permission
//    private void requestCameraPermission() {
//        requestPermissions(cameraPermission, CAMERA_REQUEST);
//    }

    //requesting camera and gallery if permission is not given
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode) {
//            case CAMERA_REQUEST: {
//                if (grantResults.length > 0) {
//                    boolean camera_accepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
//                    boolean writeStorageaccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
//                    if (camera_accepted && writeStorageaccepted) {
//                        pickFromGallery();
//                    } else {
//                        Toast.makeText(this, "Please enable Camera and Storage Permission", Toast.LENGTH_LONG).show();
//                    }
//                }
//            }
//            break;
//            case STORAGE_REQUEST: {
//                if (grantResults.length > 0) {
//                    boolean camera_accepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
//                    boolean writeStorageaccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
//                    if (writeStorageaccepted) {
//                        pickFromGallery();
//                    } else {
//                        Toast.makeText(this, "Please Enable Storage Permission", Toast.LENGTH_LONG).show();
//                    }
//                }
//            }
//            break;
//        }
//    }

    //pick image from gallery
//    private void pickFromGallery() {
//        CropImage.activity().start(HomeFragment.this);
//    }

    private void choosePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            imageUri = data.getData();
            image.setImageURI(imageUri);
            uploadPic();
        }
//        else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//            if (requestCode == RESULT_OK){
//                Uri resultUri = result.getUri();
//                Picasso.with(this).load(resultUri).into(image);
//            }
//        }
    }

    private void uploadPic() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading");
        progressDialog.show();

        StorageReference fileRef = storageReference.child("images/");
        final UploadTask uploadTask = fileRef.putFile(imageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(HomeFragment.this, "Upload image failure", Toast.LENGTH_SHORT).show();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()){
                            throw task.getException();
                        }
                        downloadUri = fileRef.getDownloadUrl().toString();
                        return fileRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()){
                            downloadUri = task.getResult().toString();
                            String userID = mAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("User Record Information").document(userID).collection("Images From Gallery").document();
                            Map<String, Object> AddImage= new HashMap<>();
                            AddImage.put("Image", downloadUri);
                            documentReference.set(AddImage);
                        }
                    }
                });
            }
        });

//        fileRef.putFile(imageUri)
//                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//
//                        progressDialog.dismiss();
//                        //Toast.makeText(getApplicationContext(), "Image Uploaded", Toast.LENGTH_LONG).show();
//                        Snackbar.make(findViewById(android.R.id.content), "Image Uploaded", Snackbar.LENGTH_LONG).show();
//
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        progressDialog.dismiss();
//                        Toast.makeText(getApplicationContext(), "Failed To Upload", Toast.LENGTH_LONG).show();
//                    }
//                })
//                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
//                        double progressPercent = (100.00 + taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
//                        progressDialog.setMessage("Progress: " + (int) progressPercent + "%");
//                    }
//                });

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