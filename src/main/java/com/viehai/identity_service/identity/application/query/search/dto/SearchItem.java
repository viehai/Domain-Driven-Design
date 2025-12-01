package com.viehai.identity_service.identity.application.query.search.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchItem {
    private String id;
    private String username;
    private String email;

    private String firstName;
    private String lastName;

    private String line;
    private String ward;
    private String city;
    private String country;

    private List<String> jobCodes;
    private List<String> jobNames;
}
