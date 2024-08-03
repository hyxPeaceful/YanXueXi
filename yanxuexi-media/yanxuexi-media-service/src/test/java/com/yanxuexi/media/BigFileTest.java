package com.yanxuexi.media;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author hyx
 * @version 1.0
 * @description 测试大文件上传方法
 * @date 2024-08-02 21:21
 **/
public class BigFileTest {

    // 测试文件分块上传
    @Test
    public void testChunk() {
        String chunkFolder = "D:\\Develop\\test\\fileUpload\\chunk\\";
        File uploadFile = new File("D:\\Develop\\test\\fileUpload\\1.mp4");
        // 设定分块大小
        int chunkSize = 1024 * 1024 * 5;
        // 计算分块数量
        int chunkNum = (int) Math.ceil(uploadFile.length() * 1.0 / chunkSize);
        // 每次读取文件内容大小
        byte[] buffer = new byte[1024];
        try(RandomAccessFile raf_read = new RandomAccessFile(uploadFile, "r");) {
            for (int i = 0; i < chunkNum; i++) {
                //创建分块文件
                File file = new File(chunkFolder + i);
                // 一定要先删除文件，否则合并会失败
                if(file.exists()){
                    file.delete();
                }
                // 创建分块文件
                boolean newFile = file.createNewFile();
                if (newFile) {
                    //向分块文件中写数据
                    RandomAccessFile raf_write = new RandomAccessFile(file, "rw");
                    int len = -1;
                    while ((len = raf_read.read(buffer)) != -1) {
                        // 注意这里的len变量可以保证写入的长度与读出内容的长度一致
                        raf_write.write(buffer, 0, len);
                        if (file.length() >= chunkSize) {
                            break;
                        }
                    }
                    raf_write.close();
                    System.out.println("完成分块"+i);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 测试文件合并
    @Test
    public void testMerge() throws IOException {
        // 原文件
        File sourceFile = new File("D:\\Develop\\test\\fileUpload\\1.mp4");
        // 合并到的文件
        File mergeFile = new File("D:\\Develop\\test\\fileUpload\\2.mp4");
        // 分块所在目录
        String chunkFolder = "D:\\Develop\\test\\fileUpload\\chunk\\";
        File chunkFiles = new File(chunkFolder);

        if (mergeFile.exists()) {
            mergeFile.delete();
        }

        File[] files = chunkFiles.listFiles();
        List<File> fileList = Arrays.asList(files);
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return Integer.parseInt(o1.getName()) - Integer.parseInt(o2.getName());
            }
        });
        byte[] buffer = new byte[1024];
        mergeFile.createNewFile();
        RandomAccessFile raf_rw = new RandomAccessFile(mergeFile, "rw");
        raf_rw.seek(0);
        for (File file : fileList) {
            RandomAccessFile raf_r = new RandomAccessFile(file, "r");
            int len = -1;
            while ((len = raf_r.read(buffer)) != -1) {
                raf_rw.write(buffer, 0, len);
            }
            raf_r.close();
        }
        raf_rw.close();

        // 验证合并后的文件与原文件是否一致
        FileInputStream mergeFileStream = new FileInputStream(mergeFile);
        FileInputStream sourceFilestream = new FileInputStream(sourceFile);
        String mergeFileMd5Hex = DigestUtils.md5Hex(mergeFileStream);
        String sourceFileMd5Hex = DigestUtils.md5Hex(sourceFilestream);
        if (mergeFileMd5Hex.equals(sourceFileMd5Hex)) {
            System.out.println("文件合并成功");
        }
        else {
            System.out.println("文件合并失败");
        }
    }
}
