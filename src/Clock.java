import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.LocalTime;

public class Clock extends Application {

    private static final int SIZE = 400; // Размер квадратной рамки
    private static final int SQUARE_WIDTH = 240; // Ширина прямоугольника
    private static final int SQUARE_HEIGHT = 220; // Высота прямоугольника
    private static final int RADIUS = 80; // Радиус окружности

    @Override
    public void start(Stage primaryStage) {
        Canvas canvas = new Canvas(SIZE, SIZE);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        drawRectangle(gc);
        drawCircle(gc);
        drawClockHands(gc); // Initial draw of clock hands

        // Animation timer to update clock hands every second
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                gc.clearRect(0, 0, SIZE, SIZE); // Clear the canvas
                drawRectangle(gc);
                drawCircle(gc);
                drawClockHands(gc); // Redraw clock hands
            }
        };
        timer.start();

        StackPane root = new StackPane(canvas);
        Scene scene = new Scene(root, SIZE, SIZE);
        primaryStage.setTitle("ЭТИ ЧАСЫ ПРОСТО ИМБА!!!");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    private void drawRectangle(GraphicsContext gc) {
        gc.setStroke(javafx.scene.paint.Color.BLACK);
        gc.setLineWidth(2);
        double x = (double) (SIZE - SQUARE_WIDTH) / 2; // Центрируем прямоугольник по горизонтали
        double y = (double) (SIZE - SQUARE_HEIGHT) / 2; // Центрируем прямоугольник по вертикали
        gc.strokeRect(x, y, SQUARE_WIDTH, SQUARE_HEIGHT); // Рисуем первый прямоугольник

        // Рисуем две вертикальные линии на нижней линии прямоугольника
        double lineX1 = x + SQUARE_WIDTH / 6; // Первая линия на 1/6 ширины
        double lineX2 = x + 5 * SQUARE_WIDTH / 6; // Вторая линия на 5/6 ширины
        double lineY = y + SQUARE_HEIGHT; // Нижняя граница прямоугольника
        double lineLength = 40; // Длина вертикальных линий
        gc.setLineWidth(1); // Устанавливаем ширину линии
        gc.strokeLine(lineX1, lineY, lineX1, lineY + lineLength); // Первая вертикальная линия
        gc.strokeLine(lineX2, lineY, lineX2, lineY + lineLength); // Вторая вертикальная линия
        gc.strokeLine(lineX1 + lineLength / 2, lineY + lineLength, lineX1, lineY + lineLength); // Первая горизонтальная линия
        gc.strokeLine(lineX2, lineY + lineLength, lineX2 - lineLength / 2, lineY + lineLength); // Вторая горизонтальная линия
        // Рисуем дугу между горизонтальными линиями
        double arcWidth = lineX2 - lineX1 - lineLength; // Ширина дуги равна расстоянию между вертикальными линиями
        double arcX = lineX1 + lineLength / 2; // Начальная координата дуги
        double arcY = lineY + lineLength / 2; // Высота дуги
        gc.strokeArc(arcX, arcY, arcWidth, lineLength, 0, 180, javafx.scene.shape.ArcType.OPEN); // Рисуем дугу

        // Получаем текущую дату
        LocalDate currentDate = LocalDate.now();
        String dateText = String.format("%02d.%02d.%04d", currentDate.getDayOfMonth(), currentDate.getMonthValue(), currentDate.getYear());

        // Устанавливаем шрифт и цвет текста
        gc.setFill(javafx.scene.paint.Color.BLACK);
        gc.setFont(new javafx.scene.text.Font(16)); // Устанавливаем размер шрифта

        // Вычисляем позицию текста
        double textX = (SIZE - gc.getFont().getSize() * dateText.length() / 2) / 2; // Центрируем текст
        double textY = arcY - 5; // Позиция текста над дугой

        // Рисуем текст
        gc.fillText(dateText, textX, textY);
    }

    private void drawCircle(GraphicsContext gc) {
        gc.setStroke(javafx.scene.paint.Color.BLACK);
        gc.setLineWidth(2);
        double centerX = (double) SIZE / 2;
        double centerY = (double) SIZE / 2;
        gc.strokeOval(centerX - RADIUS, centerY - RADIUS, RADIUS * 2, RADIUS * 2); // Рисуем окружность

        // Рисуем 12 основных делений на окружности
        for (int i = 0; i < 12; i++) {
            double angle = Math.toRadians(i * 360.0 / 12); // Угол для каждого деления
            double startX = centerX + (RADIUS - 6) * Math.cos(angle - Math.PI / 2);
            double startY = centerY + (RADIUS - 6) * Math.sin(angle - Math.PI / 2);
            double endX = centerX + (RADIUS + 8) * Math.cos(angle - Math.PI / 2); // Увеличиваем длину деления снаружи
            double endY = centerY + (RADIUS + 8) * Math.sin(angle - Math.PI / 2);
            gc.strokeLine(startX, startY, endX, endY); // Рисуем основное деление

            // Добавляем номера к делениям снаружи круга
            double textX = centerX + (RADIUS + 20) * Math.cos(angle - Math.PI / 2); // Увеличиваем радиус для текста
            double textY = centerY + (RADIUS + 20) * Math.sin(angle - Math.PI / 2);
            gc.fillText(String.valueOf((i == 0) ? 12 : i), textX - 5, textY + 5); // Рисуем номер деления
        }

        // Рисуем 4 дополнительных деления между первым и вторым делением, только внутри окружности
        for (int i = 1; i <= 4; i++) {
            double angle = Math.toRadians((360.0 / 12) * (i / 5.0)); // Угол для дополнительных делений
            double startX = centerX + (RADIUS - 6) * Math.cos(angle - Math.PI / 2); // Начало внутри окружности
            double startY = centerY + (RADIUS - 6) * Math.sin(angle - Math.PI / 2);
            double endX = centerX + (RADIUS) * Math.cos(angle - Math.PI / 2); // Конец внутри окружности
            double endY = centerY + (RADIUS) * Math.sin(angle - Math.PI / 2);
            gc.strokeLine(startX, startY, endX, endY); // Рисуем дополнительное деление
        }
    }

    private void drawClockHands(GraphicsContext gc) {
        LocalTime now = LocalTime.now();
        double hour = now.getHour() % 12; // Convert to 12-hour format
        double minute = now.getMinute();
        double second = now.getSecond();

        double centerX = (double) SIZE / 2;
        double centerY = (double) SIZE / 2;

        // углы для стрелок
        double hourAngle = Math.toRadians((hour + minute / 60) * 30); // 360 / 12 = 30 degrees per hour
        double minuteAngle = Math.toRadians((minute + second / 60) * 6); // 360 / 60 = 6 degrees per minute
        double secondAngle = Math.toRadians(second * 6); // 360 / 60 = 6 degrees per second

        // Часовая стрелка
        gc.setStroke(javafx.scene.paint.Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeLine(centerX, centerY, centerX + (RADIUS - 40) * Math.cos(hourAngle - Math.PI / 2), centerY + (RADIUS - 40) * Math.sin(hourAngle - Math.PI / 2));

        // Минутная стрелка
        gc.setStroke(javafx.scene.paint.Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeLine(centerX, centerY, centerX + (RADIUS - 20) * Math.cos(minuteAngle - Math.PI / 2), centerY + (RADIUS - 20) * Math.sin(minuteAngle - Math.PI / 2));

        // Секундная стрелка
        gc.setStroke(javafx.scene.paint.Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeLine(centerX, centerY, centerX + (RADIUS - 10) * Math.cos(secondAngle - Math.PI / 2), centerY + (RADIUS - 10) * Math.sin(secondAngle - Math.PI / 2));
    }

    public static void main(String[] args) {
        launch(args);
    }
}