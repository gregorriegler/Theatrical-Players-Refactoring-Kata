import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

public class StatementPrinter {

    public String print(Invoice invoice, Map<String, Play> plays) {
        var totalAmount = 0;
        for (var performance : invoice.performances) {
            totalAmount += performance.amount(plays);
        }

        var volumeCredits = 0;
        for (var performance : invoice.performances) {
            volumeCredits = calculateVolumeCredits(plays, volumeCredits, performance);
        }

        var result = String.format("Statement for %s\n", invoice.customer);
        NumberFormat frmt = NumberFormat.getCurrencyInstance(Locale.US);
        for (var performance : invoice.performances) {
            // print line for this order
            result += String.format("  %s: %s (%s seats)\n", performance.getName(plays), frmt.format(performance.amount(plays) / 100), performance.audience);
        }
        result += String.format("Amount owed is %s\n", frmt.format(totalAmount / 100));
        result += String.format("You earned %s credits\n", volumeCredits);
        return result;
    }

    private int calculateVolumeCredits(Map<String, Play> plays, int volumeCredits, Performance performance) {
        // add volume credits
        volumeCredits += Math.max(performance.audience - 30, 0);
        // add extra credit for every ten comedy attendees
        if ("comedy".equals(performance.getType(plays))) volumeCredits += Math.floor(performance.audience / 5);
        return volumeCredits;
    }

}
