package tn.esprit.services.sofieneservice;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class TwilioService {

    // Replace these with your Twilio credentials
    public static final String ACCOUNT_SID = "ACf1813a6857c67ca95ce8bcc12460968e";
    public static final String AUTH_TOKEN = "bd10dae3d67cc1a764552f7594438b11";
    public static final String TWILIO_PHONE_NUMBER = "+18313188131";

    static {
        // Initialize Twilio
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    /**
     * Send an SMS message.
     *
     * @param toPhoneNumber The recipient's phone number (e.g., "55321315" or "+21655321315").
     * @param messageBody   The message to send.
     */
    public static void sendSMS(String toPhoneNumber, String messageBody) {
        try {
            // Ensure the phone number is in E.164 format
            String formattedPhoneNumber = formatPhoneNumber(toPhoneNumber);

            // Send the SMS
            Message message = Message.creator(
                    new PhoneNumber(formattedPhoneNumber), // To
                    new PhoneNumber(TWILIO_PHONE_NUMBER), // From
                    messageBody // Message body
            ).create();

            System.out.println("SMS sent successfully! SID: " + message.getSid());
        } catch (Exception e) {
            System.err.println("Failed to send SMS: " + e.getMessage());
        }
    }

    /**
     * Format the phone number to E.164 format.
     *
     * @param phoneNumber The raw phone number (e.g., "55321315").
     * @return The formatted phone number (e.g., "+21655321315").
     */
    private static String formatPhoneNumber(String phoneNumber) {
        // Remove any spaces or special characters
        phoneNumber = phoneNumber.replaceAll("[^0-9]", "");

        // If the number doesn't start with "+216", add it
        if (!phoneNumber.startsWith("+216")) {
            phoneNumber = "+216" + phoneNumber;
        }

        return phoneNumber;
    }
}