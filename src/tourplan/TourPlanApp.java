package tourplan;

import java.util.*;
import tourplan.Models.*;
import tourplan.RangeStructures.*;
import tourplan.TreeAlgorithms.*;

public class TourPlanApp {
	private final Scanner in = new Scanner(System.in);
	private final DestinationBST destinations = new DestinationBST();
	private final BookingAVL bookings = new BookingAVL();
	private final BookingManager bookingManager = new BookingManager();
	private final DatabaseManager databaseManager = new DatabaseManager();

	public static void main(String[] args) {
		new TourPlanApp().run();
	}

	private void run() {
		seed();
		banner();
		while (true) {
			System.out.println("Select your Tour Plan: ");
			System.out.println(
					"\n1 Journey & hotel booking\n2 Trees\n3 Range analytics\n4 Graph network\n5 Shortest paths\n6 Sorting\n7 Greedy & DP\n0 Exit");
			switch (readInt("Select module: ")) {
			case 1 -> bookingMenu();
			case 2 -> trees();
			case 3 -> ranges();
			case 4 -> graphNetwork();
			case 5 -> paths();
			case 6 -> sorting();
			case 7 -> optimization();
			case 0 -> {
				System.out.println("Thank you for using Tour Plan Management System.");
				return;
			}
			default -> System.out.println("Invalid option.");
			}
		}
	}

	private void banner() {
		System.out.println(
				"====================================================\n TOURPLAN - Smart Tourism & Itinerary Optimization\n Java 17 | User-input DSA demonstration\n====================================================");
	}

	private void seed() {
		Destination[] seedDestinations = {
				new Destination(104, "Jaipur", 8.8),
				new Destination(101, "Delhi", 9.2),
				new Destination(108, "Goa", 9.5)
		};
		for (Destination destination : seedDestinations) {
			destinations.insert(destination);
			databaseManager.insertDestination(destination);
		}
		bookings.insert(new Booking(5002, "Aarav", "Palm Resort", 145));
		bookings.insert(new Booking(5001, "Maya", "Heritage Inn", 110));
	}

	private void bookingMenu() {
		System.out.println(
				"\n1 Book journey ticket  2 Book hotel  3 Search booking  4 View all  5 Cancel journey  6 Cancel hotel");
		switch (readInt("Choice: ")) {
		case 1 -> bookJourney();
		case 2 -> bookHotel();
		case 3 -> searchBooking();
		case 4 -> showBookings();
		case 5 -> System.out.println(
				bookingManager.cancelJourneyTicket(readInt("Journey booking ID: ")) ? "Journey ticket cancelled."
						: "Journey booking not found.");
		case 6 -> System.out
				.println(bookingManager.cancelHotelBooking(readInt("Hotel booking ID: ")) ? "Hotel booking cancelled."
						: "Hotel booking not found.");
		default -> System.out.println("Invalid option.");
		}
	}

	private void bookJourney() {
		int id = readInt("Booking ID: ");
		String passenger = read("Passenger name: ");
		String transport = read("Transport (Flight/Train/Bus): ");
		String source = read("Source: ");
		String destination = read("Destination: ");
		String date = read("Travel date (DD-MM-YYYY): ");
		int seats = readPositiveInt("Number of seats: ");
		double fare = readPositiveDouble("Fare per seat: ");
		JourneyTicket ticket = new JourneyTicket(id, passenger, transport, source, destination, date, seats,
				seats * fare);
		if (!bookingManager.addJourneyTicket(ticket)) {
			System.out.println("Booking ID already exists.");
			return;
		}
		if (!databaseManager.insertJourneyTicket(ticket)) {
			bookingManager.cancelJourneyTicket(ticket.bookingId());
			System.out.println("Failed to save journey ticket to PostgreSQL.");
			return;
		}
		System.out.println("Journey ticket booked and saved to PostgreSQL:\n" + ticket);
	}

	private void bookHotel() {
		int id = readInt("Booking ID: ");
		String guest = read("Guest name: ");
		String hotel = read("Hotel name: ");
		String roomType = read("Room type: ");
		String checkIn = read("Check-in date (DD-MM-YYYY): ");
		String checkOut = read("Check-out date (DD-MM-YYYY): ");
		int rooms = readPositiveInt("Number of rooms: ");
		int guests = readPositiveInt("Number of guests: ");
		int nights = readPositiveInt("Number of nights: ");
		double rate = readPositiveDouble("Price per room per night: ");
		HotelBooking booking = new HotelBooking(id, guest, hotel, roomType, checkIn, checkOut, rooms, guests,
				rooms * nights * rate);
		if (!bookingManager.addHotelBooking(booking)) {
			System.out.println("Booking ID already exists.");
			return;
		}
		if (!databaseManager.insertHotelBooking(booking)) {
			bookingManager.cancelHotelBooking(booking.bookingId());
			System.out.println("Failed to save hotel booking to PostgreSQL.");
			return;
		}
		System.out.println("Hotel booked and saved to PostgreSQL:\n" + booking);
	}

	private void searchBooking() {
		int id = readInt("Booking ID: ");
		JourneyTicket journey = bookingManager.findJourneyTicket(id);
		if (journey != null) {
			System.out.println(journey);
			return;
		}
		HotelBooking hotel = bookingManager.findHotelBooking(id);
		System.out.println(hotel == null ? "Booking not found." : hotel);
	}

	private void showBookings() {
		System.out.println("\nDESTINATIONS");
		if (databaseManager.allDestinations().isEmpty())
			System.out.println("No destinations.");
		else
			databaseManager.allDestinations().forEach(System.out::println);

		System.out.println("\nJOURNEY TICKETS");
		if (databaseManager.allJourneyTickets().isEmpty())
			System.out.println("No journey tickets.");
		else
			databaseManager.allJourneyTickets().forEach(System.out::println);

		System.out.println("\nHOTEL BOOKINGS");
		if (databaseManager.allHotelBookings().isEmpty())
			System.out.println("No hotel bookings.");
		else
			databaseManager.allHotelBookings().forEach(System.out::println);
	}

	private void trees() {
		System.out.println(
				"\n1 Add destination  2 Search  3 Delete  4 Sorted itinerary  5 Add booking  6 Delete booking  7 Show AVL");
		switch (readInt("Choice: ")) {
		case 1 -> {
			Destination destination = new Destination(readInt("Destination ID: "), read("Name: "),
					readDouble("Popularity (0-10): "));
			destinations.insert(destination);
			databaseManager.insertDestination(destination);
			System.out.println("Inserted.");
		}
		case 2 -> System.out.println(
				Optional.ofNullable(destinations.search(readInt("ID: "))).map(Object::toString).orElse("Not found."));
		case 3 -> System.out.println(destinations.delete(readInt("ID: ")) ? "Deleted." : "Not found.");
		case 4 -> destinations.inOrder().forEach(System.out::println);
		case 5 -> {
			bookings.insert(
					new Booking(readInt("Booking ID: "), read("Tourist: "), read("Hotel: "), readDouble("Price: ")));
			System.out.println("AVL record inserted.");
		}
		case 6 -> {
			bookings.delete(readInt("Booking ID: "));
			System.out.println("Delete completed.");
		}
		case 7 -> {
			bookings.inOrder().forEach(System.out::println);
			System.out.println("Balanced tree height: " + bookings.height());
		}
		default -> System.out.println("Invalid option.");
		}
	}

	private void ranges() {
		int n = readInt("Number of seasons/months: ");
		int[] visits = readIntArray(n, "Enter visit counts");
		SegmentTree st = new SegmentTree(visits);
		FenwickTree ft = new FenwickTree(n);
		for (int i = 0; i < n; i++)
			ft.add(i, visits[i]);
		int l = readInt("Start index (0 based): "), r = readInt("End index: ");
		System.out.println("Segment Tree sum = " + st.query(l, r));
		System.out.println("Fenwick cumulative/range sum = " + ft.rangeSum(l, r));
		BPlusPriceIndex index = new BPlusPriceIndex();
		int hotels = readInt("Hotels to index: ");
		for (int i = 0; i < hotels; i++)
			index.add(readDouble("Hotel price: "), read("Hotel name: "));
		System.out.println("Hotels in range:");
		index.range(readDouble("Minimum price: "), readDouble("Maximum price: ")).forEach(System.out::println);
	}

	private GraphAlgorithms inputGraph(boolean directed) {
		int v = readInt("Number of destinations: "), e = readInt("Number of routes: ");
		GraphAlgorithms g = new GraphAlgorithms(v);
		System.out.println("Destination IDs are 0 to " + (v - 1));
		for (int i = 0; i < e; i++) {
			System.out.println("Route " + (i + 1));
			int a = readInt("From: "), b = readInt("To: "), w = readInt("Cost/distance: ");
			if (directed)
				g.addDirectedEdge(a, b, w);
			else
				g.addUndirectedEdge(a, b, w);
		}
		return g;
	}

	private void graphNetwork() {
		GraphAlgorithms g = inputGraph(false);
		int start = readInt("Start destination: ");
		System.out.println("BFS reachable order: " + g.bfs(start));
		System.out.println("DFS connectivity order: " + g.dfs(start));
		try {
			List<GraphAlgorithms.Edge> mst = g.kruskalMST();
			int total = mst.stream().mapToInt(GraphAlgorithms.Edge::weight).sum();
			mst.forEach(System.out::println);
			System.out.println("Optimal circuit MST cost: " + total);
		} catch (IllegalStateException e) {
			System.out.println(e.getMessage());
		}
	}

	private void paths() {
		GraphAlgorithms g = inputGraph(true);
		System.out.println("1 Dijkstra  2 Bellman-Ford  3 Floyd-Warshall  4 Topological itinerary");
		int choice = readInt("Choice: ");
		try {
			if (choice == 1 || choice == 2) {
				int s = readInt("Source: "), t = readInt("Destination: ");
				GraphAlgorithms.PathResult p = choice == 1 ? g.dijkstra(s, t) : g.bellmanFord(s, t);
				System.out
						.println(p.distance() < 0 ? "No route." : "Path " + p.path() + ", total cost " + p.distance());
			} else if (choice == 3) {
				long[][] d = g.floydWarshall();
				for (long[] row : d) {
					for (long x : row)
						System.out.print((x >= Long.MAX_VALUE / 8 ? "INF" : x) + "\t");
					System.out.println();
				}
			} else if (choice == 4)
				System.out.println("Valid activity order: " + g.topologicalSort());
			else
				System.out.println("Invalid option.");
		} catch (RuntimeException e) {
			System.out.println("Cannot compute: " + e.getMessage());
		}
	}

	private List<Destination> readDestinations() {
		int n = readInt("Number of destinations: ");
		List<Destination> a = new ArrayList<>();
		for (int i = 0; i < n; i++)
			a.add(new Destination(readInt("ID: "), read("Name: "), readDouble("Popularity: ")));
		return a;
	}

	private List<TravelPackage> readPackages() {
		int n = readInt("Number of packages/items: ");
		List<TravelPackage> p = new ArrayList<>();
		for (int i = 0; i < n; i++)
			p.add(new TravelPackage(read("Name: "), readInt("Cost: "), readInt("Benefit value: ")));
		return p;
	}

	private void sorting() {
		System.out.println(
				"1 Merge Sort packages by cost  2 Quick Sort popularity  3 Heap top attractions  4 Counting Sort booking IDs");
		int c = readInt("Choice: ");
		if (c == 1) {
			List<TravelPackage> p = readPackages();
			OptimizationAlgorithms.mergeSort(p);
			p.forEach(System.out::println);
		} else if (c == 2) {
			List<Destination> d = readDestinations();
			OptimizationAlgorithms.quickSort(d);
			d.forEach(System.out::println);
		} else if (c == 3) {
			List<Destination> d = readDestinations();
			OptimizationAlgorithms.topByHeap(d, readInt("How many top attractions? ")).forEach(System.out::println);
		} else if (c == 4) {
			int[] a = readIntArray(readInt("Number of IDs: "), "Enter IDs");
			OptimizationAlgorithms.countingSort(a);
			System.out.println(Arrays.toString(a));
		} else
			System.out.println("Invalid option.");
	}

	private void optimization() {
		System.out.println("1 Activity Selection  2 Fractional Knapsack  3 0/1 Knapsack  4 Tourism Growth LIS");
		int c = readInt("Choice: ");
		if (c == 1) {
			int n = readInt("Number of activities: ");
			List<Activity> a = new ArrayList<>();
			for (int i = 0; i < n; i++)
				a.add(new Activity(read("Activity: "), readInt("Start hour: "), readInt("Finish hour: ")));
			System.out.println("Maximum compatible schedule:");
			OptimizationAlgorithms.activitySelection(a).forEach(System.out::println);
		} else if (c == 2) {
			OptimizationAlgorithms.fractionalKnapsack(readPackages(), readDouble("Budget: ")).forEach(x -> System.out
					.printf("%s: %.1f%%, cost %.2f, value %.2f%n", x.name(), x.fraction() * 100, x.cost(), x.value()));
		} else if (c == 3) {
			List<TravelPackage> p = readPackages();
			List<TravelPackage> chosen = OptimizationAlgorithms.zeroOneKnapsack(p, readInt("Budget: "));
			chosen.forEach(System.out::println);
			System.out.println("Total cost=" + chosen.stream().mapToInt(TravelPackage::cost).sum() + ", value="
					+ chosen.stream().mapToInt(TravelPackage::value).sum());
		} else if (c == 4) {
			int[] a = readIntArray(readInt("Number of periods: "), "Enter tourist counts");
			System.out.println("Longest growth trend: " + OptimizationAlgorithms.lis(a));
		} else
			System.out.println("Invalid option.");
	}

	private String read(String prompt) {
		System.out.print(prompt);
		return in.nextLine().trim();
	}

	private int readInt(String prompt) {
		while (true)
			try {
				return Integer.parseInt(read(prompt));
			} catch (NumberFormatException e) {
				System.out.println("Enter a valid integer.");
			}
	}

	private double readDouble(String prompt) {
		while (true)
			try {
				return Double.parseDouble(read(prompt));
			} catch (NumberFormatException e) {
				System.out.println("Enter a valid number.");
			}
	}

	private int readPositiveInt(String prompt) {
		while (true) {
			int value = readInt(prompt);
			if (value > 0)
				return value;
			System.out.println("Enter a value greater than zero.");
		}
	}

	private double readPositiveDouble(String prompt) {
		while (true) {
			double value = readDouble(prompt);
			if (value > 0)
				return value;
			System.out.println("Enter a value greater than zero.");
		}
	}

	private int[] readIntArray(int n, String title) {
		int[] a = new int[n];
		System.out.println(title + ":");
		for (int i = 0; i < n; i++)
			a[i] = readInt("Value " + i + ": ");
		return a;
	}
}