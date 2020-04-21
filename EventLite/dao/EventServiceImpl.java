package uk.ac.man.cs.eventlite.dao;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.ac.man.cs.eventlite.entities.Event;

@Service
public class EventServiceImpl implements EventService {


	@Autowired
	private EventRepository eventRepository;

	@Override
	public long count() {

		return eventRepository.count();
	}

	@Override
	public Iterable<Event> findAll() {
	
		return eventRepository.findAllByOrderByDateAscTimeAsc();
	}
	
	@Override
	public <S extends Event> S save(S s)
	{
		return eventRepository.save(s);
	}
	
	public void delete(long id)
	{
		eventRepository.delete(id);
	}
	
	@Override
	public Iterable<Event> search(String toMatch) {
		return eventRepository.findAllByNameContainingOrderByDateAscTimeAsc(toMatch);
	}
	
	@Override
	public Event findOne(long id) {
		return eventRepository.findOne(id);
	}
}
