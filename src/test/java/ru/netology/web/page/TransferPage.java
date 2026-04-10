package ru.netology.web.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class TransferPage {
    private SelenideElement heading = $("h1");
    private SelenideElement transferAmountInput = $("[data-test-id=amount] input");
    private SelenideElement transferFromInput = $("[data-test-id=from] input");
    private SelenideElement transferButton = $("[data-test-id=action-transfer]");
    private SelenideElement errorMessage = $("[data-test-id='error-message']");

    public TransferPage() {
        heading.should(Condition.text("Пополнение карты")).should(Condition.visible);
    }

    public void getErrorMessage() {
        errorMessage.should(Condition.text("Выполнена попытка перевода суммы, превышающей остаток на карте списания")).should(Condition.visible);
    }

    public void doTransfer(String amount, String cardNumber) {
        transferAmountInput.setValue(amount);
        transferFromInput.setValue(cardNumber);
        transferButton.click();
    }

    public DashboardPage doValidTransfer(String amount, String cardNumber) {
        doTransfer(amount, cardNumber);
        return new DashboardPage();
    }

    public void doInvalidTransfer(String amount, String cardNumber) {
        doTransfer(amount, cardNumber);
        getErrorMessage();
    }
}
