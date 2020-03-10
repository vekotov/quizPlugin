package vekotov.ru.quizPlugin;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

public class ConfigLoading {
    File quests_file;
    File messages_file;
    FileConfiguration quests_config;
    FileConfiguration messages_config;

    quizPlugin p;

    public ConfigLoading(quizPlugin p) {
        this.p = p;

        p.playerquests.clear();
        p.quests.clear();
        p.messages.clear(); //firstly clearing old configs

        quests_file = new File(p.getDataFolder(), "quests.yml");
        messages_file = new File(p.getDataFolder(), "messages.yml");

        if (!quests_file.exists()) { //create and load new quests file from resources if file not exist
            p.saveResource("quests.yml", false);
        }
        if (!messages_file.exists()) { //create and load new messages file from resources if file not exist
            p.saveResource("messages.yml", false);
        }

        messages_config = YamlConfiguration.loadConfiguration(messages_file);
        quests_config = YamlConfiguration.loadConfiguration(quests_file);
        /*

        How does anti-fool defence work: we handling a situation if config doesnt exist
        and if config broken (in which case we will handle exception dropped by get(something)

         */
        for (String key : quests_config.getConfigurationSection("Quests").getKeys(false)) { //for every Quest do that
            String desc;
            String right_answer;
            String reward;

            ArrayList<String> answers = new ArrayList<String>(); //new answers list
            for (String answer_key : quests_config.getConfigurationSection("Quests." + key + ".answers").getKeys(false)) { //for every answer in config
                String answer = safeLoadString(key, "answers." + answer_key);
                if(answer == null)continue;
                answers.add(answer);
            }

            if(answers.size() < 2){
                p.getLogger().warning("При загрузке файла quests.yml произошла ошибка: квест " + key + " имеет всего " + answers.size() + " ответов, нужно миниум 2. Квест не будет загружен.");
            }

            desc = safeLoadString(key, "description");
            if(desc == null)continue;

            right_answer = safeLoadString(key, "right_answer");
            if(right_answer == null)continue;

            reward = safeLoadString(key, "reward");
            if(reward == null)continue;

            Quest quest = new Quest(desc, reward, p.quests.size() + 1, right_answer, answers); //create new quest
            p.quests.put(quest.id, quest); //add quest to global list
        }

        String[] keys = {
                "ERROR_PLAYER_ONLY",
                "ERROR_PLUGIN_DISABLED",
                "NOT_GOT_QUESTION_YET",
                "RIGHT_ANSWER",
                "WRONG_ANSWER",
                "QUESTION",
                "HOVERTEXT_HINT_ANSWER",
                "HOVERTEXT_HINT_RESTART",
                "ANSWERS"
        };

        for(String key : keys){
            load_message(key);
        }

        p.getLogger().info("Загружено " + p.quests.size() + " вопросов.");
    }

    void load_message(String key){
        try{
            if(messages_config.contains(key)) {
                p.messages.put(key, messages_config.getString(key));
            }else{
                exceptionLogMessageLoading(key);
                loadDefaultMessage(key);
            }
        }catch (Exception e){
            exceptionLogMessageLoading(key);
            loadDefaultMessage(key);
        }
    }

    void exceptionLogMessageLoading(String key){
        p.getLogger().warning("При загрузке файла messages.yml произошла ошибка: не удалось загрузить строку " + key + ". Загружена стандартная строка.");
    }

    void loadDefaultMessage(String key){
        Reader defConfigStream = new InputStreamReader(p.getResource("messages.yml"));
        p.messages.put(key, YamlConfiguration.loadConfiguration(defConfigStream).getString(key));
    }

    String safeLoadString(String key, String field){
        try {
            if(quests_config.contains("Quests." + key + "." + field))
                return quests_config.getString("Quests." + key + "." + field); //get reward
            else{
                exceptionLogQuestLoading(key, field);
                return null;
            }
        }catch (Exception e){
            exceptionLogQuestLoading(key, field);
            return null;
        }
    }

    void exceptionLogQuestLoading(String key, String field){
        p.getLogger().warning("При загрузке файла quests.yml произошла ошибка: квесту " + key  + " не удалось загрузить поле " + field + ". Квест не будет загружен.");
    }
}
