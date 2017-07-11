package com.recommend.movie.tasks;


import no.uib.cipr.matrix.DenseMatrix;

import java.util.concurrent.Semaphore;

public class NormalizeMatrixTask implements  Runnable{

    private DenseMatrix movieMatrix;
    private int i;
    private Semaphore s;

    public NormalizeMatrixTask(DenseMatrix movieMatrix, int i, Semaphore s) {
        this.movieMatrix = movieMatrix;
        this.i = i;
        this.s = s;
    }

    @Override
    public void run() {
        int counter = 0;
        for(int j = 1; j < movieMatrix.numColumns(); j++){
            if(movieMatrix.get(i, j) == 1)
                counter++;
        }
        for(int k = 1; k < movieMatrix.numColumns(); k++){
            if(movieMatrix.get(i, k) == 1){
                movieMatrix.set(i, k , 1/Math.sqrt(counter));
            }
        }
        s.release();
    }
}
