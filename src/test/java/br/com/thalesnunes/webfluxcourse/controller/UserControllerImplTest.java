package br.com.thalesnunes.webfluxcourse.controller;

import br.com.thalesnunes.webfluxcourse.entity.User;
import br.com.thalesnunes.webfluxcourse.mapper.UserMapper;
import br.com.thalesnunes.webfluxcourse.model.request.UserRequest;
import br.com.thalesnunes.webfluxcourse.model.response.UserResponse;
import br.com.thalesnunes.webfluxcourse.repository.UserRepository;
import br.com.thalesnunes.webfluxcourse.service.UserService;
import com.mongodb.reactivestreams.client.MongoClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWebTestClient
class UserControllerImplTest {

    private static final String ID = "123456";
    private static final String NAME = "Thales";
    private static final String EMAIL = "thales@email.com";
    private static final String PASSWORD = "123";
    private static final String BASE_URI = "/users";

    @Autowired
    private WebTestClient webTestClient;
    @MockBean
    private UserService service;
    @MockBean
    private UserMapper mapper;
    @MockBean
    private MongoClient mongoClient;
    @MockBean
    private UserRepository repository;

    @Test
    @DisplayName("Test endpoint save with success")
    void testSaveWithSuccess() {
        final var userRequest = new UserRequest(ID, EMAIL, PASSWORD);

        when(service.save(any(UserRequest.class))).thenReturn(Mono.just(User.builder().build()));

        webTestClient.post()
                .uri(BASE_URI)
                .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromValue(userRequest))
                .exchange()
                .expectStatus().isCreated();

        verify(service, times(1)).save(any(UserRequest.class));
    }

    @Test
    @DisplayName("Test endpoint save with bad request")
    void testSaveWithBadRequest() {
        final var request = new UserRequest(NAME.concat(" "), EMAIL, PASSWORD);

        webTestClient.post()
                .uri(BASE_URI)
                .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.path").isEqualTo("/users")
                .jsonPath("$.status").isEqualTo(BAD_REQUEST.value())
                .jsonPath("$.error").isEqualTo("Validation error")
                .jsonPath("$.message").isEqualTo("Error on validation attributes")
                .jsonPath("$.errors[0].fieldName").isEqualTo("name")
                .jsonPath("$.errors[0].message").isEqualTo("field cannot have blank spaces at the beginning or at end");
    }

    @Test
    @DisplayName("Test find by id endpoint with success")
    void testFindByIdWithSuccess() {
        final var userResponse = new UserResponse(ID, NAME, EMAIL, PASSWORD);

        when(service.findById(anyString())).thenReturn(Mono.just(User.builder().build()));
        when(mapper.toResponse(any(User.class))).thenReturn(userResponse);

        webTestClient.get().uri(BASE_URI + "/" + ID)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(ID)
                .jsonPath("$.name").isEqualTo(NAME)
                .jsonPath("$.email").isEqualTo(EMAIL)
                .jsonPath("$.password").isEqualTo(PASSWORD);

        verify(service).findById(anyString());
        verify(mapper).toResponse(any(User.class));
    }

    @Test
    @DisplayName("Test findAll endpoint with success")
    void testfindAllWithSuccess() {

        final var userResponse = new UserResponse(ID, NAME, EMAIL, PASSWORD);

        when(service.findAll()).thenReturn(Flux.just(User.builder().build()));
        when(mapper.toResponse(any(User.class))).thenReturn(userResponse);

        webTestClient.get().uri(BASE_URI)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.[0].id").isEqualTo(ID)
                .jsonPath("$.[0].name").isEqualTo(NAME)
                .jsonPath("$.[0].email").isEqualTo(EMAIL)
                .jsonPath("$.[0].password").isEqualTo(PASSWORD);

        verify(service).findAll();
        verify(mapper).toResponse(any(User.class));
    }

    @Test
    @DisplayName("Test update endpoint with success")
    void testeUpdateWithSuccess() {
        final var userResponse = new UserResponse(ID, NAME, EMAIL, PASSWORD);
        final var userRequest = new UserRequest(NAME.concat(" "), EMAIL, PASSWORD);

        when(service.update(anyString(), any(UserRequest.class)))
                .thenReturn(Mono.just(User.builder().build()));
        when(mapper.toResponse(any(User.class))).thenReturn(userResponse);

        webTestClient.patch()
                .uri(BASE_URI + "/" + ID)
                .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromValue(userRequest))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(ID)
                .jsonPath("$.name").isEqualTo(NAME)
                .jsonPath("$.email").isEqualTo(EMAIL)
                .jsonPath("$.password").isEqualTo(PASSWORD);

        verify(service).update(anyString(), any(UserRequest.class));
        verify(mapper).toResponse(any(User.class));
    }

    @Test
    @DisplayName("Test delete endpoint with success")
    void testDeleteWithSuccess() {

        when(service.delete(anyString()))
                .thenReturn(Mono.just(User.builder().build()));

        webTestClient.delete()
                .uri(BASE_URI + "/" + ID)
                .exchange()
                .expectStatus().isOk();

        verify(service, times(1)).delete(anyString());
    }
}