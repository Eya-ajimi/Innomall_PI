package tn.esprit.tests;

import tn.esprit.entities.Discount;
import tn.esprit.services.DiscountService;
import tn.esprit.utils.DataBase;

import java.sql.SQLException;
import java.util.List;
import java.util.Date;

public class TestCrudDiscount {
    public static void main(String[] args) {
        DataBase m1 = DataBase.getInstance();
        DiscountService discountService = new DiscountService();

//        addDiscount(discountService);
//       getAllDiscounts(discountService);
//        getDiscountsByShopId(discountService, 1);
//        getDiscountById(discountService, 1);
//        updateDiscount(discountService);
//        deleteDiscount(discountService);
    }

    private static void addDiscount(DiscountService discountService) {
        Discount d1 = new Discount(1, 75f, new Date(), new Date()); // Sample data for Discount
        try {
            int result = discountService.insert(d1);
            if (result > 0) {
                System.out.println("Discount added successfully.");
            }
        } catch (SQLException e) {
            System.out.println("Error adding discount: " + e.getMessage());
        }
    }

    private static void getAllDiscounts(DiscountService discountService) {
        try {
            List<Discount> discounts = discountService.showAll();
            if (discounts.size() > 0) {
                for (Discount discount : discounts) {
                    System.out.println(discount);
                }
            } else {
                System.out.println("No discounts found.");
            }
        } catch (SQLException e) {
            System.out.println("Error fetching all discounts: " + e.getMessage());
        }
    }

    private static void getDiscountsByShopId(DiscountService discountService, int shopId) {
        try {
            List<Discount> discounts = discountService.getDiscountByShopId(shopId);
            if (discounts.size() > 0) {
                for (Discount discount : discounts) {
                    System.out.println(discount);
                }
            } else {
                System.out.println("No discounts found for shop with ID: " + shopId);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching discounts for shop: " + e.getMessage());
        }
    }

    private static void getDiscountById(DiscountService discountService, int discountId) {
        try {
            Discount discount = discountService.getEntityById(discountId);
            if (discount != null) {
                System.out.println(discount);
            } else {
                System.out.println("No discount found with ID: " + discountId);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching discount by ID: " + e.getMessage());
        }
    }

    private static void updateDiscount(DiscountService discountService) {
        Discount discountToUpdate = new Discount( 1, 15.0f, new Date(), new Date()); // Sample data for updating
         // Set the ID of the discount to update
        try {
            int result = discountService.update(discountToUpdate);
            if (result > 0) {
                System.out.println("Discount updated successfully.");
            } else {
                System.out.println("No discount found to update.");
            }
        } catch (SQLException e) {
            System.out.println("Error updating discount: " + e.getMessage());
        }
    }

    private static void deleteDiscount(DiscountService discountService) {
        Discount discountToDelete = new Discount(1, 1, 0.0f, new Date(), new Date()); // Sample data for deleting
      // Set the ID of the discount to delete
        try {
            int result = discountService.delete(discountToDelete);
            if (result > 0) {
                System.out.println("Discount deleted successfully.");
            } else {
                System.out.println("No discount found to delete.");
            }
        } catch (SQLException e) {
            System.out.println("Error deleting discount: " + e.getMessage());
        }
    }
}
