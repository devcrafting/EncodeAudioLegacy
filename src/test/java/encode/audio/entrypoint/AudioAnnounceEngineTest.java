package encode.audio.entrypoint;

import org.approvaltests.Approvals;
import org.junit.Test;

public class AudioAnnounceEngineTest {

	@Test
	// if the method cannot launch the default diff tools, try to set a
	// reporter with @UseReporter(...).
	// http://blog.approvaltests.com/2011/12/using-reporters-in-approval-tests.html
	public void checkReporter() throws Exception {
		Approvals.verify("My diff reporter is working!");
	}
}
