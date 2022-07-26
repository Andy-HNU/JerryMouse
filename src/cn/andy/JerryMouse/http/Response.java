package cn.andy.JerryMouse.http;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Locale;

public class Response extends BaseResponse {
    private StringWriter stringWriter;
    private PrintWriter printWriter;
    private byte[] contentBodyBytes;
    private String ContentType;

    private int status;
    public Response(){
        this.stringWriter = new StringWriter();
        this.printWriter = new PrintWriter(stringWriter);
        this.ContentType = "text/html";
    }

    public void setContentBodyBytes(byte[] contentBodyBytes) {
        this.contentBodyBytes = contentBodyBytes;
    }

    public String getContentType(){
        return this.ContentType;
    }



    public PrintWriter getPrintWriter(){
        return this.printWriter;
    }

    public byte[] getBody() throws UnsupportedEncodingException {
        if (contentBodyBytes == null) {
            String content = stringWriter.toString();
            contentBodyBytes = content.getBytes(StandardCharsets.UTF_8);
        }
        return contentBodyBytes;
    }

    public void setContentType(String contentType){
        this.ContentType = contentType;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return printWriter;
    }

    @Override
    public int getStatus() {
        return this.status;
    }

    @Override
    public void setStatus(int status) {
        this.status = status;
    }
}
