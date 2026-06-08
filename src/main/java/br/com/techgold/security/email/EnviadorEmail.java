package br.com.techgold.security.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;


import jakarta.mail.internet.MimeMessage;

@Component
public class EnviadorEmail {
	
		@Value("${spring.mail.username}")
		private String senderEmail;
		
		@Value("${sistech.email.copia}")
		private String copiaEmail;
	
		@Autowired
	    private JavaMailSender emailSender;
		
		
		@Async
	    public void enviar2fa(String email, String assunto, String mensagem) {
	        try {
	            
	            MimeMessage message = emailSender.createMimeMessage();
	            message.setSubject(assunto);
	            MimeMessageHelper helper;
	            message.setContent(mensagem, "text/html; charset=utf-8");
	            helper = new MimeMessageHelper(message, true);
	            helper.setFrom(senderEmail);
	            helper.setTo(email);
	            helper.setText(mensagem, true);
	            emailSender.send(message);

	            //Simulando demora de 3 segundos para enviar email
	            //Thread.sleep(3000);

	        } catch (Exception e) {
	            throw new RuntimeException("Erro ao enviar email!", e);
	        }
	    }


		public void enviarEmail(String assunto, String destinatario, String texto) {
			
			try {
				String corpoEmail = "<h4 style='color: red'><b>"+texto+"</b></h3>"; 
				
				MimeMessage message = emailSender.createMimeMessage();
	            message.setSubject(assunto);
	            MimeMessageHelper helper;
	            helper = new MimeMessageHelper(message, true);
	            helper.setFrom(senderEmail);
	            helper.setTo(destinatario);
	            helper.setText(corpoEmail,true);
	            emailSender.send(message);
			 }catch (Exception e) {
		            throw new RuntimeException("Erro ao enviar email!", e);
		     }
		}
		

}
