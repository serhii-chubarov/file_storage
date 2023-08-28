package net.proselyte.service.impl;

import net.proselyte.dao.CrudDao;
import net.proselyte.dto.EventDto;
import net.proselyte.dto.FileDto;
import net.proselyte.dto.UserDto;
import net.proselyte.dto.mapper.EventMapper;
import net.proselyte.dto.mapper.FileMapper;
import net.proselyte.dto.mapper.UserMapper;
import net.proselyte.entity.Event;
import net.proselyte.entity.File;
import net.proselyte.entity.User;
import net.proselyte.exception.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {
    @Mock
    private EventMapper eventMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private FileMapper fileMapper;

    @Mock
    private CrudDao<Event> eventDao;

    @InjectMocks
    private EventServiceImpl eventService;

    @Test
    void saveShouldSaveEntity() {
        EventDto eventDto =  new EventDto();
        Event event = new Event();
        when(eventMapper.toEvent(any())).thenReturn(event);

        eventService.save(eventDto);

        verify(eventMapper).toEvent(any());
        verify(eventDao).save(event);
    }

    @Test
    void findByIdShouldReturnDto() {
        EventDto eventDto =  new EventDto();
        Event event = new Event();
        when(eventDao.findById(any())).thenReturn(Optional.of(event));
        when(eventMapper.toEventDto(event)).thenReturn(eventDto);

        assertThat(eventService.findById(any())).isEqualTo(eventDto);

        verify(eventMapper).toEventDto(any());
        verify(eventDao).findById(any());
    }

    @Test
    void findByIdShouldThrowExceptionIfThereIsNoEntity() {
        when(eventDao.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.findById(any()))
                               .isInstanceOf(EntityNotFoundException.class)
                               .hasMessage("Event not found!");

        verify(eventDao).findById(any());
    }

    @Test
    void findAllShouldReturnListOfDto() {
        List<EventDto> eventsDto = asList(new EventDto(), new EventDto());
        List<Event> events = asList(new Event(), new Event());

        when(eventDao.findAll()).thenReturn(events);
        when(eventMapper.toEventDto(any())).thenReturn(eventsDto.get(0))
                                          .thenReturn(eventsDto.get(1));

        assertThat(eventService.findAll()).isEqualTo(eventsDto);

        verify(eventDao).findAll();
        verify(eventMapper, times(2)).toEventDto(any());
    }

    @Test
    void updateShouldUpdateEntity() {
        UserDto userDto = new UserDto("bob");
        FileDto fileDto = new FileDto("file_1", "/home");
        EventDto eventDto =  new EventDto(userDto, fileDto);

        Event event = new Event();
        User user = new User("bob");
        File file = new File("file_1", "/home");

        when(eventDao.findById(any())).thenReturn(Optional.of(event));
        when(userMapper.toUser(any())).thenReturn(user);
        when(fileMapper.toFile(any())).thenReturn(file);

        assertThatCode(() -> eventService.update(any(), eventDto)).doesNotThrowAnyException();
        assertThat(event.getUser()).isEqualTo(user);
        assertThat(event.getFile()).isEqualTo(file);

        verify(eventDao).findById(any());
        verify(userMapper).toUser(any());
        verify(fileMapper).toFile(any());
    }

    @Test
    void updateShouldThrowExceptionIfThereIsNoEntity() {
        EventDto eventDto =  new EventDto();
        when(eventDao.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.update(any(), eventDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Event not found!");

        verify(eventDao).findById(any());
    }

    @Test
    void removeShouldDeleteEntity() {
        eventService.remove(any());

        verify(eventDao).deleteById(any());
    }
}
