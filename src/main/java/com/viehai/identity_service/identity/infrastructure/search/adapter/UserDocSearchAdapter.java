package com.viehai.identity_service.identity.infrastructure.search.adapter;

import com.viehai.identity_service.identity.application.port.out.search.UserDocSearchPort;
import com.viehai.identity_service.identity.application.query.search.dto.SearchHitsPage;
import com.viehai.identity_service.identity.application.query.search.dto.SearchItem;
import com.viehai.identity_service.identity.application.query.search.dto.SearchResult;
import com.viehai.identity_service.identity.infrastructure.search.model.UserDoc;
import com.viehai.identity_service.identity.infrastructure.search.repo.UserDocRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.StreamSupport;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserDocSearchAdapter implements UserDocSearchPort {

    ElasticsearchOperations elasticsearchOperations;
    UserDocRepository userDocRepository;

    // -----------------------
    //  MAPPING: UserDoc -> SearchResult
    // -----------------------
    private SearchItem map(UserDoc doc) {
        return SearchItem.builder()
                .id(doc.getId())
                .username(doc.getUsername())
                .email(doc.getEmail())
                .firstName(doc.getFirstName())
                .lastName(doc.getLastName())
                .line(doc.getLine())
                .ward(doc.getWard())
                .city(doc.getCity())
                .country(doc.getCountry())
                .jobCodes(doc.getJobCodes())
                .jobNames(doc.getJobNames())
                .build();
    }

    @Override
    public List<SearchItem> search(String queryJson) {
        StringQuery query = new StringQuery(queryJson);

        return elasticsearchOperations.search(query, UserDoc.class)
                .stream()
                .map(SearchHit::getContent)
                .map(this::map)               // <-- map UserDoc -> SearchItem
                .toList();
    }


    @Override
    public SearchHitsPage<SearchItem> search(String queryJson, Pageable pageable) {
        StringQuery query = new StringQuery(queryJson);
        query.setPageable(pageable);

        SearchHits<UserDoc> hits = elasticsearchOperations.search(query, UserDoc.class);

        List<SearchItem> docs = hits.getSearchHits()
                .stream()
                .map(SearchHit::getContent)
                .map(this::map)
                .toList();

        return SearchHitsPage.<SearchItem>builder()
                .documents(docs)
                .totalHits(hits.getTotalHits())
                .build();
    }

    @Override
    public List<SearchItem> findAll() {
        return StreamSupport.stream(userDocRepository.findAll().spliterator(), false)
                .map(this::map)
                .toList();
    }

    @Override
    public long count() {
        return userDocRepository.count();
    }

    @Override
    public List<SearchItem> findByUsername(String username) {
        return userDocRepository.findByUsername(username)
                .stream()
                .map(this::map)
                .toList();
    }
}
