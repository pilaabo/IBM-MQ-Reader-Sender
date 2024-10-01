package ru.digitalspirit;

import com.ibm.mq.*;
import com.ibm.mq.constants.CMQC;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class MQSender {
    public static void main(String[] args) {
        String queueManagerName = "";
        String queueName = "";
        String hostname = "";
        int port = 1414;
        String channel = "";

        MQQueueManager queueManager = null;

        try {
            // Устанавливаем параметры подключения
            MQEnvironment.hostname = hostname;
            MQEnvironment.port = port;
            MQEnvironment.channel = channel;

            // Подключаемся к менеджеру очереди
            queueManager = new MQQueueManager(queueManagerName);

            // Открываем очередь для отправки
            int openOptions = CMQC.MQOO_OUTPUT;
            MQQueue queue = queueManager.accessQueue(queueName, openOptions);

            // Создаем сообщение
            MQMessage message = new MQMessage();
            String filePath = "app/src/main/resources/message";
            String rawMessage = Files.readString(Path.of(filePath));
            message.writeUTF(rawMessage);

            // Устанавливаем параметры отправки
            MQPutMessageOptions pmo = new MQPutMessageOptions();

            // Отправляем сообщение в очередь
            queue.put(message, pmo);

            System.out.println("Сообщение успешно отправлено в очередь!");

            // Закрываем очередь
            queue.close();
        } catch (MQException e) {
            System.err.println("Произошла ошибка: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Ошибка ввода/вывода: " + e.getMessage());
        } finally {
            try {
                if (queueManager != null) {
                    queueManager.disconnect();
                }
            } catch (MQException e) {
                System.err.println("Ошибка при отключении: " + e.getMessage());
            }
        }
    }
}
