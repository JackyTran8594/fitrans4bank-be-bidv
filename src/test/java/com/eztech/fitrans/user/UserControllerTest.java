package com.eztech.fitrans.user;

import static com.eztech.fitrans.authentication.AuthControllerOAuthTest.NEW_EMAIL;
import static com.eztech.fitrans.authentication.AuthControllerOAuthTest.USERNAME;
import static com.eztech.fitrans.authentication.AuthControllerPasswordTest.AUTH_HEADER;
import static com.eztech.fitrans.authentication.AuthControllerPasswordTest.EXISTING_EMAIL;
import static com.eztech.fitrans.authentication.AuthControllerPasswordTest.TOKEN_PREFIX;

import com.eztech.fitrans.authentication.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Locale;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

import com.eztech.fitrans.authentication.*;
import com.eztech.fitrans.image.Image;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import javax.xml.bind.DatatypeConverter;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserControllerTest {

    public static final String ADMIN_EMAIL = "admin@admin.admin";
    public static final String ADMIN_PASSWORD = "!2e4S";
    public static final String BASE_STRING = "iVBORw0KGgoAAAANSUhEUgAAAAoAAAAKCAYAAACNMs+9AAAABmJLR0QA";
    public static final Long USER_ID = Long.valueOf(1);
    public static final String EXISTING_ID = "1";
    public static final String EXISTING_ID_2 = "2";
    public static final int NEW_AGE = 999;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;

    @Before
    public void setUp() {
        Locale.setDefault(Locale.US);
    }

    @Test
    public void getUser() {
        RefreshTokenDTO login = login();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(AUTH_HEADER, TOKEN_PREFIX + login.getTokens().getAccessToken());

        ResponseEntity<UserDTO> response = restTemplate.exchange("/users/" + EXISTING_ID, HttpMethod.GET,
            new HttpEntity<>(headers), UserDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    public void updateUser() {
        RefreshTokenDTO login = login();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(AUTH_HEADER, TOKEN_PREFIX + login.getTokens().getAccessToken());
        ResponseEntity<UserDTO> existingUser = restTemplate.exchange("/users/" + EXISTING_ID, HttpMethod.GET,
            new HttpEntity<>(headers), UserDTO.class);
        UserDTO updatedUser = existingUser.getBody();
        assert updatedUser != null;
        updatedUser.setAge(NEW_AGE);

        ResponseEntity<UserDTO> response = restTemplate.exchange("/users/" + EXISTING_ID, HttpMethod.PUT,
            new HttpEntity<>(updatedUser, headers), UserDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(updatedUser);
    }

    @Test
    public void getUserImage() {
        RefreshTokenDTO login = login();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(AUTH_HEADER, TOKEN_PREFIX + login.getTokens().getAccessToken());

        ResponseEntity<String> response = restTemplate.exchange("/users/" + EXISTING_ID + "/photo?token=" + login.getTokens().getAccessToken(), HttpMethod.GET,
                new HttpEntity<>(headers), String.class);


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    public void updateUserImage() {
        RefreshTokenDTO login = login();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(AUTH_HEADER, TOKEN_PREFIX + login.getTokens().getAccessToken());
        ResponseEntity<String> existingImageBase64String = restTemplate.
                exchange("/users/" + EXISTING_ID + "/photo" + login.getTokens().getAccessToken(), HttpMethod.GET,
                new HttpEntity<>(headers), String.class);

        String imageInBase64 = existingImageBase64String.getBody();
        assert imageInBase64 != null;
        Image updatedImage = new Image(imageInBase64.getBytes());
        updatedImage.setId(USER_ID);
        updatedImage.setImageBytes(Base64.getDecoder().decode(BASE_STRING.getBytes(StandardCharsets.UTF_8)));

        ResponseEntity<Image> response = restTemplate.exchange("/users/" + EXISTING_ID + "/photo", HttpMethod.PUT,
                new HttpEntity<>(BASE_STRING, headers), Image.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(updatedImage);
    }

    @Test
    public void deleteUser() {
        RefreshTokenDTO login = login();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(AUTH_HEADER, TOKEN_PREFIX + login.getTokens().getAccessToken());
        ResponseEntity<UserDTO> existingUser = restTemplate.exchange("/users/" + EXISTING_ID, HttpMethod.GET,
            new HttpEntity<>(headers), UserDTO.class);
        UserDTO userForDelete = existingUser.getBody();

        ResponseEntity<ResponseMessage> response = restTemplate.exchange("/users/" + EXISTING_ID_2, HttpMethod.DELETE,
            new HttpEntity<>(userForDelete, headers), ResponseMessage.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(new ResponseMessage("Ok"));
        assertThat(userRepository.findById(Long.valueOf(EXISTING_ID_2))).isEqualTo(Optional.empty());
    }

    @Test
    public void currentUser() {
        RefreshTokenDTO login = login();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(AUTH_HEADER, TOKEN_PREFIX + login.getTokens().getAccessToken());

        ResponseEntity<UserDTO> response = restTemplate.exchange("/users/current", HttpMethod.GET,
            new HttpEntity<>(headers), UserDTO.class);

        UserDTO userFromResponse = response.getBody();
        assert userFromResponse != null;

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(userFromResponse.getEmail()).isEqualTo(EXISTING_EMAIL);
    }

    private Claims decodeToken(String jwt) {
        return Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary(tokenService.getAccessTokenSecretKey()))
                .parseClaimsJws(jwt).getBody();
    }

    @Test
    public void updateCurrentUser() {
        RefreshTokenDTO login = login();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(AUTH_HEADER, TOKEN_PREFIX + login.getTokens().getAccessToken());
        ResponseEntity<UserDTO> existingUser = restTemplate.exchange("/users/current", HttpMethod.GET,
            new HttpEntity<>(headers), UserDTO.class);
        UserDTO updatedUser = existingUser.getBody();
        assert updatedUser != null;
        updatedUser.setEmail("updatedemail@user.com");

        ResponseEntity<Tokens> response = restTemplate.exchange("/users/current", HttpMethod.PUT,
            new HttpEntity<>(updatedUser, headers), Tokens.class);

        Tokens tokens = response.getBody();
        assert tokens != null;
        Claims claims = decodeToken(tokens.getAccessToken());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(claims.getSubject()).isEqualTo(updatedUser.getEmail());
    }

    @Test
    public void createUser() {
        RefreshTokenDTO login = login();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(AUTH_HEADER, TOKEN_PREFIX + login.getTokens().getAccessToken());

        UserDTO newUser = new UserDTO(USERNAME, NEW_EMAIL);
        newUser.setId(USER_ID);
        ResponseEntity<UserDTO> response = restTemplate.exchange("/users", HttpMethod.POST,
            new HttpEntity<>(newUser, headers), UserDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(userRepository.findByEmail(NEW_EMAIL)).isNotEmpty();
    }

    private RefreshTokenDTO login() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        LoginDTO request = new LoginDTO(ADMIN_EMAIL, ADMIN_PASSWORD);
        HttpEntity<LoginDTO> entity = new HttpEntity<>(request, headers);

        ResponseEntity<RefreshTokenDTO> response = restTemplate.postForEntity(
            "/auth/login", entity, RefreshTokenDTO.class);
        return response.getBody();
    }

}
