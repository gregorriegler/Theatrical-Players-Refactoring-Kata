import java.util.List;
import java.util.Map;

public class Invoice {

    public String customer;
    public List<Performance> performances;

    public Invoice(String customer, List<Performance> performances) {
        this.customer = customer;
        this.performances = performances;
    }

    int calcTotal(Map<String, Play> plays) {
        return performances.stream()
            .mapToInt(perf -> perf.amount(perf.play(plays)))
            .sum();
    }

    int calcVolumeCredits(Map<String, Play> plays) {
        var volumeCredits = 0;
        for (var perf : performances) {
            volumeCredits = perf.addVolumeCredits(plays, volumeCredits);
        }
        return volumeCredits;
    }
}
