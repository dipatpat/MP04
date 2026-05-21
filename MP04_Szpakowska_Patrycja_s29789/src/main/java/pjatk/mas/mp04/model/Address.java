package pjatk.mas.mp04.model;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Address {

    private String street;
    private String buildingNumber;
    private String apartmentNumber;
    private String city;
    private String postCode;

    public Address() {}

    public Address(String street, String buildingNumber, String city, String postCode) {
        if (street == null || street.isBlank())          {
            throw new IllegalArgumentException("Street cannot be blank");
        }
        if (buildingNumber == null || buildingNumber.isBlank()) {
            throw new IllegalArgumentException("Building number cannot be blank");
        }
        if (city == null || city.isBlank())              {
            throw new IllegalArgumentException("City cannot be blank");
        }
        if (postCode == null || postCode.isBlank())     {
            throw new IllegalArgumentException("Post code cannot be blank");
        }
        this.street         = street;
        this.buildingNumber = buildingNumber;
        this.city           = city;
        this.postCode       = postCode;
    }

    @Basic public String getStreet() { {
        return street;
    } }
    public void setStreet(String street) {
        this.street = street; }

    @Basic public String getBuildingNumber() {
        return buildingNumber;
    }
    public void setBuildingNumber(String buildingNumber) {
        this.buildingNumber = buildingNumber; }

    @Column(nullable = true)
    public String getApartmentNumber() {
        return apartmentNumber; }
    public void setApartmentNumber(String apartmentNumber) {
        this.apartmentNumber = apartmentNumber; }

    @Basic public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }

    @Basic public String getPostCode() {
        return postCode;
    }
    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    @Override
    public String toString() {
        String apt = apartmentNumber != null ? "/" + apartmentNumber : "";
        return street + " " + buildingNumber + apt + ", " + postCode + " " + city;
    }
}
