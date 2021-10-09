import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main {

    private static final String OUT_FILE = "audio.bin";

    public static void main(String[] args) {
        try {
            DataOutputStream outStream = new DataOutputStream(new FileOutputStream(OUT_FILE));
            Module pitch = new Oscillator(Oscillator.Type.SAWTOOTH, 0.5, 500, 500);
            Module osc = new Oscillator(Oscillator.Type.SINE, pitch.get(), new ConstNode(0.2), new ConstNode(0));
            Module filt = new BSF(osc.get(), 500, 300, 801, Filter.Window.BLACKMAN);
            for (int i = 0; i < Time.LENGTH; i++) {
                double sample = filt.get().update(i).get();
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
