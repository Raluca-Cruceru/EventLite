package uk.ac.man.cs.eventlite.dao;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;

import uk.ac.man.cs.eventlite.EventLite;

import org.junit.Test;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EventLite.class)
@DirtiesContext
@ActiveProfiles("test")

public class EventServiceTest extends AbstractTransactionalJUnit4SpringContextTests {

	@Autowired
	private EventService eventService;
	
	@Autowired
	private VenueService venueService;

	@Test
	public void testSave() {
		Event newEvent = new Event();
		Date now = Calendar.getInstance().getTime();
		Venue venue = new Venue();
		venue.setCapacity(1000);
		venue.setName("Example");
		assertNotNull(venueService.save(venue));
		newEvent.setDate(now);
		newEvent.setTime(now);
		newEvent.setName("Test");
		newEvent.setVenue(venue);
		long expectedCount = eventService.count() + 1;
		assertNotNull(eventService.save(newEvent));
		assertEquals(expectedCount, eventService.count());
	}

}
