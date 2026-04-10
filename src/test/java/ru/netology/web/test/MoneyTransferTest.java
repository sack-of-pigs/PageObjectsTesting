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
        var cardsInfo = DataHelper.getCardsInfo();
        var firstCardBalance = dashboardPage.getCardBalance(cardsInfo.getFirstCard().getTestId());
        var secondCardBalance = dashboardPage.getCardBalance(cardsInfo.getSecondCard().getTestId());
        var amount = (int) Math.round(secondCardBalance*0.1);
        dashboardPage.transferMoneyTo(cardsInfo.getFirstCard().getTestId(),
                cardsInfo.getSecondCard().getCardNumber(),
                amount); // На первую карту переводим 10% от баланса на второй карте
        var firstCardBalanceAfterTransfer = dashboardPage.getCardBalance(cardsInfo.getFirstCard().getTestId());
        var secondCardBalanceAfterTransfer = dashboardPage.getCardBalance(cardsInfo.getSecondCard().getTestId());
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
        var cardsInfo = DataHelper.getCardsInfo();
        var firstCardBalance = dashboardPage.getCardBalance(cardsInfo.getFirstCard().getTestId());
        var secondCardBalance = dashboardPage.getCardBalance(cardsInfo.getSecondCard().getTestId());
        var amount = secondCardBalance+100; //делаем сумму больше, чем есть на балансе
        dashboardPage.transferMoneyTo(cardsInfo.getFirstCard().getTestId(),
                cardsInfo.getSecondCard().getCardNumber(),
                amount); // На первую карту переводим сумму превышающую баланс второй карты
        dashboardPage.getErrorMessage(); //ловим сообщение об ошибке
        var firstCardBalanceAfterTransfer = dashboardPage.getCardBalance(cardsInfo.getFirstCard().getTestId());
        var secondCardBalanceAfterTransfer = dashboardPage.getCardBalance(cardsInfo.getSecondCard().getTestId());
        //балансы на картах не должны измениться
        assertEquals(firstCardBalance, firstCardBalanceAfterTransfer);
        assertEquals(secondCardBalance, secondCardBalanceAfterTransfer);
    }
}