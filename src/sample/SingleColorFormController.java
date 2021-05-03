package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SingleColorFormController implements Initializable{

    public ImageView imageView;
    public Slider slider_red_color;
    public Slider slider_green_color;
    public Slider slider_blue_color;
    public Button buttonBack;
    BufferedImage inputImage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        slider_red_color.setValue(127.0);

        slider_red_color.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (! isChanging) {
                redColorChangeOnly(slider_red_color.getValue());
            }
        });

        slider_green_color.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (! isChanging) {
                greenColorChangeOnly(slider_green_color.getValue());
            }
        });

        slider_blue_color.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (! isChanging) {
                blueColorChangeOnly(slider_blue_color.getValue());
            }
        });
    }

    public void setImportImage(BufferedImage inputImage){
        this.inputImage = inputImage;
    }

    public void setImageIntoImageView(){
        if(this.inputImage==null && this.imageView==null)
            return;
        imageView.setImage(SwingFXUtils.toFXImage(this.inputImage,null));
    }

    private void redColorChangeOnly(Number red) {
        BufferedImage outputImage =
                new BufferedImage(inputImage.getWidth(),inputImage.getHeight(),BufferedImage.TYPE_3BYTE_BGR);

        for(int row=0;row<inputImage.getHeight();row++){
            for(int col=0;col<inputImage.getWidth();col++){

                int rgb = inputImage.getRGB(col,row);

                int green = (rgb>>8)&0xFF;
                int blue = (rgb)&0xFF;

                outputImage.setRGB(col, row, (red.intValue()<<16)|(green<<8)|blue);
            }
        }

        imageView.setImage(SwingFXUtils.toFXImage(outputImage,null));
    }

    private void greenColorChangeOnly(Number green) {
        BufferedImage outputImage =
                new BufferedImage(inputImage.getWidth(),inputImage.getHeight(),BufferedImage.TYPE_3BYTE_BGR);

        for(int row=0;row<inputImage.getHeight();row++){
            for(int col=0;col<inputImage.getWidth();col++){

                int rgb = inputImage.getRGB(col,row);

                int red = (rgb>>16)&0xFF;
                int blue = (rgb)&0xFF;

                outputImage.setRGB(col, row, (red<<16)|(green.intValue()<<8)|blue);
            }
        }

        imageView.setImage(SwingFXUtils.toFXImage(outputImage,null));
    }

    private void blueColorChangeOnly(Number blue) {
        BufferedImage outputImage =
                new BufferedImage(inputImage.getWidth(),inputImage.getHeight(),BufferedImage.TYPE_3BYTE_BGR);

        for(int row=0;row<inputImage.getHeight();row++){
            for(int col=0;col<inputImage.getWidth();col++){

                int rgb = inputImage.getRGB(col,row);

                int red = (rgb>>16)&0xFF;
                int green = (rgb>>8)&0xFF;

                outputImage.setRGB(col, row, (red<<16)|(green<<8)|blue.intValue());
            }
        }

        imageView.setImage(SwingFXUtils.toFXImage(outputImage,null));
    }

    public void gotoMainPage(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent parent = loader.load();
        Scene scene = new Scene(parent);
        Stage stage = (Stage) buttonBack.getScene().getWindow();
        stage.setScene(scene);
    }
}
