package com.viehai.identity_service.identity.application.service;

import com.viehai.identity_service.identity.interfaces.dto.request.UserCreateRequest;
import com.viehai.identity_service.identity.interfaces.dto.request.UserUpdateRequest;
import com.viehai.identity_service.identity.domain.model.UserMongo;
import com.viehai.identity_service.identity.domain.repository.UserMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Profile("mongodb")
public class UserMongoService {

    @Autowired
    private UserMongoRepository userMongoRepository;

    public UserMongo createUser(UserCreateRequest request){
        UserMongo user = new UserMongo();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setDob(request.getDob());

        return userMongoRepository.save(user);
    }

    public List<UserMongo> getUsers(){
        return userMongoRepository.findAll();
    }

    public UserMongo getUser(String id){
        return userMongoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public UserMongo updateUser(String userId, UserUpdateRequest request){
        UserMongo user = getUser(userId);
        user.setPassword(request.getPassword());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setDob(request.getDob());
        return userMongoRepository.save(user);
    }

    public void deleteUser(String userId){
        userMongoRepository.deleteById(userId);
    }
}
