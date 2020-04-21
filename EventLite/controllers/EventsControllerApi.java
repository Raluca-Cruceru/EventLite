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
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

@RestController
@RequestMapping(value = "/api/events", produces = { MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE })
public class EventsControllerApi {

	@Autowired
	private EventService eventService;

	@RequestMapping(method = RequestMethod.GET)
	public Resources<Resource<Event>> getAllEvents() {

		return eventToResource(eventService.findAll());
	}
	
	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> createEvents(@RequestBody @Valid Event events, BindingResult result)
	{
		if (result.hasErrors()) {
			return ResponseEntity.unprocessableEntity().build();		
			}
		
		eventService.save(events);
		URI location = linkTo(EventsControllerApi.class).slash(events.getName()).toUri();
		return ResponseEntity.created(location).build();
	}
	
	

	private Resource<Event> eventToResource(Event event) {
		Link selfLink = linkTo(EventsControllerApi.class).slash(event.getId()).withSelfRel();
		Link venueLink = linkTo(EventsControllerApi.class).slash(event.getId()).slash("venue").withRel("venue");

		return new Resource<Event>(event, selfLink, venueLink);
	}

	private Resources<Resource<Event>> eventToResource(Iterable<Event> events) {
		Link selfLink = linkTo(methodOn(EventsControllerApi.class).getAllEvents()).withSelfRel();

		List<Resource<Event>> resources = new ArrayList<Resource<Event>>();
		for (Event event : events) {
			resources.add(eventToResource(event));
		}
		
		Link profileLink = linkTo(HomeControllerApi.class).slash("profile").slash("events").withRel("profile");
		
		return new Resources<Resource<Event>>(resources, selfLink, profileLink);
	}
	
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public Resources<Resource<Event>> getVenue(@PathVariable("id") long id) {
		Event event = (Event) eventService.findOne(id);
		
		List<Resource<Event>> resources = new ArrayList<Resource<Event>>();
		resources.add(eventToResource(event));
		
		Link selfLink = linkTo(methodOn(EventsControllerApi.class).getAllEvents()).slash(id).withSelfRel();
		Link eventLink = linkTo(methodOn(EventsControllerApi.class).getAllEvents()).slash(id).slash("events").withRel("event");
		Link venueLink = linkTo(methodOn(EventsControllerApi.class).getAllEvents()).slash(id).withRel("venues");
		
		
		return new Resources<Resource<Event>>(resources, selfLink, venueLink, eventLink);
	}
	
	
	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public Resources<Resource<Event>> searchAllEvents(@RequestParam String search) {
		return eventToResource(eventService.search(search));
	}
}
