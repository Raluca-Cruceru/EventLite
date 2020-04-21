package uk.ac.man.cs.eventlite.entities;

import java.util.Date;

import javax.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "events")
public class Event {

	@Id
	@GeneratedValue
	private long id;

	@Temporal(TemporalType.DATE)
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date date;

	@Temporal(TemporalType.TIME)
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	@DateTimeFormat(pattern = "HH:mm")
	private Date time;
	
	private String name;
	
	private String description;
	
	private String organiser;

	@ManyToOne
	private Venue venue;

	public Event() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Venue getVenue() {
		return this.venue;
	}

	public void setVenue(Venue venue) {
		this.venue = venue;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setOrganiser(String description) {
		this.organiser = description;
	}
	
	public String getOrganiser() {
		return organiser;
	}
}
