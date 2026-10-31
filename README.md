# exprcalc

Evaluate arithmetic expressions with variables and functions using a recursive descent parser.

## Usage

```java
double v = ExprCalc.evaluate("2 * (x + 3) ^ 2", Map.of("x", 1.0));  // 32.0
```

## Test

```bash
mvn test
```

## License

MIT
