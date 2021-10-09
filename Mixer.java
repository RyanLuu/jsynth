import java.util.Arrays;

public class Mixer extends Module {

    public Mixer(InputNode... inputs) {
        super(1, inputs);
    }

    @Override
    public double[] compute(double[] inputs) {
        return new double[] { Arrays.stream(inputs).sum() };
    }

}

