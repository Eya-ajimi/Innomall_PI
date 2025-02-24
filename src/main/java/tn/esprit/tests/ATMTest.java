package tn.esprit.test;

import tn.esprit.entities.ATM;
import tn.esprit.services.ATMService;
import tn.esprit.utils.DataBase;

import java.sql.Connection;
import java.util.List;

public class ATMTest {

    public static void main(String[] args) {
        // Get the singleton instance of the database connection
        DataBase db = DataBase.getInstance();
        Connection connection = db.getCnx();

        // Create an instance of ATMService
        ATMService atmService = new ATMService(connection);

        try {
            // Test Insert Operation
            System.out.println("Testing Insert Operation...");
            ATM newATM = new ATM(0, "ATB", "Active");
            ATM newATM2 = new ATM(0, "BH", "Active");

            int insertResult = atmService.insert(newATM);
            int enter = atmService.insert(newATM2);
            if (insertResult > 0) {
                System.out.println("ATM inserted successfully! ID: " + newATM.getId());
            } else {
                System.out.println("Failed to insert ATM.");
            }

            // Test ShowAll Operation
            System.out.println("\nTesting ShowAll Operation...");
            List<ATM> atmList = atmService.showAll();
            for (ATM atm : atmList) {
                System.out.println(atm);
            }

            // Test Update Operation
            System.out.println("\nTesting Update Operation...");
            if (newATM.getId() != 0) {
                newATM.setStatus("Inactive");
                int updateResult = atmService.update(newATM);
                if (updateResult > 0) {
                    System.out.println("ATM updated successfully!");
                } else {
                    System.out.println("Failed to update ATM.");
                }
            } else {
                System.out.println("No ATM to update.");
            }

            // Test ShowAll Operation after Update
            System.out.println("\nTesting ShowAll Operation after Update...");
            atmList = atmService.showAll();
            for (ATM atm : atmList) {
                System.out.println(atm);
            }

            // Test Delete Operation
            System.out.println("\nTesting Delete Operation...");
            if (newATM.getId() != 0) {
                int deleteResult = atmService.delete(newATM);
                if (deleteResult > 0) {
                    System.out.println("ATM deleted successfully!");
                } else {
                    System.out.println("Failed to delete ATM.");
                }
            } else {
                System.out.println("No ATM to delete.");
            }

            // Test ShowAll Operation after Delete
            System.out.println("\nTesting ShowAll Operation after Delete...");
            atmList = atmService.showAll();
            for (ATM atm : atmList) {
                System.out.println(atm);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}