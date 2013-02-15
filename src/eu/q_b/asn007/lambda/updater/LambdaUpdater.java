package eu.q_b.asn007.lambda.updater;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import com.sun.awt.AWTUtilities;

public class LambdaUpdater {

	private JFrame frame;

	private File launcherFile = new File(Utils.getWorkingDirectory()
			+ File.separator + "launcher.jar");
	private CProgress progressCircle;
	private Thread thread = new Thread() {
		public void run() {
			if (needsUpdate())
				update();
			launchLauncher();
		}

		private void update() {
			try {
				Utils.download(
						new URL(Utils
								.runGET("http://lambda.q-b.eu/api/api.php",
										"act=launcherurl").trim()
								.replace("\n", "")), launcherFile,
						progressCircle);
			} catch (Exception e) {
				JOptionPane
						.showMessageDialog(
								null,
								"Whoops... LambdaUpdater could not update the launcher. If error persists, restart the launcher and show developer this log:\n"
										+ stack2string(e),
								"ERROR: FAILED TO START LAUNCHER",
								JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
				System.exit(0);
			}

		}

		private boolean needsUpdate() {
			String s = Utils
					.runGET("http://lambda.q-b.eu/api/api.php",
							"act=launcherhash").replace("\n", "").trim();
			if (s == null || s.equals(""))
				return false;
			if (!s.equals(Utils.getMD5(launcherFile)))
				return true;
			else
				return false;
		}

		private void launchLauncher() {

			if (!launcherFile.exists()) {
				JOptionPane
						.showMessageDialog(
								null,
								"Main module is not downloaded, but you're offline! To continue, connect to the Internet",
								"ERROR: SYSTEM IS OFFLINE",
								JOptionPane.ERROR_MESSAGE);
				System.exit(0);
			}

			try {

				ArrayList<String> localArrayList = new ArrayList<String>();

				if (Utils.getPlatform().equals(Utils.OS.windows))
					localArrayList.add("javaw");
				else {
					localArrayList.add("java");
				}
				localArrayList.add("-Xmx"
						+ Utils.readFileAsString(Utils.getWorkingDirectory()
								+ File.separator + "memory", "1024") + "m");
				localArrayList.add("-Dsun.java2d.noddraw=true");
				localArrayList.add("-Dsun.java2d.d3d=false");
				localArrayList.add("-Dsun.java2d.opengl=false");
				localArrayList.add("-Dsun.java2d.pmoffscreen=false");
				localArrayList.add("-classpath");
				localArrayList.add(launcherFile.toString());
				localArrayList.add("eu.q_b.asn007.lambda.Main");
				ProcessBuilder localProcessBuilder = new ProcessBuilder(
						localArrayList);
				Process localProcess = localProcessBuilder.start();
				if (localProcess == null)
					throw new Exception("!");
				System.exit(0);
			} catch (Exception localException) {
				localException.printStackTrace();
				JOptionPane
						.showMessageDialog(
								null,
								"Whoops... LambdaUpdater could not start the main module... If this error persists, show the developer this log:\n"
										+ stack2string(localException),
								"ERROR: FAILED TO START LAUNCHER",
								JOptionPane.ERROR_MESSAGE);
				System.exit(0);

			}
		}
	};

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new LambdaUpdater();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public LambdaUpdater() {
		initialize();
	}

	private void initialize() {
		frame = new JFrame();
		frame.setSize(new Dimension(200, 200));
		frame.setUndecorated(true);
		try {
			frame.setIconImage(ImageIO.read(LambdaUpdater.class
					.getResourceAsStream("/eu/q_b/asn007/lambda/updater/icon.png")));
		} catch (IOException ignored) {}
		progressCircle = new CProgress(frame.getContentPane());
		progressCircle.setIndeterminate(true);
		frame.getContentPane().add(progressCircle);
		AWTUtilities.setWindowOpaque(frame, false);
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
		System.out.println("Lolita");
		thread.start();
	}

	public String stack2string(Exception e) {
		try {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			return "------\r\n" + sw.toString() + "------\r\n";
		} catch (Exception e2) {
			return "bad stack2string";
		}
	}
}
