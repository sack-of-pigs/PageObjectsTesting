package ru.netology.web.page;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class DashboardPage {
    private SelenideElement heading = $("[data-test-id=dashboard]");
    private ElementsCollection cards = $$(".list__item div");
    private final String balanceStart = "баланс: ";
    private final String balanceFinish = " р.";
    private SelenideElement transferAmountInput = $("[data-test-id=amount] input");
    private SelenideElement transferFromInput = $("[data-test-id=from] input");
    private SelenideElement transferButton = $("[data-test-id=action-transfer]");
    private SelenideElement errorMessage = $("[data-test-id=error-notification]");

    public DashboardPage() {
        heading.shouldBe(visible);
    }

    public int getCardBalance(String id) {
        var text = cards.find(Condition.attribute("data-test-id",id)).text();
        return extractBalance(text);
    }

    private int extractBalance(String text) {
        var start = text.indexOf(balanceStart);
        var finish = text.indexOf(balanceFinish);
        var value = text.substring(start + balanceStart.length(), finish);
        return Integer.parseInt(value);
    }

    public void transferMoneyTo(String id, String cardNumberFrom, int moneyAmount) {
        cards.find(Condition.attribute("data-test-id",id)).$("button").click();
        transferAmountInput.setValue(String.valueOf(moneyAmount));
        transferFromInput.setValue(cardNumberFrom);
        transferButton.click();
    }

    public void getErrorMessage() {
        errorMessage.should(visible).$(".notification__content").should(Condition.text("Ошибка! "));
    }
}