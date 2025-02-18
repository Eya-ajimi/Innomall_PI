package tn.esprit.tests;

import tn.esprit.entities.Schedule;
import tn.esprit.entities.enums.Days;
import tn.esprit.services.ScheduleService;
import tn.esprit.utils.DataBase;
import java.util.Scanner;
import java.sql.SQLException;
import java.sql.Time;
import java.util.Collections;
import java.util.List;

public class TestCrudSchedule {
    public static void main(String[] args) {
        DataBase m1 = DataBase.getInstance();
        ScheduleService scheduleService = new ScheduleService();

//        addSchedule(scheduleService);
//       getAllSchedules(scheduleService);
//       getScheduleByShopOwnerId(scheduleService, 1);
//        updateSchedule(scheduleService,2);
//        deleteSchedule(scheduleService);
    }

    private static void addSchedule(ScheduleService scheduleService) {
        Schedule s1 = new Schedule( Days.WEDNESDAY, Time.valueOf("08:00:00"), Time.valueOf("21:00:00"), 3);
        try {
            int result = scheduleService.insert(s1);
            if (result > 0) {
                System.out.println("Schedule added !");
            }
        } catch (SQLException e) {
            System.out.println("Error adding schedule: " + e.getMessage());
        }
    }

    private static void getAllSchedules(ScheduleService scheduleService) {
        try {
            List<Schedule> schedules = scheduleService.showAll();
            if (schedules.size() > 0) {
                for (Schedule schedule : schedules) {
                    System.out.println(schedule);
                }
            } else {
                System.out.println("No schedules found.");
            }
        } catch (SQLException e) {
            System.out.println("Error fetching all schedules: " + e.getMessage());
        }
    }

    private static void getScheduleByShopOwnerId(ScheduleService scheduleService, int shop_id) {
        try {
            List<Schedule> schedule = scheduleService.getScheduleByShopOwnerId(shop_id);
            if (schedule.size() !=0) {
                for (Schedule schedule1 : schedule) {
                    System.out.println(schedule1);
                }
            } else {
                System.out.println("No schedule found with ID: " + shop_id);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching schedule by ID: " + e.getMessage());
        }
    }

    private static void updateSchedule(ScheduleService scheduleService, int id_update) {
        try {
            Schedule schedule_to_update = scheduleService.getEntityById(id_update);
            if (schedule_to_update == null) {
                System.out.println("No schedule found with ID: " + id_update);
                return;
            }

            Scanner scanner = new Scanner(System.in);

            // Ask for updates
            System.out.println("Current day: " + schedule_to_update.getDay() + ". Update? (y/n)");
            if (scanner.nextLine().equalsIgnoreCase("y")) {
                System.out.println("Enter new day (e.g., MONDAY, TUESDAY...): ");
                schedule_to_update.setDay(Days.valueOf(scanner.nextLine().toUpperCase()));
            }

            System.out.println("Current opening time: " + schedule_to_update.getOpeningTime() + ". Update? (y/n)");
            if (scanner.nextLine().equalsIgnoreCase("y")) {
                System.out.println("Enter new opening time (HH:mm:ss): ");
                schedule_to_update.setOpeningTime(Time.valueOf(scanner.nextLine()));
            }

            System.out.println("Current closing time: " + schedule_to_update.getClosingTime() + ". Update? (y/n)");
            if (scanner.nextLine().equalsIgnoreCase("y")) {
                System.out.println("Enter new closing time (HH:mm:ss): ");
                schedule_to_update.setClosingTime(Time.valueOf(scanner.nextLine()));
            }

            System.out.println("Current shop owner ID: " + schedule_to_update.getShopOwnerId() + ". Update? (y/n)");
            if (scanner.nextLine().equalsIgnoreCase("y")) {
                System.out.println("Enter new shop owner ID: ");
                schedule_to_update.setShopOwnerId(Integer.parseInt(scanner.nextLine()));
            }

            // Perform update
            int result = scheduleService.update(schedule_to_update);
            if (result > 0) {
                System.out.println("Schedule updated successfully.");
            } else {
                System.out.println("No schedule found to update.");
            }

        } catch (SQLException e) {
            System.out.println("Error updating schedule: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid input provided: " + e.getMessage());
        }
    }

    private static void deleteSchedule(ScheduleService scheduleService) {
        Schedule scheduleToDelete = new Schedule(1, Days.WEDNESDAY, Time.valueOf("10:00:00"), Time.valueOf("17:00:00"), 1);
        try {
            int result = scheduleService.delete(scheduleToDelete);
            if (result > 0) {
                System.out.println("Schedule deleted successfully.");
            } else {
                System.out.println("No schedule found to delete.");
            }
        } catch (SQLException e) {
            System.out.println("Error deleting schedule: " + e.getMessage());
        }
    }
}
