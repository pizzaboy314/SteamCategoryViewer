import java.util.ArrayList;


public class Tag implements Comparable {

	private String name;
	private ArrayList<SteamGame> games;

	public Tag(String name) {
		games = new ArrayList<SteamGame>();
		this.name = name;
	}

	@Override
	public boolean equals(Object t) {
		return ((Tag) t).getName().equalsIgnoreCase(this.name);
	}

	public void addGame(SteamGame game) {
		games.add(game);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<SteamGame> getGames() {
		return games;
	}

	@Override
	public int compareTo(Object o) {
		return (this.name).compareTo(((Tag) o).getName());
	}

}
