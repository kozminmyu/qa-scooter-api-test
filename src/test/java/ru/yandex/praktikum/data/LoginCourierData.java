package ru.yandex.praktikum.data;

// Класс для JSON-а входных данных (тела) для авторизации курьера
public class LoginCourierData {
    // Параметры для полей JSON-а
    private String login;
    private String password;

    // Конструктор с параметрами
    public LoginCourierData(String login, String password) {
        this.login = login;
        this.password = password;
    }

    // Конструкор из данных курьера
    public LoginCourierData(NewCourierData newCourierData) {
        this.login = newCourierData.getLogin();
        this.password = newCourierData.getPassword();
    }

    // Конструктор без параметров для работы библиотеки Gson
    public LoginCourierData() {
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
}
