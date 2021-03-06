package de.tum.in.i22.uc.cm.datatypes.basic;

import de.tum.in.i22.uc.cm.datatypes.interfaces.DeepCloneable;

/**
 * Three-valued boolean logic: true, false, undef.
 *
 * @author Florian Kelbert
 *
 */
public enum Trilean implements DeepCloneable<Trilean> {
	TRUE {
		@Override
        public boolean value() {
                return true;
        }

		@Override
		public boolean is(boolean b) {
			return b;
		}

		@Override
		public Trilean negate() {
			return Trilean.FALSE;
		}

		@Override
		public boolean isBool() {
			return true;
		}

		@Override
		public Trilean deepClone() {
			return TRUE;
		}
	},

	FALSE {
		@Override
        public boolean value() {
                return false;
        }

		@Override
		public boolean is(boolean b) {
			return !b;
		}

		@Override
		public Trilean negate() {
			return Trilean.TRUE;
		}

		@Override
		public boolean isBool() {
			return true;
		}

		@Override
		public Trilean deepClone() {
			return FALSE;
		}
	},

	UNDEF {
		@Override
        public boolean value() {
                throw new UnsupportedOperationException("Unable to convert UNDEF to boolean.");
        }

		@Override
		public boolean is(boolean b) {
			return false;
		}

		@Override
		public Trilean negate() {
			return Trilean.UNDEF;
		}

		@Override
		public boolean isBool() {
			return false;
		}

		@Override
		public Trilean deepClone() {
			return UNDEF;
		}
	};


	/**
	 * Tests whether this Enum is the same as
	 * the specified boolean value.
	 * For {@link Trilean#UNDEF}, this result is false.
	 *
	 * @param b the boolean with which to compare
	 * @return true, if this {@link Trilean} encodes the same value.
	 */
	public abstract boolean is(boolean b);

	public abstract boolean isBool();

	public abstract boolean value();

	public abstract Trilean negate();
}