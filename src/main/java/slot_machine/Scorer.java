package slot_machine;

public interface Scorer {
  Score jackpot(Symbol symbol);
  Score symbolScore(Symbol symbol);
}
