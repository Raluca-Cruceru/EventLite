package uk.ac.man.cs.eventlite.controllers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import javax.inject.Inject;
import javax.servlet.Filter;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
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
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.twitter.api.Twitter;
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
public class VenuesControllerTest {

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
    private VenuesController venuesController;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.standaloneSetup(venuesController).apply(springSecurity(springSecurityFilterChain))
                .build();
    }


    @Test
    public void addVenue() throws Exception {

        mvc.perform(post("/venues/addVenue").with(user("b").roles(Security.ADMIN_ROLE))
                .accept(MediaType.TEXT_HTML)
        		.param("name", "test").param("road", "43 Landcross Road").param("postcode", "M14 6LZ").param("capacity", "2000").with(csrf()))
        .andExpect(status().isFound()).andExpect(view().name("redirect:/venues"))
        .andExpect(model().hasNoErrors())
        .andExpect(handler().methodName("addNewVenue"));

    }
    
    @Test
    public void deleteVenue() throws Exception {
    	Venue v1 = new Venue();
    	
    	Venue v2 = new Venue();
    	Event event1 = new Event();
    	event1.setVenue(v2);
		v1.setId(1);
    	 when(venueService.findOne(1)).thenReturn(v1);
    	 when(eventService.findAll()).thenReturn(Collections.<Event>singletonList(event1));
        mvc.perform(post("/venues/delete/1").with(user("b").roles(Security.ADMIN_ROLE))
                .accept(MediaType.TEXT_HTML).with(csrf()))
        .andExpect(status().isFound()).andExpect(view().name("redirect:/venues"))
        .andExpect(model().hasNoErrors())
        .andExpect(handler().methodName("DeleteVenue"));

        verify(venueService).delete(1);
    }
    
    @Test
    public void searchVenues() throws Exception {
    	Venue newVenue = new Venue();
    	newVenue.setName("Arena");
        when(venueService.findAll()).thenReturn(Collections.<Venue>singletonList(newVenue));

        mvc.perform(get("/venues/search").param("search", "arena").accept(MediaType.TEXT_HTML))
        		.andExpect(status().isOk())
        		.andExpect(model().hasNoErrors())
                .andExpect(view().name("venues/index"))
                .andExpect(handler().methodName("searchForEvents"));

        verifyZeroInteractions(eventService);
        verifyZeroInteractions(event);
        verifyZeroInteractions(venue);
    }
    
    @Test
	public void updateVenueName() throws Exception {
		Venue v1 = new Venue();
		v1.setId(1);

		when(venueService.findOne(1)).thenReturn(v1);
		mvc.perform(post("/venues/update/name/1")
				.with(user("b").roles(Security.ADMIN_ROLE))
				.with(csrf())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("edit", "new name")
				.accept(MediaType.TEXT_HTML))
			.andExpect(status().isFound())
            .andExpect(model().hasNoErrors())
            .andExpect(view().name("redirect:/venues/1"))
            .andExpect(handler().methodName("UpdateVenueName"));

		ArgumentCaptor<Venue> captor = ArgumentCaptor.forClass(Venue.class);
        verify(venueService).save(captor.capture());

        assertThat("new name", equalTo(captor.getValue().getName()));
        
	}
	
    @Test
	public void updateVenueCapacity() throws Exception {
		Venue v1 = new Venue();
		v1.setId(1);

		when(venueService.findOne(1)).thenReturn(v1);
		mvc.perform(post("/venues/update/capacity/1")
				.with(user("b").roles(Security.ADMIN_ROLE))
				.with(csrf())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("capacity", "100")
				.accept(MediaType.TEXT_HTML))
			.andExpect(status().isFound())
            .andExpect(model().hasNoErrors())
            .andExpect(view().name("redirect:/venues/1"))
            .andExpect(handler().methodName("UpdateVenueCapacity"));

		ArgumentCaptor<Venue> captor = ArgumentCaptor.forClass(Venue.class);
        verify(venueService).save(captor.capture());

        assertThat(100, equalTo(captor.getValue().getCapacity()));
        
	}
    
    @Test
	public void updateVenueAddress() throws Exception {
		Venue v1 = new Venue();
		v1.setId(1);

		when(venueService.findOne(1)).thenReturn(v1);
		mvc.perform(post("/venues/update/address/1")
				.with(user("b").roles(Security.ADMIN_ROLE))
				.with(csrf())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("road", "road")
				.param("postcode", "post")
				.param("longitude", "200")
				.param("latitude", "100")
				.accept(MediaType.TEXT_HTML))
			.andExpect(status().isFound())
            .andExpect(model().hasNoErrors())
            .andExpect(view().name("redirect:/venues/1"))
            .andExpect(handler().methodName("UpdateVenueAddress"));

		ArgumentCaptor<Venue> captor = ArgumentCaptor.forClass(Venue.class);
        verify(venueService).save(captor.capture());

        assertThat("road", equalTo(captor.getValue().getRoad()));
        assertThat("post", equalTo(captor.getValue().getPostcode()));
        assertThat("road, post", equalTo(captor.getValue().getAddress()));
        assertThat("200", equalTo(captor.getValue().getLongitude()));
        assertThat("100", equalTo(captor.getValue().getLatitude()));
	}

}