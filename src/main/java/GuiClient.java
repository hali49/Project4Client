
import java.util.HashMap;
import javafx.scene.control.Label;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class GuiClient extends Application{

	Stage stage; //easier scene switching
	HashMap<String, Scene> sceneMap; //scene swtiching
	Client clientConnection;


	//menu screen
	VBox mainMenuVbox;
	Button onlineGameButton;
	Button offlineGameButton;
	//menu screen

	//matchmaking screen
	Label matchmakingLabel;
	//matchmaking screen

	//gameplay screen
	Button quitGame; //for server testing
	Label gameFound; //for server testing
	//gameplay screen
	//keep for reference
	TextField c1;
	Button b1;
	ListView<String> listItems2;
	//keep for reference
	
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		clientConnection = new Client(data->{
				Platform.runLater(()->{
					GameInfo gameData = (GameInfo)data;
					if (gameData.gameFound) {
						stage.setScene(sceneMap.get("gameplay"));
					}


			});
		});
							
		clientConnection.start();
		//IMPORTANT scene switching/now if you want a button to change scene you can
		//declare that in the scene creation function
		stage = primaryStage;
		sceneMap = new HashMap<String, Scene>();
		sceneMap.put("mainMenu",  generateMainMenu());
		sceneMap.put("matchmaking", generateMatchmaking());
		sceneMap.put("gameplay", generateGameplayScene());

		///Keep for reference
		c1 = new TextField();
		b1 = new Button("Send");
		b1.setOnAction(e->{
			GameInfo toSendDog = new GameInfo();
			clientConnection.sendInfo(toSendDog);
			c1.clear();});
		//Keep for reference

		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });

		primaryStage.setScene(sceneMap.get("mainMenu"));
		primaryStage.setTitle("Battleships");
		primaryStage.show();
	}

	public Scene generateMainMenu() {
		onlineGameButton = new Button("Online");
		offlineGameButton = new Button("Offline");
		mainMenuVbox = new VBox(20, onlineGameButton, offlineGameButton);


		onlineGameButton.setOnAction(e->{
			stage.setScene(sceneMap.get("matchmaking"));
			GameInfo data = new GameInfo();
			data.lookingForGame = true;
			clientConnection.sendInfo(data);

		});

		return new Scene(mainMenuVbox, 400, 300);
	}

	public Scene generateMatchmaking() {
		matchmakingLabel = new Label("Matchmaking");
		return  new Scene(matchmakingLabel, 400, 300);
	}

	public Scene generateGameplayScene() {
		gameFound = new Label("Game found");
		quitGame = new Button("Quit");
		VBox temp = new VBox(gameFound, quitGame);

		quitGame.setOnAction(e->{
			stage.setScene(sceneMap.get("mainMenu"));
		});

		return new Scene(temp, 600, 400);
	}

}
