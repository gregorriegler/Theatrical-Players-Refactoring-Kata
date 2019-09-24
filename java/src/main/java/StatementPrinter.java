import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

public class StatementPrinter {

    private final NumberFormat frmt = NumberFormat.getCurrencyInstance(Locale.US);

    public String print(Invoice invoice, Map<String, Play> plays) {
        var totalAmount = 0;
        for (var perf : invoice.performances) {
            totalAmount += perf.amount(perf.play(plays));
        }

        var volumeCredits = 0;
        for (var perf : invoice.performances) {
            volumeCredits = addVolumeCredits(plays, volumeCredits, perf);
        }

        InvoiceData invoiceData = new InvoiceData();
        invoiceData.customer = invoice.customer;
        return asPrintableReport(invoiceData, invoice, plays, totalAmount, volumeCredits);
    }

    private String asPrintableReport(InvoiceData invoiceData, Invoice invoice, Map<String, Play> plays, int totalAmount, int volumeCredits) {
        StringBuilder result = new StringBuilder(String.format("Statement for %s\n", invoiceData.customer));

        for (var perf : invoice.performances) {
            String performanceName = perf.play(plays).name;
            int performanceAmount = perf.amount(perf.play(plays)) / 100;
            int audience = perf.audience;
            InvoicePerformanceData performanceData = new InvoicePerformanceData();
            performanceData.name = performanceName;
            performanceData.amount = performanceAmount;
            performanceData.audience = audience;
            result.append(String.format("  %s: %s (%s seats)\n", performanceData.name, frmt.format(performanceData.amount), audience));
        }

        result.append(String.format("Amount owed is %s\n", frmt.format(totalAmount / 100)));
        result.append(String.format("You earned %s credits\n", volumeCredits));
        return result.toString();
    }

    private int addVolumeCredits(Map<String, Play> plays, int volumeCredits, Performance perf) {
        volumeCredits += Math.max(perf.audience - 30, 0);
        // add extra credit for every ten comedy attendees
        if ("comedy".equals(perf.play(plays).type)) volumeCredits += Math.floor(perf.audience / 5);
        return volumeCredits;
    }

    private class InvoiceData {
        public String customer;
    }

    private class InvoicePerformanceData {
        public String name;
        public int amount;
        public int audience;
    }
}
