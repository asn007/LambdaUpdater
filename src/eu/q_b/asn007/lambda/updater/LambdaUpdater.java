package eu.q_b.asn007.lambda.updater;

import java.awt.Color;
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
import javax.swing.UIManager;

import com.sun.awt.AWTUtilities;

public class LambdaUpdater {

	private JFrame frame;
	private ProgressBar progressBar;
	private File launcherFile = new File(Utils.getWorkingDirectory()
			+ File.separator + "launcher.jar");
	private Thread thread = new Thread() {
		public void run() {
			progressBar.setValue(10);
			if (Utils.isOnline()) {
				if (needsUpdate())
					update();
				launchLauncher();
			} else
				launchLauncher();

		}

		private void update() {
			try {
				Utils.download(
						new URL(Utils.runGET(
								"http://lambda.q-b.eu/api/api.php",
								"act=launcherurl")), launcherFile, progressBar);
			} catch (Exception e) {
				JOptionPane
						.showMessageDialog(
								null,
								"Ой... lambdaUpdater'у не удалось загрузить новую версию лаунчера... Если ошибка повторяется, покажите лог ошибки ниже разработчику:\n"
										+ stack2string(e),
								"ERROR: FAILED TO START LAUNCHER",
								JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
				System.exit(0);
			}

		}

		private boolean needsUpdate() {
			progressBar.setValue(45);
			if (!Utils.getMD5(launcherFile).equals(
					Utils.runGET("http://lambda.q-b.eu/api/api.php",
							"act=launcherhash")))
				return true;
			else
				return false;
		}

		private void launchLauncher() {

			if (!launcherFile.exists()) {
				progressBar.setValue(0);
				JOptionPane
						.showMessageDialog(
								null,
								"Лаунчер не загружен, а вы находитесь не в сети.. \nДля первого запуска необходимо быть в сети!",
								"ERROR: SYSTEM IS OFFLINE",
								JOptionPane.ERROR_MESSAGE);
				System.exit(0);
			}
			progressBar.setValue(100);
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
								"Ой... lambdaUpdater'у не удалось запустить лаунчер. Если ошибка повторяется, покажите лог ошибки ниже разработчику:\n"
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
					UIManager.setLookAndFeel(UIManager
							.getSystemLookAndFeelClassName());
					LambdaUpdater window = new LambdaUpdater();
					window.frame.setUndecorated(true);
					window.frame.setResizable(false);
					AWTUtilities.setWindowOpaque(window.frame, false);
					window.frame.setVisible(true);
					window.frame.setLocationRelativeTo(null);
					window.frame.setAlwaysOnTop(true);
					window.frame.setIconImage(ImageIO.read(LambdaUpdater.class
							.getResourceAsStream("/eu/q_b/asn007/lambda/updater/icon.png")));
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

		try {
			progressBar = new ProgressBar(
					ImageIO.read(LambdaUpdater.class
							.getResourceAsStream("/eu/q_b/asn007/lambda/updater/lambda-loader.png")),
					ImageIO.read(LambdaUpdater.class
							.getResourceAsStream("/eu/q_b/asn007/lambda/updater/lambda-loader-gray.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		frame.setBackground(Color.black);
		frame.setForeground(Color.black);
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(progressBar);
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
