package com.api_rest_vm.service;

import org.springframework.stereotype.Service;

@Service
public class EmailService {

    public void enviarEmail(String destinatario, String assunto, String corpo) {
        System.out.println("----------------------------------------");
        System.out.println("Simulando envio de e-mail:");
        System.out.println("Destinatário: " + destinatario);
        System.out.println("Assunto: " + assunto);
        System.out.println("Corpo: " + corpo);
        System.out.println("----------------------------------------");
    }
}
