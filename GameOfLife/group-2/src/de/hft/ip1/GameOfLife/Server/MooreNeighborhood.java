package de.hft.ip1.GameOfLife.Server;

public final class MooreNeighborhood {

    public static final int NW = 1;
    public static final int N = 2;
    public static final int NE = 3;
    public static final int W = 4;
    public static final int C = 5;
    public static final int E = 6;
    public static final int SW = 7;
    public static final int S = 8;
    public static final int SE = 9;

    /**
     * Only neighbors, not [center]
     */
    public static final int[] ONLY_NEIGHBORS = {
            NW, N, NE, W, E, SW, S, SE
    };

}