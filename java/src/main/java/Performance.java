import java.util.Arrays;

public class Performance {

    final String playID;
    final int audience;
    private Play play;
    private PerformanceType type;

    public Performance(String playID, int audience) {
        this.playID = playID;
        this.audience = audience;
    }

    void setPlay(Play play) {
        this.play = play;
        this.type = PerformanceType.of(play.type);
    }

    String getName() {
        return play.name;
    }

    int amount() {
        return type.amount(audience);
    }

    int volumeCredit() {
        return type.volumeCredit(audience);
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
                .map(Enum::name)
                .noneMatch(name -> name.equals(type))) {
                throw new Error("unknown type: ${performance.type}");
            }
            return PerformanceType.valueOf(type);
        }
    }
}
