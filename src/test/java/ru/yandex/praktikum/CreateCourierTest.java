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

// Тесты для метода создания нового курьера
public class CreateCourierTest {

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

    // Успешное создание нового курьера
    @Test
    @DisplayName("Successful creation of a new courier")
    public void createNewCourierAndCheckResponseForSuccess() {
        shouldDeleteCourier = true;

        NewCourierData newCourierData = new NewCourierData("newTestCourier", "1234", "Ivan");
        // Отправка POST запроса
        Response response = courierClient.create(newCourierData); //sendPOSTRequestCreateCourier(newCourierData);
        // Сравнение кода
        courierClient.verifyStatusCode(response, 201);
        // Сравнение результата в теле ответа
        courierClient.verifySuccessCreateIsOkTrue(response);
        // Проверка, что курьер создался, через получение Id курьера
        LoginCourierData loginCourierData = new LoginCourierData(newCourierData);
        Response loginResponse = courierClient.login(loginCourierData);
        courierId = courierClient.getCourierID(loginResponse);
    }


    // Ошибка создания двух одинаковых курьеров
    @Test
    @DisplayName("Failed creation of the same courier twice")
        public void createNewCourierWithSameLoginAndCheckResponseForError() {
        shouldDeleteCourier = true;

        NewCourierData firstCourierData = new NewCourierData("newTestCourier", "1234", "Ivan");
        NewCourierData secondCourierData = new NewCourierData("newTestCourier", "1234", "Petr");
        // Отправка POST запросов
        courierClient.create(firstCourierData);
        Response response = courierClient.create(secondCourierData);
        // Сравнение кода
        courierClient.verifyStatusCode(response, 409);
        // Сравнение результата в теле ответа
        courierClient.verifyFailureCode(response, 409);
        courierClient.verifyFailureMessage(response, "Этот логин уже используется. Попробуйте другой.");
        // Начало пост-обработки: получение ID созданного курьера
        LoginCourierData loginCourierData = new LoginCourierData(firstCourierData);
        Response loginResponse = courierClient.login(loginCourierData);
        courierId = courierClient.getCourierID(loginResponse);

    }

    // Ошибка создания нового курьера без заведения логина
    @Test
    @DisplayName("Failed creation of a new courier without login")
    public void createNewCourierWithNoLoginAndCheckResponseForError() {
        shouldDeleteCourier = false;

        NewCourierData newCourierData = new NewCourierData("", "1234", "Ivan");
        // Отправка POST запроса
        Response response = courierClient.create(newCourierData);
        // Сравнение кода
        courierClient.verifyStatusCode(response, 400);
        // Сравнение результата в теле ответа
        courierClient.verifyFailureCode(response, 400);
        courierClient.verifyFailureMessage(response, "Недостаточно данных для создания учетной записи");
    }

    // Ошибка создания нового курьера без заведения логина
    @Test
    @DisplayName("Failed creation of a new courier without password")
    public void createNewCourierWithNoPasswordAndCheckResponseForError() {
        shouldDeleteCourier = false;

        NewCourierData newCourierData = new NewCourierData("newTestCourier", "", "Ivan");
        // Отправка POST запроса
        Response response = courierClient.create(newCourierData);
        // Сравнение кода
        courierClient.verifyStatusCode(response, 400);
        // Сравнение результата в теле ответа
        courierClient.verifyFailureCode(response, 400);
        courierClient.verifyFailureMessage(response, "Недостаточно данных для создания учетной записи");
    }

    // Пост-обработка
    @After
    public void tearDown() {
        if (shouldDeleteCourier) {
            LoginCourierData loginCourierData = new LoginCourierData("newTestCourier", "1234");

            DeleteCourierData deleteCourierData = new DeleteCourierData(courierId);

            // Пост-обработка: удаление созданного курьера
            courierClient.delete(deleteCourierData);
        }
    }
}
