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

public class LoginCourierTest {
    private String testCourierLogin = "newTestCourier";
    private String testCourierPassword = "1234";
    private String incorrectTestCourierPassword = "4321";

    // Признак необходимости удаления куреьера при пост-обработке
    private boolean shouldDeleteCourier;

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URI;
    }

    // Успешная авторизация по логину и паролю
    @Test
    @DisplayName("Successful login")
    public void loginWithExistingLoginAndPasswordAndCheckResponseForSuccess() {
        shouldDeleteCourier = true;

        NewCourierData newCourierData = new NewCourierData("newTestCourier", "1234", "Ivan");
        LoginCourierData loginCourierData = new LoginCourierData("newTestCourier", "1234");
        // Создание нового курьера
        createNewCourier(newCourierData);
        // Отправка POST запроса
        Response response = sendPOSTRequestLoginCourier(loginCourierData);
        // Сравнение кода
        compareStatusCode(response, 200);
        // Сравнение результата в теле ответа
        compareSuccessResult(response);
    }

    // Ошибочная авторизация по несуществующему логину
    @Test
    @DisplayName("Login with non-existent login")
    public void loginWithNonexistentLoginAndCheckResponseForError() {
        shouldDeleteCourier = false;

        LoginCourierData loginCourierData = new LoginCourierData("newTestCourier", "1234");
        // Отправка POST запроса
        Response response = sendPOSTRequestLoginCourier(loginCourierData);
        // Сравнение кода
        compareStatusCode(response, 404);
        // Сравнение результата в теле ответа
        compareFailureResultCode(response,404);
        compareFailureResultMessage(response,"Учетная запись не найдена");
    }

    // Ошибка авторизации по некорректному паролю
    @Test
    @DisplayName("Login with incorrect password")
    public void loginWithWrongPasswordAndCheckResponseForError() {
        shouldDeleteCourier = true;

        NewCourierData newCourierData = new NewCourierData("newTestCourier", "1234", "Ivan");
        // Создание данных с неправильным паролем
        LoginCourierData loginCourierData = new LoginCourierData("newTestCourier", "4321");
        // Создание нового курьера
        createNewCourier(newCourierData);
        // Отправка POST запроса
        Response response = sendPOSTRequestLoginCourier(loginCourierData);
        // Сравнение кода
        compareStatusCode(response, 404);
        // Сравнение результата в теле ответа
        compareFailureResultCode(response,404);
        compareFailureResultMessage(response,"Учетная запись не найдена");
    }

    // Ошибочная авторизация по неуказанному логину
    @Test
    @DisplayName("Login with no login")
    public void loginWithNoLoginAndCheckResponseForError() {
        shouldDeleteCourier = false;

        LoginCourierData loginCourierData = new LoginCourierData("", "1234");
        // Отправка POST запроса
        Response response = sendPOSTRequestLoginCourier(loginCourierData);
        // Сравнение кода
        compareStatusCode(response, 400);
        // Сравнение результата в теле ответа
        compareFailureResultCode(response,400);
        compareFailureResultMessage(response,"Недостаточно данных для входа");
    }

    // Ошибочная авторизация по неуказанному паролю
    @Test
    @DisplayName("Login with no login")
    public void loginWithNoPasswordAndCheckResponseForError() {
        shouldDeleteCourier = false;

        LoginCourierData loginCourierData = new LoginCourierData("newTestCourier", "");
        // Отправка POST запроса
        Response response = sendPOSTRequestLoginCourier(loginCourierData);
        // Сравнение кода
        compareStatusCode(response, 400);
        // Сравнение результата в теле ответа
        compareFailureResultCode(response,400);
        compareFailureResultMessage(response,"Недостаточно данных для входа");
    }

    // Пост-обработка
    @After
    public void postProcessing() {
        if (shouldDeleteCourier) {
            LoginCourierData loginCourierData = new LoginCourierData("newTestCourier", "1234");

            // Пост-обработка: получение id созданного курьера
            Response response = sendPOSTRequestLoginCourier(loginCourierData);
            int id = getCourierID(response);

            DeleteCourierData deleteCourierData = new DeleteCourierData(id);

            // Пост-обработка: удаление созданного курьера
            deleteNewCourier(deleteCourierData, id);
        }
    }

    @Step("Send POST request to /api/v1/courier/login")
    public Response sendPOSTRequestLoginCourier(LoginCourierData loginCourierData){
        return given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(loginCourierData)
                        .when()
                        .post(LOGIN_URI);
    }

    @Step("Get courier ID")
    public int getCourierID(Response response) {
        return response
                .then()
                .extract()
                .body()
                .path("id");
    }

    @Step("Pre-processing: create  new  courier")
    public void createNewCourier(NewCourierData newCourierData) {
        given()
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

    @Step("Make sure id field contains data")
    public void compareSuccessResult(Response response){
        response.then().assertThat().body("id", notNullValue());
    }

    @Step("Compare failure result (code)")
    public void compareFailureResultCode(Response response, int expectedResult){
        response.then().assertThat().body("code", equalTo(expectedResult));
    }

    @Step("Compare failure result (message)")
    public void compareFailureResultMessage(Response response, String expectedResult){
        response.then().assertThat().body("message", equalTo(expectedResult));
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
