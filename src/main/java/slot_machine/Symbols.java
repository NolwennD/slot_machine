package slot_machine;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Symbols {

  private static final Predicate<Entry<Symbol, Long>> IS_JACKPOT = isJackpot();
  private static final Predicate<Entry<Symbol, Long>> HAS_DOUBLE_SYMBOLS =
    hasDoubleSymbol();

  private final Optional<Result> jackpot;
  private final Optional<Result> doubleSymbols;

  private Symbols(SymbolsBuilder builder) {
    Roll roll = buildRoll(builder);

    jackpot = roll.jackpot();
    doubleSymbols = roll.doubleS();
  }

  private Roll buildRoll(SymbolsBuilder builder) {
    return groupSymbolByNumber(builder).entrySet()
    .stream()
    .collect(Collectors.teeing(
      Collectors.filtering(IS_JACKPOT, Collectors.reducing((a, __) -> a)),
      Collectors.filtering(HAS_DOUBLE_SYMBOLS, Collectors.reducing((a, __) -> a)),
      (jackpotEntry, doubleEntry) -> new Roll(jackpotEntry.map(r -> Result.jackopt(r.getKey())), doubleEntry.map(r -> Result.doubleSymbol(r.getKey()))))
      );
  }

  private Map<Symbol, Long> groupSymbolByNumber(SymbolsBuilder builder) {
    return Stream.of(builder.first, builder.second, builder.third).collect(
      Collectors.groupingBy(Function.identity(), Collectors.counting())
      );
  }

  private record Roll(Optional<Result> jackpot, Optional<Result> doubleS) {}

  public Result result() {
    return jackpot.or(() -> doubleSymbols).orElse(Result.fail());
  }

  private static Predicate<Entry<Symbol, Long>> hasDoubleSymbol() {
    return entry -> entry.getValue() == 2;
  }

  private static Predicate<Entry<Symbol, Long>> isJackpot() {
    return entry -> entry.getValue() == 3;
  }

  public static SymbolsBuilder first(Symbol first) {
    return new SymbolsBuilder(first);
  }

  public static class SymbolsBuilder
    implements SymbolsSecondBuilder, SymbolsThirdBuilder {

    private final Symbol first;
    private Symbol second;
    private Symbol third;

    public SymbolsBuilder(Symbol first) {
      this.first = first;
    }

    @Override
    public SymbolsThirdBuilder second(Symbol second) {
      this.second = second;

      return this;
    }

    @Override
    public Symbols third(Symbol third) {
      this.third = third;

      return new Symbols(this);
    }
  }

  interface SymbolsSecondBuilder {
    SymbolsThirdBuilder second(Symbol second);
  }

  interface SymbolsThirdBuilder {
    Symbols third(Symbol third);
  }
}
