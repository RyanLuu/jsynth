import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main {

    private static final String OUT_FILE = "audio.bin";

    public static void main(String[] args) {
        try {
            DataOutputStream outStream = new DataOutputStream(new FileOutputStream(OUT_FILE));
            Module clock = new Oscillator(Oscillator.Type.SQUARE, Frequency.toVOct(Time.TEMPO_HZ));
            Module seq = new Sequencer(clock.get(), new double[] {Frequency.toVOct("G2"), Frequency.toVOct("Bb2"), Frequency.toVOct("A2"), Frequency.toVOct("Bb2")});
            Module slew = new Slew(seq.get(), 2, Frequency.toVOct("G2"));
            Module bass = new Oscillator(Oscillator.Type.SQUARE, slew.get(), new ConstNode(0.08), new ConstNode(0));
            Module pad0 = new Oscillator(Oscillator.Type.SINE, Frequency.toVOct("G4"), 0.1, 0);
            Module pad1 = new Oscillator(Oscillator.Type.SINE, Frequency.toVOct("A4"), 0.1, 0);
            Module pad2 = new Oscillator(Oscillator.Type.SINE, Frequency.toVOct("D5"), 0.1, 0);
            Module noise = new Noise();
            Module lpf = new LPF(noise.get(), 500, 301, Filter.Window.RECTANGULAR);
            Module saw = new Oscillator(Oscillator.Type.SAWTOOTH, Frequency.toVOct(Time.TEMPO_HZ), -0.5, 0.5);
            Module vca = new VCA(lpf.get(), saw.get());
            Module mix = new Mixer(0.5, bass.get(), pad0.get(), pad1.get(), pad2.get(), vca.get());
            Module rev = new Reverb(mix.get(), Reverb.Type.FREEVERB, 1, 1, 0.5);
            for (int i = 0; i < Time.LENGTH; i++) {
                double sample = rev.get().update(i).get();
                outStream.writeDouble(sample);
            }
            outStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Play audio using ffmpeg
        String[] command = new String[] {
            "ffplay",
            "-showmode", "waves",
            "-f", String.format("f%dbe", Double.SIZE),
            "-ar", String.valueOf(Time.SAMPLE_RATE),
            OUT_FILE
        };
        ProcessBuilder builder = new ProcessBuilder(command);
        builder.inheritIO();
        try {
            Process process = builder.start();
            process.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
