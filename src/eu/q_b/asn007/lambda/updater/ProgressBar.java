package eu.q_b.asn007.lambda.updater;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;

import javax.swing.JProgressBar;

public class ProgressBar extends JProgressBar {

	/**
	 * 
	 */

	private BufferedImage progressImage;
	private BufferedImage progressBg;
	private static final long serialVersionUID = 6639316373716299344L;

	public ProgressBar(BufferedImage s, BufferedImage a) {
		this.progressBg = a;
		this.progressImage = s;
	}

	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		g2d.drawImage(progressBg, 0, 0, null);
		g2d.drawImage(
				getScaledImage(
						progressImage,
						progressBg.getWidth(null),
						progressBg.getHeight()
								- ((int) (getValue()
										* progressBg.getHeight(null) / getMaximum()))),
				0, 0, null);

	}

	public boolean isOpaque() {
		return false;
	}

	@SuppressWarnings("unused")
	private Image getScaledShape(Shape s, int desiredWidth, int desiredHeight,
			Color c) {
		BufferedImage buff = new BufferedImage(s.getBounds().width,
				s.getBounds().height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) buff.getGraphics();
		g.setColor(c);
		g.fill(s);
		return createImage(new FilteredImageSource(buff.getSource(),
				new CropImageFilter(0, 0, desiredWidth, desiredHeight)));
	}

	private Image getScaledImage(BufferedImage s, int desiredWidth,
			int desiredHeight) {
		return createImage(new FilteredImageSource(s.getSource(),
				new CropImageFilter(0, 0, desiredWidth, desiredHeight)));
	}

	@SuppressWarnings("unused")
	private Shape transformShape(Shape s, int desiredWidth, int desiredHeight) {
		AffineTransform trans = new AffineTransform();
		trans.scale(desiredWidth, desiredHeight);
		return trans.createTransformedShape(s);
	}
}
