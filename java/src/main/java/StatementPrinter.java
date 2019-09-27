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
            var play = plays.get(perf.playID);
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

            // add volume credits
            volumeCredits += Math.max(perf.audience - 30, 0);
            // add extra credit for every ten comedy attendees
            if ("comedy".equals(play.type)) volumeCredits += Math.floor(perf.audience / 5);

            // print line for this order
            printLine(result, new LineData(play.name, thisAmount / 100, perf.audience));
            totalAmount += thisAmount;
        }
        result.append(String.format("Amount owed is %s\n", frmt.format(totalAmount / 100)));
        result.append(String.format("You earned %s credits\n", volumeCredits));
        return result.toString();
    }

    private void printLine(StringBuilder result, LineData lineData) {
        result.append(String.format("  %s: %s (%s seats)\n", lineData.getPlayName(), frmt.format(lineData.getAmount()), lineData.getAudience()));
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
