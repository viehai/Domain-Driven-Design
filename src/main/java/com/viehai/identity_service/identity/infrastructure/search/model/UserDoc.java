package com.viehai.identity_service.identity.infrastructure.search.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "users")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDoc {
    @Id
    String id;

    @Field(type = FieldType.Keyword)
    String username;      // lọc chính xác
    @Field(type = FieldType.Text)
    String firstName;     // full-text
    @Field(type = FieldType.Text)
    String lastName;      // full-text

    private String email;

    @Field(type = FieldType.Text)
    String line;
    @Field(type = FieldType.Text)
    String ward;
    @Field(type = FieldType.Text)
    String city;
    @Field(type = FieldType.Keyword)
    String country;


    @Field(type = FieldType.Keyword)
    List<String> jobCodes;
    @Field(type = FieldType.Text)
    List<String> jobNames;
}