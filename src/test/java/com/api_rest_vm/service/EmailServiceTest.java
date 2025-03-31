package com.api_rest_vm.service;

import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.verify;

@SpringBootTest
class EmailServiceTest {

    @Spy
    private EmailService emailServiceSpy;

    // Testa se o método de envio de email está sendo chamado
    @Test
    void sendEmail_shouldSimulateSendingWithCorrectData() {
        String recipient = "email_mock@vm.com";
        String subject = "Subject Mock";
        String body = "Body Mock.";

        emailServiceSpy.sendEmail(recipient, subject, body);

        verify(emailServiceSpy).sendEmail(recipient, subject, body);
    }
}
