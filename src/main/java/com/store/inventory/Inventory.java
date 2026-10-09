package com.store.inventory;

import com.store.inventory.api.InventoryService;
import com.store.inventory.api.StockAlertListener;
import com.store.inventory.api.impl.InventoryServiceImpl;
import com.store.inventory.repository.ProductCategoryRepository;
import com.store.inventory.repository.ProductStateRepository;
import com.store.inventory.repository.ReservationStateRepository;
import com.store.inventory.repository.impl.ProductCategoryRepositoryImpl;
import com.store.inventory.repository.impl.ProductStateRepositoryImpl;
import com.store.inventory.repository.impl.ReservationStateRepositoryImpl;
import com.store.inventory.services.AlertService;
import com.store.inventory.services.AvailabilityService;
import com.store.inventory.services.ProductService;
import com.store.inventory.services.ReservationService;
import com.store.inventory.services.impl.AlertServiceImpl;
import com.store.inventory.services.impl.AvailabilityServiceImpl;
import com.store.inventory.services.impl.ProductServiceImpl;
import com.store.inventory.services.impl.ReservationServiceImpl;

import java.time.Clock;

/**
 * Entry point used by our automated tests. Keep this signature exactly as it is,
 * and build your implementation here.
 */
public final class Inventory {

  private Inventory() {
  }

  public static InventoryService create(Clock clock, StockAlertListener alertListener) {

    ReservationStateRepository reservationStateRepository = new ReservationStateRepositoryImpl();
    AvailabilityService availabilityService = new AvailabilityServiceImpl(clock, reservationStateRepository);
    AlertService alertService = new AlertServiceImpl(alertListener, availabilityService);
    ProductStateRepository productStateRepository = new ProductStateRepositoryImpl();
    ProductCategoryRepository productCategoryRepository = new ProductCategoryRepositoryImpl();
    ProductService productService = new ProductServiceImpl(productStateRepository, alertService, availabilityService);
    ReservationService reservationService = new ReservationServiceImpl(clock,
        productStateRepository, productCategoryRepository, reservationStateRepository, alertService, availabilityService);

    return new InventoryServiceImpl(productService, reservationService);
  }
}
