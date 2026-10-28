import java.util.Scanner;

/**
 * Course 1: Calculator CLI
 * Grundlagen der Informatik IV - Hochschule Düsseldorf
 */
public class App {

    // Einstiegspunkt: Die JVM ruft main ohne Objekt auf (deshalb static)
    public static void main(String[] args) {
        runMenu();
    }

    // Menue-Methode (Task 1.3.4)
    public static void runMenu() {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        double firstNumber;
        double secondNumber;
        int firstInteger;
        int secondInteger;

        while (running) {
            System.out.println("CALCULATOR - bitte waehlen:");
            System.out.println("0: end program");
            System.out.println("1: add");
            System.out.println("2: subtract");
            System.out.println("3: multiply");
            System.out.println("4: divide");
            System.out.println("5: sin (x in Grad)");
            System.out.println("6: power (x^y)");
            System.out.println("7: gcd");
            System.out.println("8: fibonacci");

            int choice = scanner.nextInt();

            if (choice == 0) {
                running = false;
                System.out.println("Programm beendet.");
                break;
            }

            switch (choice) {
                case 1:
                    System.out.println("Erster Summand: ");
                    firstNumber = scanner.nextDouble();
                    System.out.println("Zweiter Summand: ");
                    secondNumber = scanner.nextDouble();
                    System.out.println(firstNumber + " + " + secondNumber + " = " + add(firstNumber, secondNumber));
                    break;

                case 2:
                    System.out.println("Minuend: ");
                    firstNumber = scanner.nextDouble();
                    System.out.println("Subtrahend: ");
                    secondNumber = scanner.nextDouble();
                    System.out.println(firstNumber + " - " + secondNumber + " = " + subtract(firstNumber, secondNumber));
                    break;

                case 3:
                    System.out.println("Multiplikator: ");
                    firstNumber = scanner.nextDouble();
                    System.out.println("Multiplikand: ");
                    secondNumber = scanner.nextDouble();
                    System.out.println(firstNumber + " * " + secondNumber + " = " + multiply(firstNumber, secondNumber));
                    break;

                case 4:
                    System.out.println("Dividend: ");
                    firstNumber = scanner.nextDouble();
                    System.out.println("Divisor: ");
                    secondNumber = scanner.nextDouble();
                    if (secondNumber != 0) {
                        System.out.println(firstNumber + " / " + secondNumber + " = " + divide(firstNumber, secondNumber));
                    } else {
                        System.out.println("Division durch 0 ist nicht erlaubt!");
                    }
                    break;

                case 5:
                    System.out.println("Winkel in Grad: ");
                    firstNumber = scanner.nextDouble();
                    System.out.println("sin(" + firstNumber + ") = " + sinDegrees(firstNumber));
                    break;

                case 6:
                    System.out.println("Basis: ");
                    firstNumber = scanner.nextDouble();
                    System.out.println("Exponent: ");
                    secondNumber = scanner.nextDouble();
                    System.out.println(firstNumber + " ^ " + secondNumber + " = " + power(firstNumber, secondNumber));
                    break;

                case 7:
                    System.out.println("Erste ganze Zahl: ");
                    firstInteger = scanner.nextInt();
                    System.out.println("Zweite ganze Zahl: ");
                    secondInteger = scanner.nextInt();
                    System.out.println("gcd(" + firstInteger + ", " + secondInteger + ") = " + gcd(firstInteger, secondInteger));
                    break;

                case 8:
                    System.out.println("n (ganze Zahl): ");
                    firstInteger = scanner.nextInt();
                    System.out.println("fibonacci(" + firstInteger + ") = " + fibonacci(firstInteger));
                    break;

                default:
                    System.out.println("Ungueltige Auswahl!");
                    break;
            }
        }

        scanner.close();
    }

    // ===== Grundrechenarten (Task 1.3.2) =====

    public static double add(double summand1, double summand2) {
        return summand1 + summand2;
    }

    public static double subtract(double minuend, double subtrahend) {
        return minuend - subtrahend;
    }

    public static double multiply(double multiplier, double multiplicand) {
        return multiplier * multiplicand;
    }

    public static double divide(double dividend, double divisor) {
        return dividend / divisor;
    }

    // ===== Weitere Funktionen (Task 1.3.3) =====

    // Math.sin erwartet Radiant, deshalb zuerst von Grad umrechnen
    public static double sinDegrees(double angleInDegrees) {
        return Math.sin(Math.toRadians(angleInDegrees));
    }

    public static double power(double base, double exponent) {
        return Math.pow(base, exponent);
    }

    // Euklidischer Algorithmus
    public static int gcd(int a, int b) {
        while (b != 0) {
            int rest = a % b;
            a = b;
            b = rest;
        }
        return a;
    }

    // previous und current sind immer die letzten zwei Zahlen der Folge
    public static long fibonacci(int n) {
        long previous = 0;
        long current = 1;
        for (int i = 0; i < n; i++) {
            long next = previous + current;
            previous = current;
            current = next;
        }
        return previous;
    }
}
