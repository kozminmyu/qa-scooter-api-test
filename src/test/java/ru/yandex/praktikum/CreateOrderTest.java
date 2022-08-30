package ru.yandex.praktikum;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.yandex.praktikum.data.NewOrderData;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;
import static ru.yandex.praktikum.constant.EndpointConstant.*;

// Тесты для метода создания нового заказа
@RunWith(Parameterized.class)
public class CreateOrderTest {

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
        RestAssured.baseURI = BASE_URI;
    }

    // Успешное создание нового заказа (разных цветов)
    @Test
    @Description("Successful creation of a new order")
    public void createNewOrderAndCheckResponseForSuccess() {
        NewOrderData newOrderData = new NewOrderData("Ivan", "Ivanov", "Moscow, Kremlin, 1", "4", "+7 800 355 35 35", 5, "2022-08-27", "gsdgkdkgsdk", testColor);
        // Отправка POST запроса
        Response response = sendPOSTRequestCreateOrder(newOrderData);
        // Сравнение кода
        compareStatusCode(response, 201);
        // Сравнение результата в теле ответа
        compareSuccessResult(response);
    }

    @Step("Send POST request to /api/v1/orders")
    public Response sendPOSTRequestCreateOrder(NewOrderData newOrderData){
        return given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(newOrderData)
                        .when()
                        .post(ORDERS_URI);
    }

    @Step("Compare status code")
    public void compareStatusCode(Response response, int expectedCode){
        response.then().statusCode(expectedCode);
    }

    @Step("Compare success result")
    public void compareSuccessResult(Response response) {
        response.then().assertThat().body("track", notNullValue());
    }
}
