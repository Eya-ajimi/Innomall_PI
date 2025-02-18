package tn.esprit.tests;

import tn.esprit.entities.*;
import tn.esprit.services.ProductService;
import tn.esprit.utils.DataBase;
import java.sql.SQLException;
import java.util.List;

public class TestCrudProduct {

    private static ProductService productservice = new ProductService();



    public static void testCreateProduct() {
        Product p1 = new Product(3, null, "tiramisu", 30, 12);
        try {
            productservice.insert(p1);
            System.out.println("Product created successfully.");
        } catch (SQLException e) {
            System.out.println("Error creating product: " + e.getMessage());
        }
    }

    public static void testGetAllProducts() {
        try {
            List<Product> products = productservice.showAll();
            System.out.println(products);
        } catch (SQLException e) {
            System.out.println("Error fetching all products: " + e.getMessage());
        }
    }

    public static void testGetAllProductsByShopId() {
        try {
            List<Product> shop_products = productservice.getProductsByShopId(2);
            for (Product product : shop_products) {
                System.out.println(product);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching products by shop ID: " + e.getMessage());
        }
    }

    public static void testUpdateProduct() {
        String description_update = "WIWIWIWI";
        Integer discount_id_update = null;
        Integer stock_update = 70;
        Float price_update = null;
        int product_id_update = 1;

        try {
            Product product_update = productservice.getEntityById(product_id_update);
            if (product_update == null) {
                System.out.println("Product not found!");
            } else {
                System.out.println("Before update: " + product_update);

                // Update only non-null fields
                if (discount_id_update != null) product_update.setDiscount_id(discount_id_update);
                if (stock_update != null) product_update.setStock(stock_update);
                if (price_update != null) product_update.setPrice(price_update);
                if (description_update != null && !description_update.isEmpty()) product_update.setDescription(description_update);

                int rowsAffected = productservice.update(product_update);
                if (rowsAffected > 0) {
                    System.out.println("Product updated successfully.");
                } else {
                    System.out.println("No changes were made.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error updating product: " + e.getMessage());
        }
    }

    public static void testDeleteProduct() {
        int product_id_delete = 9;
        try {
            Product product_delete = productservice.getEntityById(product_id_delete);
            if (product_delete == null) {
                System.out.println("Product not found!");
            } else {
                System.out.println("Product to be deleted: " + product_delete);
                productservice.delete(product_delete);
                System.out.println("Product deleted successfully.");
            }
        } catch (SQLException e) {
            System.out.println("Error deleting product: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        DataBase m1 = DataBase.getInstance();

//        testCreateProduct();
//        testGetAllProducts();
//        testGetAllProductsByShopId();
//      testUpdateProduct();
//        testDeleteProduct();
    }
}
