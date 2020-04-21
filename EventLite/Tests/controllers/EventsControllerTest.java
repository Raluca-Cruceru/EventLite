package uk.ac.man.cs.eventlite.controllers;

import static org.mockito.Mockito.verify;

import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import java.text.SimpleDateFormat;
import java.util.Collections;

import javax.servlet.Filter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import uk.ac.man.cs.eventlite.EventLite;
import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;
import uk.ac.man.cs.eventlite.config.Security;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EventLite.class)
@AutoConfigureMockMvc
@DirtiesContext
@ActiveProfiles("test")
public class EventsControllerTest {

    private MockMvc mvc;

    @Autowired
    private Filter springSecurityFilterChain;

    @Mock
    private Event event;

    @Mock
    private Venue venue;

    @Mock
    private EventService eventService;

    @Mock
    private VenueService venueService;

    @InjectMocks
    private EventsController eventsController;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.standaloneSetup(eventsController).apply(springSecurity(springSecurityFilterChain))
                .build();
    }

   /* @Test
    public void getIndexWhenNoEvents() throws Exception {
        when(eventService.findAll()).thenReturn(Collections.<Event> emptyList());
        when(venueService.findAll()).thenReturn(Collections.<Venue> emptyList());

        mvc.perform(get("/events")
        .accept(MediaType.TEXT_HTML))
        .andExpect(status().isOk())
        .andExpect(view().name("events/index")).andExpect(handler().methodName("getAllEvents"));

        verify(eventService).findAll();
        verifyZeroInteractions(event);
        verifyZeroInteractions(venue);
    }

    @Test
    public void getIndexWithEvents() throws Exception {
        when(eventService.findAll()).thenReturn(Collections.<Event> singletonList(event));
        when(venueService.findAll()).thenReturn(Collections.<Venue> singletonList(venue));

        mvc.perform(get("/events").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
                .andExpect(view().name("events/index")).andExpect(handler().methodName("getAllEvents"));

        verify(eventService).findAll();
        verifyZeroInteractions(event);
        verifyZeroInteractions(venue);
    }*/
    
    @Test
    public void deleteEvent() throws Exception {
    	 when(eventService.findOne(2)).thenReturn(event);
    	 when(event.getVenue()).thenReturn(venue);
        mvc.perform(post("/events/delete/2").with(user("b").roles(Security.ADMIN_ROLE))
                .accept(MediaType.TEXT_HTML).with(csrf()))
        .andExpect(status().isFound()).andExpect(view().name("redirect:/events"))
        .andExpect(model().hasNoErrors())
        .andExpect(handler().methodName("DeleteEvent"));

        verify(eventService).delete(2);
    }
    
    
    
    @Test
    public void addNewEvent() throws Exception {
        Venue v = new Venue();
        venueService.save(v);
        ArgumentCaptor<Event> captor = ArgumentCaptor.forClass(Event.class);
        mvc.perform(MockMvcRequestBuilders.post("/events/addEvent")
                .with(user("b").roles(Security.ADMIN_ROLE)).with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("name", "New Event")
                .param("date", "2018-07-07")
                .param("time", "12:00")
                .param("venue.id", String.valueOf(v.getId()))
                .param("description", "descr")
                .accept(MediaType.TEXT_HTML)).andExpect(status().isFound())
                .andExpect(model().hasNoErrors())
                .andExpect(view().name("redirect:/events"))
                .andExpect(handler().methodName("addNewEvent"));
        verify(eventService).save(captor.capture());
        assertThat("New Event", equalTo(captor.getValue().getName()));
    }

    @Test
    public void searchEventWithEvents() throws Exception {
    	Event newEvent = new Event();
    	newEvent.setOrganiser("Matt");
    	newEvent.setName("Queen");
        when(eventService.findAll()).thenReturn(Collections.<Event>singletonList(newEvent));

        mvc.perform(get("/events/search").param("search", "Queen").accept(MediaType.TEXT_HTML))
        		.andExpect(status().isOk())
        		.andExpect(model().hasNoErrors())
                .andExpect(view().name("events/index"))
                .andExpect(handler().methodName("searchForEvents"));

        verifyZeroInteractions(venueService);
        verifyZeroInteractions(event);
        verifyZeroInteractions(venue);
    }
    
    /*@Test
    public void getDetailedEvents() throws Exception {
    	Event newEvent = new Event();
    	newEvent.setOrganiser("Matt");
    	newEvent.setName("Queen");
        when(eventService.findOne(1)).thenReturn(newEvent);

        mvc.perform(get("/events/1").accept(MediaType.TEXT_HTML))
        		.andExpect(status().isOk())
        		.andExpect(model().hasNoErrors())
                .andExpect(view().name("events/EventDetails"))
                .andExpect(handler().methodName("getOne"));

        verifyZeroInteractions(venueService);
        verifyZeroInteractions(event);
        verifyZeroInteractions(venue);
    }*/

    
    @Test
	public void updateEventName() throws Exception {
		Venue v1 = new Venue();
		Venue v2 = new Venue();
		venueService.save(v1);
		venueService.save(v2);
		Event e = new Event();
		e.setName("Test event");
		e.setVenue(v1);		
		e.setId(2);
		when(eventService.findOne(2)).thenReturn(e);
		mvc.perform(post("/events/update/name/" + e.getId())
				.with(user("b").roles(Security.ADMIN_ROLE))
				.with(csrf())
				.contentType(MediaType.TEXT_HTML)
				.param("edit", "New name")
				.accept(MediaType.TEXT_HTML))
			.andExpect(status().isFound())
            .andExpect(model().hasNoErrors())
            .andExpect(view().name("redirect:/events/" + e.getId()))
            .andExpect(handler().methodName("UpdateEventName"));

		ArgumentCaptor<Event> captor = ArgumentCaptor.forClass(Event.class);
        verify(eventService).save(captor.capture());

        assertThat(e.getId(), equalTo(captor.getValue().getId()));
        assertThat("New name", equalTo(captor.getValue().getName()));
        
	}

    @Test
	public void updateEventDescription() throws Exception {
		Venue v1 = new Venue();
		Venue v2 = new Venue();
		venueService.save(v1);
		venueService.save(v2);
		Event e = new Event();
		e.setName("Test event");
		e.setVenue(v1);		
		e.setId(2);
		when(eventService.findOne(2)).thenReturn(e);
		mvc.perform(post("/events/update/description/" + e.getId())
				.with(user("b").roles(Security.ADMIN_ROLE))
				.with(csrf())
				.contentType(MediaType.TEXT_HTML)
				.param("edit", "New desc")
				.accept(MediaType.TEXT_HTML))
			.andExpect(status().isFound())
            .andExpect(model().hasNoErrors())
            .andExpect(view().name("redirect:/events/" + e.getId()))
            .andExpect(handler().methodName("UpdateEventDesc"));

		ArgumentCaptor<Event> captor = ArgumentCaptor.forClass(Event.class);
        verify(eventService).save(captor.capture());

        assertThat(e.getId(), equalTo(captor.getValue().getId()));
        assertThat("New desc", equalTo(captor.getValue().getDescription()));
        
	}
    
    @Test
	public void updateEventVenue() throws Exception {
		Venue v1 = new Venue();
		Venue v2 = new Venue();
		v1.setId(1);
		v2.setId(2);
		venueService.save(v1);
		venueService.save(v2);
		Event e = new Event();
		e.setName("Test event");
		e.setVenue(v1);		
		e.setId(2);
		String id = Long.toString(v2.getId());
		when(eventService.findOne(2)).thenReturn(e);
		when(venueService.findOne(2)).thenReturn(v2);
		mvc.perform(post("/events/update/venue/" + v2.getId())
				.with(user("b").roles(Security.ADMIN_ROLE))
				.with(csrf())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("venue", id)
				.accept(MediaType.TEXT_HTML))
			.andExpect(status().isFound())
            .andExpect(model().hasNoErrors())
            .andExpect(view().name("redirect:/events/" + e.getId()))
            .andExpect(handler().methodName("UpdateEventVenue"));

		ArgumentCaptor<Event> captor = ArgumentCaptor.forClass(Event.class);
        verify(eventService).save(captor.capture());

        assertThat(e.getId(), equalTo(captor.getValue().getId()));
        assertThat(v2.getId(), equalTo(captor.getValue().getVenue().getId()));
        
	}
	
    @Test
	public void updateEventDate() throws Exception {
		Venue v1 = new Venue();
		Venue v2 = new Venue();
		venueService.save(v1);
		venueService.save(v2);
		Event e = new Event();
		e.setName("Test event");
		e.setVenue(v1);		
		e.setId(2);
		when(eventService.findOne(2)).thenReturn(e);
		mvc.perform(post("/events/update/date/" + e.getId())
				.with(user("b").roles(Security.ADMIN_ROLE))
				.with(csrf())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("date", "2020-09-09")
				.accept(MediaType.TEXT_HTML))
			.andExpect(status().isFound())
            .andExpect(model().hasNoErrors())
            .andExpect(view().name("redirect:/events/" + e.getId()))
            .andExpect(handler().methodName("UpdateEventDate"));

		ArgumentCaptor<Event> captor = ArgumentCaptor.forClass(Event.class);
        verify(eventService).save(captor.capture());
        
        assertThat(e.getId(), equalTo(captor.getValue().getId()));
        assertThat("Wed Sep 09 00:00:00 BST 2020", equalTo(captor.getValue().getDate().toString()));
        
	}
	
    @Test
	public void updateEventTime() throws Exception {
		Venue v1 = new Venue();
		Venue v2 = new Venue();
		venueService.save(v1);
		venueService.save(v2);
		Event e = new Event();
		e.setName("Test event");
		e.setVenue(v1);		
		e.setId(2);
		when(eventService.findOne(2)).thenReturn(e);
		mvc.perform(post("/events/update/time/" + e.getId())
				.with(user("b").roles(Security.ADMIN_ROLE))
				.with(csrf())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("time", "17:00")
				.accept(MediaType.TEXT_HTML))
			.andExpect(status().isFound())
            .andExpect(model().hasNoErrors())
            .andExpect(view().name("redirect:/events/" + e.getId()))
            .andExpect(handler().methodName("UpdateEventTime"));

		ArgumentCaptor<Event> captor = ArgumentCaptor.forClass(Event.class);
        verify(eventService).save(captor.capture());
        
        assertThat(e.getId(), equalTo(captor.getValue().getId()));
        assertThat("Thu Jan 01 17:00:00 GMT 1970", equalTo(captor.getValue().getTime().toString()));
        
	}

}