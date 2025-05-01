package ru.netology.test;


import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import ru.netology.data.DataGenerator;

import java.time.Duration;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class DataTest {

     @BeforeEach
     void setup() {
         open("http://localhost:9999");
     }

    DataGenerator.UserInfo user = DataGenerator.Registration.generateUser("ru");
    String firstDate = DataGenerator.generateDate(3);
    String secondDate = DataGenerator.generateDate(5);

    @BeforeAll
    static void setUpAll () {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll () {
        SelenideLogger.removeListener("allure");
    }

     @Test
     void shouldRegisterByAccountNumber() {
         $("[data-test-id='city'] input").setValue(user.getCity());
         $("[data-test-id='date'] input")
                 .sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
         $("[data-test-id='date'] input").setValue(firstDate);
         $("[data-test-id='name'] input").setValue(user.getName());
         $("[data-test-id='phone'] input").setValue(user.getPhone());
         $("[data-test-id='agreement']").click();
         $("button.button").click();

         // Проверка успешного уведомления
         $("[data-test-id='success-notification']")
                 .shouldBe(visible, Duration.ofSeconds(5))
                 .shouldHave(text("Встреча успешно запланирована на " + firstDate));

         // Повторная отправка с новой датой
         $("[data-test-id='date'] input")
                 .sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
         $("[data-test-id='date'] input").setValue(secondDate);
         $("button.button").click();

         // Проверка появления окна с предложением перепланировать
         $("[data-test-id='replan-notification']")
                 .shouldBe(visible, Duration.ofSeconds(5))
                 .shouldHave(text("У вас уже запланирована встреча на другую дату. Перепланировать?"));

         // Нажимаем кнопку «Перепланировать»
         $("[data-test-id='replan-notification'] button.button").click();

         // Проверка успешного перепланирования
         $("[data-test-id='success-notification']")
                 .shouldBe(visible, Duration.ofSeconds(5))
                 .shouldHave(text("Встреча успешно запланирована на " + secondDate));
     }



}
