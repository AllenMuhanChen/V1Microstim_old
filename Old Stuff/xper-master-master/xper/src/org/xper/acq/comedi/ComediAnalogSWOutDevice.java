package org.xper.acq.comedi;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.xper.Dependency;
import org.xper.acq.device.AnalogOutDevice;
import org.xper.acq.vo.ComediChannelSpec;
import org.xper.exception.ComediException;

/**
 * Software timing analog output.
 * 
 * @author John
 *
 */
public class ComediAnalogSWOutDevice implements AnalogOutDevice {
	@Dependency
	List<ComediChannelSpec> outputChannels;
	@Dependency
	String deviceString;
	
	ByteBuffer handle;
	ByteBuffer buf;
	
	@PostConstruct
	public void init () {
		if (deviceString == null) {
			throw new ComediException("Device name is null.");
		}
		if (outputChannels == null || outputChannels.size() == 0) {
			throw new ComediException("Output channels list is null or empty.");
		}
		buf = ByteBuffer.allocateDirect(
				outputChannels.size() * Double.SIZE / 8).order(
				ByteOrder.nativeOrder());
		handle = nCreateTask(deviceString, outputChannels.size());
		for (int i = 0; i < outputChannels.size(); i ++) {
			ComediChannelSpec spec = outputChannels.get(i);
			if (spec.getAref() == null) {
				throw new ComediException("Reference setting for channel " + spec.getChannel() + " is null."); 
			}
			nCreateChannels(handle, i, spec.getChannel(),
					spec.getMinValue(), spec.getMaxValue(), spec.getAref());
		}
	}
	
	@PreDestroy
	public void destroy () {
		nDestroy(handle);
	}
	
	/**
	 * Output the data. One data point for each channel.
	 */
	public void write (double [] data) {
		if (data.length != outputChannels.size()) {
			throw new ComediException("Data length incorrect, expecting " + outputChannels.size() + " find " + data.length);
		}
		for (int i = 0; i < data.length; i ++) {
			buf.putDouble(data[i]);
		}
		buf.flip();
		
		nWrite(handle, buf);
	}
	
	native ByteBuffer nCreateTask(String deviceString, int nChannels);
	native void nDestroy(ByteBuffer handle);
	native void nCreateChannels(ByteBuffer handle, int i, short channel,
			double minValue, double maxValue, String aref);
	native void nWrite (ByteBuffer handle, ByteBuffer data);

	public List<ComediChannelSpec> getOutputChannels() {
		return outputChannels;
	}

	public void setOutputChannels(List<ComediChannelSpec> outputChannels) {
		this.outputChannels = outputChannels;
	}

	public String getDeviceString() {
		return deviceString;
	}

	public void setDeviceString(String deviceString) {
		this.deviceString = deviceString;
	}
}
