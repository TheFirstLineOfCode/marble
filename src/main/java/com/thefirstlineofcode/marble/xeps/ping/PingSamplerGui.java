package com.thefirstlineofcode.marble.xeps.ping;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.util.JMeterUtils;

public class PingSamplerGui extends AbstractSamplerGui {
	private static final long serialVersionUID = 3407218947396368827L;
	
	private JTextField timeout;
	
	public PingSamplerGui() {
		init();
	}
	
	private void init() {
		setLayout(new BorderLayout(0, 5));
		setBorder(makeBorder());
		
		VerticalPanel mainPanel = new VerticalPanel();
		mainPanel.add(createTimeoutPanel());
		
		add(makeTitlePanel(), BorderLayout.NORTH);
		add(mainPanel, BorderLayout.CENTER);
	}

	private JPanel createTimeoutPanel() {
		JLabel label = new JLabel(JMeterUtils.getResString("xmpp_ping_ping_timeout"));
		
		timeout = new JTextField(10);
		timeout.setText("2000");
		label.setLabelFor(timeout);
		
		JPanel hostPanel = new JPanel(new BorderLayout(5, 0));
		hostPanel.add(label, BorderLayout.WEST);
		hostPanel.add(timeout, BorderLayout.CENTER);
		
		return hostPanel;
    }

	@Override
	public String getLabelResource() {
		return "xmpp_ping_sampler_title";
	}

	@Override
	public TestElement createTestElement() {
		PingSampler sampler =  new PingSampler();
		modifyTestElement(sampler);
		
		return sampler;
	}

	@Override
	public void modifyTestElement(TestElement element) {
		PingSampler sampler = (PingSampler)element;
		sampler.setPingTimeout(timeout.getText());
		
		super.configureTestElement(element);
	}
	
	@Override
	public void configure(TestElement element) {
		super.configure(element);
		
		PingSampler sampler = (PingSampler)element;
		
		timeout.setText(sampler.getPingTimeout());
	}
	
	@Override
	public void clearGui() {
		super.clearGui();
		
		timeout.setText("");
	}

}
