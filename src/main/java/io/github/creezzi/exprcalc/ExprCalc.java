package io.github.creezzi.exprcalc;

import java.util.Map;

/** Supports + - * / % ^, unary minus, parentheses, variables and a few functions. */
public final class ExprCalc {
    private final String src;
    private final Map<String, Double> vars;
    private int pos;

    private ExprCalc(String src, Map<String, Double> vars) {
        this.src = src;
        this.vars = vars;
    }

    public static double evaluate(String expression) {
        return evaluate(expression, Map.of());
    }

    public static double evaluate(String expression, Map<String, Double> variables) {
        ExprCalc p = new ExprCalc(expression, variables);
        double result = p.expr();
        p.ws();
        if (p.pos < expression.length()) {
            throw p.error("unexpected '" + expression.charAt(p.pos) + "'");
        }
        return result;
    }

    private IllegalArgumentException error(String message) {
        return new IllegalArgumentException(message + " at position " + pos);
    }

    private void ws() {
        while (pos < src.length() && Character.isWhitespace(src.charAt(pos))) {
            pos++;
        }
    }

    private boolean eat(char c) {
        ws();
        if (pos < src.length() && src.charAt(pos) == c) {
            pos++;
            return true;
        }
        return false;
    }

    private double expr() {
        double v = term();
        while (true) {
            if (eat('+')) {
                v += term();
            } else if (eat('-')) {
                v -= term();
            } else {
                return v;
            }
        }
    }

    private double term() {
        double v = unary();
        while (true) {
            if (eat('*')) {
                v *= unary();
            } else if (eat('/')) {
                double d = unary();
                if (d == 0) {
                    throw error("division by zero");
                }
                v /= d;
            } else if (eat('%')) {
                double d = unary();
                if (d == 0) {
                    throw error("division by zero");
                }
                v %= d;
            } else {
                return v;
            }
        }
    }

    private double unary() {
        if (eat('-')) {
            return -unary();
        }
        if (eat('+')) {
            return unary();
        }
        return power();
    }

    private double power() {
        double base = atom();
        if (eat('^')) {
            return Math.pow(base, unary());
        }
        return base;
    }

    private double atom() {
        ws();
        if (eat('(')) {
            double v = expr();
            if (!eat(')')) {
                throw error("expected ')'");
            }
            return v;
        }
        int start = pos;
        if (pos < src.length() && (Character.isDigit(src.charAt(pos)) || src.charAt(pos) == '.')) {
            while (pos < src.length() && (Character.isDigit(src.charAt(pos)) || src.charAt(pos) == '.')) {
                pos++;
            }
            try {
                return Double.parseDouble(src.substring(start, pos));
            } catch (NumberFormatException e) {
                pos = start;
                throw error("invalid number");
            }
        }
        if (pos < src.length() && Character.isLetter(src.charAt(pos))) {
            while (pos < src.length() && (Character.isLetterOrDigit(src.charAt(pos)) || src.charAt(pos) == '_')) {
                pos++;
            }
            String name = src.substring(start, pos);
            if (eat('(')) {
                return call(name);
            }
            if (vars.containsKey(name)) {
                return vars.get(name);
            }
            if (name.equals("pi")) {
                return Math.PI;
            }
            if (name.equals("e")) {
                return Math.E;
            }
            pos = start;
            throw error("unknown variable '" + name + "'");
        }
        throw error(pos >= src.length() ? "unexpected end of input" : "unexpected '" + src.charAt(pos) + "'");
    }

    private double call(String name) {
        java.util.List<Double> args = new java.util.ArrayList<>();
        if (!eat(')')) {
            do {
                args.add(expr());
            } while (eat(','));
            if (!eat(')')) {
                throw error("expected ')'");
            }
        }
        switch (name) {
            case "sqrt": return one(name, args, Math.sqrt(args.isEmpty() ? 0 : args.get(0)));
            case "abs": return one(name, args, Math.abs(args.isEmpty() ? 0 : args.get(0)));
            case "floor": return one(name, args, Math.floor(args.isEmpty() ? 0 : args.get(0)));
            case "ceil": return one(name, args, Math.ceil(args.isEmpty() ? 0 : args.get(0)));
            case "min": return args.stream().mapToDouble(d -> d).min().orElseThrow(() -> error("min needs arguments"));
            case "max": return args.stream().mapToDouble(d -> d).max().orElseThrow(() -> error("max needs arguments"));
            default: throw error("unknown function '" + name + "'");
        }
    }

    private double one(String name, java.util.List<Double> args, double result) {
        if (args.size() != 1) {
            throw error(name + " takes exactly one argument");
        }
        return result;
    }
}
