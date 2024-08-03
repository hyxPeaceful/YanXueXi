package com.yanxuexi.media;

import io.minio.*;
import io.minio.errors.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author hyx
 * @version 1.0
 * @description Minio 测试
 * @date 2024-07-27 20:56
 **/
public class MinioTest {
    static MinioClient minioClient =
            MinioClient.builder()
                    .endpoint("http://192.168.101.65:9000")
                    .credentials("minioadmin", "minioadmin")
                    .build();

    // 测试文件上传功能
    @Test
    public void testUpload() throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        // 上传文件参数
        UploadObjectArgs testbucket = UploadObjectArgs.builder()
                .bucket("testbucket")
                .filename("D:\\yxh\\Pictures\\Saved Pictures\\02 头像\\1.jpg")
                .object("test/1.jpg")
                .build();
        // 上传文件
        minioClient.uploadObject(testbucket);
    }

    // 测试文件删除功能
    @Test
    public void testDelete() throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        // 删除文件参数
        RemoveObjectArgs testbucket = RemoveObjectArgs.builder()
                .bucket("testbucket")
                .object("1.jpg")
                .build();
        // 删除文件
        minioClient.removeObject(testbucket);
    }

    // 测试文件查询功能
    @Test
    public void testSelect() throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        // 查询文件参数
        GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                .bucket("testbucket")
                .object("test/1.jpg")
                .build();

        // 查询文件
        FilterInputStream inputStream = minioClient.getObject(getObjectArgs);
        FileOutputStream fileOutputStream = new FileOutputStream(new File("C:\\Users\\yxh\\Desktop\\1.jpg"));
        IOUtils.copy(inputStream, fileOutputStream);

        // 校验下载文件的完整性，分别计算minio文件系统上文件和本地文件的Md5值，进行对比校验
        String sourceMd5 = DigestUtils.md5Hex(inputStream); // 直接使用上面的inputstream（网络输入流）是不稳定的
        String localMd5 = DigestUtils.md5Hex(new FileInputStream(new File("C:\\Users\\yxh\\Desktop\\1.jpg")));
        if (sourceMd5.equals(localMd5)) {
            System.out.println("下载成功");
        }
    }

    // 分块上传
    @Test
    public void uploadChunk() throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        File chunkFolder = new File("D:\\Develop\\test\\fileUpload\\chunk\\");
        File[] chunkFiles = chunkFolder.listFiles();
        for (int i = 0; i < chunkFiles.length; i++) {
            // 上传文件参数
            UploadObjectArgs testbucket = UploadObjectArgs.builder()
                    .bucket("testbucket")
                    .filename("D:\\Develop\\test\\fileUpload\\chunk\\" + i)
                    .object("chunk/" + i)
                    .build();
            // 上传文件
            minioClient.uploadObject(testbucket);
            System.out.println("分块" + i + "上传成功");
        }
    }

    // 测试MinIo分块合并功能
    @Test
    public void mergeChunk() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        File chunkFolder = new File("D:\\Develop\\test\\fileUpload\\chunk\\");
        File[] chunkFiles = chunkFolder.listFiles();
        int chunkNum = chunkFiles.length;
        List<ComposeSource> composeSourceList = Stream.iterate(0, i -> ++i).
                limit(chunkNum).
                map(i -> ComposeSource.builder().bucket("testbucket").object("chunk/" + i).build()).toList();
        ComposeObjectArgs composeObjectArgs = ComposeObjectArgs.builder().bucket("testbucket").object("test/1.mp4").sources(composeSourceList).build();
        minioClient.composeObject(composeObjectArgs);
    }
}
