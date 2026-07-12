/*
* - Class annotations: @Entity, @Table(name = "locations"), and Lombok's @Getter, @Setter, @NoArgsConstructor.
- Three fields:
  - locationId — Long, annotated @Id and @GeneratedValue(strategy = GenerationType.IDENTITY) (matches BIGSERIAL).
  - name — String.
  - zone — String.

Reminders:
- BIGSERIAL/BIGINT → Long, VARCHAR → String.
- camelCase → snake_case is automatic (locationId → location_id), so @Column is optional here; add @Column(name = "location_id") on the id if you want it explicit.
- The imports come from jakarta.persistence.* (for @Entity, @Id, etc.) and lombok.* — IntelliJ will offer to auto-import as you type the annotations.
*/

public class Location {

    private long locationID;
    private string locationName;
    private string zoneName;

    public Location(long id, string name, string zone){
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
}