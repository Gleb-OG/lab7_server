package lab.exceptions;


public class WrongArgsNumber extends RuntimeException {

    private final int number;

    public WrongArgsNumber(int number) {
        this.number = number;
    }

    public String getMessage() {
        return "Введено неверное количество аргументов, ожидалось: " + number;
    }
}

