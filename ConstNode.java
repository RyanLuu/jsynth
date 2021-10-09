public class ConstNode extends InputNode {

    public ConstNode(double k) {
        this.value = k;
    }

    public InputNode update(int sample) {
        return this;
    }

}
