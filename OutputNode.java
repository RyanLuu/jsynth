public class OutputNode extends InputNode {

    private Module module;
    private int sample;
    
    public OutputNode(Module module) {
        this.module = module;
    }

    public void set(double value) {
        this.value = value;
    }

    @Override
    public InputNode update(int sample) {
        while (this.sample < sample) {
            module.update(this.sample++);
        }
        return this;
    }

}

