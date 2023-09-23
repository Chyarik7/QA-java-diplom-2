import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.example.user.User;
import org.example.user.UserAPI;
import org.example.user.GenerationUser;
import static org.example.user.GenerationUser.faker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.Matchers.equalTo;

public class ChangeUserInfoTest {
    private final UserAPI userSteps = new UserAPI();
    private Response response;
    private User user;
    private String accessToken;
    @Before
    public void setUp() {
        user = GenerationUser.createUserWithRandomData();
        response = userSteps.userCreate(user);
        accessToken = response
                .then().extract().body().path("accessToken");
    }
    @Test
    @DisplayName("Изменение данных пользователя")
    @Description("Код 200. Данные имени/почты изменены.")
    public void changeAuthorizedUpdateUser() {
        user.setName(faker.name().firstName());
        user.setEmail(faker.internet().emailAddress());
        response = userSteps.userDataAccountChanging(user, accessToken);
        response.then()
                .statusCode(200)
                .and()
                .body("success", equalTo(true));
    }
    @Test
    @DisplayName("Изменение пароля")
    @Description("Код 200. Пароль изменён.")
    public void changeAuthorizedUserPasswor() {
        user.setPassword(faker.internet().password());
        response = userSteps.userDataAccountChanging(user, accessToken);
        response.then()
                .statusCode(200)
                .and()
                .body("success", equalTo(true));
    }
    @Test
    @DisplayName("Изменение пароля у не авторизованного пользователя")
    @Description("Код 401, данные не изменены")
    public void changeUserDataPasswordWithoutAuthoriz() {
        user.setPassword(faker.internet().password());
        response = userSteps.userDataAccountChanging(user, "");
        response.then()
                .statusCode(401)
                .and()
                .body("success", equalTo(false));
    }
    @Test
    @DisplayName("Изменение данных у не авторизованного пользователя")
    @Description("Код 401, данные не изменены")
    public void changeUnauthorizedUser() {
        user.setName(faker.name().firstName());
        user.setEmail(faker.internet().emailAddress());
        response = userSteps.userDataAccountChanging(user, "");
        response.then()
                .statusCode(401)
                .and()
                .body("success", equalTo(false));
    }
    @After
    public void cleanUp() {
        if (accessToken != null) {
            userSteps.userDelete(accessToken);
        }
    }
}