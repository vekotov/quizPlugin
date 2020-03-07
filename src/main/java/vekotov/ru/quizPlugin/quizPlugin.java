package vekotov.ru.quizPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;


public class quizPlugin extends JavaPlugin {
    HashMap<String, Integer> playerquests = new HashMap<String, Integer>();
    HashMap<Integer, Quest> quests = new HashMap<Integer, Quest>();
    HashMap<Messages, String> messages = new HashMap<Messages, String>();

    enum Messages{
        ERROR_PLAYER_ONLY, ERROR_PLUGIN_DISABLED, NOT_GOT_QUESTION_YET, RIGHT_ANSWER, WRONG_ANSWER, QUESTION, HOVERTEXT_HINT_ANSWER, HOVERTEXT_HINT_RESTART, ANSWERS
    }

    private File quests_file = new File(getDataFolder(), "quests.yml");
    private File messages_file = new File(getDataFolder(), "messages.yml");
    public FileConfiguration quests_config = YamlConfiguration.loadConfiguration(quests_file);
    public FileConfiguration messages_config = YamlConfiguration.loadConfiguration(messages_file);
    public boolean IsWorking = true;

    public Quest.ANSWER string_to_enum(String answer) {
        if (answer.equals("A")) return Quest.ANSWER.A;
        if (answer.equals("B")) return Quest.ANSWER.B;
        if (answer.equals("C")) return Quest.ANSWER.C;
        if (answer.equals("D")) return Quest.ANSWER.D;
        return Quest.ANSWER.A;
    }

    /*
 -----------------------------------------------------------------------
 CONFIGURATION SECTION BELOW
     */
//TODO: Move it to configLoading.java and add exception working
    public void loadConfigs() {
        playerquests.clear();
        quests.clear();
        messages.clear();

        if (!quests_file.exists()) {
            saveResource("quests.yml", false);
            quests_file = new File(getDataFolder(), "quests.yml");
            quests_config = YamlConfiguration.loadConfiguration(quests_file);
        }
        if (!messages_file.exists()){
            saveResource("messages.yml", false);
            messages_file = new File(getDataFolder(), "messages.yml");
            messages_config = YamlConfiguration.loadConfiguration(messages_file);
        }

        try {
            for (String key : quests_config.getConfigurationSection("Quests").getKeys(false)) {
                String desc = quests_config.getString("Quests." + key + ".description");

                String right_answer = quests_config.getString("Quests." + key + ".right_answer");
                ArrayList<String> answers = new ArrayList<String>();
                for (String answer_key : quests_config.getConfigurationSection("Quests." + key + ".answers").getKeys(false)) {
                    answers.add(quests_config.getString("Quests." + key + ".answers." + answer_key));
                }
                String reward = quests_config.getString("Quests." + key + ".reward");
                Quest quest = new Quest(desc, reward,quests.size() + 1, string_to_enum(right_answer), answers);
                quests.put(quest.id, quest);
            }

            messages.put(Messages.ERROR_PLAYER_ONLY, messages_config.getString("ERROR_PLAYER_ONLY"));
            messages.put(Messages.ERROR_PLUGIN_DISABLED, messages_config.getString("ERROR_PLUGIN_DISABLED"));
            messages.put(Messages.NOT_GOT_QUESTION_YET, messages_config.getString("NOT_GOT_QUESTION_YET"));
            messages.put(Messages.RIGHT_ANSWER, messages_config.getString("RIGHT_ANSWER"));
            messages.put(Messages.QUESTION, messages_config.getString("QUESTION"));
            messages.put(Messages.HOVERTEXT_HINT_ANSWER, messages_config.getString("HOVERTEXT_HINT_ANSWER"));
            messages.put(Messages.ANSWERS, messages_config.getString("ANSWERS"));
            messages.put(Messages.HOVERTEXT_HINT_RESTART, messages_config.getString("HOVERTEXT_HINT_RESTART"));
        } catch (Exception var9) {
            getLogger().warning("При загрузке плагина произошла ошибка: " + var9.getMessage());
            IsWorking = false;
        }

        getLogger().info("Загружено " + quests.size() + " вопросов.");

        IsWorking = quests.size() != 0;
    }

    /*
    ЕND OF CONFIGURATION SECTION
    ----------------------------------
    */

    public void onEnable() {
        loadConfigs();
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) { //If it is console
            sender.sendMessage(ChatColor.RED + messages.get(Messages.ERROR_PLAYER_ONLY));
            return true;
        }
        if (!IsWorking) { //If configs not loaded
            sender.sendMessage(messages.get(Messages.ERROR_PLUGIN_DISABLED));
            return true;
        } else { //if all ok
            Player player = (Player) sender;
            String name = player.getName();
            if (cmd.getName().equalsIgnoreCase("answer")) { //on /answer [letter] command
                if (args.length != 1) return false;
                if (!playerquests.containsKey(name)) { //checking player having question
                    player.sendMessage(messages.get(Messages.NOT_GOT_QUESTION_YET));
                    return true;
                }
                String answer_text = args[0];
                Quest.ANSWER answer = string_to_enum(answer_text);
                Quest quest = quests.get(playerquests.get(name));
                if (quest.right_answer == answer) {
                    TextComponent msg = new TextComponent(messages.get(Messages.RIGHT_ANSWER));
                    msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(messages.get(Messages.HOVERTEXT_HINT_RESTART)).create()));
                    msg.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/startgame"));
                    player.sendMessage(messages.get(Messages.RIGHT_ANSWER));
                    String command = quest.reward.replace("%player%", name);
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                }else{
                    player.sendMessage(messages.get(Messages.WRONG_ANSWER));
                }
                playerquests.remove(name);
                return true;
            } else if (cmd.getName().equalsIgnoreCase("startgame")) {
                if (args.length != 0) return false;

                int min_id = 1;
                int max_id = playerquests.size();
                int generated_id = min_id + (int) (Math.random() * (max_id + 1));
                playerquests.put(name, generated_id);
                Quest quest = quests.get(generated_id);
                player.sendMessage(messages.get(Messages.QUESTION).replace("%question%", quest.description));
                player.sendMessage(messages.get(Messages.ANSWERS));
                ArrayList<String> answers = quest.answers;

                for (int t = 0; t < answers.size(); t++) {
                    String s = answers.get(t);
                    TextComponent msg = new TextComponent("");
                    String letter = "";
                    switch (t) {
                        case 0:
                            letter = "A";
                            msg.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/answer A"));
                            break;
                        case 1:
                            letter = "B";
                            msg.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/answer B"));
                            break;
                        case 2:
                            letter = "C";
                            msg.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/answer C"));
                            break;
                        case 3:
                            letter = "D";
                            msg.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/answer D"));
                    }
                    msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(messages.get(Messages.HOVERTEXT_HINT_ANSWER)).create()));
                    msg.setText(letter + ". " + s);
                    player.spigot().sendMessage(msg);
                }
                return true;
            }
        }
        return false;
    }
}
