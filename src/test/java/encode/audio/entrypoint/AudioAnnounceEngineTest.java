package encode.audio.entrypoint;

import org.approvaltests.legacycode.LegacyApprovals;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.dreamhead.moco.HttpServer;
import static com.github.dreamhead.moco.Moco.*;
import static com.github.dreamhead.moco.Runner.*;
import com.thoughtworks.xstream.XStream;

import flux.AudioAnnounceTmlg;
import flux.IFluxTmlg;

public class AudioAnnounceEngineTest {
    
    
    private com.github.dreamhead.moco.Runner runner;
    HttpServer server = httpserver(12306);

    @Before
    public void setuphttp() {
        runner = runner(server);
        runner.start();
    }

    @After
    public void tearDown() {
        runner.stop();
    }

    @Test public void 
    coverageAudioAnnounceEnginLockdown() throws Exception {
         Object[] sourceFileNames = {"10.151.156.180Mon_Nov_04_140724_CET_2013343.wav", "10.151.156.180Tue_Nov_05_141112_CET_2013343.mp3"};
        Object[] targetFormats = {"wav", "mp3", "ogg"};
        Object[] finalUrls = { "null10.151.156.180Mon_Nov_04_140724_CET_2013343", "null10.151.156.180Tue_Nov_05_141112_CET_2013343"} ;
        LegacyApprovals.LockDown(this, "publishAudioFileVariations", targetFormats, finalUrls, sourceFileNames);
    }
    
    public String publishAudioFileVariations(String targetFormat, String finalUrl, String sourceFileName) throws AppTechnicalException {
        // Given
        AudioAnnounceTmlg audioFileMessage = new AudioAnnounceTmlg(finalUrl, targetFormat, sourceFileName);
        DataObject configAudioTmp = new AudioDataObject("." + targetFormat);
        DataObject httpDataObj = new HttpDataObj("./src/test/resources/", "http://localhost/get");
        
        LocalHTTPSServer localServerFolder = new LocalHTTPSServer();
        LocalTmpFolder localTmpFolder = new LocalTmpFolder();
        AudioAnnounceEngine audioAnnounceEngine = new AudioAnnounceEngine(localServerFolder, localTmpFolder);

        // When
        IFluxTmlg flux = audioAnnounceEngine.publishAudioFile(audioFileMessage, configAudioTmp, httpDataObj);
        return new XStream().toXML(flux);

    } 
    
}
