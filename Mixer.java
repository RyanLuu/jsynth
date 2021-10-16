import java.util.Arrays;

public class Mixer extends Module {

    private double masterGain;

    public Mixer(double masterGain, InputNode... inputs) {
        super(1, inputs);
        this.masterGain = masterGain;
    }

    public Mixer(InputNode... inputs) {
        this(1, inputs);
    }

    @Override
    public double[] compute(double[] inputs) {
        return new double[] { masterGain * Arrays.stream(inputs).sum() };
    }

}

