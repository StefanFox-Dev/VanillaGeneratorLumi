package aeza.vanilla.generator.noise;

import cn.nukkit.math.NukkitRandom;

public class SimplexNoise extends NoiseGenerator {
    protected static final double SQRT_3 = Math.sqrt(3.0);
    protected static final double F2 = 0.5 * (SQRT_3 - 1.0);
    protected static final double G2 = (3.0 - SQRT_3) / 6.0;
    protected static final double F3 = 1.0 / 3.0;
    protected static final double G3 = 1.0 / 6.0;

    protected static final int[][] GRAD_3 = {
            {1, 1, 0}, {-1, 1, 0}, {1, -1, 0}, {-1, -1, 0},
            {1, 0, 1}, {-1, 0, 1}, {1, 0, -1}, {-1, 0, -1},
            {0, 1, 1}, {0, -1, 1}, {0, 1, -1}, {0, -1, -1}
    };

    public SimplexNoise(NukkitRandom random) {
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

    public double getNoise2D(double xin, double yin) {
        double s = (xin + yin) * F2;
        int i = floor(xin + s);
        int j = floor(yin + s);
        double t = (i + j) * G2;
        double X0 = i - t;
        double Y0 = j - t;
        double x0 = xin - X0;
        double y0 = yin - Y0;

        int i1, j1;
        if (x0 > y0) {
            i1 = 1; j1 = 0;
        } else {
            i1 = 0; j1 = 1;
        }

        double x1 = x0 - i1 + G2;
        double y1 = y0 - j1 + G2;
        double x2 = x0 - 1.0 + 2.0 * G2;
        double y2 = y0 - 1.0 + 2.0 * G2;

        int ii = i & 255;
        int jj = j & 255;
        int gi0 = perm[ii + perm[jj]] % 12;
        int gi1 = perm[ii + i1 + perm[jj + j1]] % 12;
        int gi2 = perm[ii + 1 + perm[jj + 1]] % 12;

        double n0, n1, n2;
        double t0 = 0.5 - x0 * x0 - y0 * y0;
        if (t0 < 0) n0 = 0.0;
        else {
            t0 *= t0;
            n0 = t0 * t0 * (GRAD_3[gi0][0] * x0 + GRAD_3[gi0][1] * y0);
        }

        double t1 = 0.5 - x1 * x1 - y1 * y1;
        if (t1 < 0) n1 = 0.0;
        else {
            t1 *= t1;
            n1 = t1 * t1 * (GRAD_3[gi1][0] * x1 + GRAD_3[gi1][1] * y1);
        }

        double t2 = 0.5 - x2 * x2 - y2 * y2;
        if (t2 < 0) n2 = 0.0;
        else {
            t2 *= t2;
            n2 = t2 * t2 * (GRAD_3[gi2][0] * x2 + GRAD_3[gi2][1] * y2);
        }

        return 70.0 * (n0 + n1 + n2);
    }
}
