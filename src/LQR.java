import org.ejml.simple.SimpleMatrix;

public class LQR {
    private SimpleMatrix A, B, Q, R, K;

    public LQR(SimpleMatrix A, SimpleMatrix B, SimpleMatrix Q, SimpleMatrix R) {
        this.A = A;
        this.B = B;
        this.Q = Q;
        this.R = R;
        this.K = computeK();
    }

    private SimpleMatrix computeK() {
        SimpleMatrix P = Q.copy();
        SimpleMatrix K;

        // Iterative approach to solve the continuous-time algebraic Riccati equation
        for (int i = 0; i < 100; i++) {
            SimpleMatrix P_next = A.transpose().mult(P).mult(A).minus(
                    A.transpose().mult(P).mult(B).mult(
                            (R.plus(B.transpose().mult(P).mult(B))).invert()).mult(
                            B.transpose().mult(P).mult(A))).plus(Q);
            P = P_next;
        }

        K = (R.plus(B.transpose().mult(P).mult(B))).invert().mult(B.transpose().mult(P).mult(A));
        return K;
    }

    public SimpleMatrix update(SimpleMatrix state) {
        return K.mult(state).negative();
    }
}
