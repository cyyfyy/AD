package com.mrc.Server;

import java.util.Random;

/**
 * Created by cyyfyy on 12/23/2017.
 */
class Generator {

    private final int WORLDSIZE;
    private final int SEALEVEL;

    Generator(int worldSize, int seaLevel) {
        this.WORLDSIZE = worldSize;
        this.SEALEVEL = seaLevel;
    }

    float[][] heightGen() {
        return heightGen(System.currentTimeMillis());
    }

    float[][] heightGen(long seed) {
        System.out.println("Generating height map...");
        float[][] heightMap = diamondSquare(seed);
        System.out.println("...done");
        return heightMap;
    }

    private float[][] diamondSquare(long seed) {
        float[][] map = new float[WORLDSIZE][WORLDSIZE];
        for (int i = 0; i < WORLDSIZE; i++) {
            for (int j = 0; j < WORLDSIZE; j++) {
                map[i][j] = 0;
            }
        }
        //initialize corners
        map[0][0] = map[0][WORLDSIZE - 1] = map[WORLDSIZE - 1][0] = map[WORLDSIZE - 1][WORLDSIZE - 1] = 1000f;


        float valmin = Float.MAX_VALUE;
        float valmax = Float.MIN_VALUE;
        Random rand = new Random(seed);
        float averageOffset = 1000f;

        //Begin diamond-square algorithm
        for (int squareSize = WORLDSIZE - 1; squareSize >= 2; squareSize /= 2, averageOffset /= 2) {
            int halfSquare = squareSize / 2;

            //square part of algorithm
            for (int x = 0; x < WORLDSIZE - 1; x += squareSize) {
                for (int y = 0; y < WORLDSIZE - 1; y += squareSize) {
                    //get average of the current corners
                    float avg = map[x][y] + map[x + squareSize][y] + map[x][y + squareSize] + map[x + squareSize][y + squareSize];
                    avg /= 4.0;
                    //Calculate random value in range of 2h and then subtract h so the end value is in the range (-h, +h)
                    map[x + halfSquare][y + halfSquare] = avg + (rand.nextFloat() * 2 * averageOffset) - averageOffset;

                    valmin = Math.min(valmin, map[x + halfSquare][y + halfSquare]);
                    valmax = Math.max(valmax, map[x + halfSquare][y + halfSquare]);
                }
            }

            //diamond part of the algorithm
            for (int x = 0; x < WORLDSIZE - 1; x += halfSquare) {
                for (int y = (x + halfSquare) % squareSize; y < WORLDSIZE - 1; y += squareSize) {
                    //get average of points around center using modulo so that we can wrap around the edges nicely
                    float avg = map[(x - halfSquare + WORLDSIZE - 1) % (WORLDSIZE - 1)][y] + map[(x + halfSquare) % (WORLDSIZE - 1)][y] + map[x][(y + halfSquare) % (WORLDSIZE - 1)] + map[x][(y - halfSquare + WORLDSIZE - 1) % (WORLDSIZE - 1)];
                    avg /= 4.0;
                    //range -h, +h
                    avg = avg + (rand.nextFloat() * 2 * averageOffset) - averageOffset;
                    map[x][y] = avg;

                    valmin = Math.min(valmin, avg);
                    valmax = Math.max(valmax, avg);

                    if (x == 0) map[WORLDSIZE - 1][y] = avg;
                    if (y == 0) map[x][WORLDSIZE - 1] = avg;

                }
            }
        }

        //normalize
        for (int i = 0; i < WORLDSIZE; i++) {
            for (int j = 0; j < WORLDSIZE; j++) {
                map[i][j] = (map[i][j] - valmin) / (valmax - valmin);
            }
        }
        return map;
    }

    float[][] fillDEM(float[][] argMap) {
        float[][] newh = new float[WORLDSIZE][WORLDSIZE];
        for (int i = 0; i < WORLDSIZE; i++) {
            for (int j = 0; j < WORLDSIZE; j++) {
                newh[i][j] = Float.MAX_VALUE;
            }
        }
        //fill in edges
        for (int row = 0; row < WORLDSIZE; row++) {
            newh[row][0] = argMap[row][0];
            newh[row][WORLDSIZE - 1] = argMap[row][WORLDSIZE - 1];
        }
        for (int col = 1; col < WORLDSIZE - 1; col++) {
            newh[0][col] = argMap[0][col];
            newh[WORLDSIZE - 1][col] = argMap[WORLDSIZE - 1][col];
        }
        while (true) {
            boolean changed = false;
            float epsilon = (float) 1e-5;
            for (int p = 0; p < WORLDSIZE; p++) {
                for (int q = 0; q < WORLDSIZE; q++) {
                    if (newh[p][q] == argMap[p][q]) continue;
                    for (int x = -1; x <= 1; x++) {
                        for (int y = -1; y <= 1; y++) {
                            int xn = p + x;
                            int yn = q + y;
                            //check bounds
                            if (xn < 0 || yn < 0 || xn > WORLDSIZE - 1 || yn > WORLDSIZE - 1) {
                                continue;
                            }
                            //don't check ourselves
                            else if (x == 0 && y == 0) {
                                continue;
                            } else {
                                float oh = newh[xn][yn] + epsilon;
                                if (argMap[p][q] >= oh) {
                                    newh[p][q] = argMap[p][q];
                                    changed = true;
                                    break;
                                }
                                if (newh[p][q] > oh && oh > argMap[p][q]) {
                                    newh[p][q] = oh;
                                    changed = true;
                                }
                            }
                        }
                    }
                }
            }
            if (!changed) {
                return newh;
            }
        }
    }
}
