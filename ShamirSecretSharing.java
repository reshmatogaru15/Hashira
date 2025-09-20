import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class ShamirSecretSharing {

    public static void main(String[] args) throws Exception {
        // Read JSON file as lines
        List<String> lines = Files.readAllLines(Paths.get("input.json"));

        int n = 0, k = 0;
        List<long[]> points = new ArrayList<>();
        String key = null, base = null, value = null;

        for (String line : lines) {
            line = line.trim();

            // Extract n and k
            if (line.contains("\"n\"")) {
                n = Integer.parseInt(line.replaceAll(".*\"n\"\\s*:\\s*(\\d+).*", "$1"));
            } else if (line.contains("\"k\"")) {
                k = Integer.parseInt(line.replaceAll(".*\"k\"\\s*:\\s*(\\d+).*", "$1"));
            }

            // Extract points
            if (line.matches("\"\\d+\": \\{")) {
                key = line.replaceAll("\"(\\d+)\":.*", "$1");
            } else if (line.contains("\"base\"")) {
                base = line.replaceAll(".*\"base\"\\s*:\\s*\"(\\d+)\".*", "$1");
            } else if (line.contains("\"value\"")) {
                value = line.replaceAll(".*\"value\"\\s*:\\s*\"([^\"]+)\".*", "$1");

                if (key != null && base != null && value != null) {
                    int b = Integer.parseInt(base);
                    BigInteger yBig = new BigInteger(value, b);
                    long y = yBig.longValue();
                    long x = Long.parseLong(key);

                    points.add(new long[]{x, y});
                    key = base = value = null;
                }
            }
        }

        // Sort points by x
        points.sort(Comparator.comparingLong(a -> a[0]));

        // Take first k points
        List<long[]> subset = points.subList(0, k);

        long secret = lagrangeInterpolationAtZero(subset);
        System.out.println("Recovered Secret (f(0)) = " + secret);
    }

    private static long lagrangeInterpolationAtZero(List<long[]> points) {
        double result = 0.0;
        int k = points.size();

        for (int i = 0; i < k; i++) {
            double xi = points.get(i)[0];
            double yi = points.get(i)[1];

            double term = yi;
            for (int j = 0; j < k; j++) {
                if (i == j) continue;
                double xj = points.get(j)[0];
                term *= (0 - xj) / (xi - xj);
            }
            result += term;
        }

        return Math.round(result);
    }
}
