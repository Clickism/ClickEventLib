package me.clickism.clickeventlib.statistic;

import me.clickism.clickeventlib.util.FormatUtils;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Represents a type of statistic.
 *
 * @param <T> the type of the statistic
 */
public class StatisticType<T> {

    /**
     * The integer statistic type.
     */
    public static final StatisticType<Integer> INTEGER =
            new StatisticType<>(Integer::parseInt, Integer::sum, String::valueOf);
    /**
     * The double statistic type.
     * <p>
     * Formats the double to two decimal places.
     */
    public static final StatisticType<Double> DOUBLE =
            new StatisticType<>(Double::parseDouble, Double::sum, d -> String.format("%.2f", d));
    /**
     * The long statistic type.
     */
    public static final StatisticType<Long> LONG =
            new StatisticType<>(Long::parseLong, Long::sum, String::valueOf);
    /**
     * The boolean statistic type.
     */
    public static final StatisticType<Boolean> BOOLEAN =
            new StatisticType<>(Boolean::parseBoolean, (a, b) -> a || b, String::valueOf);
    /**
     * The milliseconds statistic type. Formats the milliseconds using {@link FormatUtils#formatMillis}.
     */
    public static final StatisticType<Long> MILLISECONDS =
            new StatisticType<>(Long::parseLong, Long::sum, FormatUtils::formatMillis);

    private final Function<String, T> parser;
    private final BiFunction<T, T, T> sum;
    private final Function<T, String> formatter;

    /**
     * Creates a new statistic type with the given parser.
     *
     * @param parser    the parser
     * @param sum       the sum function
     * @param formatter the formatter
     */
    public StatisticType(Function<String, T> parser, BiFunction<T, T, T> sum, Function<T, String> formatter) {
        this.parser = parser;
        this.sum = sum;
        this.formatter = formatter;
    }

    /**
     * Sum the two given values.
     *
     * @param a the first value
     * @param b the second value
     * @return the sum of the two values
     */
    public T sum(T a, T b) {
        return sum.apply(a, b);
    }

    /**
     * Parse the given string.
     *
     * @param string the string to parse
     * @return the parsed value
     * @throws IllegalArgumentException if the string cannot be parsed
     */
    public T parse(String string) throws IllegalArgumentException {
        return parser.apply(string);
    }

    /**
     * Format the given value.
     *
     * @param value the value to format
     * @return the formatted value
     */
    public String format(T value) {
        return formatter.apply(value);
    }
}
