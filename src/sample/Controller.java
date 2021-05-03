package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import sample.edge_detection.CannyOperator;
import sample.edge_detection.SobelOperator;
import sample.filter.*;
import sample.threshold.BasicGLobalThreshold;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller {

    public ImageView input_image;
    public ImageView output_image;
    public BufferedImage inputBufferedImage, outputBufferedImage;
    public MenuBar menuBar;
    private int imageWidth, imageHeight;


    public void openImageFromComputer(ActionEvent actionEvent) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JPG File","*.jpg"),
                new FileChooser.ExtensionFilter("PNG File","*.png")
        );
        File file = fileChooser.showOpenDialog(null);
        if(file == null) {
            System.out.println("No file choose!!");
            return;
        }
        System.out.println(file.getAbsolutePath());

        // to show the image as input and output
        input_image.setImage(new Image(new FileInputStream(file.getAbsolutePath())));

        // all kinds of calculations
        inputBufferedImage = ImageIO.read(new FileInputStream(file.getAbsolutePath()));

        imageWidth = inputBufferedImage.getWidth();
        imageHeight = inputBufferedImage.getHeight();

    }

    public void saveImageIntoComputer(ActionEvent actionEvent) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JPG File","*.jpg"),
                new FileChooser.ExtensionFilter("PNG File","*.png")
        );
        File file = fileChooser.showSaveDialog(null);
        if(file==null)
            return;
        ImageIO.write(this.outputBufferedImage,"jpg",file);

    }

    public void gotoSingleColorForm(ActionEvent actionEvent) throws IOException {
        if(inputBufferedImage == null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Input Error");
            alert.setContentText("Input image not found!");
            alert.show();
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("singleColor.fxml"));
        Parent parent = loader.load();

        SingleColorFormController controller = (SingleColorFormController) loader.getController();

        Scene scene = new Scene(parent);
        Stage stage = (Stage) menuBar.getScene().getWindow();

        stage.setScene(scene);

        controller.setImportImage(inputBufferedImage);
        controller.setImageIntoImageView();

    }



    public void doConversionFromRBGToCMY(ActionEvent actionEvent) {
        outputBufferedImage =
                new BufferedImage(imageWidth,imageHeight,BufferedImage.TYPE_3BYTE_BGR);

        for(int row=0;row<imageHeight;row++){
            for(int col=0;col<imageWidth;col++){

                int rgb = inputBufferedImage.getRGB(col, row);

                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = (rgb) & 0xFF;

                //System.out.println("RGB combine value: "+rgb+", Red->"+red+", Green->"+green+", Blue->"+blue);

                int cyan = 255 - red;
                int magenta = 255 - green;
                int yellow = 255 - blue;

                outputBufferedImage.setRGB(col, row, (cyan<<16)|(magenta<<8)|yellow);
            }
        }

        output_image.setImage(SwingFXUtils.toFXImage(outputBufferedImage,null));

    }

    public void doConversionFromRGBToHSI(ActionEvent actionEvent) {
        outputBufferedImage =
                new BufferedImage(imageWidth,imageHeight,BufferedImage.TYPE_3BYTE_BGR);

        for(int row=0;row<imageHeight;row++){
            for(int col=0;col<imageWidth;col++){

                int rgb = inputBufferedImage.getRGB(col, row);

                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = (rgb) & 0xFF;

                //System.out.println("RGB combine value: "+rgb+", Red->"+red+", Green->"+green+", Blue->"+blue);

                int H=0, S=0, I=0;
                float[] hsb = Color.RGBtoHSB(red, green, blue, null);
                H = Math.round(hsb[0]);
                S = Math.round(hsb[1]);
                I = Math.round(hsb[2]);
//                if(red+blue+green != 0) {
//                    double xyz = 1;
//
//                    xyz = 2* Math.sqrt(Math.pow((red-green), 2) + red*green - blue*green + blue*blue);
//                    double value=0;
////                    if(xyz!=0)
////                    if(xyz!=0)
//                    value =  (2*red - green - blue)/xyz;
//                    double radian = Math.acos(value);
//                    double degree = Math.toDegrees(radian);
//
//                    //System.out.println(value);
////                    System.out.println(radian+ " to degree = "+degree);
////                    System.out.println(degree);
//                    H = (int) Math.round(degree);
//                    if (blue > green)
//                        H = (int) Math.round(360 - degree);
//
//
//
//                    int min = Math.min(Math.min(red, green), blue);
//                    S =Math.round( 1 - (3/ (red + green + blue)) * min);
//                }
//                I = Math.round ((red + blue + green) / 3);
// //               Color rgbColor = Color.getHSBColor(H, S, I);
//               // System.out.println(H+" "+S+" "+I);
                System.out.println(H + " "+ S + " "+I);
                outputBufferedImage.setRGB(col, row, (H<<16)|(S<<8)|I);
            }
        }

        output_image.setImage(SwingFXUtils.toFXImage(outputBufferedImage,null));
    }

    public void doAverageGrayScale(ActionEvent actionEvent) {
        outputBufferedImage = new
                BufferedImage(inputBufferedImage.getWidth(),inputBufferedImage.getHeight(),BufferedImage.TYPE_BYTE_GRAY);
        for(int row=0;row<inputBufferedImage.getHeight();row++){
            for(int col=0;col<inputBufferedImage.getWidth();col++){
                int rgb = inputBufferedImage.getRGB(col,row);

                int red = (rgb>>16)&0xFF;
                int green = (rgb>>8)&0xFF;
                int blue= (rgb)&0xFF;

                int gray = (red+green+blue)/3;


                outputBufferedImage.setRGB(col,row,(gray<<16)|(gray<<8)|gray);
            }
        }
        output_image.setImage(SwingFXUtils.toFXImage(outputBufferedImage,null));
    }

    public void doDeSaturation(ActionEvent actionEvent) {
        outputBufferedImage = new
                BufferedImage(inputBufferedImage.getWidth(),inputBufferedImage.getHeight(),BufferedImage.TYPE_BYTE_GRAY);
        for(int row=0;row<inputBufferedImage.getHeight();row++){
            for(int col=0;col<inputBufferedImage.getWidth();col++){
                int rgb = inputBufferedImage.getRGB(col,row);

                int red = (rgb>>16)&0xFF;
                int green = (rgb>>8)&0xFF;
                int blue= (rgb)&0xFF;

                int max = Math.max(Math.max(red,green),blue);
                int min = Math.min(Math.min(red, green),blue);

                int gray = (max+min)/2;


                outputBufferedImage.setRGB(col,row,(gray<<16)|(gray<<8)|gray);
            }
        }
        output_image.setImage(SwingFXUtils.toFXImage(outputBufferedImage,null));
    }

    public void doLuma(ActionEvent actionEvent) {
        outputBufferedImage = new
                BufferedImage(inputBufferedImage.getWidth(),inputBufferedImage.getHeight(),BufferedImage.TYPE_BYTE_GRAY);
        for(int row=0;row<inputBufferedImage.getHeight();row++){
            for(int col=0;col<inputBufferedImage.getWidth();col++){
                int rgb = inputBufferedImage.getRGB(col,row);

                int red = (rgb>>16)&0xFF;
                int green = (rgb>>8)&0xFF;
                int blue= (rgb)&0xFF;

                int max = Math.max(Math.max(red,green),blue);
                int min = Math.min(Math.min(red, green),blue);

                int gray = (int)(red*0.3 + green*0.59 + blue*0.11);

                outputBufferedImage.setRGB(col,row,(gray<<16)|(gray<<8)|gray);
            }
        }
        output_image.setImage(SwingFXUtils.toFXImage(outputBufferedImage,null));
    }

    public void doDecomposition(ActionEvent actionEvent) {
        outputBufferedImage = new
                BufferedImage(inputBufferedImage.getWidth(),inputBufferedImage.getHeight(),BufferedImage.TYPE_BYTE_GRAY);
        for(int row=0;row<inputBufferedImage.getHeight();row++){
            for(int col=0;col<inputBufferedImage.getWidth();col++){
                int rgb = inputBufferedImage.getRGB(col,row);

                int red = (rgb>>16)&0xFF;
                int green = (rgb>>8)&0xFF;
                int blue= (rgb)&0xFF;

                int max = Math.max(Math.max(red,green),blue);
                int min = Math.min(Math.min(red, green),blue);

                int gray = max;
                outputBufferedImage.setRGB(col,row,(gray<<16)|(gray<<8)|gray);
            }
        }
        output_image.setImage(SwingFXUtils.toFXImage(outputBufferedImage,null));
    }

    private BufferedImage getGrayScaleImage() {
        BufferedImage outputImage = new
                BufferedImage(inputBufferedImage.getWidth(),inputBufferedImage.getHeight(),BufferedImage.TYPE_BYTE_GRAY);
        for(int row=0;row<inputBufferedImage.getHeight();row++){
            for(int col=0;col<inputBufferedImage.getWidth();col++){
                int rgb = inputBufferedImage.getRGB(col,row);

                int red = (rgb>>16)&0xFF;
                int green = (rgb>>8)&0xFF;
                int blue= (rgb)&0xFF;

                int gray = (red+green+blue)/3;

                outputImage.setRGB(col,row,(gray<<16)|(gray<<8)|gray);
            }
        }
        return outputImage;
    }

    public void calculateGrayScaleHistogram(ActionEvent actionEvent) throws IOException {
        if(inputBufferedImage == null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Input Error");
            alert.setContentText("Input image not found!");
            alert.show();
            return;
        }

        int[] histogram = new int[256];

        BufferedImage grayScaleImage = getGrayScaleImage();

        WritableRaster raster = grayScaleImage.getRaster();

        for(int row=0; row<grayScaleImage.getHeight(); row++){
            for(int col=0; col<grayScaleImage.getWidth(); col++){

                int pixel = raster.getSample(col, row, 0);

                histogram[pixel]++;

            }
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("HSI_Chart.fxml"));
        Parent parent = loader.load();

        HSIChart controller = (HSIChart) loader.getController();

        Scene scene = new Scene(parent);
        Stage stage = (Stage) menuBar.getScene().getWindow();

        stage.setScene(scene);

        controller.setImportImage(inputBufferedImage);
        controller.setImageIntoImageView();
        controller.setHsiChar();

//        for(int index=0;index<histogram.length;index++){
//            System.out.println("Pixel: "+index+", Count: "+histogram[index]);
//        }
    }

    int[] histogram = new int[256];

    public void equalizeHistogram(ActionEvent actionEvent) {

        BufferedImage grayScaleImage = getGrayScaleImage();

        WritableRaster raster = grayScaleImage.getRaster();
        for(int row=0; row<grayScaleImage.getHeight(); row++){
            for(int col=0; col<grayScaleImage.getWidth(); col++){

                int pixel = raster.getSample(col, row, 0);

                histogram[pixel]++;

            }
        }

        ArrayList<Pair<Integer, Integer>> histogramList = new ArrayList<>();
        for(int i=1;i<256;i++){
            if(histogram[i]>0)
                histogramList.add(new Pair<>(i, histogram[i]));
//            System.out.println(histogram[i]);
        }



        grayScaleImage = getGrayScaleImage();
        ColorModel cm = grayScaleImage.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();

        raster = grayScaleImage.getRaster();
        WritableRaster raster2 = grayScaleImage.getRaster();

        int cdf =0;
        int n = raster.getHeight();
        int m = raster.getWidth();
        int min = histogramList.get(0).getValue();
        int newHistogram[] = new int[256];

        for(int i=0;i<256;i++){
            newHistogram[i] = 0;
//            System.out.println(histogram[i]);
        }

        int size = histogramList.size();
        int arr[] = new int[size];
        arr[0] = histogramList.get(0).getValue();

        for(int i=1;i<size;i++){
            arr[i] = arr[i-1] + histogramList.get(i).getValue();
        }

        for (int i=0;i<histogramList.size();i++) {

            //System.out.println(index);

            int upper = arr[i]-min;
            int lower = m*n - min;
            int x = Math.round(upper*255/lower);
            newHistogram[histogramList.get(i).getKey()] = x;
//            System.out.println(x);
            //System.out.println(histogram[i]-min);

            //System.out.println(histogram[index]+"before and after"+newHistogram[index]);
            //newHistogram[Math.round(((count - min) * 255) / ((m * n) - min))] = integerIntegerPair.getValue();
        }

        int[] balancedHistogram = new int[256];
        for(int i=0;i<256;i++){
            balancedHistogram[i]=0;
        }


        for(int row=0; row<grayScaleImage.getHeight(); row++){
            for(int col=0; col<grayScaleImage.getWidth(); col++){

                int pixel = raster.getSample(col, row, 0);

                int newval = newHistogram[pixel];
//                System.out.println("Old pixel = "+pixel+" New pixel = "+newval);
                raster2.setSample(col, row, 0, newHistogram[pixel]);
                //grayScaleImage.setRGB(col, row, newval);
            }
        }

        for(int row=0; row<grayScaleImage.getHeight(); row++){
            for(int col=0; col<grayScaleImage.getWidth(); col++){

                int pixel = raster2.getSample(col, row, 0);

                balancedHistogram[raster2.getSample(col, row, 0)]++;

            }
        }

//        for(int i=0;i<256;i++){
//            System.out.println(balancedHistogram[i]);
//        }

//        for(int i=0;i<256;i++){
//            System.out.println(" After :"+balancedHistogram[i]);
//        }

        outputBufferedImage = new BufferedImage(cm, raster2, isAlphaPremultiplied, null);

        output_image.setImage(SwingFXUtils.toFXImage(outputBufferedImage,null));

    }

    public void doBGT(ActionEvent actionEvent) {
        BasicGLobalThreshold bgt = new BasicGLobalThreshold(getGrayScaleImage());
        BufferedImage bufferedImage = bgt.doBasicGlobalThreshold();
        output_image.setImage(SwingFXUtils.toFXImage(bufferedImage,null));
        this.outputBufferedImage = bufferedImage;
    }

    public void doMeanFilter(ActionEvent actionEvent) {
        MeanFilter meanFilter = new MeanFilter(getGrayScaleImage());
        BufferedImage bufferedImage = meanFilter.getMeanFilteringWithBorder();
        output_image.setImage(SwingFXUtils.toFXImage(bufferedImage,null));
        this.outputBufferedImage = bufferedImage;
    }
    public void doMinFilter(ActionEvent actionEvent) {
        MinimumFilter minFilter = new MinimumFilter(getGrayScaleImage());
        BufferedImage bufferedImage = minFilter.getMeanFilteringWithBorder();
        output_image.setImage(SwingFXUtils.toFXImage(bufferedImage,null));
        this.outputBufferedImage = bufferedImage;
    }
    public void doMaxFilter(ActionEvent actionEvent) {
        MaximumFilter maximumFilter = new MaximumFilter(getGrayScaleImage());
        BufferedImage bufferedImage = maximumFilter.getMeanFilteringWithBorder();
        output_image.setImage(SwingFXUtils.toFXImage(bufferedImage,null));
        this.outputBufferedImage = bufferedImage;
    }
    public void doMedianFilter(ActionEvent actionEvent) {
        MedeanFilter medeanFilter = new MedeanFilter(getGrayScaleImage());
        BufferedImage bufferedImage = medeanFilter.getMeanFilteringWithBorder();
        output_image.setImage(SwingFXUtils.toFXImage(bufferedImage,null));
        this.outputBufferedImage = bufferedImage;
    }
    public void doGaussianFilter(ActionEvent actionEvent) {
        GaussianFilter gaussianFilter = new GaussianFilter(getGrayScaleImage());
        BufferedImage bufferedImage = gaussianFilter.getGaussianFiltering();
        output_image.setImage(SwingFXUtils.toFXImage(bufferedImage,null));
        this.outputBufferedImage = bufferedImage;
    }
    public void applySobelOperator(ActionEvent actionEvent) {
        SobelOperator sobelOperator = new SobelOperator(inputBufferedImage);
        BufferedImage bufferedImage = sobelOperator.edgeDetectionBySobel();
        output_image.setImage(SwingFXUtils.toFXImage(bufferedImage,null));
        this.outputBufferedImage = bufferedImage;
    }
    public void applyCannyOperator(ActionEvent actionEvent) {
        CannyOperator cannyOperator = new CannyOperator(inputBufferedImage);
        BufferedImage bufferedImage = cannyOperator.edgeDetectionByCanny();
        output_image.setImage(SwingFXUtils.toFXImage(bufferedImage,null));
        this.outputBufferedImage = bufferedImage;
    }
}
