import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

public class StatementPrinter {

    public String print(Invoice invoice, Map<String, Play> plays) {
        var result = String.format("Statement for %s\n", invoice.customer);
        NumberFormat frmt = NumberFormat.getCurrencyInstance(Locale.US);
        for (var performance : invoice.performances) {
            // print line for this order
            result += String.format("  %s: %s (%s seats)\n", performance.getName(plays), frmt.format(performance.amount(plays) / 100), performance.audience);
        }
        result += String.format("Amount owed is %s\n", frmt.format(invoice.getTotalAmount(plays) / 100));
        result += String.format("You earned %s credits\n", invoice.getVolumeCredits(plays));
        return result;
    }

}
