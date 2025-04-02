package com.api_rest_vm.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
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
