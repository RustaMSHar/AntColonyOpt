import java.util.Arrays;

public class main {

	public static void main(String[] args) {

	    int[] set = {3, 1, 4, 2, 2, 1};  // The set to be partitioned
	    int targetSum = Arrays.stream(set).sum() / 2;  // The target sum for each subset

	    AntColony ac = new AntColony(10, 2.0, 2.0, 0.5, 0.8, 100);
	    int[] solution = ac.run(set, targetSum);

	    System.out.println(Arrays.toString(solution));

	}

}
