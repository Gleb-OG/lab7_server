package main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.database.DBManager;
import main.invoker.Invoker;
import main.managers.CollectionManager;
import main.managers.ScriptManager;
import main.network.Request;
import main.network.Response;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//ssh -p 2222 -L 5432:localhost:5432 s466366@helios.cs.ifmo.ru JYqp&1503 chcp 1251 dNifs7jxVOz4anOD2
public class Server {
    final int PORT;
    public static boolean scriptMode = false;
    public static Invoker inv = new Invoker();
    public static CollectionManager collectionManager = new CollectionManager();
    public static ScriptManager scriptManager = new ScriptManager(inv);
    private static final DBManager dbManager = new DBManager();
    private static final Logger logger = LogManager.getLogger(Server.class);
    private final ExecutorService processor = Executors.newCachedThreadPool();
    private final ExecutorService responder = Executors.newFixedThreadPool(4);

    public Server(int PORT) {
        this.PORT = PORT;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            logger.info("Сервер запущен на порте {} и ожидает подключения.", PORT);

            while (!serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            logger.error("Критическая ошибка запуска сервера: " + e);
        }
    }

    private void sendResponse(ObjectOutputStream out, Response response) {
        try {
            synchronized (out) {
                out.writeObject(response);
                out.flush();
            }
        } catch (IOException e) {
            logger.error("Ошибка при отправке ответа клиенту: {}.", e.getMessage());
        }
    }

    private void handleClient(Socket clientSocket) {
        logger.info("Подключен клиент: {}.", clientSocket.getInetAddress());

        try (ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())) {

            while (true) {
                Request request;

                try {
                    request = (Request) in.readObject();
                } catch (EOFException eof) {
                    logger.warn("Клиент {} закрыл соединение.", clientSocket.getInetAddress());
                    break;
                }

                if (!dbManager.authenticateUser(request.getLogin(), request.getPassword())) {
                    sendResponse(out, new Response("Аутентификация не была пройдена!"));
                    continue;
                }

                processor.submit(() -> {
                    Response response;

                    try {
                        String result = inv.executeClientCommand(request);

                        if ("exit".equals(request.getCommandName())) {
                            response = new Response("Выход выполнен.\n-----\n" + result + "\n-----", "exit");
                        } else {
                            response = new Response("Результат выполнения команды "
                                    + request.getCommandName()
                                    + ":\n-----\n" + result + "\n-----");
                        }
                    } catch (Exception e) {
                        logger.error("Ошибка при выполнении команды", e);
                        response = new Response("Ошибка на сервере: " + e.getMessage());
                    }

                    Response finalResponse = response;
                    responder.submit(() -> sendResponse(out, finalResponse));
                });
            }
        } catch (Exception e) {
            logger.error("Ошибка в работе с клиентом {}: {}", clientSocket.getInetAddress(), e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException ignore) {}
        }
    }

    public static void main(String[] args) {
        try {
            logger.info("Загружена коллекция из базы данных.");
            collectionManager.loadCollectionFromDB();
        } catch (Exception e) {
            logger.error("Завершение работы программы. Ошибка: " + e.getMessage());
            System.exit(-1);
        }

        int port = 9959;
        Server server = new Server(port);
        server.start();
    }
}

//        public void start() {
//            try {
//                ServerSocket serverSocket = new ServerSocket(PORT);
//
//                logger.info("Сервер запущен на порте {} и ожидает подключения.", PORT);
//
//                while (!serverSocket.isClosed()) {
//                    Socket clientSocket = serverSocket.accept();
//
//                    new Thread(() -> {
//                        logger.info("Установлено соединение с клиентом ({}).", clientSocket.getInetAddress());
//
//                        try (ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
//                             ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())) {
//                            while (true) {
//                                Request request = (Request) in.readObject();
//
//                                if (!dbManager.authenticateUser(request.getLogin(), request.getPassword())) {
//                                    out.writeObject(new Response("Аутентификация не была пройдена!"));
//                                    out.flush();
//                                    continue;
//                                }
//
//                                processor.submit(() -> {
//                                    Response response;
//
//                                    if (request.getCommandName().equals("exit")) {
//                                        response = new Response("Результат выполнения команды " + request.getCommandName() + ", запрошенной клиентом ("
//                                                + clientSocket.getInetAddress() + "): \n-----\n" + inv.executeClientCommand(request) + "\n-----", "exit");
//                                    } else {
//                                        response = new Response("Результат выполнения команды " + request.getCommandName() + ", запрошенной клиентом ("
//                                                + clientSocket.getInetAddress() + "): \n-----\n" + inv.executeClientCommand(request) + "\n-----");
//                                    }
//
//                                    logger.info("Отправлен результат выполнения команды {} клиенту ({}).", request.getCommandName(), clientSocket.getInetAddress());
//
//                                    responder.submit(() -> {
//                                        try {
//                                            synchronized (out) {
//                                                out.writeObject(response);
//                                                out.flush();
//                                            }
//                                        } catch (IOException e) {
//                                            logger.error("Ошибка при передаче данных на клиенту: " + e);
//                                        }
//                                    });
//                                });
//                            }
//                        } catch (EOFException eof) {
//                            logger.warn("Разорвано соединение с клиентом ({})." + eof, clientSocket.getInetAddress());
//                        } catch (Exception e) {
//                            logger.error("Ошибка: " + e);
//                        }
//                    }).start();
//                }
//            } catch (IOException e) {
//                logger.error("Критическая ошибка запуска сервера", e);
//            }
//        }