package com.bjgjdsj.zyb.voip.core.util;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static android.os.Environment.isExternalStorageRemovable;

public class FileUtils {

    /** 拼接路径
     *  concatPath("/mnt/sdcard", "/DCIM/Camera")  	=>		/mnt/sdcard/DCIM/Camera
     *  concatPath("/mnt/sdcard", "DCIM/Camera")  	=>		/mnt/sdcard/DCIM/Camera
     *  concatPath("/mnt/sdcard/", "/DCIM/Camera")  =>		/mnt/sdcard/DCIM/Camera
     * */
    public static String concatPath(String... paths) {
        StringBuilder result = new StringBuilder();
        if (paths != null) {
            for (String path : paths) {
                if (path != null && path.length() > 0) {
                    int len = result.length();
                    boolean suffixSeparator = len > 0 && result.charAt(len - 1) == File.separatorChar;//后缀是否是'/'
                    boolean prefixSeparator = path.charAt(0) == File.separatorChar;//前缀是否是'/'
                    if (suffixSeparator && prefixSeparator) {
                        result.append(path.substring(1));
                    } else if (!suffixSeparator && !prefixSeparator) {//补前缀
                        result.append(File.separatorChar);
                        result.append(path);
                    } else {
                        result.append(path);
                    }
                }
            }
        }
        return result.toString();
    }

	/** 计算文件的md5值 */
	public static String calculateMD5(File updateFile) {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			Log.e("FileUtils", "Exception while getting digest", e);
			return null;
		}

		InputStream is;
		try {
			is = new FileInputStream(updateFile);
		} catch (FileNotFoundException e) {
			Log.e("FileUtils", "Exception while getting FileInputStream", e);
			return null;
		}

		//DigestInputStream

		byte[] buffer = new byte[8192];
		int read;
		try {
			while ((read = is.read(buffer)) > 0) {
				digest.update(buffer, 0, read);
			}
			byte[] md5sum = digest.digest();
			BigInteger bigInt = new BigInteger(1, md5sum);
			String output = bigInt.toString(16);
			// Fill to 32 chars
			output = String.format("%32s", output).replace(' ', '0');
			return output;
		} catch (IOException e) {
			throw new RuntimeException("Unable to process file for MD5", e);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				Log.e("FileUtils", "Exception on closing MD5 input stream", e);
			}
		}
	}

	/** 计算文件的md5值 */
	public static String calculateMD5(File updateFile, int offset, int partSize) {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			Log.e("FileUtils", "Exception while getting digest", e);
			return null;
		}

		InputStream is;
		try {
			is = new FileInputStream(updateFile);
		} catch (FileNotFoundException e) {
			Log.e("FileUtils", "Exception while getting FileInputStream", e);
			return null;
		}

		//DigestInputStream
		final int buffSize = 8192;//单块大小
		byte[] buffer = new byte[buffSize];
		int read;
		try {
			if (offset > 0) {
				is.skip(offset);
			}
			int byteCount = Math.min(buffSize, partSize), byteLen = 0;
			while ((read = is.read(buffer, 0, byteCount)) > 0 && byteLen < partSize) {
				digest.update(buffer, 0, read);
				byteLen += read;
				//检测最后一块，避免多读数据
				if (byteLen + buffSize > partSize) {
					byteCount = partSize - byteLen;
				}
			}
			byte[] md5sum = digest.digest();
			BigInteger bigInt = new BigInteger(1, md5sum);
			String output = bigInt.toString(16);
			// Fill to 32 chars
			output = String.format("%32s", output).replace(' ', '0');
			return output;
		} catch (IOException e) {
			throw new RuntimeException("Unable to process file for MD5", e);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				Log.e("FileUtils", "Exception on closing MD5 input stream", e);
			}
		}
	}

    /** 检测文件是否可用 */
    public static boolean checkFile(File f) {
        if (f != null && f.exists() && f.canRead() && (f.isDirectory() || (f.isFile() && f.length() > 0))) {
            return true;
        }
        return false;
    }

    /** 检测文件是否可用 */
    public static boolean checkFile(String path) {
        if (!TextUtils.isEmpty(path)) {
            File f = new File(path);
            if (f != null && f.exists() && f.canRead() && (f.isDirectory() || (f.isFile() && f.length() > 0)))
                return true;
        }
        return false;
    }

	public static long getFileSize(String fn) {
		File f = null;
		long size = 0;

		try {
			f = new File(fn);
			size = f.length();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			f = null;
		}
		return size < 0 ? null : size;
	}

	public static long getFileSize(File fn) {
		return fn == null ? 0 : fn.length();
	}

	public static String getFileType(String fn, String defaultType) {
		FileNameMap fNameMap = URLConnection.getFileNameMap();
		String type = fNameMap.getContentTypeFor(fn);
		return type == null ? defaultType : type;
	}

	public static String getFileType(String fn) {
		return getFileType(fn, "application/octet-stream");
	}

	public static String getFileExtension(String filename) {
		String extension = "";
		if (filename != null) {
			int dotPos = filename.lastIndexOf(".");
			if (dotPos >= 0 && dotPos < filename.length() - 1) {
				extension = filename.substring(dotPos + 1);
			}
		}
		return extension.toLowerCase();
	}

	public static boolean deleteFile(File f) {
		if (f != null && f.exists() && !f.isDirectory()) {
			return f.delete();
		}
		return false;
	}

	public static void deleteDir(File f) {
		if (f != null && f.exists() && f.isDirectory()) {
			for (File file : f.listFiles()) {
				if (file.isDirectory())
					deleteDir(file);
				file.delete();
			}
			f.delete();
		}
	}

	public static void deleteDir(String f) {
		if (f != null && f.length() > 0) {
			deleteDir(new File(f));
		}
	}

	public static boolean deleteFile(String f) {
		if (f != null && f.length() > 0) {
			return deleteFile(new File(f));
		}
		return false;
	}

	/**
	 * read file
	 * 
	 * @param file
	 * @param charsetName
	 *            The nick_name of a supported {@link java.nio.charset.Charset
	 *            </code>charset<code>}
	 * @return if file not exist, return null, else return content of file
	 * @throws RuntimeException
	 *             if an error occurs while operator BufferedReader
	 */
	public static String readFile(File file, String charsetName) {
		StringBuilder fileContent = new StringBuilder("");
		if (file == null || !file.isFile()) {
			return fileContent.toString();
		}

		BufferedReader reader = null;
		try {
			InputStreamReader is = new InputStreamReader(new FileInputStream(file), charsetName);
			reader = new BufferedReader(is);
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (!fileContent.toString().equals("")) {
					fileContent.append("\r\n");
				}
				fileContent.append(line);
			}
			reader.close();
		} catch (IOException e) {
			throw new RuntimeException("IOException occurred. ", e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					throw new RuntimeException("IOException occurred. ", e);
				}
			}
		}
		return fileContent.toString();
	}

	public static String readFile(String filePath, String charsetName) {
		return readFile(new File(filePath), charsetName);
	}

	public static String readFile(File file) {
		return readFile(file, "utf-8");
	}

	/**
	 * 文件拷贝
	 * 
	 * @param from
	 * @param to
	 * @return
	 */
	public static boolean fileCopy(String from, String to) {
		boolean result = false;

		int size = 1 * 1024;

		FileInputStream in = null;
		FileOutputStream out = null;
		try {
			in = new FileInputStream(from);
			out = new FileOutputStream(to);
			byte[] buffer = new byte[size];
			int bytesRead = -1;
			while ((bytesRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
			}
			out.flush();
			result = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
			}
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
			}
		}
		return result;
	}

	/**
	 * 获取文件夹大小
	 * @param file File实例
	 * @return long
	 */
	public static long getFolderSize(File file){

		long size = 0;
		try {
			File[] fileList = file.listFiles();
			for (int i = 0; i < fileList.length; i++)
			{
				if (fileList[i].isDirectory())
				{
					size = size + getFolderSize(fileList[i]);

				}else{
					size = size + fileList[i].length();

				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//return size/1048576;
		return size;
	}

	/**
	 * 格式化单位
	 * @param size
	 * @return
	 */
	public static String getFormatSize(double size) {
		double kiloByte = size/1024;
		if(kiloByte < 1) {
			return size + "B";
		}

		double megaByte = kiloByte/1024;
		if(megaByte < 1) {
			BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
			return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";
		}

		double gigaByte = megaByte/1024;
		if(gigaByte < 1) {
			BigDecimal result2  = new BigDecimal(Double.toString(megaByte));
			return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";
		}

		double teraBytes = gigaByte/1024;
		if(teraBytes < 1) {
			BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
			return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
		}
		BigDecimal result4 = new BigDecimal(teraBytes);
		return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";
	}

	/**
	 * 根据byte数组，生成文件
	 */
	public static String getFile(byte[] bfile, String filePath,String fileName) {
		BufferedOutputStream bos = null;
		FileOutputStream fos = null;
		File file = null;
		try {
			File dir = new File(filePath);
			if(!dir.exists()&&dir.isDirectory()){//判断文件目录是否存在
				dir.mkdirs();
			}
			file = new File(filePath, fileName);
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			bos.write(bfile);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}

		}
		if (file != null) {
			return file.getAbsolutePath();
		}
		return "";
	}

	public static File getDiskCacheDir(Context context, String uniqueName) {
		// Check if media is mounted or storage is built-in, if so, try and use external cache dir
		// otherwise use internal cache dir
		final String cachePath =
				Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
						!isExternalStorageRemovable() ? context.getExternalCacheDir().getPath() :
						context.getCacheDir().getPath();

		return new File(cachePath + File.separator + uniqueName);
	}


	/**
	 * 持久化对象
	 */
	public static void storeObject(Object obj, File file){
		try {
			ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file));
			outputStream.writeObject(obj);
			outputStream.flush();
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 加载对象
	 */
	public static Object loadObject(File file) {
		Object obj = null;
		try {
			ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file));
			obj = inputStream.readObject();
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return obj;
	}

	/*
	 * Java文件操作 获取文件扩展名
	 */
	public static String getExtensionName(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot >-1) && (dot < (filename.length() - 1))) {
				return filename.substring(dot + 1);
			}
		}
		return filename;
	}
	/*
	 * Java文件操作 获取不带扩展名的文件名
	 *
	 */
	public static String getFileNameNoEx(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot >-1) && (dot < (filename.length()))) {
				return filename.substring(0, dot);
			}
		}
		return filename;
	}

	public static boolean checkFileIsGif(String path) {
		try {
			RandomAccessFile file = new RandomAccessFile(path, "r");
			Byte byte1 = file.readByte();
			Byte byte2 = file.readByte();
			Byte byte3 = file.readByte();
			Byte byte4 = file.readByte();
			if (byte1 == 0x47 && byte2 == 0x49 && byte3 == 0x46 && byte4 == 0x38) {
				return true;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}


	/**
	 * Get a file path from a Uri. This will get the the path for Storage Access
	 * Framework Documents, as well as the _data field for the MediaStore and
	 * other file-based ContentProviders.
	 *
	 * @param context The context.
	 * @param uri The Uri to query.
	 * @author paulburke
	 */
	public static String getPath(final Context context, final Uri uri) {
		// DocumentProvider
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
			// ExternalStorageProvider
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/" + split[1];
				}

				// TODO handle non-primary volumes
			}
			// DownloadsProvider
			else if (isDownloadsDocument(uri)) {

				final String id = DocumentsContract.getDocumentId(uri);
				Uri contentUri = uri;
				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
					contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
				}

				return getDataColumn(context, contentUri, null, null);
			}
			// MediaProvider
			else if (isMediaDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}

				final String selection = "_id=?";
				final String[] selectionArgs = new String[] {
						split[1]
				};

				return getDataColumn(context, contentUri, selection, selectionArgs);
			}
		}
		// MediaStore (and general)
		else if ("content".equalsIgnoreCase(uri.getScheme())) {
			return getDataColumn(context, uri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	}

	/**
	 * Get the value of the data column for this Uri. This is useful for
	 * MediaStore Uris, and other file-based ContentProviders.
	 *
	 * @param context The context.
	 * @param uri The Uri to query.
	 * @param selection (Optional) Filter used in the query.
	 * @param selectionArgs (Optional) Selection arguments used in the query.
	 * @return The value of the _data column, which is typically a file path.
	 */
	public static String getDataColumn(Context context, Uri uri, String selection,
									   String[] selectionArgs) {

		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = {
				column
		};

		try {
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
					null);
			if (cursor != null && cursor.moveToFirst()) {
				final int column_index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(column_index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}


	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}

	public static String getPathFromUri(Context context, Uri uri) {
		String path = null;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
			//如果是document类型的Uri，通过document id处理，内部会调用Uri.decode(docId)进行解码
			String docId = DocumentsContract.getDocumentId(uri);
			//primary:Azbtrace.txt
			//video:A1283522
			String[] splits = docId.split(":");
			String type = null, id = null;
			if(splits.length == 2) {
				type = splits[0];
				id = splits[1];
			}
			switch (uri.getAuthority()) {
				case "com.android.externalstorage.documents":
					if("primary".equals(type)) {
						path = Environment.getExternalStorageDirectory() + File.separator + id;
					}
					break;
				case "com.android.providers.downloads.documents":
					if("raw".equals(type)) {
						path = id;
					} else {
						Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
						path = getMediaPathFromUri(context, contentUri, null, null);
					}
					break;
				case "com.android.providers.media.documents":
					Uri externalUri = null;
					switch (type) {
						case "image":
							externalUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
							break;
						case "video":
							externalUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
							break;
						case "audio":
							externalUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
							break;
					}
					if(externalUri != null) {
						String selection = "_id=?";
						String[] selectionArgs = new String[]{ id };
						path = getMediaPathFromUri(context, externalUri, selection, selectionArgs);
					}
					break;
			}
		} else if (ContentResolver.SCHEME_CONTENT.equalsIgnoreCase(uri.getScheme())) {
			path = getMediaPathFromUri(context, uri, null, null);
		} else if (ContentResolver.SCHEME_FILE.equalsIgnoreCase(uri.getScheme())) {
			//如果是file类型的Uri(uri.fromFile)，直接获取图片路径即可
			path = uri.getPath();
		}
		//确保如果返回路径，则路径合法
		return path == null ? null : (new File(path).exists() ? path : null);
	}

	private static String getMediaPathFromUri(Context context, Uri uri, String selection, String[] selectionArgs) {
		String path;
		String authroity = uri.getAuthority();
		path = uri.getPath();
		String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
		if(!path.startsWith(sdPath)) {
			int sepIndex = path.indexOf(File.separator, 1);
			if(sepIndex == -1) path = null;
			else {
				path = sdPath + path.substring(sepIndex);
			}
		}

		if(path == null || !new File(path).exists()) {
			ContentResolver resolver = context.getContentResolver();
			String[] projection = new String[]{ MediaStore.MediaColumns.DATA };
			Cursor cursor = resolver.query(uri, projection, selection, selectionArgs, null);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					try {
						int index = cursor.getColumnIndexOrThrow(projection[0]);
						if (index != -1) path = cursor.getString(index);
//						Log.i(TAG, "getMediaPathFromUri query " + path);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
						path = null;
					} finally {
						cursor.close();
					}
				}
			}
		}
		return path;
	}


}
