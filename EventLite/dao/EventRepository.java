package uk.ac.man.cs.eventlite.dao;
import org.springframework.data.repository.CrudRepository;
import uk.ac.man.cs.eventlite.entities.Event;

public interface EventRepository extends CrudRepository <Event, Long>{
	
	long count();

	public Iterable<Event> findAllByOrderByDateAscTimeAsc();
	public Iterable<Event> findAllByNameContainingOrderByDateAscTimeAsc(String matcher);
}

