package com.example.graduatedesign.net.netty;


import com.example.graduatedesign.net.netty.model.MyRequest;
import com.google.gson.Gson;

import java.nio.charset.StandardCharsets;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class MessageDecoder extends ByteToMessageDecoder {
    private final Gson gson = new Gson();

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        byte[] body = new byte[in.readInt()];//创建一个从readerIndex开始可读取总数的byte数组
        in.readBytes(body);//将数据读到byte数组当中
        String context = new String(body, StandardCharsets.UTF_8);//转回String
        System.out.println("decoder出来的字符串" + context);

        MyRequest request = gson.fromJson(context, MyRequest.class);
        out.add(request);

        in.skipBytes(in.readableBytes());
    }

}
