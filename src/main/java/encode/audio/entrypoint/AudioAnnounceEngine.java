package encode.audio.entrypoint;

import encode.audio.utils.AudioFile;
import encode.audio.utils.CoreUtil;
import encode.audio.utils.DummyLogService;
import encode.audio.utils.LogService;
import flux.AudioAnnounceTmlg;
import flux.FluxTmlg;
import flux.IAudioAnnounceTmlg;
import flux.IFluxTmlg;

public class AudioAnnounceEngine {

	private LogService logger = new DummyLogService();
	private DataObject audioConfig;
	private DataObject httpConfig;

	private LocalHTTPSServer localHTTPSServer;
	private LocalTmpFolder localTmpFolder;

	public AudioAnnounceEngine(LocalHTTPSServer localHTTPSServer, LocalTmpFolder localTmpFolder) {
		super();
		this.localHTTPSServer = localHTTPSServer;
		this.localTmpFolder = localTmpFolder;
	}

	public String publishAudioFile(AudioAnnounceTmlg audioAnnounceTmlg, DataObject audioConfigTmp, DataObject httpConfigTmp) {
		this.audioConfig = audioConfigTmp;
		this.httpConfig = httpConfigTmp;
		IFluxTmlg targetAudioFileMessage = new FluxTmlg(audioAnnounceTmlg);

		IAudioAnnounceTmlg audioAnnounce = targetAudioFileMessage.getBody().getTravelInfo().getAudioAnnounce();
		AudioFile newAudioFile = processAudioAnnounce(targetAudioFileMessage, audioAnnounce);

		targetAudioFileMessage = updateAudioFileMessage(targetAudioFileMessage, newAudioFile);
		return targetAudioFileMessage.toString();
	}

	/**
	 * process audio announce
	 *
	 * @param flux
	 * @param audioAnnounce
	 * @return AudioFile
	 * @throws CoreException
	 * */
	public AudioFile processAudioAnnounce(IFluxTmlg flux, IAudioAnnounceTmlg audioAnnounce) {

		String fileName = audioAnnounce.getFileName();
		String fileUrl = audioAnnounce.getUrl();
		String audioTempPath = httpConfig.getString("audio_temp_path");
		String filePath = audioTempPath + fileName;

		AudioFile newAudioFile = CoreUtil.simulateEncodedAudioFileProperties(fileName, audioConfig.getString("final_audio_file_extension"));
		String encodedFilename = newAudioFile.getName();

		logger.log(LogService.LOG_DEBUG, "Encoding audio file :" + filePath + " (path : " + httpConfig.getString("audio_temp_path") + ")");
		newAudioFile = CoreUtil.encodeAudioFile(audioTempPath, fileName, audioConfig);

		// Uploading encoded audio file to HTTPS server
		uploadAudioAnnounce(localHTTPSServer, newAudioFile);

		return newAudioFile;
	}

	public void uploadAudioAnnounce(LocalHTTPSServer localServerFolder, AudioFile newAudioFile) {
		logger.log(LogService.LOG_DEBUG, "Uploading audio file to HTTPS server");
		localServerFolder.uploadAudioAnnounce(newAudioFile.getName());
		logger.log(LogService.LOG_DEBUG, "Uploading audio file to HTTPS server : OK");
	}

	public IFluxTmlg updateAudioFileMessage(IFluxTmlg flux, AudioFile newAudioFile) {
		String serverAudioEmbarqueURLGet = httpConfig.getString("url_embarque_server_get");

		flux.getBody().getTravelInfo().getAudioAnnounce().setFileName(newAudioFile.getName());
		flux.getBody().getTravelInfo().getAudioAnnounce().setFormat(newAudioFile.getFormat());
		flux.getBody().getTravelInfo().getAudioAnnounce().setUrl(serverAudioEmbarqueURLGet + newAudioFile.getName());

		logger.log(LogService.LOG_DEBUG, "Send message oBix to the adaptor: " + flux.toString());
		return flux;
	}
}
