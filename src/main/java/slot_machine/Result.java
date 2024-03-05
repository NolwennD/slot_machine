package slot_machine;

public sealed interface Result {
  static Result jackpot(Symbol symbol) {
    return new Jackpot(symbol);
  }

  static Result pair(Symbol symbol) {
    return new Pair(symbol);
  }

  static Result fail() {
    return new Fail();
  }
}

record Jackpot(Symbol symbol) implements Result {}

record Pair(Symbol symbol) implements Result {}

record Fail() implements Result {}
