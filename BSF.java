public class BSF extends Filter {

    private double left;
    private double right;

    public BSF(InputNode input, double center, double bandwidth, int width, Window window) {
        super(input, width, window);
        this.left = (center - bandwidth / 2) / Time.SAMPLE_RATE;
        this.right = (center + bandwidth / 2) / Time.SAMPLE_RATE;
    }

    @Override
    public void buildFilter() {
        double[] lpf = new double[width];
        {
            double sum = 0;
            for (int i = 0; i < width; i++) {
                double x = 2 * Math.PI * left * (i - (width - 1) / 2);
                double h = (x == 0) ? 1 : Math.sin(x) / x;
                double f = h * window.compute(i, width);
                lpf[i] = f;
                sum += f;
            }
            for (int i = 0; i < width; i++) {
                lpf[i] /= sum;
            }
        }

        double[] hpf = new double[width];
        {
            double sum = 0;
            for (int i = 0; i < width; i++) {
                double x = 2 * Math.PI * right * (i - (width - 1) / 2);
                double h = (x == 0) ? 1 : Math.sin(x) / x;
                double f = h * window.compute(i, width);
                hpf[i] = f;
                sum += f;
            }
            for (int i = 0; i < width; i++) {
                hpf[i] /= sum;
                hpf[i] *= -1;
            }
            hpf[(width - 1) / 2]++;
        }

        filter = new double[width];
        for (int i = 0; i < width; i++) {
            filter[i] = lpf[i] + hpf[i];
        }
    }

}

