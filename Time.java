public class Time {
    public static int SAMPLE_RATE = 44100;

    public static int SECOND = SAMPLE_RATE;
    public static int MINUTE = SECOND * 60;
    public static double MILLIS = (double) SECOND / 1000;

    public static int TEMPO = 120;  // beats per minute
    public static int TIME_SIGNATURE = 4;  // beats per measure
    public static double BEAT = (double) MINUTE / TEMPO;
    public static int MEASURES = 4;
    public static double LENGTH = MEASURES * TIME_SIGNATURE * BEAT;
}
