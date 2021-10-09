public class Sequencer extends Module {

    private double[] sequence;
    private int position;
    private boolean clockState;
    private boolean risingEdge;

    public Sequencer(InputNode clock, double[] sequence, boolean risingEdge) {
        super(1, clock);
        this.sequence = sequence;
        this.position = 0;
        this.clockState = risingEdge;
        this.risingEdge = risingEdge;
    }

    public Sequencer(InputNode clock, double[] sequence) {
        this(clock, sequence, false);
    }

    @Override
    public double[] compute(double[] inputs) {
        if (!clockState && inputs[0] > 0) {
            clockState = true;
            if (risingEdge) {
                position = (position + 1) % sequence.length;
            }
        } else if (clockState && inputs[0] <= 0) {
            clockState = false;
            if (!risingEdge) {
                position = (position + 1) % sequence.length;
            }
        }
        return new double[] { sequence[position] };
    }

}
