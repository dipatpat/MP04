package pjatk.mas.mp04.model;

import jakarta.persistence.*;
import org.hibernate.annotations.NaturalId;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;


/**
 * RM – Classes demo:
 *   required attributes  : nationalHealthId, firstName, lastName, birthDate, homeAddress
 *   optional attribute   : email  (nullable column)
 *   multi-valued attribute: allergies (@ElementCollection → separate table)
 *   derived attribute    : age    (@Transient – computed, never stored)
 *   embedded component   : homeAddress (@Embedded → columns inlined into this table)
 */
@Entity
@Table(name = "patients")
public class Patient {

    private Long id;
    private String nationalHealthId;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private Address homeAddress;
    private String email;
    private List<String> allergies = new ArrayList<>();

    /** Required by Hibernate. */
    public Patient() {}

    public Patient(String nationalHealthId, String firstName, String lastName,
                   LocalDate birthDate, Address homeAddress) {
        if (nationalHealthId == null || nationalHealthId.isBlank())
            throw new IllegalArgumentException("National health ID cannot be blank");
        if (firstName == null || firstName.isBlank())
            throw new IllegalArgumentException("First name cannot be blank");
        if (lastName == null || lastName.isBlank())
            throw new IllegalArgumentException("Last name cannot be blank");
        if (birthDate == null)
            throw new IllegalArgumentException("Birth date cannot be null");
        if (birthDate.isAfter(LocalDate.now()))
            throw new IllegalArgumentException("Birth date cannot be in the future");
        if (homeAddress == null)
            throw new IllegalArgumentException("Home address cannot be null");

        this.nationalHealthId = nationalHealthId;
        this.firstName        = firstName;
        this.lastName         = lastName;
        this.birthDate        = birthDate;
        this.homeAddress      = homeAddress;
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }
    private void setId(Long id) {
        this.id = id;
    }

    @NaturalId
    @Column(unique = true, nullable = false)
    public String getNationalHealthId() {
        return nationalHealthId;
    }
    public void setNationalHealthId(String nationalHealthId) {
        this.nationalHealthId = nationalHealthId;
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
    public LocalDate getBirthDate() {
        return birthDate;
    }
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }


    @Embedded
    public Address getHomeAddress() {
        return homeAddress;
    }
    public void setHomeAddress(Address homeAddress) {
        this.homeAddress = homeAddress;
    }


    @Column(nullable = true)
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }


    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "patient_allergies", joinColumns = @JoinColumn(name = "patient_id"))
    @Column(name = "allergy")
    public List<String> getAllergies() {
        return allergies;
    }
    public void setAllergies(List<String> allergies) {
        this.allergies = allergies;
    }


    @Transient
    public int getAge() {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    @Transient
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public void addAllergy(String allergy) {
        if (allergy == null || allergy.isBlank())
            throw new IllegalArgumentException("Allergy cannot be blank");
        if (allergies.stream().anyMatch(a -> a.equalsIgnoreCase(allergy.trim())))
            throw new IllegalArgumentException("Allergy '" + allergy.trim() + "' already recorded");
        allergies.add(allergy.trim());
    }

    @Override
    public String toString() {
        return String.format("Patient[%s]: %s, dob: %s, age: %d, address: %s, email: %s, allergies: %s",
                nationalHealthId, getFullName(), birthDate, getAge(), homeAddress,
                email != null ? email : "(none)",
                allergies.isEmpty() ? "(none)" : String.join(", ", allergies));
    }
}
