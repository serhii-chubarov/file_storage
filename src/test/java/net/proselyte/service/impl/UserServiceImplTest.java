package net.proselyte.service.impl;

import net.proselyte.dao.CrudDao;
import net.proselyte.dto.UserDto;
import net.proselyte.dto.mapper.UserMapper;
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
class UserServiceImplTest {
    @Mock
    private UserMapper userMapper;

    @Mock
    private CrudDao<User> userDao;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void saveShouldSaveEntity() {
        UserDto userDto =  new UserDto("username");
        User user = new User("username");
        when(userMapper.toUser(any())).thenReturn(user);

        userService.save(userDto);

        verify(userMapper).toUser(any());
        verify(userDao).save(user);
    }

    @Test
    void findByIdShouldReturnDto() {
        UserDto userDto =  new UserDto("username");
        User user = new User("username");
        when(userDao.findById(any())).thenReturn(Optional.of(user));
        when(userMapper.toUserDto(user)).thenReturn(userDto);

        assertThat(userService.findById(any())).isEqualTo(userDto);

        verify(userMapper).toUserDto(any());
        verify(userDao).findById(any());
    }

    @Test
    void findByIdShouldThrowExceptionIfThereIsNoEntity() {
        when(userDao.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(any()))
                               .isInstanceOf(EntityNotFoundException.class)
                               .hasMessage("User not found!");

        verify(userDao).findById(any());
    }

    @Test
    void findAllShouldReturnListOfDto() {
        List<UserDto> usersDto = asList(new UserDto("username"), new UserDto("username"));
        List<User> users = asList(new User("username"), new User("username"));

        when(userDao.findAll()).thenReturn(users);
        when(userMapper.toUserDto(any())).thenReturn(usersDto.get(0))
                                         .thenReturn(usersDto.get(1));

        assertThat(userService.findAll()).isEqualTo(usersDto);

        verify(userDao).findAll();
        verify(userMapper, times(2)).toUserDto(any());
    }

    @Test
    void updateShouldUpdateEntity() {
        UserDto userDto =  new UserDto("foo");
        User user = new User("bar");
        when(userDao.findById(any())).thenReturn(Optional.of(user));

        assertThatCode(() -> userService.update(any(), userDto)).doesNotThrowAnyException();
        assertThat(user.getName()).isEqualTo("foo");

        verify(userDao).findById(any());
    }

    @Test
    void updateShouldThrowExceptionIfThereIsNoEntity() {
        UserDto userDto =  new UserDto("foo");
        when(userDao.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.update(any(), userDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("User not found!");

        verify(userDao).findById(any());
    }

    @Test
    void removeShouldDeleteEntity() {
        userService.remove(any());

        verify(userDao).deleteById(any());
    }
}
