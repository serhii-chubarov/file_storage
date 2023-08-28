package net.proselyte.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.proselyte.dao.CrudDao;
import net.proselyte.dto.UserDto;
import net.proselyte.dto.mapper.UserMapper;
import net.proselyte.entity.User;
import net.proselyte.exception.EntityNotFoundException;
import net.proselyte.service.UserService;

import java.util.List;

@Slf4j
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private static final String USER_NOT_FOUND = "User not found!";

    private final UserMapper userMapper;
    private final CrudDao<User> userDao;

    @Override
    public void save(UserDto userDto) {
        log.debug("Service for saving user {}", userDto);
        userDao.save(userMapper.toUser(userDto));
    }

    @Override
    public UserDto findById(Integer id) {
        log.debug("Service for finding user by id");
        return userDao.findById(id)
                      .map(userMapper::toUserDto)
                      .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND));
    }

    @Override
    public List<UserDto> findAll() {
        log.debug("Service for finding all users");
        return userDao.findAll()
                      .stream()
                      .map(userMapper::toUserDto)
                      .toList();
    }

    @Override
    public void update(Integer id, UserDto userDto) {
        log.debug("Service for updating user");
        User user = userDao.findById(id)
                           .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND));
        user.setName(userDto.getName());
    }

    @Override
    public void remove(Integer id) {
        log.debug("Service for removing user");
        userDao.deleteById(id);
    }
}
