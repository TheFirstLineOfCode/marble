package com.thefirstlineofcode.marble.xeps.ping;

import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.property.StringProperty;

import com.thefirstlineofcode.basalt.xeps.ping.Ping;
import com.thefirstlineofcode.basalt.xmpp.core.stanza.Iq;
import com.thefirstlineofcode.basalt.xmpp.core.stanza.Stanza;
import com.thefirstlineofcode.basalt.xmpp.core.stanza.error.RemoteServerTimeout;
import com.thefirstlineofcode.basalt.xmpp.core.stanza.error.ServiceUnavailable;
import com.thefirstlineofcode.chalk.core.stream.StreamConfig;
import com.thefirstlineofcode.chalk.xeps.ping.IPing;
import com.thefirstlineofcode.chalk.xeps.ping.IPing.Result;
import com.thefirstlineofcode.chalk.xeps.ping.PingPlugin;
import com.thefirstlineofcode.marble.AbstractXmppSampler;
import com.thefirstlineofcode.marble.IPluginContributor;
import com.thefirstlineofcode.marble.XmppMessage;
import com.thefirstlineofcode.marble.XmppSampleResult;
import com.thefirstlineofcode.marble.XmppSampleResultWatcher;

public class PingSampler extends AbstractXmppSampler implements IPluginContributor {
	private static final long serialVersionUID = 1L;
	
	public static final String PING_TIMEOUT = "PingSampler.timeout";
	
	@Override
	public SampleResult sample(Entry entry) {
		IPing ping = getChatClient().createApi(IPing.class);
		
		StringBuilder samplerData = new StringBuilder();
		StreamConfig streamConfig = getChatClient().getStreamConfig();
		samplerData.append("In Band Registration\n");
		samplerData.append("Host: ").append(streamConfig.getHost()).append('\n');
		samplerData.append("Port: ").append(streamConfig.getPort()).append('\n');
		samplerData.append("Ping API Implementation Class: " + ping.getClass().getName());
		
		XmppSampleResult result = createXmppResult();		
		
		PingSampleResultWatcher watcher = new PingSampleResultWatcher(result);
		getChatClient().getStream().addStanzaWatcher(watcher);
		
		int timeout = getTimeoutAsInt();
		ping.setTimeout(timeout);
		
		result.sampleStart();
		
		Result pingResult = ping.ping();
		if (pingResult == Result.PONG) {				
			result.setResponseCodeOK();
			result.setResponseMessageOK();
			result.setSuccessful(true);
		} else if (pingResult == Result.TIME_OUT) {
			result.setSuccessful(false);
			result.setResponseCode(getErrorCode(RemoteServerTimeout.DEFINED_CONDITION));
			result.setResponseMessage("Ping Timeout");
		} else {
			result.setSuccessful(false);
			result.setResponseCode(getErrorCode(ServiceUnavailable.DEFINED_CONDITION));
			result.setResponseMessage("Service Unavailable");
		}
		
		return result;
	}
	
	private class PingSampleResultWatcher extends XmppSampleResultWatcher {
		public PingSampleResultWatcher(XmppSampleResult result) {
			super(result);
		}

		@Override
		protected boolean accepts(Stanza stanza, XmppMessage message) {
			if (!(stanza instanceof Iq))
				return false;
			
			Iq iq = (Iq)stanza;
			return (iq.getObject() instanceof Ping);
		}	
	}
	
	public void setPingTimeout(String timeout) {
		setProperty(new StringProperty(PING_TIMEOUT, timeout));
	}
	
	public String getPingTimeout() {
		return getPropertyAsString(PING_TIMEOUT);
	}
	
	public int getTimeoutAsInt() {
		return getPropertyAsInt(PING_TIMEOUT, 2000);
	}

	@Override
	public Class<?>[] getPlugins() {
		return new Class<?>[] {PingPlugin.class};
	}
}
