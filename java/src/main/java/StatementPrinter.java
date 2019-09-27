import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class StatementPrinter {

    private final NumberFormat frmt = NumberFormat.getCurrencyInstance(Locale.US);

    public String print(Invoice invoice, Map<String, Play> plays) {
        PerformanceData performanceData = createPerformanceData(invoice, plays);
        return doPrint(performanceData);
    }

    private PerformanceData createPerformanceData(Invoice invoice, Map<String, Play> plays) {
        var totalAmount = 0;
        var volumeCredits = 0;

        for (var perf : invoice.performances) {
            volumeCredits += getVolumeCredits(plays, perf);
            totalAmount += calcAmount(perf, plays.get(perf.playID));
        }

        List<LineData> lineDataList = invoice.performances.stream()
            .map(perf -> new LineData(plays.get(perf.playID).name, calcAmount(perf, plays.get(perf.playID)) / 100, perf.audience))
            .collect(Collectors.toList());

        return new PerformanceData(invoice.customer, lineDataList, volumeCredits, totalAmount);
    }

    private String doPrint(PerformanceData performanceData) {
        StringBuilder result = new StringBuilder(String.format("Statement for %s\n", performanceData.getCustomer()));
        performanceData.getLineDataList().stream()
            .map(lineData -> String.format("  %s: %s (%s seats)\n", lineData.getPlayName(), frmt.format(lineData.getAmount()), lineData.getAudience()))
            .forEach(result::append);
        result.append(String.format("Amount owed is %s\n", frmt.format(performanceData.getTotalAmount() / 100)));
        result.append(String.format("You earned %s credits\n", performanceData.getVolumeCredits()));
        return result.toString();
    }

    private int getVolumeCredits(Map<String, Play> plays, Performance perf) {
        // add volume credits
        int volumeCredits1 = 0;
        volumeCredits1 += Math.max(perf.audience - 30, 0);
        // add extra credit for every ten comedy attendees
        if ("comedy".equals(plays.get(perf.playID).type)) volumeCredits1 += Math.floor(perf.audience / 5);
        return volumeCredits1;
    }

    private int calcAmount(Performance perf, Play play) {
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

    private static class PerformanceData {
        private final String customer;
        private final List<LineData> lineDataList;
        private final int volumeCredits;
        private final int totalAmount;

        private PerformanceData(String customer, List<LineData> lineDataList, int volumeCredits, int totalAmount) {
            this.customer = customer;
            this.lineDataList = lineDataList;
            this.volumeCredits = volumeCredits;
            this.totalAmount = totalAmount;
        }

        public String getCustomer() {
            return customer;
        }

        public List<LineData> getLineDataList() {
            return lineDataList;
        }

        public int getVolumeCredits() {
            return volumeCredits;
        }

        public int getTotalAmount() {
            return totalAmount;
        }
    }
}
