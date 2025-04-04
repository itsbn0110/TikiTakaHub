package com.manageleaguefootball.demo.service.impl;

import com.manageleaguefootball.demo.dto.UserDTO;
import com.manageleaguefootball.demo.exception.AppException;
import com.manageleaguefootball.demo.exception.ErrorCode;
import com.manageleaguefootball.demo.model.User;
import com.manageleaguefootball.demo.repository.UserRepository;
import com.manageleaguefootball.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;
  private static ModelMapper mapper() {
    ModelMapper modelMapper = new ModelMapper();
    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    return modelMapper;
  }

  private static UserDTO mapToViews(User user) {
    if (user == null) {
      return null;
    }
    return mapper().map(user, UserDTO.class);
  }

  private static User mapToEntity(UserDTO userDTO) {
    if (userDTO == null) {
      return null;
    }
    return mapper().map(userDTO, User.class);
  }

  @Override
  public UserDTO saveUser(UserDTO userDTO) {
    User user = userRepository.save(mapToEntity(userDTO));
    return mapToViews(user);
  }

  @Override
  public boolean checkPassword(String email, String password) {
    Optional<User> userOptional = userRepository.findUserByEmail(email);
    if (userOptional.isPresent()) {
      User user = userOptional.get();
      return user.getPassword().equals(password);
    } 
    else {
      throw new AppException(ErrorCode.INVALID_PASSWORD);
    }
    
  }

  @Override
  public boolean checkEmailExists(String email) {
    boolean exists = userRepository.findUserByEmail(email).isPresent();
    if (!exists) {
      throw new AppException(ErrorCode.UNAUTHENTICATED);
    }
    return true;
  }

  @Override
  public boolean login(UserDTO userDTO) {
    boolean exists = userRepository.existsByUsernameAndPassword(userDTO.getUsername(), userDTO.getPassword());
    if (!exists) {
        throw new AppException(ErrorCode.UNAUTHENTICATED);
    }
    return true;
  }
}
