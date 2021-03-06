package vekotov.ru.quizPlugin;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdAnswer implements CommandExecutor {
    private quizPlugin p;

    public CmdAnswer(quizPlugin p){
        this.p = p;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (!(sender instanceof Player)) { //If it is console
            sender.sendMessage(ChatColor.RED + p.messages.get("ERROR_PLAYER_ONLY"));
            return true;
        }

        if (!p.IsWorking) { //If configs not loaded
            sender.sendMessage(p.messages.get("ERROR_PLUGIN_DISABLED"));
            return true;
        }

        Player player = (Player) sender;
        String name = player.getName();
        if (args.length != 1) return false;

        if (!p.playerquests.containsKey(name)) { //checking player having question
            player.sendMessage(p.messages.get("NOT_GOT_QUESTION_YET"));
            return true;
        }
        String answer = args[0];
        Quest quest = p.quests.get(p.playerquests.get(name));
        TextComponent msg = new TextComponent();
        msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(p.messages.get("HOVERTEXT_HINT_RESTART")).create()));
        msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/startgame"));
        if (quest.right_answer.equals(answer)) {
            msg.setText(p.messages.get("RIGHT_ANSWER"));
            String command = quest.reward.replace("%player%", name);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }else{
            msg.setText(p.messages.get("WRONG_ANSWER"));
        }
        player.spigot().sendMessage(msg);
        p.playerquests.remove(name);
        return true;
    }
}
