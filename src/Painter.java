import java.awt.Graphics;
import javax.swing.JComponent;
import java.util.List;

public class Painter extends JComponent {
	private List<Algo> algos;

	public Painter(List<Algo> algos) {
		this.algos = algos;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		for (Algo algo : algos) {
			algo.paint(g);
		}
	}
}
