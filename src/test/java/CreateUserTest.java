import org.example.user.User;
import org.example.user.GenerationUser;
import org.example.user.UserAPI;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class CreateUserTest {
    private final UserAPI userSteps = new UserAPI();
    private Response response;
    private User user;
    private String accessToken;

    @After
    public void cleanUp() {
        if (accessToken != null) {
            userSteps.userDelete(accessToken);
        }
    }
    @Test
    @DisplayName("Создание пользователя с валидными данными")
    @Description("Код 200, пользователь создан, токен рабочий.")
    public void createUserValid() {
        user = GenerationUser.createUserWithRandomData();
        response = userSteps.userCreate(user);
        accessToken = response
                .then().extract().body().path("accessToken");
        response
                .then().statusCode(200)
                .and()
                .body("accessToken", notNullValue());
    }
    @Test
    @DisplayName("Регистрация пользователя с существующими данными")
    @Description("Код 403, пользователь уже зарегистрирован.")
    public void createUserWithRegBefore() {
        user = GenerationUser.createUserWithRandomData();
        response = userSteps.userCreate(user);
        accessToken = response
                .then().extract().body().path("accessToken");
        response.then()
                .statusCode(200);
        response = userSteps.userCreate(user);
        response.then()
                .statusCode(403)
                .and()
                .body("message", equalTo("User already exists"));
    }
    @Test
    @DisplayName("Создание пользователя без заполненного поля")
    @Description("Код 403. Не заполнено поле почты/имени/пароля.")
    public void createUserWithEmptyNameField() {
        user = GenerationUser.createUserWithEmptyData();
        response = userSteps.userCreate(user);
        accessToken = response
                .then().extract().body().path("accessToken");
        response.then()
                .statusCode(403)
                .and()
                .body("message", equalTo("Email, password and name are required fields"));
    }
}