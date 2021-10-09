import java.util.Arrays;

public abstract class Module {

    private InputNode[] inputNodes;
    private OutputNode[] outputNodes;

    public Module(int numOutputs, InputNode... inputNodes) {
        this.inputNodes = inputNodes;
        this.outputNodes = new OutputNode[numOutputs];
        Arrays.fill(this.outputNodes, new OutputNode(this));
    }

    public abstract double[] compute(double[] inputs);

    public void update(int sample) {
        double[] inputs = Arrays.stream(inputNodes)
            .map((InputNode n) -> n.update(sample))
            .mapToDouble(InputNode::get)
            .toArray();
        double[] outputs = compute(inputs);
        assert(outputs.length == outputNodes.length);
        for (int i = 0; i < outputs.length; i++) {
            outputNodes[i].set(outputs[i]);
        }
    }

    public OutputNode get() {
        assert(outputNodes.length == 1);
        return outputNodes[0];
    }

    public OutputNode get(int index) {
        return outputNodes[index];
    }

}

