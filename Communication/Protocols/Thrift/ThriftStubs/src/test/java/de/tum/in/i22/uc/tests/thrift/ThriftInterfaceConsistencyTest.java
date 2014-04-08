package de.tum.in.i22.uc.tests.thrift;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import de.tum.in.i22.uc.thrift.types.TAny2Any;
import de.tum.in.i22.uc.thrift.types.TAny2Pdp;
import de.tum.in.i22.uc.thrift.types.TAny2Pip;
import de.tum.in.i22.uc.thrift.types.TAny2Pmp;



public class ThriftInterfaceConsistencyTest {
	@Test
	public void testThriftInterfaceConsistency() throws Exception {
		Set<MethodWrapper> any2anyMethods = toMethodWrapperSet(Arrays.asList(TAny2Any.Iface.class.getDeclaredMethods()));

		Set<MethodWrapper> any2pdpMethods = toMethodWrapperSet(Arrays.asList(TAny2Pdp.Iface.class.getDeclaredMethods()));
		Set<MethodWrapper> any2pipMethods = toMethodWrapperSet(Arrays.asList(TAny2Pip.Iface.class.getDeclaredMethods()));
		Set<MethodWrapper> any2pmpMethods = toMethodWrapperSet(Arrays.asList(TAny2Pmp.Iface.class.getDeclaredMethods()));


		/*
		 * Test whether all methods declared in TAny2Pdp
		 * are also declared in TAny2Any
		 */
		for (MethodWrapper m : any2pdpMethods) {
			System.out.print("Testing " + m + " ... ");
			Assert.assertTrue(any2anyMethods.contains(m));
			System.out.println("ok");
		}
		System.out.println();


		/*
		 * Test whether all methods declared in TAny2Pip
		 * are also declared in TAny2Any
		 */
		for (MethodWrapper m : any2pipMethods) {
			System.out.print("Testing " + m + " ... ");
			Assert.assertTrue(any2anyMethods.contains(m));
			System.out.println("ok");
		}
		System.out.println();


		/*
		 * Test whether all methods declared in TAny2Pmp
		 * are also declared in TAny2Any
		 */
		for (MethodWrapper m : any2pmpMethods) {
			System.out.print("Testing " + m + " ... ");
			Assert.assertTrue(any2anyMethods.contains(m));
			System.out.println("ok");
		}
		System.out.println();


		/*
		 * Test whether all methods declared in TAny2Any
		 * are declared in of the other TAny2P*p
		 */
		for (MethodWrapper m : any2anyMethods) {
			System.out.print("Testing " + m + " ... ");
			Assert.assertTrue(any2pdpMethods.contains(m)
								|| any2pipMethods.contains(m)
								|| any2pmpMethods.contains(m));
			System.out.println("ok");
		}
	}


	/*
	 * We need this wrapper class because
	 * - java.lang.reflect.Method.equals() compares the declaring class,
	 * 		which is irrelevant for us
	 * - java.lang.reflect.Method is final and can not be extended
	 */
	public static Set<MethodWrapper> toMethodWrapperSet(Collection<Method> methods) {
		if (methods == null || methods.size() == 0) {
			return Collections.emptySet();
		}

		Set<MethodWrapper> result = new HashSet<>();

		for (Method m : methods) {
			result.add(new MethodWrapper(m));
		}

		return result;
	}


}

class MethodWrapper {
	private final Method _method;

	public MethodWrapper(Method m) {
		_method = m;
	}

	@Override
	public boolean equals(Object obj) {
		/*
		 * Different to the original equals method
		 * of java.lang.reflect.Method, we do not
		 * compare the declaring class, as this is the very point
		 * of this consistency check.
		 */
		if (obj != null && obj instanceof MethodWrapper) {
			Method other = ((MethodWrapper) obj)._method;
			if (_method.getName() != other.getName()
					|| !_method.getReturnType().equals(other.getReturnType())) {
				return false;
			}


			Class<?>[] params1 = _method.getParameterTypes();
			Class<?>[] params2 = other.getParameterTypes();
			if (params1.length == params2.length) {
				for (int i = 0; i < params1.length; i++) {
					if (params1[i] != params2[i])
						return false;
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return _method.getName().hashCode();
	}

	@Override
	public String toString() {
		return _method.toString();
	}
}
