package com.openplatform.adas.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


/**
 * ShellUtils
 * <ul>
 * <strong>Check root</strong>
 * <li>{@link ShellUtils#checkRootPermission()}</li>
 * </ul>
 * <ul>
 * <strong>Execte command</strong>
 * <li>{@link ShellUtils#execCommand(String, boolean)}</li>
 * <li>{@link ShellUtils#execCommand(String, boolean, boolean)}</li>
 * <li>{@link ShellUtils#execCommand(List, boolean)}</li>
 * <li>{@link ShellUtils#execCommand(List, boolean, boolean)}</li>
 * <li>{@link ShellUtils#execCommand(String[], boolean)}</li>
 * <li>{@link ShellUtils#execCommand(String[], boolean, boolean)}</li>
 * </ul>
 *
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2013-5-16
 */
public class ShellUtils {
	public static final String COMMAND_SU = "su";
	public static final String COMMAND_SH = "sh";
	public static final String COMMAND_EXIT = "exit\n";
	public static final String COMMAND_LINE_END = "\n";

	private ShellUtils() {
		throw new AssertionError();
	}

	/**
	 * check whether has root permission
	 *
	 * @return
	 */
	public static boolean checkRootPermission() {
		return execCommand("echo root", true, false).result == 0;
	}

	/**
	 * execute shell command, default return result msg
	 *
	 * @param command
	 *            command
	 * @param isRoot
	 *            whether need to run with root
	 * @return
	 * @see ShellUtils#execCommand(String[], boolean, boolean)
	 */
	public static CommandResult execCommand(String command, boolean isRoot) {
		return execCommand(new String[] { command }, isRoot, true);
	}

	/**
	 * execute shell commands, default return result msg
	 *
	 * @param commands
	 *            command list
	 * @param isRoot
	 *            whether need to run with root
	 * @return
	 * @see ShellUtils#execCommand(String[], boolean, boolean)
	 */
	public static CommandResult execCommand(List<String> commands, boolean isRoot) {
		return execCommand(commands == null ? null : commands.toArray(new String[] {}), isRoot, true);
	}

	/**
	 * execute shell commands, default return result msg
	 *
	 * @param commands
	 *            command array
	 * @param isRoot
	 *            whether need to run with root
	 * @return
	 * @see ShellUtils#execCommand(String[], boolean, boolean)
	 */
	public static CommandResult execCommand(String[] commands, boolean isRoot) {
		return execCommand(commands, isRoot, true);
	}

	/**
	 * execute shell command
	 *
	 * @param command
	 *            command
	 * @param isRoot
	 *            whether need to run with root
	 * @param isNeedResultMsg
	 *            whether need result msg
	 * @return
	 * @see ShellUtils#execCommand(String[], boolean, boolean)
	 */
	public static CommandResult execCommand(String command, boolean isRoot, boolean isNeedResultMsg) {
		return execCommand(new String[] { command }, isRoot, isNeedResultMsg);
	}

	/**
	 * execute shell commands
	 *
	 * @param commands
	 *            command list
	 * @param isRoot
	 *            whether need to run with root
	 * @param isNeedResultMsg
	 *            whether need result msg
	 * @return
	 * @see ShellUtils#execCommand(String[], boolean, boolean)
	 */
	public static CommandResult execCommand(List<String> commands, boolean isRoot, boolean isNeedResultMsg) {
		return execCommand(commands == null ? null : commands.toArray(new String[] {}), isRoot, isNeedResultMsg);
	}

	/**
	 * execute shell commands
	 *
	 * @param commands
	 *            command array
	 * @param isRoot
	 *            whether need to run with root
	 * @param isNeedResultMsg
	 *            whether need result msg
	 * @return <ul>
	 *         <li>if isNeedResultMsg is false, {@link CommandResult#successMsg}
	 *         is null and {@link CommandResult#errorMsg} is null.</li>
	 *         <li>if {@link CommandResult#result} is -1, there maybe some
	 *         excepiton.</li>
	 *         </ul>
	 */
	public static CommandResult execCommand(String[] commands, boolean isRoot, boolean isNeedResultMsg) {
		int result = -1;
		if (commands == null || commands.length == 0) {
			return new CommandResult(result, null, null);
		}
		Process process = null;
		List<String> successMsgList = new ArrayList<String>();
		List<String> errorMsgList = new ArrayList<String>();
		StringBuffer errorMsg = null;
		DataOutputStream os = null;
		try {
			process = Runtime.getRuntime().exec(isRoot ? COMMAND_SU : COMMAND_SH);
			
			// 创建2个线程，分别读取输入流缓冲区和错误流缓冲区
            ThreadUtil stdoutUtil = new ThreadUtil(process.getInputStream(), successMsgList);
            ThreadUtil erroroutUtil = new ThreadUtil(process.getErrorStream(), errorMsgList);
            //启动线程读取缓冲区数据
            stdoutUtil.start();
            erroroutUtil.start();
			
			os = new DataOutputStream(process.getOutputStream());
			for (String command : commands) {
				if (command == null) {
					continue;
				}
				// donnot use os.writeBytes(commmand), avoid chinese charset
				// error
				os.write(command.getBytes());
				os.writeBytes(COMMAND_LINE_END);
				os.flush();
			}
			os.writeBytes(COMMAND_EXIT);
			os.flush();
			
			result = process.waitFor();
			// get command result
			if (isNeedResultMsg) {
				if(errorMsgList.size()>0) errorMsg = new StringBuffer();
				for(String error:errorMsgList){
					errorMsg.append(error);
				}
			
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (os != null) {
					os.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (process != null) {
				process.destroy();
			}
		}
		return new CommandResult(result, successMsgList == null ? null : successMsgList, errorMsg == null ? null
				: errorMsg.toString());
	}
	
	static class ThreadUtil implements Runnable {
	    private List<String> list;
	    private InputStream inputStream;

	    public ThreadUtil(InputStream inputStream, List<String> list) {
	        this.inputStream = inputStream;
	        this.list = list;
	    }

	    public void start() {
	        Thread thread = new Thread(this);
	        thread.setDaemon(true);
	        thread.start();
	    }

	    public void run() {
	        BufferedReader br = null;
	        try {
	            br = new BufferedReader(new InputStreamReader(inputStream));
	            String line = null;
	            while ((line = br.readLine()) != null) {
	                if (line != null) {
	                    list.add(line);
	                }
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            try {
	                inputStream.close();
	                br.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	    }

	}

	/**
	 * result of command
	 * <ul>
	 * <li>{@link CommandResult#result} means result of command, 0 means normal,
	 * else means error, same to excute in linux shell</li>
	 * <li>{@link CommandResult#successMsg} means success message of command
	 * result</li>
	 * <li>{@link CommandResult#errorMsg} means error message of command result</li>
	 * </ul>
	 *
	 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a>
	 *         2013-5-16
	 */
	public static class CommandResult {
		/** result of command **/
		public int result;
		/** success message of command result **/
		public List<String> successMsg;
		/** error message of command result **/
		public String errorMsg;

		public CommandResult(int result) {
			this.result = result;
		}

		public CommandResult(int result, List<String> successMsg, String errorMsg) {
			this.result = result;
			this.successMsg = successMsg;
			this.errorMsg = errorMsg;
		}
	}
}