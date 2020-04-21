package uk.ac.man.cs.eventlite.dao;
import org.springframework.stereotype.Service;
import uk.ac.man.cs.eventlite.entities.Venue;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class VenueServiceImpl implements VenueService {
	
	@Autowired
	private VenueRepository venueRepo;

	@Override
	public <S extends Venue> S save(S save) {
		return venueRepo.save(save);
	}
	
	@Override
	public long count() {
		return venueRepo.count();
	}

	@Override
	public Iterable<Venue> findAll() {
		return venueRepo.findAllByOrderByNameAsc();
	}
	
	@Override
	public Iterable<Venue> findAllByCount() {
		return venueRepo.findAllByOrderByEventcountDesc();
	}
	
	
	@Override
	public Venue findOne(long id) {
		return venueRepo.findOne(id);
	}
	
	public void delete(long id)
	{
		venueRepo.delete(id);
	}

}
