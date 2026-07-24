package aeza.vanilla.generator.noise;

import java.util.SplittableRandom;

public class SimplexOctaveGenerator {
    private final SimplexNoise[] octaves;

    public SimplexOctaveGenerator(SplittableRandom random, int octaves) {
        this.octaves = new SimplexNoise[octaves];
        for (int i = 0; i < octaves; ++i) {
            this.octaves[i] = new SimplexNoise(random);
        }
    }

    public double getNoise2D(double x, double z, double frequency, double amplitude) {
        double total = 0;
        double freq = frequency;
        double amp = amplitude;
        for (SimplexNoise octave : octaves) {
            total += octave.getNoise2D(x * freq, z * freq) * amp;
            freq *= 2.0;
            amp /= 2.0;
        }
        return total;
    }
}
