package CSanScenGenerator;

import java.util.*;
import java.io.*;
import java.sql.*;

/**
 *
 * @author Peter
 */
public final class Utility {

    private Utility() {
    }
    private static Random rng = new Random();

    /**
     * Produces a random integer between lo and hi
     * @param lo
     * @param hi
     * @return
     */
    public static int randBetween(int lo, int hi) {
        if (lo > hi) {
            int t = hi;
            hi = lo;
            lo = t;
        }
        return rng.nextInt(hi - lo + 1) + lo;
    }

    /**
     * Produces a random double-precision number between lo and hi
     * @param lo
     * @param hi
     * @return
     */
    public static double randBetween(double lo, double hi) {
        if (lo > hi) {
            double t = hi;
            hi = lo;
            lo = t;
        }
        return rng.nextDouble() * (hi - lo) + lo;
    }

    /**
     * Produces a random integer between lo and hi
     * @param lo
     * @param hi
     * @return
     */
    public static int randBetween_f(double lo, double hi) {
        return randBetween((int) Math.floor(lo), (int) Math.floor(hi));
    }

    /**
     * Produce a random number following a normal distribution
     * @param mean Mean value of the distribution
     * @param var Greatest actual value skewed from the mean. The distribution will be transformed in a way that it will has
     * only ~0.3% probability of getting a value that is outside <code>mean - var</code> to <code>mean + var</code>
     * @return
     */
    public static long randGaussian(double mean, double var) {
        return Math.round(rng.nextGaussian() * (var / 3) + mean);
    }

    /**
     * Perform a random number test
     * @param prob The probability of success, 0 to 100
     * @return Whether the test is a success
     */
    public static boolean probTestPercentage(double prob) {
        return (prob / 100.0 >= rng.nextDouble());
    }

    /**
     * Cap value v within range lo and hi. If it is not in range it will return the extreme value as appropriate.
     * @param v
     * @param lo
     * @param hi
     * @return
     */
    public static int cap(int v, int lo, int hi) {
        if (v <= lo) {
            return lo;
        }
        if (v >= hi) {
            return hi;
        }
        return v;
    }

    /**
     * Randomly produces value 0 to v.length according to probability given in v
     * @param v Probability of getting each values. First value is the prob to get 0, second to get 1, and so on...
     * If all values of v sums up is less than 1, there will be prob of 1 - the sum to be v.length
     * If all values of v sums up is over 1, those over 1 are discarded.
     * @return
     */
    public static int randomCategorize(double... v) {
        float p = rng.nextFloat();
        double t = v[0];
        for (int i = 0; i < v.length; ++i) {
            if (p < t) {
                return i;
            }
            if (i + 1 < v.length) {
                t += v[i + 1];
            }
        }
        return v.length;
    }

    /**
     * Randomly produces value 0 to v.length-1 according to probability given in v
     * @param v Probability of getting each values, as a weight for every value of its respective position.
     * @return
     */
    public static int randomCategorize_i(int... v) {
        int sum = 0;
        for (int i = 0; i < v.length; ++i) {
            sum += v[i];
        }
        double[] d = new double[v.length];
        for (int i = 0; i < v.length; ++i) {
            d[i] = v[i] / (double) sum;
        }
        return randomCategorize(d);
    }
    
    /**
     * Randomly produces a number in the keys, with weights equal to corresponding value entry.
     * @param map
     * @return 
     */
    public static int randomCategorize(Map<Integer, Integer> map){
        while (true){ // some precision errors may cause the code to exit without reaching p < t. Do it infinitely.
            int randMax = 1 << 30;
            int sum = 0;
            for (Integer i : map.values()){
                sum += i;
            }

            int p = rng.nextInt(randMax);
            int t = 0;
            for (Map.Entry<Integer, Integer> i : map.entrySet()){
                t += i.getValue() / (double) sum * randMax;
                if (p < t){
                    return i.getKey();
                }
            }
        }
        
        //input map cannot accept doubles. we need a new map
        /*Map<Integer, Double> weights = new HashMap<Integer, Double>();   
        for (Map.Entry<Integer, Integer> i : map.entrySet()){
            weights.put(i.getKey(), i.getValue() / (double) sum);
        }
        
        float p = rng.nextFloat();
        double t = 0;
        for (Map.Entry<Integer, Double> i : weights.entrySet()){
            t += i.getValue();
            if (p < t){
                return i.getKey();
            }
        }*/
    }

    /**
     * Tell whether an int is in an array of ints
     * @param needle int to find
     * @param hayshack int list to find from
     * @return true if the int is in the list, false otherwise
     */
    public static boolean inArray(int needle, int[] hayshack) {
        for (int i = 0; i < hayshack.length; ++i) {
            if (needle == hayshack[i]) {
                return true;
            }
        }
        return false;
    }

    public static int[] shuffle(int[] a) {
        return shuffle(a, a.length);
    }

    /**
     * Shuffle an integer array up to element n. Every combinations are guaranteed to be equally likely to appear
     * @param a An array to be shuffled
     * @return The shuffled array
     */
    public static int[] shuffle(int[] a, int n) {
        int j, tp;
        for (int i = n - 1; i >= 0; i--) {
            j = Utility.randBetween(0, i);
            tp = a[j];
            a[j] = a[i];
            a[i] = tp;
        }
        return a;
    }

    /**
     * Get an array consists of 0 to n-1 in a random order
     * @param n
     * @return
     */
    public static int[] randomOrder(int n) {
        int[] r = new int[n];
        for (int i = 0; i < n; ++i) {
            r[i] = i;
        }
        return shuffle(r);
    }

    /**
     * Randomly pick an element from a list
     * @param <T>
     * @param c
     * @return
     */
    public static <T> T randomPick(List<T> c) {
        return c.get(Utility.randBetween(0, c.size() - 1));
    }
    
    /**
     * Randomly pick an element from an array
     * @param c
     * @return
     */
    public static int randomPick(int[] c) {
        return c[Utility.randBetween(0, c.length - 1)];
    }
    
    /**
     * Randomly pick an element from an array
     * @param c
     * @return
     */
    public static <T> T randomPick(T[] c) {
        return c[Utility.randBetween(0, c.length - 1)];
    }
    
    /**
     * Randomly pick an element from a set
     * @param <T>
     * @param c
     * @return
     */
    public static <T> T randomPick(Collection<T> c){
        int index = Utility.randBetween(0, c.size() - 1);
        int j = 0;
        for (T i : c){
            if (j == index){
                return i;
            }
            j++;
        }
        assert false;
        return null;
    }

    /**
     * Select a subset of k numbers from 0 to n-1. It is guaranteed that it is equally likely to generate all subsets,
     * and the returned list is sorted in ascending order
     * @param n Number of elements to choose
     * @param k Number of elements to be chosen into the subset
     * @return Integer array containing the subset.
     * @throws IllegalArgumentException If n &lt; k or n &lt; 0 or k &lt; 0
     */
    public static int[] intSubset(int n, int k) {
        if (n < k || n < 0 || k < 0) {
            throw new IllegalArgumentException();
        }

        //Number of elements chosen
        int chosen = 0;
        int[] result = new int[k];

        //Choose k 1's from n, forming binary containing k 1's
        if (Utility.probTestPercentage((double) k / n * 100)) {
            result[chosen] = 0;
            chosen++;
        }

        for (int j = 1; j < n; ++j) {
            if (Utility.probTestPercentage(((double) k - chosen) / (n - j) * 100)) {
                result[chosen] = j;
                chosen++;
                if (chosen == k) {
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Get a random subset of elements in the given list. All subsets are equally likely to appear.
     * @param <T>
     * @param o List to select sub elements from
     * @param k Number of elements
     * @return Subset of elements in the given list
     */
    public static <T> List<T> subset(List<T> o, int k) {
        int[] choose = intSubset(o.size(), k);

        List<T> t = new ArrayList<T>(k);
        for (int i = 0; i < k; ++i) {
            t.add(o.get(choose[i]));
        }

        return t;
    }

    /**
     * Get a random subset of elements in the given set. All subsets are equally likely to appear.
     * @param <T>
     * @param o List to select sub elements from
     * @param k Number of elements
     * @return Subset of elements in the given list
     */
    public static <T> Set<T> subset(Set<T> o, int k) {
        int[] choose = intSubset(o.size(), k);

        Set<T> t = new HashSet<T>(k);

        Iterator<T> it = o.iterator();

        int i = 0, choosei = 0;
        while (it.hasNext()) {
            T elem = it.next();
            if (choose[choosei] == i) {
                t.add(elem);
                choosei++;
                if (choosei >= choose.length) {
                    break;
                }
            }
            i++;
        }

        return t;
    }
    
    /**
     * Combine all elements in a string array to one string
     * @param str Array of objects to be combined
     * @param delimiter The delimiter to separate the strings
     * @return The combined string
     */
    public static String join(Object[] str, String delimiter){
        StringBuilder res = new StringBuilder(128);
        for (int i = 0; i < str.length; ++i) {
            res.append(i == 0 ? "" : delimiter);
            res.append(str[i]);
        }
        return res.toString();
    }
    
    /**
     * Combine all elements in a string array to one string
     * @param str Array of objects to be combined
     * @param delimiter The delimiter to separate the strings
     * @return The combined string
     */
    public static String join(int[] str, String delimiter){
        StringBuilder res = new StringBuilder(128);
        for (int i = 0; i < str.length; ++i) {
            res.append(i == 0 ? "" : delimiter).append(str[i]);
        }
        return res.toString();
    }
    
    public static String joinPoints(Collection<Point> p, String delimiter){
        StringBuilder res = new StringBuilder(256);
        boolean start = true;
        for (Point i : p){
            res.append(start ? "" : delimiter).append(i.x).append(delimiter).append(i.y);
            start = false;
        }
        return res.toString();
    }
    
    /**
     * Copy the file, byte by byte, from src to dest
     * @param src Path to source file
     * @param dest Path to destination file
     * @throws IOException 
     */
    public static void copyFile(String src, String dest) throws IOException{
        FileInputStream fis = new FileInputStream(src);
        FileOutputStream fos = new FileOutputStream(dest);

        byte[] b = new byte[4096];
        while (fis.read(b) > 0){
            fos.write(b);
        }

        fis.close();
        fos.close();
    }
    
    /**
     * Obtain IDs from a result set
     * @param rs
     * @return
     * @throws SQLException 
     */
    public static List<Integer> resultSetToId(ResultSet rs) throws SQLException{
        List<Integer> r = new ArrayList<Integer>();
        while (rs.next()){
            r.add(rs.getInt("ID"));
        }
        return r;
    }
    
    /**
     * Convert an integer list to integer array
     * @param list
     * @return 
     */
    public static int[] intListToArray(List<Integer> list){
        int[] r = new int[list.size()];
        for (int i = 0; i < list.size(); ++i){
            r[i] = list.get(i);
        }
        return r;
    }
    
    /**
     * Convert a space-separated integer strings into set of integers
     * @param s
     * @return 
     */
    public static Set<Integer> fromIntListToSet(String s){
        String[] t = s.split("\\s");
        Set<Integer> r = new HashSet<Integer>();
        if (s.isEmpty()) return r;
        for (String i : t){
            r.add(Integer.parseInt(i));
        }
        return r;
    }
    
    /**
     * Convert a space-separated integer strings into set of integers
     * @param s
     * @return 
     */
    public static List<Integer> fromIntListToList(String s){
        String[] t = s.split("\\s");
        List<Integer> r = new ArrayList<Integer>();
        if (s.isEmpty()) return r;
        for (String i : t){
            if (i.isEmpty()) continue;
            r.add(Integer.parseInt(i));
        }
        return r;
    }
    
    /**
     * Round a number fo n significant figures. See http://stackoverflow.com/questions/202302/rounding-to-an-arbitrary-number-of-significant-digits
     * @param num The value to round
     * @param n Number of s.f. required
     * @return the result
     */
    public static double roundToSignificantFigures(double num, int n) {
        if (num == 0) {
            return 0;
        }

        final double d = Math.ceil(Math.log10(num < 0 ? -num: num));
        final int power = n - (int) d;

        final double magnitude = Math.pow(10, power);
        final long shifted = Math.round(num*magnitude);
        return shifted/magnitude;
    }

}
