import java.awt.geom.Rectangle2D;
import javax.swing.JButton;
import javax.swing.JFrame;
import java.awt.*;
import java.awt.event.*;

public class FractalExplorer {
    private int displaySize;
    private JImageDisplay display;
    private FractalGenerator fractal;
    private Rectangle2D.Double rectangle;

    public FractalExplorer(int size) {
        displaySize = size;
        fractal = new Mandelbrot();
        rectangle = new Rectangle2D.Double();
        fractal.getInitialRange(rectangle);
        display = new JImageDisplay(displaySize, displaySize); // квадрат
    }

    public void createAndShowGUI() {
        display.setLayout(new BorderLayout());
        // создание окна с заголовком
        JFrame frame = new JFrame("Fractal Explorer: Mandelbrot");
        frame.add(display, BorderLayout.CENTER);
        // создание кнопки
        JButton resetButton = new JButton("Reset scale");
        // обработчик кнопки сброса
        ResetHandler handler = new ResetHandler();
        resetButton.addActionListener(handler);
        frame.add(resetButton, BorderLayout.SOUTH);
        // обработчик клика мыши по фракталу
        MouseHandler click = new MouseHandler();
        display.addMouseListener(click);
        // окно закрывается только при нажатие крестика
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);
    }

    /*
     * Метод для отрисовки фрактала
     * Окрашивает пиксели в зависимотси от итерации
     */
    private void drawFractal() {
        System.out.println("drawFractal() start");
        for (int x = 0; x < displaySize; x++) {
            for (int y = 0; y < displaySize; y++) {

                double xCoord = FractalGenerator.getCoord(rectangle.x, rectangle.x +
                        rectangle.width, displaySize, x);
                double yCoord = FractalGenerator.getCoord(rectangle.y, rectangle.y +
                        rectangle.height, displaySize, y);

                int iteration = fractal.numIterations(xCoord, yCoord);

                if (iteration == -1) { // точка не выходит за границы, ставим черный
                    display.drawPixel(x, y, 0);
                } else {
                    float hue = 0.7f + (float) iteration / 200f;
                    int rgbColor = Color.HSBtoRGB(hue, 1f, 1f);
                    display.drawPixel(x, y, rgbColor);
                }
            }
        }
        System.out.println("drawFractal() done");
        display.repaint();
    }

    private class ResetHandler implements ActionListener {

        // метод созданный по умолчанию
        @Override
        public void actionPerformed(ActionEvent e) {
            // возвращение фрактала к начальному положению
            fractal.getInitialRange(rectangle);
            drawFractal();
        }
    }

    private class MouseHandler extends MouseAdapter {
        public void mouseClicked(MouseEvent e) {
            // Координаты клика
            int x = e.getX();
            int y = e.getY();
            // Новые координаты центра
            double xCoord = FractalGenerator.getCoord(rectangle.x,
                    rectangle.x + rectangle.width, displaySize, x);
            double yCoord = FractalGenerator.getCoord(rectangle.y,
                    rectangle.y + rectangle.height, displaySize, y);

            // Устанавливаем центр в точку по которой был клик и приближаем
            fractal.recenterAndZoomRange(rectangle, xCoord, yCoord, 0.5);
            // перерисовываем
            drawFractal();
        }
    }

    public static void main(String[] args) {
        // размер окна
        FractalExplorer fractal = new FractalExplorer(800);
        // рисование gui
        fractal.createAndShowGUI();
        // оторисовка
        fractal.drawFractal();
    }
}
