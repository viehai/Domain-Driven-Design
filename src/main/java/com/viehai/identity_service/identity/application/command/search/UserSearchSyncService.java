package com.viehai.identity_service.identity.application.command.search;

import com.viehai.identity_service.identity.application.port.out.search.UserDocSyncPort;
import com.viehai.identity_service.identity.infrastructure.search.model.UserDoc;
import com.viehai.identity_service.identity.domain.model.Address;
import com.viehai.identity_service.identity.domain.model.Job;
import com.viehai.identity_service.identity.domain.model.User;
import com.viehai.identity_service.identity.domain.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserSearchSyncService {
    UserDocSyncPort syncPort;
    UserRepository userRepository;

    public void upsertFrom(User user) {
        if (user == null) {
            return;
        }
        syncPort.save(toDoc(user));
    }

    public void upsertAll(List<User> users) {
        if (users == null || users.isEmpty()) {
            return;
        }
        syncPort.saveAll(users.stream()
                .filter(Objects::nonNull)
                .map(this::toDoc)
                .toList());
    }

    public void deleteById(String id) {
        syncPort.deleteById(id);
    }

    @Transactional
    public int reindexAll() {
        List<User> all = userRepository.findAll();
        upsertAll(all);
        return all.size();
    }

    private UserDoc toDoc(User user) {
        Address address = user.getAddress();
        Set<Job> jobs = user.getJobs();

        return UserDoc.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .line(address != null ? address.getLine() : null)
                .ward(address != null ? address.getWard() : null)
                .city(address != null ? address.getCity() : null)
                .country(address != null ? address.getCountry() : null)
                .jobCodes(jobs != null ? jobs.stream()
                        .map(Job::getCode)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()) : null)
                .jobNames(jobs != null ? jobs.stream()
                        .map(Job::getName)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()) : null)
                .build();
    }
}

