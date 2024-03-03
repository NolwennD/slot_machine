package slot_machine;

public record Multiplicator(int ratio) {
  public static Multiplicator of(int ratio) {
    return new Multiplicator(ratio);
  }
}
