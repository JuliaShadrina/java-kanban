package ru.yandex.servise.util;

import ru.yandex.model.Intent;

public class Node { // хранение узла двусвязного списка: задача + 2 ссылки на соседние узлы
    private Intent item;
    private Node next;
    private Node last;

    // реализация = конструктор + геттеры + сеттеры
    public Node(Intent item, Node next, Node last) {
        this.item = item;
        this.next = next;
        this.last = last;
    }

    public Intent getItem() {
        return item;
    }

    public void setItem(Intent item) {
        this.item = item;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public Node getLast() {
        return last;
    }

    public void setLast(Node last) {
        this.last = last;
    }

}
