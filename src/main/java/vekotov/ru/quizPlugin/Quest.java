package vekotov.ru.quizPlugin;

import java.util.ArrayList;

public class Quest {
    public String description;
    public String reward;
    public String answer_style;
    //TODO: Create categories
    public int id;
    public String right_answer;
    public ArrayList<String> answers;

    public Quest(String description, String answer_style, String reward, int id, String right_answer, ArrayList<String> answers) {
        this.description = description;
        this.answer_style = answer_style;
        this.reward = reward;
        this.id = id;
        this.right_answer = right_answer;
        this.answers = answers;
    }
}