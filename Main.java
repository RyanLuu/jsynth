import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main {

    private static final String OUT_FILE = "audio.bin";

    public static void main(String[] args) {
        try {
            DataOutputStream outStream = new DataOutputStream(new FileOutputStream(OUT_FILE));
            Oscillator osc = new Oscillator(Oscillator.Type.TRIANGLE, new ConstNode(440), new ConstNode(0.1), new ConstNode(0));
            for (int i = 0; i < Time.LENGTH; i++) {
                double sample = osc.get().update(i).get();
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
