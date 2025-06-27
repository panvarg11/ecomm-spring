package com.mpantoja.sbecommerce.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "addresses")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    @NotBlank
    @Size(min = 3, message = "street name must be at least 3 characters")
    private String street;

    @NotBlank
    @Size(min = 3, message = "Building name must be at least 3 characters")
    private String buildingName;

    @NotBlank
    @Size(min = 2, message = "Building name must be at least 2 characters")
    private String cityName;

    @NotBlank
    @Size(min = 2, message = "State name must be at least 2 characters")
    private String stateName;

    @NotBlank
    @Size(min = 2, message = "Country name must be at least 2 characters")
    private String country;

    @NotBlank
    @Size(min = 5, message = "Zip Code name must be at least 2 characters")
    private String zipcode;

    @ToString.Exclude
    @ManyToMany(mappedBy = "addresses")
    private List<User> users = new ArrayList<>();

    public Address(String street, String buildingName, String cityName, String stateName, String country, String zipcode) {
        this.street = street;
        this.buildingName = buildingName;
        this.cityName = cityName;
        this.stateName = stateName;
        this.country = country;
        this.zipcode = zipcode;
    }


}
