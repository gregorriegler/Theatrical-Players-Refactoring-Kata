import java.util.Arrays;
import java.util.Map;

public class Performance {

    public String playID;
    public int audience;

    public Performance(String playID, int audience) {
        this.playID = playID;
        this.audience = audience;
    }

    private Play getPlay(Map<String, Play> plays) {
        return plays.get(playID);
    }

    String getName(Map<String, Play> plays) {
        return getPlay(plays).name;
    }

    int amount(Map<String, Play> plays) {
        return getType(plays).amount(audience);
    }

    int volumeCredit(Map<String, Play> plays) {
        return getType(plays).volumeCredit(audience);
    }

    PerformanceType getType(Map<String, Play> plays) {
        return PerformanceType.of(getPlay(plays).type);
    }

    public enum PerformanceType implements AmountCalculator, VolumeCreditCalculator {
        tragedy {
            @Override
            public int amount(int audience) {
                int amount = 40000;
                if (audience > 30) {
                    amount += 1000 * (audience - 30);
                }
                return amount;
            }
        },
        comedy {
            @Override
            public int amount(int audience) {
                int amount = 30000;
                if (audience > 20) {
                    amount += 10000 + 500 * (audience - 20);
                }
                amount += 300 * audience;
                return amount;
            }

            @Override
            public int volumeCredit(int audience) {
                return (int) (Math.max(audience - 30, 0) + Math.floor(audience / 5));
            }
        };

        public static PerformanceType of(String type) {
            if (Arrays.stream(PerformanceType.values())
                .map(value -> value.name())
                .noneMatch(name -> name.equals(type))) {
                throw new Error("unknown type: ${performance.getPlay(plays).type}");
            }
            return PerformanceType.valueOf(type);
        }
    }
}
