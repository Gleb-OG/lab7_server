package main.interfaces;

import main.exceptions.InvalidDataException;
import main.network.Request;

import java.io.IOException;

/**
 * Интерфейс паттерна command
 */
public interface CommandInterface {
    String execute(String[] args) throws InvalidDataException;
    String execute(Request request) throws IOException, InvalidDataException;
    String description();
}
