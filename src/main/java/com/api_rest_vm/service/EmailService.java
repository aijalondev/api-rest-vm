package com.api_rest_vm.service;

import org.springframework.stereotype.Service;

@Service
public class EmailService {

    public void sendEmail(String recipient, String subject, String body) {
        System.out.println("----------------------------------------");
        System.out.println("Simulando envio de e-mail:");
        System.out.println("Destinatário: " + recipient);
        System.out.println("Assunto: " + subject);
        System.out.println("Corpo: " + body);
        System.out.println("----------------------------------------");
    }
}
