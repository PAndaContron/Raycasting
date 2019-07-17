package raycasting.math;

public class Matrix3D {
	public static final Matrix3D IDENTITY = new Matrix3D(new double[][] {
		{1, 0, 0},
		{0, 1, 0},
		{0, 0, 1},
	});
	
	private double[][] values = new double[3][3];
	
	public Matrix3D(double[][] initValues) {
		if(initValues.length != 3 || initValues[0].length != 3) {
			throw new IllegalArgumentException("Matrix must be initialized with 3x3 array");
		}
		
		for(int r=0; r<3; r++) {
			for(int c=0; c<3; c++) {
				values[r][c] = initValues[r][c];
			}
		}
	}
	
	public double get(int row, int col) {
		return values[row][col];
	}
	
	public double determinant() {
		return get(0, 0)*get(1, 1)*get(2, 2) + get(0, 1)*get(1, 2)*get(2, 0) + get(0, 2)*get(1, 0)*get(2, 1) - 
				(get(0, 0)*get(1, 2)*get(2, 1) + get(0, 1)*get(1, 0)*get(2, 2) + get(0, 2)*get(1, 1)*get(2, 0));
	}
	
	public double minorDeterminant(int row, int col) {
		int r1 = Math.min((row+1)%3, (row+2)%3);
		int r2 = Math.max((row+1)%3, (row+2)%3);
		int c1 = Math.min((col+1)%3, (col+2)%3);
		int c2 = Math.max((col+1)%3, (col+2)%3);
		
		return get(r1, c1)*get(r2, c2) - get(r1, c2)*get(r2, c1);
	}
	
	public Matrix3D transpose() {
		double[][] newValues = new double[3][3];
		for(int r=0; r<3; r++) {
			for(int c=0; c<3; c++) {
				newValues[r][c] = values[c][r];
			}
		}
		return new Matrix3D(newValues);
	}
	
	public Matrix3D adjugate() {
		Matrix3D transposed = transpose();
		double[][] newValues = new double[3][3];
		for(int r=0; r<3; r++) {
			for(int c=0; c<3; c++) {
				newValues[r][c] = transposed.minorDeterminant(r, c);
			}
		}
		
		newValues[0][1] *= -1;
		newValues[1][0] *= -1;
		newValues[1][2] *= -1;
		newValues[2][1] *= -1;
		
		return new Matrix3D(newValues);
	}
	
	public Matrix3D inverse() {
		return adjugate().scalarMultiply(1/determinant());
	}
	
	public Vector3D getRow(int row) {
		return new Vector3D(get(row, 0), get(row, 1), get(row, 2));
	}
	
	public Vector3D getCol(int col) {
		return new Vector3D(get(0, col), get(1, col), get(2, col));
	}
	
	public Matrix3D scalarMultiply(double scalar) {
		double[][] newValues = new double[3][3];
		for(int r=0; r<3; r++) {
			for(int c=0; c<3; c++) {
				newValues[r][c] = values[r][c] * scalar;
			}
		}
		return new Matrix3D(newValues);
	}
	
	public Matrix3D add(Matrix3D other) {
		double[][] newValues = new double[3][3];
		for(int r=0; r<3; r++) {
			for(int c=0; c<3; c++) {
				newValues[r][c] = values[r][c] + other.get(r, c);
			}
		}
		return new Matrix3D(newValues);
	}
	
	public Matrix3D sub(Matrix3D other) {
		double[][] newValues = new double[3][3];
		for(int r=0; r<3; r++) {
			for(int c=0; c<3; c++) {
				newValues[r][c] = values[r][c] - other.get(r, c);
			}
		}
		return new Matrix3D(newValues);
	}
	
	public Matrix3D mult(Matrix3D other) {
		double[][] newValues = new double[3][3];
		
		for(int r=0; r<3; r++) {
			for(int c=0; c<3; c++) {
				Vector3D row = this.getRow(r);
				Vector3D col = other.getCol(c);
				newValues[r][c] = row.dot(col);
			}
		}
		
		return new Matrix3D(newValues);
	}
	
	public Vector3D mult(Vector3D vector) {
		return new Vector3D(getRow(0).dot(vector), getRow(1).dot(vector), getRow(2).dot(vector));
	}
}
