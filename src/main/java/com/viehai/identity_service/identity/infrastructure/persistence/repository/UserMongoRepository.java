package com.viehai.identity_service.identity.domain.repository;

import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.viehai.identity_service.identity.domain.model.UserMongo;

@Profile("mongodb") // chỉ bật khi chạy mongo
public interface UserMongoRepository extends MongoRepository<UserMongo, String> {
}
