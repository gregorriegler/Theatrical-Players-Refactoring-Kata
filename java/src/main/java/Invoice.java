import java.util.List;
import java.util.Map;

public class Invoice {

    public String customer;
    public List<Performance> performances;

    public Invoice(String customer, List<Performance> performances) {
        this.customer = customer;
        this.performances = performances;
    }

    int getTotalAmount(Map<String, Play> plays) {
        return performances.stream().mapToInt(performance -> performance.amount(plays)).sum();
    }

    int getVolumeCredits(Map<String, Play> plays) {
        return performances.stream().mapToInt(performance -> performance.volumeCreditsToAdd(plays)).sum();
    }

}
