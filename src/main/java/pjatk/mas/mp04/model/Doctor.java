package pjatk.mas.mp04.model;

import jakarta.persistence.*;

@Entity
@Table(name = "doctors")
@PrimaryKeyJoinColumn(name = "staff_id")
public class Doctor extends ClinicStaff {

    private String licenseNumber;
    private String specialization;

    public Doctor() {}

    public Doctor(String firstName, String lastName, String department,
                  String licenseNumber, String specialization) {
        super(firstName, lastName, department);
        if (licenseNumber == null || licenseNumber.isBlank())
            throw new IllegalArgumentException("License number cannot be blank");
        if (specialization == null || specialization.isBlank())
            throw new IllegalArgumentException("Specialization cannot be blank");
        this.licenseNumber  = licenseNumber;
        this.specialization = specialization;
    }

    @Column(unique = true, nullable = false)
    public String getLicenseNumber() {
        return licenseNumber;
    }
    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    @Column(nullable = false)
    public String getSpecialization() {
        return specialization;
    }
    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    @Override
    public String generateWorkReport() {
        return String.format("Doctor %s | Specialization: %s | License: %s",
                getFullName(), specialization, licenseNumber);
    }

    @Override
    public String toString() {
        return String.format("Doctor[%s]: Dr. %s, %s, dept: %s",
                licenseNumber, getFullName(), specialization, getDepartment());
    }
}
