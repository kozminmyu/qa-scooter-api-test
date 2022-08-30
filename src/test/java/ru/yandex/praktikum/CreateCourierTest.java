package ru.yandex.praktikum;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.data.DeleteCourierData;
import ru.yandex.praktikum.data.LoginCourierData;
import ru.yandex.praktikum.data.NewCourierData;


import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static ru.yandex.praktikum.constant.EndpointConstant.*;

// Тесты для метода создания нового курьера
public class CreateCourierTest {

    // Признак необходимости удаления куреьера при пост-обработке
    private boolean shouldDeleteCourier;

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URI;
    }

    // Успешное создание нового курьера
    @Test
    @DisplayName("Successful creation of a new courier")
    public void createNewCourierAndCheckResponseForSuccess() {
        shouldDeleteCourier = true;

        NewCourierData newCourierData = new NewCourierData("newTestCourier", "1234", "Ivan");
        // Отправка POST запроса
        Response response = sendPOSTRequestCreateCourier(newCourierData);
        // Сравнение кода
        compareStatusCode(response, 201);
        // Сравнение результата в теле ответа
        compareSuccessResult(response, true);
    }

    // Ошибка создания двух одинаковых курьеров
    @Test
    @DisplayName("Failed creation of the same courier twice")
        public void createNewCourierWithSameLoginAndCheckResponseForError() {
        shouldDeleteCourier = true;

        NewCourierData firstCourierData = new NewCourierData("newTestCourier", "1234", "Ivan");
        NewCourierData secondCourierData = new NewCourierData("newTestCourier", "1234", "Petr");
        // Отправка POST запросов
        sendPOSTRequestCreateCourier(firstCourierData);
        Response secondResponse = sendPOSTRequestCreateCourier(secondCourierData);
        // Сравнение кода
        compareStatusCode(secondResponse, 409);
        // Сравнение результата в теле ответа
        compareFailureResultCode(secondResponse, 409);
        compareFailureResultMessage(secondResponse, "Этот логин уже используется. Попробуйте другой.");
    }

    // Ошибка создания нового курьера без заведения логина
    @Test
    @DisplayName("Failed creation of a new courier without login")
    public void createNewCourierWithNoLoginAndCheckResponseForError() {
        shouldDeleteCourier = false;

        NewCourierData newCourierData = new NewCourierData("", "1234", "Ivan");
        // Отправка POST запроса
        Response response = sendPOSTRequestCreateCourier(newCourierData);
        // Сравнение кода
        compareStatusCode(response, 400);
        // Сравнение результата в теле ответа
        compareFailureResultCode(response, 400);
        compareFailureResultMessage(response, "Недостаточно данных для создания учетной записи");
    }

    // Ошибка создания нового курьера без заведения логина
    @Test
    @DisplayName("Failed creation of a new courier without password")
    public void createNewCourierWithNoPasswordAndCheckResponseForError() {
        shouldDeleteCourier = false;

        NewCourierData newCourierData = new NewCourierData("newTestCourier", "", "Ivan");
        // Отправка POST запроса
        Response response = sendPOSTRequestCreateCourier(newCourierData);
        // Сравнение кода
        compareStatusCode(response, 400);
        // Сравнение результата в теле ответа
        compareFailureResultCode(response, 400);
        compareFailureResultMessage(response, "Недостаточно данных для создания учетной записи");
    }

    // Пост-обработка
    @After
    public void postProcessing() {
        if (shouldDeleteCourier) {
            LoginCourierData loginCourierData = new LoginCourierData("newTestCourier", "1234");

            // Пост-обработка: получение id созданного курьера
            int id = getNewCourierID(loginCourierData);

            DeleteCourierData deleteCourierData = new DeleteCourierData(id);

            // Пост-обработка: удаление созданного курьера
            deleteNewCourier(deleteCourierData, id);
        }
    }


    @Step("Send POST request to /api/v1/courier")
    public Response sendPOSTRequestCreateCourier(NewCourierData newCourierData){
        return given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(newCourierData)
                        .when()
                        .post(COURIER_URI);
    }

    @Step("Compare status code")
    public void compareStatusCode(Response response, int expectedCode){
        response.then().statusCode(expectedCode);
    }

    @Step("Compare success result (ok)")
    public void compareSuccessResult(Response response, boolean expectedResult){
        response.then().assertThat().body("ok", equalTo(expectedResult));
    }

    @Step("Compare failure result (code)")
    public void compareFailureResultCode(Response response, int expectedResult){
        response.then().assertThat().body("code", equalTo(expectedResult));
    }

    @Step("Compare failure result (message)")
    public void compareFailureResultMessage(Response response, String expectedResult){
        response.then().assertThat().body("message", equalTo(expectedResult));
    }

    @Step("Post-processing: get new courier's ID")
    public int getNewCourierID(LoginCourierData loginCourierData) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(loginCourierData)
                .when()
                .post(LOGIN_URI)
                .then()
                .extract()
                .body()
                .path("id");
    }

    @Step("Post-processing: delete  new  courier")
    public void deleteNewCourier(DeleteCourierData deleteCourierData, int id) {
        given()
                .header("Content-type", "application/json")
                .and()
                .body(deleteCourierData)
                .when()
                .delete(COURIER_URI + id);
    }
}
