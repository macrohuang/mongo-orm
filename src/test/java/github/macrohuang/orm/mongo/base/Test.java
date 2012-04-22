package github.macrohuang.orm.mongo.base;


public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String[] t = new String[] { "abc", "123" };
		System.out.println(t.getClass() == (new String[0]).getClass());
		System.out.println(t.getClass());
		System.out.println((new Object[0]).getClass());
	}

}
