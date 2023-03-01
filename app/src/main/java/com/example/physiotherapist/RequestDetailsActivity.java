package com.example.physiotherapist;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.physiotherapist.pojo.ClientRequest;
import com.squareup.picasso.Picasso;

public class RequestDetailsActivity extends AppCompatActivity {

    private TextView packageNameTV, packagePriceTV, packageDateTV, patientNameTV, patientPhoneTV, patientEmailTV, doctorNameTV;

    private ImageView  prescriptionIV;

    private LinearLayout callLO, emailLO;

    private ClientRequest clientRequest;

    private LoadingDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_details);

        clientRequest = (ClientRequest) getIntent().getSerializableExtra("RequestDetails");

        dialog = new LoadingDialog(RequestDetailsActivity.this,"Loading...");
        dialog.show();

        packageNameTV = findViewById(R.id.req_details_physio_pack_name);
        packagePriceTV = findViewById(R.id.req_details_physio_pack_price);
        packageDateTV = findViewById(R.id.req_details_physio_show_date);
        patientNameTV = findViewById(R.id.req_details_physio_pat_name);
        patientPhoneTV = findViewById(R.id.req_details_physio_pat_phone);
        patientEmailTV = findViewById(R.id.req_details_physio_pat_email);
        doctorNameTV = findViewById(R.id.req_details_physio_doc_ref);

        callLO = findViewById(R.id.req_details_physio_callLO);
        emailLO = findViewById(R.id.req_details_physio_mailLO);
        prescriptionIV = findViewById(R.id.req_details_physio_prescription_view);

        packageNameTV.setText(clientRequest.getPackageDetails().getPackageName());
        packagePriceTV.setText(clientRequest.getPackageDetails().getPackagePrice());
        packageDateTV.setText(clientRequest.getBookingDate());
        patientNameTV.setText("Name: "+clientRequest.getPatientDetails().getFirstName()+" "+clientRequest.getPatientDetails().getLastName());
        patientPhoneTV.setText(clientRequest.getPatientDetails().getPhone());
        patientEmailTV.setText(clientRequest.getPatientDetails().getEmail());
        doctorNameTV.setText(clientRequest.getRefDoctorName());

        Picasso.get().load(Uri.parse(clientRequest.getPrescriptionImage())).into(prescriptionIV);

        dialog.dismiss();

        callLO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uri = "tel:" + clientRequest.getPatientDetails().getPhone().trim();
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse(uri));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    Activity#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for Activity#requestPermissions for more details.
                        return;
                    }
                }
                startActivity(intent);
            }
        });

        emailLO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri data = Uri.parse("mailto:"+clientRequest.getPatientDetails().getEmail());
                intent.setData(data);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RequestDetailsActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
