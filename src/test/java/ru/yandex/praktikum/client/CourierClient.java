package ru.yandex.praktikum.client;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.yandex.praktikum.data.DeleteCourierData;
import ru.yandex.praktikum.data.LoginCourierData;
import ru.yandex.praktikum.data.NewCourierData;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static ru.yandex.praktikum.constant.EndpointConstant.*;

public class CourierClient extends RestClient {

    // Шаг отправки POST-запроса на создание курьера
    @Step("Send POST request to /api/v1/courier (create a courier)")
    public Response create(NewCourierData newCourierData){
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(newCourierData)
                .when()
                .post(COURIER_URI);
    }

    // Шаг отправки POST-запроса на авторизацию курьера
    @Step("Send POST request to /api/v1/courier/login (login with a courier)")
    public Response login(LoginCourierData loginCourierData){
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(loginCourierData)
                .when()
                .post(LOGIN_URI);
    }

    // Шаг отправки DELETE-запроса на удаление курьера
    @Step("Send DELETE request to /api/v1/courier (delete a courier)")
    public Response delete(DeleteCourierData deleteCourierData) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(deleteCourierData)
                .when()
                .delete(COURIER_URI + deleteCourierData.getId());
    }

    @Step("Verify success result (ok)")
    public void verifySuccessCreateIsOkTrue(Response response){
        response.then().assertThat().body("ok", equalTo(true));
    }

    @Step("Verify success result (id)")
    public void verifySuccessLoginIdNotNull(Response response){
        response.then().assertThat().body("id", notNullValue());
    }

    @Step("Verify failure result (code)")
    public void verifyFailureCode(Response response, int expectedResult){
        response.then().assertThat().body("code", equalTo(expectedResult));
    }

    @Step("Verify failure result (message)")
    public void verifyFailureMessage(Response response, String expectedResult){
        response.then().assertThat().body("message", equalTo(expectedResult));
    }

    public int getCourierID(Response response) {
        return response
                .then()
                .extract()
                .body()
                .path("id");
    }
}
