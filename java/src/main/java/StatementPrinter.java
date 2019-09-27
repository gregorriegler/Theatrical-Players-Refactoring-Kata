import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

public class StatementPrinter {

    private final NumberFormat frmt = NumberFormat.getCurrencyInstance(Locale.US);

    public String print(Invoice invoice, Map<String, Play> plays) {
        var totalAmount = 0;
        var volumeCredits = 0;
        StringBuilder result = new StringBuilder(String.format("Statement for %s\n", invoice.customer));

        for (var perf : invoice.performances) {
            volumeCredits += getVolumeCredits(plays, perf);
            totalAmount += calcAmount(perf, plays.get(perf.playID));
        }

        // print line for this order
        invoice.performances.stream()
            .map(perf -> new LineData(plays.get(perf.playID).name, calcAmount(perf, plays.get(perf.playID)) / 100, perf.audience))
            .map(lineData -> String.format("  %s: %s (%s seats)\n", lineData.getPlayName(), frmt.format(lineData.getAmount()), lineData.getAudience()))
            .forEach(result::append);
        result.append(String.format("Amount owed is %s\n", frmt.format(totalAmount / 100)));
        result.append(String.format("You earned %s credits\n", volumeCredits));
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
}
