import java.awt.Color;
import java.awt.Dimension;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.vmichalak.protocol.ssdp.Device;
import com.vmichalak.protocol.ssdp.SSDPClient;

import de.owlhq.remotebox.BlinkApp;
import de.owlhq.remotebox.animation.BlinkAnimation;
import de.owlhq.remotebox.animation.BlinkAnimator;
import de.owlhq.remotebox.data.RtBoxInfo;
import de.owlhq.remotebox.gui.panel.AnimationDialog;
import de.owlhq.remotebox.gui.panel.LedInterface;
import de.owlhq.remotebox.gui.panel.LedPanel;

public class MainTest {
	public static void main(String[] args) throws JsonSyntaxException, JsonIOException, IOException {
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
		//JFrame f = new JFrame();
		//f.setSize(new Dimension(800,800));
		//f.add(new AnimationDialog());
		LedInterface led = new LedPanel();
		//f.add(led);
		//f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//f.setVisible(true);
		//BlinkAnimator bAnim = new BlinkAnimator(ab, led, true);
		//bAnim.startAnimation();
		Color your_color = new Color(128,128,128);

		String hex = "#"+Integer.toHexString(your_color.getRGB()).substring(2);
		System.out.println(hex);
		
		List<Device> devices = SSDPClient.discover(1000, "remote-box-client");
	    System.out.println(devices.size() + " devices found");
	    for (Device device : devices) {
	    	System.out.println("== DEVICE : ==");
			System.out.println(" USN:         " + device.getUSN());
			System.out.println(" IP:          " + device.getIPAddress());
			System.out.println(" Server:      " + device.getServer());
			System.out.println(" ServiceType: " + device.getServiceType());
			System.out.println(" URL:         " + device.getDescriptionUrl());
		}
	    System.out.println(BlinkApp.getConfig("ASDF"));;
	    BlinkApp.showStartupDialog();
	    List<String> files = BlinkApp.getSelectedDeviceAudioController().getAudioFiles();
	    for (String file : files) {
			System.out.println("AudioFile: " + file);
		}
	    RtBoxInfo rtInfo = BlinkApp.getSelectedDeviceStatus();
	    System.out.println("-------------------------------------------------------");
	    System.out.println("Status:");
	    System.out.println("-------------------------------------------------------");
	    System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(rtInfo));
	    System.out.println("-------------------------------------------------------");
	    if (rtInfo.getLed().getCurrentlyPlaying() != null) {
		    System.out.println("Currently Playing:");
		    System.out.println("-------------------------------------------------------");
		    System.out.println(rtInfo.getLed().getCurrentlyPlaying().getBlink());
		    if (rtInfo.getLed().getCurrentlyPlaying().isEndless()) 
			    System.out.println("Endlessly");
		    System.out.println("-------------------------------------------------------");
		    System.out.println(BlinkApp.getSelectedDevice().getAnimation(rtInfo.getLed().getCurrentlyPlaying().getBlink()));
	    }
	}
}
