import java.util.function.DoubleUnaryOperator;

public class Oscillator extends Module {

    public enum Type {
        SINE(Math::sin),
        SQUARE(d -> Math.round(d / (2 * Math.PI)) * 2 - 1),
        SAWTOOTH(d -> (d - Math.PI) / Math.PI),
        TRIANGLE(d -> (d < Math.PI) ? (2 * d / Math.PI - 1) : (-2 * d / Math.PI + 3));
        private DoubleUnaryOperator shape;
        private Type(DoubleUnaryOperator shape) {
            this.shape = shape;
        }
    }

    Type type;
    double angle;

    public Oscillator(Type type, InputNode voltPerOct, InputNode amplitude, InputNode offset) {
        super(1, voltPerOct, amplitude, offset);
        this.type = type;
        this.angle = 0;
    }

    public Oscillator(Type type, InputNode voltPerOct) {
        this(type, voltPerOct, new ConstNode(1), new ConstNode(0));
    }

    public Oscillator(Type type, double voltPerOct, double amplitude, double offset) {
        this(type, new ConstNode(voltPerOct), new ConstNode(amplitude), new ConstNode(offset));
    }

    public Oscillator(Type type, double voltPerOct) {
        this(type, new ConstNode(voltPerOct), new ConstNode(1), new ConstNode(0));
    }

    @Override
    public double[] compute(double[] inputs) {
        double frequency = Frequency.toHz(inputs[0]);
        this.angle += (2 * Math.PI * frequency / Time.SAMPLE_RATE);
        this.angle %= 2 * Math.PI;
        return new double[] { this.type.shape.applyAsDouble(this.angle) * inputs[1] + inputs[2] };
    }

}
