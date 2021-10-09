public abstract class Filter extends Module {

    public interface Window {
        public static final Window RECTANGULAR = (i, width) -> 1;
        public static final Window BLACKMAN = (i, width) -> 0.42 - 0.5 * Math.cos(2 * Math.PI * i / (width - 1)) + 0.08 * Math.cos(4 * Math.PI * i / (width - 1));

        double compute(int i, int width);
    }

    protected int width;
    private double[] x;
    protected double[] filter;
    protected Window window;

    public Filter(InputNode input, int width, Window window) {
        super(1, input);
        assert(width % 2 == 1);
        this.width = width;
        this.x = new double[width];
        this.filter = null;
        this.window = window;
    }

    protected abstract void buildFilter();

    @Override
    public double[] compute(double[] inputs) {
        // lazy load filter
        if (this.filter == null) {
            buildFilter();
        }
        double output = 0;
        for (int i = 1; i < width; i++) {
            x[i - 1] = x[i];
            output += x[i - 1] * filter[i - 1];
        }
        x[width - 1] = inputs[0];
        output += x[width - 1] * filter[width - 1];
        return new double[] { output };
    }

}

