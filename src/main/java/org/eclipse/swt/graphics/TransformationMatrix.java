package org.eclipse.swt.graphics;

public class TransformationMatrix {
	public double a, b, c, d, e, f;

	public static TransformationMatrix getTranslation(double x, double y) {
		TransformationMatrix t = new TransformationMatrix();
		t.a = 1;
		t.d = 1;
		t.e = x;
		t.f = y;
		return t;
	}

	public static TransformationMatrix getRotation(double deg) {
		double a = Math.toRadians(-deg);
		TransformationMatrix t = new TransformationMatrix();
		double cosa = Math.cos(a);
		double sina = Math.sin(a);

		t.a = cosa;
		t.b = sina;
		t.c = -sina;
		t.d = cosa;

		return t;
	}

	/**
	 * Matrix multiplication
	 * 
	 * @param newRotation
	 * @return
	 */
	public TransformationMatrix times(TransformationMatrix that) {
		TransformationMatrix t = new TransformationMatrix();
		t.a = this.a * that.a + this.c * that.b;
		t.b = this.b * that.a + this.d * that.b;
		t.c = this.a * that.c + this.c * that.d;
		t.d = this.b * that.c + this.d * that.d;
		t.e = this.a * that.e + this.c * that.f + this.e;
		t.f = this.b * that.e + this.d * that.f + this.f;

		return t;
	}

	public void transformPoint(Point p) {
		// double x = a * p.x + c * p.y + e;
		// double y = b * p.x + d * p.y + f;
		// p.x = x;
		// p.y = y;
	}

	void invert() {
		// adapted from AWT.AffineTransform
		double M00, M01, M02;
		double M10, M11, M12;
		double det;
		M00 = a;
		M01 = c;
		M02 = e;
		M10 = b;
		M11 = d;
		M12 = f;

		det = M00 * M11 - M01 * M10;

		if (Math.abs(det) <= Double.MIN_VALUE) {
			throw new RuntimeException("Determinant is " + det);
		}

		a = M11 / det;
		b = -M10 / det;
		c = -M01 / det;
		d = M00 / det;
		e = (M01 * M12 - M11 * M02) / det;
		f = (M10 * M02 - M00 * M12) / det;
	}

	@Override
	public String toString() {
		return "[" + a + "," + c + "," + e + "]\n[" + b + "," + d + "," + f
				+ "]";
	}
}
