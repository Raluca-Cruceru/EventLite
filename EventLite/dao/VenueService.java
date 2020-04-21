package uk.ac.man.cs.eventlite.dao;

import uk.ac.man.cs.eventlite.entities.Venue;

public interface VenueService {
	public <S extends Venue> S save(S save);
	public Iterable<Venue> findAll();
	public Iterable<Venue> findAllByCount();
	public long count();
	public Venue findOne(long id);
	public void delete(long id);
}
