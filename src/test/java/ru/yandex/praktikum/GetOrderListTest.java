package ru.yandex.praktikum;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.data.NewOrderData;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class GetOrderListTest {

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    // Успешное получение списка заказов
    @Test
    @Description("Successful creation of a new order")
    public void createNewOrderAndCheckResponseForSuccess() {
        // Отправка GET запроса
        Response response = sendGETRequestGetOrderList();
        // Сравнение кода
        compareStatusCode(response, 200);
        // Сравнение результата в теле ответа
        compareSuccessResult(response);
    }

    @Step("Send POST request to /api/v1/orders")
    public Response sendGETRequestGetOrderList(){
        Response response =
                given()
                        .get("/api/v1/orders");
        return response;
    }

    @Step("Compare status code")
    public void compareStatusCode(Response response, int expectedCode){
        response.then().statusCode(expectedCode);
    }

    @Step("Make sure response contains data")
    public void compareSuccessResult(Response response) {
        response.then().assertThat().body("orders", hasSize(greaterThan(0)));
    }
}
