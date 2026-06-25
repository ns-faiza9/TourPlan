package tourplan;

public final class Models {
    private Models() {}

    public record Destination(int id, String name, double popularity) {
        @Override public String toString() {
            return String.format("D%03d | %-20s | popularity %.1f", id, name, popularity);
        }
    }

    public record Booking(int id, String tourist, String hotel, double price) {
        @Override public String toString() {
            return String.format("B%04d | %-15s | %-18s | $%.2f", id, tourist, hotel, price);
        }
    }

    public record JourneyTicket(int bookingId, String passenger, String transport,
            String source, String destination, String travelDate, int seats, double totalFare) {
        @Override public String toString() {
            return String.format("J%04d | %s | %s: %s -> %s | %s | seats %d | $%.2f",
                    bookingId, passenger, transport, source, destination, travelDate, seats, totalFare);
        }
    }

    public record HotelBooking(int bookingId, String guest, String hotel, String roomType,
            String checkIn, String checkOut, int rooms, int guests, double totalPrice) {
        @Override public String toString() {
            return String.format("H%04d | %s | %s (%s) | %s to %s | rooms %d, guests %d | $%.2f",
                    bookingId, guest, hotel, roomType, checkIn, checkOut, rooms, guests, totalPrice);
        }
    }

    public record Activity(String name, int start, int finish) {
        @Override public String toString() { return name + " [" + start + ":00-" + finish + ":00]"; }
    }

    public record TravelPackage(String name, int cost, int value) {
        @Override public String toString() { return name + " (cost=$" + cost + ", value=" + value + ")"; }
    }
}