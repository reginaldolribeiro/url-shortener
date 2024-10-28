package com.reginaldolribeiro.url_shortener.adapter.controller.user;

import com.reginaldolribeiro.url_shortener.adapter.controller.url.InvalidUuidException;
import com.reginaldolribeiro.url_shortener.app.port.CreateUserPort;
import com.reginaldolribeiro.url_shortener.app.port.GetUserPort;
import com.reginaldolribeiro.url_shortener.app.usecase.user.CreateUserInput;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Validated
@RestController()
@RequestMapping("/user")
@Slf4j
public class UserController {

    private final CreateUserPort createUserPort;
    private final GetUserPort getUserPort;

    public UserController(CreateUserPort createUserPort, GetUserPort getUserPort) {
        this.createUserPort = createUserPort;
        this.getUserPort = getUserPort;
    }


    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest createUserRequest){
        log.info("Received createUser request with parameters: {}", createUserRequest);
        var input = new CreateUserInput(createUserRequest.name(), createUserRequest.email());
        var output = createUserPort.save(input);
        var response = new UserResponse(output.id(),
                output.name(),
                output.email(),
                output.createdAt(),
                output.updatedAt(),
                output.active()
        );
        return new ResponseEntity<UserResponse>(response, HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public ResponseEntity<UserResponse> findById(@PathVariable @NotBlank String id){
        log.info("Received findById request with parameter: {}", id);
        var parsedId = parseUuid(id);
        var output = getUserPort.findById(parsedId);
        var response = new UserResponse(output.getId(),
                output.getName(),
                output.getEmail(),
                output.getCreatedAt(),
                output.getUpdatedAt(),
                output.isActive()
        );
        return new ResponseEntity<UserResponse>(response, HttpStatus.OK);
    }

    private static UUID parseUuid(String id) {
        try{
            return UUID.fromString(id);
        } catch (Exception e){
            throw new InvalidUuidException("Invalid UUID.");
        }
    }

}
