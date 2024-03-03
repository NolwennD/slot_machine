package slot_machine;

public record Score(int value) {
  public static final Score ZERO = new Score(0);

  public Score multiply(Multiplicator by) {
    return new Score(value * by.ratio());
  }

  public static Score of(int value) {
    return new Score(value);
  }
}
