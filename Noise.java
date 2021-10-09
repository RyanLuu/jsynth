import java.util.Random;

public class Noise extends Module {

    private Random rand;

    public Noise() {
        super(1);
        this.rand = new Random();
    }

    @Override
    public double[] compute(double[] inputs) {
        return new double[] { this.rand.nextGaussian() };
    }
}
