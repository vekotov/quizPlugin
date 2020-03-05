package vekotov.ru.TestingPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import net.md_5.bungee.api.chat.ClickEvent;
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

public class TestingPlugin extends JavaPlugin {
    HashMap<String, Integer> playerquests = new HashMap();
    HashMap<Integer, Quest> quests = new HashMap();

    private File quests_file = new File(getDataFolder(), "quests.yml");
    public FileConfiguration quests_config = YamlConfiguration.loadConfiguration(quests_file);
    public boolean IsWorking = true;

    public Quest.ANSWER string_to_enum(String answer) {
        if (answer.equals("A")) return Quest.ANSWER.A;
        if (answer.equals("B")) return Quest.ANSWER.B;
        if (answer.equals("C")) return Quest.ANSWER.C;
        if (answer.equals("D")) return Quest.ANSWER.D;
        return Quest.ANSWER.A;
    }

    public void loadConfigs() {
        playerquests.clear();
        quests.clear();

        if (!quests_file.exists()) {
            saveResource("quests.yml", false);
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
        } catch (Exception var9) {
            getLogger().info("Vekotov, ты обосрался, еррорка: " + var9.getMessage());
            IsWorking = false;
        }

        if (quests.size() == 0) {
            getLogger().info("Vekotov, ты херню сотворил с конфигом, у тебя 0 квестов.");
            IsWorking = false;
        }
    }

    public void onEnable() {
        loadConfigs();
    }

    public void onDisable() {
        getLogger().info("onDisable is called!");
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Сцуко команды для игроков только");
            return true;
        }
        if (!IsWorking) {
            sender.sendMessage("Плагин здох");
            return true;
        } else {
            Player player = (Player) sender;
            String name = player.getName();
            if (cmd.getName().equalsIgnoreCase("answer")) {
                if (args.length != 1) return false;
                if (!playerquests.containsKey(name)) {
                    player.sendMessage("Нуб введи /startgame для начала, ты же еще даже вопросы не получил.");
                    return true;
                }
                String answer_text = args[0];
                Quest.ANSWER answer = string_to_enum(answer_text);
                Quest quest = quests.get(playerquests.get(name));
                if (quest.right_answer == answer) {
                    player.sendMessage("ПОЗДРАВЛЯЕМ ТЕБЯ!!!!!!!!");
                    String command = quest.reward.replace("%player%", name);
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                }else{
                    player.sendMessage("Короче ты ответил неверно.");
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
                player.sendMessage("Вопрос: " + quest.description + " (вы можете кликнуть на верный ответ в чате).");
                player.sendMessage("Ответы:");
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

                    msg.setText(letter + ". " + s);
                    player.spigot().sendMessage(msg);
                }
                return true;
            }
        }
        return false;
    }
}
