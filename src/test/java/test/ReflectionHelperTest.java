package test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ReflectionHelperTest {

	private ReflectionHelper helper = new ReflectionHelper();

	@Test
	public void testGetValueForField() throws Exception {
		assertEquals(helper, helper.getValueForField(this, "helper"));
	}

	@Test
	public void testGetValueForPath() throws Exception {
		C c = new C("toto");
		B b = new B();
		b.c = c;
		A a = new A();
		a.b = b;

		assertEquals("toto", helper.getValueForPath(a, "b.c.val"));
	}

	static class A {
		B b;
	}

	static class B {
		C c;
	}

	static class C {
		private final String val;

		public C(String val) {
			super();
			this.val = val;
		}

		public String getVal() {
			return val;
		}

	}

}
