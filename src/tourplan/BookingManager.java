package tourplan;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import tourplan.Models.HotelBooking;
import tourplan.Models.JourneyTicket;

public class BookingManager {
    private final Map<Integer, JourneyTicket> journeyTickets = new LinkedHashMap<>();
    private final Map<Integer, HotelBooking> hotelBookings = new LinkedHashMap<>();

    public boolean addJourneyTicket(JourneyTicket ticket) {
        if (containsId(ticket.bookingId())) return false;
        journeyTickets.put(ticket.bookingId(), ticket);
        return true;
    }

    public boolean addHotelBooking(HotelBooking booking) {
        if (containsId(booking.bookingId())) return false;
        hotelBookings.put(booking.bookingId(), booking);
        return true;
    }

    public JourneyTicket findJourneyTicket(int id) { return journeyTickets.get(id); }
    public HotelBooking findHotelBooking(int id) { return hotelBookings.get(id); }

    public boolean cancelJourneyTicket(int id) { return journeyTickets.remove(id) != null; }
    public boolean cancelHotelBooking(int id) { return hotelBookings.remove(id) != null; }

    public List<JourneyTicket> allJourneyTickets() {
        List<JourneyTicket> result = new ArrayList<>(journeyTickets.values());
        result.sort(Comparator.comparingInt(JourneyTicket::bookingId));
        return result;
    }

    public List<HotelBooking> allHotelBookings() {
        List<HotelBooking> result = new ArrayList<>(hotelBookings.values());
        result.sort(Comparator.comparingInt(HotelBooking::bookingId));
        return result;
    }

    private boolean containsId(int id) {
        return journeyTickets.containsKey(id) || hotelBookings.containsKey(id);
    }
}