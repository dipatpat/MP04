package pjatk.mas.mp04.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * RM – Inheritance demo (subclass via JOINED).
 * RM – Associations demo: one Doctor has many Appointments (1-to-many).
 */
@Entity
@Table(name = "doctors")
@PrimaryKeyJoinColumn(name = "staff_id")
public class Doctor extends ClinicStaff {

    private String licenseNumber;
    private String specialization;
    private List<Appointment> appointments = new ArrayList<>();

    /** Required by Hibernate. */
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
    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String v) { this.licenseNumber = v; }

    @Column(nullable = false)
    public String getSpecialization() { return specialization; }
    public void setSpecialization(String v) { this.specialization = v; }

    // --- 1-to-many: one Doctor → many Appointments ---

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<Appointment> getAppointments() { return appointments; }
    public void setAppointments(List<Appointment> v) { this.appointments = v; }

    public Appointment scheduleAppointment(LocalDate date, String notes) {
        return new Appointment(this, date, notes);
    }

    void addAppointment(Appointment a) {
        if (!appointments.contains(a)) appointments.add(a);
    }

    void removeAppointment(Appointment a) {
        appointments.remove(a);
    }

    @Override
    public String generateWorkReport() {
        return String.format("Doctor %s | Specialization: %s | License: %s | Appointments: %d",
                getFullName(), specialization, licenseNumber, appointments.size());
    }

    @Override
    public String toString() {
        return String.format("Doctor[%s]: Dr. %s, %s, dept: %s",
                licenseNumber, getFullName(), specialization, getDepartment());
    }
}
