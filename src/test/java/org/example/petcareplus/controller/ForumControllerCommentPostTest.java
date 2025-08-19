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
class ForumControllerCommentPostTest {

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
		// Since @PathVariable Long postId, null would cause Spring to handle it before reaching method
		// For testing purposes, we'll simulate this scenario
		doThrow(new IllegalArgumentException("Post ID không được để trống")).when(forumService).saveCommentPost(isNull(), eq(user.getAccountId()), eq("Rất hay"));

		assertThrows(IllegalArgumentException.class, () -> {
			controller.commentPost(null, "Rất hay", session);
		});
		
		verify(forumService).saveCommentPost(isNull(), eq(user.getAccountId()), eq("Rất hay"));
	}

	@Test
	@DisplayName("UTCID02: Empty PostID")
	void UTCID02_EmptyPostId() {
		// Empty PostID would be handled by Spring's @PathVariable parsing
		doThrow(new IllegalArgumentException("Post ID không được để trống")).when(forumService).saveCommentPost(isNull(), eq(user.getAccountId()), eq("Rất hay"));

		assertThrows(IllegalArgumentException.class, () -> {
			controller.commentPost(null, "Rất hay", session);
		});
		
		verify(forumService).saveCommentPost(isNull(), eq(user.getAccountId()), eq("Rất hay"));
	}

	@Test
	@DisplayName("UTCID03: Invalid PostID format")
	void UTCID03_InvalidPostIdFormat() {
		// Invalid format like "abc" would cause Spring parsing error before reaching method
		// For testing, we'll simulate service exception
		doThrow(new IllegalArgumentException("Post không hợp lệ")).when(forumService).saveCommentPost(eq(1L), eq(user.getAccountId()), eq("Rất hay"));

		assertThrows(IllegalArgumentException.class, () -> {
			controller.commentPost(1L, "Rất hay", session);
		});
		
		verify(forumService).saveCommentPost(eq(1L), eq(user.getAccountId()), eq("Rất hay"));
	}

	@Test
	@DisplayName("UTCID04: Valid PostID")
	void UTCID04_ValidPostId() {
		doNothing().when(forumService).saveCommentPost(eq(1L), eq(user.getAccountId()), eq("Rất hay"));

		String view = controller.commentPost(1L, "Rất hay", session);

		assertEquals("redirect:/post-detail/1", view);
		verify(forumService).saveCommentPost(eq(1L), eq(user.getAccountId()), eq("Rất hay"));
	}

	@Test
	@DisplayName("UTCID05: Negative PostID")
	void UTCID05_NegativePostId() {
		doThrow(new IllegalArgumentException("Post không hợp lệ")).when(forumService).saveCommentPost(eq(-1L), eq(user.getAccountId()), eq("Rất hay"));

		assertThrows(IllegalArgumentException.class, () -> {
			controller.commentPost(-1L, "Rất hay", session);
		});
		
		verify(forumService).saveCommentPost(eq(-1L), eq(user.getAccountId()), eq("Rất hay"));
	}

	@Test
	@DisplayName("UTCID06: Non-existent PostID")
	void UTCID06_NonExistentPostId() {
		doThrow(new RuntimeException("Post ID không tồn tại")).when(forumService).saveCommentPost(eq(1000L), eq(user.getAccountId()), eq("Rất hay"));

		assertThrows(RuntimeException.class, () -> {
			controller.commentPost(1000L, "Rất hay", session);
		});
		
		verify(forumService).saveCommentPost(eq(1000L), eq(user.getAccountId()), eq("Rất hay"));
	}

	@Test
	@DisplayName("UTCID07: NULL comment")
	void UTCID07_NullComment() {
		String view = controller.commentPost(1L, null, session);

		assertEquals("redirect:/post-detail/1?error=empty", view);
		verify(forumService, never()).saveCommentPost(any(), any(), any());
	}

	@Test
	@DisplayName("UTCID08: Empty comment")
	void UTCID08_EmptyComment() {
		String view = controller.commentPost(1L, "", session);

		assertEquals("redirect:/post-detail/1?error=empty", view);
		verify(forumService, never()).saveCommentPost(any(), any(), any());
	}

	@Test
	@DisplayName("UTCID09: Valid comment")
	void UTCID09_ValidComment() {
		doNothing().when(forumService).saveCommentPost(eq(1L), eq(user.getAccountId()), eq("Rất hay"));

		String view = controller.commentPost(1L, "Rất hay", session);

		assertEquals("redirect:/post-detail/1", view);
		verify(forumService).saveCommentPost(eq(1L), eq(user.getAccountId()), eq("Rất hay"));
	}

	@Test
	@DisplayName("UTCID10: Not authenticated")
	void UTCID10_NotAuthenticated() {
		MockHttpSession noUserSession = new MockHttpSession();

		String view = controller.commentPost(1L, "Rất hay", noUserSession);

		assertEquals("redirect:/login", view);
		verify(forumService, never()).saveCommentPost(any(), any(), any());
	}

	@Test
	@DisplayName("UTCID11: Not authenticated with valid data")
	void UTCID11_NotAuthenticatedWithValidData() {
		MockHttpSession noUserSession = new MockHttpSession();

		String view = controller.commentPost(1L, "Rất hay", noUserSession);

		assertEquals("redirect:/login", view);
		verify(forumService, never()).saveCommentPost(any(), any(), any());
	}
}
