package com.soniccandle.analysis;

/** FFT radix-2 iterativa. Evita una dependencia nativa o externa para el núcleo. */
public final class FastFourierTransform {

    private FastFourierTransform() {
    }

    public static void transform(double[] real, double[] imaginary) {
        int n = real.length;
        if (n != imaginary.length || Integer.bitCount(n) != 1) {
            throw new IllegalArgumentException("La FFT requiere dos arreglos del mismo tamaño y potencia de dos.");
        }

        for (int i = 1, j = 0; i < n; i++) {
            int bit = n >> 1;
            while ((j & bit) != 0) {
                j ^= bit;
                bit >>= 1;
            }
            j ^= bit;
            if (i < j) {
                double temp = real[i];
                real[i] = real[j];
                real[j] = temp;
                temp = imaginary[i];
                imaginary[i] = imaginary[j];
                imaginary[j] = temp;
            }
        }

        for (int length = 2; length <= n; length <<= 1) {
            double angle = -2.0 * Math.PI / length;
            double stepReal = Math.cos(angle);
            double stepImaginary = Math.sin(angle);
            int half = length >>> 1;

            for (int offset = 0; offset < n; offset += length) {
                double twiddleReal = 1.0;
                double twiddleImaginary = 0.0;
                for (int k = 0; k < half; k++) {
                    int even = offset + k;
                    int odd = even + half;
                    double oddReal = real[odd] * twiddleReal - imaginary[odd] * twiddleImaginary;
                    double oddImaginary = real[odd] * twiddleImaginary + imaginary[odd] * twiddleReal;

                    real[odd] = real[even] - oddReal;
                    imaginary[odd] = imaginary[even] - oddImaginary;
                    real[even] += oddReal;
                    imaginary[even] += oddImaginary;

                    double nextReal = twiddleReal * stepReal - twiddleImaginary * stepImaginary;
                    twiddleImaginary = twiddleReal * stepImaginary + twiddleImaginary * stepReal;
                    twiddleReal = nextReal;
                }
            }
        }
    }
}
