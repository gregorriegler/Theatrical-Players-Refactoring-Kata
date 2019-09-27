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
            int thisAmount = calcAmount(perf, plays.get(perf.playID));
            volumeCredits = addVolumeCredits(plays, volumeCredits, perf);

            // print line for this order
            LineData lineData = new LineData(plays.get(perf.playID).name, thisAmount / 100, perf.audience);
            result.append(String.format("  %s: %s (%s seats)\n", lineData.getPlayName(), frmt.format(lineData.getAmount()), lineData.getAudience()));
            totalAmount += thisAmount;
        }
        result.append(String.format("Amount owed is %s\n", frmt.format(totalAmount / 100)));
        result.append(String.format("You earned %s credits\n", volumeCredits));
        return result.toString();
    }

    private int addVolumeCredits(Map<String, Play> plays, int volumeCredits, Performance perf) {
        // add volume credits
        volumeCredits += Math.max(perf.audience - 30, 0);
        // add extra credit for every ten comedy attendees
        if ("comedy".equals(plays.get(perf.playID).type)) volumeCredits += Math.floor(perf.audience / 5);
        return volumeCredits;
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
