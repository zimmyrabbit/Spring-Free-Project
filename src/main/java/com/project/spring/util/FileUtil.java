package com.project.spring.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;

public class FileUtil
{
	private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);
	public static List<Map> ffmpegUploadFile(HttpServletRequest request, String fileuploadPath, String subUploadPath) throws IOException, Exception
	{
		MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest)request;

		Map files = multiRequest.getFileMap();

		String uploadPath = fileuploadPath + File.separatorChar + subUploadPath;
		System.out.println("uploadPath ==> "+uploadPath);
		File saveFolder = new File(uploadPath);
		String fileName = null;
		List result = new ArrayList();

		boolean isDir = false;

		if ((!saveFolder.exists()) || (saveFolder.isFile())) {
			boolean bool1 = saveFolder.mkdirs();
		}
		
		//max file size check
		Iterator itr = files.entrySet().iterator();

		while (itr.hasNext()) {
			Map.Entry entry = (Map.Entry)itr.next();
			MultipartFile file = (MultipartFile)entry.getValue();
			System.out.println("File Size ===>>>  "+file.getSize());
			if(Constants.MAX_VIDEO_FILESIZE < file.getSize()) {
				throw new Exception("============ DownLoad File Size Over MaxSize =====================");
			}
		}
		
		Iterator fitr = files.entrySet().iterator();
		Map fileInfo = new HashMap();
		while (fitr.hasNext()) {
			Map.Entry entry = (Map.Entry)fitr.next();
			MultipartFile file = (MultipartFile)entry.getValue();
			fileName = file.getOriginalFilename();
			
			if (!"".equals(fileName)) {
				fileInfo = new HashMap();
				String nFileName = UUID.randomUUID().toString();  
				String realFileName = UUID.randomUUID().toString();

				String ext = fileName.substring(fileName.lastIndexOf("."));
				String filePath = uploadPath + File.separatorChar + nFileName + ext;  // ????????? ??????
				String realPath = uploadPath + File.separatorChar + realFileName + ext; // ?????? ?????? ??????
				file.transferTo(new File(filePath)); //???????????? ????????? ??????
				
				FFmpeg ffmpeg = new FFmpeg(Constants.FFMPEG_PATH); // ffmpeg ?????? 
				FFprobe ffprobe = new FFprobe(Constants.FFPROBE_PATH);  // ffprobe ??????
				FFmpegProbeResult info = ffprobe.probe(filePath); // ???????????? ????????? ?????? ??????
				int bit_rate = (int)info.getFormat().bit_rate > 1048000 ? 1048000 : (int)info.getFormat().bit_rate; // ???????????? ???????????? ??????????????? ??????
				
				int width = 0;
				int height = 0;
				
				for(int i=0; i<info.getStreams().size(); i++) {
					if(info.getStreams().get(i).width > 0) {
						if(info.getStreams().get(i).width > 1280) {
							width = 800;
							height = 600;
						} else {
							width = info.getStreams().get(i).width;
							height = info.getStreams().get(i).height;
						}
					}						
				}
				
				logger.debug("====== Video Conversion Start ======");
				/* ???????????? ?????? */
				FFmpegBuilder builder = new FFmpegBuilder().setInput(filePath)
					.addOutput(realPath)  // ????????? ??????
					.setVideoResolution(width, height)  // ????????? x, y
					.setVideoBitRate(bit_rate)  // ??????????????? ( ????????? ?????? ????????? --> ???????????? ???????????? ????????????)
					.setVideoCodec("libx264")  // ??????
					.setStrict(FFmpegBuilder.Strict.EXPERIMENTAL)
					.done();
				
				FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);	
				try {
					executor.createJob(builder, p -> {
						if(p.isEnd()) {
							logger.debug("Video conversion Successfully completed ===>> " + realPath);
							logger.debug("Video File Size(Byte) ===>> " + p.total_size);
						}
					}).run();
				}catch(Exception e ) {
					e.getMessage();
					logger.debug("Video conversion failed ===>>" + fileName);
				}
				/* ????????? ?????? ?????? ===> ?????? ?????? ????????? ?????? ????????? ?????? */

				File removeFile = new File(filePath);
				removeFile.delete();  /* ?????? ????????? ??????????????? ??????????????? */
				
				FFmpegProbeResult realInfo = ffprobe.probe(realPath);  // ????????? ???????????? ?????? ??????

				fileInfo.put("originName", fileName);
				fileInfo.put("saveFilePath", uploadPath);
				fileInfo.put("storedName", realFileName + ext);
				fileInfo.put("fileSize", String.valueOf(realInfo.getFormat().size));
				fileInfo.put("contentType", ext);
				
				result.add(fileInfo);				
			}
		}

		return result;
	}
	
	public static List<Map> multiUploadFile(HttpServletRequest request, String fileuploadPath, String subUploadPath) throws Exception
	{
		MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest)request;

		Map files = multiRequest.getFileMap();

		String uploadPath = fileuploadPath + File.separatorChar + subUploadPath;
		System.out.println("uploadPath ==> "+uploadPath);
		File saveFolder = new File(uploadPath);
		String fileName = null;
		List result = new ArrayList();

		boolean isDir = false;

		if ((!saveFolder.exists()) || (saveFolder.isFile())) {
			boolean bool1 = saveFolder.mkdirs();
		}
		
		//max file size check
		Iterator itr = files.entrySet().iterator();

		while (itr.hasNext()) {
			Map.Entry entry = (Map.Entry)itr.next();
			MultipartFile file = (MultipartFile)entry.getValue();
			System.out.println("File Size ===>>>  "+file.getSize());
			if(Constants.MAX_FILESIZE < file.getSize()) {
				throw new Exception("============ DownLoad File Size Over MaxSize =====================");
			}
		}
		
		Iterator fitr = files.entrySet().iterator();
		Map fileInfo = new HashMap();
		while (fitr.hasNext()) {
			Map.Entry entry = (Map.Entry)fitr.next();
			MultipartFile file = (MultipartFile)entry.getValue();
			fileName = file.getOriginalFilename();
			
			if (!"".equals(fileName)) {
				fileInfo = new HashMap();
				String nFileName = UUID.randomUUID().toString();

				String ext = fileName.substring(fileName.lastIndexOf("."));

				fileInfo.put("originName", fileName);
				fileInfo.put("saveFilePath", uploadPath);
				fileInfo.put("storedName", nFileName + ext);
				fileInfo.put("fileSize", String.valueOf(file.getSize()));
				fileInfo.put("contentType", ext);

				String filePath = uploadPath + File.separatorChar + nFileName + ext;
				file.transferTo(new File(filePath));
				
				result.add(fileInfo);				
			}
		}

		return result;
	}
}
