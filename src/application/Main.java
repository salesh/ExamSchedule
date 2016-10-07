//Saša Cvetković 171/2013
package application;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
	public static Stage pomPrimaryStage = null;

	@Override
	public void start (Stage primaryStage){
		try {
			pomPrimaryStage = primaryStage;
			Parent root = FXMLLoader.load(getClass().getResource("schema.fxml"));
			Scene scene = new Scene(root);
			pomPrimaryStage.setScene(scene);
			pomPrimaryStage.show();
			pomPrimaryStage.setTitle("V05P1");
			pomPrimaryStage.getIcons().add(new Image(("file:resources/dat.png")));
			scene.getStylesheets().addAll(this.getClass().getResource("style.css").toExternalForm());

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

}
