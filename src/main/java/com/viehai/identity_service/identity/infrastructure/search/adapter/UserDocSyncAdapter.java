package com.viehai.identity_service.identity.infrastructure.search.adapter;

import com.viehai.identity_service.identity.application.port.out.search.UserDocSyncPort;
import com.viehai.identity_service.identity.infrastructure.search.model.UserDoc;
import com.viehai.identity_service.identity.infrastructure.search.repo.UserDocRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserDocSyncAdapter implements UserDocSyncPort {

    UserDocRepository userDocRepository;

    @Override
    public void save(UserDoc doc) {
        userDocRepository.save(doc);
    }

    @Override
    public void saveAll(List<UserDoc> docs) {
        userDocRepository.saveAll(docs);
    }

    @Override
    public void deleteById(String id) {
        userDocRepository.deleteById(id);
    }
}
