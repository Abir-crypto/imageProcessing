package sample.edge_detection;

import sample.filter.GaussianFilter;
import sample.filter.MeanFilter;
import sample.filter.MedeanFilter;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Collections;

public class SobelOperator {
    private BufferedImage inputImage, outputImage;
    /**
     * Sobel Horizontal Kernel
     * -1 -2 -1
     * 0 0 0
     * 1 2 1
     */
    private int[][] sobelHKernel={{-1,-2,-1},{0,0,0},{1,2,1}};
    /**
     * Sobel Vertical Kernel
     * -1 0 1
     * -2 0 2
     * -1 0 1
     */
    private int[][] sobelVKernel = {{-1,0,1},{-2,0,2},{-1,0,1}};

    private int Gx, Gy, G;

    public SobelOperator(BufferedImage inputImage) {
        this.inputImage = inputImage;
    }

    public BufferedImage edgeDetectionBySobel(){
        outputImage = new BufferedImage(this.inputImage.getWidth(),this.inputImage.getHeight(),BufferedImage.TYPE_BYTE_GRAY);

        /**
         * Apply gray scaling algo
         */
        BufferedImage grayScaleImage = applyGrayScale(this.inputImage);
        /**
         * Apply Linear or non-linear filtering process
         */
        BufferedImage smoothingImage = applySmoothingFilter(grayScaleImage);

        /**
         * Apply sobel algo
         */
        return applySobelOperator(smoothingImage);
    }

    private BufferedImage applySobelOperator(BufferedImage smoothingImage) {
        int kernelSize =sobelVKernel.length;

        int midKernelX = sobelVKernel.length/2;
        int midKernelY = sobelHKernel.length/2;

        WritableRaster raster = smoothingImage.getRaster();
        WritableRaster outputRaster = outputImage.getRaster();

        for(int row=0;row<smoothingImage.getHeight();row++){
            for(int col=0;col<smoothingImage.getWidth();col++){
                Gx=Gy=G=0;
                for(int kernelX=0;kernelX<kernelSize;kernelX++){
                    int kernelCurrentXPosition = kernelSize-kernelX-1;
                    for(int kernelY=0;kernelY<kernelSize;kernelY++){
                        int kernelCurrentYPosition = kernelSize-kernelY-1;

                        int imageXBoundary = row + (kernelCurrentXPosition-midKernelX);
                        int imageYBoundary = col + (kernelCurrentYPosition-midKernelY);

                        if(imageXBoundary>=0 && imageXBoundary<smoothingImage.getHeight() &&
                                imageYBoundary>=0 && imageYBoundary<smoothingImage.getWidth()){

                            int pixel = raster.getSample(imageYBoundary, imageXBoundary, 0);

                            Gx += (this.sobelVKernel[kernelCurrentXPosition][kernelCurrentYPosition] * pixel);
                            Gy += (this.sobelHKernel[kernelCurrentXPosition][kernelCurrentYPosition] * pixel);
                        }
                    }
                }
                int tempG = (int) (Math.pow(Gx,2)+Math.pow(Gy,2));
                G = (int) Math.sqrt(tempG);
                outputRaster.setSample(col, row, 0, G);
            }
        }

        outputImage.setData(outputRaster);
        return outputImage;

    }

    private BufferedImage applySmoothingFilter(BufferedImage grayScaleImage) {
        // TODO: apply linear or non-linear filter
        GaussianFilter gaussianFilter = new GaussianFilter(grayScaleImage);
//        return gaussianFilter.getGaussianFiltering();
        MeanFilter meanFilter = new MeanFilter(gaussianFilter.getGaussianFiltering());
        return meanFilter.getMeanFiltering();
//        return grayScaleImage;
    }


    private BufferedImage applyGrayScale(BufferedImage inputImage) {
        BufferedImage bufferedImage = new
                BufferedImage(inputImage.getWidth(),inputImage.getHeight(),BufferedImage.TYPE_BYTE_GRAY);
        for(int row=0;row<inputImage.getHeight();row++){
            for(int col=0;col<inputImage.getWidth();col++){
                int rgb = inputImage.getRGB(col,row);

                int red = (rgb>>16)&0xFF;
                int green = (rgb>>8)&0xFF;
                int blue= (rgb)&0xFF;

                int gray = (red+green+blue)/3;

                bufferedImage.setRGB(col,row,(gray<<16)|(gray<<8)|gray);
            }
        }
        return bufferedImage;
    }
}
