public class BPF extends Filter {

    private double left;
    private double right;
    private int halfWidth;

    public BPF(InputNode input, double center, double bandwidth, int width, Window window) {
        super(input, width, window);
        this.left = (center - bandwidth / 2) / Time.SAMPLE_RATE;
        this.right = (center + bandwidth / 2) / Time.SAMPLE_RATE;
        this.halfWidth = width / 2 + 1;
    }

    @Override
    public void buildFilter() {
        double[] lpf = new double[halfWidth];
        {
            double sum = 0;
            for (int i = 0; i < halfWidth; i++) {
                double x = 2 * Math.PI * right * (i - (halfWidth - 1) / 2);
                double h = (x == 0) ? 1 : Math.sin(x) / x;
                double f = h * window.compute(i, halfWidth);
                lpf[i] = f;
                sum += f;
            }
            for (int i = 0; i < halfWidth; i++) {
                lpf[i] /= sum;
            }
        }

        double[] hpf = new double[halfWidth];
        {
            double sum = 0;
            for (int i = 0; i < halfWidth; i++) {
                double x = 2 * Math.PI * left * (i - (halfWidth - 1) / 2);
                double h = (x == 0) ? 1 : Math.sin(x) / x;
                double f = h * window.compute(i, halfWidth);
                hpf[i] = f;
                sum += f;
            }
            for (int i = 0; i < halfWidth; i++) {
                hpf[i] /= sum;
                hpf[i] *= -1;
            }
            hpf[(halfWidth - 1) / 2]++;
        }

        filter = new double[width];
        for (int i = 0; i < halfWidth; i++) {
            for (int j = 0; j < halfWidth; j++) {
                filter[i + j] += lpf[i] * hpf[j];
            }
        }
    }

}

