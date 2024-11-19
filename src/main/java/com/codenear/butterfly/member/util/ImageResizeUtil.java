package com.codenear.butterfly.member.util;

import static com.codenear.butterfly.global.exception.ErrorCode.SERVER_ERROR;

import com.codenear.butterfly.member.exception.MemberException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.web.multipart.MultipartFile;

public class ImageResizeUtil {

    private static final int TARGET_WIDTH = 300;
    private static final int TARGET_HEIGHT = 300;
    private static final String IMAGE_FORMAT = "jpeg";
    private static final String MIME_TYPE = "image/jpeg";

    public static MultipartFile resizeImage(MultipartFile originalFile) {
        try (InputStream inputStream = originalFile.getInputStream();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            processResize(inputStream, outputStream);

            byte[] resizedImageBytes = outputStream.toByteArray();
            String resizedFileName = originalFile.getName();

            return createMultipartFile(resizedImageBytes, resizedFileName);
        } catch (IOException e) {
            throw new MemberException(SERVER_ERROR, e.getMessage());
        }
    }

    private static void processResize(InputStream inputStream, ByteArrayOutputStream outputStream) throws IOException {
        Thumbnails.of(inputStream)
                .size(TARGET_WIDTH, TARGET_HEIGHT)
                .outputFormat(IMAGE_FORMAT)
                .toOutputStream(outputStream);
    }

    private static MultipartFile createMultipartFile(byte[] resizedImageBytes, String resizedFileName) {
        return new CustomMultipartFile(
                resizedImageBytes,
                resizedFileName,
                MIME_TYPE
        );
    }
}
