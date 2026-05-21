package pjatk.mas.mp04.model;

import jakarta.persistence.*;

@Entity
@Table(name = "doctors")
@PrimaryKeyJoinColumn(name = "staff_id")
public class Doctor extends ClinicStaff {

    private String medicalLicenseNumber;
    private String specialization;
    private int appointmentsThisShift;

    public Doctor() {}

    public Doctor(String firstName, String lastName, String department,
                  String medicalLicenseNumber, String specialization) {
        super(firstName, lastName, department);
        if (medicalLicenseNumber == null || medicalLicenseNumber.isBlank())
            throw new IllegalArgumentException("License number cannot be blank");
        if (specialization == null || specialization.isBlank())
            throw new IllegalArgumentException("Specialization cannot be blank");
        this.medicalLicenseNumber = medicalLicenseNumber;
        this.specialization       = specialization;
        this.appointmentsThisShift = 0;
    }

    @Column(unique = true, nullable = false)
    public String getMedicalLicenseNumber() {
        return medicalLicenseNumber;
    }
    public void setMedicalLicenseNumber(String medicalLicenseNumber) {
        this.medicalLicenseNumber = medicalLicenseNumber;
    }

    @Column(nullable = false)
    public String getSpecialization() {
        return specialization;
    }
    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    @Column(nullable = false)
    public int getAppointmentsThisShift() {
        return appointmentsThisShift;
    }
    public void setAppointmentsThisShift(int appointmentsThisShift) {
        if (appointmentsThisShift < 0)
            throw new IllegalArgumentException("Appointments count cannot be negative");
        this.appointmentsThisShift = appointmentsThisShift;
    }

    public void recordAppointment() {
        this.appointmentsThisShift++;
    }

    @Override
    public String generateWorkReport() {
        return String.format("Doctor %s | Specialization: %s | License: %s | Appointments today: %d",
                getFullName(), specialization, medicalLicenseNumber, appointmentsThisShift);
    }

    @Override
    public String toString() {
        return String.format("Doctor[%s]: Dr. %s, %s, dept: %s, appointments: %d",
                medicalLicenseNumber, getFullName(), specialization, getDepartment(), appointmentsThisShift);
    }
}
