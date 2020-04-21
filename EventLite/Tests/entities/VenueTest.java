package uk.ac.man.cs.eventlite.entities;


import static org.junit.Assert.*;

import java.util.Calendar;

import org.junit.Before;
import org.junit.Test;

import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;


public class VenueTest  {
	Venue venue = new Venue();
	
	Calendar c = Calendar.getInstance();

	
	@Before
	public void createVenue(){
		c.set(2020,12, 12, 19, 00);
		venue.setName("venue_name");
		venue.setId(1337);
		venue.setRoad("road");
		venue.setPostcode("post");
		venue.setAddress("address");
		venue.setCapacity(1000);
		venue.setEventcount(2);
		venue.setLongitude("1111");
		venue.setLatitude("2222");
	}
	
	@Test
	public void testGetters(){
		assertEquals("venue_name", venue.getName());
		assertEquals(1337, venue.getId());
		assertEquals("road", venue.getRoad());
		assertEquals("post", venue.getPostcode());
		assertEquals("address", venue.getAddress());
		assertEquals(1000, venue.getCapacity());
		assertEquals(2, venue.getEventcount());
		assertEquals("1111", venue.getLongitude());
		assertEquals("2222", venue.getLatitude());
	}
}
