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

// Тесты для метода создания нового курьера
public class CreateCourierTest {
    private String testCourierLogin = "newTestCourier";
    private String testCourierPassword = "1234";

    // Признак необходимости удаления куреьера при пост-обработке
    private boolean shouldDeleteCourier;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    // Успешное создание нового курьера
    @Test
    @DisplayName("Successful creation of a new courier")
    public void createNewCourierAndCheckResponseForSuccess() {
        shouldDeleteCourier = true;

        NewCourierData newCourierData = new NewCourierData(testCourierLogin, testCourierPassword, "Ivan");
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

        NewCourierData firstCourierData = new NewCourierData(testCourierLogin, testCourierPassword, "Ivan");
        NewCourierData secondCourierData = new NewCourierData(testCourierLogin, testCourierPassword, "Petr");
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

        NewCourierData newCourierData = new NewCourierData("", testCourierPassword, "Ivan");
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

        NewCourierData newCourierData = new NewCourierData(testCourierLogin, "", "Ivan");
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
            LoginCourierData loginCourierData = new LoginCourierData(testCourierLogin, testCourierPassword);

            // Пост-обработка: получение id созданного курьера
            int id = getNewCourierID(loginCourierData);

            DeleteCourierData deleteCourierData = new DeleteCourierData(id);

            // Пост-обработка: удаление созданного курьера
            deleteNewCourier(deleteCourierData, id);
        }
    }


    @Step("Send POST request to /api/v1/courier")
    public Response sendPOSTRequestCreateCourier(NewCourierData newCourierData){
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(newCourierData)
                        .when()
                        .post("/api/v1/courier");
        return response;
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
        int id = given()
                .header("Content-type", "application/json")
                .and()
                .body(loginCourierData)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .extract()
                .body()
                .path("id");
        return id;
    }

    @Step("Post-processing: delete  new  courier")
    public void deleteNewCourier(DeleteCourierData deleteCourierData, int id) {
        given()
                .header("Content-type", "application/json")
                .and()
                .body(deleteCourierData)
                .when()
                .delete("/api/v1/courier/" + id);
    }
}
