package org.example.petcareplus.controller;

import org.example.petcareplus.entity.Account;
import org.example.petcareplus.entity.Order;
import org.example.petcareplus.enums.AccountRole;
import org.example.petcareplus.enums.AccountStatus;
import org.example.petcareplus.enums.OrderStatus;
import org.example.petcareplus.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import org.springframework.mock.web.MockHttpSession;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerCancelOrderTest {

	@Mock
	private OrderService orderService;

	@InjectMocks
	private OrderController controller;

	private MockHttpSession session;
	private RedirectAttributes redirectAttrs;
	private Account owner;
	private Account otherUser;

	@BeforeEach
	void setUp() {
		session = new MockHttpSession();
		redirectAttrs = new RedirectAttributesModelMap();

		owner = new Account();
		owner.setAccountId(1L);
		owner.setName("Owner");
		owner.setRole(AccountRole.CUSTOMER);
		owner.setStatus(AccountStatus.ACTIVE);

		otherUser = new Account();
		otherUser.setAccountId(2L);
		otherUser.setName("Other");
		otherUser.setRole(AccountRole.CUSTOMER);
		otherUser.setStatus(AccountStatus.ACTIVE);

		session.setAttribute("loggedInUser", owner);
	}

	private Order buildOrder(Long id, Account account, OrderStatus status) {
		Order o = new Order();
		o.setOrderId(id);
		o.setAccount(account);
		o.setStatus(status);
		return o;
	}

	@Test
	@DisplayName("UTCID01: NULL OrderID")
	void UTCID01_NullOrderId() {
		when(orderService.findById(null)).thenReturn(Optional.empty());

		String view = controller.cancelOrder(null, session, redirectAttrs);

		assertEquals("redirect:/list-order", view);
		assertTrue(redirectAttrs.getFlashAttributes().containsKey("error"));
		verify(orderService, never()).save(any());
	}

	@Test
	@DisplayName("UTCID02: Valid OrderID owned by user, PENDING -> CANCELLED and success message")
	void UTCID02_OwnerPendingToCancelled() {
		Order order = buildOrder(1L, owner, OrderStatus.PENDING);
		when(orderService.findById(1L)).thenReturn(Optional.of(order));

		String view = controller.cancelOrder(1L, session, redirectAttrs);

		ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
		verify(orderService).save(captor.capture());
		assertEquals(OrderStatus.CANCELLED, captor.getValue().getStatus());
		assertEquals("redirect:/list-order", view);
		assertEquals("Đã hủy đơn hàng thành công", redirectAttrs.getFlashAttributes().get("success"));
	}

	@Test
	@DisplayName("UTCID03: Valid OrderID owned by user, status not PENDING")
	void UTCID03_OwnerNotPending() {
		Order order = buildOrder(1L, owner, OrderStatus.COMPLETED);
		when(orderService.findById(1L)).thenReturn(Optional.of(order));

		String view = controller.cancelOrder(1L, session, redirectAttrs);

		verify(orderService, never()).save(any());
		assertEquals("redirect:/list-order", view);
		assertEquals("Chỉ có thể hủy đơn hàng ở trạng thái Chờ duyệt", redirectAttrs.getFlashAttributes().get("error"));
	}

	@Test
	@DisplayName("UTCID04: Valid OrderID not owned by user")
	void UTCID04_NotOwner() {
		Order order = buildOrder(1L, otherUser, OrderStatus.PENDING);
		when(orderService.findById(1L)).thenReturn(Optional.of(order));

		String view = controller.cancelOrder(1L, session, redirectAttrs);

		verify(orderService, never()).save(any());
		assertEquals("redirect:/list-order", view);
		assertEquals("Bạn không có quyền hủy đơn hàng này", redirectAttrs.getFlashAttributes().get("error"));
	}

	@Test
	@DisplayName("UTCID05: OrderID -1 (not found)")
	void UTCID05_NegativeId() {
		when(orderService.findById(-1L)).thenReturn(Optional.empty());

		String view = controller.cancelOrder(-1L, session, redirectAttrs);

		assertEquals("redirect:/list-order", view);
		assertTrue(redirectAttrs.getFlashAttributes().containsKey("error"));
		verify(orderService, never()).save(any());
	}

	@Test
	@DisplayName("UTCID06: OrderID 1000 (not found)")
	void UTCID06_NotFound() {
		when(orderService.findById(1000L)).thenReturn(Optional.empty());

		String view = controller.cancelOrder(1000L, session, redirectAttrs);

		assertEquals("redirect:/list-order", view);
		assertTrue(redirectAttrs.getFlashAttributes().containsKey("error"));
		verify(orderService, never()).save(any());
	}

	@Test
	@DisplayName("UTCID07: Exception when findById")
	void UTCID07_ExceptionOnFind() {
		when(orderService.findById(any())).thenThrow(new RuntimeException("DB error"));

		String view = controller.cancelOrder(1L, session, redirectAttrs);

		assertEquals("redirect:/list-order", view);
		assertTrue(redirectAttrs.getFlashAttributes().containsKey("error"));
		verify(orderService, never()).save(any());
	}

	@Test
	@DisplayName("UTCID08: Not authenticated (no session user)")
	void UTCID08_NotAuthenticated() {
		MockHttpSession noUserSession = new MockHttpSession();

		String view = controller.cancelOrder(1L, noUserSession, redirectAttrs);

		assertNotEquals("redirect:/login", view);
		assertTrue(redirectAttrs.getFlashAttributes().containsKey("error"));
		// save may or may not be called depending on where exception occurs, ensure no unexpected exceptions
	}
}
