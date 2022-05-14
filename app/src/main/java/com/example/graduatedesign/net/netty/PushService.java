package com.example.graduatedesign.net.netty;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.graduatedesign.data.model.Message;
import com.example.graduatedesign.data.model.NetResult;
import com.example.graduatedesign.net.netty.model.Callback;
import com.example.graduatedesign.net.netty.model.LoginInfo;
import com.example.graduatedesign.net.netty.model.LoginStatus;
import com.example.graduatedesign.net.netty.model.MsgType;
import com.example.graduatedesign.net.netty.model.MyRequest;
import com.google.gson.Gson;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.ReferenceCountUtil;

/**
 * Created by hzwangchenyan on 2017/12/26.
 * 安卓socket长连接Service服务类
 */
public class PushService extends Service {
    private static final String TAG = "PushService";
    private static final String HOST = "172.24.38.102";
    private static final int PORT = 7960;
    /**
     * 活动启动成功的回调操作
     */
    public static Callback<Void> serviceStartCallBack;
    /* 消息为json格式，转换为统一格式bean */
    private final Gson gson = new Gson();
    /**
     * 接收到入会申请，新消息，通知的回调
     */
    private final CopyOnWriteArrayList<Callback<Message>> receiveMsgCallbackList = new CopyOnWriteArrayList<>();
    /**
     * 请求后端接口时传入的回调，时间+请求url -> key , 回调对象 -> value
     */
    private final Map<String, Callback> actionCallBackMap = new ConcurrentHashMap<>();
    private int retryCount = 0;
    /**
     * Channel-本地I/O设备、网络I/O的通信桥梁
     */
    private SocketChannel socketChannel;
    /**
     * 登录结果回调
     */
    private Callback<Void> loginCallback;
    /**
     * 参考 https://www.runoob.com/w3cnote/android-tutorial-handler-message.html
     * 在主线程创建的handler，负责发送与处理信息，消息会被发送至消息队列
     * UI主线程的消息处理对象Looper不断地从消息队列中取出Message分发给对应的Handler处理
     */
    private Handler handler;
    private LoginStatus status = LoginStatus.UNLOGIN;

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        /* 服务对象缓存 */
        AppCache.setService(this);
        Log.d(TAG, "onCreate: 打开service中");
    }

    /**
     * 此服务不进行绑定
     *
     * @param intent
     * @return IBinder app通过该对象与Service组件进行通信,此为null
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        serviceStartCallBack.onEvent(200, null, null);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        close();
        AppCache.setService(null);
        super.onDestroy();
    }

    /**
     * 添加信息回调，使用者通过传入回调获取数据
     * 添加前会确认是否有该对象存在,
     * 对于接收入会申请，新消息，通知，都在此处
     */
    public void addReceiveMsgCallback(Callback<Message> receiveMsgCallback) {
        if (this.receiveMsgCallbackList.contains(receiveMsgCallback))
            return;
        this.receiveMsgCallbackList.add(receiveMsgCallback);
    }

    public void removeReceiveMsgCallback(Callback<Message> receiveMsgCallback) {
        this.receiveMsgCallbackList.remove(receiveMsgCallback);
    }

    /**
     * 私有，创建连接，由登录方法调用
     *
     * @param callback 回调
     */
    private void connect(@NonNull Callback<Void> callback) {
        /* 正在连接状态，不创建新的连接 */
        Log.d(TAG, "connect: 尝试连接中");
        if (status == LoginStatus.CONNECTING) {
            return;
        }

        /* 标识正在连接 */
        updateStatus(LoginStatus.CONNECTING);
        /* 创建连接 */
        NioEventLoopGroup group = new NioEventLoopGroup();
        new Bootstrap()
                /* 配置连接设置 */
                .channel(NioSocketChannel.class)
                .group(group)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(65535))
//                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        /* 消息管道添加Object解码器、编码器等 */
                        ChannelPipeline pipeline = socketChannel.pipeline();

                        /* 30s写空闲，则客户端应当发送心跳包 */
                        /* 60s没有收到心跳包，连接超时，服务器直接关闭连接，客户端关闭后尝试重连 */
                        pipeline.addLast(new IdleStateHandler(60, 30, 0));

                        pipeline.addLast(new MessageDecoder());
                        pipeline.addLast(new MessageEncoder());
                        pipeline.addLast(new ClientChannelHandle());
                    }
                })
                /* 尝试连接配置的端口 */
                .connect(new InetSocketAddress(HOST, PORT))
                .addListener((ChannelFutureListener) future -> {
                    if (future.isSuccess()) {
                        /* 连接成功，保存连接通道 */
                        socketChannel = (SocketChannel) future.channel();
                        callback.onEvent(200, "success", null);
                    } else {
                        Log.e(TAG, "connect failed");
                        close();
                        /* 这里一定要关闭，不然一直重试会引发OOM */
                        future.channel().close();
                        group.shutdownGracefully();
                        callback.onEvent(400, "connect failed", null);
                    }
                });
    }

    /**
     * 登录
     *
     * @param token    用户登录后获取的token
     * @param callback 用户传入的回调，service通过服务将消息通知给用户
     */
    public void login(String token, Callback<Void> callback) {
        Log.d(TAG, "login: 进入登录函数");

        /* 已登录或正在连接 */
        if (status == LoginStatus.CONNECTING || status == LoginStatus.LOGINING) {
            return;
        }

        /* 调用连接操作 */
        connect((code, msg, aVoid) -> {
            Log.d(TAG, "login: 尝试连接");
            if (code == 200) {
                Log.d(TAG, "login: 连接成功");

                MyRequest loginMsg = new MyRequest();
                loginMsg.setToken(token);
                loginMsg.setType(MsgType.LOGIN);

                /* 登录成功，将loginInfo对象写给服务器缓存 */
                socketChannel.writeAndFlush(loginMsg)
                        .addListener((ChannelFutureListener) future -> {
                            /* 操作成功后，保存用户传入的回调 */
                            if (future.isSuccess()) {
                                loginCallback = callback;
                                Log.d(TAG, "login: 成功发送登录信息");
                            } else {
                                /* 操作不成功，标识登录状态，通过回调告知用户登录失败信息 */
                                close();
                                updateStatus(LoginStatus.UNLOGIN);
                                if (callback != null) {
                                    handler.post(() -> callback.onEvent(400, "登录失败，连接故障", null));
                                }
                            }
                        });
            } else {

                Log.d(TAG, " 连接失败");

                /* 连接失败，关闭连接 */
                close();
                /* 操作不成功，标识登录状态，通过回调告知用户登录失败信息 */
                updateStatus(LoginStatus.UNLOGIN);
                if (callback != null) {
                    handler.post(() -> callback.onEvent(400, "服务器连接失败", null));
                }
            }
        });
    }

    /**
     * 发送信息
     *
     * @param callback 操作回调，沟通渠道
     */
    public void sendTxtMsg(MyRequest message, Callback<Void> callback) {
        /* 未登录，拒绝发送信息 */
        if (status != LoginStatus.LOGINED) {
            callback.onEvent(401, "未登录", null);
            return;
        }

        /* 信息对象转换成json，根据操作结果向回调传入不同的数据 */
        socketChannel.writeAndFlush(message)
                .addListener((ChannelFutureListener) future -> {
                    if (callback == null) {
                        return;
                    }
                    if (future.isSuccess()) {
                        handler.post(() -> callback.onEvent(200, "发送成功", null));
                    } else {
                        handler.post(() -> callback.onEvent(400, "发送失败", null));
                    }
                });
    }


    /**
     * 调用服务器接口，处理业务逻辑并返回 Result 对象作为操作结构
     *
     * @param url      服务器中接口地址
     * @param body     操作参数
     * @param callback 结果回调
     */
    public void sendActionMsg(String url, String body, Callback callback) {
        /* 未登录，拒绝发送信息 */
        if (status != LoginStatus.LOGINED) {
            callback.onEvent(401, "未登录", null);
            return;
        }

        String time = "" + System.currentTimeMillis();

        /* 构建请求接口操作时的数据对象 */
        MyRequest request = new MyRequest();
        request.setToken(AppCache.getMyInfo().getToken());
        request.setTimestamp(time);
        request.setType(MsgType.ACTION);
        request.setUrl(url);
        request.setBody(body);

        /* 信息对象转换成json，根据操作结果向回调传入不同的数据 */
        socketChannel.writeAndFlush(request)
                .addListener((ChannelFutureListener) future -> {
                    if (callback == null) {
                        return;
                    }
                    if (future.isSuccess()) {
                        actionCallBackMap.put(time + url, callback);
                    } else {
                        handler.post(() -> callback.onEvent(400, "发送失败", null));
                    }
                });
    }

    /**
     * 关闭连接
     */
    private void close() {
        if (socketChannel != null) {
            socketChannel.close();
            socketChannel = null;
        }
    }

    /**
     * 尝试重连时，自动重新登录
     *
     * @param mills 重连操作间隔时间
     */
    private void retryLogin(long mills) {
        /* 用户未登录过，没有登录信息 */
        if (AppCache.getMyInfo() == null) {
            return;
        }
        retryCount++;

        /* 多次重连 */
        handler.postDelayed(() -> login(AppCache.getMyInfo().getToken(), (code, msg, aVoid) -> {

            if (code != 200) {
                retryLogin(mills);
            }

        }), mills);
    }

    /**
     * 私有，修改保存的登录状态
     *
     * @param status 新登录状态
     */
    private void updateStatus(LoginStatus status) {
        if (this.status != status) {
            Log.d(TAG, "update status from " + this.status + " to " + status);
            this.status = status;
        }
    }

    /**
     * 连接通道操作处理器
     */
    private class ClientChannelHandle extends SimpleChannelInboundHandler<MyRequest> {

        /* 连接断开回调，试图重连*/
        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            super.channelInactive(ctx);
            PushService.this.close();
            updateStatus(LoginStatus.UNLOGIN);/* 停止重连 */
            if (retryCount > 4) {
                retryCount = 0;
                handler.removeCallbacksAndMessages(null);
                return;
            }
            retryLogin(10000);
        }

        /**
         * IdleStateHandler 类说的很清楚。。。
         * 当连接空闲时间到达设定的最大值时，IdleStateHandler 调用此函数
         *
         * @param ctx
         * @param evt
         */
        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            super.userEventTriggered(ctx, evt);
            if (evt instanceof IdleStateEvent) {
                IdleStateEvent e = (IdleStateEvent) evt;
                if (e.state() == IdleState.WRITER_IDLE) {
                    /* 空闲了，发个心跳吧吗，检测服务器是否宕机 */
                    if (AppCache.getMyInfo() != null) {
                        MyRequest message = new MyRequest();
                        message.setToken(AppCache.getMyInfo().getToken());
                        message.setType(MsgType.PING);
                        ctx.writeAndFlush(message);
                    }
                } else if (e.state() == IdleState.READER_IDLE) {
                    PushService.this.close();
                }
            }
        }

        /**
         * 通道数据可读取事件
         *
         * @param ctx 上下文对象
         * @param msg 通道接收的信息
         */
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, MyRequest msg) throws Exception {
            switch (msg.getType()) {
                /* 登录操作类型回信 */
                case MsgType.LOGIN:

                    try {
                        Log.d(TAG, "channelRead0, 收到服务器回信: " + msg.getToken());

                        /* 服务器返回的登录消息 */
                        NetResult loginResult = gson.fromJson(msg.getBody(), NetResult.class);
                        Log.d(TAG, "返回的数据: " + loginResult.getData());
                        /* 登录成功 */
                        if (loginResult.getCode() == 200) {
                            /* 更新标识状态及缓存信息 */
                            updateStatus(LoginStatus.LOGINED);
                            Double doubleId = (Double) loginResult.getData();
                            LoginInfo loginInfo = new LoginInfo(doubleId.intValue(), msg.getToken());
                            AppCache.setMyInfo(loginInfo);
                            /* 通过回调通知用户登录操作成功 */
                            if (loginCallback != null) {
                                /* 回调在主线程上处理，此处是将消息（操作）存入消息队列等待执行 */
                                handler.post(() -> {
                                    loginCallback.onEvent(200, "success", null);
                                    loginCallback = null;
                                });
                            }
                        }
                        /* 登录失败 */
                        else {
                            close();
                            updateStatus(LoginStatus.UNLOGIN);
                            if (loginCallback != null) {
                                handler.post(() -> {
                                    loginCallback.onEvent(loginResult.getCode(), loginResult.getMsg(), null);
                                    loginCallback = null;
                                });
                            }
                        }
                    } catch (Exception e1) {
                        Log.e(TAG, "channelRead0: 登录发生错误");
                        e1.printStackTrace();
                        if (loginCallback != null) {
                            handler.post(() -> {
                                loginCallback.onEvent(500, "登录时发生错误", null);
                                loginCallback = null;
                            });
                        }
                    }
                    break;
                /* ping类型回信 */
                case MsgType.PING:
                    Log.d(TAG, "receive ping from server");
                    break;
                /* 操作结果回信 */
                case MsgType.ACTION:
                    Log.d(TAG, "服务器返回了操作结果");
                    String action_key = msg.getTimestamp() + msg.getUrl();
                    NetResult action_result = gson.fromJson(msg.getBody(), NetResult.class);
                    Log.d(TAG, "解读出来的操作结果是否为空: " + action_result);
                    Callback callback = actionCallBackMap.get(action_key);
                    if (callback != null) {
                        if (action_result == null) {
                            handler.post(() -> callback.onEvent(500, "服务器故障", null));
                        } else {
                            handler.post(() -> callback.onEvent(action_result.getCode(), action_result.getMsg(), action_result.getData()));
                        }
                        Log.d(TAG, "操作结果传入回调,同时删除原先的回调对象");
                        actionCallBackMap.remove(action_key);
                    }
                    break;
                /* 接收到新的信息(本系统定义的Message)，可能是普通消息，入会申请，通知 */
                case MsgType.TEXT:
                    Log.d(TAG, "receive text msg " + msg.getBody());
                    if (receiveMsgCallbackList != null) {
                        try {
                            if (msg.getBody() == null)
                                throw new IllegalArgumentException("无效信息");
                            //todo:限制为一次只接收一个消息
                            Message message = gson.fromJson(msg.getBody(), Message.class);
                            if (message == null)
                                throw new IllegalArgumentException("无效信息");
                            for (Callback<Message> msgCB : receiveMsgCallbackList) {
                                handler.post(() -> msgCB.onEvent(200, msg.getBody(), message));
                            }
                        } catch (Exception e) {
                            for (Callback<Message> msgCallBack : receiveMsgCallbackList) {
                                handler.post(() -> msgCallBack.onEvent(700, e.getMessage(), null));
                            }
                        }
                    }
                    break;
                default:
                    /* 丢弃信息 */
                    ReferenceCountUtil.release(msg);
            }
        }
    }
}
