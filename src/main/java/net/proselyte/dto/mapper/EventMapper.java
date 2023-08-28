package net.proselyte.dto.mapper;

import net.proselyte.dto.EventDto;
import net.proselyte.entity.Event;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface EventMapper {
    EventMapper INSTANCE = Mappers.getMapper(EventMapper.class);

    EventDto toEventDto(Event event);

    Event toEvent(EventDto eventDto);
}
