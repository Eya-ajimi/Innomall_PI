package tn.esprit.services;
import tn.esprit.entities.Product;
import tn.esprit.utils.DataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductService implements CRUD<Product> {

    private Connection connection;

    public ProductService() {
        this.connection = DataBase.getInstance().getCnx();
    }

    @Override
    public int insert(Product product) throws SQLException {
        // Use PreparedStatement to prevent SQL injection
        String query = "INSERT INTO product (shop_id, discount_id, description, stock, price) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            // Set the parameters for the query
            pst.setInt(1, product.getShop_id());
           //did that so that a product can be without a discount
            if (product.getDiscount_id()!= null) {
                pst.setInt(2, product.getDiscount_id());
            } else {
                pst.setNull(2, Types.INTEGER);  // Set discount_id to SQL NULL
            }
            pst.setString(3, product.getDescription());
            pst.setInt(4, product.getStock());
            pst.setFloat(5, product.getPrice());

            // Execute the insert and return the number of rows affected
            return pst.executeUpdate();
        }
    }

    @Override
    public int update(Product product) throws SQLException {
        String query = "UPDATE product SET discount_id = ?, description = ?, stock = ?, price = ? WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            // Handle null discount_id
            if (product.getDiscount_id() == null) {
                pst.setNull(1, java.sql.Types.INTEGER);
            } else {
                pst.setInt(1, product.getDiscount_id());
            }

            pst.setString(2, product.getDescription());
            pst.setInt(3, product.getStock());
            pst.setFloat(4, product.getPrice());
            pst.setInt(5, product.getId());

            return pst.executeUpdate();
        }
    }

    @Override
    public int delete(Product product) throws SQLException {
        String query = "DELETE FROM product WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, product.getId());
            return pst.executeUpdate();
        }
    }

    @Override
    public List<Product> showAll() throws SQLException {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM product";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Product product = new Product();
                product.setId(rs.getInt("id"));
                product.setShop_id(rs.getInt("shop_id"));
                product.setDiscount_id(rs.getInt("discount_id"));
                product.setDescription(rs.getString("description"));
                product.setStock(rs.getInt("stock"));
                product.setPrice(rs.getFloat("price"));
                products.add(product);
            }
        }
        return products;
    }

    @Override
    public Product getEntityById(int id) throws SQLException {
        String query = "SELECT * FROM product WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return new Product(
                        rs.getInt("id"),
                        rs.getInt("shop_id"),
                        rs.getObject("discount_id") != null ? rs.getInt("discount_id") : null,
                        rs.getString("description"),
                        rs.getInt("stock"),
                        rs.getFloat("price")
                );
            }
        }
        return null; // Return null if no product is found
    }

    public List<Product> getProductsByShopId(int shopId) throws SQLException {
        String query = "SELECT * FROM product WHERE shop_id = ?";
        List<Product> productList = new ArrayList<>();
        try (PreparedStatement pst = connection.prepareStatement(query)) {

            pst.setInt(1, shopId);

            // Execute the query
            ResultSet rs = pst.executeQuery();

            // Loop through the result set and add products to the list
            while (rs.next()) {
                Product product = new Product();
                product.setId(rs.getInt("id"));
                product.setShop_id(rs.getInt("shop_id"));
                product.setDiscount_id(rs.getInt("discount_id"));
                product.setDescription(rs.getString("description"));
                product.setStock(rs.getInt("stock"));
                product.setPrice(rs.getFloat("price"));
                productList.add(product);
            }
        }

        return productList;
    }

}
