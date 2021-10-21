public class Time {
    public static int SAMPLE_RATE = 44100;

    public static int SECOND = SAMPLE_RATE;
    public static int MINUTE = SECOND * 60;
    public static double MILLIS = (double) SECOND / 1000;

    public static double TEMPO = 120;  // beats per minute
    public static double TEMPO_HZ = TEMPO / 60;
    public static int TIME_SIGNATURE = 4;  // beats per measure
    public static double BEAT = MINUTE / TEMPO;
    public static int MEASURES = 4;
    public static double LENGTH_IN_SAMPLES = MEASURES * TIME_SIGNATURE * BEAT;
}
