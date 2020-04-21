package uk.ac.man.cs.eventlite.dao;

import uk.ac.man.cs.eventlite.entities.Event;

public interface EventService {

	public long count();

	public Iterable<Event> findAll();
	
	public <S extends Event> S save(S s);
	Iterable<Event> search(String toMatch);
	
	public Event findOne(long id);

	public void delete(long id);

}
