package lab.managers;

/**
 * Хранит команды, введенные пользователем, и их аргументы в строковом представлении.
 */
public class ConsoleManager {

    private String[] tokens;

    public String[] getTokens() {
        return tokens;
    }

    public String getToken(int i) {
        return tokens[i];
    }

    public void setTokens(String[] tokens) {
        this.tokens = tokens;
    }
}