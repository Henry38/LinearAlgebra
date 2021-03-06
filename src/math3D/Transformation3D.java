package math3D;

import math4D.Matrix4D;
import math4D.Vecteur4D;

public class Transformation3D {
	
	protected Matrix4D matrix;
	
	/** Constructeur */
	public Transformation3D(Matrix4D matrix) {
		this.matrix = new Matrix4D(matrix);
	}
	
	/** Constructeur par copie */
	public Transformation3D(Transformation3D t) {
		this(t.matrix);
	}
	
	/** Constructeur par defaut */
	public Transformation3D() {
		this(new Matrix4D.Identity());
	}
	
	/** Retourne la transformation inverse */
	public Transformation3D getInverseTransformation() {
		return new Transformation3D(matrix.getInverse());
	}
	
	/** Calcule le resultat de la transformation du point (x,y,z) */
	public Point3D transform(double x, double y, double z) {
		Vecteur4D hp = new Vecteur4D(x, y, z, 1);
		Vecteur4D r = matrix.mult(hp);
		return new Point3D(r.getDx()/r.getDw(), r.getDy()/r.getDw(), r.getDz()/r.getDw());
	}
	
	/** Calcule le resultat de la transformation du Point3D p */
	public Point3D transform(Point3D p) {
		return transform(p.getX(), p.getY(), p.getZ());
	}
	
	/** Revient sur la transformation identite */
	public void clear() {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				double d = (i == j ? 1 : 0);
				matrix.set(i, j, d);
			}
		}
	}
	
	/** Ajoute une translation a la transformation */
	public void addTranslation(double dx, double dy, double dz) {
		matrix.set(0, 3, matrix.get(0, 3) + dx);
		matrix.set(1, 3, matrix.get(1, 3) + dy);
		matrix.set(2, 3, matrix.get(2, 3) + dz);
	}
	
	/** Ajoute une translation a la transformation */
	public void addTranslation(Vecteur3D vect) {
		addTranslation(vect.getDx(), vect.getDy(), vect.getDz());
	}
	
	/** Ajoute une translation a la transformation */
	public void addTranslation(Point3D p) {
		addTranslation(p.getX(), p.getY(), p.getZ());
	}
	
	/** Ajoute une rotation a la transformation */
	public void addRotation(Vecteur3D axis, double radian) {
		double cos = Math.cos(radian);
		double sin = Math.sin(radian);
		double x = matrix.get(0, 3);
		double y = matrix.get(1, 3);
		double z = matrix.get(2, 3);
		double d = axis.getNorm();
		double dx = axis.getDx() / d;
		double dy = axis.getDy() / d;
		double dz = axis.getDz() / d;
		double mc = 1 - cos;
		// Matrice de rotation
		double[][] r = new double[][] {
				{dx*dx*mc + cos		, dx*dy*mc - dz*sin	, dx*dz*mc + dy*sin	},
				{dx*dy*mc + dz*sin	, dy*dy*mc + cos	, dy*dz*mc - dx*sin	},
				{dx*dz*mc - dy*sin	, dy*dz*mc + dx*sin	, dz*dz*mc + cos	}
		};
		// Rotation de l'origine
		double m03 = r[0][0] * x + r[0][1] * y + r[0][2] * z;
		double m13 = r[1][0] * x + r[1][1] * y + r[1][2] * z;
		double m23 = r[2][0] * x + r[2][1] * y + r[2][2] * z;
		matrix.set(0, 3, m03);
		matrix.set(1, 3, m13);
		matrix.set(2, 3, m23);
		// Rotation des axes
		double m00 = r[0][0] * matrix.get(0, 0) + r[0][1] * matrix.get(1, 0) + r[0][2] * matrix.get(2, 0);
		double m01 = r[0][0] * matrix.get(0, 1) + r[0][1] * matrix.get(1, 1) + r[0][2] * matrix.get(2, 1);
		double m02 = r[0][0] * matrix.get(0, 2) + r[0][1] * matrix.get(1, 2) + r[0][2] * matrix.get(2, 2);
		double m10 = r[1][0] * matrix.get(0, 0) + r[1][1] * matrix.get(1, 0) + r[1][2] * matrix.get(2, 0);
		double m11 = r[1][0] * matrix.get(0, 1) + r[1][1] * matrix.get(1, 1) + r[1][2] * matrix.get(2, 1);
		double m12 = r[1][0] * matrix.get(0, 2) + r[1][1] * matrix.get(1, 2) + r[1][2] * matrix.get(2, 2);
		double m20 = r[2][0] * matrix.get(0, 0) + r[2][1] * matrix.get(1, 0) + r[2][2] * matrix.get(2, 0);
		double m21 = r[2][0] * matrix.get(0, 1) + r[2][1] * matrix.get(1, 1) + r[2][2] * matrix.get(2, 1);
		double m22 = r[2][0] * matrix.get(0, 2) + r[2][1] * matrix.get(1, 2) + r[2][2] * matrix.get(2, 2);
		matrix.set(0, 0, m00);
		matrix.set(0, 1, m01);
		matrix.set(0, 2, m02);
		matrix.set(1, 0, m10);
		matrix.set(1, 1, m11);
		matrix.set(1, 2, m12);
		matrix.set(2, 0, m20);
		matrix.set(2, 1, m21);
		matrix.set(2, 2, m22);
	}
	
	/** Ajoute une homothetie a la transformation */
	public void addScale(double sx, double sy, double sz) {
		matrix.set(0, 0, sx * matrix.get(0, 0));
		matrix.set(1, 1, sy * matrix.get(1, 1));
		matrix.set(2, 2, sz * matrix.get(2, 2));
	}
	
	/** Combine la transformation courante avec la transformation t */
	public void compose(Transformation3D t) {
		Matrix4D m = matrix.mult(t.matrix);
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				matrix.set(i, j, m.get(i, j));
			}
		}
	}
	
	/** Combine les deux transformations t1 et t2 */
	public static Transformation3D compose(Transformation3D t1, Transformation3D t2) {
		Transformation3D t = new Transformation3D(t1);
		t.compose(t2);
		return t;
	}
	
	/** Representation textuelle d'une transformation */
	@Override
	public String toString() {
		return matrix.toString();
	}
}
