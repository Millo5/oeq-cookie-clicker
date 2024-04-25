import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class Main {
    private static volatile boolean stopRequested = false;

    public static void main(String[] args) {
        // Load guitar sound sample
        File guitarSample = new File("guitar_sample.wav");

        // Play the guitar sound with varying pitch
        while (true) {
            int input = getRandomNumber(18, 42);
            double pitchShift = input * 10; // Adjust pitch based on input relative to 30
            playSound(guitarSample, pitchShift);
        }
    }

    public static void playSound(File file, double pitchShift) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
            AudioFormat format = audioInputStream.getFormat();
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();

            int bufferSize = (int) format.getSampleRate() * format.getFrameSize();
            byte[] buffer = new byte[bufferSize];
            int bytesRead;
            while (!stopRequested && (bytesRead = audioInputStream.read(buffer)) != -1) {
                for (int i = 0; i < bytesRead; i += format.getFrameSize()) {
                    for (int channel = 0; channel < format.getChannels(); channel++) {
                        double pitchShiftFactor = Math.pow(2.0, pitchShift / 12.0); // Calculate pitch shift factor
                        int originalSample = buffer[i + channel] & 0xFF;
                        int shiftedSample = (int) (originalSample * pitchShiftFactor);
                        buffer[i + channel] = (byte) Math.min(shiftedSample, 255);
                    }
                }
                line.write(buffer, 0, bytesRead);
                // Adjust pitch shift for next iteration
                int input = getRandomNumber(18, 42);
                pitchShift = input * 10; // Adjust pitch based on input relative to 30
            }

            line.drain();
            line.close();
            audioInputStream.close();
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            e.printStackTrace();
        }
    }

    public static int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
}


