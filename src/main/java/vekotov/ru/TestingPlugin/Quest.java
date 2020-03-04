package vekotov.ru.TestingPlugin;

public class Quest {
    public String description;
    public int id;
    public ANSWER right_answer;
    public String[] answers;

    public Quest(String description, int id, ANSWER right_answer, String[] answers) {
        this.description = description;
        this.id = id;
        this.right_answer = right_answer;
        this.answers = answers;
    }

    public static enum ANSWER {
        A, B, C, D;
    }
}