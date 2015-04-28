import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.JOptionPane;


public class SteamGame {

	private String name;
	private Integer appid;
	private boolean dlc;
	private ArrayList<String> tags;

	public SteamGame(String name, Integer appid) {
		this.name = name;
		this.appid = appid;
		this.dlc = false;
		parseCategories();
	}

	public void parseCategories() {
		tags = new ArrayList<String>();
		String url = "http://store.steampowered.com/app/" + appid;

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
				url = (String) JOptionPane
						.showInputDialog(null, "Malformed URL format. ", "Provide URL", JOptionPane.PLAIN_MESSAGE, null, null, null);
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
				if (inputLine.contains("class=\"app_tag\" style=\"display: none;\">") && inputLine.contains("</a>")) {
					if (tags.size() < 5) {
						arr = inputLine.split("</a>");
						String tag = arr[0].trim();
						tags.add(tag);
					}
				}
				if (inputLine.contains("This content requires the base game")) {
					dlc = true;
				}
				inputLine = in.readLine();
			}

			in.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isDlc() {
		return dlc;
	}

	public void setDlc(boolean dlc) {
		this.dlc = dlc;
	}

	public ArrayList<String> getTags() {
		return tags;
	}

	public Integer getAppid() {
		return appid;
	}

	public void setAppid(Integer appid) {
		this.appid = appid;
	}
}
