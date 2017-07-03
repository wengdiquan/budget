package text;

import java.util.Objects;

public class TestMain {
	public static void main(String[] args) {
		System.out.println(Objects.isNull("1"));
		String tt = "FC-2004004++GJCCJ-99999";
		String[] xx = tt.split("\\+");
	}
}
