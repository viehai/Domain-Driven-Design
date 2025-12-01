package com.viehai.identity_service.identity.infrastructure.search.repo;

import com.viehai.identity_service.identity.infrastructure.search.model.UserDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface UserDocRepository extends ElasticsearchRepository<UserDoc, String> {
    List<UserDoc> findByUsername(String username);
    List<UserDoc> findByFirstNameContainingIgnoreCase(String q);
    List<UserDoc> findByLastNameContainingIgnoreCase(String q);
}
