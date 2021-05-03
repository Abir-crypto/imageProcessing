package sample.filter;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.IntStream;

public class GaussianFilter {
    private BufferedImage inputImage;
    private BufferedImage outputImage;
    int[] tempPixel;
    int maskSize = 3;

    public GaussianFilter(BufferedImage inputImage) {
        this.inputImage = inputImage;
        tempPixel = new int[9];
    }


    public BufferedImage getGaussianFiltering(){

        outputImage = new BufferedImage(
                this.inputImage.getWidth(),
                this.inputImage.getHeight(),
                BufferedImage.TYPE_BYTE_GRAY
        );

        int kernalSize = 3;
        int kernalCal = 1;
        Double[][] kernal = new Double[kernalSize][kernalSize];
        for(int i=-kernalCal,x=0;i<=kernalCal;i++,x++){
            for(int j=-kernalCal,y=0;j<=kernalCal;j++,y++){
                double epower = -(Math.pow(i, 2) + Math.pow(j, 2))/(2*Math.pow(-1.125, 2));
                double other = 1/(2*Math.PI*Math.pow(-1.125, 2));
                kernal[x][y] = other * Math.exp(epower);
                System.out.print(kernal[x][y]+" ");
            }
            System.out.println();
        }

        //Rotating 180 degree

        Double[][] conv = new Double[7][7];
        for(int i=0;i<kernalSize;i++){
            for(int j=0;j<kernalSize;j++){
                conv[i][j] = kernal[kernalSize-1-i][kernalSize-1-j];
            }
        }


//        System.out.println("After 180 degree rotation");
//        for(int i=0;i<3;i++){
//            for(int j=0;j<3;j++){
//                System.out.print(conv[i][j]+" ");
//            }
//            System.out.println();
//        }



        WritableRaster raster = this.inputImage.getRaster();
        WritableRaster outRaster = this.outputImage.getRaster();
        int[][] arr2d = {
                {36, 36, 35, 35, 35, 35, 35},
                {36, 36, 36, 35, 35, 35, 35},
                {36, 36, 36, 36, 36, 35, 35},
                {36, 36, 36, 36, 36, 36, 36},
                {37, 37, 37 ,37, 36, 36, 37},
                {37, 37, 37, 37, 37, 37, 37},
                {37, 37, 37, 37, 37, 37, 37}
        };

        for(int row=0;row<7;row++){
            for(int col=0;col<7;col++){
                double gx =0;
                double ksum =0;
                for(int i=row-kernalCal,x=0;i<=row+kernalCal;i++,x++){
                    for(int j=col-kernalCal,y=0;j<=col+kernalCal;j++,y++){
                        if(i>=0 && i<7 && j>=0 && j<7){
//                            int val = raster.getSample(j,i,0);
                            int val = arr2d[i][j];
                            gx += val*conv[x][y];
                            ksum +=conv[x][y];
                        }
                    }
                }
//                gx = gx/ksum;
                System.out.print(Math.round(gx)+" ");
//                outRaster.setSample(col, row, 0, (int)gx);
            }
            System.out.println();
        }

        //BufferedImage result = new BufferedImage(inputImage.getWidth(), inputImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
//        outputImage.setData(outRaster);
        return inputImage;
    }
}
