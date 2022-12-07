package dev.iwilkey.terrafort.math;

import java.util.Random;

public class PerlinNoise {
    /**
     * @param width       width of noise array
     * @param height      height of noise array
     * @param octaveCount numbers of layers used for blending noise
     * @param persistence value of impact each layer get while blending
     * @param seed        used for randomizer
     * @return float array containing calculated "Perlin-Noise" values
     */
    public static float[][] generatePerlinNoise(int width, int height, int octaveCount, float persistence, long seed) {
        final float[][] base = new float[width][height];
        final float[][] perlinNoise = new float[width][height];
        final float[][][] noiseLayers = new float[octaveCount][][];
 
        Random random = new Random(seed);
        //fill base array with random values as base for noise
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                base[x][y] = random.nextFloat();
            }
        }
 
        //calculate octaves with different roughness
        for (int octave = 0; octave < octaveCount; octave++) {
            noiseLayers[octave] = generatePerlinNoiseLayer(base, width, height, octave);
        }
 
        float amplitude = 1f;
        float totalAmplitude = 0f;
 
        //calculate perlin noise by blending each layer together with specific persistence
        for (int octave = octaveCount - 1; octave >= 0; octave--) {
            amplitude *= persistence;
            totalAmplitude += amplitude;
 
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    //adding each value of the noise layer to the noise
                    //by increasing amplitude the rougher noises will have more impact
                    perlinNoise[x][y] += noiseLayers[octave][x][y] * amplitude;
                }
            }
        }
 
        //normalize values so that they stay between 0..1
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                perlinNoise[x][y] /= totalAmplitude;
            }
        }
 
        return perlinNoise;
    }
 
    /**
     * @param base   base random float array
     * @param width  width of noise array
     * @param height height of noise array
     * @param octave current layer
     * @return float array containing calculated "Perlin-Noise-Layer" values
     */
    static float[][] generatePerlinNoiseLayer(float[][] base, int width, int height, int octave) {
        float[][] perlinNoiseLayer = new float[width][height];
 
        //calculate period (wavelength) for different shapes
        int period = 1 << octave; //2^k
        float frequency = 1f / period; // 1/2^k
 
        for (int x = 0; x < width; x++) {
            //calculates the horizontal sampling indices
            int x0 = (x / period) * period;
            int x1 = (x0 + period) % width;
            float horizintalBlend = (x - x0) * frequency;
 
            for (int y = 0; y < height; y++) {
                //calculates the vertical sampling indices
                int y0 = (y / period) * period;
                int y1 = (y0 + period) % height;
                float verticalBlend = (y - y0) * frequency;
 
                //blend top corners
                float top = interpolate(base[x0][y0], base[x1][y0], horizintalBlend);
 
                //blend bottom corners
                float bottom = interpolate(base[x0][y1], base[x1][y1], horizintalBlend);
 
                //blend top and bottom interpolation to get the final blend value for this cell
                perlinNoiseLayer[x][y] = interpolate(top, bottom, verticalBlend);
            }
        }
 
        return perlinNoiseLayer;
    }
 
    /**
     * @param a     value of point a
     * @param b     value of point b
     * @param alpha determine which value has more impact (closer to 0 -> a, closer to 1 -> b)
     * @return interpolated value
     */
    static float interpolate(float a, float b, float alpha) {
        return a * (1 - alpha) + alpha * b;
    }
 
    public static void main(String[] args) {
  
 
        /*
                float noiseValue = perlinNoise[x][y];
               */
    
    }
}