package lab.interfaces;

import lab.exceptions.InvalidDataException;
import lab.network.Request;
import lab.network.Response;

import java.io.IOException;

/**
 * Интерфейс паттерна command
 */
public interface CommandInterface {
    Response execute(String[] args) throws InvalidDataException;
    Response execute(Request request) throws IOException, InvalidDataException;
    String description();
}
