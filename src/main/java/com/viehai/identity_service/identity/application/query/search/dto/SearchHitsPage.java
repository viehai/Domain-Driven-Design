package com.viehai.identity_service.identity.application.query.search.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchHitsPage<T> {
    List<T> documents;
    long totalHits;
}

