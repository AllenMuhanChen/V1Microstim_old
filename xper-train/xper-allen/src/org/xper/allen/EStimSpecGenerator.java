
package org.xper.allen;
import org.xper.allen.EStimSpec;

public class EStimSpecGenerator {
	public static EStimSpec generate() {
		EStimSpec e = new EStimSpec();
		e.set_id(0);
		e.set_chan(1);
		e.set_trig_src("");
		e.set_num_pulses(1);
		e.set_pulse_train_period(0);
		e.set_post_stim_refractory_period(0);
		e.set_stim_shape("Biphasic");
		e.set_stim_polarity("Cathodic");
		e.set_d1(100);
		e.set_d2(100);
		e.set_dp(0);
		e.set_a1(100);
		e.set_a2(100);
		e.set_pre_stim_amp_settle(0);
		e.set_post_stim_amp_settle(0);
		e.set_maintain_amp_settle_during_pulse_train(0);
		e.set_post_stim_charge_recovery_on(0);
		e.set_post_stim_charge_recovery_off(0);
		return e;
	}

	public EStimSpec generateStimSpec() {
		return EStimSpecGenerator.generate();
	}
}