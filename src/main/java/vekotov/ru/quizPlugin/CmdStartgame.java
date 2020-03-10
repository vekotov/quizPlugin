package vekotov.ru.quizPlugin;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class CmdStartgame implements CommandExecutor {

    private quizPlugin p;

    public CmdStartgame(quizPlugin plugin) {
        this.p = plugin; // Store the plugin in situations where you need it.
    }

    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) { //If it is console
            sender.sendMessage(ChatColor.RED + p.messages.get("ERROR_PLAYER_ONLY"));
            return true;
        }

        if (!p.IsWorking) { //If configs not loaded
            sender.sendMessage(p.messages.get("ERROR_PLUGIN_DISABLED"));
            return true;
        }

        Player player = (Player) sender;
        String name = player.getName(); //getting name of player


        int min_id = 1; //minimal = 1 quest
        int max_id = p.playerquests.size(); //maximal = number of quest
        int generated_id = min_id + (int) (Math.random() * (max_id + 1)); //getting random id //TODO: Check it, looks like here are error in generating random
        p.playerquests.put(name, generated_id); //putting player in list players with quests
        Quest quest = p.quests.get(generated_id); //creating quest for him
        player.sendMessage(p.messages.get("QUESTION").replace("%question%", quest.description)); //sending him a question
        player.sendMessage(p.messages.get("ANSWERS")); //sending him a "answers:" line
        ArrayList<String> answers = quest.answers; //list of answers //TODO: rework it to different number of lines

        for (int t = 0; t < answers.size(); t++) {
            //TODO: Add shuffling answers before printing to (against remembering by location)
            String text = answers.get(t);
            TextComponent msg = new TextComponent("");
            String letter = "";
            switch (t) {
                case 0:
                    letter = "A";
                    msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/answer A"));
                    break;
                case 1:
                    letter = "B";
                    msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/answer B"));
                    break;
                case 2:
                    letter = "C";
                    msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/answer C"));
                    break;
                case 3:
                    letter = "D";
                    msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/answer D"));
            }
            msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(p.messages.get("HOVERTEXT_HINT_ANSWER")).create()));
            msg.setText(letter + ". " + text);
            player.spigot().sendMessage(msg);
        }
        return true;
    }
}
