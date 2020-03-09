package vekotov.ru.quizPlugin;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

public class configLoading {
    static public void loadConfigs(quizPlugin p) {
        File quests_file = new File(p.getDataFolder(), "quests.yml");
        File messages_file = new File(p.getDataFolder(), "messages.yml");
        FileConfiguration quests_config = YamlConfiguration.loadConfiguration(quests_file);
        FileConfiguration messages_config = YamlConfiguration.loadConfiguration(messages_file);

        p.playerquests.clear();
        p.quests.clear();
        p.messages.clear(); //firstly clearing old configs

        if (!quests_file.exists()) { //create and load new quests file from resources if file not exist
            p.saveResource("quests.yml", false);
            quests_file = new File(p.getDataFolder(), "quests.yml");
            quests_config = YamlConfiguration.loadConfiguration(quests_file);
        }
        if (!messages_file.exists()) { //create and load new messages file from resources if file not exist
            p.saveResource("messages.yml", false);
            messages_file = new File(p.getDataFolder(), "messages.yml");
            messages_config = YamlConfiguration.loadConfiguration(messages_file);
        }


        /*

        How does anti-fool defence work: we handling a situation if config doesnt exist
        and if config broken (in which case we will handle exception dropped by get(something)

         */
        for (String key : quests_config.getConfigurationSection("Quests").getKeys(false)) { //for every Quest do that
            boolean skip_quest = false; //if something going wrong we just skipping quest with wrong configuration

            String desc = "Default Descrpition";
            String right_answer = "A";
            String reward = "";

            try {
                if(quests_config.contains("Quests." + key + ".description"))
                    desc = quests_config.getString("Quests." + key + ".description"); //get description
                else{
                    skip_quest = true;
                    p.getLogger().warning("При загрузке файла quests.yml произошла ошибка: квесту " + key  + " не удалось загрузить описание. Квест не будет загружен.");
                }
            }catch (Exception e){
                skip_quest = true;
                p.getLogger().warning("При загрузке файла quests.yml произошла ошибка: квесту " + key  + " не удалось загрузить описание. Квест не будет загружен.");
            }

            ArrayList<String> answers = new ArrayList<String>(); //new answers list
            for (String answer_key : quests_config.getConfigurationSection("Quests." + key + ".answers").getKeys(false)) { //for every answer in config
                try{
                    answers.add(quests_config.getString("Quests." + key + ".answers." + answer_key)); //add answer
                }catch (Exception e){
                    skip_quest = true;
                    p.getLogger().warning("При загрузке файла quests.yml произошла ошибка: квесту " + key  + " не удалось загрузить ответ '" + answer_key + "'. Квест не будет загружен.");
                }
            }

            if(answers.size() < 2){
                skip_quest = true;
                p.getLogger().warning("При загрузке файла quests.yml произошла ошибка: квест " + key + " имеет всего " + answers.size() + " ответов, нужно миниум 2. Квест не будет загружен.");
            }

            try {
                if(quests_config.contains("Quests." + key + ".right_answer"))
                    right_answer = quests_config.getString("Quests." + key + ".right_answer"); //get right answer
                else{
                    skip_quest = true;
                    p.getLogger().warning("При загрузке файла quests.yml произошла ошибка: квесту " + key  + " не удалось загрузить верный ответ. Квест не будет загружен.");
                }
            }catch (Exception e){
                skip_quest = true;
                p.getLogger().warning("При загрузке файла quests.yml произошла ошибка: квесту " + key  + " не удалось загрузить верный ответ. Квест не будет загружен.");
            }

            try {
                if(quests_config.contains("Quests." + key + ".right_answer"))
                    reward = quests_config.getString("Quests." + key + ".reward"); //get reward
                else{
                    skip_quest = true;
                    p.getLogger().warning("При загрузке файла quests.yml произошла ошибка: квесту " + key  + " не удалось загрузить награду. Квест не будет загружен.");
                }
            }catch (Exception e){
                skip_quest = true;
                p.getLogger().warning("При загрузке файла quests.yml произошла ошибка: квесту " + key  + " не удалось загрузить награду. Квест не будет загружен.");
            }

            if(!skip_quest) {
                Quest quest = new Quest(desc, reward, p.quests.size() + 1, p.string_to_enum(right_answer), answers); //create new quest
                p.quests.put(quest.id, quest); //add quest to global list
            }
        }


        //load messages
        try {
            if(messages_config.contains("ERROR_PLAYER_ONLY"))
                p.messages.put(quizPlugin.Messages.ERROR_PLAYER_ONLY, messages_config.getString("ERROR_PLAYER_ONLY"));
            else{
                Reader defConfigStream = new InputStreamReader(p.getResource("messages.yml"));
                p.messages.put(quizPlugin.Messages.ERROR_PLAYER_ONLY, YamlConfiguration.loadConfiguration(defConfigStream).getString("ERROR_PLAYER_ONLY"));
                p.getLogger().warning("При загрузке файла messages.yml произошла ошибка: не удалось загрузить строку ERROR_PLAYER_ONLY. Загружена стандартная строка.");
            }
        }catch (Exception e){
            Reader defConfigStream = new InputStreamReader(p.getResource("messages.yml"));
            p.messages.put(quizPlugin.Messages.ERROR_PLAYER_ONLY, YamlConfiguration.loadConfiguration(defConfigStream).getString("ERROR_PLAYER_ONLY"));
            p.getLogger().warning("При загрузке файла messages.yml произошла ошибка: не удалось загрузить строку ERROR_PLAYER_ONLY. Загружена стандартная строка.");
        }

        try{
            if(messages_config.contains("ERROR_PLUGIN_DISABLED"))
                p.messages.put(quizPlugin.Messages.ERROR_PLUGIN_DISABLED, messages_config.getString("ERROR_PLUGIN_DISABLED"));
            else{
                Reader defConfigStream = new InputStreamReader(p.getResource("messages.yml"));
                p.messages.put(quizPlugin.Messages.ERROR_PLUGIN_DISABLED, YamlConfiguration.loadConfiguration(defConfigStream).getString("ERROR_PLUGIN_DISABLED"));
                p.getLogger().warning("При загрузке файла messages.yml произошла ошибка: не удалось загрузить строку ERROR_PLUGIN_DISABLED. Загружена стандартная строка.");
            }
        }catch (Exception e){
            Reader defConfigStream = new InputStreamReader(p.getResource("messages.yml"));
            p.messages.put(quizPlugin.Messages.ERROR_PLUGIN_DISABLED, YamlConfiguration.loadConfiguration(defConfigStream).getString("ERROR_PLUGIN_DISABLED"));
            p.getLogger().warning("При загрузке файла messages.yml произошла ошибка: не удалось загрузить строку ERROR_PLUGIN_DISABLED. Загружена стандартная строка.");
        }

        try {
            if(messages_config.contains("NOT_GOT_QUESTION_YET"))
                p.messages.put(quizPlugin.Messages.NOT_GOT_QUESTION_YET, messages_config.getString("NOT_GOT_QUESTION_YET"));
            else {
                Reader defConfigStream = new InputStreamReader(p.getResource("messages.yml"));
                p.messages.put(quizPlugin.Messages.NOT_GOT_QUESTION_YET, YamlConfiguration.loadConfiguration(defConfigStream).getString("NOT_GOT_QUESTION_YET"));
                p.getLogger().warning("При загрузке файла messages.yml произошла ошибка: не удалось загрузить строку NOT_GOT_QUESTION_YET. Загружена стандартная строка.");
            }
        }catch (Exception e){
            Reader defConfigStream = new InputStreamReader(p.getResource("messages.yml"));
            p.messages.put(quizPlugin.Messages.NOT_GOT_QUESTION_YET, YamlConfiguration.loadConfiguration(defConfigStream).getString("NOT_GOT_QUESTION_YET"));
            p.getLogger().warning("При загрузке файла messages.yml произошла ошибка: не удалось загрузить строку NOT_GOT_QUESTION_YET. Загружена стандартная строка.");
        }

        try {
            if(messages_config.contains("RIGHT_ANSWER"))
                p.messages.put(quizPlugin.Messages.RIGHT_ANSWER, messages_config.getString("RIGHT_ANSWER"));
            else{
                Reader defConfigStream = new InputStreamReader(p.getResource("messages.yml"));
                p.messages.put(quizPlugin.Messages.RIGHT_ANSWER, YamlConfiguration.loadConfiguration(defConfigStream).getString("RIGHT_ANSWER"));
                p.getLogger().warning("При загрузке файла messages.yml произошла ошибка: не удалось загрузить строку RIGHT_ANSWER. Загружена стандартная строка.");
            }
        }catch (Exception e){
            Reader defConfigStream = new InputStreamReader(p.getResource("messages.yml"));
            p.messages.put(quizPlugin.Messages.RIGHT_ANSWER, YamlConfiguration.loadConfiguration(defConfigStream).getString("RIGHT_ANSWER"));
            p.getLogger().warning("При загрузке файла messages.yml произошла ошибка: не удалось загрузить строку RIGHT_ANSWER. Загружена стандартная строка.");
        }

        try{
            if(messages_config.contains("WRONG_ANSWER"))
                p.messages.put(quizPlugin.Messages.WRONG_ANSWER, messages_config.getString("WRONG_ANSWER"));
            else{
                Reader defConfigStream = new InputStreamReader(p.getResource("messages.yml"));
                p.messages.put(quizPlugin.Messages.WRONG_ANSWER, YamlConfiguration.loadConfiguration(defConfigStream).getString("WRONG_ANSWER"));
                p.getLogger().warning("При загрузке файла messages.yml произошла ошибка: не удалось загрузить строку WRONG_ANSWER. Загружена стандартная строка.");
            }
        }catch (Exception e){
            Reader defConfigStream = new InputStreamReader(p.getResource("messages.yml"));
            p.messages.put(quizPlugin.Messages.WRONG_ANSWER, YamlConfiguration.loadConfiguration(defConfigStream).getString("WRONG_ANSWER"));
            p.getLogger().warning("При загрузке файла messages.yml произошла ошибка: не удалось загрузить строку WRONG_ANSWER. Загружена стандартная строка.");
        }

        try {
            if(messages_config.contains("QUESTION"))
                p.messages.put(quizPlugin.Messages.QUESTION, messages_config.getString("QUESTION"));
            else{
                Reader defConfigStream = new InputStreamReader(p.getResource("messages.yml"));
                p.messages.put(quizPlugin.Messages.QUESTION, YamlConfiguration.loadConfiguration(defConfigStream).getString("QUESTION"));
                p.getLogger().warning("При загрузке файла messages.yml произошла ошибка: не удалось загрузить строку QUESTION. Загружена стандартная строка.");
            }
        }catch (Exception e){
            Reader defConfigStream = new InputStreamReader(p.getResource("messages.yml"));
            p.messages.put(quizPlugin.Messages.QUESTION, YamlConfiguration.loadConfiguration(defConfigStream).getString("QUESTION"));
            p.getLogger().warning("При загрузке файла messages.yml произошла ошибка: не удалось загрузить строку QUESTION. Загружена стандартная строка.");
        }

        try {
            if(messages_config.contains("HOVERTEXT_HINT_ANSWER"))
                p.messages.put(quizPlugin.Messages.HOVERTEXT_HINT_ANSWER, messages_config.getString("HOVERTEXT_HINT_ANSWER"));
            else{
                Reader defConfigStream = new InputStreamReader(p.getResource("messages.yml"));
                p.getLogger().warning("При загрузке файла messages.yml произошла ошибка: не удалось загрузить строку HOVERTEXT_HINT_ANSWER. Загружена стандартная строка.");
                p.messages.put(quizPlugin.Messages.HOVERTEXT_HINT_ANSWER, YamlConfiguration.loadConfiguration(defConfigStream).getString("HOVERTEXT_HINT_ANSWER"));
            }
        }catch (Exception e){
            Reader defConfigStream = new InputStreamReader(p.getResource("messages.yml"));
            p.getLogger().warning("При загрузке файла messages.yml произошла ошибка: не удалось загрузить строку HOVERTEXT_HINT_ANSWER. Загружена стандартная строка.");
            p.messages.put(quizPlugin.Messages.HOVERTEXT_HINT_ANSWER, YamlConfiguration.loadConfiguration(defConfigStream).getString("HOVERTEXT_HINT_ANSWER"));
        }

        try {
            if(messages_config.contains("ANSWERS"))
                p.messages.put(quizPlugin.Messages.ANSWERS, messages_config.getString("ANSWERS"));
            else {
                Reader defConfigStream = new InputStreamReader(p.getResource("messages.yml"));
                p.messages.put(quizPlugin.Messages.ANSWERS, YamlConfiguration.loadConfiguration(defConfigStream).getString("ANSWERS"));
                p.getLogger().warning("При загрузке файла messages.yml произошла ошибка: не удалось загрузить строку ANSWERS. Загружена стандартная строка.");
            }
        }catch (Exception e){
            Reader defConfigStream = new InputStreamReader(p.getResource("messages.yml"));
            p.messages.put(quizPlugin.Messages.ANSWERS, YamlConfiguration.loadConfiguration(defConfigStream).getString("ANSWERS"));
            p.getLogger().warning("При загрузке файла messages.yml произошла ошибка: не удалось загрузить строку ANSWERS. Загружена стандартная строка.");
        }

        try {
            if(messages_config.contains("HOVERTEXT_HINT_RESTART"))
                p.messages.put(quizPlugin.Messages.HOVERTEXT_HINT_RESTART, messages_config.getString("HOVERTEXT_HINT_RESTART"));
            else{
                Reader defConfigStream = new InputStreamReader(p.getResource("messages.yml"));
                p.messages.put(quizPlugin.Messages.HOVERTEXT_HINT_RESTART, YamlConfiguration.loadConfiguration(defConfigStream).getString("HOVERTEXT_HINT_RESTART"));
                p.getLogger().warning("При загрузке файла messages.yml произошла ошибка: не удалось загрузить строку HOVERTEXT_HINT_RESTART. Загружена стандартная строка.");
            }
        }catch (Exception e){
            Reader defConfigStream = new InputStreamReader(p.getResource("messages.yml"));
            p.messages.put(quizPlugin.Messages.HOVERTEXT_HINT_RESTART, YamlConfiguration.loadConfiguration(defConfigStream).getString("HOVERTEXT_HINT_RESTART"));
            p.getLogger().warning("При загрузке файла messages.yml произошла ошибка: не удалось загрузить строку HOVERTEXT_HINT_RESTART. Загружена стандартная строка.");
        }

        p.getLogger().info("Загружено " + p.quests.size() + " вопросов.");

        if(p.quests.size() == 0){
            p.IsWorking = false;
            p.getLogger().warning("Плагин был выключен по причине отсутствия доступных вопросов.");
        }else {
            p.IsWorking = true;
        }
    }
}
