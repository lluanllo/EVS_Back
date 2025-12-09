package com.empresa.course.MicroserviceCourseApi.Service.Impl;

import com.empresa.course.MicroserviceCourseApi.Entities.Enums.ManeuverType;
import com.empresa.course.MicroserviceCourseApi.Entities.RouteLeg;
import com.empresa.course.MicroserviceCourseApi.Entities.RoutePlan;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.List;

/**
 * Servicio para generar imágenes de las rutas de navegación.
 * Genera un diagrama visual con:
 * - La playa
 * - El canal náutico
 * - La ruta con todos los tramos
 * - Indicadores de viradas y bordos
 * - Dirección del viento
 */
@Slf4j
@Service
public class RouteImageGeneratorService {

    private static final int IMAGE_WIDTH = 800;
    private static final int IMAGE_HEIGHT = 600;
    private static final int MARGIN = 50;

    @Value("${sailing.beach.name:La Antilla}")
    private String beachName;

    /**
     * Genera una imagen de la ruta en formato Base64.
     */
    public String generateRouteImage(RoutePlan plan) {
        try {
            BufferedImage image = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = image.createGraphics();

            // Configurar renderizado de alta calidad
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // Fondo (mar)
            drawBackground(g2d);

            // Playa
            drawBeach(g2d);

            // Dirección del viento
            drawWindIndicator(g2d, plan.getWindDirection().getDegrees(), plan.getWindSpeedKnots());

            // Ruta de navegación
            drawRoute(g2d, plan.getLegs(), plan.getBeachLatitude(), plan.getBeachLongitude());

            // Leyenda
            drawLegend(g2d, plan);

            // Título
            drawTitle(g2d, plan);

            g2d.dispose();

            // Convertir a Base64
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG", baos);
            return Base64.getEncoder().encodeToString(baos.toByteArray());

        } catch (Exception e) {
            log.error("Error generando imagen de ruta: {}", e.getMessage(), e);
            throw new RuntimeException("Error generando imagen de ruta", e);
        }
    }

    private void drawBackground(Graphics2D g2d) {
        // Gradiente de mar
        GradientPaint seaGradient = new GradientPaint(
                0, 0, new Color(0, 105, 148),
                0, IMAGE_HEIGHT, new Color(0, 150, 199)
        );
        g2d.setPaint(seaGradient);
        g2d.fillRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);

        // Patrón de olas sutiles
        g2d.setColor(new Color(255, 255, 255, 30));
        for (int y = 50; y < IMAGE_HEIGHT; y += 40) {
            for (int x = 0; x < IMAGE_WIDTH; x += 80) {
                g2d.drawArc(x, y, 80, 20, 0, 180);
            }
        }
    }

    private void drawBeach(Graphics2D g2d) {
        // Arena de la playa en la parte inferior
        int beachHeight = 80;
        GradientPaint sandGradient = new GradientPaint(
                0, IMAGE_HEIGHT - beachHeight, new Color(238, 214, 175),
                0, IMAGE_HEIGHT, new Color(210, 180, 140)
        );
        g2d.setPaint(sandGradient);
        g2d.fillRect(0, IMAGE_HEIGHT - beachHeight, IMAGE_WIDTH, beachHeight);

        // Línea de orilla
        g2d.setColor(new Color(255, 255, 255, 150));
        g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.drawLine(0, IMAGE_HEIGHT - beachHeight, IMAGE_WIDTH, IMAGE_HEIGHT - beachHeight);

        // Nombre de la playa
        g2d.setColor(new Color(101, 67, 33));
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("🏖️ " + beachName, 20, IMAGE_HEIGHT - 30);
    }

    private void drawWindIndicator(Graphics2D g2d, int windDegrees, int windSpeed) {
        int centerX = IMAGE_WIDTH - 80;
        int centerY = 80;
        int arrowLength = 40;

        // Círculo de fondo
        g2d.setColor(new Color(255, 255, 255, 200));
        g2d.fillOval(centerX - 50, centerY - 50, 100, 100);
        g2d.setColor(Color.DARK_GRAY);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawOval(centerX - 50, centerY - 50, 100, 100);

        // Flecha de viento
        AffineTransform old = g2d.getTransform();
        g2d.translate(centerX, centerY);
        g2d.rotate(Math.toRadians(windDegrees));

        // Cuerpo de la flecha
        g2d.setColor(new Color(220, 50, 50));
        g2d.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.drawLine(0, -arrowLength, 0, arrowLength);

        // Punta de la flecha (hacia donde va el viento)
        Path2D arrow = new Path2D.Double();
        arrow.moveTo(0, arrowLength);
        arrow.lineTo(-10, arrowLength - 15);
        arrow.lineTo(10, arrowLength - 15);
        arrow.closePath();
        g2d.fill(arrow);

        g2d.setTransform(old);

        // Texto de velocidad
        g2d.setColor(Color.DARK_GRAY);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        String windText = windSpeed + " kn";
        g2d.drawString(windText, centerX - 15, centerY + 70);
        g2d.drawString("Viento", centerX - 20, centerY - 55);
    }

    private void drawRoute(Graphics2D g2d, List<RouteLeg> legs, double beachLat, double beachLng) {
        if (legs.isEmpty()) return;

        // Calcular escala y offset para centrar la ruta
        double minLat = legs.stream().mapToDouble(RouteLeg::getStartLatitude).min().orElse(beachLat);
        double maxLat = legs.stream().mapToDouble(RouteLeg::getEndLatitude).max().orElse(beachLat);
        double minLng = legs.stream().mapToDouble(RouteLeg::getStartLongitude).min().orElse(beachLng);
        double maxLng = legs.stream().mapToDouble(RouteLeg::getEndLongitude).max().orElse(beachLng);

        // Añadir margen
        double latRange = Math.max(maxLat - minLat, 0.005);
        double lngRange = Math.max(maxLng - minLng, 0.005);

        // Área de dibujo
        int drawWidth = IMAGE_WIDTH - 2 * MARGIN - 120; // Espacio para leyenda
        int drawHeight = IMAGE_HEIGHT - 2 * MARGIN - 80; // Espacio para playa

        // Punto de partida (playa)
        int startX = MARGIN + 50;
        int startY = IMAGE_HEIGHT - MARGIN - 80;

        // Dibujar cada tramo
        int prevX = startX;
        int prevY = startY;

        for (int i = 0; i < legs.size(); i++) {
            RouteLeg leg = legs.get(i);

            // Calcular posiciones en píxeles
            int endX = (int) (startX + ((leg.getEndLongitude() - beachLng) / lngRange) * drawWidth);
            int endY = (int) (startY - ((leg.getEndLatitude() - beachLat) / latRange) * drawHeight);

            // Limitar al área visible
            endX = Math.max(MARGIN, Math.min(IMAGE_WIDTH - MARGIN - 120, endX));
            endY = Math.max(MARGIN, Math.min(IMAGE_HEIGHT - MARGIN - 80, endY));

            // Color según tipo de maniobra
            Color legColor = getColorForManeuver(leg.getManeuverType());
            g2d.setColor(legColor);
            g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            // Dibujar línea del tramo
            if (leg.getDistanceMeters() > 0) {
                g2d.drawLine(prevX, prevY, endX, endY);

                // Dibujar flecha de dirección en el medio
                drawArrowHead(g2d, prevX, prevY, endX, endY, legColor);

                // Número del tramo
                int midX = (prevX + endX) / 2;
                int midY = (prevY + endY) / 2;
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 10));
                g2d.fillOval(midX - 8, midY - 8, 16, 16);
                g2d.setColor(Color.DARK_GRAY);
                g2d.drawString(String.valueOf(leg.getOrder()), midX - 3, midY + 4);
            }

            // Marcador de virada
            if (leg.getManeuverType() == ManeuverType.VIRADA ||
                leg.getManeuverType() == ManeuverType.TRASLUCHADA) {
                drawManeuverMarker(g2d, endX, endY, leg.getManeuverType());
            }

            prevX = endX;
            prevY = endY;
        }

        // Punto de inicio (embarcadero)
        g2d.setColor(new Color(0, 150, 0));
        g2d.fillOval(startX - 8, startY - 8, 16, 16);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 10));
        g2d.drawString("S", startX - 3, startY + 4);

        // Punto final
        g2d.setColor(new Color(200, 0, 0));
        g2d.fillOval(prevX - 8, prevY - 8, 16, 16);
        g2d.setColor(Color.WHITE);
        g2d.drawString("F", prevX - 3, prevY + 4);
    }

    private void drawArrowHead(Graphics2D g2d, int x1, int y1, int x2, int y2, Color color) {
        double angle = Math.atan2(y2 - y1, x2 - x1);
        int midX = (x1 + x2) / 2;
        int midY = (y1 + y2) / 2;
        int arrowSize = 8;

        Path2D arrow = new Path2D.Double();
        arrow.moveTo(midX + arrowSize * Math.cos(angle), midY + arrowSize * Math.sin(angle));
        arrow.lineTo(midX - arrowSize * Math.cos(angle - Math.PI/6), midY - arrowSize * Math.sin(angle - Math.PI/6));
        arrow.lineTo(midX - arrowSize * Math.cos(angle + Math.PI/6), midY - arrowSize * Math.sin(angle + Math.PI/6));
        arrow.closePath();

        g2d.setColor(color);
        g2d.fill(arrow);
    }

    private void drawManeuverMarker(Graphics2D g2d, int x, int y, ManeuverType type) {
        g2d.setColor(type == ManeuverType.VIRADA ? new Color(255, 165, 0) : new Color(255, 100, 100));
        g2d.setStroke(new BasicStroke(2));

        // Círculo con símbolo
        g2d.drawOval(x - 6, y - 6, 12, 12);
        g2d.setFont(new Font("Arial", Font.BOLD, 8));
        g2d.drawString(type == ManeuverType.VIRADA ? "V" : "T", x - 3, y + 3);
    }

    private Color getColorForManeuver(ManeuverType type) {
        return switch (type) {
            case CENIDA -> new Color(220, 20, 60); // Rojo - dificultad
            case TRAVES -> new Color(30, 144, 255); // Azul
            case LARGO -> new Color(50, 205, 50); // Verde
            case POPA -> new Color(255, 215, 0); // Dorado
            case SALIDA, LLEGADA -> new Color(128, 0, 128); // Púrpura
            default -> new Color(100, 100, 100);
        };
    }

    private void drawLegend(Graphics2D g2d, RoutePlan plan) {
        int legendX = IMAGE_WIDTH - 140;
        int legendY = 150;
        int lineHeight = 20;

        // Fondo de leyenda
        g2d.setColor(new Color(255, 255, 255, 220));
        g2d.fillRoundRect(legendX - 10, legendY - 20, 130, 180, 10, 10);
        g2d.setColor(Color.DARK_GRAY);
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRoundRect(legendX - 10, legendY - 20, 130, 180, 10, 10);

        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString("Leyenda", legendX, legendY);

        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        int y = legendY + lineHeight;

        // Tipos de navegación
        drawLegendItem(g2d, legendX, y, new Color(220, 20, 60), "Ceñida");
        y += lineHeight;
        drawLegendItem(g2d, legendX, y, new Color(30, 144, 255), "Través");
        y += lineHeight;
        drawLegendItem(g2d, legendX, y, new Color(50, 205, 50), "Largo");
        y += lineHeight;
        drawLegendItem(g2d, legendX, y, new Color(255, 215, 0), "Popa");
        y += lineHeight + 5;

        // Marcadores
        g2d.setColor(new Color(0, 150, 0));
        g2d.fillOval(legendX, y - 4, 10, 10);
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawString("Salida", legendX + 15, y + 4);
        y += lineHeight;

        g2d.setColor(new Color(200, 0, 0));
        g2d.fillOval(legendX, y - 4, 10, 10);
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawString("Llegada", legendX + 15, y + 4);
        y += lineHeight;

        g2d.setColor(new Color(255, 165, 0));
        g2d.drawString("V = Virada", legendX, y + 4);
        y += lineHeight;
        g2d.drawString("T = Trasluchada", legendX, y + 4);
    }

    private void drawLegendItem(Graphics2D g2d, int x, int y, Color color, String text) {
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawLine(x, y, x + 20, y);
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawString(text, x + 25, y + 4);
    }

    private void drawTitle(Graphics2D g2d, RoutePlan plan) {
        // Título
        g2d.setColor(new Color(255, 255, 255, 220));
        g2d.fillRoundRect(10, 10, 350, 60, 10, 10);

        g2d.setColor(Color.DARK_GRAY);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("Plan de Navegación - " + plan.getCourseType().getDisplayName(), 20, 35);

        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.drawString(String.format("Duración: %d min | Nivel: %d | %s",
                plan.getClassDurationMinutes(),
                plan.getStudentLevel(),
                plan.getWindDirection().getDisplayName() + " " + plan.getWindSpeedKnots() + "kn"
        ), 20, 55);
    }
}

