import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class AudioPlayer {

    public void play(String filename) {
        play(new File(filename));
    }
    public void play(File file) {
        Thread thread = new Thread(() -> {
            try {
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
                AudioFormat format = audioInputStream.getFormat();

                // Open audio line
                SourceDataLine line = AudioSystem.getSourceDataLine(format);
                line.open(format);
                line.start();

                // Read audio data from file and write to the line
                byte[] buffer = new byte[4096];
                int bytesRead = 0;
                while ((bytesRead = audioInputStream.read(buffer)) != -1) {
                    line.write(buffer, 0, bytesRead);
                }

                // Close the line and stream
                line.drain();
                line.close();
                audioInputStream.close();
            } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }
    public void playWithPitchShift(String filename, float pitchShift) {
        File file = new File(filename);
        playWithPitchShift(file, pitchShift);
    }
    public void playWithPitchShift(File file, float pitchShift) {
        Thread thread = new Thread(() -> {
            try {
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
                AudioFormat format = audioInputStream.getFormat();

                // Apply pitch shift
                if (pitchShift != 1.0f && format.getSampleRate() != AudioSystem.NOT_SPECIFIED) {
                    AudioFormat newFormat = new AudioFormat(format.getEncoding(), format.getSampleRate() * pitchShift,
                            format.getSampleSizeInBits(), format.getChannels(), format.getFrameSize(),
                            format.getFrameRate() * pitchShift, format.isBigEndian());
                    audioInputStream = AudioSystem.getAudioInputStream(newFormat, audioInputStream);
                }

                // Open audio line
                SourceDataLine line = AudioSystem.getSourceDataLine(format);
                line.open(format);
                line.start();

                // Read audio data from file and write to the line
                byte[] buffer = new byte[4096];
                int bytesRead = 0;
                while ((bytesRead = audioInputStream.read(buffer)) != -1) {
                    line.write(buffer, 0, bytesRead);
                }

                // Close the line and stream
                line.drain();
                line.close();
                audioInputStream.close();
            } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }
}
