import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class StatementPrinter {

    private final NumberFormat frmt = NumberFormat.getCurrencyInstance(Locale.US);

    public String print(Invoice invoice, Map<String, Play> plays) {
        var printData = PrintData.of(invoice, plays);
        return doPrint(printData);
    }

    private String doPrint(PrintData print) {
        var result = String.format("Statement for %s\n", print.customer);
        for (var performance : print.performances) {
            // print line for this order
            result += String.format("  %s: %s (%s seats)\n", performance.name, frmt.format(performance.amount), performance.audience);
        }
        result += String.format("Amount owed is %s\n", frmt.format(print.totalAmount));
        result += String.format("You earned %s credits\n", print.volumeCredits);
        return result;
    }

    private static class PrintData {
        public String customer;
        public List<PerformancePrintData> performances;
        public int totalAmount;
        public int volumeCredits;

        public PrintData(String customer, List<PerformancePrintData> performances, int totalAmount, int volumeCredits) {
            this.customer = customer;
            this.performances = performances;
            this.totalAmount = totalAmount;
            this.volumeCredits = volumeCredits;
        }

        private static PrintData of(Invoice invoice, Map<String, Play> plays) {
            return new PrintData(
                invoice.customer,
                invoice.performances.stream()
                    .map(performance -> PerformancePrintData.of(performance, plays))
                    .collect(Collectors.toList()),
                invoice.getTotalAmount(plays) / 100,
                invoice.getVolumeCredits(plays)
            );
        }

        private static class PerformancePrintData {
            public String name;
            public int amount;
            public int audience;

            public PerformancePrintData(String name, int amount, int audience) {
                this.name = name;
                this.amount = amount;
                this.audience = audience;
            }

            public static PerformancePrintData of(Performance performance, Map<String, Play> plays) {
                return new PerformancePrintData(
                    performance.getName(plays),
                    performance.amount(plays) / 100,
                    performance.audience);
            }
        }
    }
}
