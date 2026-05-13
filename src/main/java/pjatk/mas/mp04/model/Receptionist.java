package pjatk.mas.mp04.model;

import jakarta.persistence.*;

/**
 * RM – Inheritance demo (subclass via JOINED).
 */
@Entity
@Table(name = "receptionists")
@PrimaryKeyJoinColumn(name = "staff_id")
public class Receptionist extends ClinicStaff {

    private String deskNumber;

    /** Required by Hibernate. */
    public Receptionist() {}

    public Receptionist(String firstName, String lastName, String department, String deskNumber) {
        super(firstName, lastName, department);
        if (deskNumber == null || deskNumber.isBlank())
            throw new IllegalArgumentException("Desk number cannot be blank");
        this.deskNumber = deskNumber;
    }

    @Column(nullable = false)
    public String getDeskNumber() { return deskNumber; }
    public void setDeskNumber(String v) { this.deskNumber = v; }

    @Override
    public String generateWorkReport() {
        return String.format("Receptionist %s | Desk: %s | Dept: %s",
                getFullName(), deskNumber, getDepartment());
    }

    @Override
    public String toString() {
        return String.format("Receptionist[desk=%s]: %s, dept: %s",
                deskNumber, getFullName(), getDepartment());
    }
}
