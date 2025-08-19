package org.example.petcareplus.controller;

import org.example.petcareplus.dto.PostDTO;
import org.example.petcareplus.entity.Account;
import org.example.petcareplus.entity.Post;
import org.example.petcareplus.enums.AccountRole;
import org.example.petcareplus.enums.AccountStatus;
import org.example.petcareplus.service.ForumService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ForumControllerSavePostTest {

	@Mock
	private ForumService forumService;

	@InjectMocks
	private ForumController controller;

	private MockHttpSession session;
	private Account user;
	private PostDTO postDTO;

	@BeforeEach
	void setUp() {
		session = new MockHttpSession();
		postDTO = new PostDTO();

		user = new Account();
		user.setAccountId(1L);
		user.setName("Test User");
		user.setRole(AccountRole.CUSTOMER);
		user.setStatus(AccountStatus.ACTIVE);

		session.setAttribute("loggedInUser", user);
	}

	@Test
	@DisplayName("UTCID01: NULL title")
	void UTCID01_NullTitle() {
		postDTO.setTitle(null);
		postDTO.setDescription("Cat For Fun");
		postDTO.setImageFiles(new ArrayList<>());
		postDTO.setVideoFiles(new ArrayList<>());

		doNothing().when(forumService).savePost(any(PostDTO.class), anyLong());

		String view = controller.savePost(postDTO, session);

		assertEquals("redirect:/forum", view);
		verify(forumService).savePost(postDTO, user.getAccountId());
	}

	@Test
	@DisplayName("UTCID02: Empty title")
	void UTCID02_EmptyTitle() {
		postDTO.setTitle("");
		postDTO.setDescription("Cat For Fun");
		postDTO.setImageFiles(new ArrayList<>());
		postDTO.setVideoFiles(new ArrayList<>());

		doNothing().when(forumService).savePost(any(PostDTO.class), anyLong());

		String view = controller.savePost(postDTO, session);

		assertEquals("redirect:/forum", view);
		verify(forumService).savePost(postDTO, user.getAccountId());
	}

	@Test
	@DisplayName("UTCID03: NULL content")
	void UTCID03_NullContent() {
		postDTO.setTitle("Cat");
		postDTO.setDescription(null);
		postDTO.setImageFiles(new ArrayList<>());
		postDTO.setVideoFiles(new ArrayList<>());

		doNothing().when(forumService).savePost(any(PostDTO.class), anyLong());

		String view = controller.savePost(postDTO, session);

		assertEquals("redirect:/forum", view);
		verify(forumService).savePost(postDTO, user.getAccountId());
	}

	@Test
	@DisplayName("UTCID04: Valid title and content")
	void UTCID04_ValidData() {
		postDTO.setTitle("Cat");
		postDTO.setDescription("Cat For Fun");
		postDTO.setImageFiles(new ArrayList<>());
		postDTO.setVideoFiles(new ArrayList<>());

		doNothing().when(forumService).savePost(any(PostDTO.class), anyLong());

		String view = controller.savePost(postDTO, session);

		assertEquals("redirect:/forum", view);
		verify(forumService).savePost(postDTO, user.getAccountId());
	}

	@Test
	@DisplayName("UTCID05: Exception when savePost")
	void UTCID05_ExceptionOnSave() {
		postDTO.setTitle("Cat");
		postDTO.setDescription("Cat For Fun");
		postDTO.setImageFiles(new ArrayList<>());
		postDTO.setVideoFiles(new ArrayList<>());

		doThrow(new RuntimeException("Database error")).when(forumService).savePost(any(PostDTO.class), anyLong());

		assertThrows(RuntimeException.class, () -> {
			controller.savePost(postDTO, session);
		});
		
		verify(forumService).savePost(postDTO, user.getAccountId());
	}

	@Test
	@DisplayName("UTCID06: Service throws IllegalArgumentException")
	void UTCID06_IllegalArgumentException() {
		postDTO.setTitle("Cat");
		postDTO.setDescription("Cat For Fun");
		postDTO.setImageFiles(new ArrayList<>());
		postDTO.setVideoFiles(new ArrayList<>());

		doThrow(new IllegalArgumentException("Invalid data")).when(forumService).savePost(any(PostDTO.class), anyLong());

		assertThrows(IllegalArgumentException.class, () -> {
			controller.savePost(postDTO, session);
		});
		
		verify(forumService).savePost(postDTO, user.getAccountId());
	}

	@Test
	@DisplayName("UTCID07: Service throws RuntimeException")
	void UTCID07_RuntimeException() {
		postDTO.setTitle("Cat");
		postDTO.setDescription("Cat For Fun");
		postDTO.setImageFiles(new ArrayList<>());
		postDTO.setVideoFiles(new ArrayList<>());

		doThrow(new RuntimeException("Service error")).when(forumService).savePost(any(PostDTO.class), anyLong());

		assertThrows(RuntimeException.class, () -> {
			controller.savePost(postDTO, session);
		});
		
		verify(forumService).savePost(postDTO, user.getAccountId());
	}

	@Test
	@DisplayName("UTCID08: Not authenticated (no session user)")
	void UTCID08_NotAuthenticated() {
		MockHttpSession noUserSession = new MockHttpSession();
		postDTO.setTitle("Cat");
		postDTO.setDescription("Cat For Fun");
		postDTO.setImageFiles(new ArrayList<>());
		postDTO.setVideoFiles(new ArrayList<>());

		assertThrows(NullPointerException.class, () -> {
			controller.savePost(postDTO, noUserSession);
		});
		
		verify(forumService, never()).savePost(any(), anyLong());
	}
}
