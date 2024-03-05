package slot_machine;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Symbols {

  private static final Predicate<Entry<Symbol, Long>> IS_JACKPOT = isJackpot();
  private static final Predicate<Entry<Symbol, Long>> HAS_PAIR = hasPair();

  private final Result result;

  private Symbols(SymbolsBuilder builder) {
    Roll roll = buildRoll(builder);

    result = roll.jackpot().or(roll::pair).orElseGet(Result::fail);
  }

  private Roll buildRoll(SymbolsBuilder builder) {
    return groupSymbolByNumber(builder)
      .entrySet()
      .stream()
      .collect(
        Collectors.teeing(
          toResult(IS_JACKPOT, entry -> Result.jackpot(entry.getKey())),
          toResult(HAS_PAIR, entry -> Result.pair(entry.getKey())),
          Roll::new
        )
      );
  }

  private Collector<Entry<Symbol, Long>, ?, Optional<Result>> toResult(
    Predicate<Entry<Symbol, Long>> combination,
    Function<Entry<Symbol, Long>, Result> mapper
  ) {
    return Collectors.filtering(
      combination,
      Collectors.mapping(mapper, Collectors.reducing((a, __) -> a))
    );
  }

  private Map<Symbol, Long> groupSymbolByNumber(SymbolsBuilder builder) {
    return Stream.of(builder.first, builder.second, builder.third).collect(
      Collectors.groupingBy(Function.identity(), Collectors.counting())
    );
  }

  private record Roll(Optional<Result> jackpot, Optional<Result> pair) {}

  public Result result() {
    return result;
  }

  private static Predicate<Entry<Symbol, Long>> hasPair() {
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
