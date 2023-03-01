package com.example.physiotherapist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.physiotherapist.adapter.ClientRequestAdapter;
import com.example.physiotherapist.adapter.PackageListAdapter;
import com.example.physiotherapist.packages.AddNewPackageActivity;
import com.example.physiotherapist.pojo.ClientRequest;
import com.example.physiotherapist.pojo.PackageDetails;
import com.example.physiotherapist.pojo.VendorDetails;
import com.example.physiotherapist.sigin.LoginActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, PackageListAdapter.PackageListAdapterListener, ClientRequestAdapter.ClientRequestAdapterListener {

    private static final int GALLERY_REQUEST_CODE = 848;
    private static final int PERMISSION_CODE = 8972;

    private boolean isPermissionGranted = false;

    private Uri ImageUrl_main;

    private FirebaseAuth auth;
    private FirebaseUser user;

    private DatabaseReference rootRef;
    private DatabaseReference userRef;
    private DatabaseReference packageRef;
    private DatabaseReference requestRef;

    private Toolbar toolbar;

    private LoadingDialog dialog;

    private ImageView vendorImageIV;

    private TextView vendorNmeTV, vendorAddressTV, vendorHistoryTV, uploadPhotoTV, savePhotoTV;

    private RadioButton packageRB, requestRB;

    private Button addNewPackageBtn;

    private RecyclerView baseRecycler;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private String userID, photoLink, name, registrationNumber, area , city, history, phone, email, password;

    private PackageListAdapter packageAdapter;
    private ClientRequestAdapter requestAdapter;

    private List<PackageDetails> packageList = new ArrayList<PackageDetails>();
    private List<ClientRequest> requestList = new ArrayList<ClientRequest>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dialog = new LoadingDialog(MainActivity.this,"Loading...");
        dialog.show();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        rootRef = FirebaseDatabase.getInstance().getReference();
        userRef = rootRef.child("Physiotherapist").child(user.getUid());
        packageRef = userRef.child("Package");
        requestRef = userRef.child("ClientRequest");

        toolbar = (Toolbar) findViewById(R.id.physiotherapist_vendor_toolbar_base);
        setSupportActionBar(toolbar);

        vendorImageIV = findViewById(R.id.vendor_image);
        vendorNmeTV = findViewById(R.id.vendor_name);
        vendorAddressTV = findViewById(R.id.vendor_address);
        vendorHistoryTV = findViewById(R.id.vendor_history);
        uploadPhotoTV = findViewById(R.id.upload_photo);
        savePhotoTV = findViewById(R.id.save_photo);

        packageRB = findViewById(R.id.packageRB);
        requestRB = findViewById(R.id.requestRB);
        addNewPackageBtn = findViewById(R.id.add_new_packageBtn);

        baseRecycler = findViewById(R.id.package_and_request_base_recycler);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                VendorDetails vd = dataSnapshot.child("Details").getValue(VendorDetails.class);

                userID = vd.getVendorID();
                photoLink = vd.getImage();
                name = vd.getName();
                registrationNumber = vd.getLicence();
                area = vd.getArea();
                city = vd.getCity();
                history = vd.getHistory();
                phone = vd.getPhone();
                email = vd.getEmail();
                password = vd.getPassword();

                Picasso.get().load(Uri.parse(photoLink)).into(vendorImageIV);
                vendorNmeTV.setText(name);
                vendorAddressTV.setText(area+", "+city);
                vendorHistoryTV.setText(history);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        packageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                packageList.clear();
                for (DataSnapshot d: dataSnapshot.getChildren()){
                    PackageDetails pd = d.child("Details").getValue(PackageDetails.class);
                    packageList.add(pd);
                }
                Collections.reverse(packageList);
                packageAdapter = new PackageListAdapter(MainActivity.this, packageList);
                LinearLayoutManager llm = new LinearLayoutManager(MainActivity.this);
                llm.setOrientation(RecyclerView.VERTICAL);
                baseRecycler.setLayoutManager(llm);
                baseRecycler.setAdapter(packageAdapter);
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        uploadPhotoTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();
                if(isPermissionGranted){
                    openGallery();
                }else {
                    Toast.makeText(MainActivity.this, "Please Allow Permission", Toast.LENGTH_SHORT).show();
                }
            }
        });

        savePhotoTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                userRegistration();
            }
        });

        addNewPackageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddNewPackageActivity.class);
                startActivity(intent);
                finish();
            }
        });

        packageRB.setOnClickListener(this);
        requestRB.setOnClickListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("notify", "notification", NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        FirebaseMessaging.getInstance().subscribeToTopic("physiotherapist");
    }

    private void userRegistration() {
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
                            Toast.makeText(MainActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();

                        } else {
                            dialog.dismiss();
                            Toast.makeText(MainActivity.this, "Failed to upload", Toast.LENGTH_SHORT).show();

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
            vendorImageIV.setImageURI(ImageUrl_main);
            savePhotoTV.setVisibility(View.VISIBLE);
            uploadPhotoTV.setVisibility(View.GONE);
        }
    }

    private void checkPermission() {
        if ((ActivityCompat
                .checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) &&
                (ActivityCompat
                        .checkSelfPermission(MainActivity.this
                                ,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(MainActivity.this,
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_item,menu);
        return true;
    }

    public void log_out(MenuItem item){
        dialog.show();
        Map<String, Object> tokenMap = new HashMap<>();
        tokenMap.put("device_token", "");
        rootRef.child("Users").child(user.getUid()).child("Token").updateChildren(tokenMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                auth.signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                dialog.dismiss();
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.packageRB){
            addNewPackageBtn.setVisibility(View.VISIBLE);
            packageRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    packageList.clear();
                    for (DataSnapshot d: dataSnapshot.getChildren()){
                        PackageDetails pd = d.child("Details").getValue(PackageDetails.class);
                        packageList.add(pd);
                    }
                    Collections.reverse(packageList);
                    packageAdapter = new PackageListAdapter(MainActivity.this, packageList);
                    LinearLayoutManager llm = new LinearLayoutManager(MainActivity.this);
                    llm.setOrientation(RecyclerView.VERTICAL);
                    baseRecycler.setLayoutManager(llm);
                    baseRecycler.setAdapter(packageAdapter);
                    dialog.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else if (view.getId() == R.id.requestRB){
            addNewPackageBtn.setVisibility(View.GONE);
            requestRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    requestList.clear();
                    for (DataSnapshot d: dataSnapshot.getChildren()){
                        ClientRequest pd = d.child("Details").getValue(ClientRequest.class);
                        requestList.add(pd);
                    }
                    Collections.reverse(requestList);
                    requestAdapter = new ClientRequestAdapter(MainActivity.this, requestList);
                    LinearLayoutManager llm = new LinearLayoutManager(MainActivity.this);
                    llm.setOrientation(RecyclerView.VERTICAL);
                    baseRecycler.setLayoutManager(llm);
                    baseRecycler.setAdapter(requestAdapter);
                    dialog.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void onPackageDetails(PackageDetails packageDetails) {
        Intent intent = new Intent(MainActivity.this, AddNewPackageActivity.class);
        intent.putExtra("PackageDetails", packageDetails);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRequestDetails(ClientRequest cr) {
        Intent intent = new Intent(MainActivity.this, RequestDetailsActivity.class);
        intent.putExtra("RequestDetails", cr);
        startActivity(intent);
        finish();
    }
}
