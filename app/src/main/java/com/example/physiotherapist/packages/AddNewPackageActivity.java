package com.example.physiotherapist.packages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.physiotherapist.LoadingDialog;
import com.example.physiotherapist.MainActivity;
import com.example.physiotherapist.R;
import com.example.physiotherapist.pojo.PackageDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class AddNewPackageActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth auth;
    private FirebaseUser user;

    private DatabaseReference rootRef;
    private DatabaseReference userRef;

    private Spinner packageIsp;

    private LinearLayout startTimeLO, endTimeLO;
    private TextView startTimeTV, endTimeTV;

    private EditText priceEdt;

    private int mHour, mMinute;

    private String id, name, start, end, price;

    private Button addPackageBtn, updatePackageBtn;

    private LoadingDialog dialog;

    private PackageDetails packageDetails;

    private String packageArray[] ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_package);

        dialog = new LoadingDialog(AddNewPackageActivity.this, "Please wait...");

        startTimeLO = findViewById(R.id.start_time);
        endTimeLO = findViewById(R.id.end_time);
        startTimeTV = findViewById(R.id.start_time_TV);
        endTimeTV = findViewById(R.id.end_time_TV);
        priceEdt = findViewById(R.id.price);
        packageIsp = findViewById(R.id.isp_package);
        addPackageBtn = findViewById(R.id.add_new_package_info);
        updatePackageBtn = findViewById(R.id.update_package_info);

        packageDetails = (PackageDetails) getIntent().getSerializableExtra("PackageDetails");

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();


        rootRef = FirebaseDatabase.getInstance().getReference();
        userRef = rootRef.child("Physiotherapist").child(user.getUid()).child("Package");

        ArrayAdapter spinnerPackageAdapter = ArrayAdapter.createFromResource(AddNewPackageActivity.this, R.array.package_array, R.layout.spinner_item_select_model);
        spinnerPackageAdapter.setDropDownViewResource(R.layout.spinner_item_drop_down_model);
        packageIsp.setAdapter(spinnerPackageAdapter);

        packageIsp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                name = packageIsp.getItemAtPosition(i).toString().trim();
                if (name.equals("Hourly")){
                    startTimeLO.setVisibility(View.GONE);
                    endTimeLO.setVisibility(View.GONE);
                    start = "Select";
                    end = "Select";
                    return;
                }else if (name.equals("Daily")){

                    startTimeLO.setVisibility(View.VISIBLE);
                    endTimeLO.setVisibility(View.VISIBLE);

                    return;
                }else if (name.equals("Weekly")){
                    startTimeLO.setVisibility(View.GONE);
                    endTimeLO.setVisibility(View.GONE);
                    start = "Select";
                    end = "Select";
                    return;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if (packageDetails == null){
        }else {
            addPackageBtn.setVisibility(View.GONE);
            updatePackageBtn.setVisibility(View.VISIBLE);

            id = packageDetails.getPackageId();
            start = packageDetails.getStartTime();
            end = packageDetails.getEndTime();

            startTimeTV.setText(start);
            endTimeTV.setText(end);
            priceEdt.setText(packageDetails.getPackagePrice());

            name = packageDetails.getPackageName();
            selectPackage(name);
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,R.layout.spinner_item_select_model, packageArray);
            arrayAdapter.setDropDownViewResource(R.layout.spinner_item_drop_down_model);
            packageIsp.setAdapter(arrayAdapter);
        }

        startTimeTV.setOnClickListener(this);
        endTimeTV.setOnClickListener(this);

        addPackageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (packageIsp.equals("Select")){
                    Toast.makeText(AddNewPackageActivity.this, "Must Select a package", Toast.LENGTH_SHORT).show();
                    return;
                }

                price = priceEdt.getText().toString().trim();
                if (price.isEmpty()){
                    priceEdt.setError("Price is required");
                    priceEdt.requestFocus();
                    return;
                }

                if(start.isEmpty() || end.isEmpty()){
                    Toast.makeText(AddNewPackageActivity.this, "Time Schedule missing...!", Toast.LENGTH_SHORT).show();
                }else {
                    dialog.show();
                    id = userRef.push().getKey();
                    addPackageInfo();
                }
            }
        });

        updatePackageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((start.equals("Select") || end.equals("Select")) && name.equals("Daily")){
                    Toast.makeText(AddNewPackageActivity.this, "Time Schedule missing...!", Toast.LENGTH_SHORT).show();
                }else {
                    dialog.show();
                    price = priceEdt.getText().toString().trim();
                    addPackageInfo();
                }
            }
        });
    }

    private void selectPackage(String name) {
        if (name.equals("Hourly")){
            packageArray = new String[]{"Hourly","Daily","Weekly"};
        }
        else if (name.equals("Daily")){
            packageArray = new String[]{"Daily","Weekly","Hourly"};
        }
        else if (name.equals("Weekly")){
            packageArray = new String[]{"Weekly","Hourly","Daily"};
        }
    }

    private void addPackageInfo() {
        PackageDetails pd = new PackageDetails(id, name, price, start, end);

        userRef.child(id).child("Details").setValue(pd).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    dialog.dismiss();
                    Toast.makeText(AddNewPackageActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AddNewPackageActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    dialog.dismiss();
                    Toast.makeText(AddNewPackageActivity.this, "Failed....", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AddNewPackageActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.start_time_TV){
            // Get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            if (hourOfDay>11) {
                                if (hourOfDay==12){
                                    startTimeTV.setText(hourOfDay + ":" + minute+" PM");
                                    start = startTimeTV.getText().toString();
                                }else {
                                    int cou = hourOfDay-12;
                                    startTimeTV.setText(cou + ":" + minute+" PM");
                                    start = startTimeTV.getText().toString();
                                }


                            }else {
                                startTimeTV.setText(hourOfDay + ":" + minute+" AM");
                                start = startTimeTV.getText().toString();
                            }

                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();

        }else if (view.getId()==R.id.end_time_TV){
            // Get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            if (hourOfDay>11) {
                                if (hourOfDay==12){
                                    endTimeTV.setText(hourOfDay + ":" + minute+" PM");
                                    end = endTimeTV.getText().toString();
                                }else {
                                    int cou = hourOfDay-12;
                                    endTimeTV.setText(cou + ":" + minute+" PM");
                                    end = endTimeTV.getText().toString();
                                }


                            }else {
                                endTimeTV.setText(hourOfDay + ":" + minute+" AM");
                                end = endTimeTV.getText().toString();
                            }
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();

        }
    }
}
