package com.api.sic.backend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.api.sic.backend.core.ConfigEnvs;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    private String emailFrom = "fellipe.patrick678@gmail.com";

    public void enviarEmailSimples(String para, String matricula, String senha) {
        try {
            MimeMessage mensagem = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensagem, true, "UTF-8");

            String conteudoHtml = String.format("""
                <html>
                    <body style="font-family: Arial, sans-serif; color: #333;">
                        <h2 style="color: #2E8B57;">Olá, seja bem-vindo ao <span style="color: #1E90FF;">SIC-Zoonoses</span>!</h2>
                        
                        <p>Estamos felizes em tê-lo(a) conosco.</p>
                        
                        <p>Segue abaixo suas credenciais de acesso ao sistema:</p>
                        
                        <ul style="line-height: 1.6;">
                            <li><em><strong>Matrícula:</strong> %s</em></li>
                            <li><em><strong>Senha provisória:</strong> %s</em></li>
                        </ul>

                        <p><em style="color: red;">⚠️ Recomendamos que você altere sua senha no primeiro acesso, pois ela foi gerada automaticamente pelo sistema.</em></p>

                        <hr style="margin: 30px 0;"/>
                        
                        <p style="font-size: 12px; color: gray;"><em>❗ Esta é uma mensagem automática, por favor não responda.</em></p>
                    </body>
                </html>
                """, matricula, senha);

            helper.setTo(para);
            helper.setSubject("🎉 Boas-vindas ao SIC-Zoonoses!");
            helper.setText(conteudoHtml, true);
            helper.setFrom(emailFrom);

            mailSender.send(mensagem);
        } catch (MessagingException e) {
            e.printStackTrace(); 
        }
    }

    public void enviarRedefinirPassword(String para, String matricula, String senha) {
        try {
            MimeMessage mensagem = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensagem, true, "UTF-8");
    
            String conteudoHtml = String.format("""
                <html>
                    <body style="font-family: Arial, sans-serif; color: #333;">
                        <h2 style="color: #2E8B57;">Redefinição de senha - <span style="color: #1E90FF;">SIC-Zoonoses</span></h2>
                        
                        <p>Olá,</p>
                        
                        <p>Você solicitou a redefinição da sua senha de acesso ao sistema.</p>
    
                        <p><strong>Credenciais atualizadas:</strong></p>
                        
                        <ul style="line-height: 1.6;">
                            <li><em><strong>Matrícula:</strong> %s</em></li>
                            <li><em><strong>Nova senha provisória:</strong> %s</em></li>
                        </ul>
    
                        <p><em style="color: red;">⚠️ Por motivos de segurança, recomendamos que você altere sua senha assim que acessar o sistema.</em></p>
    
                        <hr style="margin: 30px 0;"/>
                        
                        <p style="font-size: 12px; color: gray;"><em>❗ Esta é uma mensagem automática. Não responda a este e-mail.</em></p>
                    </body>
                </html>
                """, matricula, senha);
    
            helper.setTo(para);
            helper.setSubject("🔐 Sua senha foi redefinida - SIC-Zoonoses");
            helper.setText(conteudoHtml, true);
            helper.setFrom(emailFrom);
    
            mailSender.send(mensagem);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void enviarLinkRedefinicaoSenha(String para, String token) {
    try {
        MimeMessage mensagem = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mensagem, true, "UTF-8");


        String linkRedefinicao = ConfigEnvs.pagina_web_senha + token;

        String conteudoHtml = String.format("""
            <html>
                <body style="font-family: Arial, sans-serif; color: #333;">
                    <h2 style="color: #2E8B57;">Redefinição de senha - <span style="color: #1E90FF;">SIC-Zoonoses</span></h2>

                    <p>Olá,</p>

                    <p>Recebemos uma solicitação para redefinir a sua senha de acesso ao sistema.</p>

                    <p>Para continuar, clique no botão abaixo ou acesse o link diretamente:</p>

                    <p style="margin: 20px 0;">
                        <a href="%s" style="padding: 10px 20px; background-color: #1E90FF; color: white; text-decoration: none; border-radius: 5px;">Redefinir senha</a>
                    </p>

                    <p>Ou copie e cole este link no seu navegador:</p>
                    <p><a href="%s">%s</a></p>

                    <p style="color: red;"><em>⚠️ Se você não solicitou essa mudança, ignore este e-mail.</em></p>

                    <hr style="margin: 30px 0;"/>

                    <p style="font-size: 12px; color: gray;"><em>❗ Esta é uma mensagem automática. Não responda a este e-mail.</em></p>
                </body>
            </html>
        """, linkRedefinicao, linkRedefinicao, linkRedefinicao);

        helper.setTo(para);
        helper.setSubject("🔐 Solicitação de redefinição de senha - SIC-Zoonoses");
        helper.setText(conteudoHtml, true);
        helper.setFrom(emailFrom);

        mailSender.send(mensagem);
    } catch (MessagingException e) {
        e.printStackTrace();
    }
}

    
}
