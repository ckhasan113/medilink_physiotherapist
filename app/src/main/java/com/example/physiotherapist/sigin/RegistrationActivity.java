package com.example.physiotherapist.sigin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.physiotherapist.LoadingDialog;
import com.example.physiotherapist.MainActivity;
import com.example.physiotherapist.R;
import com.example.physiotherapist.pojo.VendorDetails;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegistrationActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST_CODE = 848;
    private static final int PERMISSION_CODE = 8972;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    private DatabaseReference rootRef;
    private DatabaseReference userRef;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private String userID, photoLink, name, registrationNumber, area , city, history, phone, email, password, rePassword;


    private CircleImageView addImage;

    private Uri ImageUrl_main;

    private EditText nameEdit, registerEdt, areaEdt, cityEdt, historyEdt, phoneEdt, emailEdt, passEdt, rePassEdt;

    private boolean isPermissionGranted = false;

    private LoadingDialog dialog;

    private CardView registration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        dialog = new LoadingDialog(RegistrationActivity.this, "Loading...");

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        rootRef = FirebaseDatabase.getInstance().getReference();

        firebaseAuth = FirebaseAuth.getInstance();

        addImage = findViewById(R.id.addimagebtn);

        nameEdit = findViewById(R.id.physiotherapist_vendor_name);
        registerEdt = findViewById(R.id.registerNumber);
        areaEdt = findViewById(R.id.physiotherapist_vendor_area);
        cityEdt = findViewById(R.id.physiotherapist_vendor_City);
        historyEdt = findViewById(R.id.physiotherapist_vendor_history);
        phoneEdt = findViewById(R.id.phone_number);
        emailEdt = findViewById(R.id.email);
        passEdt = findViewById(R.id.password);
        rePassEdt = findViewById(R.id.re_password);


        registration = findViewById(R.id.physiotherapist_vendor_register);

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();
                if(isPermissionGranted){
                    openGallery();
                }else {
                    Toast.makeText(RegistrationActivity.this, "Please Allow Permission", Toast.LENGTH_SHORT).show();
                }
            }
        });

        registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userRegistration();
            }
        });
    }

    private void userRegistration() {
        if (ImageUrl_main == null){
            Toast.makeText(RegistrationActivity.this, "Select a picture", Toast.LENGTH_SHORT).show();
            return;
        }

        name = nameEdit.getText().toString();
        if (name.isEmpty()){
            nameEdit.setError("Enter name");
            nameEdit.requestFocus();
            return;
        }

        registrationNumber = registerEdt.getText().toString();
        if (registrationNumber.isEmpty()){
            registerEdt.setError("Enter Registration  Number");
            registerEdt.requestFocus();
            return;
        }

        area = areaEdt.getText().toString();
        if (area.isEmpty()){
            areaEdt.setError("Enter area");
            areaEdt.requestFocus();
            return;
        }

        city = cityEdt.getText().toString();
        if (city.isEmpty()){
            cityEdt.setError("Enter area");
            cityEdt.requestFocus();
            return;
        }


        history = historyEdt.getText().toString();
        if (history.isEmpty()){
            historyEdt.setError("Enter History");
            historyEdt.requestFocus();
            return;
        }

        phone = phoneEdt.getText().toString();
        if (phone.isEmpty()){
            phoneEdt.setError("Enter phone");
            phoneEdt.requestFocus();
            return;
        }

        email = emailEdt.getText().toString();
        if (email.isEmpty()){
            emailEdt.setError("Enter email");
            emailEdt.requestFocus();
            return;
        }

        password = passEdt.getText().toString()+"physio";
        if (password.isEmpty()){
            passEdt.setError("Enter password");
            passEdt.requestFocus();
            return;
        }

        rePassword = rePassEdt.getText().toString()+"physio";
        if (rePassword.isEmpty()){
            rePassEdt.setError("Enter password");
            rePassEdt.requestFocus();
            return;
        }

        if (!(rePassword.equals(password))){
            Toast.makeText(RegistrationActivity.this, "Password does not match", Toast.LENGTH_SHORT).show();
            rePassEdt.setError("Enter same password");
            rePassEdt.requestFocus();
            return;
        }

        dialog.show();

        registerUser();
    }

    private void registerUser() {
        try {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                firebaseAuth = FirebaseAuth.getInstance();
                                rootRef = FirebaseDatabase.getInstance().getReference();
                                user = firebaseAuth.getCurrentUser();
                                userRef = rootRef.child("Physiotherapist").child(user.getUid());
                                userID = user.getUid();
                                Toast.makeText(RegistrationActivity.this, "Successful", Toast.LENGTH_SHORT).show();

                                userRegistrationData();
                            }else {
                                dialog.dismiss();
                                Toast.makeText(RegistrationActivity.this, "Failed to register...", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });
        }catch (Exception e){
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    private void userRegistrationData() {
        storageReference = FirebaseStorage.getInstance().getReference();
        final Uri imageUri = ImageUrl_main;
        final StorageReference imageRef = storageReference.child("PhysiotherapistVendorImage").child(imageUri.getLastPathSegment());
        UploadTask uploadTask = imageRef.putFile(imageUri);

        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return imageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                Uri downloadUri = task.getResult();
                photoLink = downloadUri.toString();
                VendorDetails details = new VendorDetails(userID, photoLink, name, registrationNumber, area, city, history, phone, email, password, "pv1");
                userRef.child("Details").setValue(details).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            dialog.dismiss();
                            Toast.makeText(RegistrationActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                            String token = FirebaseInstanceId.getInstance().getToken();
                            Map<String, Object> tokenMap = new HashMap<>();
                            tokenMap.put("device_token", token);

                            rootRef.child("Users").child(user.getUid()).child("Token").setValue(tokenMap);

                            dialog.dismiss();
                            startActivity(intent);
                            finish();

                        } else {
                            dialog.dismiss();
                            Toast.makeText(RegistrationActivity.this, "Failed to registration", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,GALLERY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == GALLERY_REQUEST_CODE){
            ImageUrl_main = data.getData();
            addImage.setImageURI(ImageUrl_main);
        }
    }

    private void checkPermission() {
        if ((ActivityCompat
                .checkSelfPermission(RegistrationActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) &&
                (ActivityCompat
                        .checkSelfPermission(RegistrationActivity.this
                                ,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(RegistrationActivity.this,
                    new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    },PERMISSION_CODE);

        }else {
            isPermissionGranted = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode ==PERMISSION_CODE){

            if((grantResults[0] ==PackageManager.PERMISSION_GRANTED
                    && grantResults[1] ==PackageManager.PERMISSION_GRANTED
            )){
                isPermissionGranted = true;
            }else {
                checkPermission();
            }
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
        finish();
    }
}
