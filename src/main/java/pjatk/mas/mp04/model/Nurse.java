package pjatk.mas.mp04.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "nurses")
public class Nurse {

    private Long id;
    private String firstName;
    private String lastName;
    private String licenseNumber;
    private List<Ward> wards = new ArrayList<>();

    public Nurse() {}

    public Nurse(String firstName, String lastName, String licenseNumber) {
        if (firstName == null || firstName.isBlank()){
            throw new IllegalArgumentException("First name cannot be blank");
        }
        if (lastName == null || lastName.isBlank())  {
            throw new IllegalArgumentException("Last name cannot be blank");
        }
        if (licenseNumber == null || licenseNumber.isBlank()) {
            throw new IllegalArgumentException("License number cannot be blank");
        }
        this.firstName     = firstName;
        this.lastName      = lastName;
        this.licenseNumber = licenseNumber;
    }

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

    @Column(unique = true, nullable = false)
    public String getLicenseNumber() {
        return licenseNumber;
    }
    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    @ManyToMany(mappedBy = "nurses")
    public List<Ward> getWards() {
        return wards;
    }
    public void setWards(List<Ward> wards) {
        this.wards = wards;
    }

    public void addWard(Ward ward) {
        if (ward == null) throw new IllegalArgumentException("Ward cannot be null");
        if (!wards.contains(ward)) {
            wards.add(ward);
            ward.addNurse(this);
        }
    }

    public void removeWard(Ward ward) {
        if (wards.remove(ward)) {
            ward.removeNurse(this);
        }
    }

    @Transient
    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public String toString() {
        String wardList = wards.isEmpty() ? "none"
                : String.join(", ", wards.stream().map(Ward::getWardCode).toList());
        return String.format("Nurse[%s]: %s | wards: %s", licenseNumber, getFullName(), wardList);
    }
}
