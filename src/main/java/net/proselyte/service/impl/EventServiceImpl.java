package net.proselyte.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.proselyte.dao.CrudDao;
import net.proselyte.dto.EventDto;
import net.proselyte.dto.mapper.EventMapper;
import net.proselyte.dto.mapper.FileMapper;
import net.proselyte.dto.mapper.UserMapper;
import net.proselyte.entity.Event;
import net.proselyte.exception.EntityNotFoundException;
import net.proselyte.service.EventService;

import java.util.List;

@Slf4j
@AllArgsConstructor
public class EventServiceImpl implements EventService {
    private static final String EVENT_NOT_FOUND = "Event not found!";

    private final EventMapper eventMapper;
    private final UserMapper userMapper;
    private final FileMapper fileMapper;
    private final CrudDao<Event> eventDao;

    @Override
    public void save(EventDto eventDto) {
        log.debug("Service for saving event {}", eventDto);
        eventDao.save(eventMapper.toEvent(eventDto));
    }

    @Override
    public EventDto findById(Integer id) {
        log.debug("Service for finding event by id");
        return eventDao.findById(id)
                       .map(eventMapper::toEventDto)
                       .orElseThrow(() -> new EntityNotFoundException(EVENT_NOT_FOUND));
    }

    @Override
    public List<EventDto> findAll() {
        log.debug("Service for finding all events");
        return eventDao.findAll()
                       .stream()
                       .map(eventMapper::toEventDto)
                       .toList();
    }

    @Override
    public void update(Integer id, EventDto eventDto) {
        log.debug("Service for updating event");
        Event event = eventDao.findById(id)
                              .orElseThrow(() -> new EntityNotFoundException(EVENT_NOT_FOUND));
        event.setUser(userMapper.toUser(eventDto.getUser()));
        event.setFile(fileMapper.toFile(eventDto.getFile()));
    }

    @Override
    public void remove(Integer id) {
        log.debug("Service for removing event");
        eventDao.deleteById(id);
    }
}
