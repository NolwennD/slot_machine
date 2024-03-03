package slot_machine;

import java.util.function.Function;

public class SpecialScorer implements Scorer {

  private final Function<Symbol, Score> symbolScore;
  private final Multiplicator ratio;

  public SpecialScorer(
    Function<Symbol, Score> symbolScore,
    Multiplicator ratio
  ) {
    this.symbolScore = symbolScore;
    this.ratio = ratio;
  }

  @Override
  public Score jackpot(Symbol symbol) {
    return symbolScore(symbol).multiply(ratio);
  }

  @Override
  public Score symbolScore(Symbol symbol) {
    return symbolScore.apply(symbol);
  }
}
