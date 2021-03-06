package eu.q_b.asn007.lambda.updater;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;

import javax.swing.JProgressBar;

public class ProgressBar extends JProgressBar {
	private BufferedImage progressImage;
	private BufferedImage progressBg;
	private static final long serialVersionUID = 6639316373716299344L;

	public ProgressBar(BufferedImage s, BufferedImage a) {
		this.progressBg = a;
		this.progressImage = s;
	}

	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		g2d.drawImage(this.progressBg, 0, 0, null);
		g2d.drawImage(
				getScaledImage(this.progressImage,
						this.progressBg.getWidth(null),
						this.progressBg.getHeight() - getValue()
								* this.progressBg.getHeight(null)
								/ getMaximum()), 0, 0, null);
	}

	public boolean isOpaque() {
		return false;
	}

	private Image getScaledImage(BufferedImage s, int desiredWidth,
			int desiredHeight) {
		return createImage(new FilteredImageSource(s.getSource(),
				new CropImageFilter(0, 0, desiredWidth, desiredHeight)));
	}
}
