import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class StatementPrinter {

    private final NumberFormat frmt = NumberFormat.getCurrencyInstance(Locale.US);

    public String print(Invoice invoice, Map<String, Play> plays) {
        InvoiceData invoiceData = createInvoiceData(invoice, plays);
        return asPrintableReport(invoiceData);
    }

    private InvoiceData createInvoiceData(Invoice invoice, Map<String, Play> plays) {
        InvoiceData invoiceData = new InvoiceData();
        invoiceData.customer = invoice.customer;
        invoiceData.performances = invoice.performances
            .stream()
            .map(perf -> InvoicePerformanceData.create(plays, perf))
            .collect(Collectors.toList());
        invoiceData.totalAmount = invoice.calcTotal(plays) / 100;
        invoiceData.volumeCredits = invoice.calcVolumeCredits(plays);
        return invoiceData;
    }

    private String asPrintableReport(InvoiceData invoiceData) {
        StringBuilder result = new StringBuilder(String.format("Statement for %s\n", invoiceData.customer));

        for (var performanceData : invoiceData.performances) {
            result.append(String.format("  %s: %s (%s seats)\n", performanceData.name, frmt.format(performanceData.amount), performanceData.audience));
        }

        result.append(String.format("Amount owed is %s\n", frmt.format(invoiceData.totalAmount)));
        result.append(String.format("You earned %s credits\n", invoiceData.volumeCredits));
        return result.toString();
    }

    private class InvoiceData {
        public String customer;
        public List<InvoicePerformanceData> performances;
        public int totalAmount;
        public int volumeCredits;
    }

    private static class InvoicePerformanceData {
        public String name;
        public int amount;
        public int audience;

        private static InvoicePerformanceData create(Map<String, Play> plays, Performance perf) {
            InvoicePerformanceData performanceData = new InvoicePerformanceData();
            performanceData.name = perf.play(plays).name;
            performanceData.amount = perf.amount(perf.play(plays)) / 100;
            performanceData.audience = perf.audience;
            return performanceData;
        }
    }
}
