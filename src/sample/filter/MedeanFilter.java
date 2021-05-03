package sample.filter;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.IntStream;

public class MedeanFilter {
    private BufferedImage inputImage;
    private BufferedImage outputImage;
    int[] tempPixel;
    int maskSize = 3;

    public MedeanFilter(BufferedImage inputImage) {
        this.inputImage = inputImage;
        tempPixel = new int[9];
    }

    public BufferedImage getMeanFiltering(){
        outputImage = new BufferedImage(
                this.inputImage.getWidth(),
                this.inputImage.getHeight(),
                BufferedImage.TYPE_BYTE_GRAY
        );


        WritableRaster raster = this.inputImage.getRaster();
        WritableRaster outRaster = this.outputImage.getRaster();
        for(int row=1;row<this.inputImage.getHeight()-1;row++){
            for(int col=1;col<this.inputImage.getWidth()-1;col++){
                tempPixel[0] = raster.getSample(col-1, row-1, 0);
                tempPixel[1] = raster.getSample(col,row-1,0);
                tempPixel[2] = raster.getSample(col+1, row-1, 0);

                tempPixel[3] = raster.getSample(col-1, row, 0);
                tempPixel[4] = raster.getSample(col, row, 0);
                tempPixel[5] = raster.getSample(col+1, row, 0);

                tempPixel[6] = raster.getSample(col-1, row+1, 0);
                tempPixel[7] = raster.getSample(col, row+1, 0);
                tempPixel[8] = raster.getSample(col+1, row+1, 0);


                int mean = (int) IntStream.of(tempPixel).average().getAsDouble();

                outRaster.setSample(col, row, 0, mean);
            }
        }

        outputImage.setData(outRaster);

        return outputImage;
    }

    public BufferedImage getMeanFilteringWithBorder(){
        int tempCount=0;

        outputImage = new BufferedImage(
                this.inputImage.getWidth(),
                this.inputImage.getHeight(),
                BufferedImage.TYPE_BYTE_GRAY
        );

        int maskingX = maskSize/2;
        int maskingY = maskSize/2;

        WritableRaster raster = this.inputImage.getRaster();
        WritableRaster outRaster = this.outputImage.getRaster();

        for(int row=0;row<this.inputImage.getHeight();row++){
            for(int col=0;col<this.inputImage.getWidth();col++){
                ArrayList<Integer> kernal = new ArrayList<>();
                for(int i=row-1;i<=row+1;i++){
                    for(int j=col-1;j<=col+1;j++){
                        if(i>=0 && i<inputImage.getHeight() && j>=0 && j<inputImage.getWidth()){
                            kernal.add(raster.getSample(j,i,0));
                        }
                    }
                }
                Collections.sort(kernal);
                int median=0;
                if(kernal.size()%2==0){
                    int x = kernal.get(kernal.size()/2);
                    int y = kernal.get(kernal.size()/2 -1);
                    median = x+y/2;
                }
                else{
                    median = kernal.get(kernal.size()/2);
                }
                outRaster.setSample(col, row, 0, median);
            }
        }



//        for(int row=0;row<this.inputImage.getHeight();row++){
//            for(int col=0;col<this.inputImage.getWidth();col++){
//                tempCount = 0;
//                for(int maskX = 0; maskX<3; maskX++){
//                    int mx = maskSize-1-maskX;
//                    for(int maskY = 0; maskY<3; maskY++){
//                        int my = maskSize-1-maskY;
//                        int imageXBoundary = row + (mx-maskingX);
//                        int imageYBoundary = col + (my-maskingY);
//
//                        if(imageXBoundary>=0 && imageXBoundary<this.inputImage.getHeight() &&
//                                imageYBoundary>=0 && imageYBoundary<this.inputImage.getWidth()){
//
//                            tempPixel[tempCount] = raster.getSample(imageYBoundary, imageXBoundary, 0);
//                            tempCount++;
//                        }
//                    }
//                }
//                if(tempPixel.length>0){
//                    int mean = (int) IntStream.of(tempPixel).average().getAsDouble();
//
//                    outRaster.setSample(col, row, 0, mean);
//                }
//            }
//        }

        outputImage.setData(outRaster);

        return outputImage;
    }
}
