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
class ForumControllerUpdatePostTest {

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
	@DisplayName("UTCID01: NULL PostID")
	void UTCID01_NullPostId() {
		postDTO.setPostId(null);
		postDTO.setTitle("Cat");
		postDTO.setDescription("Cat For Fun");
		postDTO.setImageFiles(new ArrayList<>());
		postDTO.setVideoFiles(new ArrayList<>());

		doThrow(new IllegalArgumentException("PostID cannot be empty")).when(forumService).updatePost(any(PostDTO.class), anyLong());

		assertThrows(IllegalArgumentException.class, () -> {
			controller.updatePost(postDTO, session, "/update-post");
		});
		
		verify(forumService).updatePost(postDTO, user.getAccountId());
	}

	@Test
	@DisplayName("UTCID02: Empty PostID")
	void UTCID02_EmptyPostId() {
		postDTO.setPostId(null); // Empty as null
		postDTO.setTitle("Cat");
		postDTO.setDescription("Cat For Fun");
		postDTO.setImageFiles(new ArrayList<>());
		postDTO.setVideoFiles(new ArrayList<>());

		doThrow(new IllegalArgumentException("PostID cannot be empty")).when(forumService).updatePost(any(PostDTO.class), anyLong());

		assertThrows(IllegalArgumentException.class, () -> {
			controller.updatePost(postDTO, session, "/update-post");
		});
		
		verify(forumService).updatePost(postDTO, user.getAccountId());
	}

	@Test
	@DisplayName("UTCID03: Negative PostID")
	void UTCID03_NegativePostId() {
		postDTO.setPostId(-1L);
		postDTO.setTitle("Cat");
		postDTO.setDescription("Cat For Fun");
		postDTO.setImageFiles(new ArrayList<>());
		postDTO.setVideoFiles(new ArrayList<>());

		doThrow(new IllegalArgumentException("Invalid PostID")).when(forumService).updatePost(any(PostDTO.class), anyLong());

		assertThrows(IllegalArgumentException.class, () -> {
			controller.updatePost(postDTO, session, "/update-post");
		});
		
		verify(forumService).updatePost(postDTO, user.getAccountId());
	}

	@Test
	@DisplayName("UTCID04: Non-existent PostID")
	void UTCID04_NonExistentPostId() {
		postDTO.setPostId(1000L);
		postDTO.setTitle("Cat");
		postDTO.setDescription("Cat For Fun");
		postDTO.setImageFiles(new ArrayList<>());
		postDTO.setVideoFiles(new ArrayList<>());

		doThrow(new RuntimeException("PostID does not exist")).when(forumService).updatePost(any(PostDTO.class), anyLong());

		assertThrows(RuntimeException.class, () -> {
			controller.updatePost(postDTO, session, "/update-post");
		});
		
		verify(forumService).updatePost(postDTO, user.getAccountId());
	}

	@Test
	@DisplayName("UTCID05: Invalid PostID format")
	void UTCID05_InvalidPostIdFormat() {
		postDTO.setPostId(null); // Invalid format represented as null
		postDTO.setTitle("Cat");
		postDTO.setDescription("Cat For Fun");
		postDTO.setImageFiles(new ArrayList<>());
		postDTO.setVideoFiles(new ArrayList<>());

		doThrow(new IllegalArgumentException("Invalid PostID")).when(forumService).updatePost(any(PostDTO.class), anyLong());

		assertThrows(IllegalArgumentException.class, () -> {
			controller.updatePost(postDTO, session, "/update-post");
		});
		
		verify(forumService).updatePost(postDTO, user.getAccountId());
	}

	@Test
	@DisplayName("UTCID06: Valid PostID and data")
	void UTCID06_ValidData() {
		postDTO.setPostId(1L);
		postDTO.setTitle("Cat");
		postDTO.setDescription("Cat For Fun");
		postDTO.setImageFiles(new ArrayList<>());
		postDTO.setVideoFiles(new ArrayList<>());

		doNothing().when(forumService).updatePost(any(PostDTO.class), anyLong());

		String view = controller.updatePost(postDTO, session, "/update-post");

		assertEquals("redirect:/forum", view);
		verify(forumService).updatePost(postDTO, user.getAccountId());
	}

	@Test
	@DisplayName("UTCID07: NULL title")
	void UTCID07_NullTitle() {
		postDTO.setPostId(1L);
		postDTO.setTitle(null);
		postDTO.setDescription("Cat For Fun");
		postDTO.setImageFiles(new ArrayList<>());
		postDTO.setVideoFiles(new ArrayList<>());

		doThrow(new IllegalArgumentException("Title cannot be empty")).when(forumService).updatePost(any(PostDTO.class), anyLong());

		assertThrows(IllegalArgumentException.class, () -> {
			controller.updatePost(postDTO, session, "/update-post");
		});
		
		verify(forumService).updatePost(postDTO, user.getAccountId());
	}

	@Test
	@DisplayName("UTCID08: Not authenticated")
	void UTCID08_NotAuthenticated() {
		MockHttpSession noUserSession = new MockHttpSession();
		postDTO.setPostId(1L);
		postDTO.setTitle("Cat");
		postDTO.setDescription("Cat For Fun");
		postDTO.setImageFiles(new ArrayList<>());
		postDTO.setVideoFiles(new ArrayList<>());

		String view = controller.updatePost(postDTO, noUserSession, "/update-post");

		assertEquals("redirect:/login", view);
		verify(forumService, never()).updatePost(any(), anyLong());
	}

	@Test
	@DisplayName("UTCID09: NULL content")
	void UTCID09_NullContent() {
		postDTO.setPostId(1L);
		postDTO.setTitle("Cat");
		postDTO.setDescription(null);
		postDTO.setImageFiles(new ArrayList<>());
		postDTO.setVideoFiles(new ArrayList<>());

		doThrow(new IllegalArgumentException("Note cannot be empty")).when(forumService).updatePost(any(PostDTO.class), anyLong());

		assertThrows(IllegalArgumentException.class, () -> {
			controller.updatePost(postDTO, session, "/update-post");
		});
		
		verify(forumService).updatePost(postDTO, user.getAccountId());
	}

	@Test
	@DisplayName("UTCID10: Empty content")
	void UTCID10_EmptyContent() {
		postDTO.setPostId(1L);
		postDTO.setTitle("Cat");
		postDTO.setDescription("");
		postDTO.setImageFiles(new ArrayList<>());
		postDTO.setVideoFiles(new ArrayList<>());

		doThrow(new IllegalArgumentException("Note cannot be empty")).when(forumService).updatePost(any(PostDTO.class), anyLong());

		assertThrows(IllegalArgumentException.class, () -> {
			controller.updatePost(postDTO, session, "/update-post");
		});
		
		verify(forumService).updatePost(postDTO, user.getAccountId());
	}

	@Test
	@DisplayName("UTCID11: Service throws 401 exception")
	void UTCID11_ServiceThrows401() {
		postDTO.setPostId(1L);
		postDTO.setTitle("Cat");
		postDTO.setDescription("Cat For Fun");
		postDTO.setImageFiles(new ArrayList<>());
		postDTO.setVideoFiles(new ArrayList<>());

		doThrow(new RuntimeException("status 401")).when(forumService).updatePost(any(PostDTO.class), anyLong());

		assertThrows(RuntimeException.class, () -> {
			controller.updatePost(postDTO, session, "/update-post");
		});
		
		verify(forumService).updatePost(postDTO, user.getAccountId());
	}

	@Test
	@DisplayName("UTCID12: Service throws 500 exception")
	void UTCID12_ServiceThrows500() {
		postDTO.setPostId(1L);
		postDTO.setTitle("Cat");
		postDTO.setDescription("Cat For Fun");
		postDTO.setImageFiles(new ArrayList<>());
		postDTO.setVideoFiles(new ArrayList<>());

		doThrow(new RuntimeException("status 500")).when(forumService).updatePost(any(PostDTO.class), anyLong());

		assertThrows(RuntimeException.class, () -> {
			controller.updatePost(postDTO, session, "/update-post");
		});
		
		verify(forumService).updatePost(postDTO, user.getAccountId());
	}

	@Test
	@DisplayName("UTCID13: Redirect to my-posts when update-my-post")
	void UTCID13_RedirectToMyPosts() {
		postDTO.setPostId(1L);
		postDTO.setTitle("Cat");
		postDTO.setDescription("Cat For Fun");
		postDTO.setImageFiles(new ArrayList<>());
		postDTO.setVideoFiles(new ArrayList<>());

		doNothing().when(forumService).updatePost(any(PostDTO.class), anyLong());

		String view = controller.updatePost(postDTO, session, "/update-my-post/1");

		assertEquals("redirect:/forum-my-posts/1", view);
		verify(forumService).updatePost(postDTO, user.getAccountId());
	}
}
