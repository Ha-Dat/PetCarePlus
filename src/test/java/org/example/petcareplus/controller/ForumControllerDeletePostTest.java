package org.example.petcareplus.controller;

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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ForumControllerDeletePostTest {

	@Mock
	private ForumService forumService;

	@InjectMocks
	private ForumController controller;

	private MockHttpSession session;
	private Account user;

	@BeforeEach
	void setUp() {
		session = new MockHttpSession();

		user = new Account();
		user.setAccountId(1L);
		user.setName("Test User");
		user.setRole(AccountRole.CUSTOMER);
		user.setStatus(AccountStatus.ACTIVE);

		session.setAttribute("loggedInUser", user);
	}

	@Test
	@DisplayName("UTCID01: NULL PostID")
	void UTCID01_NullPostId() {
		doThrow(new IllegalArgumentException("Post ID không được để trống")).when(forumService).deletePostById(null);

		assertThrows(IllegalArgumentException.class, () -> {
			controller.deletePost(null, session);
		});
		
		verify(forumService).deletePostById(null);
	}

	@Test
	@DisplayName("UTCID02: Empty PostID")
	void UTCID02_EmptyPostId() {
		// Empty PostID would be represented as null or invalid input
		doThrow(new IllegalArgumentException("Post ID không được để trống")).when(forumService).deletePostById(null);

		assertThrows(IllegalArgumentException.class, () -> {
			controller.deletePost(null, session);
		});
		
		verify(forumService).deletePostById(null);
	}

	@Test
	@DisplayName("UTCID03: Invalid PostID format")
	void UTCID03_InvalidPostIdFormat() {
		// Invalid format would cause parsing error, but since it's @PathVariable Long, 
		// Spring would handle this before reaching the method
		// For testing purposes, we'll simulate this with a service exception
		doThrow(new IllegalArgumentException("Post không hợp lệ")).when(forumService).deletePostById(anyLong());

		assertThrows(IllegalArgumentException.class, () -> {
			controller.deletePost(1L, session);
		});
		
		verify(forumService).deletePostById(1L);
	}

	@Test
	@DisplayName("UTCID04: Valid PostID")
	void UTCID04_ValidPostId() {
		doNothing().when(forumService).deletePostById(1L);

		String view = controller.deletePost(1L, session);

		assertEquals("redirect:/forum", view);
		verify(forumService).deletePostById(1L);
	}

	@Test
	@DisplayName("UTCID05: Negative PostID")
	void UTCID05_NegativePostId() {
		doThrow(new IllegalArgumentException("Post không hợp lệ")).when(forumService).deletePostById(-1L);

		assertThrows(IllegalArgumentException.class, () -> {
			controller.deletePost(-1L, session);
		});
		
		verify(forumService).deletePostById(-1L);
	}

	@Test
	@DisplayName("UTCID06: Non-existent PostID")
	void UTCID06_NonExistentPostId() {
		doThrow(new RuntimeException("Post ID không tồn tại")).when(forumService).deletePostById(1000L);

		assertThrows(RuntimeException.class, () -> {
			controller.deletePost(1000L, session);
		});
		
		verify(forumService).deletePostById(1000L);
	}

	@Test
	@DisplayName("UTCID07: Service throws 500 exception")
	void UTCID07_ServiceThrows500() {
		doThrow(new RuntimeException("status 500")).when(forumService).deletePostById(1L);

		assertThrows(RuntimeException.class, () -> {
			controller.deletePost(1L, session);
		});
		
		verify(forumService).deletePostById(1L);
	}

	@Test
	@DisplayName("UTCID08: Service throws 500 exception")
	void UTCID08_ServiceThrows500Again() {
		doThrow(new RuntimeException("status 500")).when(forumService).deletePostById(1L);

		assertThrows(RuntimeException.class, () -> {
			controller.deletePost(1L, session);
		});
		
		verify(forumService).deletePostById(1L);
	}

	@Test
	@DisplayName("UTCID09: Not authenticated")
	void UTCID09_NotAuthenticated() {
		MockHttpSession noUserSession = new MockHttpSession();

		String view = controller.deletePost(1L, noUserSession);

		assertEquals("redirect:/login", view);
		verify(forumService, never()).deletePostById(anyLong());
	}
}
