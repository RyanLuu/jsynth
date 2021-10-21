import java.util.function.DoubleUnaryOperator;

public class Oscillator extends Module {

    public enum Shape {
        SINE(Math::sin),
        SQUARE(d -> Math.round(d / (2 * Math.PI)) * 2 - 1),
        SAWTOOTH(d -> (d - Math.PI) / Math.PI),
        TRIANGLE(d -> (d < Math.PI) ? (2 * d / Math.PI - 1) : (-2 * d / Math.PI + 3));
        private DoubleUnaryOperator shape;
        private Shape(DoubleUnaryOperator shape) {
            this.shape = shape;
        }
    }

    Shape shape;
    double angle;

    public Oscillator(Shape shape, InputNode voltPerOct, InputNode amplitude, InputNode offset) {
        super(1, voltPerOct, amplitude, offset);
        this.shape = shape;
        this.angle = 0;
    }

    public Oscillator(Shape shape, InputNode voltPerOct) {
        this(shape, voltPerOct, new ConstNode(1), new ConstNode(0));
    }

    public Oscillator(Shape shape, double voltPerOct, double amplitude, double offset) {
        this(shape, new ConstNode(voltPerOct), new ConstNode(amplitude), new ConstNode(offset));
    }

    public Oscillator(Shape shape, double voltPerOct) {
        this(shape, new ConstNode(voltPerOct), new ConstNode(1), new ConstNode(0));
    }

    @Override
    public double[] compute(double[] inputs) {
        double frequency = Frequency.toHz(inputs[0]);
        this.angle += (2 * Math.PI * frequency / Time.SAMPLE_RATE);
        this.angle %= 2 * Math.PI;
        return new double[] { this.shape.shape.applyAsDouble(this.angle) * inputs[1] + inputs[2] };
    }

}
