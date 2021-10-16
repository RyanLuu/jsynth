import java.util.Arrays;

/**
 *  SATREV and JCREV Schroeder reverberators
 */
public class Reverb extends Module {

    public enum Type {
        SATREV, JCREV, FREEVERB
    }

    private final Filter filter;
    private final double dry, wet;

    public Reverb(InputNode input, Type type, double size, double decay, double wet) {
        super(1, input);
        assert(0 <= wet && wet <= 1);
        assert(0 <= size);
        assert(0 <= decay);
        this.dry = 1 - wet;
        this.wet = wet;
        if (wet == 0) {
            this.filter = new Gain(1);
        } else {
            this.filter = buildFilter(type, size, decay);
        }
    }

    public Reverb(InputNode input, Type type) {
        this(input, type, 1, 1, 0.5);
    }

    public Reverb(InputNode input) {
        this(input, Type.SATREV);
    }

    private Filter buildFilter(Type type, double size, double decay) {
        if (size == 0) {
            return new Gain(1);
        } else {
            // decay in units of time instead of reflections
            decay /= size;
            switch (type) {
                case SATREV: {
                    size *= Time.SAMPLE_RATE / 25000;
                    return new Series(
                        new Gain(0.015 * 2),
                        new Parallel(
                            new FBCF(0.827 * decay, (int) (778  * size)),
                            new FBCF(0.805 * decay, (int) (901  * size)),
                            new FBCF(0.783 * decay, (int) (1011 * size)),
                            new FBCF(0.764 * decay, (int) (1123 * size))
                        ),
                        new AP(0.7 * decay, (int) (125 * size)),
                        new AP(0.7 * decay, (int) (42  * size)),
                        new AP(0.7 * decay, (int) (12  * size))
                    );
                }
                case JCREV: {
                    size *= Time.SAMPLE_RATE / 44100;
                    return new Series(
                        new Gain(0.015 * 2),
                        new AP(0.7 * decay, (int) (1051 * size)),
                        new AP(0.7 * decay, (int) (337  * size)),
                        new AP(0.7 * decay, (int) (113  * size)),
                        new Parallel(
                            new FFCF(0.742 * decay, (int) (4799 * size)),
                            new FFCF(0.733 * decay, (int) (4999 * size)),
                            new FFCF(0.715 * decay, (int) (5399 * size)),
                            new FFCF(0.697 * decay, (int) (5801 * size))
                        )
                    );
                }
                case FREEVERB: {
                    size *= Time.SAMPLE_RATE / 44100;
                    return new Series(
                        new Gain(0.015 * 2),
                        new Parallel(
                            new LBCF(0.84 * decay, 0.2, (int) (1116 * size)),
                            new LBCF(0.84 * decay, 0.2, (int) (1188 * size)),
                            new LBCF(0.84 * decay, 0.2, (int) (1277 * size)),
                            new LBCF(0.84 * decay, 0.2, (int) (1356 * size)),
                            new LBCF(0.84 * decay, 0.2, (int) (1422 * size)),
                            new LBCF(0.84 * decay, 0.2, (int) (1491 * size)),
                            new LBCF(0.84 * decay, 0.2, (int) (1557 * size)),
                            new LBCF(0.84 * decay, 0.2, (int) (1617 * size))
                        ),
                        new APA(0.5 * decay, (int) (556 * size)),
                        new APA(0.5 * decay, (int) (441 * size)),
                        new APA(0.5 * decay, (int) (341 * size)),
                        new APA(0.5 * decay, (int) (225 * size))
                    );
                }
                default:
                    return new Gain(1);
            }
        }
    }

    @Override
    public double[] compute(double[] inputs) {
        return new double[]{ dry * inputs[0] + wet * filter.compute(inputs[0]) };
    }
    

    private abstract class Filter {
        public abstract double compute(double input);
    }

    private abstract class BufferedFilter extends Filter {
        protected double[] buffer;
        protected int index;
        protected BufferedFilter(int bufferSize) {
            this.buffer = new double[bufferSize];
            this.index = 0;
        }
        protected void advanceIndex() {
            this.index = (this.index + 1) % buffer.length;
        }
    }

    private class Gain extends Filter {
        private double g;
        public Gain(double g) {
            this.g = g;
        }
        public double compute(double input) {
            return g * input;
        }        
    }

    private class Series extends Filter {
        private Filter[] filters;
        public Series(Filter... filters) {
            this.filters = filters;
        }
        public double compute(double input) {
            return Arrays.stream(filters).reduce(input, (x, f) -> f.compute(x), (x, y) -> y);
        }
    }

    private class Parallel extends Filter {
        private Filter[] filters;
        public Parallel(Filter... filters) {
            this.filters = filters;
        }
        public double compute(double input) {
            return Arrays.stream(filters).mapToDouble(f -> f.compute(input)).sum();
        }
    }

    /*
     *  Schroeder Allpass Section
     *  https://ccrma.stanford.edu/~jos/pasp/Schroeder_Allpass_Sections.html
     */
    private class AP extends BufferedFilter {
        private final double gain;
        public AP(double gain, int delay) {
            super(delay);
            this.gain = gain;
        }
        public double compute(double input) {
            double prev = buffer[index];
            buffer[index] = input - gain * prev;
            advanceIndex();
            return prev + gain * buffer[index];
        }
    }

    /*
     *  Freeverb Allpass Approximation
     *  https://ccrma.stanford.edu/%7Ejos/pasp/Freeverb_Allpass_Approximation.html
     */
    private class APA extends BufferedFilter {
        private final double feedback;
        public APA(double feedback, int delay) {
            super(delay);
            this.feedback = feedback;
        }
        public double compute(double input) {
            double prev = buffer[index];
            buffer[index] = input + feedback * prev;
            advanceIndex();
            return prev - input;
        }
    }

    /*
     *  Feedforward Comb Filter
     *  https://ccrma.stanford.edu/~jos/pasp/Feedforward_Comb_Filters.html
     */
    private class FFCF extends BufferedFilter {
        private final double gain;
        public FFCF(double gain, int delay) {
            super(delay);
            this.gain = gain;
        }
        public double compute(double input) {
            double output = buffer[index] * gain + input;
            buffer[index] = input;
            advanceIndex();
            return output;
        }
    }

    /*
     *  Feedback Comb Filter
     *  https://ccrma.stanford.edu/~jos/pasp/Feedback_Comb_Filters.html
     */
    private class FBCF extends BufferedFilter {
        private final double gain;
        public FBCF(double gain, int delay) {
            super(delay);
            this.gain = gain;
        }
        public double compute(double input) {
            buffer[index] = input - buffer[index] * gain;
            advanceIndex();
            return buffer[index];
        }
    }

    /*
     *  Lowpass Feedback Comb Filter
     *  https://ccrma.stanford.edu/~jos/pasp/Lowpass_Feedback_Comb_Filter.html
     */
    private class LBCF extends BufferedFilter {
        private final double feedback, damping;
        private double filterStore;
        public LBCF(double feedback, double damping, int delay) {
            super(delay);
            this.feedback = feedback;
            this.damping = damping;
            this.filterStore = 0;
        }
        public double compute(double input) {
            double prev = buffer[index];
            filterStore = (prev * (1 - damping)) + (filterStore * damping);
            buffer[index] = input + feedback * filterStore;
            advanceIndex();
            return prev;
        }
    }
}

