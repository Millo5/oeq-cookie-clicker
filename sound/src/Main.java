import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    private static volatile boolean stopRequested = false;



    public static File sample;
    public static void main(String[] args) throws InterruptedException {
        // Load guitar sound sample
        File guitarSample = new File("guitar_sample.wav");
//        guitarSample = new File("piano-e.wav");sneeze.wav"
        File sound = new File("sneeze.wav");

        AudioPlayer player = new AudioPlayer();

        String song = "D D D A G3 G F D F G C C D A G# G F D F G B B D A G# G F D F G A# A# D A G# G F D F G F F F F D D D F F G G# G F D F G F F F G G# A C A D D D A D C A A A A A";
        song = "A B # A # D E # A G";
        ArrayList<Float> notes = parseSong(song);

//        notes = new ArrayList<>();
//        notes.add(1.1f);
//        notes.add(0.8f);

//        while (true) {

//            float pitch = getRandomNumber(5, 20) / 10f;
//            for (float i = 1; i < 20; i++) {

        Thread.sleep(1000);
            notes.forEach(i -> {
                if (i != null) {
                    System.out.println("now playing: " + i);
                    player.playWithPitchShift(sound, i);
                }
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });


//        }


    }

    private static ArrayList<Float> parseSong(String song) {
        ArrayList<Float> notes = new ArrayList<>();
        Arrays.stream(song.split(" ")).forEach(note -> {
            if (note.equals("#")) {
                notes.add(null);
                return;
            }
            int noteValue = note.charAt(0) - 'A' - 4;
            float pitchShift = 1f - 0.1f * noteValue;
            notes.add(pitchShift);
        });
        return notes;
    }

    public static void playSoundNew(File file, double pitch) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
            AudioFormat format = audioInputStream.getFormat();

            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);

            if (clip.isControlSupported(FloatControl.Type.SAMPLE_RATE)) {
                FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.SAMPLE_RATE);
                control.setValue((float) pitch);
            }

//            FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.SAMPLE_RATE);
//            control.setValue((float) (control.getValue() * Math.pow(2.0, pitch / 12.0)));

            clip.start();

            while (clip.isRunning()) {
                Thread.sleep(100);
            }

            clip.close();
            audioInputStream.close();

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException | InterruptedException e) {
            throw new RuntimeException(e);
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


