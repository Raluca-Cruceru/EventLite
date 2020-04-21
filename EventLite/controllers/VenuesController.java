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
import org.springframework.validation.BindingResult;

import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

@Controller
@RequestMapping(value = "/venues", produces = { MediaType.TEXT_HTML_VALUE })
public class VenuesController {

    @Autowired
	private EventService eventService;

	@Autowired
	private VenueService venueService;
	
	@RequestMapping(method = RequestMethod.GET)
	public String getAllVenues(Model model, @ModelAttribute("SUCCESS_MESSAGE") String success, @ModelAttribute("ERROR_MESSAGE") String error) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String currentPrincipalName = authentication.getAuthorities().toString();
		Iterable<Venue> venues = venueService.findAll();
		Iterator<Venue> iter = venues.iterator();
		
		if(currentPrincipalName.equals("[ROLE_ORGANIZER]")){
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
		model.addAttribute("venues", venues);
		
		if(success.length() != 0) {
			model.addAttribute("success", success);
		}else if(error.length() != 0) {
			model.addAttribute("error", error);
		}
		return "venues/index";
	}
	
	@RequestMapping(value="/search" ,method = RequestMethod.GET)
	public String searchForEvents(@RequestParam String search, Model model)
	{
		Iterable<Venue> venues = venueService.findAll();
		Iterator<Venue> iter = venues.iterator();
		
		while(iter.hasNext()){
			String nextName = iter.next().getName();
			if(!nextName.toLowerCase().contains(search)) {
				iter.remove();
			}
		}
		
		model.addAttribute("venues", venues);
		
		return "venues/index";
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public String getOne(@PathVariable("id") Long id, @ModelAttribute Venue venues, Model model, @ModelAttribute("SUCCESS_MESSAGE") String success, @ModelAttribute("ERROR_MESSAGE") String error) {
		Iterable<Event> events = eventService.findAll();
		Iterator<Event> iter = events.iterator();
		
		while(iter.hasNext()){
			if(id != iter.next().getVenue().getId()) {
				iter.remove();
			}
		}
	
		model.addAttribute("events", events);
		
		if(success.length() != 0) {
			model.addAttribute("success", success);
		}else if(error.length() != 0) {
			model.addAttribute("error", error);
		}
		Venue venue = venueService.findOne(id);
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String currentPrincipalName = authentication.getAuthorities().toString();
		if(currentPrincipalName.equals("[ROLE_ORGANIZER]")){
			String username = authentication.getName();
			if(username.equals(venue.getOrganiser())){
				 model.addAttribute("canedit",true);
			}
		}
		if(currentPrincipalName.equals("[ROLE_ADMINISTRATOR]")){
		   model.addAttribute("canedit",true);
		}
        model.addAttribute("venue",venue);
        return "venues/VenueDetails";
    }

	@RequestMapping(method = RequestMethod.POST, value = "/delete/{id}")
	public String DeleteVenue(@PathVariable("id") long id, RedirectAttributes redirectAttributes) {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String currentPrincipalName = authentication.getAuthorities().toString();
		System.out.println(currentPrincipalName);
		if(currentPrincipalName.equals("[ROLE_ADMINISTRATOR]") || currentPrincipalName.equals("[ROLE_ORGANIZER]")){
			Iterable<Event> events = eventService.findAll();
			Iterator<Event> iter = events.iterator();
			Boolean pass = true;
			while(iter.hasNext()){
				Event next = iter.next();
				if(id == next.getVenue().getId()) {
					pass = false;
				}
			}
			
			if(pass) {
				venueService.delete(id);
				redirectAttributes.addFlashAttribute("SUCCESS_MESSAGE", "Venue deleted successfully!");
				return "redirect:/venues";
			}else {
				redirectAttributes.addFlashAttribute("ERROR_MESSAGE", "Venue has 1 or more events taking place there, to delete a venue first delete all of it's events.");
				return "redirect:/venues/" + id;
			}
		}
		
		return "redirect:/venues";
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/update/name/{id}")
	public String UpdateVenueName(@PathVariable("id") long id, @RequestParam(value="edit", required=true) String edit, RedirectAttributes redirectAttributes) {
		Venue venue = venueService.findOne(id);
		
		if(edit.length() > 0 && edit.length() <= 256) {
			venue.setName(edit);
			venueService.save(venue);
			redirectAttributes.addFlashAttribute("SUCCESS_MESSAGE", "Name updated successfully!");
		}else {
			redirectAttributes.addFlashAttribute("ERROR_MESSAGE", "Name must be non-empty and less than 256 characters.");
		}
        

		return "redirect:/venues/"+id;
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/update/capacity/{id}")
	public String UpdateVenueCapacity(@PathVariable("id") long id, @RequestParam(value="capacity", required=true) int capacity, RedirectAttributes redirectAttributes) {
		Venue venue = venueService.findOne(id);
		
		if(capacity > 0) {
			venue.setCapacity(capacity);
			venueService.save(venue);
			redirectAttributes.addFlashAttribute("SUCCESS_MESSAGE", "Capacity updated successfully!");
		}else {
			redirectAttributes.addFlashAttribute("ERROR_MESSAGE", "Capacity must be a non-negative integer.");
		}
        

		return "redirect:/venues/"+id;
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/update/address/{id}")
	public String UpdateVenueAddress(@PathVariable("id") long id, @RequestParam(value="longitude", required=true) String lng, @RequestParam(value="latitude", required=true) String lat, @RequestParam(value="road", required=true) String road,   @RequestParam(value="postcode", required=true) String postcode, RedirectAttributes redirectAttributes) {
		Venue venue = venueService.findOne(id);
		if(road.length() > 300) {
			redirectAttributes.addFlashAttribute("ERROR_MESSAGE", "Address must have less than 300 characters.");	

		}else if(postcode.length() < 1 || postcode.length() > 8) {
			redirectAttributes.addFlashAttribute("ERROR_MESSAGE", "Valid postcode is required.");	
		}else {
			venue.setRoad(road);
			venue.setPostcode(postcode);
			venue.setLongitude(lng);
			venue.setLatitude(lat);
			venue.setAddress(road + ", " + postcode);
			venueService.save(venue);
			redirectAttributes.addFlashAttribute("SUCCESS_MESSAGE", "Address updated successfully!");	

		}

		return "redirect:/venues/"+id;
	}
	
	@RequestMapping(value = "/addVenue", method = RequestMethod.GET)
	public String ControllerVenue(@Valid @ModelAttribute Venue venues, Model model, @ModelAttribute("SUCCESS_MESSAGE") String success, @ModelAttribute("ERROR_MESSAGE") String error  ) {
	
	  model.addAttribute("venue",new Venue());
	  
	  if(success.length() != 0) {
		  model.addAttribute("success", success);
	  }else if(error.length() != 0) {
		  model.addAttribute("error", error);
	  }
	   
	  return "venues/addVenue";
	  
	}
	
	@RequestMapping( value = "/addVenue", method = RequestMethod.POST)
	public String addNewVenue(@Valid @ModelAttribute Venue venue, Model model, @RequestHeader HttpHeaders headers,  BindingResult bindingResult, RedirectAttributes redirectAttributes){
		if (bindingResult.hasErrors()) {
			System.out.println("Adding venue failed.");
			return "venues";
		}
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();
		
		if(venue.getName().length() == 0 || venue.getName().length() > 256) {
			redirectAttributes.addFlashAttribute("ERROR_MESSAGE", "Name must be non-empty and less than 256 characters.");
			return "redirect:/venues/addVenue";
		}else if(venue.getRoad().length() > 300) {
			redirectAttributes.addFlashAttribute("ERROR_MESSAGE", "Address must have less than 300 characters.");	
			return "redirect:/venues/addVenue";
		}else if(venue.getPostcode().length() < 1 || venue.getPostcode().length() > 8) {
			redirectAttributes.addFlashAttribute("ERROR_MESSAGE", "Valid postcode is required.");	
			return "redirect:/venues/addVenue";
		}else if(venue.getCapacity() <= 0) {
			redirectAttributes.addFlashAttribute("ERROR_MESSAGE", "Capcity must be a non-negative integer.");	
			return "redirect:/venues/addVenue";
		}else {
			venue.setAddress(venue.getRoad() + ", " + venue.getPostcode());
			System.out.println(venue.getLatitude() + ", " + venue.getLongitude());
			venue.setEventcount(0);
			venue.setOrganiser(username);
			venueService.save(venue);
			redirectAttributes.addFlashAttribute("SUCCESS_MESSAGE", "Venue added successfully!");
		}
		return "redirect:/venues";
		
	}

}