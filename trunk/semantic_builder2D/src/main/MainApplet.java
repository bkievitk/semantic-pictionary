package main;


import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.io.IOException;

import featuregame.FeatureGameGUI;
import featuregame.FeatureGameSimpleDescribeGUI;
import featuregame.FeatureGameSimpleGuessGUI;
import iomanager.FlagModel;
import iomanager.IOManager;
import iomanager.IOWeb;
import iomanager.LoadModel;
import iomanager.LoadWord;
import iomanager.SaveGuess;
import iomanager.SaveModel;

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import templates.CreatorPanel;
import templates.IdentifierPanel;
import templates.PanelLogin;
import templates.PanelLoginCallback;
import templates.UserMessage;
import templates.UserMessagePanel;
import templates.WindowOptions;
import tools.UserCredentials;
import tools.WordPair;

import modelTools.GeonModel;

import creator2DNoTree.*;
import creator2DTree.*;
import creator3DTree.*;

/**
 * This will create an applet or the main will create a JFrame.
 * These will then be set up to be a creator for the user.
 * READY
 * @author bkievitk
 */

public class MainApplet extends JApplet {

	private static final long serialVersionUID = -6371555986386040799L;
	
	public static final String GAME_2D_TREE = 			"2DTree";
	public static final String GAME_2D_NO_TREE = 		"2DNoTree";
	public static final String GAME_3D_TREE = 			"3DTree";
	public static final String GAME_SUBJECT_POOL_1 =	"subjetPoolExperiment1";
	public static final String GAME_FEATURE = 			"feature";
	public static final String GAME_MECHANICAL_TURK_1 = "mechanicalTurk1";
	public static final String GAME_MECHANICAL_TURK_2 = "mechanicalTurk2";
	public static final String GAME_FEATURE_SIMPLE = 	"featureSimple";
	
	public static final String[] GAME_TYPES = {	GAME_2D_TREE + ":build", 		GAME_2D_TREE + ":identify",
												GAME_2D_NO_TREE + ":build", 	GAME_2D_NO_TREE + ":identify",
												GAME_3D_TREE + ":build",  		GAME_3D_TREE + ":identify", 
												GAME_SUBJECT_POOL_1 + ":build",
												GAME_FEATURE,
												GAME_MECHANICAL_TURK_1,
												GAME_MECHANICAL_TURK_2,
												GAME_FEATURE_SIMPLE + ":describe", GAME_FEATURE_SIMPLE + ":guess",
												"1"};
	
	/**
	 * Set up as a JFrame.
	 * @param args
	 */
	public static void main(String[] args) {

		// Init images.
		ImageLoader.imgs = new ImageLoader(null);
				
		// Build frame and then build creator panel.
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800,600);
		
		// They can pass id, password and game type as arguments.
		if(args.length == 3) {
			int playerID = Integer.parseInt(args[0]);
			String password = args[1];
			String gameTypeStr = args[2];
			build(new UserCredentials("",password,playerID), gameTypeStr, frame.getContentPane());
		} else if(args.length == 1) {
			String gameTypeStr = args[0];
			build(gameTypeStr, frame.getContentPane());
		} else {
			build(frame.getContentPane());
		}
		
		// Show.
		frame.setVisible(true);
	}

	/**
	 * Applet initialization.
	 */
	public void init() {
	
		// Init images.
		ImageLoader.imgs = new ImageLoader(this);
		
		// Get their id. Use 1 if none found.
		try {
			String playerStr = this.getParameter("playerID");
			String gameTypeStr = this.getParameter("gameType");

			if(gameTypeStr != null) {
				if(playerStr != null) {
					int playerID = Integer.parseInt(playerStr);
					String password = this.getParameter("password");
					build(new UserCredentials("",password,playerID), gameTypeStr, getContentPane());
				} else {
					build(gameTypeStr, getContentPane());
				}
			} else {
				build(getContentPane());
			}
		} catch(NumberFormatException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Build the internal frame.
	 * First gets the game type and user info.
	 * @param container
	 */
	public static void build(final Container container)  {
		final String gameType = requestGameType(container);
		PanelLogin login = new PanelLogin(new PanelLoginCallback() {
			public void setPlayerInfo(UserCredentials credentials) {
				container.removeAll();
				build(credentials,gameType,container);
			}			
		}, null);
		container.add(login);
	}
	
	/**
	 * Build the internal frame.
	 * Need the user info but have the game type.
	 * @param gameType
	 * @param container
	 */
	public static void build(final String gameType, final Container container)  {
		PanelLogin login = new PanelLogin(new PanelLoginCallback() {
			public void setPlayerInfo(UserCredentials credentials) {
				container.removeAll();
				build(credentials,gameType,container);
			}			
		}, null);
		container.add(login);
	}

	/**
	 * Build the internal frame with everything specified.
	 * @param playerID
	 * @param password
	 * @param gameType
	 * @param container
	 */
	public static void build(final UserCredentials credentials, String gameType, final Container container) {

		// Extract game data from game type.
		final String[] gameData = gameType.split(":");
		
		// Messges.
		final UserMessagePanel message = new UserMessagePanel();
		
		// IO.
		IOWeb web = new IOWeb(message);
		LoadModel loadModel = web;
		LoadWord loadWord = web;
		SaveGuess saveGuess = web;
		SaveModel saveModel = web;
		FlagModel flagModel = web;		
		final IOManager iomanager = new IOManager(loadModel, loadWord, saveGuess, saveModel, flagModel, message);
		
		// Different setup for each game type.
		if(gameData[0].equals(GAME_2D_NO_TREE)) {
			
			// 2D version without the tree base.
			
			if(gameData[1].equals("build")) {

				Model2DNoTree model2DNoTree = new Model2DNoTree(); 
				GeonModel model = model2DNoTree;
				WindowRender2DNoTree renderWindow = new WindowRender2DNoTree(model2DNoTree);
				WindowPrimitiveEdit2DNoTree primitiveEditor = new WindowPrimitiveEdit2DNoTree(model2DNoTree);
				WindowAddPrimitve2DNoTree addPrimitive = new WindowAddPrimitve2DNoTree(model2DNoTree);
				
				WindowOptions windowOptions = new WindowOptions(model);
				CreatorPanel cp = new CreatorPanel(credentials,gameData[0],renderWindow,windowOptions,primitiveEditor,addPrimitive,null,model,message,iomanager);
				cp.gameType = gameType;
				windowOptions.setCreatorPanel(cp);
				
				container.removeAll();
				container.add(cp);
				container.validate();
				
			} else if(gameData[1].equals("identify")) {
								
				Model2DNoTree model2DNoTree = new Model2DNoTree(); 
				GeonModel model = model2DNoTree;
				WindowRender2DNoTree renderWindow = new WindowRender2DNoTree(model2DNoTree);
				IdentifierPanel ip = new IdentifierPanel(credentials,container,renderWindow,model,message,iomanager,gameType, null);
				
				container.removeAll();
				container.add(ip);
				container.validate();
			}
						
			
		} else if(gameData[0].equals(GAME_2D_TREE)) {
			
			if(gameData[1].equals("build")) {

				Model2DTree model2DTree = new Model2DTree(); 
				GeonModel model = model2DTree;
				WindowRender2DTree renderWindow = new WindowRender2DTree(model2DTree);
				WindowPrimitiveEdit2DTree primitiveEditor = new WindowPrimitiveEdit2DTree(model2DTree);
				WindowAddPrimitive2DTree addPrimitive = new WindowAddPrimitive2DTree(model2DTree);
				WindowAttachment2DTree attachment = new WindowAttachment2DTree(model2DTree,true);
				
				WindowOptions windowOptions = new WindowOptions(model);
				CreatorPanel cp = new CreatorPanel(credentials,gameData[0],renderWindow,windowOptions,primitiveEditor,addPrimitive,attachment,model,message,iomanager);
				cp.gameType = gameType;
				windowOptions.setCreatorPanel(cp);
				
				container.removeAll();
				container.add(cp);
				container.validate();
			} else if(gameData[1].equals("identify")) {
				
				Model2DTree model2DTree = new Model2DTree(); 
				GeonModel model = model2DTree;
				WindowRender2DTree renderWindow = new WindowRender2DTree(model2DTree);

				IdentifierPanel ip = new IdentifierPanel(credentials,container,renderWindow,model,message,iomanager,gameType,GAME_MECHANICAL_TURK_1);
				
				container.removeAll();
				container.add(ip);
				container.validate();
				
			}
			
		} else if(gameData[0].equals(GAME_3D_TREE)) {
			
			if(gameData[1].equals("build")) {
				Model3DTree model3DTree = new Model3DTree(); 
				GeonModel model = model3DTree;
				WindowRender3DTree renderWindow = new WindowRender3DTree(model3DTree);
				WindowPrimitiveEdit3DTree primitiveEditor = new WindowPrimitiveEdit3DTree(model3DTree);
				WindowAddPrimitive3DTree addPrimitive = new WindowAddPrimitive3DTree(model3DTree);
				WindowAttachment3DTree attachment = new WindowAttachment3DTree(model3DTree,(WindowRender3DTree)renderWindow);
	
				WindowOptions windowOptions = new WindowOptions(model);
				CreatorPanel cp = new CreatorPanel(credentials,gameData[0],renderWindow,windowOptions,primitiveEditor,addPrimitive,attachment,model,message,iomanager);
				cp.gameType = gameType;
				windowOptions.setCreatorPanel(cp);
				
				container.removeAll();
				container.add(cp);
				container.validate();
			} else if(gameData[1].equals("identify")) {
				
				Model3DTree model3DTree = new Model3DTree(); 
				GeonModel model = model3DTree;
				WindowRender3DTree renderWindow = new WindowRender3DTree(model3DTree);
				
				IdentifierPanel ip = new IdentifierPanel(credentials,container,renderWindow,model,message,iomanager,gameType,null);
				
				container.removeAll();
				container.add(ip);
				container.validate();
			}
			
		} else if(gameData[0].equals(GAME_SUBJECT_POOL_1)) {
						
			if(gameData[1].equals("build")) {
				IOWeb limited = new IOWeb(message) {
					public WordPair loadWord(int playerID, String gameType) {
						WordPair pair = super.loadWord(playerID, gameType);
						int count = getModelCount(playerID, gameType);
											
						pair.description += " (" + (count+1)  + " out of 32)";
						if(count > 32) {
							messager.showMessage("You have completed all 32 objects.", UserMessage.INFORM);
						}
						return pair;
					}
				};
				iomanager.setLoadWord(limited);
				
				Model2DTree model2DTree = new Model2DTree(); 
				GeonModel model = model2DTree;
				WindowRender2DTree renderWindow = new WindowRender2DTree(model2DTree);
				WindowPrimitiveEdit2DTree primitiveEditor = new WindowPrimitiveEdit2DTree(model2DTree);
				WindowAddPrimitive2DTree addPrimitive = new WindowAddPrimitive2DTree(model2DTree);
				WindowAttachment2DTree attachment = new WindowAttachment2DTree(model2DTree,true);
				
				WindowOptions windowOptions = new WindowOptions(model);
				CreatorPanel cp = new CreatorPanel(credentials,gameData[0],renderWindow,windowOptions,primitiveEditor,addPrimitive,attachment,model,message,iomanager);
				cp.gameType = gameType;
				windowOptions.setCreatorPanel(cp);
				
				container.removeAll();
				container.add(cp);
				container.validate();
			}
		} else if(gameData[0].equals(GAME_FEATURE)) {
			try {
				FeatureGameGUI featureGame = new FeatureGameGUI(credentials);				
				featureGame.setGameState(FeatureGameGUI.State.StartScreen);
				
				container.removeAll();
				container.add(featureGame);
				container.validate();
				
			} catch(IOException e) {
				e.printStackTrace();
			}
		} else if(gameData[0].equals(GAME_FEATURE_SIMPLE)) {
			

			if(gameData[1].equals("describe")) {
				FeatureGameSimpleDescribeGUI featureGame = new FeatureGameSimpleDescribeGUI(credentials);			
				container.removeAll();
				container.add(featureGame);
				container.validate();
			} else if(gameData[1].equals("guess")) {
				FeatureGameSimpleGuessGUI featureGame = new FeatureGameSimpleGuessGUI(credentials);			
				container.removeAll();
				container.add(featureGame);
				container.validate();
			}
		} else if(gameData[0].equals(GAME_MECHANICAL_TURK_1)) {
			
			IOWeb limited = new IOWeb(message) {
				private int wordCount = 32;
				public WordPair loadWord(int playerID, String gameType) {
					WordPair pair = super.loadWord(playerID, gameType);
					int count = getModelCount(playerID, gameType);
										
					if(count >= wordCount) {
						pair.description += " You have completed all " + wordCount + " objects. Please press the completion button on the MTurk page.";
					} else {
						pair.description += " (" + (count+1)  + " out of " + wordCount + ")";
					}
					
					return pair;
				}
			};
			iomanager.setLoadWord(limited);
			
			Model2DTree model2DTree = new Model2DTree(); 
			GeonModel model = model2DTree;
			WindowRender2DTree renderWindow = new WindowRender2DTree(model2DTree);
			WindowPrimitiveEdit2DTree primitiveEditor = new WindowPrimitiveEdit2DTree(model2DTree);
			WindowAddPrimitive2DTree addPrimitive = new WindowAddPrimitive2DTree(model2DTree);
			WindowAttachment2DTree attachment = new WindowAttachment2DTree(model2DTree,true);
			
			WindowOptions windowOptions = new WindowOptions(model);
			CreatorPanel cp = new CreatorPanel(credentials,gameData[0],renderWindow,windowOptions,primitiveEditor,addPrimitive,attachment,model,message,iomanager);
			cp.gameType = gameType;
			windowOptions.setCreatorPanel(cp);
			
			container.removeAll();
			container.add(cp);
			container.validate();
			
		} else if(gameData[0].equals(GAME_MECHANICAL_TURK_2)) {

			final int labelCount = 20;
			final int wordCount = 20;

			Model2DTree model2DTree = new Model2DTree(); 
			GeonModel model = model2DTree;
			WindowRender2DTree renderWindow = new WindowRender2DTree(model2DTree);

			IdentifierPanel ip = new IdentifierPanel(credentials,container,renderWindow,model,message,iomanager,gameType,GAME_MECHANICAL_TURK_1);
			final JLabel label = new JLabel("label");
			label.setForeground(Color.WHITE);
			
			ip.guessPanel.add(label);
			
			container.removeAll();
			container.add(ip);
			container.validate();
			
			IOWeb limited = new IOWeb(message) {
				public int loadModel(GeonModel modelIn, int playerID, String gameType) {
					int result = super.loadModel(modelIn, playerID, gameType);					
					int count = getLabelCount(playerID, GAME_MECHANICAL_TURK_2);
					
					if(count >= labelCount) {
						Model2DTree model2DTree = new Model2DTree(); 
						GeonModel model = model2DTree;
						WindowRender2DTree renderWindow = new WindowRender2DTree(model2DTree);
						WindowPrimitiveEdit2DTree primitiveEditor = new WindowPrimitiveEdit2DTree(model2DTree);
						WindowAddPrimitive2DTree addPrimitive = new WindowAddPrimitive2DTree(model2DTree);
						WindowAttachment2DTree attachment = new WindowAttachment2DTree(model2DTree,true);
						
						WindowOptions windowOptions = new WindowOptions(model);
						CreatorPanel cp = new CreatorPanel(credentials,gameData[0],renderWindow,windowOptions,primitiveEditor,addPrimitive,attachment,model,message,iomanager);
						cp.gameType = gameType;
						windowOptions.setCreatorPanel(cp);
						
						container.removeAll();
						container.add(cp);
						container.validate();
						
					} else {
						label.setText((count + 1) + "/" + labelCount);						
					}
					return result;
				}
			};
			iomanager.setLoadModel(limited);			
			
			IOWeb limited2 = new IOWeb(message) {
				public WordPair loadWord(int playerID, String gameType) {
					WordPair pair = super.loadWord(playerID, gameType);
					int count = getModelCount(playerID, gameType);
										
					if(count >= wordCount) {
						
						container.removeAll();
						container.add(new JTextArea("You have completed all " + wordCount + " objects. Please press the completion button on the MTurk page."));
						container.validate();
						
					} else {
						pair.description += " (" + (count+1)  + " out of " + wordCount + ")";
					}
					
					return pair;
				}
			};
			iomanager.setLoadWord(limited2);

			int count = limited.getLabelCount(credentials.userID, GAME_MECHANICAL_TURK_2);
			label.setText((count + 1) + "/" + labelCount);		
			
			if(count >= labelCount) {
				
				System.out.println(limited.getLabelCount(credentials.userID, GAME_MECHANICAL_TURK_2) + ">" + labelCount);
				
				model2DTree = new Model2DTree(); 
				model = model2DTree;
				renderWindow = new WindowRender2DTree(model2DTree);
				WindowPrimitiveEdit2DTree primitiveEditor = new WindowPrimitiveEdit2DTree(model2DTree);
				WindowAddPrimitive2DTree addPrimitive = new WindowAddPrimitive2DTree(model2DTree);
				WindowAttachment2DTree attachment = new WindowAttachment2DTree(model2DTree,true);
				
				WindowOptions windowOptions = new WindowOptions(model);
				CreatorPanel cp = new CreatorPanel(credentials,gameData[0],renderWindow,windowOptions,primitiveEditor,addPrimitive,attachment,model,message,iomanager);
				cp.gameType = gameType;
				windowOptions.setCreatorPanel(cp);
				
				container.removeAll();
				container.add(cp);
				container.validate();
				
				if(limited2.getModelCount(credentials.userID, gameType) >= wordCount) {
					container.removeAll();
					container.add(new JTextArea("You have completed all " + wordCount + " objects. Please press the completion button on the MTurk page."));
					container.validate();
				}
			}
			
		} else {
			container.removeAll();
			container.add(new JLabel("Game type [" + gameType + "] not recognized."));
			container.validate();
		}
	}
	
	/**
	 * Create a popup that asks for their desired game type.
	 * @param parentComponent
	 * @return
	 */
	public static String requestGameType(Component parentComponent) {
		return (String)JOptionPane.showInputDialog(
			parentComponent,
            "Select game type,",
            "Game Type",
            JOptionPane.PLAIN_MESSAGE,
            null,
            GAME_TYPES,
            GAME_TYPES[0]);		
	}
}