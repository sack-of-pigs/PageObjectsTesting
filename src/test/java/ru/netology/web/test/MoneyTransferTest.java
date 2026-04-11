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
    public LoginPage loginPage;
    public DataHelper.AuthInfo authInfo;
    public VerificationPage verificationPage;
    public DataHelper.VerificationCode verificationCode;
    public DashboardPage dashboardPage;
    public DataHelper.Card firstCard;
    public DataHelper.Card secondCard;
    public int firstCardBalance;
    public int secondCardBalance;

    @BeforeEach
    void setup() {
        open("http://localhost:9999");
    }

    @Test
    void shouldTransferMoneyBetweenOwnCards() {
        loginPage = new LoginPage();
        authInfo = DataHelper.getAuthInfo();
        verificationPage = loginPage.validLogin(authInfo);
        verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        dashboardPage = verificationPage.validVerify(verificationCode);
        firstCard = DataHelper.getFirstCardInfo();
        secondCard = DataHelper.getSecondCardInfo();
        firstCardBalance = dashboardPage.getCardBalance(firstCard.getTestId());
        secondCardBalance = dashboardPage.getCardBalance(secondCard.getTestId());
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
        loginPage = new LoginPage();
        authInfo = DataHelper.getAuthInfo();
        verificationPage = loginPage.validLogin(authInfo);
        verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        dashboardPage = verificationPage.validVerify(verificationCode);
        firstCard = DataHelper.getFirstCardInfo();
        secondCard = DataHelper.getSecondCardInfo();
        firstCardBalance = dashboardPage.getCardBalance(firstCard.getTestId());
        secondCardBalance = dashboardPage.getCardBalance(secondCard.getTestId());
        var amount = secondCardBalance+100; //делаем сумму больше, чем есть на балансе
        var transferPage = dashboardPage.pressTransferButton(firstCard.getTestId());
        transferPage.doInvalidTransfer(String.valueOf(amount),secondCard.getCardNumber()); // На первую карту переводим сумму превышающую баланс второй карты
    }
}
