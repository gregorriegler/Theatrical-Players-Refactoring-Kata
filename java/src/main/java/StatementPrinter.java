import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class StatementPrinter {

    private final NumberFormat frmt = NumberFormat.getCurrencyInstance(Locale.US);

    public String print(Invoice invoice) {
        var printData = PrintData.of(invoice);
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
        final String customer;
        final List<PerformancePrintData> performances;
        final int totalAmount;
        final int volumeCredits;

        PrintData(String customer, List<PerformancePrintData> performances, int totalAmount, int volumeCredits) {
            this.customer = customer;
            this.performances = performances;
            this.totalAmount = totalAmount;
            this.volumeCredits = volumeCredits;
        }

        private static PrintData of(Invoice invoice) {
            return new PrintData(
                invoice.customer,
                invoice.performances.stream()
                    .map(PerformancePrintData::of)
                    .collect(Collectors.toList()),
                invoice.getTotalAmount() / 100,
                invoice.getVolumeCredits()
            );
        }

        private static class PerformancePrintData {
            final String name;
            final int amount;
            final int audience;

            PerformancePrintData(String name, int amount, int audience) {
                this.name = name;
                this.amount = amount;
                this.audience = audience;
            }

            static PerformancePrintData of(Performance performance) {
                return new PerformancePrintData(
                    performance.getName(),
                    performance.amount() / 100,
                    performance.audience);
            }
        }
    }
}
