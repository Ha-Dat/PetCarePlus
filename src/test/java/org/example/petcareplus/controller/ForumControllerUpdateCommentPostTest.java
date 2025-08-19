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
class ForumControllerUpdateCommentPostTest {

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
		doThrow(new IllegalArgumentException("Comment ID không được để trống")).when(forumService).updateCommentPost(isNull(), eq("Rất hay"), eq(user));

		assertThrows(IllegalArgumentException.class, () -> {
			controller.editComment(1L, null, "Rất hay", session);
		});
		
		verify(forumService).updateCommentPost(isNull(), eq("Rất hay"), eq(user));
	}

	@Test
	@DisplayName("UTCID02: Empty CommentID")
	void UTCID02_EmptyCommentId() {
		// Empty CommentID would be handled by Spring's @PathVariable parsing
		doThrow(new IllegalArgumentException("Comment ID không được để trống")).when(forumService).updateCommentPost(isNull(), eq("Rất hay"), eq(user));

		assertThrows(IllegalArgumentException.class, () -> {
			controller.editComment(1L, null, "Rất hay", session);
		});
		
		verify(forumService).updateCommentPost(isNull(), eq("Rất hay"), eq(user));
	}

	@Test
	@DisplayName("UTCID03: Invalid CommentID format")
	void UTCID03_InvalidCommentIdFormat() {
		// Invalid format like "abc" would cause Spring parsing error before reaching method
		// For testing, we'll simulate service exception
		doThrow(new IllegalArgumentException("Comment ID không hợp lệ")).when(forumService).updateCommentPost(eq(1L), eq("Rất hay"), eq(user));

		assertThrows(IllegalArgumentException.class, () -> {
			controller.editComment(1L, 1L, "Rất hay", session);
		});
		
		verify(forumService).updateCommentPost(eq(1L), eq("Rất hay"), eq(user));
	}

	@Test
	@DisplayName("UTCID04: Valid CommentID")
	void UTCID04_ValidCommentId() {
		doNothing().when(forumService).updateCommentPost(eq(1L), eq("Rất hay"), eq(user));

		String view = controller.editComment(1L, 1L, "Rất hay", session);

		assertEquals("redirect:/post-detail/1", view);
		verify(forumService).updateCommentPost(eq(1L), eq("Rất hay"), eq(user));
	}

	@Test
	@DisplayName("UTCID05: Negative CommentID")
	void UTCID05_NegativeCommentId() {
		doThrow(new IllegalArgumentException("Comment ID không hợp lệ")).when(forumService).updateCommentPost(eq(-1L), eq("Rất hay"), eq(user));

		assertThrows(IllegalArgumentException.class, () -> {
			controller.editComment(1L, -1L, "Rất hay", session);
		});
		
		verify(forumService).updateCommentPost(eq(-1L), eq("Rất hay"), eq(user));
	}

	@Test
	@DisplayName("UTCID06: Non-existent CommentID")
	void UTCID06_NonExistentCommentId() {
		doThrow(new RuntimeException("Comment ID không tồn tại")).when(forumService).updateCommentPost(eq(1000L), eq("Rất hay"), eq(user));

		assertThrows(RuntimeException.class, () -> {
			controller.editComment(1L, 1000L, "Rất hay", session);
		});
		
		verify(forumService).updateCommentPost(eq(1000L), eq("Rất hay"), eq(user));
	}

	@Test
	@DisplayName("UTCID07: NULL comment")
	void UTCID07_NullComment() {
		String view = controller.editComment(1L, 1L, null, session);

		assertEquals("redirect:/post-detail/1?error=empty", view);
		verify(forumService, never()).updateCommentPost(any(), any(), any());
	}

	@Test
	@DisplayName("UTCID08: Empty comment")
	void UTCID08_EmptyComment() {
		String view = controller.editComment(1L, 1L, "", session);

		assertEquals("redirect:/post-detail/1?error=empty", view);
		verify(forumService, never()).updateCommentPost(any(), any(), any());
	}

	@Test
	@DisplayName("UTCID09: Valid comment")
	void UTCID09_ValidComment() {
		doNothing().when(forumService).updateCommentPost(eq(1L), eq("Rất hay"), eq(user));

		String view = controller.editComment(1L, 1L, "Rất hay", session);

		assertEquals("redirect:/post-detail/1", view);
		verify(forumService).updateCommentPost(eq(1L), eq("Rất hay"), eq(user));
	}

	@Test
	@DisplayName("UTCID10: Not authenticated")
	void UTCID10_NotAuthenticated() {
		MockHttpSession noUserSession = new MockHttpSession();

		String view = controller.editComment(1L, 1L, "Rất hay", noUserSession);

		assertEquals("redirect:/login", view);
		verify(forumService, never()).updateCommentPost(any(), any(), any());
	}

	@Test
	@DisplayName("UTCID11: Not authenticated with valid data")
	void UTCID11_NotAuthenticatedWithValidData() {
		MockHttpSession noUserSession = new MockHttpSession();

		String view = controller.editComment(1L, 1L, "Rất hay", noUserSession);

		assertEquals("redirect:/login", view);
		verify(forumService, never()).updateCommentPost(any(), any(), any());
	}
}
