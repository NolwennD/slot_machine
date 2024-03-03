package slot_machine;

import static org.assertj.core.api.Assertions.*;

import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class SlotMachineTest {

  private final SlotMachine machine = new SlotMachine();

  @Test
  void shouldScoreZeroWithThreeDifferentsSymbol() {
    var score = machine.score(
      symbols(Symbol.LEMON, Symbol.CHERRY, Symbol.APPLE)
    );

    assertThat(score).isEqualTo(Score.ZERO);
  }

  @ParameterizedTest
  @MethodSource("jackpotProvider")
  void shouldScoreTenTimesSymbolValueWhenJackpot(Symbol symbol, int value) {
    assertThat(machine.score(symbols(symbol, symbol, symbol))).isEqualTo(
      new Score(value)
    );
  }

  private Symbols symbols(Symbol firs, Symbol second, Symbol third) {
    return Symbols.first(firs).second(second).third(third);
  }

  private static Stream<Arguments> jackpotProvider() {
    return Stream.of(
      Arguments.of(Symbol.CHERRY, 10),
      Arguments.of(Symbol.LEMON, 20),
      Arguments.of(Symbol.APPLE, 30)
    );
  }

  @ParameterizedTest
  @MethodSource("doubleProvider")
  void shouldScoreSymbolValueWhenWithTwoIdentical(
    Symbol main,
    Symbol other,
    int value
  ) {
    assertThat(machine.score(symbols(main, main, other))).isEqualTo(
      new Score(value)
    );
  }

  private static Stream<Arguments> doubleProvider() {
    return Stream.of(
      Arguments.of(Symbol.CHERRY, Symbol.LEMON, 1),
      Arguments.of(Symbol.LEMON, Symbol.APPLE, 2),
      Arguments.of(Symbol.APPLE, Symbol.LEMON, 3)
    );
  }

  @ParameterizedTest
  @MethodSource("specialEventJackpotProvider")
  void shouldScoreTwentyTimesSymbolValueWhenJackpotAndSpecialEvent(
    Symbol symbol,
    int value
  ) {
    var machine = new SlotMachine(
      new SpecialScorer(specialEventScore(), Multiplicator.of(20))
    );

    assertThat(machine.score(symbols(symbol, symbol, symbol))).isEqualTo(
      new Score(value)
    );
  }

  private Function<Symbol, Score> specialEventScore() {
    return s ->
      switch (s) {
        case CHERRY -> Score.of(2);
        case LEMON -> Score.of(4);
        case APPLE -> Score.of(6);
      };
  }

  private static Stream<Arguments> specialEventJackpotProvider() {
    return Stream.of(
      Arguments.of(Symbol.CHERRY, 40),
      Arguments.of(Symbol.LEMON, 80),
      Arguments.of(Symbol.APPLE, 120)
    );
  }

  @ParameterizedTest
  @MethodSource("specialEventDoubleProvider")
  void shouldScoreSymbolTwoTimesValueWhenWithTwoIdenticalWhenSpecialEvent(
    Symbol main,
    Symbol other,
    int value
  ) {
    var machine = new SlotMachine(
      new SpecialScorer(specialEventScore(), Multiplicator.of(20))
    );

    assertThat(machine.score(symbols(main, main, other))).isEqualTo(
      new Score(value)
    );
  }

  private static Stream<Arguments> specialEventDoubleProvider() {
    return Stream.of(
      Arguments.of(Symbol.CHERRY, Symbol.LEMON, 2),
      Arguments.of(Symbol.LEMON, Symbol.APPLE, 4),
      Arguments.of(Symbol.APPLE, Symbol.LEMON, 6)
    );
  }
}
