package de.tum.in.i22.uc.cm.datatypes.basic;

/**
 * Three-valued boolean logic: true, false, undef.
 *
 * @author Florian Kelbert
 *
 */
public enum Trilean {
	TRUE {
		@Override
        public boolean value() {
                return true;
        }

		@Override
		public boolean is(boolean b) {
			return b == true;
		}

		@Override
		public Trilean negate() {
			return Trilean.FALSE;
		}
	},

	FALSE {
		@Override
        public boolean value() {
                return false;
        }

		@Override
		public boolean is(boolean b) {
			return b == false;
		}

		@Override
		public Trilean negate() {
			return Trilean.TRUE;
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
	};


	public abstract boolean is(boolean b);

	public abstract boolean value();

	public abstract Trilean negate();
}