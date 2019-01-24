package com.example.demo;

import org.junit.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class EmailValidatorTest {

    private EmailValidator emailValidator;

    @BeforeClass
    public void initData() {
        emailValidator = new EmailValidator();
    }

    @DataProvider
    public Object[][] ValidEmailProvider() {
        return new Object[][] {
                {
                        new String[] {
                                "alex@yandex.ru",
                                "alex-27@yandex.com",
                                "alex.27@yandex.com",
                                "alex111@devcolibri.com",
                                "alex.100@devcolibri.com.ua",
                                "alex@1.com",
                                "alex@gmail.com.com",
                                "alex+27@gmail.com",
                                "alex-27@yandex-test.com"
                        }
                }
        };
    }

    @DataProvider
    public Object[][] InvalidEmailProvider() {
        return new Object[][] {
                {
                        new String[] {
                                "devcolibri",
                                "alex@.com.ua",
                                "alex123@gmail.a",
                                "alex123@.com",
                                "alex123@.com.com",
                                ".alex@devcolibri.com",
                                "alex()*@gmail.com",
                                "alex@%*.com",
                                "alex..2013@gmail.com",
                                "alex.@gmail.com",
                                "alex@devcolibri@gmail.com",
                                "alex@gmail.com.1ua"
                        }
                }
        };
    }

    @Test(dataProvider = "ValidEmailProvider")
    public void ValidEmailTest(String[] Email) {

        for (String temp : Email) {
            boolean valid = emailValidator.validate(temp);
            System.out.println("Email: " + temp + " -> " + valid);
            Assert.assertEquals(valid, true);
        }

    }

    @Test(dataProvider = "InvalidEmailProvider", dependsOnMethods = "ValidEmailTest")
    public void InValidEmailTest(String[] Email) {

        for (String temp : Email) {
            boolean valid = emailValidator.validate(temp);
            System.out.println("Email: " + temp + " -> " + valid);
            Assert.assertEquals(valid, false);
        }
    }

}