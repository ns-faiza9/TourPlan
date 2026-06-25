package tourplan;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import tourplan.Models.Destination;
import tourplan.Models.HotelBooking;
import tourplan.Models.JourneyTicket;

/**
 * PostgreSQL JDBC connector for TourPlan.
 */
public class DatabaseManager {

	private static final String URL = "jdbc:postgresql://localhost:5432/TourPlan";
	private static final String USER = "postgres";
	private static final String PASSWORD = "admin123";

	private Connection conn;

	public DatabaseManager() {
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection(URL, USER, PASSWORD);
			System.out.println("[DB] Connected to PostgreSQL: " + URL);
			if (conn != null) {
				createTablesIfMissing();
			}
		} catch (ClassNotFoundException e) {
			System.out.println("[DB] PostgreSQL JDBC driver not found. Add postgresql-*.jar to Build Path.");
		} catch (SQLException e) {
			System.out.println("[DB] Connection failed: " + e.getMessage());
		}
	}

	public static void main(String[] args) {
		DatabaseManager db = new DatabaseManager();
		System.out.println("Connected: " + db.isConnected());
	}

	public boolean isConnected() {
		return conn != null;
	}

	private void createTablesIfMissing() throws SQLException {
		try (Statement s = conn.createStatement()) {
			s.execute("""
					    CREATE TABLE IF NOT EXISTS journey_tickets (
					        booking_id   INTEGER PRIMARY KEY,
					        passenger    VARCHAR(100) NOT NULL,
					        transport    VARCHAR(50),
					        source       VARCHAR(100),
					        destination  VARCHAR(100),
					        travel_date  VARCHAR(20),
					        seats        INTEGER CHECK (seats > 0),
					        total_fare   NUMERIC(10,2) CHECK (total_fare >= 0)
					    )
				""");
			s.execute("""
					    CREATE TABLE IF NOT EXISTS hotel_bookings (
					        booking_id   INTEGER PRIMARY KEY,
					        guest        VARCHAR(100) NOT NULL,
					        hotel        VARCHAR(100),
					        room_type    VARCHAR(50),
					        check_in     VARCHAR(20),
					        check_out    VARCHAR(20),
					        rooms        INTEGER CHECK (rooms > 0),
					        guests       INTEGER CHECK (guests > 0),
					        total_price  NUMERIC(10,2) CHECK (total_price >= 0)
					    )
				""");
			s.execute("""
					    CREATE TABLE IF NOT EXISTS destinations (
					        id         INTEGER PRIMARY KEY,
					        name       VARCHAR(100) NOT NULL,
					        popularity NUMERIC(3,1)
					    )
				""");
			seedInitialDataIfMissing();
		}
	}

	private void seedInitialDataIfMissing() throws SQLException {
		try (Statement s = conn.createStatement()) {
			s.executeUpdate("""
					    INSERT INTO destinations (id, name, popularity)
					    VALUES (104, 'Jaipur', 8.8), (101, 'Delhi', 9.2), (108, 'Goa', 9.5)
					    ON CONFLICT (id) DO NOTHING
				""");
			s.executeUpdate("""
					    INSERT INTO journey_tickets (booking_id, passenger, transport, source, destination, travel_date, seats, total_fare)
					    VALUES (1001, 'Aarav', 'Train', 'Lahore', 'Islamabad', '01-07-2026', 2, 2400.00)
					    ON CONFLICT (booking_id) DO NOTHING
				""");
			s.executeUpdate("""
					    INSERT INTO hotel_bookings (booking_id, guest, hotel, room_type, check_in, check_out, rooms, guests, total_price)
					    VALUES (2001, 'Maya', 'Palm Resort', 'Deluxe', '02-07-2026', '05-07-2026', 1, 2, 4500.00)
					    ON CONFLICT (booking_id) DO NOTHING
				""");
		}
	}

	public boolean insertDestination(Destination d) {
		if (conn == null) {
			System.out.println("[DB] Not connected to PostgreSQL.");
			return false;
		}
		String sql = """
				INSERT INTO destinations (id, name, popularity)
				VALUES (?,?,?)
				ON CONFLICT (id) DO UPDATE SET name = EXCLUDED.name, popularity = EXCLUDED.popularity
			""";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, d.id());
			ps.setString(2, d.name());
			ps.setDouble(3, d.popularity());
			ps.executeUpdate();
			// Return true even if no rows were inserted because ON CONFLICT handled it
			return true;
		} catch (SQLException e) {
			System.out.println("[DB] Insert destination failed: " + e.getMessage());
			return false;
		}
	}

	public List<Destination> allDestinations() {
		List<Destination> out = new ArrayList<>();
		if (conn == null) {
			return out;
		}
		try (Statement s = conn.createStatement();
				ResultSet rs = s.executeQuery("SELECT * FROM destinations ORDER BY id")) {
			while (rs.next()) {
				out.add(new Destination(rs.getInt("id"), rs.getString("name"), rs.getDouble("popularity")));
			}
		} catch (SQLException e) {
			System.out.println("[DB] " + e.getMessage());
		}
		return out;
	}

	public boolean insertJourneyTicket(JourneyTicket t) {
		if (conn == null) {
			System.out.println("[DB] Not connected to PostgreSQL.");
			return false;
		}
		String sql = """
				INSERT INTO journey_tickets (booking_id, passenger, transport, source, destination, travel_date, seats, total_fare)
				VALUES (?,?,?,?,?,?,?,?)
				ON CONFLICT (booking_id) DO NOTHING
			""";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, t.bookingId());
			ps.setString(2, t.passenger());
			ps.setString(3, t.transport());
			ps.setString(4, t.source());
			ps.setString(5, t.destination());
			ps.setString(6, t.travelDate());
			ps.setInt(7, t.seats());
			ps.setDouble(8, t.totalFare());
			int rows = ps.executeUpdate();
			return rows > 0;
		} catch (SQLException e) {
			System.out.println("[DB] Insert journey failed: " + e.getMessage());
			return false;
		}
	}

	public JourneyTicket findJourneyTicket(int id) {
		String sql = "SELECT * FROM journey_tickets WHERE booking_id=?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return new JourneyTicket(rs.getInt("booking_id"), rs.getString("passenger"), rs.getString("transport"),
						rs.getString("source"), rs.getString("destination"), rs.getString("travel_date"),
						rs.getInt("seats"), rs.getDouble("total_fare"));
			}
		} catch (SQLException e) {
			System.out.println("[DB] " + e.getMessage());
		}
		return null;
	}

	public boolean deleteJourneyTicket(int id) {
		try (PreparedStatement ps = conn.prepareStatement("DELETE FROM journey_tickets WHERE booking_id=?")) {
			ps.setInt(1, id);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			System.out.println("[DB] " + e.getMessage());
			return false;
		}
	}

	public List<JourneyTicket> allJourneyTickets() {
		List<JourneyTicket> out = new ArrayList<>();
		if (conn == null) {
			return out;
		}
		try (Statement s = conn.createStatement();
				ResultSet rs = s.executeQuery("SELECT * FROM journey_tickets ORDER BY booking_id")) {
			while (rs.next()) {
				out.add(new JourneyTicket(rs.getInt("booking_id"), rs.getString("passenger"), rs.getString("transport"),
						rs.getString("source"), rs.getString("destination"), rs.getString("travel_date"),
						rs.getInt("seats"), rs.getDouble("total_fare")));
			}
		} catch (SQLException e) {
			System.out.println("[DB] " + e.getMessage());
		}
		return out;
	}

	public boolean insertHotelBooking(HotelBooking h) {
		if (conn == null) {
			System.out.println("[DB] Not connected to PostgreSQL.");
			return false;
		}
		String sql = """
				INSERT INTO hotel_bookings (booking_id, guest, hotel, room_type, check_in, check_out, rooms, guests, total_price)
				VALUES (?,?,?,?,?,?,?,?,?)
				ON CONFLICT (booking_id) DO NOTHING
			""";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, h.bookingId());
			ps.setString(2, h.guest());
			ps.setString(3, h.hotel());
			ps.setString(4, h.roomType());
			ps.setString(5, h.checkIn());
			ps.setString(6, h.checkOut());
			ps.setInt(7, h.rooms());
			ps.setInt(8, h.guests());
			ps.setDouble(9, h.totalPrice());
			int rows = ps.executeUpdate();
			return rows > 0;
		} catch (SQLException e) {
			System.out.println("[DB] Insert hotel failed: " + e.getMessage());
			return false;
		}
	}

	public HotelBooking findHotelBooking(int id) {
		String sql = "SELECT * FROM hotel_bookings WHERE booking_id=?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return new HotelBooking(rs.getInt("booking_id"), rs.getString("guest"), rs.getString("hotel"),
						rs.getString("room_type"), rs.getString("check_in"), rs.getString("check_out"),
						rs.getInt("rooms"), rs.getInt("guests"), rs.getDouble("total_price"));
			}
		} catch (SQLException e) {
			System.out.println("[DB] " + e.getMessage());
		}
		return null;
	}

	public boolean deleteHotelBooking(int id) {
		try (PreparedStatement ps = conn.prepareStatement("DELETE FROM hotel_bookings WHERE booking_id=?")) {
			ps.setInt(1, id);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			System.out.println("[DB] " + e.getMessage());
			return false;
		}
	}

	public List<HotelBooking> allHotelBookings() {
		List<HotelBooking> out = new ArrayList<>();
		if (conn == null) {
			return out;
		}
		try (Statement s = conn.createStatement();
				ResultSet rs = s.executeQuery("SELECT * FROM hotel_bookings ORDER BY booking_id")) {
			while (rs.next()) {
				out.add(new HotelBooking(rs.getInt("booking_id"), rs.getString("guest"), rs.getString("hotel"),
						rs.getString("room_type"), rs.getString("check_in"), rs.getString("check_out"),
						rs.getInt("rooms"), rs.getInt("guests"), rs.getDouble("total_price")));
			}
		} catch (SQLException e) {
			System.out.println("[DB] " + e.getMessage());
		}
		return out;
	}

	public void close() {
		try {
			if (conn != null)
				conn.close();
		} catch (SQLException ignored) {
		}
	}
}
