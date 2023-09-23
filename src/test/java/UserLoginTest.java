import org.example.user.User;
import org.example.user.UserAPI;
import org.example.user.GenerationUser;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.Matchers.equalTo;

public class UserLoginTest {
    private final UserAPI userApi = new UserAPI();
    private Response response;
    private User user;
    private String accessToken;

    @Before
    public void setUp() {
        user = GenerationUser.createUserWithRandomData();
        response = userApi.userCreate(user);
        accessToken = response
                .then().extract().body().path("accessToken");
    }
    @Test
    @DisplayName("Валидная авторизация пользователя")
    @Description("Код 200. Пользователь авторизован.")
    public void authorizWithUser() {
        response = userApi.userLoginToken(user, accessToken);
        response
                .then().statusCode(200)
                .and()
                .body("success", equalTo(true));
    }
    @Test
    @DisplayName("Не валидная авторизация")
    @Description("Код 401. Ошибка пароля или почты.")
    public void authorizUserWithWrongPasswordAndEmail() {
        String email = user.getEmail();
        user.setEmail("wrong@email.ru");
        String password = user.getPassword();
        user.setPassword("wrongPassword");
        response = userApi.userLoginToken(user, accessToken);
        user.setEmail(email);
        user.setPassword(password);
        response.then()
                .statusCode(401)
                .and()
                .body("success", equalTo(false));
    }
    @After
    public void cleanUp() {
        if (accessToken != null) {
            userApi.userDelete(accessToken);
        }
    }
}