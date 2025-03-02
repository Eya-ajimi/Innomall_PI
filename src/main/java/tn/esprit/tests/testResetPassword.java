package tn.esprit.tests;

import tn.esprit.services.azizservice.UserService;
import tn.esprit.utils.EmailService;


public class testResetPassword {
    public static void main(String[] args) {



        try {
            UserService userService = new UserService();
            String email = "mimo@YOPmail.com";
            System.out.println(userService.emailExists(email));
            if (userService.emailExists(email)) {
                int token = userService.generatePasswordResetToken(email);


                EmailService.sendPasswordResetEmail(email, token);
                System.out.println("mail envoyé");
            }
            else {

                System.out.println("Email does not exist");
            }

                 }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
