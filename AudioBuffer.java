/**
 *  PCM signed 16 bit mono big-endian audio buffer
 */
public class AudioBuffer {

    private byte[] buffer;
    private int position;

    public AudioBuffer(int length) {
        assert(length % 2 == 0);
        this.buffer = new byte[length];
        this.position = 0;
    }

    public void put(short sample) {
        this.buffer[this.position + 0] = (byte) ((sample & 0xFF00) >> 8);
        this.buffer[this.position + 1] = (byte) ((sample & 0x00FF) >> 0);
        this.position += 2;
    }

    public boolean empty() {
        return this.position == 0;
    }

    public boolean full() {
        return this.position >= buffer.length;
    }

    public byte[] array() {
        return this.buffer;
    }

    public int size() {
        return this.position;
    }

    public void clear() {
        this.position = 0;
    }
}

