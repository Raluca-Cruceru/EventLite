package uk.ac.man.cs.eventlite.controllers;

import java.text.ParseException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ModelAttribute;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import antlr.collections.List;

import org.springframework.validation.BindingResult;

import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

import org.springframework.social.MissingAuthorizationException;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.Twitter;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;



import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;



@Controller
@RequestMapping(value = "/events", produces = { MediaType.TEXT_HTML_VALUE })
public class EventsController {

    @Autowired
	private EventService eventService;

	@Autowired
	private VenueService venueService;
	
	@Autowired
	private ConnectionRepository connectionRepository;
	
	@Autowired
	private Twitter twitter;
	
	
	@Inject
	public EventsController(Twitter twitter, ConnectionRepository connectionRepository) {
		this.twitter = twitter;
		this.connectionRepository = connectionRepository;
	}
	

	

	@RequestMapping(method = RequestMethod.GET)
	public String getAllEvents(Model model, @ModelAttribute("SUCCESS_MESSAGE") String success, @ModelAttribute("ERROR_MESSAGE") String error) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String currentPrincipalName = authentication.getAuthorities().toString();
		Iterable<Event> futureEvents = eventService.findAll();
		Iterator<Event> futureIter = futureEvents.iterator();
		
		Iterable<Event> pastEvents = eventService.findAll();
		Iterator<Event> pastIter = pastEvents.iterator();
		
		if(!currentPrincipalName.equals("[ROLE_ORGANIZER]")){
			
			
			while(futureIter.hasNext()){
				Date date = futureIter.next().getDate();
				if(!(new Date().before(date))) {
					futureIter.remove();
				}
			}
			model.addAttribute("eventsFuture", futureEvents);
			
			
			while(pastIter.hasNext()){
				Date date = pastIter.next().getDate();
				if(new Date().before(date)) {
					pastIter.remove();
				}
			}
			model.addAttribute("eventsPast", pastEvents);
			if(success.length() != 0) {
				model.addAttribute("success", success);
			}else if(error.length() != 0) {
				model.addAttribute("error", error);
			}
		}else{
			futureIter = futureEvents.iterator();
			while(futureIter.hasNext()){
				Date date = futureIter.next().getDate();
				if(!(new Date().before(date))) {
					futureIter.remove();
				}
			}
			futureIter = futureEvents.iterator();
			while(futureIter.hasNext()){
				String nextName = futureIter.next().getOrganiser();
				if(nextName != null){
					if(!nextName.contains(authentication.getName())) {
						futureIter.remove();
					}
				}else{
					futureIter.remove();
				}
			}
			model.addAttribute("eventsFuture", futureEvents);
			pastIter = pastEvents.iterator();
			while(pastIter.hasNext()){
				Date date2 = pastIter.next().getDate();
				if(new Date().before(date2)) {
					pastIter.remove();
				}
			}
			pastIter = pastEvents.iterator();
			while(pastIter.hasNext()){
				String nextName = pastIter.next().getOrganiser();
				if(nextName != null){
					if(!nextName.contains(authentication.getName())) {
						pastIter.remove();
					}
				}else{
					pastIter.remove();
				}
			}
			model.addAttribute("eventsPast", pastEvents);
			if(success.length() != 0) {
				model.addAttribute("success", success);
			}else if(error.length() != 0) {
				model.addAttribute("error", error);
			}
			/*Iterable<Event> events = eventService.findAll();
			Iterator<Event> iter = events.iterator();
			
			while(iter.hasNext()){
				String nextName = iter.next().getOrganiser();
				if(nextName != null){
					if(!nextName.contains(authentication.getName())) {
						iter.remove();
					}
				}else{
					iter.remove();
				}
			}
			model.addAttribute("events", events);
			if(success.length() != 0) {
				model.addAttribute("success", success);
			}else if(error.length() != 0) {
				model.addAttribute("error", error);
			}*/
		}
		
		/*if (connectionRepository.findPrimaryConnection(Twitter.class) == null) {
            return "redirect:/connect/twitter";
        }*/
		
		System.out.print(connectionRepository.findPrimaryConnection(Twitter.class));
		
		if (connectionRepository.findPrimaryConnection(Twitter.class) != null) 
			model.addAttribute("tweets", twitter.timelineOperations().getUserTimeline(5));
		
		

		return "events/index";
	}
	
	private Iterable<Event> filterTheEventsByName(Iterable<Event> eventListGiven, String filter)
	{
		ArrayList<Event> listOfFilteredEvents = new ArrayList<>();
		
		for(Event currentEvent : eventListGiven)
		{
			if(currentEvent.getName().toLowerCase().contains(filter.toLowerCase())){
				listOfFilteredEvents.add(currentEvent);
			}
		}
		
		return (Iterable<Event>) listOfFilteredEvents;
	}
	
	@RequestMapping(value="/search" ,method = RequestMethod.GET)
	public String searchForEvents(@RequestParam String search, Model model)
	{
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String currentPrincipalName = authentication.getAuthorities().toString();
		Iterable<Event> eventList;
		eventList = eventService.findAll();
		if(currentPrincipalName.equals("[ROLE_ORGANIZER]")){
			Iterator<Event> iter = eventList.iterator();
			
			while(iter.hasNext()){
				String nextName = iter.next().getOrganiser();
				if(nextName != null){
					if(!nextName.contains(authentication.getName())) {
						iter.remove();
					}
				}else{
					iter.remove();
				}
			}
		}
		search = search.toLowerCase();
		eventList = filterTheEventsByName(eventList, search);
		model.addAttribute("eventsFuture", eventList);
		model.addAttribute("isSearch", true);
		return "events/index";
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public String getOne(@PathVariable("id") Long id, @ModelAttribute Venue venues, Model model, @ModelAttribute("SUCCESS_MESSAGE") String success, @ModelAttribute("ERROR_MESSAGE") String error, RedirectAttributes redirectAttributes) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();
		String currentPrincipalName = authentication.getAuthorities().toString();

		Event event = eventService.findOne(id);
		System.out.println(event.getOrganiser().toLowerCase());
	        model.addAttribute("myEvent", event);
	        if(success.length() != 0) {
				model.addAttribute("success", success);
			}else if(error.length() != 0) {
				model.addAttribute("error", error);
			}
	        model.addAttribute("venues",venueService.findAll());
	        if (connectionRepository.findPrimaryConnection(Twitter.class) != null) {
			model.addAttribute("has_twitter", true);
		} else {
			model.addAttribute("has_twitter", false);
		}
			if(username.toLowerCase().equals(event.getOrganiser().toLowerCase()) || currentPrincipalName.equals("[ROLE_ADMINISTRATOR]")){
				model.addAttribute("canedit", true);
			}
	        return "events/EventDetails";

    }

	@RequestMapping(method = RequestMethod.POST, value = "/delete/{id}")
	public String DeleteEvent(@PathVariable("id") long id, RedirectAttributes redirectAttributes) {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String currentPrincipalName = authentication.getAuthorities().toString();
		System.out.println(currentPrincipalName);
		if(currentPrincipalName.equals("[ROLE_ADMINISTRATOR]") || currentPrincipalName.equals("[ROLE_ORGANIZER]")){
			Event event = eventService.findOne(id);
			Venue venue = event.getVenue();
			venue.setEventcount(venue.getEventcount() - 1);
			eventService.delete(id);
			redirectAttributes.addFlashAttribute("SUCCESS_MESSAGE", "Event deleted successfully!");
		}
		
		return "redirect:/events";
	}
	
	
	@RequestMapping(method = RequestMethod.POST, value = "/update/name/{id}")
	public String UpdateEventName(@PathVariable("id") long id, @RequestParam(value="edit", required=true) String edit, RedirectAttributes redirectAttributes) {
		Event event = eventService.findOne(id);
		
		if(edit.length() > 0 && edit.length() <= 256) {
			event.setName(edit);
			eventService.save(event);
			redirectAttributes.addFlashAttribute("SUCCESS_MESSAGE", "Name updated successfully!");
		}else {
			redirectAttributes.addFlashAttribute("ERROR_MESSAGE", "Name must be non-empty and less than 256 characters.");
		}
        

		return "redirect:/events/"+id;
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/update/description/{id}")
	public String UpdateEventDesc(@PathVariable("id") long id, @RequestParam(value="edit", required=true) String edit, RedirectAttributes redirectAttributes) {
		Event event = eventService.findOne(id);
		
		if(edit.length() <= 500) {
			event.setDescription(edit);
			eventService.save(event);
			redirectAttributes.addFlashAttribute("SUCCESS_MESSAGE", "Description updated successfully!");
		}else{
			redirectAttributes.addFlashAttribute("ERROR_MESSAGE", "Description must be less than 500 characters.");

		}
		
		return "redirect:/events/"+id;
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/update/time/{id}")
	public String UpdateEventTime(@PathVariable("id") long id, @RequestParam(value="time", required=true) String edit, RedirectAttributes redirectAttributes) {
		Event event = eventService.findOne(id);
		
		if(edit.length() == 0) {
			event.setTime(null);
			eventService.save(event);
		}else {
		    Date date;
			try {
				date = new SimpleDateFormat("kk:mm").parse(edit);
				event.setTime(date);
				eventService.save(event);
			} catch (ParseException e) {
				e.printStackTrace();
			}  
		}
		redirectAttributes.addFlashAttribute("SUCCESS_MESSAGE", "Time updated successfully!");
		return "redirect:/events/"+id;
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/update/date/{id}")
	public String UpdateEventDate(@PathVariable("id") long id, @RequestParam(value="date", required=true) String edit, RedirectAttributes redirectAttributes) {
		Event event = eventService.findOne(id);
	    Date date;
		try {
			date = new SimpleDateFormat("yyyy-MM-dd").parse(edit);
			
			if(!(new Date().before(date))) {
				redirectAttributes.addFlashAttribute("ERROR_MESSAGE", "Date must be in the future.");
			}else{
				event.setDate(date);
				eventService.save(event);
				redirectAttributes.addFlashAttribute("SUCCESS_MESSAGE", "Date updated successfully!");
			}
		} catch (ParseException e) {
			
			e.printStackTrace();
		}  

		return "redirect:/events/"+id;
	}
	
	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, value = "/update/venue/{id}")
	public String UpdateEventVenue(@PathVariable("id") long id, @RequestParam(value="venue") long edit, RedirectAttributes redirectAttributes) {
		Event event = eventService.findOne(id);	
		Venue venue = venueService.findOne(edit);	
		event.setVenue(venue);
		eventService.save(event);
		
		redirectAttributes.addFlashAttribute("SUCCESS_MESSAGE", "Venue updated successfully!");

		return "redirect:/events/"+id;
	}
	
	
	@RequestMapping(value = "/addEvent", method = RequestMethod.GET)
	public String Controller(@Valid @ModelAttribute Venue venues, Model model, @ModelAttribute("SUCCESS_MESSAGE") String success, @ModelAttribute("ERROR_MESSAGE") String error  ) {
	
	  model.addAttribute("venues",venueService.findAll());
      model.addAttribute("event", new Event());
      
      if(success.length() != 0) {
		  model.addAttribute("success", success);
	  }else if(error.length() != 0) {
		  model.addAttribute("error", error);
	  }
      
	  return "events/addEvent";
	  
	}
	
	@RequestMapping( value = "/addEvent", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public String addNewEvent(@Valid @ModelAttribute Event event, Model model,  BindingResult bindingResult, RedirectAttributes redirectAttributes){
		if (bindingResult.hasErrors()) {
			redirectAttributes.addFlashAttribute("ERROR_MESSAGE", "Adding event failed.");	

			return "events";
		}
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();
		
		if(event.getName().length() == 0 || event.getName().length() > 256) {
			redirectAttributes.addFlashAttribute("ERROR_MESSAGE", "Name must be non-empty and less than 256 characters.");
			return "redirect:/events/addEvent";
		}else if(!(new Date().before(event.getDate()))) {
			redirectAttributes.addFlashAttribute("ERROR_MESSAGE", "Date must be in the future.");
			return "redirect:/events/addEvent";
		}else if(event.getDescription().length() > 500) {
			redirectAttributes.addFlashAttribute("ERROR_MESSAGE", "Description must be less than 500 characters.");	
			return "redirect:/events/addEvent";
		}else {
			Venue venue = event.getVenue();
			venue.setEventcount(venue.getEventcount() + 1);
			event.setOrganiser(username);
			eventService.save(event);
			redirectAttributes.addFlashAttribute("SUCCESS_MESSAGE", "Event added successfully!");
		}
		return "redirect:/events";
		
	}

	@RequestMapping(method = RequestMethod.POST)
	public String postTweet(Model model, @ModelAttribute Event event, @RequestParam(name = "tweet_content") String tweetContent,
                            @RequestParam(name = "event_id") long eventId, RedirectAttributes redirectAttributes) {

	    // if the user is logged in
        if (connectionRepository.findPrimaryConnection(Twitter.class) != null) {
            twitter.timelineOperations().updateStatus(tweetContent);
            redirectAttributes.addFlashAttribute("success_message", "Tweet \"" + tweetContent + "\" sent successfully!");
        }


        return "redirect:events/" + eventId;
	}

	
}