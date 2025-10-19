package ru.yandex.servise;

import ru.yandex.model.Intent;
import ru.yandex.servise.util.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {
    private final HashMap<Integer, Node> idToIntentsHistory = new HashMap<>(); // хранит id задачи + узел списка
    private Node head;
    private Node tail;

    @Override
    public List<Intent> getHistory() {
        return new ArrayList<>(getIntents());
    }

    // добавление задачи любого типа в историю просмотров, если она уже есть - удаляем старое положение, добавляем в конец
    // если добавление успешно, возвращаем саму же задачу, если добавить не удалось - null
    @Override
    public Intent addToHistory(Intent intent) {
        if (intent == null) {
            return null;
        } else { // контроли пройдены: задача не пустая
            removeFromHistory(intent.getId()); // удаляем старое вхождение
            linkLast(intent); // вставляем новый просмотр в конец списка
            return intent;
        }
    }

    // удаление задачи из истории по id
    @Override
    public void removeFromHistory(int id) {
        Node n = idToIntentsHistory.get(id);  // получаем узел из хэш-таблицы
        removeNode(n); // удаляем узел
        idToIntentsHistory.remove(id); // удаляем id
    }

    // добавление задачи в конец двусвязного списка
    private void linkLast(Intent intent) {
        int taskId = intent.getId();
        final Node tail = this.tail; // получаем текущий хвост
        final Node newNode = new Node(intent, null, tail); // создаём новый узел
        this.tail = newNode; // назначаем добавленный узел хвостом

        if (tail == null) {
            this.head = newNode; // если список был пуст назначаем новый хвост также и головой
        } else {
            tail.setNext(newNode); // если не был пуст, назначаем next старого хвоста - новый хвост
        }
        idToIntentsHistory.put(taskId, newNode); // добавляем элемент в хэш-таблицу
    }

    // метод для сборки истории просмотров из двусвязного списка
    private List<Intent> getIntents() {
        List<Intent> intents = new ArrayList<>();

        for (Node n = head; n != null; n = n.getNext()) { // идём по узлам от головы к хвосту
            if (n.getItem() != null) {
                intents.add(n.getItem()); // добавляем узлы по порядку
            }
        }
        return intents;
    }

    // удаление узла из двусвязного списка
    private void removeNode(Node node) {
        if (node != null) {
            Node nextNode = node.getNext();
            Node lastNode = node.getLast();

            if (node == head) { // если узел на удаление - голова
                head = nextNode; // головой становится его next
                if (nextNode != null) {
                    nextNode.setLast(null); // если next существует, обнуляем его предыдущий
                } else {
                    tail = null; // если next == null, значит список стал пуст — обнуляем хвост
                }
            } else if (node == tail) { // если узел на удаление - хвост
                tail = lastNode;  // хвостом становится его last
                if (lastNode != null) {
                    lastNode.setNext(null); // если last существует, обнуляем его предыдущий
                }
            } else { // если узел на удаление не голова и не хвост, связываем м-у собой его last и next
                lastNode.setNext(nextNode);  // следующий last-а = next удаляемого
                nextNode.setLast(lastNode); // предыдущий next-а = last удаляемого
            }
        }
    }

}
