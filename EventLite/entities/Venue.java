package uk.ac.man.cs.eventlite.entities;
 
import javax.persistence.*;
 
@Entity
@Table(name = "venues")
public class Venue {
 
    @Id
    @GeneratedValue
    private long id;
 
    private String name;
   
    private String address;
   
    private String road;
   
    private String postcode;
 
    private int capacity;
   
    private int eventcount;
   
    private String latitude;
   
    private String longitude;
    
    private String organiser;
 
    public Venue() {
    }
 
    public long getId() {
        return id;
    }
 
    public void setId(long id) {
        this.id = id;
    }
 
    public String getName() {
        return name;
    }
 
    public void setName(String name) {
        this.name = name;
    }
 
    public int getCapacity() {
        return capacity;
    }
 
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
   
    public String getAddress() {
        return address;
    }
 
    public void setAddress(String address) {
        this.address = address;
    }
   
    public String getRoad() {
        return road;
    }
 
    public void setRoad(String road) {
        this.road = road;
    }
   
    public String getPostcode() {
        return postcode;
    }
 
    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }
   
    public int getEventcount() {
        return eventcount;
    }
 
    public void setEventcount(int count) {
        this.eventcount = count;
    }
   
    public String getLatitude() {
        return latitude;
    }
 
    public void setLatitude(String lat) {
        this.latitude = lat;
    }
   
    public String getLongitude() {
        return longitude;
    }
 
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
    
	public void setOrganiser(String description) {
		this.organiser = description;
	}
	
	public String getOrganiser() {
		return organiser;
	}
}