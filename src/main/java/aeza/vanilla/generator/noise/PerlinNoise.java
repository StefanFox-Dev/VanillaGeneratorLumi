package aeza.vanilla.generator.noise;

import cn.nukkit.math.NukkitRandom;

public class PerlinNoise extends NoiseGenerator {

    public PerlinNoise(NukkitRandom random) {
        this.offsetX = random.nextFloat() * 256.0;
        this.offsetY = random.nextFloat() * 256.0;
        this.offsetZ = random.nextFloat() * 256.0;

        for (int i = 0; i < 256; ++i) {
            perm[i] = i;
        }
        for (int i = 0; i < 256; ++i) {
            int pos = random.nextBoundedInt(256 - i) + i;
            int old = perm[i];
            perm[i] = perm[pos];
            perm[pos] = old;
            perm[i + 256] = perm[i];
        }
    }

    public double[] getNoise(double[] noise, double x, double y, double z, int sizeX, int sizeY, int sizeZ, double scaleX, double scaleY, double scaleZ, double amplitude) {
        if (sizeY == 1) {
            return get2dNoise(noise, x, z, sizeX, sizeZ, scaleX, scaleZ, amplitude);
        }
        return get3dNoise(noise, x, y, z, sizeX, sizeY, sizeZ, scaleX, scaleY, scaleZ, amplitude);
    }

    protected double[] get2dNoise(double[] noise, double x, double z, int sizeX, int sizeZ, double scaleX, double scaleZ, double amplitude) {
        int index = 0;
        if (noise == null || noise.length < sizeX * sizeZ) {
            noise = new double[sizeX * sizeZ];
        }
        for (int i = 0; i < sizeX; ++i) {
            double dx = x + offsetX + i * scaleX;
            int floorX = floor(dx);
            int ix = floorX & 255;
            dx -= floorX;
            double fx = fade(dx);
            for (int j = 0; j < sizeZ; ++j) {
                double dz = z + offsetZ + j * scaleZ;
                int floorZ = floor(dz);
                int iz = floorZ & 255;
                dz -= floorZ;
                double fz = fade(dz);

                int a = perm[ix] + 0;
                int aa = perm[a] + iz;
                int b = perm[ix + 1] + 0;
                int ba = perm[b] + iz;

                double x1 = lerp(fx, grad(perm[aa], dx, 0, dz), grad(perm[ba], dx - 1, 0, dz));
                double x2 = lerp(fx, grad(perm[aa + 1], dx, 0, dz - 1), grad(perm[ba + 1], dx - 1, 0, dz - 1));

                noise[index++] += lerp(fz, x1, x2) * amplitude;
            }
        }
        return noise;
    }

    protected double[] get3dNoise(double[] noise, double x, double y, double z, int sizeX, int sizeY, int sizeZ, double scaleX, double scaleY, double scaleZ, double amplitude) {
        int index = 0;
        int total = sizeX * sizeY * sizeZ;
        if (noise == null || noise.length < total) {
            noise = new double[total];
        }
        for (int i = 0; i < sizeX; ++i) {
            double dx = x + offsetX + i * scaleX;
            int floorX = floor(dx);
            int ix = floorX & 255;
            dx -= floorX;
            double fx = fade(dx);

            for (int j = 0; j < sizeZ; ++j) {
                double dz = z + offsetZ + j * scaleZ;
                int floorZ = floor(dz);
                int iz = floorZ & 255;
                dz -= floorZ;
                double fz = fade(dz);

                for (int k = 0; k < sizeY; ++k) {
                    double dy = y + offsetY + k * scaleY;
                    int floorY = floor(dy);
                    int iy = floorY & 255;
                    dy -= floorY;
                    double fy = fade(dy);

                    int a = perm[ix] + iy;
                    int aa = perm[a] + iz;
                    int ab = perm[a + 1] + iz;
                    int b = perm[ix + 1] + iy;
                    int ba = perm[b] + iz;
                    int bb = perm[b + 1] + iz;

                    double x1 = lerp(fx, grad(perm[aa], dx, dy, dz), grad(perm[ba], dx - 1, dy, dz));
                    double x2 = lerp(fx, grad(perm[ab], dx, dy - 1, dz), grad(perm[bb], dx - 1, dy - 1, dz));
                    double y1 = lerp(fy, x1, x2);

                    x1 = lerp(fx, grad(perm[aa + 1], dx, dy, dz - 1), grad(perm[ba + 1], dx - 1, dy, dz - 1));
                    x2 = lerp(fx, grad(perm[ab + 1], dx, dy - 1, dz - 1), grad(perm[bb + 1], dx - 1, dy - 1, dz - 1));
                    double y2 = lerp(fy, x1, x2);

                    noise[index++] += lerp(fz, y1, y2) * amplitude;
                }
            }
        }
        return noise;
    }
}
