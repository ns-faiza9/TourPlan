package tourplan;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public final class RangeStructures {
    private RangeStructures() {}

    public static class BPlusPriceIndex {
        private final TreeMap<Double, List<String>> leaves = new TreeMap<>();
        public void add(double price, String hotel) { leaves.computeIfAbsent(price, k -> new ArrayList<>()).add(hotel); }
        public List<String> range(double low, double high) {
            List<String> out = new ArrayList<>();
            for (Map.Entry<Double, List<String>> e : leaves.subMap(low, true, high, true).entrySet())
                for (String hotel : e.getValue()) out.add(String.format("$%.2f - %s", e.getKey(), hotel));
            return out;
        }
    }

    public static class SegmentTree {
        private final int n; private final int[] tree;
        public SegmentTree(int[] data) { n = data.length; tree = new int[Math.max(1, 4 * n)]; if (n > 0) build(data, 1, 0, n - 1); }
        private void build(int[] a, int p, int l, int r) { if (l == r) tree[p] = a[l]; else { int m = (l + r) / 2; build(a, p * 2, l, m); build(a, p * 2 + 1, m + 1, r); tree[p] = tree[p * 2] + tree[p * 2 + 1]; } }
        public int query(int left, int right) { if (n == 0 || left < 0 || right >= n || left > right) throw new IllegalArgumentException("Invalid range"); return query(1, 0, n - 1, left, right); }
        private int query(int p, int l, int r, int ql, int qr) { if (ql <= l && r <= qr) return tree[p]; if (r < ql || qr < l) return 0; int m = (l + r) / 2; return query(p * 2, l, m, ql, qr) + query(p * 2 + 1, m + 1, r, ql, qr); }
    }

    public static class FenwickTree {
        private final int[] bit;
        public FenwickTree(int n) { bit = new int[n + 1]; }
        public void add(int index, int delta) { for (int i = index + 1; i < bit.length; i += i & -i) bit[i] += delta; }
        public int prefixSum(int index) { int sum = 0; for (int i = index + 1; i > 0; i -= i & -i) sum += bit[i]; return sum; }
        public int rangeSum(int left, int right) { return prefixSum(right) - (left == 0 ? 0 : prefixSum(left - 1)); }
    }
}