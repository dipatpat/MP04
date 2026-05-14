package pjatk.mas.mp04.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Entity
@Table(name = "wards")
public class Ward {

    private Long id;
    private String wardCode;
    private String name;
    private int floor;
    private List<Nurse> nurses = new ArrayList<>();

    public Ward() {}

    public Ward(String wardCode, String name, int floor) {
        if (wardCode == null || wardCode.isBlank()) {
            throw new IllegalArgumentException("Ward code cannot be blank");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Ward name cannot be blank");
        }
        this.wardCode = wardCode;
        this.name     = name;
        this.floor    = floor;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }
    private void setId(Long id) {
        this.id = id;
    }

    @Column(unique = true, nullable = false)
    public String getWardCode() {
        return wardCode;
    }
    public void setWardCode(String wardCode) {
        this.wardCode = wardCode; }

    @Column(nullable = false)
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public int getFloor() {
        return floor;
    }
    public void setFloor(int floor) {
        this.floor = floor;
    }

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
        name = "ward_nurses",
        joinColumns        = @JoinColumn(name = "ward_id"),
        inverseJoinColumns = @JoinColumn(name = "nurse_id")
    )
    public List<Nurse> getNurses() {
        return Collections.unmodifiableList(nurses);
    }
    public void setNurses(List<Nurse> nurses) {
        this.nurses = nurses;
    }

    public void addNurse(Nurse nurse) {
        if (nurse == null) {
            throw new IllegalArgumentException("Nurse cannot be null");
        }
        if (!nurses.contains(nurse)) {
            nurses.add(nurse);
            nurse.addWard(this);
        }
    }

    public void removeNurse(Nurse nurse) {
        if (nurses.remove(nurse)) {
            nurse.removeWard(this);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Ward[%s]: %s, floor %d%n  Nurses: ", wardCode, name, floor));
        if (nurses.isEmpty()) {
            sb.append("none");
        }
        else {
            nurses.forEach(n -> sb.append(n.getFullName()).append("; "));
        }
        return sb.toString();
    }
}
