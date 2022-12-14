import java.awt.Color;
import java.awt.Dimension;
import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import de.owlhq.remotebox.BlinkAnimation;
import de.owlhq.remotebox.BlinkAnimator;
import de.owlhq.remotebox.gui.panel.AnimationDialog;
import de.owlhq.remotebox.gui.panel.LedPanel;

public class MainTest {
	public static void main(String[] args) throws JsonSyntaxException, JsonIOException, FileNotFoundException {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		BlinkAnimation ab = gson.fromJson(new FileReader("C:\\Users\\owl\\git\\remote-talk-box\\data\\blink\\2-lights-rotating-3-times.json"), BlinkAnimation.class);
		System.out.println(gson.toJson(ab));
		ab.generate();
		System.out.println(gson.toJson(ab));
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JFrame f = new JFrame();
		f.setSize(new Dimension(800,800));
		f.add(new AnimationDialog());
		LedPanel led = new LedPanel();
		//f.add(led);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
		//BlinkAnimator bAnim = new BlinkAnimator(ab, led, true);
		//bAnim.startAnimation();
		Color your_color = new Color(128,128,128);

		String hex = "#"+Integer.toHexString(your_color.getRGB()).substring(2);
		System.out.println(hex);
	}
}
