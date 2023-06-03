

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class AntColony {
	
	// Инициализируем переменные класса
    private ArrayList<int[]> solutions; // Список, где будем хранить решения
    private int numAnts; // Количество муравьев в колонии
    private double alpha; // Коэффициент для влияния феромонов
    private double beta; // Коэффициент для влияния эвристики
    private double rho; // Коэффициент испарения феромонов
    private double q0; // Вероятность выбора жадной стратегии
    private double pheromone[];  // Массив феромонов для каждого элемента
    private int numIterations; // Количество итераций алгоритма

    // Конструктор для инициализации переменных класса
    public AntColony(int numAnts, double alpha, double beta, double rho, double q0, int numIterations) {
        this.numAnts = numAnts;
        this.alpha = alpha;
        this.beta = beta;
        this.rho = rho;
        this.q0 = q0;
        this.numIterations = numIterations;
        this.solutions = new ArrayList<>();
    }
    
    // Метод для запуска алгоритма муравьиной колонии и возвращения лучшего найденного решения
    public int[] run(int[] set, int targetSum) {

    	// Инициализируем уровни феромонов
        pheromone = new double[set.length];
        Arrays.fill(pheromone, 1.0);

        int[] bestSolution = null;
        int bestValue = Integer.MAX_VALUE;

        // Запускаем алгоритм муравьиной колонии заданное количество раз
        for (int i = 0; i < numIterations; i++) {

        	// Проходимся муравьями и генерируем решения
            for (int j = 0; j < numAnts; j++) {

                int[] solution = constructSolution(set, targetSum);
                int value = evaluateSolution(solution, set, targetSum);

                // Обновляем лучшее найденное решение
                if (value < bestValue) {
                    bestValue = value;
                    bestSolution = solution;
                }

                // Обновляем уровни феромонов
                updatePheromones(solution, value);

            }

            // Обновляем уровни феромонов за счет испарения	
            evaporatePheromones();

        }

        // Возвращаем лучшее найденное решение
        return bestSolution;

    }

    
    // Метод для генерации решения муравьем
    private int[] constructSolution(int[] set, int targetSum) {

        int[] solution = new int[set.length];

        // Выбираем первый элемент случайным образом
        Random rand = new Random();
        int current = rand.nextInt(set.length);
        solution[current] = 1;

        // Выбираем следующие элементы с помощью вероятностного распределения на основе уровней феромонов и эвристики
        for (int i = 1; i < set.length; i++) {
            double randValue = rand.nextDouble();

            if (randValue < q0) {
                current = greedySelect(set, solution, current, targetSum);
            } else {
                current = probabilisticSelect(set, solution, current, targetSum);
            }

            solution[current] = 1;
        }

        return solution;

    }
    
    // Метод для выбора следующего элемента при генерации решения с помощью жадной стратегии
    private int greedySelect(int[] set, int[] solution, int current, int targetSum) {

        int best = current;
        double bestValue = Double.NEGATIVE_INFINITY;

        // Вычисляем значение каждого возможного элемента и выбираем тот, у которого максимальное значение
        for (int i = 0; i < set.length; i++) {
            if (solution[i] == 0) {
                double value = pheromone[i] * Math.pow(Math.abs(set[current] + set[i] - targetSum), -beta);
                if (value > bestValue) {
                    bestValue = value;
                    best = i;
                }
            }
        }

        return best;

    }

    // Метод для выбора следующего элемента при генерации решения с помощью вероятностной стратегии
    private int probabilisticSelect(int[] set, int[] solution, int current, int targetSum) {

        double total = 0.0;
        double[] probabilities = new double[set.length];
        
        // Вычисляем вероятность каждого возможного элемента и выбираем один на основе вероятности
        for (int i = 0; i < set.length; i++) {
            if (solution[i] == 0) {
                double value = pheromone[i] * Math.pow(Math.abs(set[current] + set[i] - targetSum), -beta);
                probabilities[i] = Math.pow(value, alpha);
                total += probabilities[i];
            }
        }

        Random rand = new Random();
        double randValue = rand.nextDouble() * total;

        for (int i = 0; i < set.length; i++) {
            if (solution[i] == 0) {
                randValue -= probabilities[i];
                if (randValue <= 0) {
                    return i;
                }
            }
        }

        
        return current;

    }

    // Метод для оценки решения
    private int evaluateSolution(int[] solution, int[] set, int targetSum) {

        int sum1 = 0;
        int sum2 = 0;

        
        // Вычисляем сумму элементов в каждом подмножестве
        for (int i = 0; i < set.length; i++) {
            if (solution[i] == 1) {
                sum1 += set[i];
            } else {
                sum2 += set[i];
            }
        }
        // Возвращаем абсолютную разницу между суммами подмножеств
        return Math.abs(sum1 - sum2);

    }

    // Метод для обновления уровней феромонов
    private void updatePheromones(int[] solution, int value) {

        double delta = 1.0 / value;
        // Обновляем уровни феромонов для каждого элемента в решении
        for (int i = 0; i < solution.length; i++) {
            if (solution[i] == 1) {
                pheromone[i] = (1 - rho) * pheromone[i] + rho * delta;
            }
        }

    }
    // Метод для испарения уровней феромонов
    private void evaporatePheromones() {
    	
    	// Испаряем уровни феромонов для каждого элемента
        for (int i = 0; i < pheromone.length; i++) {
            pheromone[i] *= (1 - rho);
        }

    }
    // Метод для получения всех найденных решений
    public ArrayList<int[]> getSolutions() {
        return solutions;
    }

}