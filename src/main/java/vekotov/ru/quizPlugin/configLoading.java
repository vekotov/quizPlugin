package vekotov.ru.quizPlugin;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;

public class configLoading {
    static public void loadConfigs(quizPlugin p){
        p.playerquests.clear();
        p.quests.clear();
        p.messages.clear(); //firstly clearing old configs

        if (!p.quests_file.exists()) { //create and load new quests file from resources if file not exist
            p.saveResource("quests.yml", false);
            p.quests_file = new File(p.getDataFolder(), "quests.yml");
            p.quests_config = YamlConfiguration.loadConfiguration(p.quests_file);
        }
        if (!p.messages_file.exists()){ //create and load new messages file from resources if file not exist
            p.saveResource("messages.yml", false);
            p.messages_file = new File(p.getDataFolder(), "messages.yml");
            p.messages_config = YamlConfiguration.loadConfiguration(p.messages_file);
        }

        try { //TODO: change try/catch structure to handle errors in yml
            for (String key : p.quests_config.getConfigurationSection("Quests").getKeys(false)) { //for every Quest do that
                String desc = p.quests_config.getString("Quests." + key + ".description"); //get description

                String right_answer = p.quests_config.getString("Quests." + key + ".right_answer"); //get right answer
                ArrayList<String> answers = new ArrayList<String>(); //new answers list
                for (String answer_key : p.quests_config.getConfigurationSection("Quests." + key + ".answers").getKeys(false)) { //for every answer in config
                    answers.add(p.quests_config.getString("Quests." + key + ".answers." + answer_key)); //add answer
                }
                String reward = p.quests_config.getString("Quests." + key + ".reward"); //get reward
                Quest quest = new Quest(desc, reward,p.quests.size() + 1, p.string_to_enum(right_answer), answers); //create new quest
                p.quests.put(quest.id, quest); //add quest to global list
            }

            //load messages
            p.messages.put(quizPlugin.Messages.ERROR_PLAYER_ONLY, p.messages_config.getString("ERROR_PLAYER_ONLY"));
            p.messages.put(quizPlugin.Messages.ERROR_PLUGIN_DISABLED, p.messages_config.getString("ERROR_PLUGIN_DISABLED"));
            p.messages.put(quizPlugin.Messages.NOT_GOT_QUESTION_YET, p.messages_config.getString("NOT_GOT_QUESTION_YET"));
            p.messages.put(quizPlugin.Messages.RIGHT_ANSWER, p.messages_config.getString("RIGHT_ANSWER"));
            p.messages.put(quizPlugin.Messages.WRONG_ANSWER, p.messages_config.getString("WRONG_ANSWER"));
            p.messages.put(quizPlugin.Messages.QUESTION, p.messages_config.getString("QUESTION"));
            p.messages.put(quizPlugin.Messages.HOVERTEXT_HINT_ANSWER, p.messages_config.getString("HOVERTEXT_HINT_ANSWER"));
            p.messages.put(quizPlugin.Messages.ANSWERS, p.messages_config.getString("ANSWERS"));
            p.messages.put(quizPlugin.Messages.HOVERTEXT_HINT_RESTART, p.messages_config.getString("HOVERTEXT_HINT_RESTART"));
        } catch (Exception e) { //if something going wrong
            p.getLogger().warning("При загрузке плагина произошла ошибка: " + e.getMessage());
            p.IsWorking = false;
        }

        p.getLogger().info("Загружено " + p.quests.size() + " вопросов.");

        p.IsWorking = p.quests.size() != 0; //if loaded 0 quests plugin not working
    }
}
