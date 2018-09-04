# RetrofitUtils

      Android Studio

    //retrofit
    compile 'com.squareup.okio:okio:1.14.0'
    compile 'com.squareup.okhttp3:okhttp:3.10.0'
    compile 'com.squareup.retrofit2:retrofit:2.4.0'
    compile 'com.squareup.retrofit2:converter-scalars:2.3.0'

  目前对以下需求进行了封装

      一般的get请求
      一般的post请求
      基于Http Post的文件上传（类似表单）
      文件下载/加载图片
      上传下载的进度回调
      支持取消某个请求
      支持自定义Callback
      支持HEAD、DELETE、PATCH、PUT


  配置RetrofitUtils

  默认情况下，将直接使用okhttp默认的配置生成HttpCreator，如果你有任何配置，记得在Application中调用HttpCreator.init(Urls.URL_BASE_HOST, okHttpClient);
方法进行设置。

  public class MyApplication extends Application
  {
  	@Override
      public void onCreate()
      {
          super.onCreate();

    OkHttpClient okHttpClient = new OkHttpClient.Builder()
                   .connectTimeout(20000L, TimeUnit.MILLISECONDS)
                   .readTimeout(20000L, TimeUnit.MILLISECONDS)
                   .addInterceptor(new LoggingInterceptor())
                   .build();
           HttpCreator.init(Urls.URL_BASE_HOST, okHttpClient);

      }
  }

  别忘了在AndroidManifest中设置。
  相当于框架中只是提供了几个实现类，你可以自行定制或者选择使用。
  对于Log

  初始化HttpCreator时，通过设置拦截器实现，框架中提供了一个LoggerInterceptor，当然你可以自行实现一个Interceptor 。

   OkHttpClient okHttpClient = new OkHttpClient.Builder()
         .addInterceptor(new LoggerInterceptor())
          //其他配置
          .build();
   HttpCreator.init(Urls.URL_BASE_HOST, okHttpClient);
  ##其他用法示例

    /**
     * get 请求模拟
     *
     * @param view
     */
    public void get(View view) {
        RetrofitUtils.get()
                .url("query")
                .addParams("name", "bbbb")
                .build()
                .execute(new StringCallBack() {
                    @Override
                    public void onError(Call call, Throwable e,int id) {
                        toast(e.getMessage());
                    }

                    @Override
                    public void onResponse(Call call, String response,int id) {
                        toast(response);
                    }
                });
    }

    /**
     * 模拟 post 请求
     *
     * @param view
     */
    public void post(View view) {
        RetrofitUtils.post()
                .url("postParam")
                .addParams("name", "bbbb")
                .build()
                .execute(new StringCallBack() {
                    @Override
                    public void onError(Call call, Throwable e,int id) {
                        toast(e.getMessage());
                    }

                    @Override
                    public void onResponse(Call call, String response,int id) {
                        toast(response);
                    }
                });
    }

    /**
     * 模拟 postRaw 请求
     *
     * @param view
     */
    public void postRaw(View view) {
        HashMap hashMap = new HashMap();
        hashMap.put("fileName", "qinlei");

        RetrofitUtils.postRaw()
                .url("postRaw")
                .addBody(JSON.toJSONString(hashMap))
                .build()
                .execute(new StringCallBack() {
                    @Override
                    public void onError(Call call, Throwable e,int id) {
                        toast(e.getMessage());
                    }

                    @Override
                    public void onResponse(Call call, String response,int id) {
                        toast(response);
                        Log.d("qinlei", "onResponse: "+response);
                    }
                });

        String xml = "<xml></xml>";
        RetrofitUtils.postRaw()
                .url("https://api.mch.weixin.qq.com/pay/unifiedorder")
                .addBody(xml, "application/xml;charset=UTF-8")
                .build()
                .execute(new StringCallBack() {
                    @Override
                    public void onError(Call call, Throwable e,int id) {
                        toast(e.getMessage());
                    }

                    @Override
                    public void onResponse(Call call, String response,int id) {
                        toast(response);
                        Log.d("qinlei", "onResponse: "+response);
                    }
                });
    }

    /**
     * 模拟 put 请求
     *
     * @param view
     */
    public void put(View view) {
        RetrofitUtils.put()
                .url("putParam")
                .tag(this)
                .addParams("name", "bbbb")
                .build()
                .execute(new StringCallBack() {
                    @Override
                    public void onError(Call call, Throwable e,int id) {
                        toast(e.getMessage());
                    }

                    @Override
                    public void onResponse(Call call, String response,int id) {
                        toast(response);
                    }
                });
    }

    /**
     * 模拟 putRaw 请求
     *
     * @param view
     */
    public void putRaw(View view) {
        HashMap hashMap = new HashMap();
        hashMap.put("fileName", "qinlei");
        RetrofitUtils.putRaw()
                .url("putRaw")
                .addBody(JSON.toJSONString(hashMap))
                .build()
                .execute(new StringCallBack() {
                    @Override
                    public void onError(Call call, Throwable e,int id) {
                        toast(e.getMessage());
                    }

                    @Override
                    public void onResponse(Call call, String response,int id) {
                        toast(response);
                    }
                });
    }

    /**
     * 模拟 delete 请求
     *
     * @param view
     */
    public void delete(View view) {
        toast("没有测试的服务器");
    }

    /**
     * 模拟 upload 请求
     *
     * @param view
     */
    public void upload(View view) {
        TakePhotoUtils.getInstance().with(this).setTakePhotoCallback(new TakePhotoCallback() {
            @Override
            public void takeSuccess(File file) {
                RetrofitUtils.upload()
                        .url("http://192.168.31.236/TruckAlliance/userInterface/v1/uploadBatch")
                        .addFiles("file", file)
                        .addFiles("file", file)
                        .build()
                        .execute(new StringCallBack() {
                            ProgressDialog progressDialog;

                            @Override
                            public void onBefore(Call call) {
                                super.onBefore(call);
                                progressDialog = new ProgressDialog(MainActivity.this);
                                progressDialog.setTitle("上传图片");
                                progressDialog.setMessage("上传图片");
                                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                progressDialog.setCancelable(false);
                                progressDialog.setCanceledOnTouchOutside(false);
                                progressDialog.show();
                            }

                            @Override
                            public void inProgress(float progress, long progressMax) {
                                super.inProgress(progress, progressMax);
                                progressDialog.setMax((int) progressMax);
                                progressDialog.setProgress((int) progress);
                            }

                            @Override
                            public void onError(Call call, Throwable e,int id) {
                                toast(e.getMessage());
                            }

                            @Override
                            public void onResponse(Call call, String response,int id) {
                                Log.d("qinlei", "onResponse: " + response);
                                toast("上传完成");
                            }

                            @Override
                            public void onAfter(Call call) {
                                super.onAfter(call);
                                progressDialog.dismiss();
                            }
                        });
            }

            @Override
            public void takeFail(String msg) {
                toast("选择相册失败");
            }

            @Override
            public void takeCancel() {
                toast("选择相册取消");

            }
        }).chooseGallery();
    }

    /**
     * 模拟 download 请求
     *
     * @param view
     */
    public void download(View view) {
        //需要请求权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            download();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1000);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1000: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    download();
                } else {

                }
                return;
            }
        }
    }

    private void download() {
        RetrofitUtils
                .download()
                .url("http:\\\\zhstatic.zhihu.com\\pkg\\store\\daily\\zhihu-daily-zhihu-2.6.0(744)-release.apk")
                .build()
                .execute(new BaseFileCallBack(DOWNLOAD_SERVICE, "知乎日报.apk") {
                    ProgressDialog progressDialog;

                    @Override
                    public void onBefore(Call call) {
                        super.onBefore(call);
                        progressDialog = new ProgressDialog(MainActivity.this);
                        progressDialog.setTitle("下载apk");
                        progressDialog.setMessage("下载");
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        progressDialog.setCancelable(false);
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();
                    }

                    @Override
                    public void inProgress(float progress, long progressMax) {
                        super.inProgress(progress, progressMax);
                        progressDialog.setMax((int) progressMax);
                        progressDialog.setProgress((int) progress);
                    }

                    @Override
                    public void onError(Call call, Throwable e,int id) {
                        toast(e.getMessage());
                    }

                    @Override
                    public void onResponse(Call call, File response,int id) {
                        toast("下载完成");
                    }

                    @Override
                    public void onAfter(Call call) {
                        super.onAfter(call);
                        progressDialog.dismiss();
                    }
                });
    }



  取消所有请求
  RetrofitUtils.cancel();

  根据tag取消请求
  目前对于支持的方法都添加了最后一个参数Object tag，取消则通过RetrofitUtils.cancelTag(tag)执行。

  例如：在Activity中，当Activity销毁取消请求：
  @Override
  protected void onDestroy()
  {
      super.onDestroy();
      //可以取消同一个tag的
     RetrofitUtils.cancelTag(this);//取消以Activity.this作为tag的请求
  }

  比如，当前Activity页面所有的请求以Activity对象作为tag，可以在onDestory里面统一取消。
  混淆

# OkHttp3
-dontwarn okhttp3.logging.**
-keep class okhttp3.internal.**{*;}
-dontwarn okio.**

 # Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions


