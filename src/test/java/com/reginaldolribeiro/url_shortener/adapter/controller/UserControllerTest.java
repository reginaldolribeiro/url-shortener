package com.reginaldolribeiro.url_shortener.adapter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reginaldolribeiro.url_shortener.FixtureTests;
import com.reginaldolribeiro.url_shortener.adapter.controller.user.CreateUserRequest;
import com.reginaldolribeiro.url_shortener.adapter.controller.user.UserController;
import com.reginaldolribeiro.url_shortener.app.exception.UserNotFoundException;
import com.reginaldolribeiro.url_shortener.app.port.CreateUserPort;
import com.reginaldolribeiro.url_shortener.app.port.GetUserPort;
import com.reginaldolribeiro.url_shortener.app.usecase.user.CreateUserInput;
import com.reginaldolribeiro.url_shortener.app.usecase.user.CreateUserOutput;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CreateUserPort createUserPort;

    @MockBean
    private GetUserPort getUserPort;

    @Nested
    @DisplayName("POST /user - Create User")
    class CreateUserTests {

        @Test
        @DisplayName("Should create a User for a valid request")
        void shouldCreateUserForValidRequest() throws Exception {
            var createUserRequest = new CreateUserRequest(FixtureTests.DEFAULT_USER_NAME, FixtureTests.DEFAULT_USER_EMAIL);
            var request = objectMapper.writer().writeValueAsString(createUserRequest);
            var createUserOutput = new CreateUserOutput(
                    UUID.randomUUID(),
                    createUserRequest.name(),
                    createUserRequest.email(),
                    LocalDateTime.now(Clock.systemUTC()),
                    LocalDateTime.now(Clock.systemUTC()),
                    true
            );

            when(createUserPort.save(any(CreateUserInput.class))).thenReturn(createUserOutput);

            mockMvc.perform(
                            MockMvcRequestBuilders
                                    .post("/user")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .accept(MediaType.APPLICATION_JSON)
                                    .content(request)
                    )
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(createUserOutput.id().toString()))
                    .andExpect(jsonPath("$.name").value(createUserRequest.name()))
                    .andExpect(jsonPath("$.email").value(createUserRequest.email()))
                    .andExpect(jsonPath("$").isMap())
                    .andExpect(jsonPath("$.name").isString())
                    .andExpect(jsonPath("$.email").isString())
                    .andExpect(jsonPath("$.created_at").isString())
                    .andExpect(jsonPath("$.updated_at").isString())
                    .andExpect(jsonPath("$.active").isBoolean())
                    .andExpect(jsonPath("$.length()").value(6));

            verify(createUserPort, times(1)).save(any(CreateUserInput.class));
        }

        // Body = {"status":400,"message":"Validation Failed","errors":{"name":"must not be blank"},"timestamp":"2024-10-28T20:01:55.69734"}

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"    "}) // Test with white spaces
        @DisplayName("Should return Bad Request Error for null, empty and blank name")
        void shouldReturnBadRequestForInvalidInputName(String invalidName) throws Exception {
            var requestJson = createUserJson(invalidName, "valid.email@example.com");

            mockMvc.perform(
                            MockMvcRequestBuilders
                                    .post("/user")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .accept(MediaType.APPLICATION_JSON)
                                    .content(requestJson)
                    )
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.message").value("Validation Failed"))
                    .andExpect(jsonPath("$.errors").isNotEmpty())
                    .andExpect(jsonPath("$.errors.name").value("must not be blank"))
                    .andExpect(jsonPath("$").isMap())
                    .andExpect(jsonPath("$.status").isNumber())
                    .andExpect(jsonPath("$.message").isString())
                    .andExpect(jsonPath("$.errors").isMap())
                    .andExpect(jsonPath("$.timestamp").isString())
                    .andExpect(jsonPath("$.length()").value(4));

            verify(createUserPort, times(0)).save(any(CreateUserInput.class));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"    "}) // Test with white spaces
        @DisplayName("Should return Bad Request Error for null, empty and blank email")
        void shouldReturnBadRequestForNullOrEmptyOrBlankEmail(String invalidEmail) throws Exception {
            var requestJson = createUserJson("User1", invalidEmail);

            mockMvc.perform(
                            MockMvcRequestBuilders
                                    .post("/user")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .accept(MediaType.APPLICATION_JSON)
                                    .content(requestJson)
                    )
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.message").value("Validation Failed"))
                    .andExpect(jsonPath("$.errors").isNotEmpty())
                    .andExpect(jsonPath("$.errors.email", anyOf(
                            is("must not be blank"),
                            is("must be a well-formed email address")
                    )))
                    .andExpect(jsonPath("$").isMap())
                    .andExpect(jsonPath("$.status").isNumber())
                    .andExpect(jsonPath("$.message").isString())
                    .andExpect(jsonPath("$.errors").isMap())
                    .andExpect(jsonPath("$.timestamp").isString())
                    .andExpect(jsonPath("$.length()").value(4));

            verify(createUserPort, times(0)).save(any(CreateUserInput.class));
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "invalid-email",         // missing '@' and domain
                "user@.com",             // domain starts with a dot
                "user.com",              // missing '@'
                "@example.com",          // missing local part
                "user@com.",             // domain ends with a dot
                "user@-example.com",     // domain starts with a hyphen
                "user@example..com",     // double dot in domain
                "user@example.com.",     // trailing dot in domain
                "user@ example.com",     // space after '@'
                "user@example .com",     // space in domain
                "user@.example.com",     // domain starts with a dot
                "user@example,com",      // comma in domain
                "user@example@com"       // multiple '@' symbols
        })
        @DisplayName("Should return Bad Request Error for null, empty and blank email")
        void shouldReturnBadRequestForInvalidEmail(String invalidEmail) throws Exception {
            var requestJson = createUserJson("User1", invalidEmail);

            mockMvc.perform(
                            MockMvcRequestBuilders
                                    .post("/user")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .accept(MediaType.APPLICATION_JSON)
                                    .content(requestJson)
                    )
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.message").value("Validation Failed"))
                    .andExpect(jsonPath("$.errors").isNotEmpty())
                    .andExpect(jsonPath("$.errors.email").value("must be a well-formed email address"))
                    .andExpect(jsonPath("$").isMap())
                    .andExpect(jsonPath("$.status").isNumber())
                    .andExpect(jsonPath("$.message").isString())
                    .andExpect(jsonPath("$.errors").isMap())
                    .andExpect(jsonPath("$.timestamp").isString())
                    .andExpect(jsonPath("$.length()").value(4));

            verify(createUserPort, times(0)).save(any(CreateUserInput.class));
        }

        @Test
        @DisplayName("Should handle service layer exceptions gracefully")
        void shouldHandleServiceExceptions() throws Exception {
            var createUserRequest = new CreateUserRequest(FixtureTests.DEFAULT_USER_NAME, FixtureTests.DEFAULT_USER_EMAIL);
            var requestJson = objectMapper.writer().writeValueAsString(createUserRequest);

            when(createUserPort.save(any(CreateUserInput.class))).thenThrow(new RuntimeException("Database error"));

            mockMvc.perform(
                            MockMvcRequestBuilders
                                    .post("/user")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .accept(MediaType.APPLICATION_JSON)
                                    .content(requestJson)
                    )
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(500))
                    .andExpect(jsonPath("$.message").value("An unexpected error occurred."))
                    .andExpect(jsonPath("$.errors").isNotEmpty())
                    .andExpect(jsonPath("$.errors.message").value("An unexpected error occurred."))
                    .andExpect(jsonPath("$").isMap())
                    .andExpect(jsonPath("$.status").isNumber())
                    .andExpect(jsonPath("$.message").isString())
                    .andExpect(jsonPath("$.errors").isMap())
                    .andExpect(jsonPath("$.timestamp").isString())
                    .andExpect(jsonPath("$.length()").value(4));

            verify(createUserPort, times(1)).save(any(CreateUserInput.class));
        }

    }

    @Nested
    @DisplayName("GET /user/{id} - Find User")
    class FindUserTests {

        @Test
        @DisplayName("Should return user details for a valid UUID")
        void shouldReturnUserForValidUuid() throws Exception {
            var userId = UUID.randomUUID();
            var response = FixtureTests.sampleActiveUserResponse(userId);
            var user = FixtureTests.createSampleUser();

            when(getUserPort.findById(userId)).thenReturn(user);

            mockMvc.perform(get("/user/{id}", userId.toString())
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                    .andExpect(jsonPath("$.id").value(notNull()))
                    .andExpect(jsonPath("$.name").value(user.getName()))
                    .andExpect(jsonPath("$.email").value(user.getEmail()))
                    .andExpect(jsonPath("$.created_at").isString())
                    .andExpect(jsonPath("$.updated_at").isString())
                    .andExpect(jsonPath("$.active").value(true))
                    .andExpect(jsonPath("$").isMap())
                    .andExpect(jsonPath("$.length()").value(6));

            verify(getUserPort, times(1)).findById(userId);
        }

        @ParameterizedTest(name = "Should return Not Found for non-existing user ID: {0}")
        @ValueSource(strings = {
                "00000000-0000-0000-0000-000000000000",
                "123e4567-e89b-12d3-a456-426614174000"
        })
        @DisplayName("Should return Not Found for non-existing user IDs")
        void shouldReturnNotFoundForNonExistingUser(String userId) throws Exception {
            var uuid = UUID.fromString(userId);
            when(getUserPort.findById(uuid)).thenThrow(new UserNotFoundException("User " + userId + " not found."));

            mockMvc.perform(get("/user/{id}", userId)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.message").value("User " + userId + " not found."))
                    .andExpect(jsonPath("$.errors").isNotEmpty())
                    .andExpect(jsonPath("$.errors.message").value("User " + userId + " not found."))
                    .andExpect(jsonPath("$.timestamp").isString())
                    .andExpect(jsonPath("$").isMap())
                    .andExpect(jsonPath("$.status").isNumber())
                    .andExpect(jsonPath("$.message").isString())
                    .andExpect(jsonPath("$.errors").isMap())
                    .andExpect(jsonPath("$.timestamp").isString())
                    .andExpect(jsonPath("$.length()").value(4));

            verify(getUserPort, times(1)).findById(uuid);
        }

        @ParameterizedTest(name = "Should return Bad Request for invalid UUID: '{0}'")
        @ValueSource(strings = {
                "invalid-uuid",
                "12345",
                "ghijklmno-pqrst-uvwxyz"
        })
        @DisplayName("Should return Bad Request for invalid UUIDs")
        void shouldReturnBadRequestForInvalidUuid(String invalidUuid) throws Exception {
            mockMvc.perform(get("/user/{id}", invalidUuid)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.message").value("Invalid UUID."))
                    .andExpect(jsonPath("$.errors").isNotEmpty())
                    .andExpect(jsonPath("$.errors.message").value("Invalid UUID."))
                    .andExpect(jsonPath("$.timestamp").isString())
                    .andExpect(jsonPath("$").isMap())
                    .andExpect(jsonPath("$.status").isNumber())
                    .andExpect(jsonPath("$.message").isString())
                    .andExpect(jsonPath("$.errors").isMap())
                    .andExpect(jsonPath("$.timestamp").isString())
                    .andExpect(jsonPath("$.length()").value(4));

            verify(getUserPort, times(0)).findById(any(UUID.class));
        }

        @Test
        @DisplayName("Should handle service layer exceptions gracefully")
        void shouldHandleServiceExceptions() throws Exception {
            var userId = UUID.randomUUID();

            when(getUserPort.findById(userId)).thenThrow(new RuntimeException("Database error"));

            mockMvc.perform(get("/user/{id}", userId)
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(500))
                    .andExpect(jsonPath("$.message").value("An unexpected error occurred."))
                    .andExpect(jsonPath("$.errors").isNotEmpty())
                    .andExpect(jsonPath("$.errors.message").value("An unexpected error occurred."))
                    .andExpect(jsonPath("$").isMap())
                    .andExpect(jsonPath("$.status").isNumber())
                    .andExpect(jsonPath("$.message").isString())
                    .andExpect(jsonPath("$.errors").isMap())
                    .andExpect(jsonPath("$.timestamp").isString())
                    .andExpect(jsonPath("$.length()").value(4));
        }
    }

    private String createUserJson(String name, String email) {
        if (name == null && email == null) {
            return "{\"name\": null, \"email\": null}";
        }
        if (name == null) {
            return String.format("{\"name\": null, \"email\": \"%s\"}", email);
        }
        if (email == null) {
            return String.format("{\"name\": \"%s\", \"email\": null}", name);
        }
        return String.format("{\"name\": \"%s\", \"email\": \"%s\"}", name, email);
    }

}