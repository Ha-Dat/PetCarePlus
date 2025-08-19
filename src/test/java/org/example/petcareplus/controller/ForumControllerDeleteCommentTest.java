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
class ForumControllerDeleteCommentTest {

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
	@DisplayName("UTCID01: NULL CommentID")
	void UTCID01_NullCommentId() {
		// Since @PathVariable Long commentId, null would cause Spring to handle it before reaching method
		// For testing purposes, we'll simulate this scenario
		doThrow(new IllegalArgumentException("Comment ID không được để trống")).when(forumService).deleteCommentPostById(isNull());

		assertThrows(IllegalArgumentException.class, () -> {
			controller.deleteComment(1L, null, session);
		});
		
		verify(forumService).deleteCommentPostById(isNull());
	}

	@Test
	@DisplayName("UTCID02: Empty CommentID")
	void UTCID02_EmptyCommentId() {
		// Empty CommentID would be handled by Spring's @PathVariable parsing
		doThrow(new IllegalArgumentException("Comment ID không được để trống")).when(forumService).deleteCommentPostById(isNull());

		assertThrows(IllegalArgumentException.class, () -> {
			controller.deleteComment(1L, null, session);
		});
		
		verify(forumService).deleteCommentPostById(isNull());
	}

	@Test
	@DisplayName("UTCID03: Invalid CommentID format")
	void UTCID03_InvalidCommentIdFormat() {
		// Invalid format like "abc" would cause Spring parsing error before reaching method
		// For testing, we'll simulate service exception
		doThrow(new IllegalArgumentException("CommentID không hợp lệ")).when(forumService).deleteCommentPostById(eq(1L));

		assertThrows(IllegalArgumentException.class, () -> {
			controller.deleteComment(1L, 1L, session);
		});
		
		verify(forumService).deleteCommentPostById(eq(1L));
	}

	@Test
	@DisplayName("UTCID04: Valid CommentID")
	void UTCID04_ValidCommentId() {
		doNothing().when(forumService).deleteCommentPostById(eq(1L));

		String view = controller.deleteComment(1L, 1L, session);

		assertEquals("redirect:/post-detail/1", view);
		verify(forumService).deleteCommentPostById(eq(1L));
	}

	@Test
	@DisplayName("UTCID05: Negative CommentID")
	void UTCID05_NegativeCommentId() {
		doThrow(new IllegalArgumentException("CommentID không hợp lệ")).when(forumService).deleteCommentPostById(eq(-1L));

		assertThrows(IllegalArgumentException.class, () -> {
			controller.deleteComment(1L, -1L, session);
		});
		
		verify(forumService).deleteCommentPostById(eq(-1L));
	}

	@Test
	@DisplayName("UTCID06: Non-existent CommentID")
	void UTCID06_NonExistentCommentId() {
		doThrow(new RuntimeException("Comment ID không tồn tại")).when(forumService).deleteCommentPostById(eq(1000L));

		assertThrows(RuntimeException.class, () -> {
			controller.deleteComment(1L, 1000L, session);
		});
		
		verify(forumService).deleteCommentPostById(eq(1000L));
	}

	@Test
	@DisplayName("UTCID07: Service throws 401 exception")
	void UTCID07_ServiceThrows401() {
		doThrow(new RuntimeException("status 401")).when(forumService).deleteCommentPostById(eq(1L));

		assertThrows(RuntimeException.class, () -> {
			controller.deleteComment(1L, 1L, session);
		});
		
		verify(forumService).deleteCommentPostById(eq(1L));
	}

	@Test
	@DisplayName("UTCID08: Service throws 500 exception")
	void UTCID08_ServiceThrows500() {
		doThrow(new RuntimeException("status 500")).when(forumService).deleteCommentPostById(eq(1L));

		assertThrows(RuntimeException.class, () -> {
			controller.deleteComment(1L, 1L, session);
		});
		
		verify(forumService).deleteCommentPostById(eq(1L));
	}

	@Test
	@DisplayName("UTCID09: Not authenticated")
	void UTCID09_NotAuthenticated() {
		MockHttpSession noUserSession = new MockHttpSession();

		String view = controller.deleteComment(1L, 1L, noUserSession);

		assertEquals("redirect:/login", view);
		verify(forumService, never()).deleteCommentPostById(any());
	}
}
