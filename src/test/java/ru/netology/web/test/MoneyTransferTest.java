package ru.netology.web.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.web.data.DataHelper;
import ru.netology.web.page.DashboardPage;
import ru.netology.web.page.LoginPage;
import ru.netology.web.page.VerificationPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MoneyTransferTest {
    public LoginPage loginPage = new LoginPage();
    public DataHelper.AuthInfo authInfo = DataHelper.getAuthInfo();
    public VerificationPage verificationPage = loginPage.validLogin(authInfo);
    public DataHelper.VerificationCode verificationCode = DataHelper.getVerificationCodeFor(authInfo);
    public DashboardPage dashboardPage = verificationPage.validVerify(verificationCode);
    public DataHelper.Card firstCard = DataHelper.getFirstCardInfo();
    public DataHelper.Card secondCard = DataHelper.getSecondCardInfo();
    public int firstCardBalance = dashboardPage.getCardBalance(firstCard.getTestId());
    public int secondCardBalance = dashboardPage.getCardBalance(secondCard.getTestId());

    @BeforeEach
    void setup() {
        open("http://localhost:9999");
    }

    @Test
    void shouldTransferMoneyBetweenOwnCards() {
        var amount = (int) Math.round(secondCardBalance*0.1); //Берем 10% от изначальной суммы баланса
        var transferPage = dashboardPage.pressTransferButton(firstCard.getTestId());
        dashboardPage = transferPage.doValidTransfer(String.valueOf(amount),secondCard.getCardNumber()); // На первую карту переводим 10% от баланса на второй карте
        var firstCardBalanceAfterTransfer = dashboardPage.getCardBalance(firstCard.getTestId());
        var secondCardBalanceAfterTransfer = dashboardPage.getCardBalance(secondCard.getTestId());
        assertEquals(firstCardBalance + amount, firstCardBalanceAfterTransfer);
        assertEquals(secondCardBalance - amount, secondCardBalanceAfterTransfer);
    }

    @Test
    void shouldNotTransferWhenAmountExceedsBalance() {
        var amount = secondCardBalance+100; //делаем сумму больше, чем есть на балансе
        var transferPage = dashboardPage.pressTransferButton(firstCard.getTestId());
        transferPage.doInvalidTransfer(String.valueOf(amount),secondCard.getCardNumber()); // На первую карту переводим сумму превышающую баланс второй карты
    }
}