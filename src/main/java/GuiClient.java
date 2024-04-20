
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Node;

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
	Label gameStatus;
	GridPane yourGrid;
	GridPane enemyGrid;
	ComboBox row1;
	ComboBox row2;
	ComboBox col1;
	ComboBox col2;
	Button placeShip;
	ArrayList<Integer> shipSizes;
	int shipIndex;

	VBox vbox1;  //server testing
	HBox hbox1;  //server testing
	HBox hbox2; //server testing
	HBox hbox3; //server testing

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
						gameStatus.setText("Place ship size: " + shipSizes.get(shipIndex));
					} else if (gameData.placeShip) {
						if (gameData.validPlacement) {
							if (gameData.allShipsPlaced) {
								gameStatus.setText("All ships placed");
								hbox3.setVisible(false);
								hbox3.setDisable(true);
							}
							else {
								shipIndex++;
								gameStatus.setText("Place ship size: " + shipSizes.get(shipIndex));
							}
							int gridRow1 = gameData.r1 + 1;
							int gridCol1 = gameData.c1 + 1;
							int gridRow2 = gameData.r2 + 1;
							int gridCol2 = gameData.c2 + 1;
							System.out.println(gridRow1 + gridCol1 + gridRow2 + gridCol2);
							ArrayList<Integer> gridRows = new ArrayList<>();
							ArrayList<Integer> gridCols = new ArrayList<>();
							if (gridRow2 - gridRow1 == 0) {
								for (int i = Math.min(gridCol1, gridCol2); i <= Math.max(gridCol1, gridCol2); i++) {
									gridCols.add(i);
									gridRows.add(gridRow1);
								}
							}
							else {
								for (int i = Math.min(gridRow1, gridRow2); i <= Math.max(gridRow1, gridRow2); i++) {
									gridCols.add(gridCol2);
									gridRows.add(i);
								}
							}
							System.out.println("Cords to color"); {
								for (int i = 0; i < gridRows.size(); i++) {
									System.out.println(gridRows.get(i) + " " + gridCols.get(i));
								}
							}
							for(int i = 0; i < gridRows.size(); i++) {
								for (int j = 0; j < yourGrid.getChildren().size(); j++) {
									if (GridPane.getRowIndex(yourGrid.getChildren().get(j)).equals(gridRows.get(i))
									&& GridPane.getColumnIndex(yourGrid.getChildren().get(j)).equals(gridCols.get(i))) {
										yourGrid.getChildren().get(j).setStyle("-fx-background-color: black;" +
												"-fx-opacity: 1");
									}
								}
							}


						}
						else {
							gameStatus.setText("Invalid cords");
						}
						placeShip.setDisable(false);
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
		quitGame = new Button("Quit");
		gameStatus = new Label();
		row1 = new ComboBox<>();
		row2 = new ComboBox<>();
		col1 = new ComboBox<>();
		col2 = new ComboBox<>();
		shipIndex = 0;
		shipSizes = new ArrayList<>();
		shipSizes.add(2);
		shipSizes.add(3);
		shipSizes.add(3);
		shipSizes.add(4);
		shipSizes.add(5);
		placeShip = new Button("Place ship");
		yourGrid = new GridPane();
		yourGrid.setHgap(2); //horizontal spacing between elements in the grid
		yourGrid.setVgap(2); //vertical spacing between elements in the grid
		for (int i = 0; i < 11; i++) {
			for (int j = 0; j < 11; j++) {
				if (i == 0) {
					Label l = new Label(String.valueOf(j));  //dont want clickable
					l.setMinWidth(50);
					l.setMaxWidth(50);
					l.setMinHeight(50);
					l.setMaxHeight(50);
					l.setAlignment(Pos.CENTER);
					l.setStyle("-fx-border-color: black; -fx-border-width: 1px");
					yourGrid.add(l, j, i);
					if (j != 0) {
						col1.getItems().add(String.valueOf(j));
						col2.getItems().add(String.valueOf(j));
					}
				}
				else if (j == 0) {
					Label l = new Label(String.valueOf((char)(i + 64)));  //same as above
					l.setMinWidth(50);
					l.setMaxWidth(50);
					l.setMinHeight(50);
					l.setMaxHeight(50);
					l.setAlignment(Pos.CENTER);
					l.setStyle("-fx-border-color: black; -fx-border-width: 1px");
					yourGrid.add(l, j, i);
					row1.getItems().add(String.valueOf((char)(i + 64)));
					row2.getItems().add(String.valueOf((char)(i + 64)));
				}
				else {
					Button b = new Button();
					b.setDisable(true);
					b.setStyle("-fx-opacity: 1");
					b.setMinWidth(50);
					b.setMaxWidth(50);
					b.setMinHeight(50);
					b.setMaxHeight(50);
					yourGrid.add(b, j, i);
				}
			}
		}
		yourGrid.getChildren().get(0).setVisible(false);

		enemyGrid = new GridPane();
		enemyGrid.setHgap(2);
		enemyGrid.setVgap(2);

		//same grid for enemy
		for (int i = 0; i < 11; i++) {
			for (int j = 0; j < 11; j++) {
				if (i == 0) {
					Label l = new Label(String.valueOf(j));
					l.setMinWidth(50);
					l.setMaxWidth(50);
					l.setMinHeight(50);
					l.setMaxHeight(50);
					l.setAlignment(Pos.CENTER);
					l.setStyle("-fx-border-color: black; -fx-border-width: 1px");
					enemyGrid.add(l, j, i);

				}
				else if (j == 0) {
					Label l = new Label(String.valueOf((char)(i + 64)));
					l.setMinWidth(50);
					l.setMaxWidth(50);
					l.setMinHeight(50);
					l.setMaxHeight(50);
					l.setAlignment(Pos.CENTER);
					l.setStyle("-fx-border-color: black; -fx-border-width: 1px");
					enemyGrid.add(l, j, i);
				}
				else {
					Button b = new Button();
					b.setMinWidth(50);
					b.setMaxWidth(50);
					b.setMinHeight(50);
					b.setMaxHeight(50);
					enemyGrid.add(b, j, i);
				}
			}
		}
		enemyGrid.getChildren().get(0).setVisible(false);

		hbox1 = new HBox(15, yourGrid, enemyGrid);
		hbox2 = new HBox(15, quitGame, gameStatus);
		hbox3 = new HBox(10, row1, col1, row2, col2, placeShip);
		vbox1 = new VBox(10, hbox1, hbox2, hbox3);


		placeShip.setOnAction(e->{
			String r1 = String.valueOf(row1.getValue());
			String c1 = String.valueOf(col1.getValue());
			String r2 = String.valueOf(row2.getValue());
			String c2 = String.valueOf(col2.getValue());

			//elements in combobox are strings for better customization on gui
			//"" because we convert to string
			if (r1 != "null" && c1 != "null" && r2 != "null" && c2 != "null") {
				placeShip.setDisable(true);

				int intr1 = (int)r1.charAt(0) - 65;
				int intc1 = Integer.parseInt(c1) - 1;
				int intr2 = (int)r2.charAt(0) - 65;
				int intc2 = Integer.parseInt(c2) - 1;
				//convert into format that works with placeship function : rows and cols from 0 to 9
				GameInfo shipPlacement = new GameInfo();
				shipPlacement.placeShip = true;
				shipPlacement.r1 = intr1;
				shipPlacement.c1 = intc1;
				shipPlacement.r2 = intr2;
				shipPlacement.c2 = intc2;
				clientConnection.sendInfo(shipPlacement);
			}

		});

		quitGame.setOnAction(e->{
			stage.setScene(sceneMap.get("mainMenu"));
			sceneMap.put("gameplay", generateGameplayScene());
		});

		return new Scene(vbox1, 1300, 750);
	}


}
