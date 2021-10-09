public class Slew extends Module {

    private double rate;
    private double lastOutput;

    public Slew(InputNode input, double rate, double initialOutput) {
        super(1, input);
        this.rate = rate / Time.SAMPLE_RATE;
        this.lastOutput = initialOutput;
    }

    public Slew(InputNode input, double rate) {
        this(input, rate, 0);
    }

    @Override
    public double[] compute(double[] inputs) {
        if (inputs[0] > lastOutput + rate) {
            lastOutput = lastOutput + rate;
        } else if (inputs[0] < lastOutput - rate) {
            lastOutput = lastOutput - rate;
        } else {
            lastOutput = inputs[0];
        }
        return new double[] { lastOutput };
    }
}
