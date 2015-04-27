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
	
	public static ArrayList<String> user1games;

	private static JFrame resultFrame;
	private static JTextArea resultText;
	private static String resultString;
	private static File resultFile;
	private static JFileChooser fc;

	public static void main(String[] args) {
		resultWindow();

		user1games = new ArrayList<String>();

		String userName1 = null;
		
		System.out.println();
		System.out.println();

		userName1 = (String) JOptionPane.showInputDialog(null, "Enter a public Steam username:\n\nPublic id is at the end of your profile, "
				+ "\neg: http://steamcommunity.com/id/pewdie = pewdie\nIf you haven't setup a custom ID, your ID is just an integer.",
				"Enter username 1", JOptionPane.PLAIN_MESSAGE, null, null, null);

		if (userName1 == null || userName1.equals("")) {
			System.exit(0);
		}


		// System.out.println(userName1 + " " + userName2);
		String comURL = "http://steamcommunity.com/id/";
		String endURL = "/games/?tab=all&sort=name";

		String user1URL = comURL + userName1 + endURL;

		user1games = parseHTML(user1URL);


		StringBuilder sb = new StringBuilder();

		resultString = sb.toString();
		resultText.setText(resultString);
		resultFrame.setVisible(true);


		System.out.println();

	}

	public static ArrayList<String> parseHTML(String input) {
		ArrayList<String> games = new ArrayList<String>();
		String url = input;

		while (url != null) {
			String nextURL = null;
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
					url = (String) JOptionPane.showInputDialog(null,
							"Malformed URL format. Are you sure you copied the entire URL?\n" + "Try again:", "Provide URL",
							JOptionPane.PLAIN_MESSAGE, null, null, null);
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
					String[] arr;
					if (inputLine.contains("rgGames")) {
						arr = inputLine.split("name");
						for (String line : arr) {
							if (!line.contains("var rgGames") && line.contains("logo")) {
								String game = line.substring(3);
								game = game.substring(0, game.indexOf("\",\"logo\":"));
								game = game.replace("\\u00ae", "");
								game = game.replace("\\u221e", "");
								game = game.replace("\\u2122", "");

								games.add(game);

								// System.out.println(game);
							}
						}
						// System.out.println();
					}
					inputLine = in.readLine();
				}

				in.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
			url = nextURL;
		}

		return games;
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
