package name.caiyao.wechatautojump;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private Process process;

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    Toast.makeText(MainActivity.this, "未获取到Root权限，程序无法运行！", Toast.LENGTH_LONG).show();
                    break;
                case 1:
                    Toast.makeText(MainActivity.this, "5秒钟后开始启动，请于5秒内打开微信跳一跳，并点击开始游戏", Toast.LENGTH_LONG).show();
                    break;
            }
            return true;
        }
    });

    /****************
     *
     * 发起添加群流程。群号：小木咖啡屋(290873461) 的 key 为： gVL8FNtZZA3jny0Gw2nUuEHCmf3xws78
     * 调用 joinQQGroup(gVL8FNtZZA3jny0Gw2nUuEHCmf3xws78) 即可发起手Q客户端申请加群 小木咖啡屋(290873461)
     *
     * @param key 由官网生成的key
     * @return 返回true表示呼起手Q成功，返回fals表示呼起失败
     ******************/
    public boolean joinQQGroup(String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(intent);
            return true;
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button start = findViewById(R.id.start);
        Button stop = findViewById(R.id.stop);
        TextView textView2 = findViewById(R.id.textView2);
        textView2.setClickable(true);
        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                joinQQGroup("gVL8FNtZZA3jny0Gw2nUuEHCmf3xws78");
            }
        });
        new Thread() {
            @Override
            public void run() {
                Utils.testRoot();
            }
        }.start();
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            if (!Utils.testRoot()) {
                                handler.sendEmptyMessage(0);
                                return;
                            }
                            handler.sendEmptyMessage(1);
                            Thread.sleep(5000);
                            Utils.copyFromAssetsToFile("jump", getApplicationContext());
                            //Utils.suCmd(new String[]{"dd if=" + getFilesDir() + "/jump of=/data/data/"+BuildConfig.APPLICATION_ID+"/files/jump"}, Runtime.getRuntime().exec("su"));
                            process = Runtime.getRuntime().exec("su");
                            Utils.suCmd(new String[]{"chmod 777 /data/data/"+BuildConfig.APPLICATION_ID+"/files/jump", "cd  /data/data/"+BuildConfig.APPLICATION_ID+"/files", "./jump 2.04"}, process);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread() {
                    @Override
                    public void run() {
                        Utils.suCmd(new String[]{"exit"}, process);
                    }
                }.start();
            }
        });

    }
}
