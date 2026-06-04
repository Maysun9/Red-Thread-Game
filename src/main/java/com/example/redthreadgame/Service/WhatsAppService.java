package com.example.redthreadgame.Service;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WhatsAppService {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.whatsapp.number}")
    private String fromNumber;

//    public void sendAllergenWarning() {
//        Twilio.init(accountSid, authToken);
//
//        String message = "";
//
//        Message.creator(
//                new PhoneNumber("whatsapp:" + bbbb),
//                new PhoneNumber(fromNumber),
//                message
//        ).create();
//    }

    public void sendSessionCode(String phoneNumber, String sessionCode) {
        Twilio.init(accountSid, authToken);
        Message.creator(
                new PhoneNumber("whatsapp:" + phoneNumber),
                new PhoneNumber("whatsapp:" + fromNumber),
                "Your game session code is: " + sessionCode
        ).create();
    }

}
