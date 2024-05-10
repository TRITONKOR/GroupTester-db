package com.tritonkor.domain.service.impl;

import com.tritonkor.domain.dto.UserStoreDto;
import com.tritonkor.domain.exception.EmailException;
import com.tritonkor.domain.exception.SignUpException;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
final class SignUpService {

    @Value("${mail.smtp.from}")
    private String EMAIL_FROM;
    private static final int VERIFICATION_CODE_EXPIRATION_MINUTES = 1;
    private static LocalDateTime codeCreationTime;
    private final UserService userService;
    private final Session session;
    final Logger LOGGER = LoggerFactory.getLogger(SignUpService.class);


    SignUpService(UserService userService, Session session) {
        this.userService = userService;
        this.session = session;
    }

    public void signUp(UserStoreDto userStoreDto, Supplier<String> waitForUserInput) {
        String verificationCode = generateAndSendVerificationCode(userStoreDto.email());
        String userInputCode = waitForUserInput.get();

        verifyCode(userInputCode, verificationCode);

        userService.create(userStoreDto);
    }

    // відправлення на пошту
    private void sendVerificationCodeEmail(String email, String verificationCode) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_FROM));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Код підтвердження");
            message.setText("Ваш код підтвердження: " + verificationCode);
            Transport.send(message);

            LOGGER.info("Повідомлення успішно відправлено.");
        } catch (MessagingException e) {
            throw new EmailException("Помилка при відправці електронного листа: " + e.getMessage());
        }
    }

    private String generateAndSendVerificationCode(String email) {
        // Генерація 6-значного коду
        String verificationCode = String.valueOf((int) (Math.random() * 900000 + 100000));

        sendVerificationCodeEmail(email, verificationCode);

        codeCreationTime = LocalDateTime.now();

        return verificationCode;
    }

    // Перевірка введеного коду
    private static void verifyCode(String inputCode, String generatedCode) {
        LocalDateTime currentTime = LocalDateTime.now();
        long minutesElapsed = ChronoUnit.MINUTES.between(codeCreationTime, currentTime);

        if (minutesElapsed > VERIFICATION_CODE_EXPIRATION_MINUTES) {
            throw new SignUpException("Час верифікації вийшов. Спробуйте ще раз.");
        }

        if (!inputCode.equals(generatedCode)) {
            throw new SignUpException("Невірний код підтвердження.");
        }

        // Скидання часу створення коду
        codeCreationTime = null;
    }
}