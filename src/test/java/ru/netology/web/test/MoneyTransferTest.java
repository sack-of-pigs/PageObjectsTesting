package ru.netology.web.test;

import org.junit.jupiter.api.Test;
import ru.netology.web.data.DataHelper;
import ru.netology.web.page.LoginPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MoneyTransferTest {
    @Test
    void shouldTransferMoneyBetweenOwnCards() {
        open("http://localhost:9999");
        var loginPage = new LoginPage();
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        var dashboardPage = verificationPage.validVerify(verificationCode);
        var firstCard = DataHelper.getFirstCardInfo();
        var secondCard = DataHelper.getSecondCardInfo();
        var firstCardBalance = dashboardPage.getCardBalance(firstCard.getTestId());
        var secondCardBalance = dashboardPage.getCardBalance(secondCard.getTestId());
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
        open("http://localhost:9999");
        var loginPage = new LoginPage();
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        var dashboardPage = verificationPage.validVerify(verificationCode);
        var firstCard = DataHelper.getFirstCardInfo();
        var secondCard = DataHelper.getSecondCardInfo();
        var secondCardBalance = dashboardPage.getCardBalance(secondCard.getTestId());
        var amount = secondCardBalance+100; //делаем сумму больше, чем есть на балансе
        var transferPage = dashboardPage.pressTransferButton(firstCard.getTestId());
        transferPage.doInvalidTransfer(String.valueOf(amount),secondCard.getCardNumber()); // На первую карту переводим сумму превышающую баланс второй карты
    }
}