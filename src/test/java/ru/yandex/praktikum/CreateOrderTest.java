package ru.yandex.praktikum;

import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.yandex.praktikum.client.OrderClient;
import ru.yandex.praktikum.data.NewOrderData;

// Тесты для метода создания нового заказа
@RunWith(Parameterized.class)
public class CreateOrderTest {

    // Создаем объект клиента
    private OrderClient orderClient = new OrderClient();

    // Параметр для параметризации
    private String[] testColor;

    public CreateOrderTest(String[] testColor) {
        this.testColor = testColor;
    }

    // Параметризация параметра color
    @Parameterized.Parameters
    public static Object[][] getColorData() {
        return new Object[][]{
                {new String[]{"BLACK"}},
                {new String[]{"GRAY"}},
                {new String[]{"BLACK", "GRAY"}},
                {new String[]{}}
        };
    }

    @Before
    public void setUp() {
        orderClient.setUp();
    }

    // Успешное создание нового заказа (разных цветов)
    @Test
    @Description("Successful creation of a new order")
    public void createNewOrderAndCheckResponseForSuccess() {
        NewOrderData newOrderData = new NewOrderData("Ivan", "Ivanov", "Moscow, Kremlin, 1", "4", "+7 800 355 35 35", 5, "2022-08-27", "gsdgkdkgsdk", testColor);
        // Отправка POST запроса
        Response response = orderClient.create(newOrderData);
        // Сравнение кода
        orderClient.verifyStatusCode(response, 201);
        // Сравнение результата в теле ответа
        orderClient.verifySuccessCreateTrackNotNull(response);
    }
}
