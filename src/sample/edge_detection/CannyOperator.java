package sample.edge_detection;

import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLOperation;
import sample.filter.GaussianFilter;
import sample.filter.MedeanFilter;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Collections;

public class CannyOperator {
    private BufferedImage inputImage, outputImage;
    /**
     * Sobel Horizontal Kernel
     * -1 -2 -1
     * 0 0 0
     * 1 2 1
     */
    private int[][] sobelHKernel={{-1,-2,-1},{0,0,0},{1,2,1}};
    int[][] thetaRaster;
    /**
     * Sobel Vertical Kernel
     * -1 0 1
     * -2 0 2
     * -1 0 1
     */
    private int[][] sobelVKernel = {{-1,0,1},{-2,0,2},{-1,0,1}};

    private int Gx, Gy, G;

    public CannyOperator(BufferedImage inputImage) {
        this.inputImage = inputImage;
        thetaRaster = new int[inputImage.getHeight()][inputImage.getWidth()];
    }

    public BufferedImage edgeDetectionByCanny(){
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
        BufferedImage sobelImage = applySolelOperator(smoothingImage);

        return applyCannyFilter(sobelImage);
    }

    private BufferedImage applySolelOperator(BufferedImage smoothingImage) {
        int kernelSize =sobelVKernel.length;

        int midKernelX = sobelVKernel.length/2;
        int midKernelY = sobelHKernel.length/2;

//
//        WritableRaster raster =  smoothingImage.getRaster();
//        WritableRaster doIstay = smoothingImage.getRaster();
//        WritableRaster outputRaster = smoothingImage.getRaster();
//        WritableRaster SWN = smoothingImage.getRaster();
        BufferedImage inputimg = new BufferedImage(smoothingImage.getWidth(),smoothingImage.getHeight(),BufferedImage.TYPE_BYTE_GRAY);



//        int[][] thetaRaster = new int[smoothingImage.getWidth()][smoothingImage.getHeight()];
//        int[][] doIstay = new int[smoothingImage.getWidth()][smoothingImage.getHeight()];
        int[][] SWN = new int[smoothingImage.getWidth()][smoothingImage.getHeight()];



        WritableRaster raster = smoothingImage.getRaster();
        WritableRaster outputRaster = inputimg.getRaster();

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

                double val = 0;
                if(Gx!=0){
                    val = Gy/Gx;
                }
                double radian = Math.atan(val);
                int degree =(int) Math.toDegrees(radian);
                thetaRaster[row][col] = degree;
            }
        }

        outputImage.setData(outputRaster);
        return outputImage;

    }

    private BufferedImage applyCannyFilter(BufferedImage smoothingImage){
        //Step 3
        BufferedImage inputimg = new BufferedImage(smoothingImage.getWidth(),smoothingImage.getHeight(),BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster doIstay = inputimg.getRaster();
        WritableRaster finalRaster = inputimg.getRaster();
        WritableRaster outputRaster = smoothingImage.getRaster();
//        WritableRaster SWN = smoothingImage.getRaster();

        int[][] SWN = new int[smoothingImage.getHeight()][smoothingImage.getWidth()];
        int p, q;

        for(int i=0;i<smoothingImage.getHeight();i++){
            for(int j=0;j<smoothingImage.getWidth();j++){
                double val = thetaRaster[i][j];
                int Gval = outputRaster.getSample(j, i, 0);
                p = q = 0;
                if((val >= 0 && val<45) || (val >= 180 && val<225)){
                    if((j-1 >= 0  && j+1<smoothingImage.getWidth())){
                        if(val >= outputRaster.getSample(j-1, i, 0) && val >= outputRaster.getSample(j+1, i, 0)){
                            doIstay.setSample(j, i, 0, Gval);
//                            doIstay[j][i] =(int) Gval;
                        }
                    }
                }
                else if((val >= 45 && val<90) || (val >= 225 && val<270)){
                    if((i-1>=0  &&  i+1<smoothingImage.getHeight()) && (j-1>=0 && j+1<smoothingImage.getWidth())){
                        if(val >= outputRaster.getSample(j-1, i+1, 0) && val >= outputRaster.getSample(j+1, i-1, 0)){
                            doIstay.setSample(j, i, 0, Gval);
//                            doIstay[j][i] =(int) Gval;
                        }
                    }
                }
                else if((val >= 90 && val<135) || (val >= 270 && val<315)){
                    if((i-1 >= 0 && j+1<smoothingImage.getWidth())){
                        if(val >= outputRaster.getSample(j, i-1, 0) && val >= outputRaster.getSample(j, i+1, 0)){
                            doIstay.setSample(j, i, 0, Gval);
//                            doIstay[j][i] =(int) Gval;
                        }
                    }
                }
                else if((val >= 135 && val<180) || (val >= 315 && val<360)){
                    if((i-1>=0  &&  i+1<smoothingImage.getHeight()) && (j-1>=0 && j+1<smoothingImage.getWidth())){
                        if(val >= outputRaster.getSample(j-1, i-1, 0) && val >= outputRaster.getSample(j+1, i+1, 0)){
                            doIstay.setSample(j, i, 0, Gval);
//                            doIstay[j][i] =(int) Gval;
                        }
                    }
                }

//                System.out.println(doIstay.getSample(j, i, 0));
            }
        }

//        for(int i=0;i<smoothingImage.getHeight();i++){
//            for(int j=0;j<smoothingImage.getWidth();j++){
//
//                System.out.println(doIstay.getSample(j, i, 0));
//            }
//        }

//        int[][] arr = new int[smoothingImage.getWidth()][smoothingImage.getHeight()];
//        for(int i=0;i<doIstay.getHeight();i++){
//            for (int j=0;j<doIstay.getWidth();j++){
//                arr[j][i] = doIstay.getSample(j, i, 0);
////                System.out.println(arr[j][i]);
//            }
//        }

        //step 4 Double Threshold

        int lowThresholdCount=0;
        int highThresholdCount=0;

        for(int i=0;i<smoothingImage.getHeight();i++){
            for(int j=0;j<smoothingImage.getWidth();j++){
                if(doIstay.getSample(j, i, 0)>40){
                    highThresholdCount++;
                }
                else
                    lowThresholdCount++;
            }
        }
        double lowRatio = lowThresholdCount/(lowThresholdCount+highThresholdCount);
        double highRatio = highThresholdCount/(lowThresholdCount+highThresholdCount);

        int highThreshold = (int) (255 * highRatio);
        int lowThreshold = (int) (highThreshold * lowRatio);



//        for(int i=0;i<doIstay.getHeight();i++){
//            for(int j=0;j<doIstay.getWidth();j++){
//                int val = doIstay.getSample(j, i, 0);
//                if(val > highThreshold){
//                    strongWeakNR.setSample(j, i, 0, 1);
//                }
//                else if(val < lowThreshold){
//                    strongWeakNR.setSample(j, i, 0, -1);
//                }
//                else{
//                    strongWeakNR.setSample(j, i, 0, 0);
//                }
//               System.out.println(doIstay.getSample(j, i, 0));
//            }
//        }



        for (int row=0;row<smoothingImage.getHeight();row++){
            for(int col=0;col<smoothingImage.getWidth();col++){
                int val = doIstay.getSample(col, row, 0);
                if(val>highThreshold){
//                    SWN.setSample(col, row, 0, 2);
                    SWN[row][col] = 2;
                }
                else if(val<lowThreshold){
//                    SWN.setSample(col, row, 0, 0);
                    SWN[row][col] = 0;
                }
                else
//                    SWN.setSample(col, row, 0, 1);
                    SWN[row][col] = 1;
            }
        }

//        Edge tracking step 5




        for(int row=0;row<this.inputImage.getHeight();row++){
            for(int col=0;col<this.inputImage.getWidth();col++){

                if( SWN[row][col] == 1) {
                    ArrayList<Integer> kernal = new ArrayList<>();
                    for (int i = row - 1; i <= row + 1; i++) {
                        for (int j = col - 1; j <= col + 1; j++) {
                            if (i >= 0 && i < inputImage.getHeight() && j >= 0 && j < inputImage.getWidth() &&  SWN[i][j]==2) {
                                kernal.add(doIstay.getSample(j, i, 0));
                            }
                        }
                    }
                    if(kernal.size()!=0)
                        finalRaster.setSample(col, row, 0, 255);
                }

                else if( SWN[row][col] == 2){
                    finalRaster.setSample(col, row, 0, 255);

                }

                else{
                    finalRaster.setSample(col, row, 0, 0);
                }
//                System.out.println(doIstay.getSample(col, row, 0));
            }
        }
        outputImage.setData(finalRaster);
        return outputImage;
    }

    private BufferedImage applySmoothingFilter(BufferedImage grayScaleImage) {
        // TODO: apply linear or non-linear filter
        GaussianFilter gaussianFilter = new GaussianFilter(grayScaleImage);
//        return gaussianFilter.getGaussianFiltering();
        MedeanFilter medeanFilter = new MedeanFilter(gaussianFilter.getGaussianFiltering());
        return medeanFilter.getMeanFiltering();
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
