package routes;

public class Test {

	public static void main(String[] args) {

		try {
			doSth();
			System.out.println("Bla 1");
		} catch (Exception e) {
			System.out.println("Bla2");
		}

		System.out.println("Bla bla");

	}

	private static void doSth() throws Exception {
		try {
			doSthElse();
		} catch (Exception e) {
			throw e;
		}
		System.out.println("Bla doSth");
	}

	private static void doSthElse() throws Exception {
		throw new Exception();
	}

}
