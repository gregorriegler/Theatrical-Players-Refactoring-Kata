import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TheatricalPlayers {

    private final Printer printer = new PlainTextPrinter();
    private final Printer htmlPrinter = (data) -> "<html>" + data.customer + "</html>";

    public String print(Invoice invoice, Map<String, Play> plays) {
        final InvoiceData invoiceData = getInvoiceData(invoice, plays);
        return printer.printer(invoiceData);
    }

    public InvoiceData getInvoiceData(Invoice invoice, Map<String, Play> plays) {
        int totalAmount = getTotalAmount(invoice, plays);
        int volumeCredits = getVolumeCredits(invoice, plays);
        List<LineData> lines = getLineData(invoice, plays);
        return new InvoiceData(invoice.customer, lines, totalAmount / 100, volumeCredits);
    }

    public List<LineData> getLineData(Invoice invoice, Map<String, Play> plays) {
        List<LineData> lines = new ArrayList<>();
        for (var perf : invoice.performances) {
            lines.add(new LineData(plays.get(perf.playID).name, amount(plays, perf), perf.audience));
        }
        return lines;
    }

    public int amount(Map<String, Play> plays, Performance perf) {
        return getThisAmount(perf, plays.get(perf.playID)) / 100;
    }

    public int getVolumeCredits(Invoice invoice, Map<String, Play> plays) {
        var volumeCredits = 0;
        for (var perf : invoice.performances) {
            volumeCredits += getThisCredits(perf, plays.get(perf.playID));
        }
        return volumeCredits;
    }

    public int getTotalAmount(Invoice invoice, Map<String, Play> plays) {
        var totalAmount = 0;
        for (var perf : invoice.performances) {
            totalAmount += getThisAmount(perf, plays.get(perf.playID));
        }
        return totalAmount;
    }

    public int getThisCredits(Performance perf, Play play) {
        // add volume credits
        var thisCredits = Math.max(perf.audience - 30, 0);
        // add extra credit for every ten comedy attendees
        if ("comedy".equals(play.type)) thisCredits += Math.floor((double) perf.audience / 5);
        return thisCredits;
    }

    public int getThisAmount(Performance perf, Play play) {
        var thisAmount = 0;

        switch (play.type) {
            case "tragedy":
                thisAmount = 40000;
                if (perf.audience > 30) {
                    thisAmount += 1000 * (perf.audience - 30);
                }
                break;
            case "comedy":
                thisAmount = 30000;
                if (perf.audience > 20) {
                    thisAmount += 10000 + 500 * (perf.audience - 20);
                }
                thisAmount += 300 * perf.audience;
                break;
            default:
                throw new Error("unknown type: ${play.type}");
        }
        return thisAmount;
    }

    private static class LineData {
        private final String playName;
        private final int amount;
        private final int audience;

        private LineData(String playName, int amount, int audience) {
            this.playName = playName;
            this.amount = amount;
            this.audience = audience;
        }

        public String getPlayName() {
            return playName;
        }

        public int getAmount() {
            return amount;
        }

        public int getAudience() {
            return audience;
        }
    }

    public static class InvoiceData {
        private final String customer;
        private final List<LineData> lines;
        private final int totalAmount;
        private final int volumeCredits;

        private InvoiceData(String customer, List<LineData> lines, int totalAmount, int volumeCredits) {
            this.customer = customer;
            this.lines = lines;
            this.totalAmount = totalAmount;
            this.volumeCredits = volumeCredits;
        }

        public String getCustomer() {
            return customer;
        }

        public List<LineData> getLines() {
            return lines;
        }

        public int getTotalAmount() {
            return totalAmount;
        }

        public int getVolumeCredits() {
            return volumeCredits;
        }
    }

    public static class PlainTextPrinter implements Printer {
        private NumberFormat format;

        public PlainTextPrinter() {
            this.format = NumberFormat.getCurrencyInstance(Locale.US);
        }

        @Override
        public String printer(InvoiceData invoiceData) {
            StringBuilder result = new StringBuilder(String.format("Statement for %s\n", invoiceData.getCustomer()));
            for (LineData lineData : invoiceData.getLines()) {
                result.append(printLine(lineData));
            }
            result.append(String.format("Amount owed is %s\n", format.format(invoiceData.getTotalAmount())));
            result.append(String.format("You earned %s credits\n", invoiceData.getVolumeCredits()));
            return result.toString();
        }

        public String printLine(LineData lineData) {
            return String.format("  %s: %s (%s seats)\n", lineData.getPlayName(), format.format(lineData.getAmount()), lineData.getAudience());
        }
    }
}
