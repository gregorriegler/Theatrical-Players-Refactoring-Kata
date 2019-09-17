import java.util.List;
import java.util.Map;

public class Invoice {

    final String customer;
    final List<Performance> performances;

    public Invoice(String customer, List<Performance> performances, Map<String, Play> plays) {
        this.customer = customer;
        this.performances = performances;
        for (Performance performance : this.performances) {
            performance.setPlay(plays.get(performance.playID));
        }
    }

    int getTotalAmount() {
        return performances.stream().mapToInt(Performance::amount).sum();
    }

    int getVolumeCredits() {
        return performances.stream().mapToInt(Performance::volumeCredit).sum();
    }

}
