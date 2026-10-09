package com.store.inventory.models;

import com.store.inventory.api.Reservation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ReservationState {
    private Reservation reservation;
    private boolean confirmed;
}
