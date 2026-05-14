package pjatk.mas.mp04.model;

import jakarta.persistence.*;


@Entity
@Table(name = "clinic_staff")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class ClinicStaff {

    private Long id;
    private String firstName;
    private String lastName;
    private String department;

    public ClinicStaff() {}

    public ClinicStaff(String firstName, String lastName, String department) {
        if (firstName == null || firstName.isBlank())   {
            throw new IllegalArgumentException("First name cannot be blank");
        }
        if (lastName == null || lastName.isBlank())     {
            throw new IllegalArgumentException("Last name cannot be blank");
        }
        if (department == null || department.isBlank()) {
            throw new IllegalArgumentException("Department cannot be blank");
        }
        this.firstName  = firstName;
        this.lastName   = lastName;
        this.department = department;
    }

    public abstract String generateWorkReport();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }
    private void setId(Long id) {
        this.id = id;
    }

    @Column(nullable = false)
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Column(nullable = false)
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Column(nullable = false)
    public String getDepartment() {
        return department;
    }
    public void setDepartment(String department) {
        this.department = department;
    }

    @Transient
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
