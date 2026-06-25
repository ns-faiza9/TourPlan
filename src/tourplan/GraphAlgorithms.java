package tourplan;

import java.util.*;

public class GraphAlgorithms {
    public record Edge(int from, int to, int weight) {}
    private final int vertices;
    private final List<List<Edge>> adjacency;
    private final List<Edge> edges = new ArrayList<>();
    public GraphAlgorithms(int vertices) { this.vertices = vertices; adjacency = new ArrayList<>(); for (int i = 0; i < vertices; i++) adjacency.add(new ArrayList<>()); }
    public void addDirectedEdge(int u, int v, int w) { check(u); check(v); Edge e = new Edge(u, v, w); adjacency.get(u).add(e); edges.add(e); }
    public void addUndirectedEdge(int u, int v, int w) { addDirectedEdge(u, v, w); addDirectedEdge(v, u, w); }
    private void check(int v) { if (v < 0 || v >= vertices) throw new IllegalArgumentException("Vertex must be 0 to " + (vertices - 1)); }
    public List<Integer> bfs(int start) { check(start); List<Integer> out = new ArrayList<>(); boolean[] seen = new boolean[vertices]; Queue<Integer> q = new ArrayDeque<>(); q.add(start); seen[start] = true; while (!q.isEmpty()) { int u = q.remove(); out.add(u); for (Edge e : adjacency.get(u)) if (!seen[e.to]) { seen[e.to] = true; q.add(e.to); } } return out; }
    public List<Integer> dfs(int start) { check(start); List<Integer> out = new ArrayList<>(); dfs(start, new boolean[vertices], out); return out; }
    private void dfs(int u, boolean[] seen, List<Integer> out) { seen[u] = true; out.add(u); for (Edge e : adjacency.get(u)) if (!seen[e.to]) dfs(e.to, seen, out); }

    public record PathResult(long distance, List<Integer> path) {}
    public PathResult dijkstra(int source, int target) {
        check(source); check(target); long[] d = new long[vertices]; int[] parent = new int[vertices]; Arrays.fill(d, Long.MAX_VALUE); Arrays.fill(parent, -1); d[source] = 0;
        PriorityQueue<long[]> pq = new PriorityQueue<>(Comparator.comparingLong(a -> a[1])); pq.add(new long[]{source, 0});
        while (!pq.isEmpty()) { long[] cur = pq.remove(); int u = (int)cur[0]; if (cur[1] != d[u]) continue; for (Edge e : adjacency.get(u)) { if (e.weight < 0) throw new IllegalArgumentException("Dijkstra requires non-negative weights"); long nd = d[u] + e.weight; if (nd < d[e.to]) { d[e.to] = nd; parent[e.to] = u; pq.add(new long[]{e.to, nd}); } } }
        return result(source, target, d, parent);
    }
    public PathResult bellmanFord(int source, int target) {
        check(source); check(target); long[] d = new long[vertices]; int[] parent = new int[vertices]; Arrays.fill(d, Long.MAX_VALUE); Arrays.fill(parent, -1); d[source] = 0;
        for (int i = 1; i < vertices; i++) { boolean changed = false; for (Edge e : edges) if (d[e.from] != Long.MAX_VALUE && d[e.from] + e.weight < d[e.to]) { d[e.to] = d[e.from] + e.weight; parent[e.to] = e.from; changed = true; } if (!changed) break; }
        for (Edge e : edges) if (d[e.from] != Long.MAX_VALUE && d[e.from] + e.weight < d[e.to]) throw new IllegalStateException("Negative cost cycle detected");
        return result(source, target, d, parent);
    }
    private PathResult result(int source, int target, long[] d, int[] parent) { if (d[target] == Long.MAX_VALUE) return new PathResult(-1, List.of()); LinkedList<Integer> path = new LinkedList<>(); for (int v = target; v != -1; v = parent[v]) path.addFirst(v); if (path.getFirst() != source) return new PathResult(-1, List.of()); return new PathResult(d[target], path); }
    public long[][] floydWarshall() { long inf = Long.MAX_VALUE / 4; long[][] d = new long[vertices][vertices]; for (int i = 0; i < vertices; i++) { Arrays.fill(d[i], inf); d[i][i] = 0; } for (Edge e : edges) d[e.from][e.to] = Math.min(d[e.from][e.to], e.weight); for (int k = 0; k < vertices; k++) for (int i = 0; i < vertices; i++) for (int j = 0; j < vertices; j++) if (d[i][k] < inf && d[k][j] < inf) d[i][j] = Math.min(d[i][j], d[i][k] + d[k][j]); return d; }
    public List<Integer> topologicalSort() { int[] degree = new int[vertices]; for (Edge e : edges) degree[e.to]++; Queue<Integer> q = new ArrayDeque<>(); for (int i = 0; i < vertices; i++) if (degree[i] == 0) q.add(i); List<Integer> order = new ArrayList<>(); while (!q.isEmpty()) { int u = q.remove(); order.add(u); for (Edge e : adjacency.get(u)) if (--degree[e.to] == 0) q.add(e.to); } if (order.size() != vertices) throw new IllegalStateException("Graph contains a cycle"); return order; }

    public List<Edge> kruskalMST() { List<Edge> unique = new ArrayList<>(); for (Edge e : edges) if (e.from < e.to) unique.add(e); unique.sort(Comparator.comparingInt(Edge::weight)); DSU dsu = new DSU(vertices); List<Edge> mst = new ArrayList<>(); for (Edge e : unique) if (dsu.union(e.from, e.to)) mst.add(e); if (mst.size() != vertices - 1) throw new IllegalStateException("Network is disconnected"); return mst; }
    private static class DSU { int[] p, rank; DSU(int n) { p = new int[n]; rank = new int[n]; for (int i = 0; i < n; i++) p[i] = i; } int find(int x) { return p[x] == x ? x : (p[x] = find(p[x])); } boolean union(int a, int b) { a = find(a); b = find(b); if (a == b) return false; if (rank[a] < rank[b]) { int t = a; a = b; b = t; } p[b] = a; if (rank[a] == rank[b]) rank[a]++; return true; } }
}