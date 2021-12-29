package com.ig.cursomc.services;

import com.ig.cursomc.domain.Cliente;
import com.ig.cursomc.domain.Pedido;


import org.springframework.mail.SimpleMailMessage;


public interface EmailService {

    void sendOrderConfirmationEmail(Pedido obj);

    void sendEmail(SimpleMailMessage msg);

    void sendNewPasswordEmail(Cliente cliente, String newPass);
}
