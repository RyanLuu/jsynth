public class LPF extends Filter {

    private double cutoff;

    public LPF(InputNode input, double cutoff, int width, Window window) {
        super(input, width, window);
        this.cutoff = cutoff / Time.SAMPLE_RATE;
    }

    @Override
    public void buildFilter() {
        filter = new double[width];
        double sum = 0;
        for (int i = 0; i < width; i++) {
            double x = 2 * Math.PI * cutoff * (i - (width - 1) / 2);
            double h = (x == 0) ? 1 : Math.sin(x) / x;
            double f = h * window.compute(i, width);
            filter[i] = f;
            sum += f;
        }
        for (int i = 0; i < width; i++) {
            filter[i] /= sum;
        }
    }

}

