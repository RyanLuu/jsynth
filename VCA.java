public class VCA extends Module {

    public VCA(InputNode input, InputNode cv) {
        super(1, input, cv);
    }

    @Override
    public double[] compute(double[] inputs) {
        return new double[] { inputs[0] * inputs[1] };
    }
}
