package com.viehai.identity_service.identity.application.query.search.dto;

import com.viehai.identity_service.identity.infrastructure.search.model.UserDoc;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResult {
    List<SearchItem> documents;
    long totalHits;
    Map<String, AggregationResult> aggregations;
}