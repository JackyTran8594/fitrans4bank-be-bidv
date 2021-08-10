/*
 * Copyright (c) Akveo 2019. All Rights Reserved.
 * Licensed under the Personal / Commercial License.
 * See LICENSE_PERSONAL / LICENSE_COMMERCIAL in the project root for license information on type of purchased license.
 */

package com.eztech.fitrans.user;

import com.eztech.fitrans.address.AddressDTO;
import com.eztech.fitrans.authentication.AuthenticationTokenService;
import com.eztech.fitrans.authentication.SignUpDTO;
import com.eztech.fitrans.authentication.TokenService;
import com.eztech.fitrans.authentication.Tokens;
import com.eztech.fitrans.authentication.exception.PasswordsDontMatchException;
import com.eztech.fitrans.authentication.exception.TokenValidationException;
import com.eztech.fitrans.authentication.exception.UserAlreadyExistsHttpException;
import com.eztech.fitrans.authentication.exception.UserNotFoundHttpException;
import com.eztech.fitrans.ecommerce.entity.GridData;
import com.eztech.fitrans.ecommerce.entity.builder.PageableBuilder;
import com.eztech.fitrans.image.Image;
import com.eztech.fitrans.image.ImageRepository;
import com.eztech.fitrans.role.RoleService;
import com.eztech.fitrans.settings.Settings;
import com.eztech.fitrans.settings.SettingsService;
import com.eztech.fitrans.user.exception.AccessTokenNotFoundHttpException;
import com.eztech.fitrans.user.exception.UserAlreadyExistsException;
import com.eztech.fitrans.user.exception.UserNotFoundException;
import com.eztech.fitrans.user.specification.builder.UserSpecificationBuilder;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.nio.charset.StandardCharsets;

import java.util.Base64;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@SuppressWarnings({"checkstyle:ParameterNumber"})
public class UserService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private ModelMapper modelMapper;
    private PageableBuilder pageableBuilder;
    private RoleService roleService;
    private ImageRepository imageRepository;
    private SettingsService settingsService;
    private AuthenticationTokenService authenticationTokenService;

    @Value("${user.defaultImage}")
    private String defaultImage;

    @Autowired
    private TokenService tokenService;

    public static final Integer DEFAULT_AGE = 18;

    @Autowired
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       ModelMapper modelMapper,

                       SettingsService settingsService,
                       RoleService roleService,
                       ImageRepository imageRepository,
                       AuthenticationTokenService authenticationTokenService,
                       PageableBuilder pageableBuilder) {
        this.pageableBuilder = pageableBuilder;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
        this.settingsService = settingsService;
        this.authenticationTokenService = authenticationTokenService;
        this.roleService = roleService;
        this.imageRepository = imageRepository;
    }

    public User findByEmail(String email) throws UserNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with email: " + email + " not found"));
    }

    @Transactional
    public User register(SignUpDTO signUpDTO) throws UserAlreadyExistsException {
        if (!signUpDTO.getPassword().equals(signUpDTO.getConfirmPassword())) {
            throw new PasswordsDontMatchException();
        }

        String email = signUpDTO.getEmail();

        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserAlreadyExistsException(email);
        }

        User user = signUpUser(signUpDTO);

        imageRepository.save(user.getImage());

        return userRepository.save(user);
    }

    @Transactional
    public void changePassword(ChangePasswordRequest changePasswordRequest) {
        User user = changePasswordRequest.getUser();

        String encodedPassword = encodePassword(changePasswordRequest.getPassword());
        user.setPasswordHash(encodedPassword);

        userRepository.save(user);
    }

    public UserDTO getUserById(Long id) {
        User existingUser = userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundHttpException("User with id: " + id + " not found", HttpStatus.NOT_FOUND)
        );

        return modelMapper.map(existingUser, UserDTO.class);
    }

    @Transactional
    public UserDTO updateUserById(Long userId, UserDTO userDTO) {
        try {
            return updateUser(userId, userDTO);
        } catch (UserAlreadyExistsException exception) {
            throw new UserAlreadyExistsHttpException();
        }
    }

    @Transactional
    public boolean deleteUser(Long id) {
        try {
            userRepository.deleteById(id);
            return true;
        } catch (EmptyResultDataAccessException e) {
            throw new UserNotFoundHttpException("User with id: " + id + " not found", HttpStatus.NOT_FOUND);
        }
    }

    public UserDTO getCurrentUser() {
        User user = UserContextHolder.getUser();
        user.setSettings(settingsService.getSettingsByUserId(user.getId()));

        return modelMapper.map(user, UserDTO.class);
    }

    public Tokens updateCurrentUser(UserDTO userDTO) {
        try {
            User user = UserContextHolder.getUser();
            Long id = user.getId();
            UserDTO updatedUser = updateUser(id, userDTO);
            user = modelMapper.map(updatedUser, User.class);
            return tokenService.createToken(user);
        } catch (UserAlreadyExistsException exception) {
            throw new UserAlreadyExistsHttpException();
        }
    }

    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        try {
            User user = modelMapper.map(userDTO, User.class);

            String email = user.getEmail();
            if (userRepository.findByEmail(email).isPresent()) {
                throw new UserAlreadyExistsException(email);
            }

            // In current version password and role are default
            user.setPasswordHash(encodePassword("testPass"));
            user.setRoles(new HashSet<>(Collections.singletonList(roleService.getDefaultRole())));
            user.setImage(new Image());
            userDTO.setImageBase64(defaultImage);

            imageRepository.save(user.getImage());
            userRepository.save(user);

            return modelMapper.map(user, UserDTO.class);
        } catch (UserAlreadyExistsException exception) {
            throw new UserAlreadyExistsHttpException();
        }
    }

    private Image convertBaseStringToImage(String baseString) {
        Image userImage = new Image();
        byte[] decodedString = Base64.getDecoder().decode(baseString.getBytes(StandardCharsets.UTF_8));
        userImage.setImageBytes(decodedString);
        return userImage;
    }

    private UserDTO updateUser(Long id, UserDTO userDTO) throws UserAlreadyExistsException {
        User existingUser = userRepository.findById(id).
                orElseThrow(() -> new UserNotFoundHttpException(
                        "User with id: " + id + " not found", HttpStatus.NOT_FOUND)
                );


        User updatedUser = modelMapper.map(userDTO, User.class);
        String updatedUserEmail = updatedUser.getEmail();
        if (!existingUser.getEmail().equals(updatedUserEmail)
                && userRepository.findByEmail(updatedUserEmail).isPresent()) {
            throw new UserAlreadyExistsException(updatedUserEmail);
        }

        updatedUser.setId(id);
        updatedUser.setPasswordHash(existingUser.getPasswordHash());
        // Current version doesn't update roles
        updatedUser.setRoles(existingUser.getRoles());
        updatedUser.setImage(existingUser.getImage());
        userRepository.save(updatedUser);

        return modelMapper.map(updatedUser, UserDTO.class);
    }

    private User signUpUser(SignUpDTO signUpDTO) {
        User user = new User();
        user.setEmail(signUpDTO.getEmail());
        user.setLogin(signUpDTO.getFullName());
        String encodedPassword = encodePassword(signUpDTO.getPassword());
        user.setPasswordHash(encodedPassword);
        user.setAge(DEFAULT_AGE);
        user.setRoles(new HashSet<>(Collections.singletonList(roleService.getDefaultRole())));
        //Set default settings and image
        user.setSettings(new Settings("cosmic"));
        user.setImage(new Image());

        return user;
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    public Image getImageById(Long id, String token) {
        try {
            authenticationTokenService.createToken(token);
        } catch (TokenValidationException e) {
            throw new AccessTokenNotFoundHttpException("Access token wasn't found", HttpStatus.NOT_FOUND);
        }

        Image existingImage = imageRepository
                .findById(id)
                .orElseThrow(
                        () -> new RuntimeException("image is not exist")
                );
        if (existingImage.getImageBytes() == null) {
            existingImage = convertBaseStringToImage(defaultImage);
            existingImage.setId(id);
        }
        return existingImage;
    }

    public Image updateUserImageById(Long id, String baseString) {
        imageRepository.findById(id).
                orElseThrow(() -> new RuntimeException("image is not exist"));
        Image existingImage;
        if (baseString != null) {
            existingImage = convertBaseStringToImage(baseString);
        } else {
            existingImage = new Image();
        }
        existingImage.setId(id);

        imageRepository.save(existingImage);

        return existingImage;
    }

    private List<UserDTO> mapOrdersToOrderDTO(List<User> orders) {
        return orders.stream().map(order -> {
            UserDTO dto = modelMapper.map(order, UserDTO.class);
            if (dto.getAddress() == null) {
                dto.setAddress(new AddressDTO());
            }
            return dto;
        }).collect(Collectors.toList());
    }

    private GridData<UserDTO> parsePageToGridData(Page<User> orderPages) {
        GridData<UserDTO> gridData = new GridData<>();
        List<User> orderList = orderPages.getContent();
        long totalCount = orderPages.getTotalElements();
        gridData.setItems(mapOrdersToOrderDTO(orderList));
        gridData.setTotalCount(totalCount);
        return gridData;
    }

    public GridData<UserDTO> getDataForGrid(UsersGridFilter filter) {
        UserSpecificationBuilder specificationBuilder = new UserSpecificationBuilder();

        Pageable paginationAndSort = pageableBuilder.build(filter);
        Optional<Specification<User>> optionalSpec = specificationBuilder.build(filter);
        Page<User> orderPages = optionalSpec
                .map(userSpecification -> userRepository.findAll(userSpecification, paginationAndSort))
                .orElseGet(() -> userRepository.findAll(paginationAndSort));
        return parsePageToGridData(orderPages);
    }
}
