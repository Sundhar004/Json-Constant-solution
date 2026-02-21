import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main {

    static BigInteger convertToDecimal(String value, int base) {
        return new BigInteger(value, base);
    }

    public static void main(String[] args) throws IOException {

        String content = new String(Files.readAllBytes(Paths.get("input.json")));
        content = content.replaceAll("\\s+", "");

        // ---- FIXED k extraction ----
        int kIndex = content.indexOf("\"k\":") + 4;
        int endIndex = kIndex;

        while (Character.isDigit(content.charAt(endIndex))) {
            endIndex++;
        }

        int k = Integer.parseInt(content.substring(kIndex, endIndex));
        // --------------------------------

        Map<BigInteger, BigInteger> points = new TreeMap<>();

        // Extract numbered keys dynamically
        int index = 0;
        while (true) {
            int keyStart = content.indexOf("\"", index);
            if (keyStart == -1) break;

            int keyEnd = content.indexOf("\"", keyStart + 1);
            String keyStr = content.substring(keyStart + 1, keyEnd);

            if (!keyStr.matches("\\d+")) {
                index = keyEnd + 1;
                continue;
            }

            BigInteger x = new BigInteger(keyStr);

            int baseIndex = content.indexOf("\"base\":\"", keyEnd) + 8;
            int baseEnd = content.indexOf("\"", baseIndex);
            int base = Integer.parseInt(content.substring(baseIndex, baseEnd));

            int valueIndex = content.indexOf("\"value\":\"", baseEnd) + 9;
            int valueEnd = content.indexOf("\"", valueIndex);
            String value = content.substring(valueIndex, valueEnd);

            BigInteger y = convertToDecimal(value, base);
            points.put(x, y);

            index = valueEnd + 1;
        }

        List<BigInteger> xValues = new ArrayList<>();
        List<BigInteger> yValues = new ArrayList<>();

        int count = 0;
        for (Map.Entry<BigInteger, BigInteger> entry : points.entrySet()) {
            if (count == k) break;
            xValues.add(entry.getKey());
            yValues.add(entry.getValue());
            count++;
        }

        // Lagrange Interpolation to compute f(0)
        BigInteger result = BigInteger.ZERO;

        for (int i = 0; i < k; i++) {

            BigInteger numerator = BigInteger.ONE;
            BigInteger denominator = BigInteger.ONE;

            for (int j = 0; j < k; j++) {
                if (i != j) {
                    numerator = numerator.multiply(xValues.get(j).negate());
                    denominator = denominator.multiply(
                            xValues.get(i).subtract(xValues.get(j))
                    );
                }
            }

            BigInteger term = yValues.get(i)
                    .multiply(numerator)
                    .divide(denominator);

            result = result.add(term);
        }

        System.out.println(result);
    }
}