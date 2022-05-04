package com.example.graduatedesign.data;

import com.example.graduatedesign.data.model.Result;
import com.example.graduatedesign.data.remote.RemoteLoginDataSource;
import com.example.graduatedesign.net.MyTokenInterceptor;
import com.example.graduatedesign.net.RetrofitExceptionResolver;
import com.example.graduatedesign.personal_module.data.User;
import com.example.graduatedesign.presenter.RegisterFirstContract;
import com.example.graduatedesign.utils.DataUtil;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Singleton
public class LoginRepository implements RegisterFirstContract.IRegisterFirstModel {

    private static final String TAG = "LoginRepository";
    private final RemoteLoginDataSource remoteLoginDataSource;
    private final MyTokenInterceptor interceptor;

    @Inject
    public LoginRepository( RemoteLoginDataSource remoteLoginDataSource, MyTokenInterceptor interceptor) {
        this.remoteLoginDataSource = remoteLoginDataSource;
        this.interceptor = interceptor;
    }

    /**
     * 更新token，同时更新请求拦截器中设置的全局token
     * @param tokenName 请求头名称，如传入null代表不更改
     * @param token 新的有效token
     */
    public void setToken(String tokenName,String token) {
        interceptor.setToken(token);
        if (tokenName!=null)
            interceptor.setTokenName(tokenName);
    }

    /**
     * 解析登录时服务器返回的数据对象
     * @param principal 用户输入的账号/email
     * @param credential 用户输入的密码
     * @param loginType 登录类型：1-密码，2-验证码，-第三方登录
     * @return
     */
    public Single<Result> login(String principal, String credential,int loginType) {
        Map<String, Object> loginInfo=new HashMap<>();
        loginInfo.put("loginType",loginType);
        loginInfo.put("principal",principal);
        loginInfo.put("credential",credential);

        return remoteLoginDataSource.login(loginInfo)
                .map(netResult -> {
                    if (netResult.getCode() == 200) {
                        Map<String, Object> netResultData = (Map<String, Object>) netResult.getData();

                        String token= (String) netResultData.get("token");
                        String tokenName= (String) netResultData.get("tokenName");
                        //登录成功后，将服务器返回的token传递给retrofit的全局拦截器，设置后请求头都会加上此token
                        setToken(tokenName,token);

                        String nickname = (String) netResultData.get("nickname");
                        Boolean isAssociationAdmin = (Boolean) netResultData.get("isAssociationAdmin");

                        // TODO:User定义的字段最好都要查到，id可能也会被int转double从而出错
                        User user = new User();
                        //Gson转换器将int转换为double，直接强制转换会出现异常
                        int number = DataUtil.getIntFromGsonMap(netResultData,"id");
                        user.setId(number);
                        int credentialInfoId = DataUtil.getIntFromGsonMap(netResultData,"credentialInfoId");
                        user.setCredentialInfoId(credentialInfoId);
                        user.setSchoolName((String) netResultData.get("schoolName"));
                        user.setEmail((String) netResultData.get("email"));
                        user.setPortrait((String) netResultData.get("portrait"));
                        user.setNickname(nickname);
                        user.setAssociationAdmin(isAssociationAdmin);

                        //处理后交给前端保存或显示的最终数据
                        Map<String, Object> data = new HashMap<>();
                        data.put("User", user);
                        data.put("tokenName",tokenName);
                        data.put("token",token);

                        return new Result.Success(data);
                    } else {
                        return new Result.Fail(netResult.getMsg());
                    }
                });
    }

    /**
     * @param email 邮箱验证码登录时输入的邮箱地址
     * @return
     */
    public Completable getLoginVerifyCode(String email){
        return remoteLoginDataSource.getRegisterVerifyCode(email)
                .map(netResult -> {
                    RetrofitExceptionResolver.analyzeNetResult(netResult);
                    return null;
                })
                .ignoreElement();
    }

    /**
     * 启动应用时，使用保存的token访问服务器验证登录状态
     * 对服务器返回的结果进行解析，方便前端进行相关跳转页面操作
     * @param token 应用保存的token
     * @return
     */
    public Single<Result> validateToken(String tokenName,String token) {
        //设置网络请求头
        setToken(tokenName,token);
        return remoteLoginDataSource.validateToken(tokenName,token)
                .map(netResult -> {
                    //对不符合要求的code，直接抛出错误
                    RetrofitExceptionResolver.analyzeNetResult(netResult);
                    //只用对合格的code情况作细分
                    if (netResult.getCode() == 200){
                        return new Result.Success(null);
                    }
                    return new Result.Fail("发生了一些问题。。。");
                });

    }

    public Single<Result> getCollegeList(){
        return remoteLoginDataSource.getCollegeList()
                .map(netResult -> {
                    RetrofitExceptionResolver.analyzeNetResult(netResult);
                    if (netResult.getCode()==200){
                        return new Result.Success<>(netResult.getData());
                    }
                    return new Result.Fail("发生了一些问题。。。");
                });
    }

    /**
     * 用户注册时，需先选择学校，输入学号
     * 访问服务器验证学号是否合法，决定是否进行下一步注册操作
     * @param collegeId
     * @param studentNo
     * @return
     */
    @Override
    public Single<Result> validateStudentNo(int collegeId,String studentNo){
        return remoteLoginDataSource.validateStudentNo(collegeId,studentNo)
                .map(netResult -> {
                    RetrofitExceptionResolver.analyzeNetResult(netResult);
                    if (netResult.getCode()==200){
                        return new Result.Success<>(null);
                    }
                    return new Result.Fail("系统发生了一些故障。。。");
                });
    }

    public Single<Result> register(Map<String,Object> registerData){
        return remoteLoginDataSource.register(registerData)
                .map(netResult -> {
                    RetrofitExceptionResolver.analyzeNetResult(netResult);
                    if (netResult.getCode()==200){
                        return new Result.Success<>("可以进行下一步");
                    }
                    return new Result.Fail("发生了一些问题。。。");
                });
    }

    public Completable getRegisterVerifyCode(String email){
        return remoteLoginDataSource.getRegisterVerifyCode(email)
                .map(netResult -> {
                    RetrofitExceptionResolver.analyzeNetResult(netResult);
                    return null;
                })
                .ignoreElement();
    }

    public Completable resetPass(Map<String,String> resetData){
        return remoteLoginDataSource.resetPass(resetData)
                .map(netResult -> {
                    RetrofitExceptionResolver.analyzeNetResult(netResult);
                    return null;
                })
                .ignoreElement();
    }

    public Completable getResetPassVerifyCode(String email){
        return remoteLoginDataSource.getResetPassVerifyCode(email)
                .map(netResult -> {
                    RetrofitExceptionResolver.analyzeNetResult(netResult);
                    return null;
                })
                .ignoreElement();
    }
}