package com.thenexusreborn.nexuscore.util;

import com.sun.javafx.css.CalculatedValue;

/**
 * An enum to represent basic mathematical operations and performs that operation.
 * This is mainly used with some abstraction based on saving a type of thing and then when loading, just performs it instead of having to account for it
 */
public enum Operator {
    ADD() {
        public Number calculate(Number number1, Number number2) {
            if (number1 instanceof Integer && number2 instanceof Integer) {
                return number1.intValue() + number2.intValue();
            } else if (number1 instanceof Integer && number2 instanceof Double) {
                return number1.intValue() + number2.doubleValue();
            } else if (number1 instanceof Double && number2 instanceof Integer) {
                return number1.doubleValue() + number2.intValue();
            } else if (number1 instanceof Double && number2 instanceof Double) {
                return number1.doubleValue() + number2.doubleValue();
            }

            return 0;
        }
    }, SUBTRACT() {
        public Number calculate(Number number1, Number number2) {
            if (number1 instanceof Integer && number2 instanceof Integer) {
                return number1.intValue() - number2.intValue();
            } else if (number1 instanceof Integer && number2 instanceof Double) {
                return number1.intValue() - number2.doubleValue();
            } else if (number1 instanceof Double && number2 instanceof Integer) {
                return number1.doubleValue() - number2.intValue();
            } else if (number1 instanceof Double && number2 instanceof Double) {
                return number1.doubleValue() - number2.doubleValue();
            }

            return 0;
        }
    }, MULTIPLY() {
        public Number calculate(Number number1, Number number2) {
            if (number1 instanceof Integer && number2 instanceof Integer) {
                return number1.intValue() * number2.intValue();
            } else if (number1 instanceof Integer && number2 instanceof Double) {
                return number1.intValue() * number2.doubleValue();
            } else if (number1 instanceof Double && number2 instanceof Integer) {
                return number1.doubleValue() * number2.intValue();
            } else if (number1 instanceof Double && number2 instanceof Double) {
                return number1.doubleValue() * number2.doubleValue();
            }

            return 0;
        }
    }, DIVIDE() {
        public Number calculate(Number number1, Number number2) {
            try {
                if (number1 instanceof Integer && number2 instanceof Integer) {
                    return number1.intValue() / number2.intValue();
                } else if (number1 instanceof Integer && number2 instanceof Double) {
                    return number1.intValue() / number2.doubleValue();
                } else if (number1 instanceof Double && number2 instanceof Integer) {
                    return number1.doubleValue() / number2.intValue();
                } else if (number1 instanceof Double && number2 instanceof Double) {
                    return number1.doubleValue() / number2.doubleValue();
                }
            } catch (Exception e) {}
            return 0;
        }
    };

    Operator() {}
    
    /**
     * Calculates the two numbers together
     * @param number1 The first number to perform the operation on
     * @param number2 The second number in which the operation is used with
     * @return The result or 0 by default
     */
    public Number calculate(Number number1, Number number2) {
        return 0;
    }
}
