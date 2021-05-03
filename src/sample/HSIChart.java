package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;


public class HSIChart{

    public CategoryAxis x;
    public NumberAxis y;
    public LineChart<?, ?> HSI_line;

    public ImageView HSI_original_img;
    public Slider slider_red_color;
    public Slider slider_green_color;
    public Slider slider_blue_color;
    public Button back;
    BufferedImage inputImage;
    XYChart.Series series = new XYChart.Series();

    int[] histogram = new int[256];

    public void setImportImage(BufferedImage inputImage){
        this.inputImage = inputImage;
    }

    private BufferedImage getGrayScaleImage() {
        BufferedImage outputImage = new
                BufferedImage(inputImage.getWidth(),inputImage.getHeight(),BufferedImage.TYPE_BYTE_GRAY);
        for(int row=0;row<inputImage.getHeight();row++){
            for(int col=0;col<inputImage.getWidth();col++){
                int rgb = inputImage.getRGB(col,row);

                int red = (rgb>>16)&0xFF;
                int green = (rgb>>8)&0xFF;
                int blue= (rgb)&0xFF;

                int gray = (red+green+blue)/3;

                outputImage.setRGB(col,row,(gray<<16)|(gray<<8)|gray);
            }
        }
        return outputImage;
    }


    public void setImageIntoImageView(){
        if(this.inputImage==null && this.HSI_original_img==null)
            return;
        //imageView.setImage(SwingFXUtils.toFXImage(this.inputImage,null));
        BufferedImage graImg = getGrayScaleImage();
        HSI_original_img.setImage(SwingFXUtils.toFXImage(graImg,null));
    }

    public void setHsiChar() {

        for(int i=0;i<256;i++){
            histogram[i] = 0;
        }

        BufferedImage grayScaleImage = getGrayScaleImage();

        WritableRaster raster = grayScaleImage.getRaster();
        for(int row=0; row<grayScaleImage.getHeight(); row++){
            for(int col=0; col<grayScaleImage.getWidth(); col++){

                int pixel = raster.getSample(col, row, 0);

                histogram[pixel]++;

            }
        }


        for(int i=0;i<256;i++){
            series.getData().add(new XYChart.Data<>(Integer.toString(i), histogram[i]));
        }
        HSI_line.getData().addAll(series);
    }


    public void histogramBalancing(ActionEvent actionEvent) {
        ArrayList<Pair<Integer, Integer>> histogramList = new ArrayList<>();
        for(int i=1;i<256;i++){
            if(histogram[i]>0)
                histogramList.add(new Pair<>(i, histogram[i]));
//            System.out.println(histogram[i]);
        }



        BufferedImage grayScaleImage = getGrayScaleImage();
        ColorModel cm = grayScaleImage.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();

        WritableRaster raster = grayScaleImage.getRaster();
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

//        BufferedImage result = new BufferedImage(cm, raster2, isAlphaPremultiplied, null);
        BufferedImage result = new BufferedImage(grayScaleImage.getWidth(), grayScaleImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        result.setData(raster2);

        HSI_original_img.setImage(SwingFXUtils.toFXImage(result,null));

        HSI_line.getData().removeAll(series);
        XYChart.Series series2 = new XYChart.Series();
        for(int i=0;i<256;i++){
            series2.getData().add(new XYChart.Data<>(Integer.toString(i), balancedHistogram[i]));
        }
        HSI_line.getData().addAll(series2);

    }

    public void gotoMainPage(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent parent = loader.load();
        Scene scene = new Scene(parent);
        Stage stage = (Stage) back.getScene().getWindow();
        stage.setScene(scene);
    }
}
