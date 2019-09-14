import java.util.Map;

public class Performance {

    public String playID;
    public int audience;

    public Performance(String playID, int audience) {
        this.playID = playID;
        this.audience = audience;
    }

    Play getPlay(Map<String, Play> plays) {
        return plays.get(playID);
    }

    String getType(Map<String, Play> plays) {
        return getPlay(plays).type;
    }

    String getName(Map<String, Play> plays) {
        return getPlay(plays).name;
    }

    int amount(Map<String, Play> plays) {
        var thisAmount = 0;

        switch (getType(plays)) {
            case "tragedy":
                thisAmount = 40000;
                if (audience > 30) {
                    thisAmount += 1000 * (audience - 30);
                }
                return thisAmount;
            case "comedy":
                thisAmount = 30000;
                if (audience > 20) {
                    thisAmount += 10000 + 500 * (audience - 20);
                }
                thisAmount += 300 * audience;
                return thisAmount;
            default:
                throw new Error("unknown type: ${performance.getPlay(plays).type}");
        }
    }

    int volumeCreditsToAdd(Map<String, Play> plays) {
        var volumeCreditsToAdd = 0;
        volumeCreditsToAdd += Math.max(audience - 30, 0);
        // add extra credit for every ten comedy attendees
        if ("comedy".equals(getType(plays))) volumeCreditsToAdd += Math.floor(audience / 5);
        return volumeCreditsToAdd;
    }
}
