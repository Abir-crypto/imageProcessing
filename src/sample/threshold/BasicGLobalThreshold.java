package sample.threshold;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public class BasicGLobalThreshold {

    BufferedImage inputImage, outputImage;

    public BasicGLobalThreshold(BufferedImage inputImage) {
        this.inputImage = inputImage;
    }

    public BufferedImage doBasicGlobalThreshold(){
        try{
            int T_old = initialThreshold();

            int T_final = BGT(T_old);
            System.out.println("Final Threshold: "+T_final);
            // apply general equations of threshold
            outputImage =
                    new BufferedImage(inputImage.getWidth(),inputImage.getHeight(),
                            BufferedImage.TYPE_BYTE_BINARY);

            WritableRaster raster = inputImage.getRaster();
            WritableRaster outputRaster = outputImage.getRaster();
            for(int row=0;row<inputImage.getHeight();row++){
                for(int col=0;col<inputImage.getWidth();col++){
                    int pixel = raster.getSample(col, row, 0);

                    if(pixel>T_final){
                        outputRaster.setSample(col, row, 0, 255);
                    }else{
                        outputRaster.setSample(col, row, 0, 0);
                    }
                }
            }

            outputImage.setData(outputRaster);

            return outputImage;

        }catch (Exception ex){
            System.err.println(ex.getMessage());
        }
        return null;
    }

    private int BGT(int T_old){

        System.out.println("Initial Thresholds: "+T_old);

        int[][] Gx = new int[inputImage.getHeight()][inputImage.getWidth()];
        int[][] Gy = new int[inputImage.getHeight()][inputImage.getWidth()];
        int countOfGx=0, countOfGy=0;

        WritableRaster raster = inputImage.getRaster();
        for(int row=0;row<inputImage.getHeight();row++){
            for(int col=0;col<inputImage.getWidth();col++){
                int pixel = raster.getSample(col, row, 0);
                if(pixel>T_old){
                    Gx[row][col] = pixel;
                    countOfGx++;
                }else{
                    Gy[row][col] = pixel;
                    countOfGy++;
                }
            }
        }

        int Mx = sumOf(Gx)/countOfGx;
        int My = sumOf(Gy)/countOfGy;

        int T_new = (int) (0.5*(Mx+My));

        int delta_T = Math.abs(T_old-T_new);

        if(delta_T>=0 && delta_T<1){
            return T_new;
        }else{
            T_old=T_new;
            T_old = BGT(T_old);
        }

        return T_new;

    }

    private int sumOf(int[][] data) {
        int sum=0;

        for(int row=0;row<inputImage.getHeight();row++){
            for(int col=0;col<inputImage.getWidth();col++){
                sum+=data[row][col];
            }
        }

        return sum;
    }


    private int initialThreshold(){
        // TODO: calculate initial threshold using histogram pick value
        return 150;
    }
}
