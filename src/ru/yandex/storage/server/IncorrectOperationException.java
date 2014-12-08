package ru.yandex.storage.server;

public class IncorrectOperationException extends RuntimeException {
    IncorrectOperationException(String message) {
        super(message);
    }
}
