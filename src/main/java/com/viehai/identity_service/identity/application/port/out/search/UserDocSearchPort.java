package com.viehai.identity_service.identity.application.port.out.search;

import com.viehai.identity_service.identity.application.query.search.dto.SearchHitsPage;
import com.viehai.identity_service.identity.application.query.search.dto.SearchItem;
import com.viehai.identity_service.identity.application.query.search.dto.SearchResult;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserDocSearchPort {

    List<SearchItem> search(String queryJson);

    SearchHitsPage<SearchItem> search(String queryJson, Pageable pageable);

    List<SearchItem> findByUsername(String username);

    List<SearchItem> findAll();

    long count();
}


