package com.viehai.identity_service.identity.interfaces.rest;

import com.viehai.identity_service.identity.application.query.search.UserSearchQueryService;
import com.viehai.identity_service.identity.application.query.search.dto.AggregationResult;
import com.viehai.identity_service.identity.application.query.search.dto.SearchItem;
import com.viehai.identity_service.identity.application.query.search.dto.SearchResult;
import com.viehai.identity_service.identity.infrastructure.search.model.UserDoc;
import com.viehai.identity_service.identity.application.command.search.UserSearchSyncService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/search/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserSearchController {

    UserSearchQueryService queryService;
    UserSearchSyncService syncService;

    @GetMapping("/fulltext")
    public List<SearchItem> fullText(@RequestParam String q) {
        return queryService.searchFullText(q);
    }

    @GetMapping("/by-username")
    public List<SearchItem> byUsername(@RequestParam("u") String username) {
        return queryService.searchByUsername(username);
    }

    @GetMapping("/advanced")
    public SearchResult advancedSearch(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Integer from,
            @RequestParam(required = false) Integer size
    ) {
        return queryService.searchAdvanced(q, country, city, from, size);
    }

    @GetMapping("/filter")
    public List<SearchItem> searchWithFilters(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) List<String> jobCodes
    ) {
        return queryService.searchWithFilters(firstName, lastName, country, city, jobCodes);
    }


    @GetMapping("/aggregations/country")
    public AggregationResult aggregateByCountry() {
        return queryService.aggregateByCountry();
    }

    @GetMapping("/aggregations/city")
    public AggregationResult aggregateByCity() {
        return queryService.aggregateByCity();
    }

    @GetMapping("/aggregations/job-codes")
    public AggregationResult aggregateByJobCodes() {
        return queryService.aggregateByJobCodes();
    }

    @GetMapping("/statistics")
    public Map<String, Object> getStatistics() {
        return queryService.getStatistics();
    }

    // ====== REINDEX (COMMAND) ======

    @PostMapping("/reindex")
    public Map<String, Object> reindexAll() {
        long n = syncService.reindexAll();
        return Map.of(
                "status", "OK",
                "reindexedCount", n
        );

    }
}


