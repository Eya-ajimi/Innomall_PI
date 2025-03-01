package tn.esprit.services;

import tn.esprit.entities.Schedule;
import tn.esprit.utils.DataBase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ScheduleService implements CRUD<Schedule> {
    private Connection connection;

    public ScheduleService() {
        this.connection = DataBase.getInstance().getCnx();
    }

    @Override
    public int insert(Schedule schedule) throws SQLException {
        String query = "INSERT INTO schedule (day_of_week, opening_time, closing_time, shopId) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, schedule.getDay().name());
            pst.setTime(2, schedule.getOpeningTime());
            pst.setTime(3, schedule.getClosingTime());
            pst.setInt(4, schedule.getShopOwnerId());
            return pst.executeUpdate();
        }
    }

    @Override
    public int update(Schedule schedule) throws SQLException {
        String query = "UPDATE schedule SET day_of_week = ?, opening_time = ?, closing_time = ?, shopId = ? WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, schedule.getDay().name());
            pst.setTime(2, schedule.getOpeningTime());
            pst.setTime(3, schedule.getClosingTime());
            pst.setInt(4, schedule.getShopOwnerId());
            pst.setInt(5, schedule.getId());
            return pst.executeUpdate();
        }
    }

    @Override
    public int delete(Schedule schedule) throws SQLException {
        String query = "DELETE FROM schedule WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, schedule.getId());
            return pst.executeUpdate();
        }
    }

    @Override
    public List<Schedule> showAll() throws SQLException {
        List<Schedule> schedules = new ArrayList<>();
        String query = "SELECT * FROM schedule";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Schedule schedule = new Schedule(
                        rs.getInt("id"),
                        tn.esprit.entities.enums.Days.valueOf(rs.getString("day_of_week")),
                        rs.getTime("opening_time"),
                        rs.getTime("closing_time"),
                        rs.getInt("shopId")
                );
                schedules.add(schedule);
            }
        }
        return schedules;
    }


    public Schedule getEntityById(int id) throws SQLException {
        String query = "SELECT * FROM schedule WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return new Schedule(
                        rs.getInt("id"),
                        tn.esprit.entities.enums.Days.valueOf(rs.getString("day_of_week")),
                        rs.getTime("opening_time"),
                        rs.getTime("closing_time"),
                        rs.getInt("shopId")
                );
            }
        }
        return null;
    }

    public List<Schedule> getScheduleByShopOwnerId(int shopOwnerId) throws SQLException {
        List<Schedule> schedules = new ArrayList<>();
        String query = "SELECT * FROM schedule WHERE shopId = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, shopOwnerId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Schedule schedule = new Schedule(
                        rs.getInt("id"),
                        tn.esprit.entities.enums.Days.valueOf(rs.getString("day_of_week")),
                        rs.getTime("opening_time"),
                        rs.getTime("closing_time"),
                        rs.getInt("shopId")
                );
                schedules.add(schedule);
            }
        }
        return schedules;
    }
}
