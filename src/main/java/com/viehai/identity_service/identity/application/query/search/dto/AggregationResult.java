package com.viehai.identity_service.identity.application.query.search.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AggregationResult {
    String name;
    Map<String, Long> buckets;
    Long totalCount;
    Long uniqueCount;
}

