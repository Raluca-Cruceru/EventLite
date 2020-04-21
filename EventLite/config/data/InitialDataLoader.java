package uk.ac.man.cs.eventlite.config.data;
 
 
import java.util.Calendar;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
 
import java.text.SimpleDateFormat;
import java.util.Date;
 
 
 
import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Venue;
 
import uk.ac.man.cs.eventlite.entities.Event;
 
@Component
@Profile({ "default", "test" })
public class InitialDataLoader implements ApplicationListener<ContextRefreshedEvent> {
 
    private final static Logger log = LoggerFactory.getLogger(InitialDataLoader.class);
 
    @Autowired
    private EventService eventService;
 
    @Autowired
    private VenueService venueService;
   
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
 
        Venue[] venues = new Venue[5];
        Event[] events = new Event[7];
 
 
        if (eventService.count() > 0) {
            log.info("Events table already populated. Skipping data initialization.");
            return;
        }else{
           
                for(int i = 0; i < venues.length; i++){
                    venues[i] = new Venue();
                }
               
                venues[0].setCapacity(5000);
                venues[0].setName("Manchester Academy");
                venues[0].setRoad("Moss Lane East");
                venues[0].setPostcode("M14 4PX");
                venues[0].setAddress("Moss Lane East, M14 4PX");
                venues[0].setLatitude("53.4636888");
                venues[0].setLongitude("-2.2325218");
                venues[0].setEventcount(1);
                venues[0].setOrganiser("Raluca");
                venueService.save(venues[0]);
               
                venues[1].setCapacity(25000);
                venues[1].setName("Manchester Arena");
                venues[1].setRoad("Victoria Station");
                venues[1].setPostcode("M3 1AR");
                venues[1].setAddress("Victoria Station, M3 1AR");
                venues[1].setLatitude("53.4881716");
                venues[1].setLongitude("-2.2463226");
                venues[1].setEventcount(2);
                venues[1].setOrganiser("Sam");
                venueService.save(venues[1]);
               
                venues[2].setCapacity(50000);
                venues[2].setName("Old Trafford Cricket Ground");
                venues[2].setRoad("Talbot Road");
                venues[2].setPostcode("M16 0PX");
                venues[2].setAddress("Talbot Road, M16 0PX");
                venues[2].setLatitude("53.456428");
                venues[2].setLongitude("-2.2889857");
                venues[2].setEventcount(2);
                venues[2].setOrganiser("Sam");
                venueService.save(venues[2]);
                
                venues[3].setCapacity(2500);
                venues[3].setName("O2 Apollo");
                venues[3].setRoad("Stockport Rd");
                venues[3].setPostcode("M12 6AP");
                venues[3].setAddress("Stockport Rd, M12 6AP");
                venues[3].setLatitude("53.4695489");
                venues[3].setLongitude("-2.2219894");
                venues[3].setEventcount(1);
                venues[3].setOrganiser("Matt");
                venueService.save(venues[3]);
                
                venues[4].setCapacity(80000);
                venues[4].setName("Etihad Stadium");
                venues[4].setRoad("Ashton New Rd");
                venues[4].setPostcode("M11 3FF");
                venues[4].setAddress("Ashton New Rd, M11 3FF");
                venues[4].setLatitude("53.4831381");
                venues[4].setLongitude("-2.2003953,15");
                venues[4].setEventcount(1);
                venues[4].setOrganiser("Sam");
                venueService.save(venues[4]);
       
                for(int i = 0; i < events.length; i++){
                    events[i] = new Event();
                }
               
                Calendar c = Calendar.getInstance();
 
                c.set(2018, 11, 25, 21, 00);
				events[0].setDate(c.getTime());
				events[0].setName("Arctic Monkeys");
				events[0].setOrganiser("Matt");
				events[0].setTime(c.getTime());
				events[0].setVenue(venues[2]);
				events[0].setId(7);
				events[0].setDescription("Jam out with ya mates!!1");
				eventService.save(events[0]);
				
				c.set(2018, 6, 22, 19, 00);
				events[1].setDate(c.getTime());
				events[1].setName("Metallica");
				events[1].setOrganiser("Sam");
				events[1].setTime(c.getTime());
				events[1].setVenue(venues[1]);
				events[1].setId(1);
				events[1].setDescription("Mosh out with ya mates...");
				eventService.save(events[1]);
				
				c.set(2018, 1, 8, 20, 30);
				events[2].setDate(c.getTime());
				events[2].setName("Guns n Roses");
				events[2].setOrganiser("Matt");
				events[2].setTime(c.getTime());
				events[2].setVenue(venues[1]);
				events[2].setId(2);
				events[2].setDescription("\"These guys are awesome!\" - Tom, from Dorset");
				eventService.save(events[2]);
				
				c.set(2018, 4, 22, 18, 30);
				events[3].setDate(c.getTime());
				events[3].setName("Nirvana");
				events[3].setOrganiser("Sam");
				events[3].setTime(c.getTime());
				events[3].setVenue(venues[0]);
				events[3].setId(3);
				events[3].setDescription("So deep man");
				eventService.save(events[3]);
				
				c.set(2018, 8, 11, 22, 00);
				events[4].setDate(c.getTime());
				events[4].setName("Queen");
				events[4].setOrganiser("Raluca");
				events[4].setTime(c.getTime());
				events[4].setVenue(venues[2]);
				events[4].setId(4);
				events[4].setDescription("Its just a fanta sea");
				eventService.save(events[4]);
               
				c.set(2017, 8, 06, 15, 00);
				events[5].setDate(c.getTime());
				events[5].setName("Stone Roses");
				events[5].setOrganiser("Sam");
				events[5].setTime(c.getTime());
				events[5].setVenue(venues[4]);
				events[5].setId(5);
				events[5].setDescription("Ey up r kid");
				eventService.save(events[5]);
 
				c.set(2018, 8, 9, 17, 30);
				events[6].setDate(c.getTime());
				events[6].setName("Pendulum");
				events[6].setOrganiser("Lorena");
				events[6].setTime(c.getTime());
				events[6].setVenue(venues[3]);
				events[6].setId(6);
				events[6].setDescription("Rave with ya mates");
				eventService.save(events[6]);
				
        }
    }
}