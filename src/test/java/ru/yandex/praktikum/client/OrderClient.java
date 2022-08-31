package ru.yandex.praktikum.client;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.yandex.praktikum.data.NewOrderData;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static ru.yandex.praktikum.constant.EndpointConstant.ORDERS_URI;

public class OrderClient extends RestClient {

    // Шаг отправки POST-запроса на создание нового заказа
    @Step("Send POST request to /api/v1/orders (create an order)")
    public Response create(NewOrderData newOrderData){
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(newOrderData)
                .when()
                .post(ORDERS_URI);
    }

    // Шаг отправки GET-запроса на получение списка заказов
    @Step("Send POST request to /api/v1/orders (get the list of orders)")
    public Response getList(){
        return given().get(ORDERS_URI);
    }

    @Step("Verify success result (track)")
    public void verifySuccessCreateTrackNotNull(Response response) {
        response.then().assertThat().body("track", notNullValue());
    }

    @Step("Verify success result (orders)")
    public void verifySuccessListOrdersNotEmpty(Response response) {
        response.then().assertThat().body("orders", hasSize(greaterThan(0)));
    }
}
