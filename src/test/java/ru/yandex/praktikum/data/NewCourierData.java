package ru.yandex.praktikum.data;

// Класс для JSON-а входных данных (тела) для создания нового курьера
public class NewCourierData {
    // Параметры для полей JSON-а
    private String login;
    private String password;
    private String firstName;

    // Конструктор с параметрами
    public NewCourierData(String login, String password, String firstName) {
        this.login = login;
        this.password = password;
        this.firstName = firstName;
    }

    // Конструктор без параметров для работы библиотеки Gson
    public NewCourierData() {
    }

    // Геттеры и сеттеры (автоматически сгенерированные)
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

}
