import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
public class Main {
    static KnapsackResult knapsackRec(int[] w, int[] v, int n, int W) {
        if (n <= 0) {
            return new KnapsackResult(0, new ArrayList<>());
        } else if (w[n - 1] > W) {
            return knapsackRec(w, v, n - 1, W);
        } else {
            KnapsackResult withoutItem = knapsackRec(w, v, n - 1, W);

            KnapsackResult withItem = knapsackRec(w, v, n - 1, W - w[n - 1]);
            withItem.maxValue += v[n - 1];
            withItem.items.add(n - 1);

            if (withItem.maxValue > withoutItem.maxValue) {
                return withItem;
            } else {
                return withoutItem;
            }
        }
        // сложность Big O(2^N)
    }

    // Динамическое программирование
    static class KnapsackResult {
        int maxValue;
        List<Integer> items;
        public KnapsackResult(int maxValue, List<Integer> items) {
            this.maxValue = maxValue;
            this.items = items;
        }
    }

    static KnapsackResult knapsackDP(int[] w, int[] v, int n, int W) {
        int[][] m = new int[n + 1][W + 1];
        for (int j = 0; j <= W; j++) {
            m[0][j] = 0;
        }
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= W; j++) {
                if (w[i - 1] > j)
                    m[i][j] = m[i - 1][j];
                else
                    m[i][j] = Math.max(m[i - 1][j], m[i - 1][j - w[i - 1]] + v[i - 1]);
            }
        }
        // сложность Big O(W*n)

        // нахождения предметов, которые составляют макс. стоимость
        List<Integer> items = new ArrayList<>();
        List<Integer> q=new ArrayList<>();
        for (int i = n, k = W; i > 0 && k > 0; i--) {
            if (m[i][k] != m[i - 1][k]) {
                items.add(i - 1); // добавляем индекс предмета
                k = k - w[i - 1]; // вычитаем вес предмета из оставшегося веса
                q.add(w[i-1]);
            }
        }
        return new KnapsackResult(m[n][W], items);
    }

    static int knapsackGreedy(int[] w, int[] v, int W, List<Integer> itemsSelected) {
        int n = w.length;
        double[] k = new double[n];
        for (int i = 0; i < n; i++) {
            k[i] = (double) v[i] / w[i];
        }

        // сортировка пузырьком для стоимости
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (k[j] < k[j + 1]) {
                    // Сортировка удельной стоимости
                    double tempCost = k[j];
                    k[j] = k[j + 1];
                    k[j + 1] = tempCost;
                    // Со сортировкой удельной стоимости сортируем веса
                    int tempWeight = w[j];
                    w[j] = w[j + 1];
                    w[j + 1] = tempWeight;
                    // А также значения
                    int tempValue = v[j];
                    v[j] = v[j + 1];
                    v[j + 1] = tempValue;
                }
            }
        }

        int currentWeight = 0; // Текущий вес в рюкзаке
        int currentValue = 0; // Текущая стоимость

        // Добавление предметов в рюкзак
        for (int i = 0; i < n; i++) {
            if (currentWeight + w[i] <= W) {
                // Добавляем целиком, если есть место
                currentWeight += w[i];
                currentValue += v[i];
                itemsSelected.add(i); // Добавляем индекс предмета в список выбранных
            } else {
                // Заканчиваем, если вес следующего предмета не помещается
                break;
            }
        }
        System.out.println(currentWeight+" - теккущий вес рюкзака");
        return currentValue;
    }


    static List<Subject> subjects = new ArrayList<>();

    static class Subject {
        String name;
        int weight;
        int prices;

        public Subject(String name, int weigth, int prices) {
            this.name = name;
            this.weight = weigth;
            this.prices = prices;
        }
    }

    static class Backpack {
        int maxWeigth;

        public void setMaxWeigth(int maxWeigth) {
            this.maxWeigth = maxWeigth;
        }

        public int getMaxWeigth() {
            return maxWeigth;
        }
    }

    public static void main(String[] args) {
        Backpack backpack = new Backpack();
        Scanner in = new Scanner(System.in);
        int variant;
        while (true) {
            System.out.println("\n1. Заполнение списка предметов из файла ");
            System.out.println("2. Добавление предмета ");
            System.out.println("3. Изменение предмета ");
            System.out.println("4. Удаление предмета ");
            System.out.println("5. Задание максимального веса рюкзака ");
            System.out.println("6. Просмотр содержимого рюкзака ");
            System.out.println("7. Выбор способа решения задачи ");
            System.out.println("8. Сравнение способов решения ");

            System.out.print("Выберите пункт меню: ");
            try {
                variant = Integer.parseInt(in.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Неверный выбор");
                continue;
            }
            switch (variant) {
                case 1:
                    System.out.print("Введите имя файла: ");
                    String filename = in.nextLine();
                    File file = new File(filename);

                    try (Scanner fileScanner = new Scanner(file)) {
                        subjects.clear(); // Очищаем список перед заполнением новыми данными
                        while (fileScanner.hasNextLine()) {
                            String line = fileScanner.nextLine();
                            String[] q = line.split(","); // Предполагаем, что данные разделены запятыми
                            if (q.length == 3) {
                                String name = q[0].trim();
                                int weight = Integer.parseInt(q[1].trim());
                                int price = Integer.parseInt(q[2].trim());
                                subjects.add(new Subject(name, weight, price));
                            }
                        }
                        System.out.println("Список предметов заполнен из файла");
                    } catch (FileNotFoundException e) {
                        System.out.println("Файл не найден.");
                    } catch (NumberFormatException e) {
                        System.out.println("Ошибка формата данных в файле.");
                    }
                    break;

                case 2:
                    System.out.println("Введите название предмета:");
                    String name = in.nextLine();
                    System.out.println("Введите вес предмета:");
                    int weight = in.nextInt();
                    System.out.println("Введите ценность предмета:");
                    int price = in.nextInt();
                    in.nextLine(); // Считывает перевод строки после числа

                    subjects.add(new Subject(name, weight, price));
                    break;

                case 3:
                    System.out.println("Укажите индекс предмета, который хотите изменить: ");
                    int k = in.nextInt();
                    in.nextLine(); // Очистка буфера ввода

                    if (k >= 0 && k < subjects.size()) {
                        System.out.println("Введите новое название предмета: ");
                        String newName = in.nextLine();
                        System.out.println("Введите новый вес предмета: ");
                        int newWeight = in.nextInt();
                        System.out.println("Введите новую ценность предмета: ");
                        int newPrice = in.nextInt();
                        in.nextLine(); // Очистка буфера ввода

                        // Получаем предмет по индексу и обновляем его данные
                        Subject subjectToUpdate = subjects.get(k);
                        subjectToUpdate.name = newName;
                        subjectToUpdate.weight = newWeight;
                        subjectToUpdate.prices = newPrice;

                        System.out.println("Предмет под индексом " + k + " был успешно изменен.");
                    } else {
                        System.out.println("Предмета с таким индексом не существует. Попробуйте снова.");
                    }
                    break;

                case 4:
                    System.out.println("Укажите индекс предмета который хотите удалить: ");
                    int index = in.nextInt();
                    if (index >= 0 && index < subjects.size()) {
                        subjects.remove(index);
                        System.out.println("Предмет под индексом " + index + " был успешно удален.");

                        // Выведем обновленный список предметов
                        for (int i = 0; i < subjects.size(); i++) {
                            System.out.println((i + 1) + ". " + subjects.get(i).name);
                        }
                    } else {
                        System.out.println("Предмет с таким индексом не существует");
                    }
                    break;

                case 5:
                    System.out.println("Введите максимальный вес рюкзака ");
                    int l = in.nextInt();
                    backpack.setMaxWeigth(l);
                    break;

                case 6:
                    System.out.println("Список предметов: ");
                    String[] arr = new String[subjects.size()];
                    for (int i = 0; i < arr.length; i++) {
                        arr[i] = subjects.get(i).name;
                        System.out.println((i + 1) + ". " + arr[i]);
                    }
                    break;

                case 7:
                    // Сначала проверяем, задан ли максимальный вес рюкзака
                    int W = backpack.getMaxWeigth();
                    if (W == 0) {
                        System.out.println("Сначала установите максимальный вес рюкзака в пункте 5 ");
                        break;
                    }
                    int n = subjects.size();
                    int[] w = new int[n];
                    int[] v = new int[n];

                    // Заполняем массивы данными из списка предметов
                    for (int i = 0; i < subjects.size(); i++) {
                        w[i] = subjects.get(i).weight;
                        v[i] = subjects.get(i).prices;
                    }
                    KnapsackResult result, res;
                    int var;
                    boolean q = true;
                    while (q) {
                        System.out.println("\n1. Решение рекурсивным методом ");
                        System.out.println("2. Решение Динамическим программированием: ");
                        System.out.println("3. Жадный алгоритм ");
                        System.out.println("4. Назад ");

                        System.out.print("Выберите пункт меню: ");
                        try {
                            var = Integer.parseInt(in.nextLine());
                        } catch (NumberFormatException e) {
                            System.out.println("Неверный выбор");
                            continue;
                        }
                        switch (var) {
                            case 1:
                                res = knapsackRec(w, v, n, W);
                                System.out.println("Решение задачи рекурсивным методом:");
                                System.out.println("Максимальная стоимость: " + res.maxValue);
                                System.out.println("Предметы, которые вошли в рюкзак:");
                                for (Integer item : res.items) {
                                    System.out.println(subjects.get(item).name+", вес: " + w[item] + ", ценность: " + v[item]);
                                }
                                break;

                            case 2:
                                result = knapsackDP(w, v, n, W);
                                System.out.println("Решение задачи методом динамического программирования:");
                                System.out.println("Максимальная стоимость: " + result.maxValue);
                                System.out.println("Предметы в рюкзаке по индексам: " + result.items);
                                for (int idx : result.items) {
                                    System.out.println(subjects.get(idx).name + ", вес  - "+subjects.get(idx).weight);
                                }
                                break;

                            case 3:
                                List<Integer> itemsSelected = new ArrayList<>(); // Список для сохранения индексов выбранных предметов
                                int maxValue = knapsackGreedy(w, v, W, itemsSelected);

                                System.out.println("Максимальная стоимость, которую можно унести: " + maxValue);
                                System.out.print("Предметы, вошедшие в рюкзак: ");
                                for (int i : itemsSelected) {
                                    System.out.print(subjects.get(i).name + " ");
                                }
                                break;
                            case 4:
                                System.out.println("Назад");
                                q = false;
                                break;
                        }
                    }
                case 8:
                    W = backpack.getMaxWeigth();
                    if (W == 0) {
                        System.out.println("Сначала установите максимальный вес рюкзака в пункте 5 ");
                        break;
                    }
                    n = subjects.size();
                    w = new int[n];
                    v = new int[n];

                    // Заполняем массивы данными из списка предметов
                    for (int i = 0; i < subjects.size(); i++) {
                        w[i] = subjects.get(i).weight;
                        v[i] = subjects.get(i).prices;
                    }
                    long startTime, endTime, absoluteTime;

                    startTime = System.nanoTime();
                    KnapsackResult recResult = knapsackRec(w, v, n, W);
                    endTime = System.nanoTime();
                    absoluteTime = endTime - startTime;
                    System.out.println("Решение рекурсивным методом: " + recResult.maxValue);
                    System.out.println("Время выполнения : " + absoluteTime);

                    startTime = System.nanoTime();
                    KnapsackResult dpResult = knapsackDP(w, v, n, W);
                    endTime = System.nanoTime();
                    absoluteTime = endTime - startTime;
                    System.out.println("Решение методом динамического программирования: " + dpResult.maxValue);
                    System.out.println("Время выполнения : " + absoluteTime);

                    List<Integer> greedyItemsSelected = new ArrayList<>();
                    startTime = System.nanoTime();
                    int greedyResult = knapsackGreedy(w, v, W, greedyItemsSelected);
                    endTime = System.nanoTime();
                    absoluteTime = endTime - startTime;
                    System.out.println("Решение жадным методом: " + greedyResult);
                    System.out.println("Время выполнения : " + absoluteTime);
                    break;


                default:
                    System.out.println("Вы ввели неверный пункт меню\nПопробуйте снова ");
            }
        }
    }
}
