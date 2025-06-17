package lab;

import lab.invoker.Invoker;
import lab.managers.CollectionManager;
import java.io.*;
import java.net.*;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.sql.Timestamp;
import lab.network.Request;


public class Server extends Thread {
    private static Server server = new Server();
    public static int port = 5745;
    public static Invoker invoker = new Invoker();
    public static CollectionManager collectionManager = new CollectionManager();
    public static String filename;
    public static boolean scriptMode = false;


    public void run() {
        try (ServerSocketChannel channel = ServerSocketChannel.open(); Selector selector = Selector.open()) {
            channel.socket().bind(new InetSocketAddress(9999));
            channel.configureBlocking(false);
            ServerSocket serverSocket = new ServerSocket(port);
            channel.register(selector, SelectionKey.OP_ACCEPT);
            while (true) {
                try {
                    OutputStreamWriter outputStream = new OutputStreamWriter(new FileOutputStream(filename));
                    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                        try {
                            outputStream.write(collectionManager.getCollection().toString());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        System.out.println("Server stopped. Collection saved.");
                    }));
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
                ByteBuffer buffer = ByteBuffer.allocate(4096);
                try {
                    System.out.println("Ожидание подключения клиента на порт " + serverSocket.getLocalPort() + "...");
                    Socket server = serverSocket.accept();
                    SocketChannel client = channel.accept();
                    client.register(selector, SelectionKey.OP_READ);
                    client.configureBlocking(false);
                    byte[] receiveData = new byte[1024];
                    byte[] responseData;
                    SocketAddress IPAddress = server.getRemoteSocketAddress();
                    int port = server.getPort();
                    System.out.println("Клиент подключен с портом: " + IPAddress);
                    while (true) {
                        try {
                            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                            System.out.println("[" + timestamp + ", IP: " + IPAddress + ", Port: " + port + "] ");
                            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(receiveData);
                            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
                            Request receivedObject = (Request) objectInputStream.readObject();
                            DataOutputStream out = new DataOutputStream(server.getOutputStream());
                            DataInputStream in = new DataInputStream(server.getInputStream());
                            String response = Invoker.call(receivedObject).getMessage();
                            System.out.println(response);
                            responseData = response.getBytes();
                            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                            ObjectOutputStream objectOut = new ObjectOutputStream(byteOut);
                            objectOut.writeObject(responseData);
                            byte[] bytes = byteOut.toByteArray();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

        public static Invoker getInvoker (Invoker invoker){
            return invoker;
        }

        public static CollectionManager getCollectionManager () {
            return collectionManager;
        }
        public static void setCollection(CollectionManager collectionManager){
            Server.collectionManager = collectionManager;
        }

        public static void setInvoker(Invoker invoker){
            Server.invoker = invoker;
        }

        public static String getFilename () {
            return filename;
        }

        public static Server setFilename (String filename){
            Server.filename = filename;
            return null;
        }

        public static void main (String[]args) {
            /**
             * Проверка наличия аргумента командной строки
             */
            if (args.length == 0) {
                System.out.println("Ошибка: Не указано имя файла в аргументах командной строки.");
                return;
            }
            server = Server.setFilename(args[0]);
            /**
             * Сохранение имени файла из аргументов командной строки
             */
            Thread thread = new Server();
            thread.start();
        }
}





