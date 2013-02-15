package eu.q_b.asn007.lambda.updater;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JProgressBar;

public class CProgress extends JProgressBar {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2287273761563499477L;
	private int value = 0;
	private int width = getWidth();
	private int height = getHeight();
	private boolean indeterminate = false;
	private Thread startThread;
	private int inArc = 45;

	BasicStroke thin = new BasicStroke(3.0f);
	BasicStroke therd = new BasicStroke(10);
	private Container container;
	private BufferedImage image;

	public CProgress(Container c) {
		this.setPreferredSize(new Dimension(120, 120));
		this.container = c;
		try {
			this.image = ImageIO.read(this.getClass().getResourceAsStream(
					"/eu/q_b/asn007/lambda/updater/icon.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setValue(int value) {
		if (value >= 0 && value <= 100) {
			if (indeterminate) {
				setIndeterminate(false);
			}
			this.value = (int) (value * -3.6);
			repaint();
		}
	}

	@Override
	public void setIndeterminate(boolean in) {
		indeterminate = in;
		if (in) {
			value = 0;
			startThread = new Thread(new StartIndeterminate(this.container));
			startThread.start();
		} else {
			if (startThread != null) {
				inArc = 45;
				startThread.interrupt();
				startThread = null;
			}
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		width = getWidth();
		height = getHeight();
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
				RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		g2d.drawImage(image, width / 2 - image.getWidth() / 2, height / 2
				- image.getHeight() / 2, null);
		g2d.setStroke(therd);
		g2d.setColor(Color.black);
		if (!indeterminate) {
			g2d.drawArc(10, 10, width - 20, height - 20, 90, value);
			g2d.setStroke(thin);
		} else {
			g2d.drawArc(10, 10, width - 20, height - 20, inArc, 45);
			inArc--;
			g2d.setStroke(thin);
		}
		g.dispose();

	}

	@Override
	protected void paintBorder(Graphics g) {

	}

	class StartIndeterminate implements Runnable {
		boolean isContinue = true;
		Container c;

		public StartIndeterminate(Container c) {
			this.c = c;
		}

		public void run() {
			while (isContinue) {
				try {
					Thread.sleep(40 / 10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block

				}
				repaint();
				c.repaint();
			}
		}
	}
}
