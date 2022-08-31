package ru.yandex.praktikum;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.client.CourierClient;
import ru.yandex.praktikum.data.DeleteCourierData;
import ru.yandex.praktikum.data.LoginCourierData;
import ru.yandex.praktikum.data.NewCourierData;

public class LoginCourierTest {

    // Создаем объект клиента
    private CourierClient courierClient = new CourierClient();

    // Признак необходимости удаления куреьера при пост-обработке
    private boolean shouldDeleteCourier;

    // ID созданного курьера
    private int courierId;

    @Before
    public void setUp() {
        courierClient.setUp();
    }

    // Успешная авторизация по логину и паролю
    @Test
    @DisplayName("Successful login")
    public void loginWithExistingLoginAndPasswordAndCheckResponseForSuccess() {
        shouldDeleteCourier = true;

        NewCourierData newCourierData = new NewCourierData("newTestCourier", "1234", "Ivan");
        LoginCourierData loginCourierData = new LoginCourierData(newCourierData);
        // Создание нового курьера
        courierClient.create(newCourierData);
        // Отправка POST запроса
        Response response = courierClient.login(loginCourierData);
        // Сравнение кода
        courierClient.verifyStatusCode(response, 200);
        // Проверка результата в теле ответа
        courierClient.verifySuccessLoginIdNotNull(response);
        courierId = courierClient.getCourierID(response);
    }

    // Ошибочная авторизация по несуществующему логину
    @Test
    @DisplayName("Login with non-existent login")
    public void loginWithNonexistentLoginAndCheckResponseForError() {
        shouldDeleteCourier = false;

        LoginCourierData loginCourierData = new LoginCourierData("newTestCourier", "1234");
        // Отправка POST запроса
        Response response = courierClient.login(loginCourierData);
        // Сравнение кода
        courierClient.verifyStatusCode(response, 404);
        // Проверка результата в теле ответа
        courierClient.verifyFailureCode(response, 404);
        courierClient.verifyFailureMessage(response, "Учетная запись не найдена");
    }

    // Ошибка авторизации по некорректному паролю
    @Test
    @DisplayName("Login with incorrect password")
    public void loginWithWrongPasswordAndCheckResponseForError() {
        shouldDeleteCourier = true;

        NewCourierData newCourierData = new NewCourierData("newTestCourier", "1234", "Ivan");
        // Создание данных с неправильным паролем
        LoginCourierData loginCourierData = new LoginCourierData("newTestCourier", "4321");
        // Создание нового курьера
        courierClient.create(newCourierData);
        // Отправка POST запроса
        Response response = courierClient.login(loginCourierData);
        // Сравнение кода
        courierClient.verifyStatusCode(response, 404);
        // Проверка результата в теле ответа
        courierClient.verifyFailureCode(response, 404);
        courierClient.verifyFailureMessage(response, "Учетная запись не найдена");
    }

    // Ошибочная авторизация по неуказанному логину
    @Test
    @DisplayName("Login with no login")
    public void loginWithNoLoginAndCheckResponseForError() {
        shouldDeleteCourier = false;

        LoginCourierData loginCourierData = new LoginCourierData("", "1234");
        // Отправка POST запроса
        Response response = courierClient.login(loginCourierData);
        // Сравнение кода
        courierClient.verifyStatusCode(response, 400);
        // Сравнение результата в теле ответа
        courierClient.verifyFailureCode(response, 400);
        courierClient.verifyFailureMessage(response, "Недостаточно данных для входа");
    }

    // Ошибочная авторизация по неуказанному паролю
    @Test
    @DisplayName("Login with no password")
    public void loginWithNoPasswordAndCheckResponseForError() {
        shouldDeleteCourier = false;

        LoginCourierData loginCourierData = new LoginCourierData("newTestCourier", "");
        // Отправка POST запроса
        Response response = courierClient.login(loginCourierData);
        // Сравнение кода
        courierClient.verifyStatusCode(response, 400);
        // Сравнение результата в теле ответа
        courierClient.verifyFailureCode(response, 400);
        courierClient.verifyFailureMessage(response, "Недостаточно данных для входа");
    }

    // Пост-обработка
    @After
    public void tearDown() {
        if (shouldDeleteCourier) {

            DeleteCourierData deleteCourierData = new DeleteCourierData(courierId);

            // Пост-обработка: удаление созданного курьера
            courierClient.delete(deleteCourierData);
        }
    }
}
