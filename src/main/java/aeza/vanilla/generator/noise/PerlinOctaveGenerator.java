package aeza.vanilla.generator.noise;

import java.util.Arrays;
import java.util.SplittableRandom;

public class PerlinOctaveGenerator {
    private final PerlinNoise[] octaves;

    public PerlinOctaveGenerator(SplittableRandom random, int octaves) {
        this.octaves = new PerlinNoise[octaves];
        for (int i = 0; i < octaves; ++i) {
            this.octaves[i] = new PerlinNoise(random);
        }
    }

    public double[] getNoise(double[] noise, double x, double y, double z, int sizeX, int sizeY, int sizeZ, double scaleX, double scaleY, double scaleZ) {
        int total = sizeX * sizeY * sizeZ;
        if (noise == null || noise.length < total) {
            noise = new double[total];
        } else {
            Arrays.fill(noise, 0, total, 0.0);
        }
        double frequency = 1.0;
        double amplitude = 1.0;
        for (PerlinNoise octave : octaves) {
            noise = octave.getNoise(noise, x, y, z, sizeX, sizeY, sizeZ, scaleX * frequency, scaleY * frequency, scaleZ * frequency, amplitude);
            frequency *= 2.0;
            amplitude /= 2.0;
        }
        return noise;
    }
}
