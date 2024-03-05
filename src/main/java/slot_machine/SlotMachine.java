package slot_machine;

public class SlotMachine {

  private final Scorer scorer;

  public SlotMachine(Scorer scorer) {
    this.scorer = scorer;
  }

  public SlotMachine() {
    this(new DefaultScorer());
  }

  public Score score(Symbols symbols) {
    return switch (symbols.result()) {
      case Jackpot(Symbol s) -> scorer.jackpot(s);
      case Pair(Symbol s) -> scorer.symbolScore(s);
      case Fail __ -> Score.ZERO;
    };
  }

  private static class DefaultScorer implements Scorer {

    @Override
    public Score symbolScore(Symbol symbol) {
      return switch (symbol) {
        case CHERRY -> Score.of(1);
        case LEMON -> Score.of(2);
        case APPLE -> Score.of(3);
      };
    }

    @Override
    public Score jackpot(Symbol symbol) {
      return symbolScore(symbol).multiply(Multiplicator.of(10));
    }
  }
}
