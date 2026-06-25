package tourplan;

import java.util.*;
import tourplan.Models.Activity;
import tourplan.Models.Destination;
import tourplan.Models.TravelPackage;

public final class OptimizationAlgorithms {
    private OptimizationAlgorithms() {}

    public static List<Activity> activitySelection(List<Activity> input) { List<Activity> sorted = new ArrayList<>(input); sorted.sort(Comparator.comparingInt(Activity::finish)); List<Activity> chosen = new ArrayList<>(); int end = Integer.MIN_VALUE; for (Activity a : sorted) if (a.start() >= end) { chosen.add(a); end = a.finish(); } return chosen; }
    public static List<TravelPackage> zeroOneKnapsack(List<TravelPackage> p, int budget) { int n = p.size(); int[][] dp = new int[n + 1][budget + 1]; for (int i = 1; i <= n; i++) for (int b = 0; b <= budget; b++) { dp[i][b] = dp[i - 1][b]; if (p.get(i - 1).cost() <= b) dp[i][b] = Math.max(dp[i][b], p.get(i - 1).value() + dp[i - 1][b - p.get(i - 1).cost()]); } List<TravelPackage> out = new ArrayList<>(); for (int i = n, b = budget; i > 0; i--) if (dp[i][b] != dp[i - 1][b]) { TravelPackage x = p.get(i - 1); out.add(x); b -= x.cost(); } Collections.reverse(out); return out; }
    public record Fraction(String name, double fraction, double cost, double value) {}
    public static List<Fraction> fractionalKnapsack(List<TravelPackage> input, double budget) { List<TravelPackage> p = new ArrayList<>(input); p.sort(Comparator.comparingDouble((TravelPackage x) -> (double)x.value() / x.cost()).reversed()); List<Fraction> out = new ArrayList<>(); for (TravelPackage x : p) { if (budget <= 0) break; double f = Math.min(1, budget / x.cost()); out.add(new Fraction(x.name(), f, x.cost() * f, x.value() * f)); budget -= x.cost() * f; } return out; }
    public static List<Integer> lis(int[] a) { if (a.length == 0) return List.of(); int[] dp = new int[a.length], prev = new int[a.length]; Arrays.fill(dp, 1); Arrays.fill(prev, -1); int best = 0; for (int i = 0; i < a.length; i++) { for (int j = 0; j < i; j++) if (a[j] < a[i] && dp[j] + 1 > dp[i]) { dp[i] = dp[j] + 1; prev[i] = j; } if (dp[i] > dp[best]) best = i; } LinkedList<Integer> out = new LinkedList<>(); for (int i = best; i != -1; i = prev[i]) out.addFirst(a[i]); return out; }

    public static void mergeSort(List<TravelPackage> a) { if (a.size() < 2) return; List<TravelPackage> temp = new ArrayList<>(Collections.nCopies(a.size(), null)); mergeSort(a, temp, 0, a.size() - 1); }
    private static void mergeSort(List<TravelPackage> a, List<TravelPackage> t, int l, int r) { if (l >= r) return; int m = (l + r) / 2; mergeSort(a, t, l, m); mergeSort(a, t, m + 1, r); int i=l,j=m+1,k=l; while(i<=m||j<=r) t.set(k++, j>r||(i<=m&&a.get(i).cost()<=a.get(j).cost())?a.get(i++):a.get(j++)); for(i=l;i<=r;i++) a.set(i,t.get(i)); }
    public static void quickSort(List<Destination> a) { quick(a, 0, a.size() - 1); }
    private static void quick(List<Destination> a, int l, int r) { if(l>=r)return; double pivot=a.get((l+r)/2).popularity(); int i=l,j=r; while(i<=j){ while(a.get(i).popularity()>pivot)i++; while(a.get(j).popularity()<pivot)j--; if(i<=j){Collections.swap(a,i++,j--);} } quick(a,l,j); quick(a,i,r); }
    public static List<Destination> topByHeap(List<Destination> input, int k) { PriorityQueue<Destination> heap = new PriorityQueue<>(Comparator.comparingDouble(Destination::popularity).reversed()); heap.addAll(input); List<Destination> out = new ArrayList<>(); while(k-- > 0 && !heap.isEmpty()) out.add(heap.remove()); return out; }
    public static void countingSort(int[] ids) { if(ids.length==0)return; int min=Arrays.stream(ids).min().orElse(0), max=Arrays.stream(ids).max().orElse(0); if((long)max-min>1_000_000) throw new IllegalArgumentException("ID range too large for counting sort"); int[] c=new int[max-min+1]; for(int x:ids)c[x-min]++; int k=0; for(int i=0;i<c.length;i++) while(c[i]-->0)ids[k++]=i+min; }
}