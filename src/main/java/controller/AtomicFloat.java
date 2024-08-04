package controller;
import java.util.concurrent.atomic.AtomicInteger;
import static java.lang.Float.*;
public class AtomicFloat extends Number {

    private final AtomicInteger bits;

    public AtomicFloat(float initialValue) {
        bits = new AtomicInteger(floatToIntBits(initialValue));
    }

    public final void set(float newValue) {
        bits.set(floatToIntBits(newValue));
    }

    public final float get() {
        return intBitsToFloat(bits.get());
    }
    @SuppressWarnings("UnusedReturnValue")
    public float addAndGet(float delta){
        set(get()+delta);
        return get();
    }

    public float floatValue() {
        return get();
    }

    public double doubleValue() {
        return floatValue();
    }

    public int intValue() {
        return (int) get();
    }

    public long longValue() {
        return (long) get();
    }

}
