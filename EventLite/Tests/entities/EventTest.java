package uk.ac.man.cs.eventlite.entities;


import static org.junit.Assert.*;

import java.util.Calendar;

import org.junit.Before;
import org.junit.Test;

import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;


public class EventTest  {
	Event event = new Event();
	Venue venue = new Venue();
	
	Calendar c = Calendar.getInstance();

	
	@Before
	public void createEvent(){
		c.set(2020,12, 12, 19, 00);
		event.setName("event_name");
		event.setVenue(venue);
		event.setId(1337);
		event.setDate(c.getTime());
		event.setTime(c.getTime());
		event.setDescription("desc");
	}
	
	@Test
	public void testGetters(){
		assertEquals("event_name", event.getName());
		assertEquals(venue, event.getVenue());
		assertEquals(1337, event.getId());
		assertEquals(c.getTime(), event.getDate());
		assertEquals(c.getTime(), event.getTime());
		assertEquals("desc", event.getDescription());
	}
}
