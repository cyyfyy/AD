package com.mrc.Server;

/**
 * Created by cyyfyy on 12/23/2017.
 */

public class World {

    public float[][] preSmoothHeightMap;
    public float[][] postSmoothHeightMap;
    public int[][] heightMap;

    private final int WORLDSIZE;
    private final int SEALEVEL;

    private Generator generator;

    public World(int worldSize, int seaLevel) {
        WORLDSIZE = worldSize;
        SEALEVEL = seaLevel;
        long timer = System.currentTimeMillis();
        init();
        generate();
        timer = System.currentTimeMillis() - timer;
        System.out.println("World took " + timer/1000f + " seconds to generate.");
    }

    public void init() {
        generator = new Generator(WORLDSIZE, SEALEVEL);
    }

    public void generate() {
        preSmoothHeightMap = generateHeightMap();
        postSmoothHeightMap = demFill();
        heightMap = normalize();
    }

    private float[][] generateHeightMap() {
        return generator.heightGen();
    }

    private float[][] demFill(){
        return generator.fillDEM(preSmoothHeightMap);
    }

    private int[][] normalize(){
        int[][] map = new int[WORLDSIZE][WORLDSIZE];
        for (int i = 0; i < WORLDSIZE; i++) {
            for (int j = 0; j < WORLDSIZE; j++) {
                if(postSmoothHeightMap[i][j] < 0){
                    System.out.println("Value < 0: " + postSmoothHeightMap[i][j]);
                    map[i][j] = 0;
                } else if(postSmoothHeightMap[i][j] < 0.2){
                    map[i][j] = 1;
                } else if(postSmoothHeightMap[i][j] < 0.4){
                    map[i][j] = 2;
                } else if(postSmoothHeightMap[i][j] < 0.6){
                    map[i][j] = 3;
                } else if(postSmoothHeightMap[i][j] < 0.8){
                    map[i][j] = 4;
                } else if(postSmoothHeightMap[i][j] <= 1){
                    map[i][j] = 5;
                } else {
                    System.out.println("Value > 1: " + postSmoothHeightMap[i][j]);
                    map[i][j] = 0;
                }
            }
        }
        return map;
    }
}
