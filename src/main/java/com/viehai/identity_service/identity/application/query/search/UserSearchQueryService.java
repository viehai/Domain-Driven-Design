package com.viehai.identity_service.identity.application.query.search;

import com.viehai.identity_service.identity.application.port.out.search.UserDocSearchPort;
import com.viehai.identity_service.identity.application.query.search.dto.AggregationResult;
import com.viehai.identity_service.identity.application.query.search.dto.SearchHitsPage;
import com.viehai.identity_service.identity.application.query.search.dto.SearchItem;
import com.viehai.identity_service.identity.application.query.search.dto.SearchResult;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserSearchQueryService {

    UserDocSearchPort searchPort;

    @Transactional(readOnly = true)
    public List<SearchItem> searchFullText(String q) {
        if (!StringUtils.hasText(q)) {
            return List.of();
        }

        String escapedQ = escapeJsonString(q);
        String queryJson = String.format("""
            {
              "multi_match": {
                "query": "%s",
                "fields": ["firstName", "lastName", "line", "ward", "city", "jobNames"],
                "type": "best_fields",
                "fuzziness": "AUTO"
              }
            }
            """, escapedQ);

        return searchPort.search(queryJson);
    }

    @Transactional(readOnly = true)
    public SearchResult searchAdvanced(String q, String country, String city, Integer from, Integer size) {
        String queryJson = buildAdvancedQuery(q, country, city);
        Pageable pageable = PageRequest.of(from != null ? from : 0, size != null ? size : 20);

        SearchHitsPage<SearchItem> page = searchPort.search(queryJson, pageable);

        Map<String, AggregationResult> aggregations = new HashMap<>();
        if (!StringUtils.hasText(country) && !StringUtils.hasText(city)) {
            aggregations.put("countries", aggregateByCountry());
            aggregations.put("cities", aggregateByCity());
            aggregations.put("jobCodes", aggregateByJobCodes());
        }

        return SearchResult.builder()
                .documents(page.getDocuments())   // list<SearchItem>
                .totalHits(page.getTotalHits())
                .aggregations(aggregations)
                .build();
    }

    @Transactional(readOnly = true)
    public List<SearchItem> searchWithFilters(
            String firstName,
            String lastName,
            String country,
            String city,
            List<String> jobCodes
    ) {
        String queryJson = buildFilterQuery(firstName, lastName, country, city, jobCodes);
        return searchPort.search(queryJson);
    }

    @Transactional(readOnly = true)
    public AggregationResult aggregateByCountry() {
        List<SearchItem> docs = searchPort.findAll();

        Map<String, Long> buckets = docs.stream()
                .filter(doc -> doc.getCountry() != null)
                .collect(Collectors.groupingBy(SearchItem::getCountry, Collectors.counting()));

        return AggregationResult.builder()
                .name("countries")
                .buckets(buckets)
                .uniqueCount((long) buckets.size())
                .totalCount(buckets.values().stream().mapToLong(Long::longValue).sum())
                .build();
    }

    @Transactional(readOnly = true)
    public AggregationResult aggregateByCity() {
        List<SearchItem> docs = searchPort.findAll();

        Map<String, Long> buckets = docs.stream()
                .filter(doc -> doc.getCity() != null)
                .collect(Collectors.groupingBy(SearchItem::getCity, Collectors.counting()));

        return AggregationResult.builder()
                .name("cities")
                .buckets(buckets)
                .uniqueCount((long) buckets.size())
                .totalCount(buckets.values().stream().mapToLong(Long::longValue).sum())
                .build();
    }

    @Transactional(readOnly = true)
    public AggregationResult aggregateByJobCodes() {
        List<SearchItem> docs = searchPort.findAll();

        Map<String, Long> buckets = new HashMap<>();

        for (SearchItem doc : docs) {
            if (doc.getJobCodes() == null) continue;
            for (String jobCode : doc.getJobCodes()) {
                buckets.put(jobCode, buckets.getOrDefault(jobCode, 0L) + 1);
            }
        }

        return AggregationResult.builder()
                .name("jobCodes")
                .buckets(buckets)
                .uniqueCount((long) buckets.size())
                .totalCount(buckets.values().stream().mapToLong(Long::longValue).sum())
                .build();
    }

    @Transactional(readOnly = true)
    public List<SearchItem> searchByUsername(String username) {
        if (!StringUtils.hasText(username)) {
            return List.of();
        }
        return searchPort.findByUsername(username);
    }

    private String escapeJsonString(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private String buildAdvancedQuery(String q, String country, String city) {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        builder.append("\"bool\": {");

        boolean hasClause = false;
        if (StringUtils.hasText(q)) {
            String escapedQ = escapeJsonString(q);
            builder.append("\"must\": [{");
            builder.append("\"multi_match\": {");
            builder.append("\"query\": \"").append(escapedQ).append("\",");
            builder.append("\"fields\": [");
            builder.append("\"firstName^2.0\",");
            builder.append("\"lastName^2.0\",");
            builder.append("\"city^1.5\",");
            builder.append("\"jobNames^1.5\",");
            builder.append("\"line\",");
            builder.append("\"ward\"");
            builder.append("],");
            builder.append("\"type\": \"best_fields\",");
            builder.append("\"fuzziness\": \"AUTO\"");
            builder.append("}");
            builder.append("}]");
            hasClause = true;
        }

        if (StringUtils.hasText(country) || StringUtils.hasText(city)) {
            if (hasClause) builder.append(",");
            builder.append("\"filter\": [");

            boolean hasFilter = false;
            if (StringUtils.hasText(country)) {
                String escapedCountry = escapeJsonString(country);
                builder.append("{\"match\": {\"country\": \"").append(escapedCountry).append("\"}}");
                hasFilter = true;
            }
            if (StringUtils.hasText(city)) {
                if (hasFilter) builder.append(",");
                String escapedCity = escapeJsonString(city);
                builder.append("{\"match\": {\"city\": \"").append(escapedCity).append("\"}}");
            }

            builder.append("]");
        }

        builder.append("}");
        builder.append("}");
        return builder.toString();
    }

    private String buildFilterQuery(String firstName,
                                    String lastName,
                                    String country,
                                    String city,
                                    List<String> jobCodes) {
        StringBuilder builder = new StringBuilder();
        builder.append("{\"bool\": {");

        boolean hasMust = false;
        boolean hasFilter = false;

        if (StringUtils.hasText(firstName) || StringUtils.hasText(lastName)) {
            builder.append("\"must\": [");
            if (StringUtils.hasText(firstName)) {
                String escapedFirstName = escapeJsonString(firstName);
                builder.append("{\"match\": {\"firstName\": \"").append(escapedFirstName).append("\"}}");
                hasMust = true;
            }
            if (StringUtils.hasText(lastName)) {
                if (hasMust) builder.append(",");
                String escapedLastName = escapeJsonString(lastName);
                builder.append("{\"match\": {\"lastName\": \"").append(escapedLastName).append("\"}}");
                hasMust = true;
            }
            builder.append("],");
        }

        if (StringUtils.hasText(country) || StringUtils.hasText(city) || (jobCodes != null && !jobCodes.isEmpty())) {
            builder.append("\"filter\": [");

            if (StringUtils.hasText(country)) {
                String escapedCountry = escapeJsonString(country);
                builder.append("{\"term\": {\"country\": \"").append(escapedCountry).append("\"}}");
                hasFilter = true;
            }

            if (StringUtils.hasText(city)) {
                if (hasFilter) builder.append(",");
                String escapedCity = escapeJsonString(city);
                builder.append("{\"term\": {\"city\": \"").append(escapedCity).append("\"}}");
                hasFilter = true;
            }

            if (jobCodes != null && !jobCodes.isEmpty()) {
                if (hasFilter) builder.append(",");
                builder.append("{\"terms\": {\"jobCodes\": [");
                for (int i = 0; i < jobCodes.size(); i++) {
                    if (i > 0) builder.append(",");
                    String escapedCode = escapeJsonString(jobCodes.get(i));
                    builder.append("\"").append(escapedCode).append("\"");
                }
                builder.append("]}}");
            }

            builder.append("]");
        }

        builder.append("}}");
        return builder.toString();
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // Total count
        long totalCount = searchPort.count();
        stats.put("totalUsers", totalCount);

        // Unique countries
        AggregationResult countries = aggregateByCountry();
        stats.put("totalCountries", countries.getUniqueCount());
        stats.put("countries", countries.getBuckets());

        // Unique cities
        AggregationResult cities = aggregateByCity();
        stats.put("totalCities", cities.getUniqueCount());
        stats.put("cities", cities.getBuckets());

        // Job codes
        AggregationResult jobCodes = aggregateByJobCodes();
        stats.put("totalJobCodes", jobCodes.getUniqueCount());
        stats.put("jobCodes", jobCodes.getBuckets());

        return stats;
    }

}
