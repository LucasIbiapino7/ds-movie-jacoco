package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.projections.UserDetailsProjection;
import com.devsuperior.dsmovie.repositories.UserRepository;
import com.devsuperior.dsmovie.tests.UserDetailsFactory;
import com.devsuperior.dsmovie.tests.UserFactory;
import com.devsuperior.dsmovie.utils.CustomUserUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
public class UserServiceTests {

	@InjectMocks
	private UserService service;

	@Mock
	private UserRepository repository;

	@Mock
	private CustomUserUtil userUtil;

	private String existsUsername, nonExistsUsername;
	private UserEntity user;
	private List<UserDetailsProjection> list;
	private List<UserDetailsProjection> listEmpty;

	@BeforeEach
	void setUp() {

		existsUsername = "maria@gmail.com";
		nonExistsUsername = "alex@gmail.com";
		list = UserDetailsFactory.createCustomClientUser(existsUsername);
		listEmpty = new ArrayList<>();

		user = UserFactory.createUserEntity();

		Mockito.when(repository.findByUsername(existsUsername)).thenReturn(Optional.of(user));
		Mockito.when(repository.findByUsername(nonExistsUsername)).thenReturn(Optional.empty());

		Mockito.when(repository.searchUserAndRolesByUsername(existsUsername)).thenReturn(list);
		Mockito.when(repository.searchUserAndRolesByUsername(nonExistsUsername)).thenReturn(listEmpty);

	}

	@Test
	public void authenticatedShouldReturnUserEntityWhenUserExists() {

		Mockito.when(userUtil.getLoggedUsername()).thenReturn(existsUsername);

		UserEntity result = service.authenticated();

		Assertions.assertNotNull(result);

	}

	@Test
	public void authenticatedShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() {

		Mockito.doThrow(ClassCastException.class).when(userUtil).getLoggedUsername();

		Assertions.assertThrows(UsernameNotFoundException.class, () -> {
			UserEntity result = service.authenticated();
		});

	}

	@Test
	public void loadUserByUsernameShouldReturnUserDetailsWhenUserExists() {

		UserDetails result = service.loadUserByUsername(existsUsername);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getUsername(), existsUsername);

	}

	@Test
	public void loadUserByUsernameShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() {

		Assertions.assertThrows(UsernameNotFoundException.class, () -> {
			UserDetails result = service.loadUserByUsername(nonExistsUsername);
		});

	}
}
