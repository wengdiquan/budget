package com.bjsj.budget.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.springframework.web.multipart.MultipartFile;


/**
 * 文件生成工具类
 * 
 */
public class FileUtil {
	// private static Logger log = Logger.getLogger(FileConfig.class);

	/**
	 * 创建文件路径
	 * 
	 * @param dirName
	 */
	public static void createDir(String dirName) {

		File dir = new File(dirName);
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}

	/**
	 * 创建文件
	 * 
	 * @param fileName
	 */
	public static void createFile(String fileName) {
		File path = new File(fileName);
		if (!path.exists()) {
			try {
				path.createNewFile();
			} catch (IOException e) {
				// log.error("创建文件失败", e);
				e.printStackTrace();

			}
		}
	}

	/**
	 * 复制单个文件
	 * 
	 * @param oldPath
	 *            String 原文件路径 如：c:/fqf.txt
	 * @param newPath
	 *            String 复制后路径 如：f:/fqf.txt 搜索
	 * @param dirName
	 *            新文件目录
	 * @return boolean
	 * @throws IOException
	 */
	public static void copyFile(String oldPath, String dirName, String newPath) throws IOException {
		InputStream inStream = null; // 读入原文件
		FileOutputStream fs = null;
		try {
			int bytesum = 0;
			int byteread = 0;
			createDir(dirName);
			File oldfile = new File(oldPath);
			if (oldfile.exists()) { // 文件存在时
				inStream = new FileInputStream(oldPath); // 读入原文件
				fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				int length;
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; // 字节数 文件大小
					fs.write(buffer, 0, byteread);
				}
				fs.close();
				inStream.close();
			}
		} catch (Exception e) {
			System.out.println("复制单个文件操作出错");
			e.printStackTrace();
			fs.close();
			inStream.close();
		}
	}

	/**
	 * 备份上传文件
	 * 
	 * @param file
	 *            文件
	 * @param fileFileName
	 *            文件名
	 * @param BackFilePATH
	 *            文件备份路径
	 * @param FileType
	 *            文件类型
	 * @return
	 */
	public static String backImportFile(File file, String fileFileName, String BackFilePath) {
		try {
			String fileName = fileFileName.split("\\.")[0] + "HQ" + TimeUtil.getCurTimeToFormat("yyMMddHHmmss") + "."
					+ FileType(fileFileName);
			// tempfile 默认路径
			String dirName = FileConfig.PATH + "tempfile/" + BackFilePath + "/"
					+ TimeUtil.getCurTimeToFormat("yyyyMMdd");
			FileUtil.createDir(dirName);
			FileUtil.createFile(dirName + "/" + fileName);
			File tempFile = new File(dirName + "/" + fileName);
			String dirfileName = dirName + "/" + fileName;
			FileUtils.copyFile(file, tempFile);
			return dirfileName;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} // 备份文件
	}

	/**
	 * @param file
	 * @param fileFileName
	 * @param BackFilePath
	 * @return
	 */
	public static String backImportFile(MultipartFile file, String fileFileName, String BackFilePath) {
		try {
			String fileName = fileFileName.split("\\.")[0] + "HQ" + TimeUtil.getCurTimeToFormat("yyMMddHHmmss") + "."
					+ FileType(fileFileName);
			fileName = fileName.replaceAll("，", "_");
			// tempfile 默认路径
			String dirName = FileConfig.PATH + "tempfile/" + BackFilePath + "/"
					+ TimeUtil.getCurTimeToFormat("yyyyMMdd");
			FileUtil.createDir(dirName);
			FileUtil.createFile(dirName + "/" + fileName);
			File tempFile = new File(dirName + "/" + fileName);
			String dirfileName = dirName + "/" + fileName;
			file.transferTo(tempFile);
			return dirfileName;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} // 备份文件
	}

	/**
	 * 获取文件后缀名
	 * 
	 * @param fileFileName
	 * @return
	 */
	public static String FileType(String fileFileName) {
		String extention = "";
		if (fileFileName.length() > 0 && fileFileName != null) { // --截取文件名
			int i = fileFileName.lastIndexOf(".");
			if (i > -1 && i < fileFileName.length()) {
				extention = fileFileName.substring(i + 1); // --扩展名
			}
		}
		return extention;
	}

	/**
	 * 创建ZIP文件
	 * 
	 * @param sourcePath
	 *            文件或文件夹路径
	 * @param zipPath
	 *            生成的zip文件存在路径（包括文件名）
	 */
	public static boolean createZip(String sourcePath, String zipPath) {
		FileOutputStream fos = null;
		ZipOutputStream zos = null;
		try {
			fos = new FileOutputStream(zipPath);
			zos = new ZipOutputStream(fos);
			writeZip(new File(sourcePath), "", zos);
			return true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (zos != null) {
					zos.close();
				}
			} catch (IOException e) {
			}
		}
	}

	private static void writeZip(File file, String parentPath, ZipOutputStream zos) {
		if (file.exists()) {
			if (file.isDirectory()) {// 处理文件夹
				parentPath += file.getName() + File.separator;
				File[] files = file.listFiles();
				for (File f : files) {
					writeZip(f, parentPath, zos);
				}
			} else {
				FileInputStream fis = null;
				DataInputStream dis = null;
				try {
					fis = new FileInputStream(file);
					dis = new DataInputStream(new BufferedInputStream(fis));
					ZipEntry ze = new ZipEntry(parentPath + file.getName());
					zos.putNextEntry(ze);
					byte[] content = new byte[1024];
					int len;
					while ((len = fis.read(content)) != -1) {
						zos.write(content, 0, len);
						zos.flush();
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						if (dis != null) {
							dis.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * 字符串写入Txt
	 * 
	 * @param string
	 *            文件路径名包含文件名
	 * @param whisStr
	 *            字符串
	 * @return
	 */
	public static boolean write2Txt(String string, StringBuffer whisStr) {
		try {
			FileOutputStream out = new FileOutputStream(string);
			out.write(whisStr.toString().getBytes());
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 文件打包成gz文件
	 * 
	 * @param inFileName
	 *            文件路径名包含文件名
	 * @param outFileName
	 *            输出gz文件
	 * @return
	 */
	public static boolean write2gz(String inFileName, String outFileName) {
		try {
			BufferedReader in = new BufferedReader(new FileReader(inFileName));
			BufferedOutputStream out = new BufferedOutputStream(
					new GZIPOutputStream(new FileOutputStream(outFileName)));
			String c;
			while ((c = in.readLine()) != null)
				out.write(c.getBytes());
			in.close();
			out.close();
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 文件下载
	 * 
	 * @param response
	 * @param fileName
	 *            文件名
	 * @param dirfileName
	 *            文件路径包含文件名
	 * @param fileType
	 *            下载文件类型
	 */
	public static void downloadFileOutPut(HttpServletResponse response, String dirName, String fileName,
			String fileType) {
		try {
			String dirfileName = dirName + "/" + fileName;
			response.reset();
			// 设置response的Header
			response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
			response.setContentType(fileType);
			OutputStream toClient;
			toClient = new BufferedOutputStream(response.getOutputStream());
			// 以流的形式下载文件。
			InputStream fis = new BufferedInputStream(new FileInputStream(dirfileName));
			byte[] buff = new byte[2048];
			int bytesRead;
			while (-1 != (bytesRead = fis.read(buff, 0, buff.length))) {
				toClient.write(buff, 0, bytesRead);
			}
			fis.close();
			toClient.flush();
			toClient.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 拷贝finereport的生成文件到本地服务器
	 * 
	 * @param dirName
	 *            文件路径不包含文件名
	 * @param dirName
	 *            文件名
	 * @param uRL
	 *            FINEREPORT 生成XLS URL
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	public static void copyURLToFile(String dirName, String fileName, String URL)
			throws MalformedURLException, IOException {
		String dirfileName = dirName + "/" + fileName;
		FileUtil.createDir(dirName);
		FileUtil.createFile(dirfileName);
		File file = new File(dirfileName);
		FileUtils.copyURLToFile(new URL(URL), file);// 拷贝URL资源到File
	}

	// deleteFilePath: D:/acerOpenOrder/
	public static void deleteFolder(String deleteFilePath) {
		// 检查文件是否存在
		File delfolder = new File(deleteFilePath);
		if (!delfolder.exists()) {
			return;
		}
		File oldFile[] = delfolder.listFiles();
		try {
			for (int i = 0; i < oldFile.length; i++) {
				if (oldFile[i].isDirectory()) {
					// 递归清空子文件夹
					deleteFolder(deleteFilePath + oldFile[i].getName() + "/");
				}
				oldFile[i].delete();
			}
		} catch (Exception e) {
			System.out.println("清空文件夹操作出错!");
			e.printStackTrace();
		}
	}
}
