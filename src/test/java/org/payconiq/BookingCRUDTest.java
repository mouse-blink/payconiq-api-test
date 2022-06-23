package org.payconiq;

import com.mashape.unirest.http.exceptions.UnirestException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.payconiq.clients.RESTClient;
import org.payconiq.helpers.BookingAssertions;
import org.payconiq.models.Booking;
import org.payconiq.models.BookingResource;
import org.payconiq.models.Credentials;
import org.payconiq.models.Dates;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class BookingCRUDTest {
    static final Logger log = Logger.getLogger(BookingCRUDTest.class.getName());
    private static final Booking minimalBooking = Booking.builder()
            .firstname("Bob")
            .lastname("Marley")
            .totalprice(0)
            .depositpaid(true)
            .bookingdates(new Dates("1970-01-01", "2038-01-19"))
            .additionalneeds("Parking")
            .build();
    private static RESTClient client;

    @BeforeAll
    static void prepareRESTClient() throws UnirestException {
        client = new RESTClient(
                "https://restful-booker.herokuapp.com",
                new Credentials("admin", "password123")
        );
        client.authenticate();
    }

    static List<Integer> getBookingsIdList(BookingResource[] bookingResources) {
        return Arrays.stream(bookingResources)
                .map(bookingResource -> bookingResource.getBookingid())
                .collect(Collectors.toList());
    }

    @Test
    @DisplayName("It is able to crate booking with minimal values")
    void crateBookingWithMinimalValues() throws UnirestException {
        BookingResource[] initialBookingResources = client.getBookingIds();
        BookingResource bookingResource = client.createBooking(minimalBooking);
        assertAll(
                () -> assertNotNull(bookingResource.getBookingid()),
                () -> BookingAssertions.assetEquals(bookingResource.getBooking(), minimalBooking)
        );
        List<BookingResource> filteredInitialBookingResources = Arrays.stream(initialBookingResources)
                .filter(res -> res.getBookingid() == bookingResource.getBookingid())
                .collect(Collectors.toList());
        assertEquals(filteredInitialBookingResources.size(), 0);
        BookingResource[] bookingResources = client.getBookingIds();
        List<BookingResource> filteredBookingResources = Arrays.stream(bookingResources)
                .filter(res -> res.getBookingid().equals(bookingResource.getBookingid()))
                .collect(Collectors.toList());
        assertEquals(filteredBookingResources.size(), 1);
        log.info("Getting Booking by ID : " + bookingResource.getBookingid());
        Booking gottenBooking = client.getBooking(bookingResource.getBookingid());
        BookingAssertions.assetEquals(gottenBooking, minimalBooking);
    }

    @Test
    @DisplayName("It should able to get Bookings with specific name")
    void getBookingsWithName() throws UnirestException {
        String name = UUID.randomUUID().toString();
        Booking unique = Booking.builder()
                .firstname(name)
                .lastname("Marley")
                .totalprice(0)
                .depositpaid(true)
                .bookingdates(new Dates("1970-01-01", "2038-01-19"))
                .additionalneeds("Parking")
                .build();
        ArrayList<Integer> newBookingIds = new ArrayList<Integer>();
        BookingResource firstBookingResource = client.createBooking(unique);
        newBookingIds.add(firstBookingResource.getBookingid());
        BookingResource secondBookingResource = client.createBooking(unique);
        newBookingIds.add(secondBookingResource.getBookingid());
        Collections.sort(newBookingIds);
        Booking queryBooking = Booking.builder().firstname(name).build();
        BookingResource[] bookings = client.getBookingIds(queryBooking);
        assertEquals(bookings.length, 2);
        List<Integer> receivedIDs = getBookingsIdList(bookings);
        Collections.sort(receivedIDs);
        assertEquals(receivedIDs, newBookingIds);
    }

    @Test
    @DisplayName("It should able to get Bookings with specific Date")
    void getBookingsWithDates() throws UnirestException {
        Dates queryDates = new Dates("1999-09-09", "2012-12-12");
        Dates searchedDates = new Dates("1999-09-10", "2012-12-13");
        Dates oldDates = new Dates("1999-09-08", "2012-12-13");
        Booking queryBooking = Booking.builder().bookingdates(new Dates("1999-09-08", null)).build();
        List<Integer> initialBookingsIDList = getBookingsIdList(client.getBookingIds(queryBooking));
        ArrayList<Integer> newBookingIds = new ArrayList<>();
        Booking queryDatesBooking = Booking.builder()
                .firstname("Bobby")
                .lastname("Darin")
                .totalprice(0)
                .depositpaid(true)
                .bookingdates(queryDates)
                .additionalneeds("Parking")
                .build();
        queryDatesBooking.setBookingdates(queryDates);
        BookingResource firstBookingResource = client.createBooking(queryDatesBooking);
        newBookingIds.add(firstBookingResource.getBookingid());
        queryDatesBooking.setBookingdates(searchedDates);
        BookingResource secondBookingResource = client.createBooking(queryDatesBooking);
        newBookingIds.add(secondBookingResource.getBookingid());
        queryDatesBooking.setBookingdates(oldDates);
        client.createBooking(queryDatesBooking);
        Collections.sort(newBookingIds);
        List<Integer> actualBookingsIDList = getBookingsIdList(client.getBookingIds(queryBooking));
        actualBookingsIDList.removeAll(initialBookingsIDList);
        assertEquals(actualBookingsIDList.size(), 2);
        Collections.sort(actualBookingsIDList);
        assertEquals(actualBookingsIDList, newBookingIds);
    }

    @Test
    @DisplayName("It is able to update fist name only in booking")
    public void updateBookingFirstName() throws UnirestException {
        BookingResource bookingResource = client.createBooking(minimalBooking);
        Booking newBooking = Booking.builder().firstname("New Name").build();
        Booking updatedBooking = client.updateBooking(bookingResource.getBookingid(), newBooking);
        newBooking.setLastname(minimalBooking.getLastname());
        newBooking.setTotalprice(minimalBooking.getTotalprice());
        newBooking.setDepositpaid(minimalBooking.getDepositpaid());
        newBooking.setAdditionalneeds(minimalBooking.getAdditionalneeds());
        newBooking.setBookingdates(minimalBooking.getBookingdates());
        BookingAssertions.assetEquals(updatedBooking, newBooking);
    }

    @Test
    @DisplayName("It is able to update last name only in booking")
    public void updateBookingLastName() throws UnirestException {
        BookingResource bookingResource = client.createBooking(minimalBooking);
        Booking newBooking = Booking.builder().lastname("New Name").build();
        Booking updatedBooking = client.updateBooking(bookingResource.getBookingid(), newBooking);
        newBooking.setFirstname(minimalBooking.getFirstname());
        newBooking.setTotalprice(minimalBooking.getTotalprice());
        newBooking.setDepositpaid(minimalBooking.getDepositpaid());
        newBooking.setAdditionalneeds(minimalBooking.getAdditionalneeds());
        newBooking.setBookingdates(minimalBooking.getBookingdates());
        BookingAssertions.assetEquals(updatedBooking, newBooking);
    }

    @Test
    @DisplayName("It is able to update Booking dates only in booking")
    public void updateBookingDates() throws UnirestException {
        BookingResource bookingResource = client.createBooking(minimalBooking);
        Booking newBooking = Booking.builder().bookingdates(new Dates("2020-01-30", "2022-03-01")).build();
        Booking updatedBooking = client.updateBooking(bookingResource.getBookingid(), newBooking);
        newBooking.setFirstname(minimalBooking.getFirstname());
        newBooking.setLastname(minimalBooking.getLastname());
        newBooking.setTotalprice(minimalBooking.getTotalprice());
        newBooking.setDepositpaid(minimalBooking.getDepositpaid());
        newBooking.setAdditionalneeds(minimalBooking.getAdditionalneeds());
        newBooking.setBookingdates(newBooking.getBookingdates());
        BookingAssertions.assetEquals(updatedBooking, newBooking);
    }

    @Test
    @DisplayName("It is able to Delete booking by id")
    public void deleteBooking() throws UnirestException {
        BookingResource bookingResource = client.createBooking(minimalBooking);
        String result = client.deleteBooking(bookingResource.getBookingid());
        assertEquals(result, "Created");
        BookingResource[] bookingResources = client.getBookingIds();
        List<BookingResource> filteredBookingResources = Arrays.stream(bookingResources)
                .filter(res -> res.getBookingid() == bookingResource.getBookingid())
                .collect(Collectors.toList());
        assertEquals(filteredBookingResources.size(), 0);
    }
}
