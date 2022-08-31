package ru.yandex.praktikum;

import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.client.OrderClient;

public class GetOrderListTest {

    // Создаем объект клиента
    private OrderClient orderClient = new OrderClient();

    @Before
    public void setUp() {
        orderClient.setUp();
    }

    // Успешное получение списка заказов
    @Test
    @Description("Successful creation of a new order")
    public void createNewOrderAndCheckResponseForSuccess() {
        // Отправка GET запроса
        Response response = orderClient.getList();
        // Сравнение кода
        orderClient.verifyStatusCode(response, 200);
        // Сравнение результата в теле ответа
        orderClient.verifySuccessListOrdersNotEmpty(response);
    }
}
