package pjatk.mas.mp04.model;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * RM – Associations demo: one Doctor has many Appointments (1-to-many).
 * Appointment is the "many" side — it holds the foreign key to Doctor.
 */
@Entity
@Table(name = "appointments")
public class Appointment {

    private Long id;
    private Doctor doctor;
    private LocalDate date;
    private String notes;

    /** Required by Hibernate. */
    public Appointment() {}

    public Appointment(Doctor doctor, LocalDate date, String notes) {
        if (doctor == null)                   throw new IllegalArgumentException("Doctor cannot be null");
        if (date == null)                     throw new IllegalArgumentException("Date cannot be null");
        if (notes == null || notes.isBlank()) throw new IllegalArgumentException("Notes cannot be blank");

        this.doctor = doctor;
        this.date   = date;
        this.notes  = notes;

        // register on the Doctor side of the bidirectional relationship
        doctor.addAppointment(this);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() { return id; }
    private void setId(Long id) { this.id = id; }

    // --- many Appointments → one Doctor ---

    @ManyToOne(optional = false)
    @JoinColumn(name = "doctor_id")
    public Doctor getDoctor() { return doctor; }
    public void setDoctor(Doctor v) { this.doctor = v; }

    @Column(nullable = false)
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate v) { this.date = v; }

    @Column(nullable = false)
    public String getNotes() { return notes; }
    public void setNotes(String v) {
        if (v == null || v.isBlank()) throw new IllegalArgumentException("Notes cannot be blank");
        this.notes = v;
    }

    @Override
    public String toString() {
        return String.format("Appointment[date=%s, doctor=Dr. %s, notes=%s]",
                date, doctor.getFullName(), notes);
    }
}
