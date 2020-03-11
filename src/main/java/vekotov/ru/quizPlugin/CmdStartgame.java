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
import java.util.Iterator;

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


        int max_id = p.quests.size(); //maximal = number of quest
        int generated_id = (int) (Math.random() * max_id); //getting random id

        p.playerquests.put(name, generated_id); //putting player in list players with quests
        Quest quest = p.quests.get(generated_id); //creating quest for him

        player.sendMessage(p.messages.get("QUESTION").replace("%question%", quest.description)); //sending him a question


        player.sendMessage(p.messages.get("ANSWERS")); //sending him a "answers:" line
        ArrayList<String> answers = quest.answers; //list of answers

        Iterator<String> iterator = answers.iterator();


        int style;
        if(quest.answer_style.equals("4-line"))style = 1;
        else if(quest.answer_style.equals("2-line"))style = 2;
        else if(quest.answer_style.equals("1-line"))style = 4;
        else return true;

        TextComponent msg = new TextComponent();

        int n = 1;
        while(iterator.hasNext()){ //TODO: Add shuffling answers before printing to (against remembering by location)
            String answer_text = iterator.next();

            msg.addExtra(p.messages.get("ANSWERS_SEPARATOR"));

            TextComponent answerButton = new TextComponent();
            String answer = p.messages.get("ANSWER_STYLE");
            answer = answer.replace("%letter%", getLetter(n));  //%letter%. %answer%
            answer = answer.replace("%answer%", answer_text);
            answerButton.setText(answer);
            answerButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(p.messages.get("HOVERTEXT_HINT_ANSWER")).create()));
            answerButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/answer " + getLetter(n)));
            msg.addExtra(answerButton);
            p.getLogger().info("STRING = " + msg.getText() + "   /// n = " + n + " /// style = " + style);
            if(n % style == 0){
                player.spigot().sendMessage(msg);
                msg = new TextComponent();
            }
            n++;
        }

        p.getLogger().info("RESULT n = " + n);
        if((n-1) % style != 0){
            p.getLogger().info("SENT VIA DUBL");
            player.spigot().sendMessage(msg);
            player.spigot().sendMessage(new TextComponent(" "));
        }else{
            p.getLogger().info("SENT VIA ONE");
            player.spigot().sendMessage(msg);
        }

        return true;
    }


    String getLetter(int number){
        switch (number){
            case 1: return "A";
            case 2: return "B";
            case 3: return "C";
            case 4: return "D";
        }
        return "";
    }
}
