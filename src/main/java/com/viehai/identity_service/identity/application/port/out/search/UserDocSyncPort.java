package com.viehai.identity_service.identity.application.port.out.search;

import com.viehai.identity_service.identity.infrastructure.search.model.UserDoc;

import java.util.List;

public interface UserDocSyncPort {

    void save(UserDoc doc);

    void saveAll(List<UserDoc> docs);

    void deleteById(String id);
}
