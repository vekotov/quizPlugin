package vekotov.ru.quizPlugin;

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
import org.bukkit.entity.Player;
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
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) { //TODO: MOVE IT TO ANOTHER FILE
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
                TextComponent msg = new TextComponent();
                msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(messages.get(Messages.HOVERTEXT_HINT_RESTART)).create()));
                msg.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/startgame"));
                if (quest.right_answer == answer) {
                    msg.setText(messages.get(Messages.RIGHT_ANSWER));
                    String command = quest.reward.replace("%player%", name);
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                }else{
                    msg.setText(messages.get(Messages.WRONG_ANSWER));
                }
                player.spigot().sendMessage(msg);
                playerquests.remove(name);
                return true;
            } else return cmd.getName().equalsIgnoreCase("startgame");
        }
    }
}
