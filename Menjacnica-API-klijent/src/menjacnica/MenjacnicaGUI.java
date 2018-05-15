package menjacnica;

import java.awt.EventQueue;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import menjacnica.util.LogsJsonUtility;
import menjacnica.util.URLConnectionUtil;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MenjacnicaGUI extends JFrame {

	private JPanel contentPane;
	private JLabel lblIzValuteZemlje;
	private JLabel lblUValutuZemlje;
	private JTextField tfIz;
	private JTextField tfU;
	private JComboBox comboBoxIz;
	private JComboBox comboBoxU;

	private LinkedList<Zemlja> listaZemalja;
	private JButton btnKonvertuj;
	private LinkedList<Zemlja> zemlje = vratiZemlje();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MenjacnicaGUI frame = new MenjacnicaGUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MenjacnicaGUI() {
		setTitle("Menjacnica");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		contentPane.add(getLblIzValuteZemlje());
		contentPane.add(getLblUValutuZemlje());
		contentPane.add(getTfIz());
		contentPane.add(getTfU());
		contentPane.add(getComboBoxIz());
		contentPane.add(getComboBoxU());
		contentPane.add(getBtnKonvertuj());
	}

	private JLabel getLblIzValuteZemlje() {
		if (lblIzValuteZemlje == null) {
			lblIzValuteZemlje = new JLabel("Iz valute zemlje:");
			lblIzValuteZemlje.setBounds(59, 57, 123, 16);
		}
		return lblIzValuteZemlje;
	}

	private JLabel getLblUValutuZemlje() {
		if (lblUValutuZemlje == null) {
			lblUValutuZemlje = new JLabel("U valutu zemlje:");
			lblUValutuZemlje.setBounds(241, 57, 123, 16);
		}
		return lblUValutuZemlje;
	}

	private JTextField getTfIz() {
		if (tfIz == null) {
			tfIz = new JTextField();
			tfIz.setBounds(59, 161, 123, 22);
			tfIz.setColumns(10);
		}
		return tfIz;
	}

	private JTextField getTfU() {
		if (tfU == null) {
			tfU = new JTextField();
			tfU.setColumns(10);
			tfU.setBounds(241, 161, 123, 22);
		}
		return tfU;
	}

	private JComboBox getComboBoxIz() {
		if (comboBoxIz == null) {
			comboBoxIz = new JComboBox();
			comboBoxIz.setBounds(59, 107, 123, 22);

			
			for (int i = 0; i < zemlje.size(); i++) {
				comboBoxIz.addItem(zemlje.get(i));
			}
		}
		return comboBoxIz;
	}

	private JComboBox getComboBoxU() {
		if (comboBoxU == null) {
			comboBoxU = new JComboBox();
			comboBoxU.setBounds(241, 107, 123, 22);
			
			for (int i = 0; i < zemlje.size(); i++) {
				comboBoxU.addItem(zemlje.get(i));
			}
		}
		return comboBoxU;
	}

	private JButton getBtnKonvertuj() {
		if (btnKonvertuj == null) {
			btnKonvertuj = new JButton("Konvertuj");
			btnKonvertuj.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					Zemlja iz = (Zemlja)comboBoxIz.getSelectedItem();
					Zemlja u = (Zemlja)comboBoxU.getSelectedItem();
					
					double kurs = vratiKurs(iz.getCurrencyID(), u.getCurrencyID());
					if(kurs != -1) {
						tfU.setText( String.valueOf(Double.parseDouble(tfIz.getText()) * kurs) );
					} else {
						System.out.println("Greksa");
//						JOptionPane.showMessageDialog(this, "Doslo je do greske", "Greska", JOptionPane.WARNING_MESSAGE);
					}

					log(new GregorianCalendar(), iz.getCurrencyID(), u.getCurrencyID(), kurs);
				}
			});
			btnKonvertuj.setBounds(160, 215, 97, 25);
		}
		return btnKonvertuj;
	}
	
	public LinkedList<Zemlja> vratiZemlje() {
		LinkedList<Zemlja> zemlje = new LinkedList<Zemlja>();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String rezultat = null;

		try {
			rezultat = URLConnectionUtil.getContent("http://free.currencyconverterapi.com/api/v3/countries");
		} catch (IOException e) {
			e.printStackTrace();
		}

		JsonObject res = gson.fromJson(rezultat, JsonObject.class);
		JsonObject obj = res.get("results").getAsJsonObject();

		Set<Entry<String, JsonElement>> attributeEntres = obj.entrySet();
		
		for (Entry<String, JsonElement> entry : obj.entrySet()) {
		    String n = entry.getKey();
		    JsonObject v = (JsonObject) entry.getValue();
		    
		    Zemlja z = new Zemlja();
		    z.setAlpha3(v.get("alpha3").toString().replaceAll("\"", ""));
		    z.setCurrencyID(v.get("currencyId").toString().replaceAll("\"", ""));
		    z.setCurrencyName(v.get("currencyName").toString().replaceAll("\"", ""));
		    z.setName(v.get("name").toString().replaceAll("\"", ""));
		    z.setId(v.get("id").toString().replaceAll("\"", ""));
		    
		    zemlje.add(z);
		}
		
		return zemlje;
	}

	public double vratiKurs(String iz, String u) {
		String url = "http://free.currencyconverterapi.com/api/v3/convert?q=" + iz + "_" + u;
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		
		String rezultat = null;

		try {
			rezultat = URLConnectionUtil.getContent(url);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		JsonObject obj = gson.fromJson(rezultat, JsonObject.class);
		JsonObject query = obj.get("query").getAsJsonObject();
		JsonObject results = obj.get("results").getAsJsonObject();
		
		if(Integer.parseInt(query.get("count").toString()) == 0) {
			return -1;
		} else {
			JsonObject p = (JsonObject) results.get(iz + "_" + u);
			return Double.parseDouble(p.get("val").toString());
		}
	}
	
	public void log(GregorianCalendar vreme, String iz, String u, double kurs) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		
		JsonArray logsArray = null;
		LinkedList<Log> logs = new LinkedList<Log>(); 
		
		//deserijalizacija
		try (FileReader reader = new FileReader("data/log.json")) {
			logsArray = gson.fromJson(reader, JsonArray.class);
			logs = LogsJsonUtility.parseLogs(logsArray);
		} catch (Exception e) {
			System.out.println("Greska: " + e.getMessage());
		}
	
		//dodavanje novog loga
		Log l = new Log();
		l.setDatumVreme(vreme);
		l.setIzValuta(iz);
		l.setuValuta(u);
		l.setKurs(kurs);
		logs.add(l);
		
		// serijalizacija
		JsonArray array = LogsJsonUtility.serializeLogs(logs);
		try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("data/log.json")))) {
			String arrayString = gson.toJson(array);
			
			out.println(arrayString);
		} catch (Exception e) {
			System.out.println("Greska: " + e.getMessage());
		}
	}
	
}
