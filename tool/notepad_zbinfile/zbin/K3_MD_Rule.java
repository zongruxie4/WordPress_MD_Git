

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.*;

import java.math.BigInteger;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.Cipher;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.swing.*;


import java.security.Key;
import java.security.Security;

//import com.google.common.collect.Maps;
import com.sun.crypto.provider.SunJCE;
//import com.google.common.collect.Maps;2

// 对于  文件类型_操作Index  执行对应的操作逻辑
public class K3_MD_Rule {



    //  类型_索引  ，对当前类型的文件执行索引执行的操作     html1---对html类型的子文件执行 索引为1 的逻辑操作  String apply(String)
    static ArrayList<String> Rule_Identify_TypeIndexList = new ArrayList<String>();
    static String Cur_Git_Name = "zukgit.github.io";

    static String Cur_Batch_End = ".bat";

    static String Cur_Bat_Name = "zmdrule_K3";
    static String zbinPath = System.getProperties().getProperty("user.home") + File.separator + "Desktop" + File.separator + "zbin";
    static String K3_File_Path = zbinPath+File.separator+"K3";
    static String Win_Lin_Mac_ZbinPath = "";


    static File K3_Properties_File = new File(System.getProperties().getProperty("user.home") + File.separator + "Desktop" + File.separator + "zbin" + File.separator + "K3.properties");
    static InputStream K3_Properties_InputStream;
    static OutputStream K3_Properties_OutputStream;
    static Properties K3_Properties = new Properties();
    static Map<String, String> propKey2ValueList = new HashMap<String, String>();


    static int BYTE_CONTENT_LENGTH_Rule7= 1024 * 10 * 10;   // 读取文件Head字节数常数
    static String strDefaultKey_Rule7 = "zukgit12"; //  8-length

    static  String strZ7DefaultKey_PSW_Rule19 = "752025";  // 8-length
    public static byte[] TEMP_Rule7 = new byte[BYTE_CONTENT_LENGTH_Rule7];

	static File K3_Temp_Text_File = new File(
			System.getProperties().getProperty("user.home") + File.separator + "Desktop" + File.separator + "zbin"
					+ File.separator + get_Bat_Sh_FlagNumber(Cur_Bat_Name) + "_Temp_Text.txt");
	

    static {
        try {
            if (!K3_Properties_File.exists()) {
                K3_Properties_File.createNewFile();
            }
            K3_Properties_InputStream = new BufferedInputStream(new FileInputStream(K3_Properties_File.getAbsolutePath()));
            K3_Properties.load(K3_Properties_InputStream);
            Iterator<String> it = K3_Properties.stringPropertyNames().iterator();
            while (it.hasNext()) {
                String key = it.next();
                // System.out.println("key:" + key + " value: " + K3_Properties.getProperty(key));
                propKey2ValueList.put(key, K3_Properties.getProperty(key));
            }
            K3_Properties_InputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    static void setProperity() {
        try {
            K3_Properties_OutputStream = new BufferedOutputStream(new FileOutputStream(K3_Properties_File.getAbsolutePath()));
            K3_Properties.store(K3_Properties_OutputStream, "");
            K3_Properties_OutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    enum OS_TYPE {
        Windows,
        Linux,
        MacOS
    }

    // JDK 的路径
    static String JDK_BIN_PATH = "";

    static OS_TYPE CUR_OS_TYPE = OS_TYPE.Windows;
    static String curOS_ExeTYPE = "";
    static ArrayList<String> mKeyWordName = new ArrayList<>();

    // 当前Shell目录下的 文件类型列表  抽取出来  通用
    static  HashMap<String, ArrayList<File>> CurDirFileTypeMap = new  HashMap<String, ArrayList<File>>(); ;

    static void initSystemInfo() {
        String osName = System.getProperties().getProperty("os.name").toLowerCase();
        String curLibraryPath =  System.getProperties().getProperty("java.library.path");
        if (osName.contains("window")) {
            CUR_OS_TYPE = OS_TYPE.Windows;
            Cur_Bat_Name = Cur_Bat_Name+".bat";
            Cur_Batch_End = ".bat";
            curOS_ExeTYPE = ".exe";
            initJDKPath_Windows(curLibraryPath);
            Win_Lin_Mac_ZbinPath = zbinPath+File.separator+"win_zbin";

        } else if (osName.contains("linux")) {
            CUR_OS_TYPE = OS_TYPE.Linux;
            Cur_Bat_Name = Cur_Bat_Name+".sh";
            curOS_ExeTYPE = "";
            Cur_Batch_End = ".sh";
            initJDKPath_Linux_MacOS(curLibraryPath);
            Win_Lin_Mac_ZbinPath = zbinPath+File.separator+"lin_zbin";

        } else if (osName.contains("mac")) {
            CUR_OS_TYPE = OS_TYPE.MacOS;
            Cur_Bat_Name = Cur_Bat_Name+".sh";
            curOS_ExeTYPE = "";
            Cur_Batch_End = ".sh";
            initJDKPath_Linux_MacOS(curLibraryPath);
            Win_Lin_Mac_ZbinPath = zbinPath+File.separator+"mac_zbin";

        }



    }

    static void initJDKPath_Linux_MacOS(String environmentPath){
        String[] environmentArr = environmentPath.split(":");
        for (int i = 0; i < environmentArr.length; i++) {
            String pathItem = environmentArr[i];
            if(pathItem.contains("jdk")&&pathItem.contains("bin")){
                JDK_BIN_PATH = pathItem;
            }
        }
    }



    static void initJDKPath_Windows(String environmentPath){
        String[] environmentArr = environmentPath.split(";");
        for (int i = 0; i < environmentArr.length; i++) {
            String pathItem = environmentArr[i];
            if(pathItem.contains("jdk")&&pathItem.contains("bin")){
                JDK_BIN_PATH = pathItem;
            }
        }
    }



    static String curDirPath = "";   // 当前 SHELL  所在目录  默认是main中的第一个 arg[0] 就是shell路径
    static File curDirFile ;



    private static String REGEX_CHINESE = "[\u4e00-\u9fa5]";

    public static String clearChinese(String lineContent) {
        if (lineContent == null || lineContent.trim().isEmpty()) {
            return null;
        }
        Pattern pat = Pattern.compile(REGEX_CHINESE);
        Matcher mat = pat.matcher(lineContent);
        return mat.replaceAll(" ");
    }



    void InitRule(){
    	 realTypeRuleList.add( new FixedImgSrc_Rule_1());
    	 realTypeRuleList.add( new Head_AddOne_Rule_2());
    	 realTypeRuleList.add( new Head_DeleteOne_Rule_3());
    	 
    	 realTypeRuleList.add( new Fixed_Table_Rule_4());
    	 
     	 realTypeRuleList.add( new Time_Head_Rule_5());
     	 
     	 realTypeRuleList.add( new Sort_ABCDZ_Rule_6());
     	 
     	 
//    	 1. 为每个代码块 起始  添加  code 字样 
//    	 2. 为 每个代码块开头 去除 字样
    	 
//    	  realTypeRuleList.add( new HTML_Rule_1());
    	  
//        realTypeRuleList.add( new HTML_Rule_1());
//        realTypeRuleList.add( new File_Name_Rule_2());
//        realTypeRuleList.add( new Image2Jpeg_Rule_3());
//        realTypeRuleList.add( new Image2Png_Rule_4());
//        realTypeRuleList.add( new AVI_Rule_5());
//        realTypeRuleList.add( new SubDirRename_Rule_6());
//        realTypeRuleList.add( new Encropty_Rule_7());
//        realTypeRuleList.add( new ClearChineseType_8());
//        realTypeRuleList.add( new FileType_Rule_9());
//        realTypeRuleList.add( new DirOperation_Rule_10());
//        realTypeRuleList.add( new AllDirSubFile_Order_Rule_11());
//
//        realTypeRuleList.add( new CalCulMediaHtml_Rule_12());
//        realTypeRuleList.add( new CalMP4_DIR_HTML_Rule_13());
//        realTypeRuleList.add( new CreateIconFile_KuaiJieFangShi_Rule_14());
//        realTypeRuleList.add( new Webp_To_Jpg_Gif_Rule_15());
//
//        realTypeRuleList.add( new File_TimeName_Rule_16());
//        realTypeRuleList.add( new Make_ZRuleDir_Rule_17());
//        realTypeRuleList.add( new MD_ReName_Rule_18());
//        realTypeRuleList.add( new ExpressTo7z_PassWord_Rule_19());
//        realTypeRuleList.add( new Land_Port_Classify_Rule_20());
//        realTypeRuleList.add( new Rename_Img_WithSize_Rule_21());
//        realTypeRuleList.add( new ReSize_Img_Rule_22());
//        realTypeRuleList.add( new Append_Pdf_Rule_23());  //  把 pdf 文件 追加 合并为 一个文件
//        
        
    }


// 3038年 5 月 3 日


    // operation_type  操作类型     1--读取文件内容字符串 进行修改      2--对文件对文件内容(字节)--进行修改    3.对全体子文件进行的随性的操作 属性进行修改(文件名称)
//     // 4.对当前子文件(包括子目录 子文件 --不包含孙目录 孙文件)   5. 所有文件 目录进行操作

    
	class Sort_ABCDZ_Rule_6 extends Basic_Rule {
		
		Sort_ABCDZ_Rule_6() {
			super("#", 6, 4);

		}
		
		@Override
		String simpleDesc() {
			return "\n"
     	+ Cur_Bat_Name + "  #_"+rule_index+"    ##打开notepad输出 #A #B  #C   #D  #E 的顺序列表 \n" 
		+ Cur_Bat_Name + "  #_"+rule_index+"    ##打开notepad输出 #A #B  #C   #D  #E 的顺序列表 \n" ;
			}
		
		@Override
		ArrayList<File> applySubFileListRule4(ArrayList<File> curFileList, HashMap<String, ArrayList<File>> subFileTypeMap,
			ArrayList<File> curDirList, ArrayList<File> curRealFileList) {
		// TODO Auto-generated method stub
			
			StringBuilder sb  = new StringBuilder();
			sb.append("# A\n\n\n");
			sb.append("# B\n\n\n");
			sb.append("# C\n\n\n");
			sb.append("# D\n\n\n");
			sb.append("# E\n\n\n");
			sb.append("# F\n\n\n");
			sb.append("# G\n\n\n");
			sb.append("# H\n\n\n");
			sb.append("# I\n\n\n");
			sb.append("# J\n\n\n");
			sb.append("# K\n\n\n");
			sb.append("# L\n\n\n");
			sb.append("# M\n\n\n");
			sb.append("# N\n\n\n");
			sb.append("# O\n\n\n");
			sb.append("# P\n\n\n");
			sb.append("# Q\n\n\n");
			sb.append("# R\n\n\n");
			sb.append("# S\n\n\n");
			sb.append("# T\n\n\n");
			sb.append("# U\n\n\n");
			sb.append("# V\n\n\n");
			sb.append("# W\n\n\n");
			sb.append("# X\n\n\n");
			sb.append("# Y\n\n\n");
			sb.append("# Z\n\n\n");

			writeContentToFile(K3_Temp_Text_File, sb.toString());
			NotePadOpenTargetFile(K3_Temp_Text_File.getAbsolutePath());
			
		
			return super.applySubFileListRule4(curFileList, subFileTypeMap, curDirList, curRealFileList);
		}
		
	}



		class Time_Head_Rule_5 extends Basic_Rule {
			int originType;
			int targetType;

			Time_Head_Rule_5() {
				super("#", 5, 5);
				originType = -1;
				targetType = -1;
			}

			@Override
			String simpleDesc() {
				return "\n"
	     	+ Cur_Bat_Name + "  #_5  1992_"+getCurrentYear()+"   ##打开notepad输出当前1992年至今年"+getCurrentYear()+"年 年月历   MD格式类型(年月里) \n" 
			+ Cur_Bat_Name + "  #_5  1992_2020   ##打开notepad输出当前1992年至2020年 年月历   MD格式类型(年月里) \n" 
			+ Cur_Bat_Name + "  #_5  1990_   ##打开notepad输出当前1990年至今年" + getCurrentYear() + " 年月历 MD格式类型(年月里) \n" 
			+ Cur_Bat_Name + " #_5  _2010   ##打开notepad输出 1992(默认)至2010年年月历 MD格式类型(年月里) \n ";
			}

			@Override
			boolean initParamsWithInputList(ArrayList<String> inputParamList) {
				boolean Flag = true;

				// 获取到装换的类型
				String inputFileTypeParams = inputParamList.get(inputParamList.size() - 1);

				if (!inputFileTypeParams.contains("_")) {
					Flag = false;
					System.out.println("无法检测到当前 第5 Rule   起始年份_截止年份   请检查后重新执行");
				} else {

					if (inputFileTypeParams.endsWith("_")) {
						String target = "";
						String[] parmas = inputFileTypeParams.split("_");
						String origin = parmas[0];
						System.out.println("item=" + inputFileTypeParams + "   origin=" + origin + "     target=" + target);
						if (isNumeric(origin) && !"".equals(origin)) {
							originType = Integer.parseInt(origin);
						}

						if (isNumeric(target) && !"".equals(target)) {
							targetType = Integer.parseInt(target);
						}
					} else {
						String[] parmas = inputFileTypeParams.split("_");
						System.out.println(
								"item=" + inputFileTypeParams + "   origin=" + parmas[0] + "     target=" + parmas[1]);
						if (parmas.length >= 2) {

							if (isNumeric(parmas[0])) {
								originType = Integer.parseInt(parmas[0]);
							}

							if (isNumeric(parmas[1])) {
								targetType = Integer.parseInt(parmas[1]);
							}

						}

					}

					Flag = true;

				}

				return super.initParamsWithInputList(inputParamList) && Flag;
			}

			@Override
			ArrayList<File> applyDir_SubFileListRule5(ArrayList<File> allSubDirFileList,
					ArrayList<File> allSubRealFileList) {
				// TODO Auto-generated method stub
				// TODO Auto-generated method stub
				if (originType == -1) { // 没有获取到 初始化值 那么 默认就是 1992
					originType = 1992;
				}

				if (targetType == -1) { // 没有获取到 初始化值 那么 默认就是 1992
					targetType = getCurrentYear();
				}

				// 确保 origin 是 小于 targetType 的
				if (originType > targetType) {
					targetType = originType + targetType;
					originType = targetType - originType;
					targetType = targetType - originType;
				}
				StringBuilder sb = new StringBuilder();
	System.out.println("targetType = "+targetType +"    originType="+ originType);
				for (int i = targetType; i >= originType; i--) {
					sb.append("   \n");
					sb.append("   \n");
					sb.append("   \n");

					for (int j = 12; j >= 1; j--) {
						sb.append("### " + i + "." + (j > 9 ? "" + j : "0" + j));

						sb.append("   \n");
						sb.append("   \n");
					}

					sb.append("## " + i);
					sb.append("   \n");
					sb.append("   \n");

				}
				writeContentToFile(K3_Temp_Text_File, sb.toString());
				NotePadOpenTargetFile(K3_Temp_Text_File.getAbsolutePath());
				return super.applyDir_SubFileListRule5(allSubDirFileList, allSubRealFileList);
			}

		}
	
    
    
    
    class Fixed_Table_Rule_4 extends Basic_Rule{
    	Fixed_Table_Rule_4(){
            super("#",4,5);
        }
   	
	   String simpleDesc(){
          return "对当前的 table 形式的 表格字符串进行合规检查 并 修复 不合格 table字符串";
      }
	
	   
	   	@Override
	   	ArrayList<File> applyDir_SubFileListRule5(ArrayList<File> allSubDirFileList, ArrayList<File> allSubRealFileList) {
	   	// TODO Auto-generated method stub
	   	
	   		
	   		ArrayList<File>  mdFileList	 = getSubFileList(allSubRealFileList,".md");
	       	
	   		if(mdFileList == null) {
	       		System.out.println("当前 Rule4  检测到的 md 文件的数量为空 null ! 无法执行规则#1 mdFileList.size() = null ");	
	       		return super.applyDir_SubFileListRule5(allSubDirFileList, allSubRealFileList);
	   	
	   		}
	   		System.out.println("mdFileList.size() = "+ mdFileList.size());
	   	 
	   		if(mdFileList.size() == 0) {
	       		System.out.println("当前 Rule4  检测到的 md 文件的数量为空! 无法执行规则#1 mdFileList.size() = "+ mdFileList.size());	
	       		return super.applyDir_SubFileListRule5(allSubDirFileList, allSubRealFileList);
	   		}
	   		
	   		
	   		for (int i = 0; i < mdFileList.size(); i++) {
	   			File mdFile = mdFileList.get(i);
	   			
	   			ArrayList<String> mdContentList = readListFromFile(mdFile);
	   			ArrayList<String> raw_mdContentList = new ArrayList<String> ();
	   			raw_mdContentList.addAll(mdContentList);
	   	 		System.out.println("Rule4 test ------------- ");
	   			ArrayList<String> fixedMdContentList = try_fixed_table(mdContentList);
	   
	   			if(!fixedMdContentList.equals(raw_mdContentList)) {
	   	 			writeContentToFile(mdFile, fixedMdContentList);
	   				System.out.println("xx 检查异常 需要重新写入! ->"+ mdFile.getAbsolutePath());

	   			}else {
	   				System.out.println("vv 检查正常 未重新写入! ->"+ mdFile.getAbsolutePath());
	   			}
	  
				}
	   		
	   		return super.applyDir_SubFileListRule5(allSubDirFileList, allSubRealFileList);

	   	}
	   	
	   	
	   	volatile  	 boolean isTableBegin = false;
	   	volatile	boolean isInCodeBlock = false;
			
	   	ArrayList<String>  try_fixed_table(ArrayList<String> rawMdList ){
	   		ArrayList<String>  resultList = new ArrayList<String> ();
	   	
	   		
	   		ArrayList<ArrayList<String>> fixedTableList_List = new  	ArrayList<ArrayList<String>>();
	   		
	   		ArrayList<Map<Integer , String>> index_fixContent_MapList = new ArrayList<Map<Integer , String>>  ();
//	   		Map<Integer , String> oneTableList = new HashMap<Integer , String>();
	  		Map<Integer , String> oneTableList =  new LinkedHashMap<Integer , String>();
	   		
	   		for (int i = 0; i < rawMdList.size(); i++) {
				
	
	   			String oneLine = rawMdList.get(i);
	   			isTableBegin = isTableContent(oneLine);
//	   			isInCodeBlock = isInCodeBlock(oneLine);
//	   			System.out.println();
//	   			System.out.println("isInCodeBlock = "+ isInCodeBlock + "  isTableBegin ="+ isTableBegin);
//	   			System.out.println("oneLine = "+ oneLine);
	   			
	   		if(!isInCodeBlock(oneLine) && isTableBegin ) {
	   			// 如果不是在 codeblock 内   并且 是  ||| 样子类型的 
	   			System.out.println("  indexKey:"+i+"   value:"+oneLine);
	   			oneTableList.put(i, oneLine);
	   		}else {
	   			if(oneTableList.size() > 0) {
	   				index_fixContent_MapList.add(oneTableList)	;
	   		   	     oneTableList =  new LinkedHashMap<Integer , String>();
	   			}
	   			
	   		}

		}
	   		
	   	  	ArrayList<Map<Integer , String>> fixedMap_list = 	FixedTableMap(index_fixContent_MapList);
	   		
//	   	 resultList = 	 getFixedList(fixedMap_list,rawMdList);
	   	resultList.addAll(getFixedList(fixedMap_list,rawMdList));
	   	  	
	   	  	
	   		return  rawMdList;
	   		
	   	}
	   	
	   	
	   	//  把 修复  写进 md 文件中
	   	@SuppressWarnings("unchecked")
	   	ArrayList<String> getFixedList(ArrayList<Map<Integer , String>> table_Map_List ,ArrayList<String> rawMdList  ){
	   		
	   		
	   		for (int i = 0; i < table_Map_List.size(); i++) {

	
	   		HashMap<Integer , String> tableMap = (LinkedHashMap<Integer , String>)table_Map_List.get(i);
	       	Map.Entry<Integer , String> entryItem;
	       	int index = 0 ;
	       	int map_size = tableMap.size();
	    	if(tableMap != null){
	    	    Iterator iterator = tableMap.entrySet().iterator();
	    	    while( iterator.hasNext() ){
	    	        entryItem = (Map.Entry<Integer , String>) iterator.next();
	    	        Integer indexLine =  entryItem.getKey();   //Map的Key
	    	       String tableLine =   entryItem.getValue();  //Map的Value
//    	    	   System.out.println(indexLine+":"+" 需要修复的行!!!  = "+ tableLine);

	    	       if(index == 0 ) {  // 说明是 表头  需要 添加 /r/n  来 确保 表格 上下是空行
	    	    	   int preLine_index = indexLine -1;
	    	    	   if(rawMdList.size() >= preLine_index  && "".equals(rawMdList.get(preLine_index).trim())) {
	    	    		   rawMdList.set(indexLine, tableLine);
	    	    		   index ++;
	    	    		   continue;
	    	    	   }
	    	    	   rawMdList.set(indexLine, "\n"+tableLine);  
	    	       	   System.out.println("prefixed !! indexLine  = "+ indexLine + "   tableLine= "+ tableLine);

	    	    	   index ++;
	    	    	   continue;
	    	       }else if(index == map_size -1) {  // 如果 是 最后一行 那么检查 下一行是否是空格  确保是空格
	    	    	   int backLine_index = indexLine + 1;
	    	 
	    	    	   if(rawMdList.size() >= backLine_index  && "".equals(rawMdList.get(backLine_index).trim())) {
	    	    		   rawMdList.set(indexLine, tableLine);
	    	    		   index ++;
	    	    		   continue;
	    	    	   }
	    	       	   System.out.println("append !! indexLine  = "+ indexLine + "   tableLine= "+ tableLine);
	    	       	   
	    	    	   rawMdList.set(indexLine, tableLine+"\n");  
	    	    	   index ++;
	    	    	   continue;
	    	       }
	    	       
		    	   rawMdList.set(indexLine, tableLine);  
		    	   index ++;
	   		
	    	    }
	    	}


	    	
	   	}
	   		return rawMdList;
	   		
	   	}
	   	
	   	@SuppressWarnings("unchecked")	
	   	void ShowTableMap(ArrayList<Map<Integer , String>> table_Map_List ){
	   		
	   		
	   		for (int i = 0; i < table_Map_List.size(); i++) {

	
		   		LinkedHashMap<Integer, String> tableMap = (LinkedHashMap<Integer , String>)table_Map_List.get(i);
	   		   System.out.println("════════"+i+" fixed table begin ════════");
//	   		Map<Integer , String> fixed_map_item = Maps.newConcurrentMap();
	       	Map.Entry<Integer , String> entryItem;
	    	if(tableMap != null){
	    	    Iterator iterator = tableMap.entrySet().iterator();
	    	    while( iterator.hasNext() ){
	    	        entryItem = (Map.Entry<Integer , String>) iterator.next();
	    	        Integer indexLine =  entryItem.getKey();   //Map的Key
	    	       String tableLine =   entryItem.getValue();  //Map的Value
	    	       int tabletag_count = getCharCount(tableLine, "|");
	    	       System.out.println(indexLine+"["+tabletag_count+"]: "+ tableLine);
	   		
	    	    }
	    	}
	    	
	   	}
	   }
    	
	   	@SuppressWarnings("unchecked")	
	   	ArrayList<Map<Integer , String>> FixedTableMap(ArrayList<Map<Integer , String>> table_Map_List ){
	   		ArrayList<Map<Integer , String>> fixed_table_Map_List = new 	ArrayList<Map<Integer , String>>();
	   	      //  把	修改后的 元素 保存在 这里 
	   		int current_tabletag_count = 0;
	   		
	     
	   		
	   		for (int i = 0; i < table_Map_List.size(); i++) {
	   		LinkedHashMap<Integer, String> tableMap = (LinkedHashMap<Integer , String>)table_Map_List.get(i);
	   		   System.out.println("════════"+i+" table begin ════════");
	   		   
	   			Map<Integer , String> fixed_map_item = new LinkedHashMap<Integer , String>();
	       	Map.Entry<Integer , String> entryItem;
	    	if(tableMap != null){
	    	    Iterator iterator = tableMap.entrySet().iterator();
	    	    
	    	    while( iterator.hasNext() ){
	    	        entryItem = (Map.Entry<Integer , String>) iterator.next();
	    	        Integer indexLine =  entryItem.getKey();   //Map的Key
	    	       String tableLine =   entryItem.getValue();  //Map的Value
	    	       int tabletag_count = getCharCount(tableLine, "|");
	    	       if(current_tabletag_count == 0) {
	    	    	   current_tabletag_count = tabletag_count;   // 取到 表头的  | 线 
	    	       }
	    	       System.out.println(indexLine+"["+tabletag_count+"]: "+ tableLine);
	    	   
	    	       if(tabletag_count != current_tabletag_count) {
	    	    	   System.out.println("需要修复的行!!!  = "+ tableLine);
	    	    	   System.out.println("tabletag_count = "+ tabletag_count + "    current_tabletag_count="+ current_tabletag_count);
	    	    	   // 当前表格 列表格式  缺失 |=== ---- |
	    	    	   String fixed_table_line  = "";
	    	    	   if(current_tabletag_count > tabletag_count) {  // 当前表格缺失
	    	    		    fixed_table_line = tableLine+getCopyStr("      |",current_tabletag_count-tabletag_count);
	    	    		   
	    	    	   }else {   // 当前存在的 行 多了   需要去除   多余的 | 
	    	    		   
	    	    		   int big_number = tabletag_count - current_tabletag_count;
	    	    		   
	    	    		   while(tabletag_count != current_tabletag_count) {
	    	    			   System.out.println("tableLine = "+ tableLine);
	    	    			   fixed_table_line = 	tableLine.substring(0, tableLine.lastIndexOf("|"));
	    	    			   fixed_table_line =     fixed_table_line.substring(0, fixed_table_line.lastIndexOf("|")+1);
	    	    			   tabletag_count =  getCharCount(fixed_table_line, "|");
	    	    		   }
	   
	    	    	   }
	    	    	   System.out.println("put["+indexLine+"] = " + fixed_table_line);
	    	    	   
	    	    	   // | 9    | 38     | Data数据包     | 113 bytes | 52  bytes      把 多余的 后缀去掉
	    	    	   fixed_table_line = fixed_table_line.substring(0,fixed_table_line.lastIndexOf("|")+1);
	    	    	   fixed_map_item.put(indexLine, fixed_table_line);
	    	    	   continue;
	    	       }
	    		   // | 9    | 38     | Data数据包     | 113 bytes | 52  bytes      把 多余的 后缀去掉
	    	       tableLine = tableLine.substring(0,tableLine.lastIndexOf("|")+1);
	 	    	   fixed_map_item.put(indexLine, tableLine);
    	    	   System.out.println("put["+indexLine+"] = " + tableLine);

	    	    }
	    	    
	    	    if(fixed_map_item.size() > 0 ) {
	    	    	fixed_table_Map_List.add(fixed_map_item);
	    	   		System.out.println("fixed_map_item.size() = "+fixed_map_item.size());
	    	    }
	    	}
			}
	   		
	   		System.out.println("fixed_table_Map_List.size() = "+fixed_table_Map_List.size());
	   		ShowTableMap(fixed_table_Map_List);
	   		
	   		return fixed_table_Map_List;
	   		
	   	}
	   	
	   	String getCopyStr(String raw , int copyCount) {
	   		StringBuilder sb = new StringBuilder();
	   		
	   		for (int i = 0; i < copyCount; i++) {
	   			sb.append(raw);
			}
	   		
	   		return sb.toString();
	   	}
	   	
	   	
	   	//  对于 那些 不满足 的  异常的 table 列  | 12   | 34   | 56     不是  |  结尾的
	   	boolean isTableContent(String oneLine ){
	   		boolean flag = false;
	   		String oneLine_trim = oneLine.trim();
	   		if(getCharCount(oneLine, "|") >= 2 && 
	   				oneLine_trim.startsWith("|") ) {
	   			
	   			flag = true;
	   		}
	   		
	   		
	   		return flag;
	   		
	   		
	   	}
	   	
	   	volatile	int code_block_tag_count = 0  ;  // ``` 的 个数 偶数 能操作  奇数 不能操作！ 
	   	
	   	boolean isInCodeBlock(String oneLine ) {

	   		if(oneLine.trim().startsWith("```") ) {
	   			code_block_tag_count++;
//	   			System.out.println("oneLine   ="+ oneLine  + "  code_block_tag_count = "+ code_block_tag_count);
	   		} 
	   		
	   		return code_block_tag_count%2 != 0;
	   		
	   	}
	   
    }
    class Head_DeleteOne_Rule_3 extends Basic_Rule{
    	

		
    	Head_DeleteOne_Rule_3(){
             super("#",3,5);
         }
    	
 	   String simpleDesc(){
           return "对当前的 所有md 文件的 #标签自减一  h2>h1 h3->h2   h4->h3 h5>h6  h6>h5   h1不变";
       }
	

 	   
   	
   	@Override
   	ArrayList<File> applyDir_SubFileListRule5(ArrayList<File> allSubDirFileList, ArrayList<File> allSubRealFileList) {
   	// TODO Auto-generated method stub
   		
   		ArrayList<File>  mdFileList	 = getSubFileList(allSubRealFileList,".md");
       	
   		if(mdFileList == null) {
       		System.out.println("当前 检测到的 md 文件的数量为空 null ! 无法执行规则#1 mdFileList.size() = null ");	
       		return super.applyDir_SubFileListRule5(allSubDirFileList, allSubRealFileList);
   	
   		}
   		System.out.println("mdFileList.size() = "+ mdFileList.size());
   	 
   		if(mdFileList.size() == 0) {
       		System.out.println("当前 检测到的 md 文件的数量为空! 无法执行规则#1 mdFileList.size() = "+ mdFileList.size());	
       		return super.applyDir_SubFileListRule5(allSubDirFileList, allSubRealFileList);
   		}
   		for (int i = 0; i < mdFileList.size(); i++) {
   			File mdFile = mdFileList.get(i);
   			
   			ArrayList<String> mdContentList = readListFromFile(mdFile);
   	 		System.out.println("Rule3 test ------------- ");
   			ArrayList<String> fixedMdContentList = try_Add_H_level(mdContentList);
   
   			if(!fixedMdContentList.equals(mdContentList)) {
   	 			writeContentToFile(mdFile, fixedMdContentList);
   			}
  
			}
   		
   	
   		return super.applyDir_SubFileListRule5(allSubDirFileList, allSubRealFileList);

   	}
   	
   	ArrayList<String> 	try_Add_H_level(ArrayList<String>  originList ){
   		ArrayList<String>  resultList = new ArrayList<String> ();
   		


   		for (int i = 0; i < originList.size(); i++) {
				String oneLine = originList.get(i);
				
				if(!isAllowOperation(oneLine)) {
					resultList.add(oneLine);
					continue;
				}
			      
				if(isHeadLine(oneLine)){
					System.out.println("isImageInLine = "+true);
					String fixedOneLine = delete_Head_OneTag(oneLine);
					resultList.add(fixedOneLine);
					System.out.println("原图片语句 = "+oneLine);
					System.out.println("经修复语句 = " + fixedOneLine);
					continue;
				}
				System.out.println("isImageInLine = "+false+"  OneLine = \n "+oneLine+"\n");
				resultList.add(oneLine);
   			
			}
   		
   		return resultList;
   	}
   	
   	String delete_Head_OneTag(String oneLine) {
   		String charOne = oneLine.trim().substring(0, 1);
   		if("#".equals(charOne)) {
   			return oneLine.trim().substring(1);
   		}
   	
   		return oneLine;
   		
   	}
   	

   	volatile	int code_block_tag_count = 0  ;  // ``` 的 个数 偶数 能操作  奇数 不能操作！  
		
   	
   	boolean isAllowOperation(String oneLine ) {
   		boolean isInBlock = true;
   		
   		
   		if(oneLine.trim().startsWith("```") ) {
   			code_block_tag_count++;
   		} 
   		
   		return code_block_tag_count%2 == 0;
   		
   	}
   	
   	
   	
   	boolean isHeadLine(String oneLine) {
   		boolean flag = false;
   		String trim_line = oneLine.trim();
   		if( trim_line.startsWith("## ") || trim_line.startsWith("### ") || 
   				trim_line.startsWith("#### ") || trim_line.startsWith("##### ") || trim_line.startsWith("###### ")) {
   			flag = true;
   		}
   		
		if(trim_line.startsWith("##") && trim_line.contains("简介") ) {
			 flag = false;
		}
   		return flag;
   	}
 	   
    }

    
    //  对当前的 所有md 文件的 #标签自增一  h1>h2  #->##  ## > ### h2->h3   h5->h6   h6不变
    
    class Head_AddOne_Rule_2 extends Basic_Rule{
    	

		
    	Head_AddOne_Rule_2(){
             super("#",2,5);
         }
    	
 	   String simpleDesc(){
           return "对当前的 所有md 文件的 #标签自增一  h1>h2  #->##  ## > ### h2->h3   h5->h6   h6不变";
       }
	
	   
 	   
    	
    	@Override
    	ArrayList<File> applyDir_SubFileListRule5(ArrayList<File> allSubDirFileList, ArrayList<File> allSubRealFileList) {
    	// TODO Auto-generated method stub
    		
    		ArrayList<File>  mdFileList	 = getSubFileList(allSubRealFileList,".md");
        	
    		if(mdFileList == null) {
        		System.out.println("当前 检测到的 md 文件的数量为空 null ! 无法执行规则#1 mdFileList.size() = null ");	
        		return super.applyDir_SubFileListRule5(allSubDirFileList, allSubRealFileList);
    	
    		}
    		System.out.println("mdFileList.size() = "+ mdFileList.size());
    	 
    		if(mdFileList.size() == 0) {
        		System.out.println("当前 检测到的 md 文件的数量为空! 无法执行规则#1 mdFileList.size() = "+ mdFileList.size());	
        		return super.applyDir_SubFileListRule5(allSubDirFileList, allSubRealFileList);
    		}
    		for (int i = 0; i < mdFileList.size(); i++) {
    			File mdFile = mdFileList.get(i);
    			
    			ArrayList<String> mdContentList = readListFromFile(mdFile);
    	 		System.out.println("Rule2 test ------------- ");
    			ArrayList<String> fixedMdContentList = try_Add_H_level(mdContentList);
    
    			if(!fixedMdContentList.equals(mdContentList)) {
    	 			writeContentToFile(mdFile, fixedMdContentList);
    			}
   
			}
    		
    	
    		return super.applyDir_SubFileListRule5(allSubDirFileList, allSubRealFileList);

    	}
    	
    	ArrayList<String> 	try_Add_H_level(ArrayList<String>  originList ){
    		ArrayList<String>  resultList = new ArrayList<String> ();
    		


    		for (int i = 0; i < originList.size(); i++) {
				String oneLine = originList.get(i);
				
				if(!isAllowOperation(oneLine)) {
					resultList.add(oneLine);
					continue;
				}
			      
				if(isHeadLine(oneLine)){
					System.out.println("isImageInLine = "+true);
					String fixedOneLine = Add_Head_OneTag(oneLine);
					resultList.add(fixedOneLine);
					System.out.println("原图片语句 = "+oneLine);
					System.out.println("经修复语句 = " + fixedOneLine);
					continue;
				}
				System.out.println("isImageInLine = "+false+"  OneLine = \n "+oneLine+"\n");
				resultList.add(oneLine);
    			
			}
    		
    		return resultList;
    	}
    	
    	String Add_Head_OneTag(String oneLine) {
    		
    		return "#"+oneLine.trim();
    		
    	}
    	

    	volatile	int code_block_tag_count = 0  ;  // ``` 的 个数 偶数 能操作  奇数 不能操作！  
		
    	
    	boolean isAllowOperation(String oneLine ) {
    		boolean isInBlock = true;
    		
    		
    		if(oneLine.trim().startsWith("```") ) {
    			code_block_tag_count++;
    		} 
    		
    		return code_block_tag_count%2 == 0;
    		
    	}
    	
    	
    	
    	boolean isHeadLine(String oneLine) {
    		boolean flag = false;
    		String trim_line = oneLine.trim();
    		if(trim_line.startsWith("# ") || trim_line.startsWith("## ") || trim_line.startsWith("### ") || 
    				trim_line.startsWith("#### ") || trim_line.startsWith("##### ") ) {
    			flag = true;
    		}

    		if(trim_line.startsWith("##") && trim_line.contains("简介") ) {
    			 flag = false;
    		}
    		return flag;
    	}
    	
    }
    
    
 // <img src="//../zimage/architect/01_sorttable.jpg"> 转为 <img src="/public/zimage/architect/01_sorttable.jpg">
//  检测每行的 img  
// 如果有  那么就得到 zimage之后的字符串  并重新 替换为  /public/zimage 字样
    
    class FixedImgSrc_Rule_1 extends Basic_Rule{
    	FixedImgSrc_Rule_1(){
             super("#",1,5);
         }
    	

    	
    	@Override
    	ArrayList<File> applyDir_SubFileListRule5(ArrayList<File> allSubDirFileList, ArrayList<File> allSubRealFileList) {
    	// TODO Auto-generated method stub
       
    		ArrayList<File>  mdFileList	 = getSubFileList(allSubRealFileList,".md");
    	
    		

    		if(mdFileList == null) {
        		System.out.println("当前 检测到的 md 文件的数量为空 null ! 无法执行规则#1 mdFileList.size() = null ");	
        		return super.applyDir_SubFileListRule5(allSubDirFileList, allSubRealFileList);
    	
    		}
    		System.out.println("mdFileList.size() = "+ mdFileList.size());
    	 
    		if(mdFileList.size() == 0) {
        		System.out.println("当前 检测到的 md 文件的数量为空! 无法执行规则#1 mdFileList.size() = "+ mdFileList.size());	
        		return super.applyDir_SubFileListRule5(allSubDirFileList, allSubRealFileList);
    		}
    		
    		for (int i = 0; i < mdFileList.size(); i++) {
    			File mdFile = mdFileList.get(i);
    			
    			ArrayList<String> mdContentList = readListFromFile(mdFile);
    	 		System.out.println("test ------------- ");
    			ArrayList<String> fixedMdContentList = tryFixImageSrc(mdContentList);
    
    			if(!fixedMdContentList.equals(mdContentList)) {
    	 			writeContentToFile(mdFile, fixedMdContentList);
    			}
   
			}
    		
    		
    		return super.applyDir_SubFileListRule5(allSubDirFileList, allSubRealFileList);
    	}
    	
		/*
		 * @Override ArrayList<File> applySubFileListRule4(ArrayList<File> curFileList,
		 * HashMap<java.lang.String, ArrayList<File>> subFileTypeMap, ArrayList<File>
		 * curDirList, ArrayList<File> curRealFileList) { // TODO Auto-generated method
		 * stub
		 * 
		 * ArrayList<File> mdFileList = subFileTypeMap.get(".md"); if(mdFileList ==
		 * null) { System.out.
		 * println("当前 检测到的 md 文件的数量为空 null ! 无法执行规则#1 mdFileList.size() = null ");
		 * return super.applySubFileListRule4(curFileList, subFileTypeMap, curDirList,
		 * curRealFileList);
		 * 
		 * } System.out.println("mdFileList.size() = "+ mdFileList.size());
		 * 
		 * if(mdFileList.size() == 0) {
		 * System.out.println("当前 检测到的 md 文件的数量为空! 无法执行规则#1 mdFileList.size() = "+
		 * mdFileList.size()); return super.applySubFileListRule4(curFileList,
		 * subFileTypeMap, curDirList, curRealFileList); }
		 * 
		 * for (int i = 0; i < mdFileList.size(); i++) { File mdFile =
		 * mdFileList.get(i);
		 * 
		 * ArrayList<String> mdContentList = readListFromFile(mdFile);
		 * System.out.println("test ------------- "); ArrayList<String>
		 * fixedMdContentList = tryFixImageSrc(mdContentList);
		 * 
		 * writeContentToFile(mdFile, fixedMdContentList); }
		 * 
		 * 
		 * return super.applySubFileListRule4(curFileList, subFileTypeMap, curDirList,
		 * curRealFileList); }
		 */
    	
    	ArrayList<String> 	tryFixImageSrc(ArrayList<String> originList ){
    		ArrayList<String>  resultList = new ArrayList<String> ();
    		
    		for (int i = 0; i < originList.size(); i++) {
				String oneLine = originList.get(i);
				if(isImageInLine(oneLine)){
					System.out.println("isImageInLine = "+true);
					String fixedOneLine = tryFixedOneLine(oneLine);
					resultList.add(fixedOneLine);
					System.out.println("原图片语句 = "+oneLine);
					System.out.println("经修复语句 = " + fixedOneLine);
					continue;
				}
				System.out.println("isImageInLine = "+false+"  OneLine = \n "+oneLine+"\n");
				resultList.add(oneLine);
    			
			}
    		return resultList;
    	}
    	
		// ![](/public/zimage/architect/01_sorttable.jpg)   
    	// <img src="//../zimage/architect/01_sorttable.jpg"><img src="//../zimage/architect/01_sorttable.jpg"> ![](/public/zimage/architect/01_sorttable.jpg)  ![](/public/zimage/architect/01_sorttable.jpg) 
    	// <img src="//../zimage/architect/01_sorttable.jpg">
    	// <img src="//../zimage/architect/01_sorttable.jpg"><img src="//../zimage/architect/01_sorttable.jpg">
    	// <img src="//../zimage/architect/01_sorttable.jpg"> ![](/public/zimage/architect/01_sorttable.jpg) 
    	String tryFixedOneLine(String imageLine) {
    		String fixedLine = imageLine;
    		
    		// 依次 处理 image  和 ![] 
    		int all_image_count = getCharCount(imageLine, "zimage");  // 当前行所有格式图片的个数
    		
    		//  处理 <image  标签 的数量
    		int image_tag_count = getCharCount(imageLine, "<image");  // 
    		int markdown_image_count_A =  getCharCount(imageLine, "![");  // 
    		int markdown_image_count_B =  getCharCount(imageLine, "](");  // 
    		int markdown_image_count = markdown_image_count_A < markdown_image_count_B ? markdown_image_count_A:markdown_image_count_B;
    		
    		System.out.println("【"+imageLine+"】"+"\nall_image_count = "+ all_image_count + "   image_tag_count= "+ image_tag_count + "   markdown_image_count= + markdown_image_count");
    		
    		if((markdown_image_count + image_tag_count) != all_image_count ) {
    			System.out.println("当前的图片个数 总数 计算错误！！！");
        		System.out.println("xxxxxx->【"+imageLine+"】"+"\nall_image_count = "+ all_image_count + "   image_tag_count= "+ image_tag_count + "   markdown_image_count=" + markdown_image_count);
    		}
    		
    		String fixedStr_A = fixImageTagSrc(imageLine);  // 修复 <img 的 src 
    		System.out.println("fixedStr_A = "+ fixedStr_A);   // 修复 img 的 src
    		String fixedStr_B = fixMarkDownImageTagSrc(fixedStr_A);  // 修复 ![]() 的 src 
    		System.out.println("fixedStr_B = "+  fixedStr_B);   // 修复 img 的 src
    		
    		
    		return fixedStr_B;
    		
    	}
    	
	boolean isImageInLine(String oneLineStr){ 
		
		boolean existflag = false;
		 
		// ![]()    <img src="//../zimage/architect/01_sorttable.jpg">
		if((oneLineStr.contains("<img") && oneLineStr.contains("src") && oneLineStr.contains("zimage"))
				|| (oneLineStr.contains("![") && oneLineStr.contains("zimage")  ) ) {
			existflag = true;
		}
    		return existflag;
    	}
    	
    	 
    	
    	   String simpleDesc(){
               return "<img src=\"//../zimage/xx.jpg\"> 转为 <img src=\"/public/zimage/xx.jpg\">";
           }
    	
    	   

   @SuppressWarnings("unchecked")	
    String fixMarkDownImageTagSrc(String imageLine) {
    			String strResult = imageLine;
    			if(!imageLine.contains("](") || !imageLine.contains("zimage") || !imageLine.contains("![")) {
    				System.out.println("当前行不包括!()[] 图片 原路返回");
    				return imageLine;
    			}
    	int count_tagA = getCharCount(imageLine,"](");

    	int count_tagB = getCharCount(imageLine,"![");

    	if(count_tagA != count_tagB) {
    		System.out.println("当前行包括图片  但图片个数 ](  和 ![ 匹配到的个数不一致 ！！ 不处理 原路返回");
    		return imageLine;
    	}

    	Map<String,String> origin_fixed_Map = new HashMap<String,String>();
    	ArrayList<String> originSrcList = new 	ArrayList<String>();
    	String[] strArr = imageLine.split("\\]\\(");
    	for (int i = 1; i < strArr.length; i++) {  // i 从0 开始  因为 strArr[0] 是 <img 之前的 内容
    		System.out.println("BBB-strArr["+i+"] = "+ strArr[i]);
    		String originStr =  strArr[i];
    		if(!originStr.startsWith("/public/zimage")) {
    			String zimageStr = originStr.substring(originStr.indexOf("zimage"));
    			String fixedSrc = "/public/"+zimageStr;
    			System.out.println("zimageStr = "+ zimageStr);
    			System.out.println("fixedSrc = "+ fixedSrc);
    			origin_fixed_Map.put(originStr, fixedSrc);
    		}
    		
    	}


    	System.out.println("MarkDown!()[]-->_origin_fixed_Map.size() = "+ origin_fixed_Map.size());


    	Map.Entry<String , String> entryItem;
    	if(origin_fixed_Map != null){
    	    Iterator iterator = origin_fixed_Map.entrySet().iterator();
    	    while( iterator.hasNext() ){
    	        entryItem = (Map.Entry<String , String>) iterator.next();
    	       String originStr =  entryItem.getKey();   //Map的Key
    	       String fixedOriginStr =   entryItem.getValue();  //Map的Value
    	       strResult = strResult.replace(originStr, fixedOriginStr);
    	    }
    	}

    	System.out.println("fixMarkDownImageTagSrc = "+ strResult);


    							
    			return strResult;
    		}
    		
    		
    @SuppressWarnings("unchecked")
   String fixImageTagSrc(String imageLine) {
    			String strResult = imageLine;
    			if(!imageLine.contains("<img")) {
    				return imageLine;
    			}
    			

    			Map<String,String> origin_fixed_Map = new HashMap<String,String>();
    			ArrayList<String> originSrcList = new 	ArrayList<String>();
    			String[] strArr = imageLine.split("<img");
    			for (int i = 1; i < strArr.length; i++) {  // i 从0 开始  因为 strArr[0] 是 <img 之前的 内容
    				System.out.println("strArr["+i+"] = "+ strArr[i]);
    				// <img src ="//../zimage/system/android/02_detailnode/public.jpg"> 
    			 if(strArr[i].contains("src") && strArr[i].contains("=") && strArr[i].contains(">")) {
    				 String originSrc = strArr[i].substring(strArr[i].indexOf("=")+"=".length(),strArr[i].indexOf(">"));
    				 System.out.println("originSrc == "+ originSrc);
    				 originSrcList.add(originSrc);
    			 }
    			}
    			System.out.println("originSrcList.size() = "+ originSrcList.size());
    			for (int i = 0; i < originSrcList.size(); i++) {
    				String  originStr = originSrcList.get(i);
    				System.out.println("originStr["+i+"] = "+ originStr);
    				if(!originStr.startsWith("/public/zimage")) {
    					String zimageStr = originStr.substring(originStr.indexOf("zimage"));
    					String fixedSrc = "\"/public/"+zimageStr;
    					System.out.println("zimageStr = "+ zimageStr);
    					System.out.println("fixedSrc = "+ fixedSrc);
    					origin_fixed_Map.put(originStr, fixedSrc);
    				}
    				
    			}
    			
    			System.out.println("<img > tag origin_fixed_Map.size() = "+ origin_fixed_Map.size());
    			
    			
    	        Map.Entry<String , String> entryItem;
    	        if(origin_fixed_Map != null){
    	            Iterator iterator = origin_fixed_Map.entrySet().iterator();
    	            while( iterator.hasNext() ){
    	                entryItem = (Map.Entry<String , String>) iterator.next();
    	               String originStr =  entryItem.getKey();   //Map的Key
    	               String fixedOriginStr =   entryItem.getValue();  //Map的Value
    	               strResult = strResult.replace(originStr, fixedOriginStr);
    	            }
    	        }
    	        
    			System.out.println("strResult = "+ strResult);
    					
    			return strResult;
    			
    		}
    		
    	   
    }


    class Basic_Rule extends Rule {
        Basic_Rule(String ctype, int cindex,int opera_type){
            this.file_type = ctype;
            this.rule_index = cindex;
            this.operation_type = opera_type;
            this.identify = this.file_type+""+this.rule_index;
            curFilterFileTypeList = new ArrayList<String>();
            curFixedFileList = new ArrayList<File>();
            firstInputIndexStr="";
        }

        String applyStringOperationRule1(String origin){
            return origin;
        }

        File applyFileByteOperationRule2(File originFile){
            return originFile;
        }

        ArrayList<File> applyFileListRule3(ArrayList<File> subFileList , HashMap<String, ArrayList<File>> fileTypeMap ){
            return null;
        }

        @Override
        ArrayList<File> applySubFileListRule4(ArrayList<File> curFileList, HashMap<String, ArrayList<File>> subFileTypeMap, ArrayList<File> curDirList, ArrayList<File> curRealFileList) {
            return curFileList;
        }


        @Override
        ArrayList<File> applyDir_SubFileListRule5(ArrayList<File> allSubDirFileList , ArrayList<File> allSubRealFileList){

            return null;
        }

        boolean initParams4InputParam(String inputParam){ firstInputIndexStr =inputParam ; return true ; }

        @Override
        boolean initParamsWithInputList(ArrayList<String> inputParamList) {
            return true;
        }

        String simpleDesc(){
            return null;
        }

	ArrayList<File> getSubFileList(ArrayList<File> originFile, String type){
		ArrayList<File>  SubTypeFileList  = new ArrayList<File> ();
		
		
		for (int i = 0; i < originFile.size(); i++) {
		File subFile = 	originFile.get(i);
	   String fileName = subFile.getName().toLowerCase();
	   if(fileName.endsWith(type.toLowerCase())) {
		   SubTypeFileList.add(subFile);
	   }
		}
		
		return  SubTypeFileList;
	}
		
        String ruleTip(String type,int index , String batName,OS_TYPE curType){
            String itemDesc = "";
            if(curType == OS_TYPE.Windows){
                itemDesc = batName.trim()+" "+type+"_"+index + "    [索引 "+index+"]  描述:"+simpleDesc();
            }else{
                itemDesc = batName.trim()+""+type+"_"+index + "    [索引 "+index+"]  描述:"+simpleDesc();
            }

            return itemDesc;
        }

        boolean tryReName(File curFile , String newName){
            String newFilePath = curFile.getParent() + File.separator + newName;
            String oldName = curFile.getName();
            File newFile = new File(newFilePath);
            if(newFile.exists() && newFilePath.equals(curFile.getAbsolutePath()) ){

//           newFilePath = curFile.getParent() + File.separator +"重复_"+newName;
//           newFile = new File(newFilePath);
                System.out.println("当前目录已存在重命名后的文件  文件名称:"+ curFile.getName());
                return false;    // 已经存在的文件不处理 直接返回

            }
            boolean flag =   curFile.renameTo(newFile);
            if(flag){
                System.out.println(oldName+" 转为 "+ newFilePath +" 成功！");
                curFixedFileList.add(curFile);
            }else{
                System.out.println(oldName+" 转为 "+ newFilePath +" 失败！");
            }
            return flag;
        }
    }

    abstract  class Rule{
        // operation_type  操作类型     1--读取文件内容字符串 进行修改      2--对文件对文件内容(字节)--进行修改    3.对全体子文件进行的随性的操作 属性进行修改(文件名称)
        // 4.对当前子文件(包括子目录 子文件 --不包含孙目录 孙文件)   // 5. 从shell 中获取到的路径 去对某一个文件进行操作
        String firstInputIndexStr ;
        int operation_type;
        String file_type;   // * 标识所有的文件类型   以及当前操作类型文件  或者 单独的文件过滤类型
        String identify;
        int rule_index;   //  (type,index)   组成了最基础的唯一键
        ArrayList<String> curFilterFileTypeList;  //  当前的文件过滤类型   多种文件过滤类型  例如把 多种格式 jpeg png 转为 jpg 时 使用到
        ArrayList<File> curFixedFileList;  // 当前修改操作成功的集合
        abstract    String applyStringOperationRule1(String origin);
        abstract    File applyFileByteOperationRule2(File originFile);
        abstract    ArrayList<File> applyFileListRule3(ArrayList<File> subFileList , HashMap<String, ArrayList<File>> fileTypeMap);
        abstract    ArrayList<File> applySubFileListRule4(ArrayList<File> curFileList , HashMap<String, ArrayList<File>> subFileTypeMap , ArrayList<File> curDirList ,ArrayList<File> curRealFileList);
        abstract    ArrayList<File> applyDir_SubFileListRule5(ArrayList<File> allSubDirFileList , ArrayList<File> allSubRealFileList);

        abstract    boolean initParams4InputParam(String inputParam);  // 初始化Rule的参数 依据输入的字符串
        abstract    boolean initParamsWithInputList(ArrayList<String> inputParamList);
        abstract   String ruleTip(String type,int index , String batName,OS_TYPE curType);  // 使用说明列表  如果覆盖 那么就不使用默认的说明 , 默认就一种情况
        abstract   String simpleDesc();  // 使用的简单描述  中文的该 rule的使用情况  默认会在 ruleTip 被调用

    }

    static void writeContentToFile(File file, String strParam) {

        try {
            if (file != null && !file.exists()) {
                file.createNewFile();
            }

            if (file != null && file.exists()) {
                BufferedWriter curBW = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"));
                curBW.write(strParam);
                curBW.flush();
                curBW.close();
                //    System.out.println("write out File OK !  File = " + file.getAbsolutePath());
            } else {
                System.out.println("write out File  Failed !    File = " + file.getAbsolutePath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String ReadFileContent( File mFilePath) {

        if (mFilePath != null  && mFilePath.exists()) {
            //  System.out.println("存在  当前文件 "+ mFilePath.getAbsolutePath());
        } else {
            System.out.println("不存在 当前文件 "+ mFilePath.getAbsolutePath() );

            return null;
        }
        StringBuilder sb= new StringBuilder();

        try {
            BufferedReader curBR = new BufferedReader(new InputStreamReader(new FileInputStream(mFilePath), "utf-8"));
            String oldOneLine = "";
            int index = 1;
            while (oldOneLine != null) {

                oldOneLine = curBR.readLine();
                if (oldOneLine == null ) {
                    continue;
                }
                
//                if(oldOneLine.trim().isEmpty()) {
//                	
//                }

                sb.append(oldOneLine+"\n");
                    System.out.println("第"+index+"行读取到的字符串:"+oldOneLine);
                index++;


            }
            curBR.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();

    }



    public static boolean isContainChinese(String str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }





	public static boolean isNumeric(String str) {

		if (str == null) {
			return false;
		}

		for (int i = str.length(); --i >= 0;) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		if (str.equals("")) {
			return false;
		}
		return true;
	}


    static void writeContentToFile(File file, ArrayList<String> strList) {

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strList.size(); i++) {
            sb.append(strList.get(i) + "\n");
        }
        try {
            if (file != null && !file.exists()) {
                file.createNewFile();
            }

            if (file != null && file.exists()) {
                BufferedWriter curBW = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"));
                curBW.write(sb.toString());
                curBW.flush();
                curBW.close();
                System.out.println("write out File OKAA !  File = " + file.getAbsolutePath());
            } else {
                System.out.println("write out File  FailedAA !    File = " + file.getAbsolutePath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    static  String  getPaddingIntString(int index , int padinglength , String oneStr , boolean dirPre){
        String result = ""+index;
        int length = (""+index).length();

        if(length < padinglength){
            int distance = padinglength  - length;
            for (int i = 0; i < distance; i++) {
                if(dirPre){
                    result = oneStr+result;
                }else{
                    result = result + oneStr;
                }

            }

        }
        return result;

    }




    public static void createEncryFile(File generalFile , File encryptFile) {

        int general_position = 0;
        int general_offset = 0;
        FileInputStream generalFileInputStream = null;
        BufferedInputStream generalBufferedInputStream = null;


        FileOutputStream encryptileOutputStream = null;
        BufferedOutputStream encryptBufferedOutputStream = null;

        try {
            if(!encryptFile.exists()){
                encryptFile.createNewFile();
            }
            generalFileInputStream = new FileInputStream(generalFile);
            generalBufferedInputStream = new BufferedInputStream(generalFileInputStream);


            encryptileOutputStream = new FileOutputStream(encryptFile);
            encryptBufferedOutputStream = new BufferedOutputStream(encryptileOutputStream);

            if(encryptFile.getAbsolutePath().trim().endsWith("md")){
                while ((general_position = generalBufferedInputStream.read(TEMP_Rule7, 0, TEMP_Rule7.length)) != -1) {
                    encryptBufferedOutputStream.write(TEMP_Rule7, 0, general_position);
                    encryptBufferedOutputStream.flush();
                }
                // 关闭流
                generalBufferedInputStream.close();
                encryptBufferedOutputStream.close();
                return;

            }


            //  System.out.println("原始文件字节大小:  " + generalBufferedInputStream.available());
            while (general_offset < BYTE_CONTENT_LENGTH_Rule7) {   // 读取原始文件的头 BYTE_CONTENT_LENGTH 个字节数进行加密
                general_position = generalBufferedInputStream.read(TEMP_Rule7, general_offset, TEMP_Rule7.length - general_offset);
                if (general_position == -1) {
                    break;
                }
                general_offset += general_position;
                // byteTo16(TEMP, general_position);   // 可以查看 指定 前 general_position 个在 TEMP数组中的字节数据 太多 注释掉
            }


            // 对读取到的TEMP字节数组 BYTE_CONTENT_LENGTH 个字节进行 ECB模式加密 明文大小与密文大小一致

            byte[]    encrypt_bytes = encrypt(TEMP_Rule7);

            System.out.println("加密原始文件:"+generalFile.getName()+"  加密前明文大小:" + TEMP_Rule7.length + "   加密后密文大小:" + encrypt_bytes.length);

            //加密后的密文 填充   encryptFile文件的头首部
            encryptBufferedOutputStream.write(encrypt_bytes, 0, encrypt_bytes.length);
            encryptBufferedOutputStream.flush();
            // 从正常的 general文件 读取  BYTE_CONTENT_LENGTH 字节数之后的所有字节写入到 加密File(Head已经加密)文件中去
            while ((general_position = generalBufferedInputStream.read(TEMP_Rule7, 0, TEMP_Rule7.length)) != -1) {
                encryptBufferedOutputStream.write(TEMP_Rule7, 0, general_position);
                encryptBufferedOutputStream.flush();
            }
            // 关闭流
            generalBufferedInputStream.close();
            encryptBufferedOutputStream.close();

        } catch (Exception e) {
            System.out.println(e.fillInStackTrace());

        }
    }


    private static Cipher encryptCipher = null;
    private static Cipher decryptCipher = null;

    static {
        try {
            Security.addProvider(new SunJCE());
            Key key = getKey(strDefaultKey_Rule7.getBytes());
            encryptCipher = Cipher.getInstance("DES/ECB/NoPadding");
            encryptCipher.init(Cipher.ENCRYPT_MODE, key);
            decryptCipher = Cipher.getInstance("DES/ECB/NoPadding");
            decryptCipher.init(Cipher.DECRYPT_MODE, key);
        } catch (Exception e) {

        }

    }

    private static Key getKey(byte[] arrBTmp) throws Exception {
        byte[] arrB = new byte[8]; //认默为0
        for (int i = 0; i < arrBTmp.length && i < arrB.length; ++i) {
            arrB[i] = arrBTmp[i];
        }
        //生成密匙
        Key key = new javax.crypto.spec.SecretKeySpec(arrB, "DES");
        return key;
    }


    // 加密字节数组
    public static byte[] encrypt(byte[] arrB) throws Exception {
        return encryptCipher.doFinal(arrB);
    }


    //密解字节数组
    public static byte[] decrypt(byte[] arrB) throws Exception {
        return decryptCipher.doFinal(arrB);
    }


    static ArrayList<File> getAllSubFile(File dirFile ) {
        ArrayList<String> typeList = new   ArrayList<String>();
        typeList.add("#");
        return  getAllSubFile( dirFile ,null , typeList);
    }

    
    


    static ArrayList<File> getAllSubFile(File dirFile , ArrayList<String> typeList) {
            return getAllSubFile(dirFile.getAbsolutePath(), "", typeList);

    }
    

    static ArrayList<File> getAllSubFile(String rootPath , ArrayList<String> typeList) {
            return getAllSubFile(rootPath, "", typeList);

    }
    
    
    static ArrayList<File> getAllSubFile(File dirFile ,String aospPath , ArrayList<String> typeList) {
        if(aospPath == null || "".equals(aospPath)){
            return getAllSubFile(dirFile.getAbsolutePath(), "", typeList);
        }
        return getAllSubFile(dirFile.getAbsolutePath(), aospPath, typeList);

    }

    static ArrayList<File> getAllSubFile(String rootPath, String aospItemPath,  ArrayList<String>  typeList) {
        ArrayList<File> allFile = new ArrayList<File>();
        Path curRootPath = Paths.get(rootPath + File.separator + aospItemPath);

        try {
            Files.walkFileTree(curRootPath, new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    String fileString = file.toAbsolutePath().toString();
                    //System.out.println("pathString = " + fileString);

                    for (int i = 0; i < typeList.size(); i++) {
                        String type =  typeList.get(i);
                        if("#".equals(type)){  // 如果 类型是 * 那么就把 所有的 非目录文件加入列表中
                            File curFile =    new File(fileString);
                            if(!curFile.isDirectory()){
                                allFile.add(curFile);
                                break;
                            }


                        }else {
                            if (fileString.endsWith(type)) {
                                allFile.add(new File(fileString));
                                break;
//                         System.out.println("file found at path: " + file.toAbsolutePath());
                            }
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }


        return allFile;


    }

    static ArrayList<File> getCurrentSubDirFile(File  rootPath) {
        ArrayList<File> allDirFile = new ArrayList<File>();
        File[] files = rootPath.listFiles();
        for (int i = 0; i < files.length; i++) {
            File fileItem = files[i];
            if(fileItem.isDirectory()){
                allDirFile.add(fileItem);
            }
        }
        return allDirFile;

    }

    static ArrayList<File> getAllSubDirFile(File  rootPath) {
        ArrayList<File> allDirFile = new ArrayList<File>();
        Path curRootPath = Paths.get(rootPath.getAbsolutePath() + File.separator );

        try {
            Files.walkFileTree(curRootPath, new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    allDirFile.add(dir.toFile());
                    return super.postVisitDirectory(dir, exc);
                }

            });
        } catch (IOException e) {
            e.printStackTrace();
        }


        return allDirFile;


    }


    // 读取加密文件  对加密部分进行解密 然后生成解密之后的文件 decryptFile
    public static void createDecryFile(File encryptFile, File decryptFile) {


        FileOutputStream decryptileOutputStream = null;
        BufferedOutputStream decryptBufferedOutputStream = null;

        FileInputStream encryptileInputStream = null;
        BufferedInputStream encryptBufferedInputStream = null;


        try {
            if(!decryptFile.exists()){
                decryptFile.createNewFile();
            }
            encryptileInputStream = new FileInputStream(encryptFile);
            encryptBufferedInputStream = new BufferedInputStream(encryptileInputStream);


            decryptileOutputStream = new FileOutputStream(decryptFile);
            decryptBufferedOutputStream = new BufferedOutputStream(decryptileOutputStream);


            int encrypt_offset = 0;
            int encrypt_position = 0;
            while (encrypt_offset < BYTE_CONTENT_LENGTH_Rule7) {    // 读取到加密文件的  加密字节部分 大小为 BYTE_CONTENT_LENGTH
                encrypt_position = encryptBufferedInputStream.read(TEMP_Rule7, encrypt_offset, TEMP_Rule7.length - encrypt_offset);

                if (encrypt_position == -1) {
                    break;
                }
                encrypt_offset += encrypt_position;
                // byteTo16(TEMP, general_position);   // 可以查看 指定 前 general_position 个在 TEMP数组中的字节数据 太多 注释掉
            }

            byte[] decrypt_bytes = decrypt(TEMP_Rule7);  // 对加密文件的加密字节进行解密
            System.out.println("解密文件:"+decryptFile.getName()+"  密文加密字节大小:" + TEMP_Rule7.length + "   解密密文之后的明文大小:" + decrypt_bytes.length);

            decryptBufferedOutputStream.write(decrypt_bytes);
            decryptBufferedOutputStream.flush();


            // 读取 encryptFile加密文件中正常的字节    BYTE_CONTENT_LENGTH 字节数之后的所有字节写入到 解密File(Head已经解密)文件中去
            while ((encrypt_offset = encryptBufferedInputStream.read(TEMP_Rule7, 0, TEMP_Rule7.length)) != -1) {
                decryptBufferedOutputStream.write(TEMP_Rule7, 0, encrypt_offset);
                decryptBufferedOutputStream.flush();
            }

            encryptBufferedInputStream.close();
            decryptBufferedOutputStream.close();

        } catch (Exception e) {
            System.out.println(e.fillInStackTrace());

        }
    }

    static void exeEmptyInputOperation() {
        System.out.println("═══════════════════"+"开始执行默认 MD_Rule 空输入参数操作 Begin "+"═══════════════════"+"\n");
     
    	System.out.println("curDirPath = "+ curDirPath);
    	ArrayList<String> searchTypeList = new 	ArrayList<String> ();
    	searchTypeList.add(".md");
    	searchTypeList.add(".MD");
    ArrayList<File>  mdFileList = 	getAllSubFile(curDirPath, searchTypeList);
	System.out.println("mdFileList.size() = "+ mdFileList.size());
	int order_index = 1;
  for (int i = 0; i < mdFileList.size(); i++) {
	File mdItemFile = mdFileList.get(i);
	String fileName = mdItemFile.getName().toLowerCase();
	if("readme.md".equals(fileName)) {
		continue;
	}

	System.out.println("MD["+order_index+"] = "+ mdItemFile.getAbsolutePath());
	order_index = order_index + 1;
	hegui_md_operation(mdItemFile);
}
  
    
        System.out.println("\n═══════════════════"+"开始执行默认 MD_Rule 空输入参数操作 End "+"═══════════════════"+"\n");

    	
    }
    
    //  对 MD 文件进行 合规 操作 
    // 1. 检测是否有 如下 表头  没有 就用 默认的   如果有表头 那么 跳过   
    //  只有标注了 时间戳  没有标注  表头  那么 就会发布在  最新发表列表中
    //  标注了  时间戳 + 表头  才会正确出现在对应位置
    // 1.1 检测 MD 文件的 表头的  typora-root-url: 和 typora-copy-images-to: 是否是对的
//    ---
//    layout: post
//    title: iw命令T4
//    category: 技术
//    tags: Android
//    keywords: iw android
//    typora-root-url: ..\..\..\                              【使得本地路径和服务器网络上的路径一致】 动态计算
//    typora-copy-images-to: ..\..\..\public\zimage           【使得本地其他路径的图片直接显示 并自动copy到图片目录 服务器对应】
//    ---
//
    
    
//2. 对 MD 文件检测 是否 有 TOC    
//    ## 简介
//    * TOC
//    {:toc}
    
    // 3. 检测当前文件的 文件名称   是否有 日期 时间戳  如果没有 那么 添加默认的 时间戳 今日    只有标注了 时间戳的 md 文章才会在网络发布 
    

    
    
    
    static void   hegui_md_operation(File mdFile){
    	String mdFileName = mdFile.getAbsolutePath();
    	String  mdFileContent = ReadFileContent(mdFile);
    	String  prefixStr = "";
    	boolean isTimeName = checkTimeName(mdFile);
    	boolean hasHead = checkHasHead(mdFileContent);
    	boolean hasToc = checkHasToc(mdFileContent);
    	
    	boolean isRootUrlRight = false ; //   当前的 根 目录 配置是否正确 
    	
//    	System.out.println("【isTimeName = "+ isTimeName + "】 【HeadTag = "+hasHead+"】【TocTag="+hasToc+"】");
    	
    	String mHeadTag = "";
    	String mTocTag = "";
    	String exist_head_tag = "";  //  当前  读取 到的 已经 存在的 headtag
    	int exist_separator_count = 0;
   		String exist_separator_tag = "..\\";

/*		---
		layout: post
		title: Windows_C代码Demo
		category: 代码
		tags: Windows_C Windows
		keywords:
		typora-root-url: ..\..\..\
		typora-copy-images-to: ..\..\..\public\zimage
---*/

   		boolean is7Blank = false;  // 在headTag中所有的冒号后都必须为空格 否则解析失败！
   		int separator_count = 1;  // 默认的  相对路径  ..\\
   		String separator_tag = "..\\";
   		

   		// 计算 正确的  相对 位置 信息 
		if(mdFileName.contains(Cur_Git_Name)) {
			String cur_git_path = mdFileName.substring(mdFileName.indexOf(Cur_Git_Name)+Cur_Git_Name.length());
//MD[6] = D:\Git_Dir\zukgit.github.io\_posts\IT\Android\iw_集合T2.md
//【isTimeName = false】 【HeadTag = false】【TocTag=false】
//cur_git_path = \_posts\IT\Android\iw_集合T2.md     separator_count= 4
			separator_count = 	getCharCount(cur_git_path,File.separator) -1 ; // 当前有四个\  三个父类  4-1=3
			System.out.println("cur_git_path = "+ cur_git_path +"     separator_count= "+ separator_count);
		 separator_tag = copyString("..\\",separator_count);
		
		}
		
   		if(hasHead) {   //  如果 有 HeadTag 的 话 
   			int first_line_index = mdFileContent.indexOf("---");
   			int second_line_index = mdFileContent.indexOf("---", 1);
   			System.out.println("first_line_index = "+ first_line_index + "      second_line_index="+second_line_index);
   			String curHeadTag = mdFileContent.substring(first_line_index,second_line_index+"---".length());
   			System.out.println("=======curHeadTag -> \n"+curHeadTag);
   			exist_head_tag = curHeadTag;
   			String realtivePathTag = curHeadTag.substring(curHeadTag.indexOf("typora-root-url:")+"typora-root-url:".length(),curHeadTag.indexOf("typora-copy-images-to:"));
   			realtivePathTag = realtivePathTag.replace("\r", "").replace("\n", "");
   			System.out.println("realtivePathTag = "+ realtivePathTag);
   			exist_separator_tag = realtivePathTag;
   			exist_separator_count = getCharCount(realtivePathTag, File.separator);
			is7Blank = getCharCount(realtivePathTag, ": ") == 7?true:false;  //正常 tag 含有 7个 【: 】 冒号空格  没有的话 需要解析充值
   		}
   		
   	
    	if(!hasHead) {  // 如果 没有 headtag   那么  需要 添加  head Tag  使用默认的 headTag
    		String Relatived_Path_Str = "";
    		
    		// D:\Git_Dir\zukgit.github.io\_posts\IT\Android\xx.md
    		// D:\Git_Dir\zukgit.github.io\
    		// ..\\..\\..\\
     		String relative_path = "..\\..\\..\\";

    		

    		String defaultHeadTag = "---\r\n"
    				+ "layout: post\r\n"
    				+ "title: 未命名Title_"+getTimeStamp()+"\r\n"
    				+ "category: 未命名Category\r\n"
    				+ "tags: 未命名Tag\r\n"
    				+ "keywords: \r\n"
    				+ "typora-root-url: "+separator_tag+"\r\n"
    				+ "typora-copy-images-to: "+separator_tag+"public\\zimage\r\n"
    				+ "---\r\n"
    				+ "";
    		
        	System.out.println("【isTimeName = "+ isTimeName + "】 【HeadTag = "+hasHead+"】【TocTag="+hasToc+"】"+"【separator_count="+separator_count+",separator_tag="+separator_tag+"】");		
        	 System.out.println("defaultHeadTag = \n"+defaultHeadTag);  

     

        	 mHeadTag = defaultHeadTag;
    		
    	}
    	
    	

		
    	if(!hasToc) {
    		mTocTag = "\r\n## 简介\r\n"
    	    	 		+ " * TOC\r\n"
    	    	 		+ " {:toc}\r\n";
    		
    	}
    	
    	
    	if(!hasToc || !hasHead ) {
    		
    	
    	String newContentMD = 	mHeadTag + mTocTag +  mdFileContent;
    	
    	writeContentToFile(mdFile, newContentMD);
    	
    	}else if(hasHead && exist_separator_count !=0  && exist_separator_count != separator_count ) {
    		 //    如果当前的  相对 路径  不正确 那么 需要 置换
//			typora-root-url:..\..\..\
//			typora-copy-images-to:..\..\..\public\zimage  冒号后必须有 空格 否则 解析错误
    		System.out.println("!!!!!! 当前文件 "+mdFile.getAbsolutePath()+"相对路径错误！ 将修复！！");

    		String newHeadTag = exist_head_tag.replace(exist_separator_tag, " "+separator_tag);
    		if(getCharCount(newHeadTag, ": ") != 7){  // 如果 格式错误 那么 一并处理
				newHeadTag = newHeadTag.replace(": ",":");  // 先把 :好 恢复一个
				newHeadTag = newHeadTag.replace(":",": ");  // 再把 冒号 全都转为 空冒号格
			}

    		String newContentMD = mdFileContent.replace(exist_head_tag, newHeadTag);
			System.out.println("exist_separator_tag  = " +exist_separator_tag );
			System.out.println("separator_tag  = " +separator_tag );
			System.out.println("exist_head_tag  = " +exist_head_tag );
			System.out.println("newHeadTag  = " +newHeadTag );
        	writeContentToFile(mdFile, newContentMD);
    		
    	} else if(!is7Blank && hasHead ){  // 相对路径正确  但是冒号格式错误
			System.out.println("相对路径正确  但是冒号格式错误  exist_head_tag = " +exist_head_tag );
		String fixedTag   = exist_head_tag.replace(": ",":");  // 先把 :好 恢复一个
			fixedTag = fixedTag.replace(":",": ");  // 再把 冒号 全都转为 空冒号格
			String newContentMD = mdFileContent.replace(exist_head_tag, fixedTag);
			writeContentToFile(mdFile, newContentMD);
		}
    	
    	if(!isTimeName) {
    		String newTimeFileName = getDayStamp()+"-"+mdFile.getName();
    		tryReName(mdFile,newTimeFileName);
    	}

   	 
    }
    
    // checkHasToc  
//    * TOC
//    {:toc}
    static boolean checkHasToc(String fileContent) {
    	  
    	return fileContent.contains("* TOC") && fileContent.contains("{:toc}") ;
    }
    
    
    
 static String copyString(String srcStr , int num) {
 StringBuilder sb = new StringBuilder();
 
 for (int i = 0; i < num; i++) {
	 sb.append(srcStr);
}
 
 return sb.toString();
    	
    }
    
    
    
    // 2020-21-21-namexx.md
    static boolean checkTimeName(File file) {
    	String fileName = file.getName();
    	String[] name_list = fileName.split("-");
    	if(name_list == null || name_list.length < 3) {
    		return false;
    	}
    	System.out.println("name_list.length = "+ name_list.length);
    	for (int i = 0; i < name_list.length; i++) {
			System.out.println("name_list["+i+"] = "+ name_list[i]);
		}
    	return isNumeric(name_list[0]) && isNumeric(name_list[1]) && isNumeric(name_list[2]);
    }
    
    
// ---
//  layout: post
//  title: iw命令T4
//  category: 技术
//  tags: Android
//  keywords: iw android
//  typora-root-url: ..\..\..\                              【使得本地路径和服务器网络上的路径一致】 动态计算
//  typora-copy-images-to: ..\..\..\public\zimage
    static boolean checkHasHead(String fileContent) {
  
    	return fileContent.contains("layout:") && fileContent.contains("title:") &&
    			fileContent.contains("category:") && fileContent.contains("tags:") &&
    			fileContent.contains("keywords:") && fileContent.contains("typora-root-url:") &&
    			fileContent.contains("typora-copy-images-to:") ;
    }
    
    
    
    static void showTip() {
        System.out.println("对Type文件内容 进行 Index 规则的处理  identy=【 Type_Index 】【 文件后缀_当前操作逻辑索引】\n");
        System.out.println("当前已实现的替换逻辑如下:\n");

        int count = 1;
        System.out.println("═══════════════════"+"使用方法列表 Begin"+"═══════════════════"+"\n");
        for (int i = 0; i < realTypeRuleList.size() ; i++) {
            Rule itemRule = realTypeRuleList.get(i);
            String type =  itemRule.file_type;
            int index =  itemRule.rule_index;
            String desc =  itemRule.ruleTip(type , index ,Cur_Bat_Name,CUR_OS_TYPE);

/*
            String itemDesc = "";
           if(curOS_TYPE == OS_TYPE.Windows){
                itemDesc = "zrule_apply_K3.bat  "+type+"_"+index + "    [索引 "+count+"]  描述:"+desc;
           }else{
               itemDesc = "zrule_apply_K3 "+type+"_"+index + "    [索引 "+count+"]  描述:"+desc;
           }
           */
            System.out.println(desc+"\n");
            count++;
        }
        System.out.println("═══════════════════"+"使用方法列表 End "+"═══════════════════"+"\n");

    }



    static int  getCharCount(String originStr , String charStr){
        int count  = 0 ;
        if(originStr == null || charStr == null){

            return count;
        }
        for (int i = 0; i < originStr.length(); i++) {
            String charOne = originStr.substring(i,i+1);
            if(charStr.equals(charOne)){
                count++;
            }
        }
        return count;

    }

    static boolean  checkInputParamsOK(){
        boolean inputOk = true;

        for (int i = 0; i < Rule_Identify_TypeIndexList.size(); i++) {
            String curInputStr = Rule_Identify_TypeIndexList.get(i);
            if(!curInputStr.contains("_")){
                return false;
            }


            String[] paramsArr =    curInputStr.split("_");
            if(paramsArr.length < 2){
                continue;
            }
            String type =  paramsArr[0];
            String index =  paramsArr[1];

//          initParams4InputParam
            if(!isNumeric(index)){  //  第二个参数不是 数字 那么 输入格式错误
                return false;
            }
            Rule matchRule = getRuleByIndex(Integer.parseInt(index));
            if(matchRule != null){
                inputOk =     matchRule.initParams4InputParam(curInputStr) &&  matchRule.initParamsWithInputList(Rule_Identify_TypeIndexList);
                return inputOk;
            }

        }

        return inputOk;
    }

    static Rule CurSelectedRule ;
    public static void main(String[] args) {

        initSystemInfo();

        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                System.out.println("args[" + i + "] = " + args[i]);
                if (i == 0) {
                    curDirPath = args[i];
                } else {
                    mKeyWordName.add(args[i]);
                    Rule_Identify_TypeIndexList.add(args[i]);
                }
            }
        }

        K3_MD_Rule  mK3_Object = new K3_MD_Rule();
        mK3_Object.InitRule();

        File mCurDirFile = new File(curDirPath);
        curDirFile = new  File(curDirPath);

        if (mKeyWordName.size() == 0) {
             exeEmptyInputOperation();  //  执行默认的 没有任何输入的操作
            //  执行默认的操作
            showTip();
            return;
        }

        if(!checkInputParamsOK()){
            System.out.println("当前用户输入的格式错误   input=【类型_索引】  例如    html_1   html_2    html_3  ");
            return;
        }



        if (curDirFile == null || !mCurDirFile.exists() || !mCurDirFile.isDirectory() ) {
            System.out.println("当前执行替换逻辑的文件路径:" + curDirPath+"  不存在! ");
            return;
        }



        //  通过  shell中输入参数来进行操作
        //  Rule_Identify_TypeIndexList.add("html_1");  //  1.添加处理的类型文件  类型_该类型的处理逻辑索引      索引从1开始


        for (int i = 0; i < Rule_Identify_TypeIndexList.size(); i++) {  //  依据文件类型 去找到文件
            // html_1
            String applyRuleString = Rule_Identify_TypeIndexList.get(i);
            String paramsArr[] = applyRuleString.split("_");
            if(paramsArr.length <2){
                continue;
            }
            String curType = paramsArr[0];
            String curApplyRule =  paramsArr[1];
            if(!isNumeric(curApplyRule)){
                continue;
            }
            int ruleIndex = Integer.parseInt(curApplyRule);


            Rule curApplayRule = getRuleByIndex(ruleIndex);
            if(curApplayRule != null){
                CurSelectedRule = curApplayRule;
            }
            if(curApplayRule == null && CurSelectedRule == null ){
                System.out.println("无法匹配到 对应的 index="+ ruleIndex +"  对应的规则 Rule !   可能需要代码添加。");
                continue;   // 继续下一个循环
            }
            if(curApplayRule == null && CurSelectedRule != null){
                return;
            }
            if(curApplayRule.curFilterFileTypeList.size() == 0){
                curApplayRule.curFilterFileTypeList.add(curType);
            }



            ArrayList<File>  typeFileList = new  ArrayList<File>();

            if(curApplayRule.operation_type == 4){  // 对于 类型是 4 的操作  只获取当前 shell 下的文件
                typeFileList.addAll(Arrays.asList(mCurDirFile.listFiles()));
                System.out.println("operation_type == 4 子目录大小: "+typeFileList.size());
            }else{
                typeFileList =  getAllSubFile(mCurDirFile,null,curApplayRule.curFilterFileTypeList);
            }

            if(typeFileList.size() == 0){
                System.out.println("未能搜索到类型列表匹配的文件:  "+ Rule_Identify_TypeIndexList.get(i));
                continue;
            }
            initFileTypeMap(typeFileList);



            if(curApplayRule.operation_type == 4){  // 只对 当前的 子 文件(目录 文件)操作
                //  对当前文件进行整理
                ArrayList<File> subDirList = new  ArrayList<File>();
                ArrayList<File> realFileList = new  ArrayList<File>();

                outCycle: for (int j = 0; j < typeFileList.size(); j++) {
                    File curFile = typeFileList.get(j);
                    if(curFile.isDirectory()){
                        subDirList.add(curFile);
                    }else{

                        if(curApplayRule.curFilterFileTypeList.contains("#")){
                            realFileList.add(curFile);
                        }else{

                            inCycle:  for (int k = 0; k < curApplayRule.curFilterFileTypeList.size(); k++) {
                                String curMatchType =  curApplayRule.curFilterFileTypeList.get(k);
//                                System.out.println("FileName:"+curFile.getName()+"  curMatchType="+curMatchType);
                                if(curFile.getName().endsWith(curMatchType)){
                                    realFileList.add(curFile);
                                    break inCycle;
                                }
                            }

                        }

                    }
                }

                ArrayList<File> resultFileList =   curApplayRule.applySubFileListRule4(typeFileList,CurDirFileTypeMap,subDirList,realFileList);
                if(resultFileList != typeFileList){
                    System.out.println("应用规则:  "+ applyRuleString +" 成功!");
                }else{
                    System.out.println("应用规则:  "+ applyRuleString +" 失败!");
                }

            }
            else if(curApplayRule.operation_type == 3){  // 对所有文件进行的 统一处理的 类型

                ArrayList<File> resultFileList =   curApplayRule.applyFileListRule3(typeFileList,CurDirFileTypeMap);
                if(resultFileList != typeFileList){

                    System.out.println("应用规则:  "+ applyRuleString +" 成功!");
                }else{
                    System.out.println("应用规则:  "+ applyRuleString +" 失败!");
                }


            } else if(curApplayRule.operation_type == 5){  // 对所有文件夹  所有子文件 孙文件 所有 子文件夹 孙文件夹

                ArrayList<File> curAllDirFile =   getAllSubDirFile(curDirFile);  // 获取所有的 文件夹列表   包含 孙子 子文件夹
                ArrayList<File> curAllRealFile =   getAllSubFile(curDirFile);   // 获取所有的 文件 列表 包含 孙子 子文件
                //     FileChannel
//  zukgit operation_type == 5
                System.out.println(" curDirFile = "+ curDirFile.toString());
                System.out.println(" curAllDirFile = "+ curAllDirFile.size());
                System.out.println(" curAllRealFile = "+ curAllRealFile.size());
                curApplayRule.applyDir_SubFileListRule5(curAllDirFile,curAllRealFile);
            }else{

                for (int j = 0; j < typeFileList.size(); j++) {
                    File itemFile =  typeFileList.get(j);
                    String fileCOntent =  ReadFileContent(itemFile).trim();
                    // 2.applyOperationRule  添加处理规则


                    String resultStr = OriApplyOperationRule(curType,curApplyRule,fileCOntent).trim();

                    int currentOperationType = 1;  // 默认操作类型是 读取字符串的内容 进行处理

                    String identy = curType.trim()+curApplyRule.trim();
//                Rule applayRule2Identify = getRuleByIdentify(identy);

                    Rule applayRule4Index = getRuleByIndex(ruleIndex);
//                如果对应相同的 index的 Rule #_2    出现了    MP3_2 的情况  那么就需要把当前的 所有的*的文件 过滤为 mp3的文件
//                if("#".equals(applayRule2Identify.file_type) && !curType.equals(applayRule2Identify.file_type)){
//
//                }


                    if(applayRule4Index != null){
                        currentOperationType =  applayRule4Index.operation_type;
                    }else{
                        System.out.println("无法匹配到 对应的 identy="+ identy +"  对应的规则 Rule !   可能需要代码添加。");
                        return;
                    }

                    if(currentOperationType == 1){    // 对字符串进行逻辑处理的类型

                        if(!fileCOntent.equals(resultStr)){
                            writeContentToFile(itemFile,resultStr);
                            System.out.println("itemFile["+j+"] 符合规则(String-Content) 应用Rule成功 " + applyRuleString+ "  = " + itemFile.getAbsolutePath() );
                        }else{
                            System.out.println("itemFile["+j+"] 不符合规则(String-Content) = " + itemFile.getAbsolutePath() );
                        }

                    }else if(currentOperationType == 2){

                        File resultFile = applayRule4Index.applyFileByteOperationRule2(itemFile);

                        if(resultFile != itemFile ){
                            System.out.println("itemFile["+j+"] 符合规则(File) 应用Rule成功 " + applyRuleString+ "  = " + itemFile.getAbsolutePath() );
                        }else{
                            System.out.println("itemFile["+j+"] 不符合规则(File) = " + itemFile.getAbsolutePath() );
                        }


                    }

                }

            }

        }

        setProperity();
    }


    static  void addCurFileTypeMapItemWithKey(String keyType, File curFile) {
        if (CurDirFileTypeMap.containsKey(keyType)) {
            ArrayList<File> fileList = CurDirFileTypeMap.get(keyType);
            fileList.add(curFile);
        } else {
            ArrayList<File> fileList = new ArrayList<File>();
            fileList.add(curFile);
            CurDirFileTypeMap.put(keyType, fileList);
        }
    }

    static void  initFileTypeMap(ArrayList<File> subFileList){
        for (File curFile : subFileList) {
            String fileName = curFile.getName();
            if (!fileName.contains(".")) {
                addCurFileTypeMapItemWithKey("unknow", curFile);
            } else {
                String suffix = fileName.substring(fileName.lastIndexOf(".")).trim().toLowerCase();
                addCurFileTypeMapItemWithKey(suffix, curFile);
            }
        }


    }

    static Map<String , ArrayList<File>>   getCurSubFileMap(File mDirFile){
        HashMap<String, ArrayList<File>> realFileListMap = new  HashMap<String, ArrayList<File>>(); ;

        for (File curFile : mDirFile.listFiles()) {
            if(curFile.isDirectory()){
                continue;
            }
            String fileName = curFile.getName();

            if (!fileName.contains(".")) {
                String type ="";   //  unknow  没有后缀名的文件
                if (realFileListMap.containsKey(type)) {
                    ArrayList<File> fileList = realFileListMap.get(type);
                    fileList.add(curFile);
                } else {
                    ArrayList<File> fileList = new ArrayList<File>();
                    fileList.add(curFile);
                    realFileListMap.put(type, fileList);
                }
            } else {
                String suffix = fileName.substring(fileName.lastIndexOf(".")).trim().toLowerCase();

                if (realFileListMap.containsKey(suffix)) {
                    ArrayList<File> fileList = realFileListMap.get(suffix);
                    fileList.add(curFile);
                } else {
                    ArrayList<File> fileList = new ArrayList<File>();
                    fileList.add(curFile);
                    realFileListMap.put(suffix, fileList);
                }
            }
        }

        return realFileListMap;
    }

    static String OriApplyOperationRule(String mType ,String index  , String mOriContent){
        String identy = mType.trim()+index.trim();
        Rule applayRule = getRuleByIdentify(identy);
        if(applayRule == null){
            System.out.println("没有查询到 identy ="+ identy+"对应的处理规则");
            return mOriContent;
        }
        return  applayRule.applyStringOperationRule1(mOriContent);
    }

    static ArrayList<Rule> realTypeRuleList = new ArrayList<Rule>();  // 规则的集合


    static Rule getRuleByIndex(int index){
        for (int i = 0; i <realTypeRuleList.size() ; i++) {
            if(realTypeRuleList.get(i).rule_index == index){
                return realTypeRuleList.get(i);
            }
        }
        return null;
    }



    ArrayList<File>  getSubTypeFileWithPoint(File dirFile , String pointType){
        ArrayList<File>  targetFileList = new   ArrayList<File>();
        String fillterFileStr = ""+pointType.toLowerCase();
        if(!dirFile.isDirectory()){
            return targetFileList;
        }
        File[] allSubFileList = dirFile.listFiles();
        for (File curFile: allSubFileList ) {
            String fileName = curFile.getName().toLowerCase();
            if(fileName.endsWith(fillterFileStr)){
                targetFileList.add(curFile) ;
            }
        }

        return targetFileList;
    }


    static String getTimeStampLong(){

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS");//设置日期格式
        String date = df.format(new Date());
        return date;
    }



    static String getTimeStamp(){

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");//设置日期格式
        String date = df.format(new Date());
        return date;
    }
    
    static String getDayStamp(){

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        String date = df.format(new Date());
        return date;
    }
    

    static  Rule getRuleByIdentify(String identify){
        for (int i = 0; i <realTypeRuleList.size() ; i++) {
            if(realTypeRuleList.get(i).identify.equals(identify)){
                return realTypeRuleList.get(i);
            }
        }
        return null;
    }


    public static void fileCopy(File origin, File target) {
        InputStream input = null;
        OutputStream output = null;
        int lengthSize;
        // 创建输入输出流对象
        try {
            input = new FileInputStream(origin);
            output = new FileOutputStream(target);
            // 获取文件长度
            try {
                lengthSize = input.available();
                // 创建缓存区域
                byte[] buffer = new byte[lengthSize];
                // 将文件中的数据写入缓存数组
                input.read(buffer);
                // 将缓存数组中的数据输出到文件
                output.write(buffer);

            } catch (IOException e) {

                e.printStackTrace();
            }

        } catch (Exception e) {

        } finally {
            if (input != null && output != null) {
                try {
                    input.close(); // 关闭流
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }





    public static String execCMD_Windows(String command) {
//        System.out.println("══════════════Begin ExE ");
        StringBuilder sb =new StringBuilder();
        StringBuilder errorSb =new StringBuilder();
        try {

            Process process=Runtime.getRuntime().exec("CMD.exe /c start /B "+command);

            InputStreamReader inputReader =  new InputStreamReader(process.getInputStream(),"GBK");
            BufferedReader bufferedReader=new BufferedReader(inputReader);
            String line;
            int waitFor =   process.waitFor();
//            Stream<String> lines = bufferedReader.lines();
//            lines.iterator();
//            System.out.println("line Count = "+lines.count());

            while((line=bufferedReader.readLine())!=null  )
            {
                sb.append(line+"\n");

            }



            boolean isAlive =   process.isAlive();
            int errorSteamCode =  process.getErrorStream().read();

            String errorStream =    process.getErrorStream().toString();
            int exitValue =    process.exitValue();
//            process.getErrorStream().
            //杀掉进程
//            System.out.println("exitValue ="+ exitValue);
            sb.append("\nexitValue = "+ exitValue+
                    "\nisAlive = "+ isAlive+
                    "\nerrorStream = "+ errorStream+
                    "\nerrorSteamCode = "+ errorSteamCode+
                    "\nwaitFor = "+waitFor);
//            process.destroy();

        } catch (Exception e) {
            System.out.println("execCMD 出现异常! ");
            return e.toString();
        }

//        System.out.println("sb.toString() = "+ sb.toString());
//        System.out.println("══════════════End ExE ");
        return sb.toString();
    }


    /**
     * 执行 mac(unix) 脚本命令~
     * @param command
     * @return
     */
    public static String execCMD_Mac(String command) {
        String[] cmd = {"/bin/bash"};
        Runtime rt = Runtime.getRuntime();
        Process proc = null;
        try {
            proc = rt.exec(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 打开流
        OutputStream os = proc.getOutputStream();
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));

        try {
            bw.write(command);

            bw.flush();
            bw.close();

            /** 真奇怪，把控制台的输出打印一遍之后竟然能正常终止了~ */
//            readConsole(proc);

            /** waitFor() 的作用在于 java 程序是否等待 Terminal 执行脚本完毕~ */
            proc.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
        int retCode = proc.exitValue();
        if (retCode != 0) {
            System.out.println("unix script retCode = " + retCode);

//            System.out.println(readConsole(proc));
            System.out.println("UnixScriptUil.execute 出错了!!");
        }
        return retCode+"";
    }


   static boolean tryReName(File curFile , String newName){
        String newFilePath = curFile.getParent() + File.separator + newName;
        String oldName = curFile.getName();
        File newFile = new File(newFilePath);
        if(newFile.exists() && newFilePath.equals(curFile.getAbsolutePath()) ){

//       newFilePath = curFile.getParent() + File.separator +"重复_"+newName;
//       newFile = new File(newFilePath);
            System.out.println("当前目录已存在重命名后的文件  文件名称:"+ curFile.getName());
            return false;    // 已经存在的文件不处理 直接返回

        }
        boolean flag =   curFile.renameTo(newFile);
        if(flag){
            System.out.println(oldName+" 转为 "+ newFilePath +" 成功！");
        }else{
            System.out.println(oldName+" 转为 "+ newFilePath +" 失败！");
        }
        return flag;
    }
   
   static ArrayList<String> readListFromFile(File fileItem) {
       ArrayList<String> contentList = new ArrayList<String>();
       try {
              BufferedReader curBR = new BufferedReader(new InputStreamReader(new FileInputStream(fileItem), "utf-8"));
//           BufferedReader curBR = new BufferedReader(new InputStreamReader(new FileInputStream(fileItem)));
           String lineContent = "";
           while (lineContent != null) {
               lineContent = curBR.readLine();
               if (lineContent == null ) {
            	
                   continue;
               }
               contentList.add(lineContent);
           }
           curBR.close();
       } catch (Exception e) {
       }
       return contentList;
   }
   
    
	// A1 ..... A2.
	static String get_Bat_Sh_FlagNumber(String mCur_Bat_Name) {
		String mCharNumber = "error";
		String curBat = mCur_Bat_Name;
		if (mCur_Bat_Name.contains(".sh")) {
			curBat = curBat.replace(".sh", "");
		}

		if (mCur_Bat_Name.contains(".bat")) {
			curBat = curBat.replace(".bat", "");
		}
		if (curBat.contains("_")) {
			String[] arrNameList = curBat.split("_");
			mCharNumber = arrNameList[arrNameList.length - 1];
		} else {
			mCharNumber = curBat;
		}

		return mCharNumber;
	}
	static int getCurrentYear() {

		SimpleDateFormat df = new SimpleDateFormat("YYYY");

		return Integer.parseInt(df.format(new Date()));

	}
	
	static void NotePadOpenTargetFile(String absPath) {
		String commandNotead = "";
		if (CUR_OS_TYPE == OS_TYPE.Windows) {
			commandNotead = "cmd.exe /c start   Notepad++.exe " + absPath;
			execCMD(commandNotead);

		} else if (CUR_OS_TYPE == OS_TYPE.Linux) {
			commandNotead = " gedit " + absPath;
		} else if (CUR_OS_TYPE == OS_TYPE.MacOS) {
			commandNotead = "/Applications/UltraEdit  " + absPath;
			execute_Mac(commandNotead);
		}
	}
	
	
	/**
	 * 执行 mac(unix) 脚本命令~
	 * 
	 * @param command
	 * @return
	 */
	public static int execute_Mac(String command) {
		String[] cmd = { "/bin/bash", "-c", command };
		Runtime rt = Runtime.getRuntime();
		Process proc = null;
		try {
			proc = rt.exec(cmd);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 打开流
//        OutputStream os = proc.getOutputStream();
//        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));

		try {
//            String newCommand = "/bin/bash -c "+"\""+command+"\"";
//            System.out.println("newCommand = " + newCommand);

//            bw.write(newCommand);

//
//            bw.flush();
//            bw.close();

			/** 真奇怪，把控制台的输出打印一遍之后竟然能正常终止了~ */
//            readConsole(proc);

			/** waitFor() 的作用在于 java 程序是否等待 Terminal 执行脚本完毕~ */
			proc.waitFor();
			Thread.sleep(100000);

		} catch (Exception e) {
			e.printStackTrace();
		}
//        int retCode = proc.exitValue();
//        if (retCode != 0) {
//            System.out.println("unix script retCode = " + retCode);
//
//            System.out.println(readConsole(proc));
//            System.out.println("UnixScriptUil.execute 出错了!!");
//        }

		return 0;
	}

	
    public static String execCMD(String command) {

        String result = "";
        if(CUR_OS_TYPE == OS_TYPE.Windows){
            return execCMD_Windows(command);
        }else if(CUR_OS_TYPE == OS_TYPE.MacOS){

            return execCMD_Mac(command);
        }else{

            execCMD_Mac(command);
        }
        return result;
    }
}