package vekotov.ru.quizPlugin;

import java.util.HashMap;
import org.bukkit.plugin.java.JavaPlugin;


public class quizPlugin extends JavaPlugin {
    HashMap<String, Integer> playerquests = new HashMap<String, Integer>();
    HashMap<Integer, Quest> quests = new HashMap<Integer, Quest>();
    HashMap<String, String> messages = new HashMap<String, String>(); //first String is key like ERROR_PLAYER_ONLY, second - text

    public boolean IsWorking = true;

    public void onEnable() {
        ConfigLoading cf = new ConfigLoading(this);
        this.getCommand("startgame").setExecutor(new CmdStartgame(this));
        this.getCommand("answer").setExecutor(new CmdAnswer(this));
    }
}
