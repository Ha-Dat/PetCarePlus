package org.example.petcareplus.controller;

import org.example.petcareplus.entity.Product;
import org.example.petcareplus.enums.ProductStatus;
import org.example.petcareplus.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductDashboardControllerToggleProductStatusTest {

	@Mock
	private ProductService productService;

	@InjectMocks
	private ProductDashboardController controller;

	private Product buildProduct(Long id, ProductStatus status, int unitInStock) {
		Product p = new Product();
		p.setProductId(id);
		p.setStatus(status);
		p.setUnitInStock(unitInStock);
		return p;
	}

	@Test
	@DisplayName("UTCID01: ProductID NULL -> redirect to product-dashboard")
	void UTCID01_NullId() {
		when(productService.findById(null)).thenReturn(Optional.empty());

		String result = controller.switchStatusProduct(null);

		assertEquals("redirect:/product-dashboard", result);
		verify(productService, never()).save(any());
	}

	@Test
	@DisplayName("UTCID02: ProductID=1, status IN_STOCK -> set INACTIVE and redirect seller dashboard")
	void UTCID02_InStockToInactive() {
		Product product = buildProduct(1L, ProductStatus.IN_STOCK, 10);
		when(productService.findById(1L)).thenReturn(Optional.of(product));

		String result = controller.switchStatusProduct(1L);

		ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
		verify(productService).save(captor.capture());
		assertEquals(ProductStatus.INACTIVE, captor.getValue().getStatus());
		assertEquals("redirect:/seller/product-dashboard", result);
	}

	@Test
	@DisplayName("UTCID03: ProductID=1, status INACTIVE & unit=0 -> set OUT_OF_STOCK")
	void UTCID03_InactiveToOutOfStock() {
		Product product = buildProduct(1L, ProductStatus.INACTIVE, 0);
		when(productService.findById(1L)).thenReturn(Optional.of(product));

		String result = controller.switchStatusProduct(1L);

		ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
		verify(productService).save(captor.capture());
		assertEquals(ProductStatus.OUT_OF_STOCK, captor.getValue().getStatus());
		assertEquals("redirect:/seller/product-dashboard", result);
	}

	@Test
	@DisplayName("UTCID04: ProductID=1, status INACTIVE & unit>0 -> set IN_STOCK")
	void UTCID04_InactiveToInStock() {
		Product product = buildProduct(1L, ProductStatus.INACTIVE, 5);
		when(productService.findById(1L)).thenReturn(Optional.of(product));

		String result = controller.switchStatusProduct(1L);

		ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
		verify(productService).save(captor.capture());
		assertEquals(ProductStatus.IN_STOCK, captor.getValue().getStatus());
		assertEquals("redirect:/seller/product-dashboard", result);
	}

	@Test
	@DisplayName("UTCID05: ProductID=-1 (not found) -> redirect to product-dashboard")
	void UTCID05_NegativeIdNotFound() {
		when(productService.findById(-1L)).thenReturn(Optional.empty());

		String result = controller.switchStatusProduct(-1L);

		assertEquals("redirect:/product-dashboard", result);
		verify(productService, never()).save(any());
	}

	@Test
	@DisplayName("UTCID06: ProductID=1000 (not found) -> redirect to product-dashboard")
	void UTCID06_NotFoundId() {
		when(productService.findById(1000L)).thenReturn(Optional.empty());

		String result = controller.switchStatusProduct(1000L);

		assertEquals("redirect:/product-dashboard", result);
		verify(productService, never()).save(any());
	}

	@Test
	@DisplayName("UTCID07: ProductID=1, status OUT_OF_STOCK -> set INACTIVE")
	void UTCID07_OutOfStockToInactive() {
		Product product = buildProduct(1L, ProductStatus.OUT_OF_STOCK, 0);
		when(productService.findById(1L)).thenReturn(Optional.of(product));

		String result = controller.switchStatusProduct(1L);

		ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
		verify(productService).save(captor.capture());
		assertEquals(ProductStatus.INACTIVE, captor.getValue().getStatus());
		assertEquals("redirect:/seller/product-dashboard", result);
	}

	@Test
	@DisplayName("UTCID08: Exception when findById -> redirect with error")
	void UTCID08_ExceptionOnFind() {
		when(productService.findById(anyLong())).thenThrow(new RuntimeException("DB error"));

		String result = controller.switchStatusProduct(1L);

		assertEquals("redirect:/product-dashboard?error", result);
		verify(productService, never()).save(any());
	}

	@Test
	@DisplayName("UTCID09: Exception when save -> redirect with error")
	void UTCID09_ExceptionOnSave() {
		Product product = buildProduct(1L, ProductStatus.IN_STOCK, 3);
		when(productService.findById(1L)).thenReturn(Optional.of(product));
		doThrow(new RuntimeException("DB error on save")).when(productService).save(any(Product.class));

		String result = controller.switchStatusProduct(1L);

		assertEquals("redirect:/product-dashboard?error", result);
	}
}
