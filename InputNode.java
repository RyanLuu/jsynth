public abstract class InputNode {

    protected double value;

    public double get() {
        return value;
    }

    public abstract InputNode update(int sample);

}
