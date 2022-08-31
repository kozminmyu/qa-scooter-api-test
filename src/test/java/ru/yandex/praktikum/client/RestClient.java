package ru.yandex.praktikum.client;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import static ru.yandex.praktikum.constant.EndpointConstant.BASE_URI;

public class RestClient {

    public void setUp() {
        RestAssured.baseURI = BASE_URI;
    }

    @Step("Verify status code")
    public void verifyStatusCode(Response response, int expectedCode){
        response.then().statusCode(expectedCode);
    }
}
