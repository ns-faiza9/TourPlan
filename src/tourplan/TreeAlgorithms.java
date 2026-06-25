package tourplan;

import java.util.ArrayList;
import java.util.List;
import tourplan.Models.Booking;
import tourplan.Models.Destination;

public final class TreeAlgorithms {
    private TreeAlgorithms() {}

    public static class DestinationBST {
        private Node root;
        private static class Node { Destination data; Node left, right; Node(Destination d) { data = d; } }
        public void insert(Destination d) { root = insert(root, d); }
        private Node insert(Node n, Destination d) {
            if (n == null) return new Node(d);
            if (d.id() < n.data.id()) n.left = insert(n.left, d);
            else if (d.id() > n.data.id()) n.right = insert(n.right, d);
            else n.data = d;
            return n;
        }
        public Destination search(int id) {
            Node n = root;
            while (n != null) { if (id == n.data.id()) return n.data; n = id < n.data.id() ? n.left : n.right; }
            return null;
        }
        public boolean delete(int id) { if (search(id) == null) return false; root = delete(root, id); return true; }
        private Node delete(Node n, int id) {
            if (n == null) return null;
            if (id < n.data.id()) n.left = delete(n.left, id);
            else if (id > n.data.id()) n.right = delete(n.right, id);
            else { if (n.left == null) return n.right; if (n.right == null) return n.left; Node s = n.right; while (s.left != null) s = s.left; n.data = s.data; n.right = delete(n.right, s.data.id()); }
            return n;
        }
        public List<Destination> inOrder() { List<Destination> out = new ArrayList<>(); inOrder(root, out); return out; }
        private void inOrder(Node n, List<Destination> out) { if (n != null) { inOrder(n.left, out); out.add(n.data); inOrder(n.right, out); } }
    }

    public static class BookingAVL {
        private Node root;
        private static class Node { Booking data; Node left, right; int height = 1; Node(Booking b) { data = b; } }
        private int h(Node n) { return n == null ? 0 : n.height; }
        private void update(Node n) { n.height = 1 + Math.max(h(n.left), h(n.right)); }
        private Node right(Node y) { Node x = y.left, t = x.right; x.right = y; y.left = t; update(y); update(x); return x; }
        private Node left(Node x) { Node y = x.right, t = y.left; y.left = x; x.right = t; update(x); update(y); return y; }
        private Node balance(Node n) {
            update(n); int b = h(n.left) - h(n.right);
            if (b > 1) { if (h(n.left.left) < h(n.left.right)) n.left = left(n.left); return right(n); }
            if (b < -1) { if (h(n.right.right) < h(n.right.left)) n.right = right(n.right); return left(n); }
            return n;
        }
        public void insert(Booking b) { root = insert(root, b); }
        private Node insert(Node n, Booking b) { if (n == null) return new Node(b); if (b.id() < n.data.id()) n.left = insert(n.left, b); else if (b.id() > n.data.id()) n.right = insert(n.right, b); else n.data = b; return balance(n); }
        public void delete(int id) { root = delete(root, id); }
        private Node delete(Node n, int id) {
            if (n == null) return null;
            if (id < n.data.id()) n.left = delete(n.left, id); else if (id > n.data.id()) n.right = delete(n.right, id);
            else { if (n.left == null || n.right == null) n = n.left != null ? n.left : n.right; else { Node s = n.right; while (s.left != null) s = s.left; n.data = s.data; n.right = delete(n.right, s.data.id()); } }
            return n == null ? null : balance(n);
        }
        public List<Booking> inOrder() { List<Booking> out = new ArrayList<>(); inOrder(root, out); return out; }
        private void inOrder(Node n, List<Booking> out) { if (n != null) { inOrder(n.left, out); out.add(n.data); inOrder(n.right, out); } }
        public int height() { return h(root); }
    }
}