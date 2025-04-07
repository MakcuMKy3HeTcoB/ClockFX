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

    // Константы для размеров элементов часов
    private static final int SIZE = 400;          // Общий размер квадратного поля
    private static final int SQUARE_WIDTH = 240;  // Ширина основного прямоугольника
    private static final int SQUARE_HEIGHT = 220; // Высота основного прямоугольника
    private static final int RADIUS = 80;         // Радиус циферблата

    @Override
    public void start(Stage primaryStage) {
        Canvas canvas = createClockCanvas();
        Scene scene = createScene(canvas);

        setupStage(primaryStage, scene);
        startAnimationTimer(canvas);
    }

    private Canvas createClockCanvas() {
        Canvas canvas = new Canvas(SIZE, SIZE);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Первоначальная отрисовка элементов
        drawOutside(gc);
        drawCircle(gc);
        drawClockHands(gc);

        return canvas;
    }

    private Scene createScene(Canvas canvas) {
        StackPane root = new StackPane(canvas);
        return new Scene(root, SIZE, SIZE);
    }

    private void setupStage(Stage stage, Scene scene) {
        stage.setTitle("ЭТИ ЧАСЫ ПРОСТО ИМБА!!!");
        stage.setScene(scene);
        stage.show();
    }

    private void startAnimationTimer(Canvas canvas) {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Очищаем холст и перерисовываем все элементы
                gc.clearRect(0, 0, SIZE, SIZE);
                drawOutside(gc);
                drawCircle(gc);
                drawClockHands(gc);
            }
        };
        timer.start();
    }

    // Остальные методы (drawOutside, drawCircle, drawClockHands) остаются без изменений
    private void drawOutside(GraphicsContext gc) {
        gc.setLineWidth(2);

        // Центрируем основной прямоугольник
        double x = (double) (SIZE - SQUARE_WIDTH) / 2;
        double y = (double) (SIZE - SQUARE_HEIGHT) / 2;
        gc.strokeRect(x, y, SQUARE_WIDTH, SQUARE_HEIGHT);

        // Рисуем ножки часов (две вертикальные линии внизу)
        double lineX1 = x + (double) SQUARE_WIDTH / 6;    // Левая ножка
        double lineX2 = x + (double) (5 * SQUARE_WIDTH) / 6; // Правая ножка
        double lineY = y + SQUARE_HEIGHT;                 // Нижняя граница
        double lineLength = 40;                           // Длина ножек

        gc.setLineWidth(1);
        gc.strokeLine(lineX1, lineY, lineX1, lineY + lineLength);    // Левая ножка
        gc.strokeLine(lineX2, lineY, lineX2, lineY + lineLength);    // Правая ножка

        // Горизонтальные линии у основания ножек
        gc.strokeLine(lineX1 + lineLength / 2, lineY + lineLength, lineX1, lineY + lineLength);
        gc.strokeLine(lineX2, lineY + lineLength, lineX2 - lineLength / 2, lineY + lineLength);

        // Дуга между ножками
        double arcWidth = lineX2 - lineX1 - lineLength;
        double arcX = lineX1 + lineLength / 2;
        double arcY = lineY + lineLength / 2;
        gc.strokeArc(arcX, arcY, arcWidth, lineLength, 0, 180, javafx.scene.shape.ArcType.OPEN);

        // Получаем и отображаем текущую дату
        LocalDate currentDate = LocalDate.now();
        String dateText = String.format("%02d.%02d.%04d",
                currentDate.getDayOfMonth(),
                currentDate.getMonthValue(),
                currentDate.getYear());

        gc.setFont(new javafx.scene.text.Font(16));

        // Центрируем текст даты над дугой
        double textX = (SIZE - gc.getFont().getSize() * dateText.length() / 2) / 2;
        double textY = arcY - 5;
        gc.fillText(dateText, textX, textY);
    }

    private void drawCircle(GraphicsContext gc) {
        gc.setLineWidth(2);

        // Центр циферблата
        double centerX = (double) SIZE / 2;
        double centerY = (double) SIZE / 2;

        // Рисуем окружность циферблата
        gc.strokeOval(centerX - RADIUS, centerY - RADIUS, RADIUS * 2, RADIUS * 2);

        // Рисуем 12 основных делений (часовые метки)
        for (int i = 0; i < 12; i++) {
            double angle = Math.toRadians(i * 360.0 / 12); // Угол для каждого часа

            // Координаты начала и конца деления
            double startX = centerX + (RADIUS - 6) * Math.cos(angle - Math.PI / 2);
            double startY = centerY + (RADIUS - 6) * Math.sin(angle - Math.PI / 2);
            double endX = centerX + (RADIUS + 8) * Math.cos(angle - Math.PI / 2);
            double endY = centerY + (RADIUS + 8) * Math.sin(angle - Math.PI / 2);

            gc.strokeLine(startX, startY, endX, endY); // Рисуем деление

            // Подписываем цифры часов
            double textX = centerX + (RADIUS + 20) * Math.cos(angle - Math.PI / 2);
            double textY = centerY + (RADIUS + 20) * Math.sin(angle - Math.PI / 2);
            gc.fillText(String.valueOf((i == 0) ? 12 : i), textX - 5, textY + 5);
        }

        // Рисуем 4 дополнительных деления между часами
        for (int i = 1; i <= 4; i++) {
            double angle = Math.toRadians((360.0 / 12) * (i / 5.0));

            // Координаты для коротких делений
            double startX = centerX + (RADIUS - 6) * Math.cos(angle - Math.PI / 2);
            double startY = centerY + (RADIUS - 6) * Math.sin(angle - Math.PI / 2);
            double endX = centerX + RADIUS * Math.cos(angle - Math.PI / 2);
            double endY = centerY + RADIUS * Math.sin(angle - Math.PI / 2);

            gc.strokeLine(startX, startY, endX, endY);
        }
    }

    private void drawClockHands(GraphicsContext gc) {
        // Получаем текущее время
        LocalTime now = LocalTime.now();
        double hour = now.getHour() % 12;   // Часы (12-часовой формат)
        double minute = now.getMinute();    // Минуты
        double second = now.getSecond();    // Секунды

        // Центр циферблата
        double centerX = (double) SIZE / 2;
        double centerY = (double) SIZE / 2;

        // Вычисляем углы для каждой стрелки
        double hourAngle = Math.toRadians((hour + minute / 60) * 30);    // 30° на час + плавное движение
        double minuteAngle = Math.toRadians((minute + second / 60) * 6); // 6° на минуту + плавное движение
        double secondAngle = Math.toRadians(second * 6);                 // 6° на секунду

        // ЧАСОВАЯ СТРЕЛКА
        gc.setLineWidth(2);

        // Конечная точка часовой стрелки
        double hourEndX = centerX + (RADIUS - 40) * Math.cos(hourAngle - Math.PI / 2);
        double hourEndY = centerY + (RADIUS - 40) * Math.sin(hourAngle - Math.PI / 2);

        // Рисуем часовую стрелку
        gc.strokeLine(centerX, centerY, hourEndX, hourEndY);

        // РОМБ НА КОНЦЕ ЧАСОВОЙ СТРЕЛКИ
        double rhombusSize = 10;   // Размер ромба
        double rhombusOffset = 9;  // Смещение от конца стрелки

        // Предварительно вычисляем тригонометрические функции для оптимизации
        double cosHourAngle = Math.cos(hourAngle);
        double sinHourAngle = Math.sin(hourAngle);
        double cosHourAngleMinusPi2 = Math.cos(hourAngle - Math.PI / 2);
        double sinHourAngleMinusPi2 = Math.sin(hourAngle - Math.PI / 2);

        // Центр ромба (немного не доходя до конца стрелки)
        double rhombusCenterX = hourEndX + rhombusOffset * cosHourAngleMinusPi2;
        double rhombusCenterY = hourEndY + rhombusOffset * sinHourAngleMinusPi2;

        // Координаты вершин ромба (ориентированы по направлению стрелки)
        double[] xPoints = {
                rhombusCenterX + rhombusSize * cosHourAngleMinusPi2, // верхняя точка
                rhombusCenterX + (rhombusSize/2) * cosHourAngle,      // правая точка
                rhombusCenterX - rhombusSize * cosHourAngleMinusPi2,  // нижняя точка
                rhombusCenterX - (rhombusSize/2) * cosHourAngle       // левая точка
        };
        double[] yPoints = {
                rhombusCenterY + rhombusSize * sinHourAngleMinusPi2, // верхняя точка
                rhombusCenterY + (rhombusSize/2) * sinHourAngle,     // правая точка
                rhombusCenterY - rhombusSize * sinHourAngleMinusPi2, // нижняя точка
                rhombusCenterY - (rhombusSize/2) * sinHourAngle       // левая точка
        };

        // Рисуем контур ромба
        gc.setLineWidth(1);
        gc.strokePolygon(xPoints, yPoints, 4);

        // МИНУТНАЯ СТРЕЛКА
        gc.setLineWidth(2);

        // Конечная точка минутной стрелки
        double minuteEndX = centerX + (RADIUS - 20) * Math.cos(minuteAngle - Math.PI / 2);
        double minuteEndY = centerY + (RADIUS - 20) * Math.sin(minuteAngle - Math.PI / 2);

        // Рисуем минутную стрелку
        gc.strokeLine(centerX, centerY, minuteEndX, minuteEndY);

        // ТРЕУГОЛЬНИК НА КОНЦЕ МИНУТНОЙ СТРЕЛКИ
        double triangleSize = 10;   // Размер треугольника
        double triangleOffset = 5;  // Смещение от конца стрелки

        // Вершины треугольника
        double triangleX1 = minuteEndX + triangleOffset * Math.cos(minuteAngle - Math.PI / 2);
        double triangleY1 = minuteEndY + triangleOffset * Math.sin(minuteAngle - Math.PI / 2);
        double triangleX2 = triangleX1 - triangleSize * Math.cos(minuteAngle - Math.PI / 2 + Math.PI / 6);
        double triangleY2 = triangleY1 - triangleSize * Math.sin(minuteAngle - Math.PI / 2 + Math.PI / 6);
        double triangleX3 = triangleX1 - triangleSize * Math.cos(minuteAngle - Math.PI / 2 - Math.PI / 6);
        double triangleY3 = triangleY1 - triangleSize * Math.sin(minuteAngle - Math.PI / 2 - Math.PI / 6);

        // Рисуем закрашенный треугольник
        gc.fillPolygon(new double[]{triangleX1, triangleX2, triangleX3},
                new double[]{triangleY1, triangleY2, triangleY3}, 3);

        // СЕКУНДНАЯ СТРЕЛКА
        gc.setLineWidth(2);

        // Рисуем секундную стрелку
        gc.strokeLine(centerX, centerY,
                centerX + (RADIUS - 10) * Math.cos(secondAngle - Math.PI / 2),
                centerY + (RADIUS - 10) * Math.sin(secondAngle - Math.PI / 2));
    }

    public static void main(String[] args) {
        launch(args);
    }
}