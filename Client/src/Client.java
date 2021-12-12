
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import sun.util.BuddhistCalendar;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashSet;


public class Client extends Application {

	//Networking
	private static final String hostIP = "127.0.0.1";  //Modify this String to match the IP Address of Server if Server.java is running on a different machine
	private static Socket socket;
	private static PrintWriter to;
	private static BufferedReader from;
	private static ObjectInputStream fromServer = null;
	private static Thread respondToServer;

	//U
	private static Stage stage = null;
	private static Scene loginScene = null;

	//Data
	private static String user;
	private static VBox history;
	private static ArrayList<Item> allItems = new ArrayList<>();
	private static final ArrayList<Scene> scenes = new ArrayList<>();
	private static final ArrayList<Button> backButtons = new ArrayList<>();
	private static final ArrayList<VBox> itemHistories = new ArrayList<>();
	private static final ArrayList<GridPane> descriptionPanes = new ArrayList<>();
	private static final ArrayList<HashSet<GridPane>> removablesUponSale = new ArrayList<>();
	private static final ArrayList<Label> timerLabels = new ArrayList<>();

	//Extras
	private static final NumberFormat myFormat = NumberFormat.getInstance();
	private static final MediaPlayer enterAuctionSound = new MediaPlayer(new Media(new File("media/sound1.mp3").toURI().toString()));
	private static BuddhistCalendar buddhistCalendar;




	//===========================================================================================================
	//Default entry point for JavaFX applications - sets up login page for user upon startup

	@Override
	public void start(Stage primaryStage) {
		stage = primaryStage;

		stage.setTitle("ANTIQUE HAVEN");
		//List<String> x = Font.getFamilies();

		AnchorPane left = new AnchorPane();
		left.setPrefWidth(310);

		Image imgg = new Image("https://i.imgur.com/csKu4V2.jpg");
		BackgroundImage bImg = new BackgroundImage(imgg, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, new BackgroundSize(50,50,true,true,true,true));
		Background bGround = new Background(bImg);
		left.setBackground(bGround);

		Circle design1 = new Circle(150,160,150);
		DropShadow dropShadow = new DropShadow();
		dropShadow.setRadius(10);
		dropShadow.setOffsetX(7);
		dropShadow.setOffsetY(20);
		dropShadow.setColor(Color.color(0.8, 0.8, 0.8));
		design1.setEffect(dropShadow);
		design1.setStroke(Paint.valueOf("#f7f4f4"));

		Line line = new Line();
		line.setStartX(310);
		line.setStartY(0);
		line.setEndX(310);
		line.setEndY(500);
		line.setStroke(Color.color(0.3, 0.1, 0.8));
		line.setStrokeWidth(10);
		line.setEffect(new DropShadow());
		Line line2 = new Line();
		line2.setStartX(310);
		line2.setStartY(5);
		line2.setEndX(310);
		line2.setEndY(495);
		line2.setStroke(Color.color(0, 0, 0));
		line2.setStrokeWidth(5);

		Label welcomeMsg = new Label("   Welcome to\n Antique Haven -\n Online Auction!");
		welcomeMsg.setFont(Font.font("Broadway", FontWeight.BOLD,32));
		welcomeMsg.setTextFill(Paint.valueOf("#f7f4f4"));
		welcomeMsg.setPrefWidth(290);
		welcomeMsg.setLayoutX(10);
		welcomeMsg.setLayoutY(100);

		Label copyrightMsg = new Label("Copyright (c) 2021 | All Rights Reserved");
		copyrightMsg.setFont(Font.font("Comic Sans", FontPosture.ITALIC,10));
		copyrightMsg.setTextFill(Paint.valueOf("#f7f4f4"));
		copyrightMsg.setLayoutX(65);
		copyrightMsg.setLayoutY(470);

		left.getChildren().addAll(line,line2,design1,copyrightMsg,welcomeMsg);



		AnchorPane right = new AnchorPane();
		right.setPrefWidth(490);

		Image img = new Image("https://i.imgur.com/9yKjBZ4.jpg");
		BackgroundImage bImgg = new BackgroundImage(img, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, new BackgroundSize(50,50,true,true,true,true));
		Background bGroundg = new Background(bImgg);
		right.setBackground(bGroundg);

		Label login = new Label("Login");
		login.setFont(Font.font("Algerian", FontWeight.BOLD,40));
		login.setTextFill(Paint.valueOf("#f7f4f4"));
		login.setLayoutX(60);
		login.setLayoutY(80);

		Line line3 = new Line();
		line3.setStartX(60);
		line3.setStartY(125);
		line3.setEndX(420);
		line3.setEndY(125);
		line3.setStroke(Color.color(1, 1, 1));
		line3.setStrokeWidth(3);
		line3.setEffect(new DropShadow());

		Label username = new Label("Username");
		username.setFont(Font.font("Centaur", FontWeight.NORMAL,20));
		username.setTextFill(Paint.valueOf("#f7f4f4"));
		username.setLayoutX(70);
		username.setLayoutY(150);
		TextField usernameItself = new TextField();
		usernameItself.setLayoutX(188);
		usernameItself.setLayoutY(153);
		usernameItself.setPrefWidth(149);
		usernameItself.setPrefHeight(25);
		usernameItself.setFont(Font.font("Times New Roman", FontWeight.NORMAL,12));

		Label password = new Label("Password");
		password.setFont(Font.font("Centaur", FontWeight.NORMAL,20));
		password.setTextFill(Paint.valueOf("#f7f4f4"));
		password.setLayoutX(70);
		password.setLayoutY(190);
		PasswordField passwordItself = new PasswordField();
		passwordItself.setLayoutX(188);
		passwordItself.setLayoutY(190);

		Text invalid = new Text();
		invalid.setLayoutX(55);
		invalid.setLayoutY(250);
		invalid.setFont(Font.font("Bodoni MT", FontWeight.BOLD,14));
		invalid.setFill(Color.RED);

		Button signIn = new Button("Sign In");
		signIn.setLayoutX(250);
		signIn.setLayoutY(230);
		signIn.setPrefWidth(100);
		signIn.setTextFill(Color.BLACK);
		signIn.setStyle("-fx-border-radius: 4.0");
		DropShadow dropShadow1 = new DropShadow();
		dropShadow1.setRadius(5);
		dropShadow1.setOffsetX(7);
		dropShadow1.setOffsetY(5);
		dropShadow1.setColor(Color.color(0.2, 1, 0.8));
		signIn.setEffect(dropShadow1);
		signIn.setFont(Font.font("Bodoni MT", FontWeight.BOLD,15));

		Line line4 = new Line();
		line4.setStartX(30);
		line4.setStartY(290);
		line4.setEndX(75);
		line4.setEndY(290);
		line4.setStroke(Color.GRAY);
		line4.setStrokeWidth(2.5);
		Line line5 = new Line();
		line5.setStartX(107);
		line5.setStartY(290);
		line5.setEndX(450);
		line5.setEndY(290);
		line5.setStroke(Color.GRAY);
		line5.setStrokeWidth(2.5);
		Label or = new Label("OR");
		or.setFont(Font.font("Cooper Black", FontPosture.ITALIC,15));
		or.setTextFill(Paint.valueOf("#f7f4f4"));
		or.setLayoutX(80);
		or.setLayoutY(280);

		Label guest1 = new Label("Click ");
		guest1.setFont(Font.font("Eras Demi ITC", FontPosture.ITALIC,20));
		guest1.setTextFill(Paint.valueOf("#f7f4f4"));
		guest1.setLayoutX(60);
		guest1.setLayoutY(325);
		Button guestEnter = new Button("Here");
		guestEnter.setLayoutX(115);
		guestEnter.setLayoutY(325);
		guestEnter.setTextFill(Color.WHITE);
		guestEnter.setFont(Font.font("Eras Demi ITC", FontPosture.ITALIC,10));
		guestEnter.setStyle("-fx-background-color: red; -fx-border-radius: 4.0");
		Label guest2 = new Label("to enter as guest");
		guest2.setFont(Font.font("Eras Demi ITC", FontPosture.ITALIC,20));
		guest2.setTextFill(Paint.valueOf("#f7f4f4"));
		guest2.setLayoutX(161);
		guest2.setLayoutY(325);

		Button exit = new Button("Exit");
		exit.setLayoutX(445);
		exit.setLayoutY(470);
		exit.setTextFill(Color.BLACK);
		exit.setFont(Font.font("Bell MT", FontPosture.ITALIC,10));
		exit.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.exit(0);
			}
		});

		right.getChildren().addAll(login,line3,username, usernameItself,password, passwordItself,signIn,guest1,line4,or,line5,guest2,guestEnter,invalid,exit);

		BorderPane mainPane = new BorderPane();
		mainPane.setLeft(left);
		mainPane.setRight(right);
		loginScene = new Scene(mainPane, 800, 500);

		stage.setScene(loginScene);


		Button welcomeUser = new Button();
		welcomeUser.setLayoutY(-1);
		welcomeUser.setLayoutX(815);
		welcomeUser.setTextFill(Color.DARKCYAN);
		welcomeUser.setStyle("-fx-background-color: black");
		welcomeUser.setFont(Font.font("Jokerman", FontPosture.ITALIC,15));



		signIn.setOnAction(event -> {
			if(usernameItself.getText().equals("")|| passwordItself.getText().equals(""))
				invalid.setText("Please enter all login credentials.");
			else
			{
				String validity="";
				setUpNetworking();
				printToServer("validateLogin_" + usernameItself.getText() + "_" + passwordItself.getText());
				try {
					validity = from.readLine();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
				if(validity.equals("validLogin"))
				{


					enterAuctionSound.play();
					invalid.setText("");
					user = usernameItself.getText();
					welcomeUser.setText("WELCOME, " + user.toUpperCase() + "!");
					usernameItself.clear();
					passwordItself.clear();
					setUpAuction();
				}
				else if(validity.equals("invalidLogin"))
				{
					invalid.setText("Invalid Login. Please try again.");
					printToServer("disconnect");
					try {
						to.close();
						from.close();
						socket.close();

					} catch (IOException ex) {
						System.out.println("Couldn't close server connection upon invalid credentials");
					}
				}
				else
				{
					System.out.println("Didn't validate login properly");
				}

			}


		});

		guestEnter.setOnAction(event -> {
			enterAuctionSound.play();
			setUpNetworking();
			invalid.setText("");
			user = "a guest";
			usernameItself.clear();
			passwordItself.clear();
			setUpAuction();

		});

		stage.setScene(loginScene);
		stage.show();
	}



	//===========================================================================================================
	// Creates a socket connection to the central auction server and initializes IO

	private static void setUpNetworking() {
		try {
			// Create a socket to connect to the server
			socket = new Socket(hostIP, 5000);

			// Create an input stream to receive data from the server
			from = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			// Create an output stream to send data to the server
			to = new PrintWriter(socket.getOutputStream());
		}
		catch (IOException ex) {
			System.out.println("SERVER AINT UP!!");
		}


		respondToServer = new Thread(new Runnable() {
			@Override
			public void run() {
				String input;
				try {
					while ((input = from.readLine()) != null) {
						System.out.println("From server: " + input);
						processRequest(input);
					}
				}
				catch(SocketException e){
					System.out.println("Client logged out.");

				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		});

	}


	//===========================================================================================================
	//Sets up auction page upon valid login by reading in the current status of all items, active bids, countdown timers, bid histories, etc.
	//Starts the thread to interpret all incoming server messages upon updates


	private static void setUpAuction() {

		printToServer("setUp_" + user);
		ArrayList<String> auctionActivityThusFar = new ArrayList<>();
		try {
			fromServer = new ObjectInputStream(socket.getInputStream());
			allItems = (ArrayList<Item>) fromServer.readUnshared();
			auctionActivityThusFar = (ArrayList<String>) fromServer.readUnshared();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}

		respondToServer.start();








		/* Auction Homepage*/

		BorderPane home = new BorderPane();
		GridPane items = new GridPane();
		items.setPadding(new Insets(-8, 10, 10, 60));
		items.setVgap(50);
		items.setHgap(69);
		Image img3 = new Image("https://i.imgur.com/LRycJPo.jpg");
		BackgroundImage bImg3 = new BackgroundImage(img3, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, new BackgroundSize(50,50,true,true,true,true));
		Background bGround3 = new Background(bImg3);
		home.setBackground(bGround3);

		Label itemsForSale = new Label("Items for Sale:");
		itemsForSale.setFont(Font.font("Vivaldi", FontWeight.BOLD,70));
		itemsForSale.setTextFill(Color.ANTIQUEWHITE);

		Line line6 = new Line();
		line6.setStartX(5);
		line6.setStartY(79);
		line6.setEndX(630);
		line6.setEndY(79);
		line6.setStroke(Color.color(1, 0.2, 0.2));
		line6.setStrokeWidth(3);
		Label fyi = new Label("(Click for more info)");
		fyi.setFont(Font.font("Bookman Old Style", FontPosture.ITALIC,16));
		fyi.setTextFill(Color.WHITE);
		fyi.setLayoutX(5);
		fyi.setLayoutY(90);
		Button welcomeUser = new Button();
		welcomeUser.setLayoutY(-1);
		welcomeUser.setLayoutX(815);
		welcomeUser.setTextFill(Color.DARKCYAN);
		welcomeUser.setStyle("-fx-background-color: black");
		welcomeUser.setFont(Font.font("Jokerman", FontPosture.ITALIC,15));

		if(user.equals("a guest"))
			welcomeUser.setText("WELCOME, GUEST!");
		else
			welcomeUser.setText("WELCOME, " + user.toUpperCase() + "!");

		Line line7 = new Line();
		line7.setStartX(700);
		line7.setStartY(83);
		line7.setEndX(700);
		line7.setEndY(125);
		line7.setStroke(Color.color(1, 1, 1));
		line7.setStrokeWidth(2.5);

		Line line10 = new Line();
		line10.setStartX(700);
		line10.setStartY(83);
		line10.setEndX(960);
		line10.setEndY(83);
		line10.setStroke(Color.color(1, 1, 1));
		line10.setStrokeWidth(5);

		Line line11 = new Line();
		line11.setStartX(960);
		line11.setStartY(83);
		line11.setEndX(960);
		line11.setEndY(124);
		line11.setStroke(Color.color(1, 1, 1));
		line11.setStrokeWidth(5);


		Button recentActivity = new Button("Auction Activity:");
		recentActivity.setLayoutY(80);
		recentActivity.setLayoutX(700);
		recentActivity.setPrefWidth(258);
		recentActivity.setTextFill(Color.WHITE);
		recentActivity.setStyle("-fx-border-color: white; -fx-background-color: black");
		recentActivity.setFont(Font.font("Castellar", FontWeight.SEMI_BOLD,20));


		StackPane stack = new StackPane();
		history = new VBox(5);
		history.setStyle("-fx-border-color: white; -fx-background-color: black; -fx-border-radius: 3.0");
		history.setPrefWidth(270);
		history.setPrefHeight(280);

		Label SOMETEST = new Label("Brayden placed a bid r $7283489!");
		SOMETEST.setAlignment(Pos.CENTER);
		SOMETEST.setTextFill(Color.WHITE);
		SOMETEST.setFont(Font.font("Eras Demi ITC", FontWeight.SEMI_BOLD,11));
		for(String s : auctionActivityThusFar)
		{
			s= Character.toUpperCase(s.charAt(0)) + s.substring(1);
			s = " " + s;

			Label activity = new Label(s);
			activity.setMaxHeight(100);
			activity.setWrapText(true);
			activity.setFont(Font.font("Eras Demi ITC", FontWeight.NORMAL,11));
			if(s.contains("has entered the auction.") || s.contains("has left the auction."))
			{
				activity.setTextFill(Color.WHITE);
			}
			else if(s.contains("bid")) {
				activity.setTextFill(Color.ROSYBROWN);
			}
			else
			{
				activity.setTextFill(Color.ORANGERED);
				activity.setFont(Font.font("Eras Demi ITC", FontWeight.SEMI_BOLD,11));
			}
			history.getChildren().add(activity);
		}


		ScrollPane histom = new ScrollPane(history);
		histom.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
		histom.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		histom.setFitToHeight(true);

		Line line8 = new Line();
		line8.setStartX(0);
		line8.setStartY(0);
		line8.setEndX(0);
		line8.setEndY(280);
		line8.setStroke(Color.color(1, 1, 1));
		line8.setStrokeWidth(2.5);
		Line line9 = new Line();
		line9.setStartX(0);
		line9.setStartY(280);
		line9.setEndX(270);
		line9.setEndY(280);
		line9.setStroke(Color.color(1, 1, 1));
		line9.setStrokeWidth(5);

		stack.getChildren().addAll(history,histom,line8,line9);
		StackPane.setAlignment(line8,Pos.CENTER_LEFT);
		StackPane.setAlignment(line9,Pos.BOTTOM_CENTER);
		AnchorPane qwer = new AnchorPane();
		qwer.setPrefHeight(110);
		qwer.getChildren().addAll(itemsForSale,line6,fyi,welcomeUser,recentActivity,line7,line10,line11);






		/* Item specific scenes */

		for(int loop =0;loop<allItems.size();loop++)
		{
			Button item = new Button();
			item.setStyle("-fx-border-color: blue; -fx-border-radius: 2.5; -fx-border-width: 2px; -fx-background-image: url(" + allItems.get(loop).iconPic + ")");
			item.setPrefWidth(250);
			item.setPrefHeight(250);
			switch (loop) {
				case 0:
					items.add(item, 0, 0);
					break;
				case 1:
					items.add(item, 1, 0);
					break;
				case 2:
					items.add(item, 0, 1);
					break;
				case 3:
					items.add(item,1,1);
					break;
				case 4:
					items.add(item,2,1);
					break;
			}
			BorderPane itemPane = new BorderPane();

			Image backItem1 = new Image("https://i.imgur.com/7wcDRcO.jpg");
			BackgroundImage bImgItem1 = new BackgroundImage(backItem1, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, new BackgroundSize(50,50,true,true,true,true));
			Background bGroundItem1 = new Background(bImgItem1);

			itemPane.setBackground(bGroundItem1);



			Image item1Pic = new Image(allItems.get(loop).itemPic);
			ImageView imageView1 = new ImageView();
			imageView1.setPreserveRatio(true);
			imageView1.setFitHeight(500);
			imageView1.setImage(item1Pic);

			GridPane TL = new GridPane();
			TL.setVgap(30);


			Label timerLabel = new Label();
			timerLabel.setTextFill(Color.RED);
			timerLabel.setFont(Font.font("Tw Cen MT", 50));
			timerLabels.add(timerLabel);

			Button goBack1 = new Button();
			goBack1.setPrefWidth(40);
			goBack1.setPrefHeight(30);
			goBack1.setTextFill(Color.BLACK);
			goBack1.setFont(Font.font("Bell MT", FontWeight.NORMAL,12));
			goBack1.setStyle("-fx-background-image: url('https://i.imgur.com/L0SIMeG.png'); -fx-border-color: white; -fx-border-radius: 2.5; -fx-border-width: 1px");
			backButtons.add(goBack1);


			TL.add(goBack1,0,0);
			TL.add(timerLabel,0,1);



			Label item1Label = new Label(allItems.get(loop).name);
			item1Label.setFont(Font.font("Vivaldi", FontWeight.SEMI_BOLD,60));
			item1Label.setTextFill(Color.AQUA);

			StackPane stack1 = new StackPane();
			VBox history1 = new VBox();
			history1.setStyle("-fx-border-color: white; -fx-background-color: black; -fx-border-radius: 3.0");
			history1.setPrefWidth(270);
			history1.setPrefHeight(400);
			itemHistories.add(history1);

			ScrollPane histom1 = new ScrollPane(history1);
			histom1.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
			histom1.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
			histom1.setPrefHeight(130);
			histom1.setFitToHeight(true);
			histom1.setHmax(150);
			for(String hist : allItems.get(loop).history)
			{
				hist = Character.toUpperCase(hist.charAt(0))+ hist.substring(1);
				Label activity = new Label(hist);
				activity.setAlignment(Pos.CENTER);
				activity.setWrapText(true);
				activity.setTextFill(Color.WHITE);
				activity.setFont(Font.font("Eras Demi ITC", FontWeight.NORMAL,11));
				if(hist.equals("SOLD!"))
				{
					activity.setTextFill(Color.RED);
					activity.setFont(Font.font("Eras Demi ITC", FontWeight.NORMAL,15));
				}
				history1.getChildren().add(activity);
			}
			stack1.getChildren().addAll(history1,histom1);

			BorderPane topOfItem1 = new BorderPane();
			topOfItem1.setLeft(TL);
			topOfItem1.setCenter(item1Label);
			topOfItem1.setRight(stack1);



			GridPane descriptionPane1 = new GridPane();
			descriptionPane1.setVgap(15);
			descriptionPane1.setPadding(new Insets(40,10,10,30));
			Label description1 = new Label(allItems.get(loop).description);
			description1.setMaxWidth(600);
			description1.setWrapText(true);
			description1.setFont(Font.font("Forte", 28));
			description1.setTextFill(Color.WHITE);

			GridPane bidding1 = new GridPane();
			bidding1.setHgap(20);

			Label enterBidAmount1 = new Label("Enter bid amount: ");
			enterBidAmount1.setFont(Font.font("Impact", 23));
			enterBidAmount1.setTextFill(Color.GREEN);

			TextField bidAmount1 = new TextField();
			bidAmount1.setFont(Font.font("Times New Roman", FontWeight.NORMAL,17));
			bidAmount1.setPrefWidth(150);

			Label errorSuccess1 = new Label();
			errorSuccess1.setWrapText(true);
			errorSuccess1.setFont(Font.font("Britannic Bold", FontWeight.BOLD,17));
			errorSuccess1.setTextFill(Color.RED);


			bidding1.add(enterBidAmount1,0,0);
			bidding1.add(bidAmount1,1,0);




			GridPane bidder1 = new GridPane();
			bidder1.setHgap(42);
			Button bid1 = new Button("Bid");
			bid1.setPrefWidth(80);
			bid1.setFont(Font.font("Impact", 18));
			bid1.setStyle("-fx-border-color: blue; -fx-background-color: green; -fx-border-radius: 3.0");
			int finalLoop = loop;
			bid1.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					try {
						printToServer("itemBid_" + allItems.get(finalLoop).itemNum + "_" + Integer.parseInt(bidAmount1.getText()) + "_" + user);
					}
					catch (NumberFormatException e)
					{
						ObservableList<Node> x = ((BorderPane)scenes.get(allItems.get(finalLoop).itemNum-1).getRoot()).getChildren();
						((Label)((GridPane)((GridPane) x.get(2)).getChildren().get(2)).getChildren().get(1)).setText("Please enter an integer.");
					}
				}
			});
			bidder1.add(bid1,6,0);
			bidder1.add(errorSuccess1,7,0);


			GridPane OR = new GridPane();
			OR.setHgap(2);
			Line line12 = new Line();
			line12.setStartX(0);
			line12.setStartY(0);
			line12.setEndX(50);
			line12.setEndY(0);
			line12.setStroke(Color.GRAY);
			line12.setStrokeWidth(2.5);
			Line line13 = new Line();
			line13.setStartX(0);
			line13.setStartY(0);
			line13.setEndX(100);
			line13.setEndY(0);
			line13.setStroke(Color.GRAY);
			line13.setStrokeWidth(2.5);
			Label or1 = new Label("OR");
			or1.setFont(Font.font("Cooper Black", FontPosture.ITALIC,15));
			or1.setTextFill(Paint.valueOf("#f7f4f4"));
			or1.setLayoutX(80);
			or1.setLayoutY(280);
			OR.add(line12,0,0);
			OR.add(or1, 1,0);
			OR.add(line13,2,0);


			GridPane instantPurchase1 = new GridPane();
			instantPurchase1.setHgap(10);
			Label instantly = new Label("Instantly");
			instantly.setFont(Font.font("Cooper Black", FontWeight.BLACK,18));
			instantly.setTextFill(Color.WHITE);

			HashSet<GridPane> removables = new HashSet<>();
			removables.add(bidder1);
			removables.add(bidding1);
			removables.add(OR);
			removables.add(instantPurchase1);
			removablesUponSale.add(removables);

			Button instantiation = new Button("Purchase");
			instantiation.setFont(Font.font("Cooper Black", FontWeight.BLACK,18));
			instantiation.setStyle("-fx-border-color: black; -fx-background-color: #ff8c00; -fx-border-radius: 3.0; -fx-border-width: 2px");
			instantiation.setTextFill(Color.WHITE);
			Label thisItemFor1 = new Label("this item for $"+ myFormat.format(allItems.get(loop).highBid) + "!");
			thisItemFor1.setFont(Font.font("Cooper Black", FontWeight.BLACK,18));
			thisItemFor1.setTextFill(Color.WHITE);
			instantiation.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					printToServer("purchase_" + allItems.get(finalLoop).itemNum + "_" + user);
				}

			});

			instantPurchase1.add(instantly,0,0);
			instantPurchase1.add(instantiation,1,0);
			instantPurchase1.add(thisItemFor1,2,0);



			descriptionPane1.add(description1,0,0);
			descriptionPane1.add(bidding1,0,3);
			descriptionPane1.add(bidder1,0,4);
			descriptionPane1.add(OR,0,5);
			descriptionPane1.add(instantPurchase1,0,6);


			descriptionPanes.add(descriptionPane1);




			GridPane bidStatus1 = new GridPane();
			bidStatus1.setVgap(2);
			bidStatus1.setPadding(new Insets(0,10,50,30));
			Label starting_curr1 = new Label("Starting Bid: ");
			starting_curr1.setFont(Font.font("NSimSun", FontWeight.BOLD,35));
			starting_curr1.setTextFill(Color.DARKGOLDENROD);
			Line line15 = new Line();
			line15.setStartX(0);
			line15.setStartY(0);
			line15.setEndX(220);

			line15.setEndY(0);
			line15.setStroke(Color.RED);
			line15.setStrokeWidth(2.5);
			Label priceByName1 = new Label("$" + myFormat.format(allItems.get(loop).startingBid));
			priceByName1.setFont(Font.font("Monotype Corsiva", FontWeight.BOLD,50));
			priceByName1.setTextFill(Color.DARKGOLDENROD);

			if(allItems.get(loop).currBid!=0)
			{
				line15.setEndX(200);
				starting_curr1.setText("Current Bid:");
				priceByName1.setText("$" + myFormat.format(allItems.get(loop).currBid) + " by " + allItems.get(loop).currOwner);

			}
			bidStatus1.add(starting_curr1,0,0);
			bidStatus1.add(line15,0,1);
			bidStatus1.add(priceByName1,0,4);




			itemPane.setTop(topOfItem1);
			itemPane.setLeft(imageView1);
			itemPane.setCenter(descriptionPane1);
			itemPane.setBottom(bidStatus1);
			Scene itemScene1 = new Scene(itemPane,1000,800);
			scenes.add(itemScene1);



			if(allItems.get(loop).sold)
			{
				ObservableList<Node> x = ((BorderPane)scenes.get(loop).getRoot()).getChildren();
				x.remove(3);

				Line line14 = new Line();
				line14.setStartX(0);
				line14.setStartY(0);
				line14.setEndX(600);
				line14.setEndY(0);
				line14.setStroke(Color.GRAY);
				line14.setStrokeWidth(2.5);
				Label SOLDTO1 = new Label("THIS ITEM HAS BEEN SOLD TO " + allItems.get(loop).currOwner.toUpperCase() + " FOR $" + myFormat.format(allItems.get(loop).currBid) + "!");
				SOLDTO1.setFont(Font.font("Mongolian Baiti", FontWeight.BOLD,35));
				SOLDTO1.setWrapText(true);
				SOLDTO1.setStyle("-fx-text-alignment: center");
				SOLDTO1.setTextFill(Color.RED);

				descriptionPanes.get(loop).getChildren().removeAll(removablesUponSale.get(loop));
				descriptionPanes.get(loop).add(line14,0,3);
				descriptionPanes.get(loop).add(SOLDTO1,0,7);


			}
			item.setOnAction(event -> stage.setScene(itemScene1));
		}

		items.add(stack,2,0);


		Button logout = new Button("Logout");
		logout.setLayoutX(920);
		logout.setTextFill(Color.RED);
		logout.setStyle("-fx-border-color: red; -fx-background-color: blue");
		logout.setFont(Font.font("Candara", FontPosture.ITALIC,15));
		logout.setOnAction(event -> {
			printToServer("disconnect_" + user);
			try {
				to.close();
				from.close();
				socket.close();
				fromServer.close();
			} catch (IOException ex) {
				System.out.println("Couldn't close server connection upon hitting logout");
			}
			stage.setScene(loginScene);
		});

		AnchorPane qwert = new AnchorPane();
		qwert.setPrefHeight(40);
		qwert.getChildren().addAll(logout);


		home.setTop(qwer);
		home.setCenter(items);
		home.setBottom(qwert);

		Scene homeScreen = new Scene(home,1000,800);

		for(Button button : backButtons)
		{
			button.setOnAction(event -> stage.setScene(homeScreen));
		}


		stage.setScene(homeScreen);
	}



	//===========================================================================================================
	// Method to communicate TO server
	private static void printToServer(String commandString) {
		System.out.println("Sending: " + commandString);
		to.println(commandString);
		to.flush();
	}


	//===========================================================================================================
	// Interprets all updates from server including bids from other clients, sales, timers, etc

	private static synchronized void processRequest(String command){

		String[] split = command.split("_");

		if(split[0].equals("bidOn"))
		{
			ObservableList<Node> x = ((BorderPane)scenes.get(Integer.parseInt(split[1])-1).getRoot()).getChildren();
			int itemNum = Integer.parseInt(split[1]);
			int amount = Integer.parseInt(split[2]);
			String custo = split[3];
			MediaPlayer mediaPlayer2 = new MediaPlayer(new Media(new File("media/sound2.mp3").toURI().toString()));
			mediaPlayer2.play();


			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					((Label)((GridPane)((GridPane) x.get(2)).getChildren().get(2)).getChildren().get(1)).setText("");

					((Label)((GridPane)x.get(3)).getChildren().get(0)).setText("Current Bid:");
					((Line)((GridPane)x.get(3)).getChildren().get(1)).setEndX(200);
					((Label)((GridPane)x.get(3)).getChildren().get(2)).setText("$" + myFormat.format(amount) + " by " + custo);


					Label aBid = new Label(Character.toUpperCase(custo.charAt(0)) + custo.substring(1) + " bid " + myFormat.format(amount) + "!");
					aBid.setAlignment(Pos.CENTER);
					aBid.setWrapText(true);
					aBid.setTextFill(Color.WHITE);
					aBid.setFont(Font.font("Eras Demi ITC", FontWeight.NORMAL,11));
					itemHistories.get(itemNum-1).getChildren().add(aBid);


					Label activity;
					if(itemNum==3 || itemNum==4)
						activity = new Label(" " + Character.toUpperCase(custo.charAt(0)) + custo.substring(1) + " bid $" + myFormat.format(amount) + " on " + allItems.get(itemNum-1).name + "!");
					else
						activity = new Label(" " + Character.toUpperCase(custo.charAt(0)) + custo.substring(1) + " bid $" + myFormat.format(amount) + " on the " + allItems.get(itemNum-1).name + "!");
					StackPane.setAlignment(activity,Pos.CENTER);
					activity.setAlignment(Pos.CENTER);
					activity.setWrapText(true);
					activity.setFont(Font.font("Eras Demi ITC", FontWeight.NORMAL,11));
					activity.setTextFill(Color.ROSYBROWN);
					history.getChildren().add(activity);

				}
			});
		}
		else if(split[0].equals("invalidBid"))
		{
			ObservableList<Node> x = ((BorderPane)scenes.get(Integer.parseInt(split[1])-1).getRoot()).getChildren();
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					if(split[2].equals("low"))
						((Label)((GridPane)((GridPane) x.get(2)).getChildren().get(2)).getChildren().get(1)).setText("Error: Bid amount is too low.");
					else
						((Label)((GridPane)((GridPane) x.get(2)).getChildren().get(2)).getChildren().get(1)).setText("Purchase the item for less!");
				}
			});

		}
		else if(split[0].equals("left") || split[0].equals("joined"))
		{

			Label activity = new Label();
			if(split[0].equals("left"))
				activity.setText(" " + Character.toUpperCase(split[1].charAt(0)) +  split[1].substring(1) + " has left the auction.");
			else
				activity.setText(" " + Character.toUpperCase(split[1].charAt(0)) +  split[1].substring(1) + " has entered the auction.");
			activity.setMaxHeight(100);
			activity.setTextFill(Color.WHITE);
			activity.setWrapText(true);
			activity.setFont(Font.font("Eras Demi ITC", FontWeight.NORMAL,11));
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					history.getChildren().add(activity);
				}
			});
		}
		else if(split[0].equals("sold"))
		{
			MediaPlayer mediaPlayer2 = new MediaPlayer(new Media(new File("media/sound2.mp3").toURI().toString()));
			mediaPlayer2.play();
			int itemNum = Integer.parseInt(split[1]);
			int amount = Integer.parseInt(split[2]);
			String custo = split[3];

			allItems.get(itemNum-1).sold=true;

			Label sold = new Label("SOLD!");
			sold.setAlignment(Pos.CENTER);
			sold.setTextFill(Color.RED);
			sold.setFont(Font.font("Eras Demi ITC", FontWeight.NORMAL,15));



			Label activity;
			if(itemNum==3 || itemNum==4)
				activity = new Label(allItems.get(itemNum-1).name + " has been sold to " + custo + " for $" + myFormat.format(amount) + "!");
			else
				activity = new Label("The " + allItems.get(itemNum-1).name + " has been sold to " + custo + " for $" + myFormat.format(amount) + "!");

			activity.setAlignment(Pos.CENTER);
			activity.setWrapText(true);
			activity.setFont(Font.font("Eras Demi ITC", FontWeight.SEMI_BOLD,11));
			activity.setTextFill(Color.ORANGERED);



			Line line14 = new Line();
			line14.setStartX(0);
			line14.setStartY(0);
			line14.setEndX(600);
			line14.setEndY(0);
			line14.setStroke(Color.GRAY);
			line14.setStrokeWidth(2.5);
			Label SOLDTO1 = new Label("THIS ITEM HAS BEEN SOLD TO " + custo.toUpperCase() + " FOR $" + myFormat.format(amount) + "!");
			SOLDTO1.setFont(Font.font("Mongolian Baiti", FontWeight.BOLD,35));
			SOLDTO1.setWrapText(true);
			SOLDTO1.setStyle("-fx-text-alignment: center");
			SOLDTO1.setTextFill(Color.RED);

			Label aBid = new Label(Character.toUpperCase(custo.charAt(0)) + custo.substring(1) + " bid " + myFormat.format(amount) + "!");
			aBid.setAlignment(Pos.CENTER);
			aBid.setWrapText(true);
			aBid.setTextFill(Color.WHITE);
			aBid.setFont(Font.font("Eras Demi ITC", FontWeight.NORMAL,11));



			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					descriptionPanes.get(itemNum-1).getChildren().removeAll(removablesUponSale.get(itemNum-1));
					timerLabels.get(itemNum-1).setText("");
					descriptionPanes.get(itemNum-1).add(line14,0,3);
					descriptionPanes.get(itemNum-1).add(SOLDTO1,0,7);
					itemHistories.get(itemNum-1).getChildren().add(aBid);
					itemHistories.get(itemNum-1).getChildren().add(sold);
					history.getChildren().add(activity);

					ObservableList<Node> x = ((BorderPane)scenes.get(itemNum-1).getRoot()).getChildren();

					x.remove(3);
				}
			});
		}
		else if(split[0].equals("soldToYou"))
		{
			int itemNum = Integer.parseInt(split[1]);
			int amount = Integer.parseInt(split[2]);

			Label SOLDTO1 = new Label("Congratulations, this item has been sold to you for $" + myFormat.format(amount) + "!");
			SOLDTO1.setFont(Font.font("Stencil", FontWeight.BOLD,35));
			SOLDTO1.setWrapText(true);
			SOLDTO1.setStyle("-fx-text-alignment: center");
			SOLDTO1.setTextFill(Color.SPRINGGREEN);


			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					descriptionPanes.get(itemNum-1).getChildren().remove(2);
					descriptionPanes.get(itemNum-1).add(SOLDTO1,0,7);

				}
			});
		}
		else if(split[0].equals("time"))
		{
			int itemNum = Integer.parseInt(split[1]);
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					timerLabels.get(itemNum-1).setText(split[2]);
				}
			});

		}
		else if(split[0].equals("timerDone"))
		{
			MediaPlayer mediaPlayer2 = new MediaPlayer(new Media(new File("media/sound2.mp3").toURI().toString()));
			mediaPlayer2.play();
			int itemNum = Integer.parseInt(split[1]);


			int amount = Integer.parseInt(split[2]);
			String custo = split[3];

			allItems.get(itemNum-1).sold=true;

			Label sold = new Label("SOLD!");
			sold.setAlignment(Pos.CENTER);
			sold.setTextFill(Color.RED);
			sold.setFont(Font.font("Eras Demi ITC", FontWeight.NORMAL,15));


			Label activity;
			if(itemNum==3 || itemNum==4)
				activity = new Label(allItems.get(itemNum-1).name + " has been sold to " + custo + " for $" + myFormat.format(amount) + "!");
			else
				activity = new Label("The " + allItems.get(itemNum-1).name + " has been sold to " + custo + " for $" + myFormat.format(amount) + "!");

			activity.setAlignment(Pos.CENTER);
			activity.setWrapText(true);
			activity.setFont(Font.font("Eras Demi ITC", FontWeight.SEMI_BOLD,11));
			activity.setTextFill(Color.ORANGERED);

			Line line14 = new Line();
			line14.setStartX(0);
			line14.setStartY(0);
			line14.setEndX(600);
			line14.setEndY(0);
			line14.setStroke(Color.GRAY);
			line14.setStrokeWidth(2.5);
			Label SOLDTO1 = new Label();
			if(custo.equals(user)) {
				SOLDTO1.setText("Congratulations, this item has been sold to you for $" + myFormat.format(amount) + "!");
				SOLDTO1.setFont(Font.font("Stencil", FontWeight.BOLD,35));
				SOLDTO1.setTextFill(Color.SPRINGGREEN);
			}
			else
			{
				SOLDTO1.setText("THIS ITEM HAS BEEN SOLD TO " + custo.toUpperCase() + " FOR $" + myFormat.format(amount) + "!");
				SOLDTO1.setFont(Font.font("Mongolian Baiti", FontWeight.BOLD,35));
				SOLDTO1.setTextFill(Color.RED);
			}
			SOLDTO1.setWrapText(true);
			SOLDTO1.setStyle("-fx-text-alignment: center");



			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					timerLabels.get(itemNum-1).setText("");
					itemHistories.get(itemNum-1).getChildren().add(sold);
					history.getChildren().add(activity);

					descriptionPanes.get(itemNum-1).getChildren().removeAll(removablesUponSale.get(itemNum-1));
					descriptionPanes.get(itemNum-1).add(line14,0,3);
					descriptionPanes.get(itemNum-1).add(SOLDTO1,0,7);

					ObservableList<Node> x = ((BorderPane)scenes.get(itemNum-1).getRoot()).getChildren();

					x.remove(3);
				}
			});
		}
		else
			System.out.println("ERROR: UNEXPECTED MESSAGE FROM SERVER: " + command);


	}



	//===========================================================================================================

	public static void main(String[] args) {
		launch(args);
	}

}
