package org.payconiq;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.payconiq.clients.RESTClient;
import org.payconiq.helpers.BookingAssertions;
import org.payconiq.models.Booking;
import org.payconiq.models.BookingResource;
import org.payconiq.models.Credentials;
import org.payconiq.models.Dates;

import static org.junit.jupiter.api.Assertions.*;

public class BookingCRUDTest {
    private static RESTClient client;

    @BeforeAll
    static void prepareRESTClient() {
        client = new RESTClient(
                "https://restful-booker.herokuapp.com",
                new Credentials("admin", "password123")
        );
    }

    @Test
    @DisplayName("It is able to crate booking")
    void crateBooking() throws Exception {
        Booking booking = Booking.builder()
                .firstname("Jim")
                .lastname("Brown")
                .totalprice(111)
                .depositpaid(true)
                .bookingdates(new Dates("1970-01-01", "2038-01-19"))
                .additionalneeds("Breakfast")
                .build();
        BookingResource bookingResource = client.createBooking(booking);
        assertAll(
                () -> assertNotNull(bookingResource.getBookingid()),
                () -> BookingAssertions.assetEquals(bookingResource.getBooking(), booking)
        );
    }

    @Test
    @DisplayName("It is able to crate booking with minimal values")
    void crateBookingWithMinimalValues() throws Exception {
        Booking booking = Booking.builder()
                .firstname("")
                .lastname("")
                .totalprice(0)
                .depositpaid(true)
                .bookingdates(new Dates("1970-01-01", "2038-01-19"))
                .additionalneeds("")
                .build();
        BookingResource bookingResource = client.createBooking(booking);
        assertAll(
                () -> assertNotNull(bookingResource.getBookingid()),
                () -> BookingAssertions.assetEquals(bookingResource.getBooking(), booking)
        );
    }
}
