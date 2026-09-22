package com.yifan.traceability.entity;

import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity // table
@Table(name = "locations")   // maps to the plural table name
@Getter                      // Lombok generates all getters
@Setter                      // Lombok generates all setters
@NoArgsConstructor           // Lombok generates the no-arg constructor JPA needs
public class Location {

    @Id // columns
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long locationId; // locationId → column location_id

    // → column name, zone
    private String name;
    private String zone;
}

    /*
    public Location(long id, string name, String zone){}

    public Location(long id, string name, String zone){
        locationID = id;
        locationName = name;
        zoneName = zone;
    }

    public long getLocationID() {
        return locationID;
    }

    public string getLocationName() {
        return locationName;
    }

    public string getZoneName() {
        return zoneName;
    }

    public void setLocationID(long locationID) {
        this.locationID = locationID;
    }

    public void setLocationName(string locationName) {
        this.locationName = locationName;
    }

    public void setZoneName(string zoneName) {
        this.zoneName = zoneName;
    }
    */
