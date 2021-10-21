import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class Main {

    private static OutputNode synth() {
        double freqV = Frequency.toVOct("F3");
        double freqIV = Frequency.toVOct("Eb3");
        double freqiii = Frequency.toVOct("Db3");
        double freqI = Frequency.toVOct("Bb2");
        Module clock = new Oscillator(Oscillator.Shape.SQUARE, Frequency.toVOct(Time.TEMPO_HZ * 2));
        Module ampl = new Oscillator(Oscillator.Shape.SAWTOOTH, Frequency.toVOct(Time.TEMPO_HZ / 3), -0.02, 0.06);
        Module seq = new Sequencer(clock.get(), new double[] {freqV, freqV, freqI, freqI, freqiii, freqIV});
        Module slew = new Slew(seq.get(), Slew.Type.FALL, 5, freqV);
        Module bass = new Oscillator(Oscillator.Shape.SQUARE, slew.get(), ampl.get(), new ConstNode(0));
        Module pad2 = new Oscillator(Oscillator.Shape.SINE, freqV + 1, 0.1, 0);
        Module pad0 = new Oscillator(Oscillator.Shape.SINE, freqI + 2, 0.1, 0);
        Module pad1 = new Oscillator(Oscillator.Shape.SINE, freqiii + 2, 0.1, 0);
        Module noise = new Noise();
        Module lpf = new LPF(noise.get(), 400, 301, Filter.Window.RECTANGULAR);
        Module saw = new Oscillator(Oscillator.Shape.SAWTOOTH, Frequency.toVOct(Time.TEMPO_HZ), -0.7, 0.7);
        Module vca = new VCA(lpf.get(), saw.get());
        Module mix = new Mixer(0.5, bass.get(), pad0.get(), pad1.get(), pad2.get(), vca.get());
        Module rev = new Reverb(mix.get(), Reverb.Type.FREEVERB, 1, 1, 0.5);
        return rev.get();
    }

    public static void main(String[] args) {
        AudioFormat format = new AudioFormat(Time.SAMPLE_RATE, 16, 1, true, true);
        try {
            SourceDataLine line = AudioSystem.getSourceDataLine(format);

            AudioBuffer buffer = new AudioBuffer(line.getBufferSize());
            OutputNode out = synth();
            line.open(format);
            line.start();
            for (int i = 0; i < Time.LENGTH_IN_SAMPLES; i++) {
                double sample = out.update(i).get();
                short sampleShort = (short) (sample * Short.MAX_VALUE);
                buffer.put(sampleShort);
                if (buffer.full()) {
                    byte[] b = buffer.array();
                    line.write(b, 0, buffer.size());
                    buffer.clear();
                }
            }
            if (!buffer.empty()) {
                byte[] b = buffer.array();
                line.write(b, 0, buffer.size());
            }
            line.drain();
            line.stop();
            line.close();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
        }
    }
}
