package pjatk.mas.mp04.model;

import jakarta.persistence.Basic;
import jakarta.persistence.Embeddable;

@Embeddable
public class Address {

    private String street;
    private String city;
    private String zipCode;

    /** Required by Hibernate. */
    public Address() {}

    public Address(String street, String city, String zipCode) {
        if (street == null || street.isBlank())  throw new IllegalArgumentException("Street cannot be blank");
        if (city == null || city.isBlank())      throw new IllegalArgumentException("City cannot be blank");
        if (zipCode == null || zipCode.isBlank()) throw new IllegalArgumentException("Zip code cannot be blank");
        this.street  = street;
        this.city    = city;
        this.zipCode = zipCode;
    }

    @Basic public String getStreet()  { return street; }
    public void setStreet(String s)   { this.street = s; }

    @Basic public String getCity()    { return city; }
    public void setCity(String c)     { this.city = c; }

    @Basic public String getZipCode() { return zipCode; }
    public void setZipCode(String z)  { this.zipCode = z; }

    @Override
    public String toString() {
        return street + ", " + city + " " + zipCode;
    }
}
