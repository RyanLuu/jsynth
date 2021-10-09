import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Frequency {

    private static double VOCT_BASE = 440;  // Frequency in Hz at 0V

    public static double toHz(double vOct) {
        return VOCT_BASE * Math.pow(2, vOct);
    }

    public static double toHz(String note) {
        Pattern pattern = Pattern.compile("([A-G])([#b]?)(\\d*)");
        Matcher matcher = pattern.matcher(note);
        boolean b = matcher.find();
        assert(b == true);
        int[] noteOffsets = new int[] { 0, 2, 3, 5, 7, 8, 10 };
        int noteOffset = noteOffsets[matcher.group(1).charAt(0) - 'A'];
        if (matcher.group(2).equals("#")) {
            noteOffset++;
        } else if (matcher.group(2).equals("b")) {
            noteOffset--;
        }
        if (noteOffset >= 3) {
            noteOffset -= 12;
        }
        int octaveOffset = matcher.group(3).isEmpty() ? 0 : Integer.valueOf(matcher.group(3)) - 4;
        return 440 * Math.pow(2, octaveOffset + noteOffset / 12.);
    }

    public static double toVOct(double Hz) {
        return Math.log(Hz / VOCT_BASE) / Math.log(2);
    }

    public static double toVOct(String note) {
        return toVOct(toHz(note));
    }

}
