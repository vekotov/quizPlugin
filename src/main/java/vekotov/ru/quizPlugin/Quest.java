package vekotov.ru.quizPlugin;

import java.util.ArrayList;

public class Quest {
    public String description;
    public String reward;
    public String category; //TODO: Create categories
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