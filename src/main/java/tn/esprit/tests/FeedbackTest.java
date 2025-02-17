package tn.esprit.tests;

import tn.esprit.entites.Feedback;
import tn.esprit.services.FeedbackService;

import java.sql.SQLException;
import java.util.List;

public class FeedbackTest {

        public static void main(String[] args) {

            try {
                FeedbackService feedbackService = new FeedbackService();

                // 🔹 Premier feedback réussi
                Feedback feedback1 = new Feedback(7, 31, 5);
                int feedbackId = feedbackService.insert(feedback1);
                System.out.println("Feedback inséré avec ID : " + feedbackId);

                // 🔹 Deuxième feedback interdit (même utilisateur et même shop)
                Feedback feedback2 = new Feedback(10, 31, 3);
                feedbackService.insert(feedback2); // 🚨 Devrait lever une exception


                // 🔹 Modifier un feedback
                feedback1.setRating(1);  // Changer la note à 4
                feedback1.setId(feedbackId);
                feedbackService.update(feedback1);
                System.out.println("Feedback mis à jour !");

                // 🔹 Supprimer un feedback
                feedbackService.delete(feedbackId);
                System.out.println("Feedback supprimé !");

                // 🔹 Afficher tous les feedbacks
                List<Feedback> feedbacks = feedbackService.showAll();
                for (Feedback f : feedbacks) {
                    System.out.println(f);
                }


            } catch (SQLException e) {
                System.out.println("⚠️ Erreur : " + e.getMessage());
            }


        }}



