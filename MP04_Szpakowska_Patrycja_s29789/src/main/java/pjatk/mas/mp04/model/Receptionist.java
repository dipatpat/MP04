package pjatk.mas.mp04.model;

import jakarta.persistence.*;


@Entity
@Table(name = "receptionists")
@PrimaryKeyJoinColumn(name = "staff_id")
public class Receptionist extends ClinicStaff {

    private String deskNumber;
    private int callsHandledThisShift;

    public Receptionist() {}

    public Receptionist(String firstName, String lastName, String department, String deskNumber) {
        super(firstName, lastName, department);
        if (deskNumber == null || deskNumber.isBlank())
            throw new IllegalArgumentException("Desk number cannot be blank");
        this.deskNumber = deskNumber;
        this.callsHandledThisShift = 0;
    }

    @Column(nullable = false)
    public String getDeskNumber() {
        return deskNumber;
    }
    public void setDeskNumber(String deskNumber) {
        this.deskNumber = deskNumber;
    }

    @Column(nullable = false)
    public int getCallsHandledThisShift() {
        return callsHandledThisShift;
    }
    public void setCallsHandledThisShift(int callsHandledThisShift) {
        if (callsHandledThisShift < 0)
            throw new IllegalArgumentException("Calls count cannot be negative");
        this.callsHandledThisShift = callsHandledThisShift;
    }

    public void handleCall() {
        this.callsHandledThisShift++;
    }

    @Override
    public String generateWorkReport() {
        return String.format("Receptionist %s | Desk: %s | Dept: %s | Calls handled: %d",
                getFullName(), deskNumber, getDepartment(), callsHandledThisShift);
    }

    @Override
    public String toString() {
        return String.format("Receptionist[desk=%s]: %s, dept: %s, calls: %d",
                deskNumber, getFullName(), getDepartment(), callsHandledThisShift);
    }
}
