package uk.ac.man.cs.eventlite.controllers;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

@RestController
@RequestMapping(value = "/api/venues", produces = { MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE })
public class VenuesControllerApi {

	private static final RequestMethod[] GET = null;
	@Autowired
	private VenueService venueService;
	@Autowired
	private EventService eventService;

	@RequestMapping(method = RequestMethod.GET)
	public Resources<Resource<Venue>> getAllVenues() {

		return venueToResource(venueService.findAll());
	}
	
	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public ResponseEntity<?> newVenue(){
		return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
	}
	
	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> createVenues(@RequestBody @Valid Venue venues, BindingResult result)
	{
		if (result.hasErrors()) {
			return ResponseEntity.unprocessableEntity().build();		
			}
		
		venueService.save(venues);
		URI location = linkTo(VenuesControllerApi.class).slash(venues.getName()).toUri();
		return ResponseEntity.created(location).build();
	}
	
	
	

	private Resource<Venue> venueToResource(Venue venue) {
		Link selfLink = linkTo(VenuesControllerApi.class).slash(venue.getId()).withSelfRel();

		return new Resource<Venue>(venue, selfLink);
	}

	private Resources<Resource<Venue>> venueToResource(Iterable<Venue> venues) {
		Link selfLink = linkTo(methodOn(VenuesControllerApi.class).getAllVenues()).withSelfRel();

		List<Resource<Venue>> resources = new ArrayList<Resource<Venue>>();
		for (Venue venue : venues) {
			resources.add(venueToResource(venue));
		}
		
		Link profileLink = linkTo(HomeControllerApi.class).slash("profile").slash("venues").withRel("profile");

		return new Resources<Resource<Venue>>(resources, selfLink, profileLink);
	}
	
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public Resources<Resource<Venue>> getEvent(@PathVariable("id") long id) {
		Venue venue = venueService.findOne(id);
		
		List<Resource<Venue>> resources = new ArrayList<Resource<Venue>>();
		resources.add(venueToResource(venue));
		
		Link selfLink = linkTo(methodOn(VenuesControllerApi.class).getAllVenues()).slash(id).withSelfRel();
		Link eventLink = linkTo(methodOn(VenuesControllerApi.class).getAllVenues()).slash(id).slash("events").withRel("event");
		Link venueLink = linkTo(methodOn(VenuesControllerApi.class).getAllVenues()).slash(id).withRel("venues");
		
		Link next3EventLink = linkTo(VenuesControllerApi.class).slash(venue.getId()).slash("next3events").withRel("next3events");
		
		
		return new Resources<Resource<Venue>>(resources, selfLink, venueLink, eventLink, next3EventLink);
	}
}
