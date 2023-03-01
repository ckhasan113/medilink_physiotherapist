package com.example.physiotherapist.pojo;

import java.io.Serializable;

public class ClientRequest implements Serializable {

    private String physioBookingID;

    private VendorDetails vendorDetails;

    private PackageDetails packageDetails;

    private String prescriptionImage;

    private String refDoctorName;

    private String bookingDate;

    private ClientDetails patientDetails;

    public ClientRequest() {
    }

    public ClientRequest(String physioBookingID, VendorDetails vendorDetails, PackageDetails packageDetails, String prescriptionImage, String refDoctorName, String bookingDate, ClientDetails patientDetails) {
        this.physioBookingID = physioBookingID;
        this.vendorDetails = vendorDetails;
        this.packageDetails = packageDetails;
        this.prescriptionImage = prescriptionImage;
        this.refDoctorName = refDoctorName;
        this.bookingDate = bookingDate;
        this.patientDetails = patientDetails;
    }

    public String getPhysioBookingID() {
        return physioBookingID;
    }

    public void setPhysioBookingID(String physioBookingID) {
        this.physioBookingID = physioBookingID;
    }

    public VendorDetails getVendorDetails() {
        return vendorDetails;
    }

    public void setVendorDetails(VendorDetails vendorDetails) {
        this.vendorDetails = vendorDetails;
    }

    public PackageDetails getPackageDetails() {
        return packageDetails;
    }

    public void setPackageDetails(PackageDetails packageDetails) {
        this.packageDetails = packageDetails;
    }

    public String getPrescriptionImage() {
        return prescriptionImage;
    }

    public void setPrescriptionImage(String prescriptionImage) {
        this.prescriptionImage = prescriptionImage;
    }

    public String getRefDoctorName() {
        return refDoctorName;
    }

    public void setRefDoctorName(String refDoctorName) {
        this.refDoctorName = refDoctorName;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public ClientDetails getPatientDetails() {
        return patientDetails;
    }

    public void setPatientDetails(ClientDetails patientDetails) {
        this.patientDetails = patientDetails;
    }
}
