package io.github.rates.tools.math;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class CurrencyConvertingDecimal extends BigDecimal {

    private static int SCALE = 20;
    private static RoundingMode ROUNDING_MODE = RoundingMode.HALF_EVEN;
    private static MathContext context = new MathContext(SCALE, ROUNDING_MODE);

    public CurrencyConvertingDecimal(String val) {
        super(val, context);
    }

    public CurrencyConvertingDecimal(BigDecimal val) {
        this(val.toString());
    }

    public static CurrencyConvertingDecimal from(BigDecimal val) {
        return new CurrencyConvertingDecimal(val.toString());
    }

    public static BigDecimal valueOf(long d) {
        return scale(new CurrencyConvertingDecimal(BigDecimal.valueOf(d)));
    }

    public static BigDecimal valueOf(double d) {
        return scale(new CurrencyConvertingDecimal(BigDecimal.valueOf(d)));
    }

    private static BigDecimal scale(BigDecimal val) {
        return val.setScale(SCALE, ROUNDING_MODE);
    }

    @Override
    public BigDecimal add(BigDecimal augend) {
        return scale(new CurrencyConvertingDecimal(super.add(augend, context)));
    }

    @Override
    public BigDecimal subtract(BigDecimal subtrahend) {
        return scale(new CurrencyConvertingDecimal(super.subtract(subtrahend)));
    }

    @Override
    public BigDecimal multiply(BigDecimal multiplicand) {
        return scale(new CurrencyConvertingDecimal(super.multiply(multiplicand)));
    }

    public BigDecimal divideWithDefaultScaling(BigDecimal divisor) {
        return new CurrencyConvertingDecimal(super.divide(divisor, SCALE, ROUNDING_MODE));
    }
}
