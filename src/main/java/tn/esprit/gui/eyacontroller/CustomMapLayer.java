package tn.esprit.gui.eyacontroller;

import com.gluonhq.maps.MapLayer;
import com.gluonhq.maps.MapPoint;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class CustomMapLayer extends MapLayer {
    private final Node marker;
    private final Text markerText;
    private final MapPoint espritCharguia = new MapPoint(36.85328920224959, 10.20719057853217);

    public CustomMapLayer() {
        marker = new Circle(10, Color.RED);
        markerText = new Text("Esprit Charguia");
        markerText.setFill(Color.BLACK);
        markerText.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        getChildren().addAll(marker, markerText);
    }

    @Override
    protected void layoutLayer() {
        Point2D point = getMapPoint(espritCharguia.getLatitude(), espritCharguia.getLongitude());
        marker.setTranslateX(point.getX());
        marker.setTranslateY(point.getY());
        double textOffsetX = 10; // Adjust this value as needed
        double textOffsetY = -10; // Adjust this value as needed
        markerText.setTranslateX(point.getX() + textOffsetX);
        markerText.setTranslateY(point.getY() + textOffsetY);
    }
}