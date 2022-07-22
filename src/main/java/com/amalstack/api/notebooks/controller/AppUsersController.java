package com.amalstack.api.notebooks.controller;

import com.amalstack.api.notebooks.dto.AppUserInfoDto;
import com.amalstack.api.notebooks.dto.AppUserRegistrationDto;
import com.amalstack.api.notebooks.exception.AppUserNotFoundException;
import com.amalstack.api.notebooks.exception.UsernameAlreadyExistsException;
import com.amalstack.api.notebooks.model.AppUser;
import com.amalstack.api.notebooks.repository.AppUserRepository;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "users", produces = MediaType.APPLICATION_JSON_VALUE)
public class AppUsersController {

    private final AppUserRepository repository;
    private final PasswordEncoder encoder;

    public AppUsersController(AppUserRepository repository, PasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }

    @PostMapping
    public AppUserInfoDto register(@RequestBody AppUserRegistrationDto appUserRegistrationDto) {

        String username = appUserRegistrationDto.email();

        if (repository.findByUsername(username).isPresent()) {
            throw new UsernameAlreadyExistsException(username);
        }

        AppUser user = repository.save(appUserRegistrationDto.toUser(encoder));

        return AppUserInfoDto.fromAppUser(user);
    }


    @GetMapping("/current")
    public AppUserInfoDto current(@AuthenticationPrincipal User user) {
        return repository
                .findByUsername(user.getUsername())
                .map(AppUserInfoDto::fromAppUser)
                .orElseThrow(AppUserNotFoundException::new);
    }


}
