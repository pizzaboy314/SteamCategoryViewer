import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;


public class Worker {
	
	public static ArrayList<SteamGame> usergames;
	public static ArrayList<Tag> usertags;

	private static JFrame resultFrame;
	private static JTextArea resultText;
	private static String resultString;
	private static File resultFile;
	private static JFileChooser fc;

	public static void main(String[] args) {
		resultWindow();

		usergames = new ArrayList<SteamGame>();

		String userName1 = null;
		
		System.out.println();
		System.out.println();

		userName1 = (String) JOptionPane.showInputDialog(null, "Enter a public Steam username:\n\nPublic id is at the end of your profile, "
				+ "\neg: http://steamcommunity.com/id/pewdie = pewdie\nIf you haven't setup a custom ID, your ID is just an integer.",
				"Enter username 1", JOptionPane.PLAIN_MESSAGE, null, null, null);

		if (userName1 == null || userName1.equals("")) {
			System.exit(0);
		}


		String comURL = "http://steamcommunity.com/id/";
		String endURL = "/games/?tab=all&sort=name";

		String user1URL = comURL + userName1 + endURL;

		parseHTML(user1URL);
		grabTagsAndAddGames();

		StringBuilder sb = new StringBuilder();
		for (Tag t : usertags) {
			sb.append(t.getName() + ":\n");
			ArrayList<SteamGame> list = t.getGames();
			for(SteamGame sg : list){
				sb.append(sg.getName() + "\n");
			}
			sb.append("\n");
		}

		resultString = sb.toString();
		resultText.setText(resultString);
		resultFrame.setVisible(true);


		System.out.println();

	}

	@SuppressWarnings("unchecked")
	public static void grabTagsAndAddGames() {
		usertags = new ArrayList<Tag>();
		for (SteamGame game : usergames) {
			ArrayList<String> tags = game.getTags();
			for (String s : tags) {
				Tag t = new Tag(s);

				if (usertags.contains(t)) {
					Tag tmp = usertags.get(usertags.indexOf(t));
					tmp.addGame(game);
					// System.out.println(tmp.getName());
				} else {
					t.addGame(game);
					usertags.add(t);
				}
			}
		}
		Collections.sort(usertags);
	}

	public static void parseHTML(String input) {
		usergames = new ArrayList<SteamGame>();
		String url = input;

		try {
			URL source = null;
			boolean valid = true;
			try {
				source = new URL(url);
			} catch (MalformedURLException e) {
				valid = false;
			}
			while (valid == false) {
				valid = true;
				url = (String) JOptionPane.showInputDialog(null, "Malformed URL format. Are you sure you copied the entire URL?\n" + "Try again:",
						"Provide URL", JOptionPane.PLAIN_MESSAGE, null, null, null);
				try {
					source = new URL(url);
				} catch (MalformedURLException e) {
					valid = false;
				}
			}

			BufferedReader in = new BufferedReader(new InputStreamReader(source.openStream()));

			String inputLine = in.readLine();
			while (inputLine != null) {
				// System.out.println(inputLine);
				if (inputLine.contains("rgGames")) {
					String s = inputLine.replace("var rgGames = [{", "").replace("}];", "").trim();
					String[] apps = s.split("\\},\\{");
					for (String app : apps) {
						SteamGame game;
						String gameTitle = null;
						Integer gameID = null;

						String[] params = app.split(",");
						for (String param : params) {

							String[] vals = param.split("\":");
							if (vals[0].contains("name") && !vals[0].contains("friendly")) {
								gameTitle = vals[1].replace("\"", "").replace("\\u00ae", "").replace("\\u221e", "").replace("\\u2122", "");
							}
							if (vals[0].contains("appid")) {
								gameID = Integer.parseInt(vals[1].replace("\"", ""));
							}
							if (gameTitle != null && gameID != null) {
								game = new SteamGame(gameTitle, gameID);
								if (!game.isDlc()) {
									usergames.add(game);
								}
								break;
							}

							System.out.println(param);
						}
					}
				}
				inputLine = in.readLine();
			}

			in.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized static void resultWindow() {
		resultFrame = new JFrame("Results");
		File resultPath = new File(System.getProperty("user.dir"));
		fc = new JFileChooser(resultPath);

		FileFilter filter = new FileNameExtensionFilter("Text file (*.txt)", "txt");
		fc.addChoosableFileFilter(filter);
		fc.setFileFilter(filter);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = new Dimension((int) (screenSize.width / 2), (int) (screenSize.height / 2));
		int x = (int) (frameSize.width / 2);
		int y = (int) (frameSize.height / 2);
		resultFrame.setBounds(x, y, 400, frameSize.height);

		resultText = new JTextArea();
		resultText.setText("");
		resultText.setEditable(false);

		JButton saveFile = new JButton("Save Results");
		saveFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int returnVal = fc.showSaveDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File f = fc.getSelectedFile();
					String filepath = f.getAbsolutePath();
					String filename = f.getName();

					if (!filename.contains(".txt")) {
						resultFile = new File(filepath + ".txt");
					} else {
						resultFile = f;
					}

					try {
						Files.write(Paths.get(resultFile.getAbsolutePath()), resultString.getBytes());
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		JPanel controls = new JPanel();
		controls.setLayout(new FlowLayout());
		controls.add(saveFile);

		resultFrame.getContentPane().add(new JScrollPane(resultText), BorderLayout.CENTER);
		resultFrame.getContentPane().add(controls, BorderLayout.SOUTH);
	}

}
