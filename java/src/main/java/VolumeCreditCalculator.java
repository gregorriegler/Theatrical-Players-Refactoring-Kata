public interface VolumeCreditCalculator {
    default int volumeCredit(int audience) {
        return Math.max(audience - 30, 0);
    }
}
