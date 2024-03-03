package slot_machine;

public sealed interface Result {
  static Result jackopt(Symbol symbol) {
    return new Jackpot(symbol);
  }

  static Result doubleSymbol(Symbol symbol) {
    return new DoubleSymbol(symbol);
  }

  static Result fail() {
    return new Fail();
  }
}

record Jackpot(Symbol symbol) implements Result {}

record DoubleSymbol(Symbol symbo) implements Result {}

record Fail() implements Result {}
