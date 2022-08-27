package ru.yandex.praktikum.data;

// Класс для JSON-а входных данных (тела) для удаления курьера
public class DeleteCourierData {
    // Параметры для полей JSON-а
    private int id;

    // Конструктор с параметрами
    public DeleteCourierData(int id) {
        this.id = id;
    }

    // Конструктор без параметров для работы библиотеки Gson
    public DeleteCourierData() {
    }

    // Геттеры и сеттеры (автоматически сгенерированные)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
