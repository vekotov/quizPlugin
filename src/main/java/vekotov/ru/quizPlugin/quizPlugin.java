package vekotov.ru.quizPlugin;

import java.util.HashMap;
import org.bukkit.plugin.java.JavaPlugin;


public class quizPlugin extends JavaPlugin {
    HashMap<String, Integer> playerquests = new HashMap<String, Integer>();
    HashMap<Integer, Quest> quests = new HashMap<Integer, Quest>();
    HashMap<Messages, String> messages = new HashMap<Messages, String>();

    enum Messages{
        ERROR_PLAYER_ONLY, ERROR_PLUGIN_DISABLED, NOT_GOT_QUESTION_YET, RIGHT_ANSWER, WRONG_ANSWER, QUESTION, HOVERTEXT_HINT_ANSWER, HOVERTEXT_HINT_RESTART, ANSWERS
    }

    public boolean IsWorking = true;

    public Quest.ANSWER string_to_enum(String answer) {
        if (answer.equals("A")) return Quest.ANSWER.A;
        if (answer.equals("B")) return Quest.ANSWER.B;
        if (answer.equals("C")) return Quest.ANSWER.C;
        if (answer.equals("D")) return Quest.ANSWER.D;
        return Quest.ANSWER.A;
    }

    public void onEnable() {
        configLoading.loadConfigs(this);
        this.getCommand("startgame").setExecutor(new startgame_cmd(this));
        this.getCommand("answer").setExecutor(new answer_cmd(this));
    }
}
