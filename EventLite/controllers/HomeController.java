package uk.ac.man.cs.eventlite.controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ModelAttribute;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
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
@RequestMapping(value = "/", produces = { MediaType.TEXT_HTML_VALUE })
public class HomeController {
	
	@Autowired
	private EventService eventService;

	@Autowired
	private VenueService venueService;
	
	@RequestMapping(method = RequestMethod.GET)
	public String getAllVenues(Model model, @ModelAttribute("SUCCESS_MESSAGE") String success, @ModelAttribute("ERROR_MESSAGE") String error) {	
		Iterable<Event> events = eventService.findAll();
		Iterator<Event> iter = events.iterator();
		int i = 0;
		while(iter.hasNext()){
			i++;
			Event nextevent = iter.next();
			
			if(!(new Date().before(nextevent.getDate()))){
				iter.remove();
				i--;
			}
			if(i > 3) {
				iter.remove();
			}
		}
		model.addAttribute("events", events);
		
		Iterable<Venue> venues = venueService.findAllByCount();
		Iterator<Venue> it = venues.iterator();
		int j = 0;
		while(it.hasNext()){
			it.next();
			if(j > 2){
				it.remove();
			}
			j++;
		}
		model.addAttribute("venues", venues);
		
		
		if(success.length() != 0) {
			model.addAttribute("success", success);
		}else if(error.length() != 0) {
			model.addAttribute("error", error);
		}
		return "home/index";
	}


}