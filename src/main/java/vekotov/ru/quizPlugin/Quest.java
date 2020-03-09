package vekotov.ru.quizPlugin;

import java.util.ArrayList;

public class Quest {
    public String description;
    public String reward;
    //TODO: Create categories
    //TODO: Add various types of printing answers (1-line, 2-line, 4-line)
    public int id;
    public ANSWER right_answer;
    public ArrayList<String> answers;

    public Quest(String description, String reward, int id, ANSWER right_answer, ArrayList<String> answers) {
        this.description = description;
        this.reward = reward;
        this.id = id;
        this.right_answer = right_answer;
        this.answers = answers;
    }

    public enum ANSWER {
        A, B, C, D
    }
}