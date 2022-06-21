package org.payconiq.helpers;

import org.payconiq.models.Booking;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookingAssertions {
    public static void assetEquals(Booking expected, Booking actual) {
        assertAll("Compare Bookings",
                () -> assertEquals(expected.getFirstname(), actual.getFirstname()),
                () -> assertEquals(expected.getLastname(), actual.getLastname()),
                () -> assertEquals(expected.getTotalprice(), actual.getTotalprice()),
                () -> assertEquals(expected.getAdditionalneeds(), actual.getAdditionalneeds()),
                () -> assertEquals(expected.getDepositpaid(), actual.getDepositpaid()),
                () -> assertAll(
                        () -> assertEquals(expected.getBookingdates().getCheckin(), actual.getBookingdates().getCheckin()),
                        () -> assertEquals(expected.getBookingdates().getCheckout(), actual.getBookingdates().getCheckout())
                )
        );
    }
}
