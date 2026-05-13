package pjatk.mas.mp04.model;

import jakarta.persistence.*;

/**
 * RM – Inheritance demo (base class).
 * Uses JOINED strategy: one row in clinic_staff per staff member,
 * plus one row in doctors / nurses for subclass-specific columns.
 */
@Entity
@Table(name = "clinic_staff")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class ClinicStaff {

    private Long id;
    private String firstName;
    private String lastName;
    private String department;

    /** Required by Hibernate. */
    public ClinicStaff() {}

    public ClinicStaff(String firstName, String lastName, String department) {
        if (firstName == null || firstName.isBlank())   throw new IllegalArgumentException("First name cannot be blank");
        if (lastName == null || lastName.isBlank())     throw new IllegalArgumentException("Last name cannot be blank");
        if (department == null || department.isBlank()) throw new IllegalArgumentException("Department cannot be blank");
        this.firstName  = firstName;
        this.lastName   = lastName;
        this.department = department;
    }

    public abstract String generateWorkReport();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() { return id; }
    private void setId(Long id) { this.id = id; }

    @Column(nullable = false)
    public String getFirstName() { return firstName; }
    public void setFirstName(String v) { this.firstName = v; }

    @Column(nullable = false)
    public String getLastName() { return lastName; }
    public void setLastName(String v) { this.lastName = v; }

    @Column(nullable = false)
    public String getDepartment() { return department; }
    public void setDepartment(String v) { this.department = v; }

    @Transient
    public String getFullName() { return firstName + " " + lastName; }
}
