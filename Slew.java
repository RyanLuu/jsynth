public class Slew extends Module {

    public enum Type {
        RISE, FALL, BOTH
    }

    private final double rate;
    private final Type type;
    private double lastOutput;

    public Slew(InputNode input, Type type, double rate, double initialOutput) {
        super(1, input);
        this.rate = rate / Time.SAMPLE_RATE;
        this.lastOutput = initialOutput;
        this.type = type;
    }

    public Slew(InputNode input, Type type, double rate) {
        this(input, type, rate, 0);
    }

    public Slew(InputNode input, double rate, double initialOutput) {
        this(input, Type.BOTH, rate, initialOutput);
    }

    public Slew(InputNode input, double rate) {
        this(input, rate, 0);
    }

    @Override
    public double[] compute(double[] inputs) {
        if (type != Type.FALL && inputs[0] > lastOutput + rate) {
            lastOutput = lastOutput + rate;
        } else if (type != Type.RISE && inputs[0] < lastOutput - rate) {
            lastOutput = lastOutput - rate;
        } else {
            lastOutput = inputs[0];
        }
        return new double[] { lastOutput };
    }
}
